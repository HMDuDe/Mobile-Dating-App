package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class userPblcProfile extends AppCompatActivity {

    private String partnerID;
    private FirebaseAuth auth;
    private boolean isAuthorized, isBlocked;

    private DatabaseReference dbRef;

    private ImageView profileImg, img2, img3, img4, img5, img6;
    private TextView nameLbl, aboutMeLbl, ageLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pblc_profile);

        Intent intent = getIntent();
        partnerID = intent.getStringExtra("Partner");
        auth = FirebaseAuth.getInstance();

        profileImg = findViewById(R.id.img1View);

        img2 = findViewById(R.id.img2View);
        img3 = findViewById(R.id.img3View);
        img4 = findViewById(R.id.img4View);
        img5 = findViewById(R.id.img5View);
        img6 = findViewById(R.id.img6View);

        nameLbl = findViewById(R.id.nameLbl);
        aboutMeLbl = findViewById(R.id.textView7);
        ageLbl = findViewById(R.id.ageDisplLbl);

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(partnerID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                nameLbl.setText(user.getfName() + " " + user.getlName());
                ageLbl.setText(user.getAge());
                aboutMeLbl.setText(user.getAboutMe());

                Glide.with(userPblcProfile.this)
                        .load(user.getMainProfileImgPath())
                        .into(profileImg);

                Glide.with(userPblcProfile.this)
                        .load(user.getAddImg2())
                        .into(img2);
                Log.d("PUBLIC PROFILE PICS", "" + user.getAddImg2());

                Glide.with(userPblcProfile.this)
                        .load(user.getAddImg3())
                        .into(img3);

                Glide.with(userPblcProfile.this)
                        .load(user.getAddImg4())
                        .into(img4);

                Glide.with(userPblcProfile.this)
                        .load(user.getAddImg5())
                        .into(img5);

                Glide.with(userPblcProfile.this)
                        .load(user.getAddImg6())
                        .into(img6);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        configMsgBtn();
        configBottomNavBar();
        configBlockUserBtn();
    }

    public void configBlockUserBtn(){
        Button blockUser = findViewById(R.id.blockUserBtn);
        blockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("Blocked Contacts");
                dbRef.push().setValue(partnerID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(userPblcProfile.this, "User Blocked!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(userPblcProfile.this, HomePage.class);
                                startActivity(intent);
                            }
                        });


            }
        });
    }
    public void configMsgBtn(){
        FloatingActionButton msgUserBtn = findViewById(R.id.floatingActionButton);
        msgUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference("Users").child(partnerID).child("Authorised contacts");
                DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Users").child(partnerID).child("Blocked Contacts");

                //Using dbRef2 to check if user is blocked or not
                dbRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            if(postSnapshot.getValue().equals(auth.getCurrentUser().getUid())){
                                isBlocked = true;
                                break;
                            }
                        }

                        //Using dbRef1 to check if user is authorised already or not
                        dbRef1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    if(postSnapshot.getValue().equals(auth.getCurrentUser().getUid())){
                                        isAuthorized = true;
                                    }
                                }

                                //If user is authorised
                                if(isBlocked == false && isAuthorized == true){
                                    Intent intent = new Intent(userPblcProfile.this, MessagingUser.class);
                                    intent.putExtra("Partner", partnerID);
                                    Toast.makeText(userPblcProfile.this, "Good luck!", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);

                                //If user isn't authorised but isn't blocked either
                                }else if(isBlocked == false && isAuthorized == false){
                                    sendMessage(auth.getCurrentUser().getUid(), partnerID);
                                    Toast.makeText(userPblcProfile.this, "MessageRequest Sent", Toast.LENGTH_SHORT).show();

                                //Else assume user is blocked
                                }else{
                                    Toast.makeText(userPblcProfile.this, "This user blocked you", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void sendMessage(String sender, String receiver){
        DatabaseReference dbRef;
        dbRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", "Message Request");

        dbRef.child("Messaging").push().setValue(hashMap);
    }

    private void configBottomNavBar(){
        BottomNavigationView navView = findViewById(R.id.bottomNav);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(userPblcProfile.this, UserPvtProfile.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(userPblcProfile.this, HomePage.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(userPblcProfile.this, Messages.class));
                        break;
                }

                return false;
            }
        });
    }
}
