package com.example.searchmysoulmate;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.searchmysoulmate.Adapters.MessageHubAdapter;
import com.example.searchmysoulmate.Models.Chat;
import com.example.searchmysoulmate.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Messages extends AppCompatActivity {

    private RecyclerView rv;
    private List<Chat> chats;
    private List<User> users;
    private List<String> checkedIDs;
    private FirebaseAuth auth;
    private DatabaseReference dbRef1;
    private int count, count2;
    private MessageHubAdapter hubAdapter;
    private List<String> blockedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        auth = FirebaseAuth.getInstance();

        rv = findViewById(R.id.msgMainRV);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        chats = new ArrayList<>();
        checkedIDs = new ArrayList<>();
        blockedUsers = new ArrayList<>();

        count = 0;
        count2 = 0;

        DatabaseReference getBlockedUsersRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("Blocked Contacts");
        getBlockedUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapsot : dataSnapshot.getChildren()) {
                    String blockedUserString = postSnapsot.getValue(String.class);

                    blockedUsers.add(blockedUserString);
                }

                Log.d("BLOCKED USER LIST: ", "" + blockedUsers);

                //STEP 1: Adding all MY messages to List
                dbRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("CURRENT USER ID: ", "" + auth.getCurrentUser().getUid());
                        Log.d("CHAT DATASNAPSHOT", "" + dataSnapshot.getValue());
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Chat currentMsg = postSnapshot.getValue(Chat.class);

                            Log.d("CURRENT MESSAGE: ", "" + currentMsg);
                            if(currentMsg.getSender().equals(auth.getCurrentUser().getUid()) || currentMsg.getReceiver().equals(auth.getCurrentUser().getUid())){

                                Log.d("CHAT ADDED: ", "" + currentMsg);
                                chats.add(currentMsg);
                                count2++;

                            }
                        }

                        Log.d("CHATS ARRAYLIST: ", "" + chats);
                        Log.d("CHAT ARRAY SIZE: ", "" + chats.size());

                        //STEP 2: Get partner objects into arrayList users
                        for(int i = 0; i <= chats.size() -1; i++){
                            users = new ArrayList<>();
                            Log.d("LOOP ENTERED", "==========================================================");
                            if(chats.get(i).getReceiver().equals(auth.getCurrentUser().getUid())){
                                Log.d("CHATS(I) GET RECEIVER", "" + chats.get(i).getReceiver());

                                if(!checkedIDs.contains(chats.get(i).getSender())){
                                    Log.d("IF STATEMENT", "TRUE");
                                    DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(chats.get(i).getSender());

                                    final int finalI = i;
                                    dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!users.contains(dataSnapshot.getValue(User.class))){
                                                Log.d("BLOCKED USER LIST: ", "" + blockedUsers);
                                                if(!blockedUsers.contains(chats.get(finalI).getSender())){
                                                    users.add(dataSnapshot.getValue(User.class));
                                                    count++;
                                                    checkedIDs.add(chats.get(finalI).getSender());
                                                }
                                            }
                                            //hubAdapter.notifyDataSetChanged();

                                            if(count < 1){
                                                Toast.makeText(Messages.this, "No Chats found", Toast.LENGTH_SHORT).show();
                                            }else{

                                                Log.d("USERS ARR CONTENTS", "" + users);
                                                hubAdapter = new MessageHubAdapter(Messages.this, removeDuplicats(users));
                                                rv.setAdapter(hubAdapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }else{
                                Log.d("ELSE STATEMENT: ", "BEFORE NESTED IF");
                                if(!checkedIDs.contains(chats.get(i).getReceiver())){
                                    DatabaseReference dbRef3 = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(chats.get(i).getReceiver());

                                    final int finalI1 = i;
                                    dbRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!users.contains(dataSnapshot.getValue(User.class))){
                                                if(!blockedUsers.contains(chats.get(finalI1).getReceiver())){
                                                    users.add(dataSnapshot.getValue(User.class));
                                                    count++;
                                                    checkedIDs.add(chats.get(finalI1).getSender());
                                                }
                                            }

                                            //hubAdapter.notifyDataSetChanged();
                                            if(count < 1){
                                                Toast.makeText(Messages.this, "No Chats found", Toast.LENGTH_SHORT).show();
                                            }else{

                                                Set<User> set = new HashSet<>(users);
                                                users.clear();
                                                users.addAll(set);

                                                Log.d("USERS ARR SIZE", "" + users.size());
                                                hubAdapter = new MessageHubAdapter(Messages.this, removeDuplicats(users));
                                                rv.setAdapter(hubAdapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
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

        dbRef1 = FirebaseDatabase.getInstance().getReference("Messaging");



        configBottomNavBar();
    }

    public List<User> removeDuplicats(List<User> arrList){

        for(int i = 0; i < arrList.size()-1; i++){
            if(arrList.get(i).getUserID().equals(arrList.get(i+1).getUserID())){
                arrList.remove(i);
            }
        }

        Log.d("EDITED LIST: ", "" + arrList);

        return arrList;
    }

    private void configBottomNavBar(){
        BottomNavigationView navView = findViewById(R.id.bottomNav);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(Messages.this, UserPvtProfile.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(Messages.this, HomePage.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(Messages.this, Messages.class));
                        break;
                }

                return false;
            }
        });
    }
}
