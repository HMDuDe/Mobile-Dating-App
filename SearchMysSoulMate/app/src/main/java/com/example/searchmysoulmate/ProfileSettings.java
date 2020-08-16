package com.example.searchmysoulmate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.searchmysoulmate.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileSettings extends AppCompatActivity {

    private DatabaseReference dbRef;
    private Switch showMaleSwitch, showFemaleSwitch;
    private EditText aboutMeTxt, minAgeTxt, maxAgeTxt, emailTxt, contactNoTxt, jobTxt, companyTxt;
    private ImageView img1;
    private Boolean showMaleBool, showFemaleBool;
    private FirebaseAuth auth;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView img2, img3, img4, img5, img6;
    private ImageView[] extraImgs;
    private int count, count2;
    private User userUpdate;
    private StorageReference storageReference;
    private Uri filePath;
    private String jobOriginal, companyOriginal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        count = 0;
        count2 = 0;

        userUpdate = new User();

        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference("images/");

        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);

        extraImgs = new ImageView[5];
        for(int i = 0; i < extraImgs.length; i++){
            switch (i){
                case 0:
                    extraImgs[i] = img2;
                    break;
                case 1:
                    extraImgs[i] = img3;
                    break;
                case 2:
                    extraImgs[i] = img4;
                    break;
                case 3:
                    extraImgs[i] = img5;
                    break;
                case 4:
                    extraImgs[i] = img6;
                    break;

            }
        }

        showMaleSwitch = findViewById(R.id.maleSwitch);
        showFemaleSwitch = findViewById(R.id.femaleSwitch);
        aboutMeTxt = findViewById(R.id.aboutMeTxt);
        img1 = findViewById(R.id.img1);
        minAgeTxt = findViewById(R.id.minAgeTxt);
        maxAgeTxt = findViewById(R.id.maxAgeTxt);
        emailTxt = findViewById(R.id.emailTxt);
        jobTxt = findViewById(R.id.jobTitleTxt);
        companyTxt = findViewById(R.id.JobCompanyTxt);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                aboutMeTxt.setText(user.getAboutMe());
                Picasso.with(ProfileSettings.this).load(user.getMainProfileImgPath()).into(img1);

                showFemaleBool = user.isShowFemales();
                showMaleBool = user.isShowMales();

                if(showFemaleBool == true){
                    showFemaleSwitch.setChecked(true);
                }else{
                    showFemaleSwitch.setChecked(false);
                }

                if(showMaleBool == true){
                    showMaleSwitch.setChecked(true);
                }else{
                    showMaleSwitch.setChecked(false);
                }

                minAgeTxt.setText(String.valueOf(user.getMinAge()));
                maxAgeTxt.setText(String.valueOf(user.getMaxAge()));

                emailTxt.setText(user.getEmail());
                jobTxt.setText(user.getJobTitle());
                companyTxt.setText(user.getCompany());

                jobOriginal = user.getJobTitle();
                companyOriginal = user.getCompany();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        configImg2();
        configImg3();
        configImg4();
        configImg5();
        configImg6();

        //Other buttons
        configBottomNavBar();
        configSaveBtn();
        configCancelBtn();
        configLogoutBtn();
        configChangeEmailBtn();
        configChangePasswordBtn();
    }

    public void configChangePasswordBtn(){
        Button changePword = findViewById(R.id.sendPasswordBtn);
        changePword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileSettings.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void configChangeEmailBtn(){
        Button changeEmail = findViewById(R.id.changeEmailBtn);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.getCurrentUser().updateEmail(String.valueOf(emailTxt.getText()));
            }
        });
    }
    public void configImg2(){
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    public void configImg3(){

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    public void configImg4(){
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    public void configImg5(){
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    public void configImg6(){
        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        filePath = data.getData();
        Log.d("DATA: ", "" + data.getData());
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            Log.d("FILE PATH:", "" + filePath);

            Picasso.with(this).load(data.getData()).fit().centerCrop().into(extraImgs[count]);
            count++;

        }

        uploadImage();
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + /*UUID.randomUUID().toString()*/ System.currentTimeMillis() + "." + getFileExtension(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("FILEPATH SETTINGS", "" + uri);
                                    if(extraImgs[count2] == img2){
                                        userUpdate.setAddImg2(String.valueOf(uri));
                                    }else if(extraImgs[count2] == img3){
                                        userUpdate.setAddImg3(String.valueOf(uri));
                                    }else if(extraImgs[count2] == img4){
                                        userUpdate.setAddImg4(String.valueOf(uri));
                                    }else if(extraImgs[count2] == img5){
                                        userUpdate.setAddImg5(String.valueOf(uri));
                                    }else if(extraImgs[count2] == img6){
                                        userUpdate.setAddImg6(String.valueOf(uri));
                                    }

                                    Log.d("USER IMG FILEPATH ", "" + uri);
                                }
                            });

                            //Log.d("USER IMG PATH: ", "" + userUpdate.getMainProfileImgPath());
                            Toast.makeText(ProfileSettings.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileSettings.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
    public void configSaveBtn(){
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFemaleSwitch.isChecked()){
                    showFemaleBool = true;
                }

                if(showMaleSwitch.isChecked()){
                    showMaleBool = true;
                }

                dbRef.child("showFemales").setValue(showFemaleBool);
                dbRef.child("showMales").setValue(showMaleBool);

                dbRef.child("minAge").setValue(String.valueOf(minAgeTxt.getText()));
                dbRef.child("maxAge").setValue(String.valueOf(maxAgeTxt.getText()));

                dbRef.child("Pictures").push().setValue(userUpdate.getAddImg2());

                if(String.valueOf(jobTxt.getText()).equals(jobOriginal)){
                    Log.d("JOB CHANGE: ", "NO CHANGES TO JOB DETECTED");
                }else{
                    dbRef.child("jobTitle").setValue(String.valueOf(jobTxt.getText()));
                }

                if(String.valueOf(companyTxt.getText()).equals(companyOriginal)){
                    Log.d("COMPANY CHANGE: ", "NO CHANGES TO COMPANY DETECTED");
                }else{
                    dbRef.child("company").setValue(String.valueOf(companyTxt.getText()));
                }

                Toast.makeText(ProfileSettings.this, "DETAILS UPDATED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void configCancelBtn(){
        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileSettings.this, UserPvtProfile.class);
                startActivity(intent);
            }
        });
    }

    public void configLogoutBtn(){
        Button logoutBtn = findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ProfileSettings.this, MainActivity.class));
            }
        });
    }

    private void configBottomNavBar(){
        BottomNavigationView navView = findViewById(R.id.bottomNav);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(ProfileSettings.this, UserPvtProfile.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(ProfileSettings.this, HomePage.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(ProfileSettings.this, Messages.class));
                        break;
                }

                return false;
            }
        });
    }
}
