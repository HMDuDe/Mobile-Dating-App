package com.example.searchmysoulmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.Adapters.MessageAdapter;
import com.example.searchmysoulmate.Models.Chat;
import com.example.searchmysoulmate.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessagingUser extends AppCompatActivity {

    private ImageView profilePic;
    private EditText msgBodyTxt;
    private TextView username;
    private Button sendImgBtn;

    private DatabaseReference dbRef;
    private FirebaseAuth auth;

    MessageAdapter msgAdapter;
    List<Chat> chats;
    RecyclerView recyclerView;
    String contactUid, currentUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging_user);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = findViewById(R.id.chat_list_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendImgBtn = findViewById(R.id.sendBtn);
        msgBodyTxt = findViewById(R.id.msgBodyTxt);
        username = findViewById(R.id.usernameLbl);
        profilePic = findViewById(R.id.msgPartnerProfilePic);

        Intent intent = getIntent();
        contactUid = intent.getStringExtra("Partner");

        auth = FirebaseAuth.getInstance();

        currentUid = auth.getCurrentUser().getUid();
        Log.d("PARTNER USER ID: ", "" + contactUid);
        Log.d("CURRENT USER ID: ", "" + currentUid);
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(contactUid);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Log.d("MAIN PROFILE IMG: ", "" + user.getMainProfileImgPath());
                if(user.getMainProfileImgPath().equals("default")){
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext())
                            .load(user.getMainProfileImgPath())
                            .into(profilePic);

                    username.setText(user.getfName() + " " + user.getlName());
                }

                readMessage(currentUid, contactUid, user.getMainProfileImgPath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = String.valueOf(msgBodyTxt.getText());

                if(msg != "" && msg != " "){
                    sendMessage(currentUid, contactUid , msg);
                }

                msgBodyTxt.setText("");
            }
        });

        configProfileImgBtn();
    }

    public void configProfileImgBtn(){
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingUser.this, userPblcProfile.class);
                intent.putExtra("Partner", contactUid);
                Log.d("PARTNER UID: ", "" + contactUid);
                startActivity(intent);
            }
        });
    }

    public void sendMessage(String sender, String receiver, String message){
        dbRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        dbRef.child("Messaging").push().setValue(hashMap);
    }

    public void readMessage(final String myID, final String userID, final String imageURL){
        chats = new ArrayList<>();

        DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Messaging");

        dbRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d("POSTSNAPSHOT: ", "" + snapshot.getValue());
                        Chat chat = snapshot.getValue(Chat.class);

                        if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) || chat.getReceiver().equals(userID) && chat.getSender().equals(myID)) {

                            chats.add(chat);
                            Log.d("CHAT MESSAGE: ", "" + chat.getReceiver());

                        }

                        msgAdapter = new MessageAdapter(MessagingUser.this, chats, imageURL);
                        recyclerView.setAdapter(msgAdapter);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
