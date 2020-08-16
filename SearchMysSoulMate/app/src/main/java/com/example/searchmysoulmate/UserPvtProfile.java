package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.Models.Image;
import com.example.searchmysoulmate.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserPvtProfile extends AppCompatActivity {

    private DatabaseReference dbRef;
    private FirebaseAuth auth;
    private StorageReference storeRef;

    private TextView fullNameLbl, ageLbl, aboutMeLbl;
    private String fName, lName;
    private ImageView img1;
    private Uri filePath;
    private ImageView userImage;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pvt_profile);

        fullNameLbl = findViewById(R.id.nameLbl);
        ageLbl = findViewById(R.id.ageDisplLbl);
        aboutMeLbl = findViewById(R.id.aboutMeLbl);

        img1 = findViewById(R.id.img1View);

        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userDetails = dataSnapshot.getValue(User.class);
                fName = userDetails.getfName();
                lName = userDetails.getlName();

                fullNameLbl.setText(fName + " " + lName);
                ageLbl.setText(userDetails.getAge());
                aboutMeLbl.setText(userDetails.getAboutMe());

                Picasso.with(UserPvtProfile.this)
                        .load(userDetails.getMainProfileImgPath())
                        .into(img1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //No code is required here
            }
        });

        configBottomNavBar();
        configSettingsBtn();
    }

    public void configSettingsBtn(){
        Button settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserPvtProfile.this, ProfileSettings.class));
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
                        startActivity(new Intent(UserPvtProfile.this, UserPvtProfile.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(UserPvtProfile.this, HomePage.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(UserPvtProfile.this, Messages.class));
                        break;
                }

                return false;
            }
        });
    }
}
