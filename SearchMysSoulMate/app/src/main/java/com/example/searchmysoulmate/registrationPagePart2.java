package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.searchmysoulmate.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class registrationPagePart2 extends AppCompatActivity {

    private DatabaseReference dbRef;
    private ImageView images;
    private EditText aboutMeTxt, ageTxt, minAgeTxt, maxAgeTxt;
    private String aboutMe, age;
    private int pickImageRequest = 1, minAge, maxAge;
    private Uri filePath;

    private final String apiKey = "_6ZrgmqZlE4Eiy92gEZ5-8etfiCSRsO2vaWNsSdiIP2t";


    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page_part2);

        images = findViewById(R.id.img1);
        aboutMeTxt = findViewById(R.id.aboutMeTxt);
        ageTxt = findViewById(R.id.ageTxt);
        minAgeTxt = findViewById(R.id.minAgeTxt);
        maxAgeTxt = findViewById(R.id.maxAgeTxt);

        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        newUser = (User) intent.getSerializableExtra("userObject");
        Log.d("OBJECT RETRIEVED: ", "" + newUser);

        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        configNextBtn();
        configSelectImgBtn1();
    }

    private void configNextBtn(){
        Button next = findViewById(R.id.nextBtn2);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutMe = String.valueOf(aboutMeTxt.getText());
                age = String.valueOf(ageTxt.getText());
                minAge = Integer.parseInt(String.valueOf(minAgeTxt.getText()));
                maxAge = Integer.parseInt(String.valueOf(maxAgeTxt.getText()));

                auth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                        .addOnCompleteListener(registrationPagePart2.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        try{
                                            throw task.getException();
                                        }catch(FirebaseAuthUserCollisionException e){
                                            Toast.makeText(registrationPagePart2.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                                        }catch(Exception e){
                                            Log.d("REGISTER EXCEPTION: ", "" + e.getMessage());
                                        }

                                    }else{
                                        task.getResult().getUser().getUid();
                                        newUser.setAboutMe(aboutMe);
                                        newUser.setAge(age);
                                        newUser.setUserID(auth.getCurrentUser().getUid());
                                        newUser.setMinAge(String.valueOf(minAge));
                                        newUser.setMaxAge(String.valueOf(maxAge));

                                        dbRef.child(task.getResult().getUser().getUid()).setValue(newUser);

                                        Toast.makeText(registrationPagePart2.this, "You're Registered!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(registrationPagePart2.this, registrationPart3.class));
                                    }
                                }
                            });

            }
        });
    }

    //Image selection & upload
    private  void configSelectImgBtn1(){
        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getImg1 = new Intent();
                getImg1.setType("image/*");
                getImg1.setAction(getImg1.ACTION_GET_CONTENT);

                Log.d("BUTTON PRESSED: ", "Image 1 method" + pickImageRequest);
                startActivityForResult(Intent.createChooser(getImg1, "Choose Picture"), pickImageRequest);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        filePath = data.getData();
        Log.d("DATA: ", "" + data.getData());
        if(requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            Log.d("FILE PATH:", "" + filePath);

            Picasso.with(this).load(data.getData()).into(images);
            pickImageRequest++;

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
                                    newUser.setMainProfileImgPath(String.valueOf(uri));
                                    Log.d("USER IMG FILEPATH ", "" + uri);
                                }
                            });

                            Log.d("USER IMG PATH: ", "" + newUser.getMainProfileImgPath());
                            Toast.makeText(registrationPagePart2.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(registrationPagePart2.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
