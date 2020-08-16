package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.searchmysoulmate.Adapters.UserAdapter;
import com.example.searchmysoulmate.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*Display users from same city if users have not been messaged yet,
* display only users which are the gender selected by current user
* don't display users who've been blocked/deleted*/
public class HomePage extends AppCompatActivity {

    private Boolean showMales, showFemales, isBlocked;
    private DatabaseReference dbRef, dbRef2;
    RecyclerView recyclerView;

    private List<User> users;
    UserAdapter userAdapter;
    int count = 0;
    private String userHomeCity, userGender;
    private User thisUser;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    private List<String> blockedUsers, authorizedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        showFemales = true;
        showMales = true;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        users = new ArrayList<>();
        blockedUsers = new ArrayList<>();
        authorizedUsers = new ArrayList<>();

        DatabaseReference getBlockedUsersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Blocked Contacts");
        getBlockedUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapsot : dataSnapshot.getChildren()) {
                    String blockedUserString = postSnapsot.getValue(String.class);

                    blockedUsers.add(blockedUserString);
                }

                Log.d("BLOCKED USER LIST: ", "" + blockedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference getAuthUsersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Authorised contacts");
        getAuthUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    String authUserString = postSnapshot.getValue(String.class);

                    authorizedUsers.add(authUserString);
                }

                Log.d("AUTH USER LIST: ", "" + authorizedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef2 = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        dbRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisUser = dataSnapshot.getValue(User.class);

                userHomeCity = dataSnapshot.getValue(User.class).getHomeCity();
                showFemales = dataSnapshot.getValue(User.class).isShowFemales();
                showMales = dataSnapshot.getValue(User.class).isShowMales();

                if(thisUser.getGender().equals("Male")){
                    userGender = "Female";
                }else{
                    userGender = "Male";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Show users based on userPreferences
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                try {
                    if (showFemales == true && showMales == true) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            User allUsers = new User();
                            if (!blockedUsers.contains(postSnapshot.getValue(User.class).getUserID())
                                    && !authorizedUsers.contains(postSnapshot.getValue(User.class).getUserID())) {

                                allUsers = postSnapshot.getValue(User.class);
                            }

                            if (allUsers.getHomeCity().equals(userHomeCity)) {
                                if (!allUsers.getUserID().equals(auth.getCurrentUser().getUid())) {
                                    if (Integer.parseInt(allUsers.getAge()) >= Integer.parseInt(thisUser.getMinAge())
                                            && Integer.parseInt(allUsers.getAge()) <= Integer.parseInt(thisUser.getMaxAge())) {

                                        Log.d("OBJECT.GET USERID: ", "" + allUsers.getUserID());
                                        Log.d("USER ID: ", "" + auth.getCurrentUser().getUid());

                                        users.add(allUsers);
                                        count++;
                                    }
                                }
                            }
                        }

                        if (count < 1) {
                            Toast.makeText(HomePage.this, "No new users found", Toast.LENGTH_SHORT).show();

                        }

                        userAdapter.notifyDataSetChanged();

                    } else if (showFemales == true && showMales == false) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            User femaleUsers = new User();
                            if (!blockedUsers.contains(postSnapshot.getValue(User.class).getUserID())
                                    && !authorizedUsers.contains(postSnapshot.getValue(User.class).getUserID())) {

                                femaleUsers = postSnapshot.getValue(User.class);
                            }

                            if (femaleUsers.getHomeCity().equals(userHomeCity)) {
                                if (femaleUsers.getGender().equals("Female")) {
                                    if (!femaleUsers.getUserID().equals(auth.getCurrentUser().getUid())) {
                                        if (Integer.parseInt(femaleUsers.getAge()) >= Integer.parseInt(thisUser.getMinAge())
                                                && Integer.parseInt(femaleUsers.getAge()) <= Integer.parseInt(thisUser.getMaxAge())) {

                                            Log.d("BLOCKED USER STATE: ", "" + isBlocked);
                                            users.add(femaleUsers);
                                            count++;
                                        }

                                    }

                                }
                            }
                        }

                        if (count < 1) {
                            Toast.makeText(HomePage.this, "No new users found", Toast.LENGTH_SHORT).show();

                        }

                        userAdapter.notifyDataSetChanged();
                    } else if (showFemales == false && showMales == true) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            User maleUsers = new User();
                            if (!blockedUsers.contains(postSnapshot.getValue(User.class).getUserID())
                                    && !authorizedUsers.contains(postSnapshot.getValue(User.class).getUserID())) {
                                maleUsers = postSnapshot.getValue(User.class);
                            }

                            if (maleUsers.getHomeCity().equals(userHomeCity)) {
                                if (maleUsers.getGender().equals("Male")) {
                                    if (!maleUsers.getUserID().equals(auth.getCurrentUser().getUid())) {
                                        if (Integer.parseInt(maleUsers.getAge()) >= Integer.parseInt(thisUser.getMinAge())
                                                && Integer.parseInt(maleUsers.getAge()) <= Integer.parseInt(thisUser.getMaxAge())) {

                                            users.add(maleUsers);
                                            count++;
                                        }
                                    }
                                }
                            }
                        }

                        if (count < 1) {
                            Toast.makeText(HomePage.this, "No new users found", Toast.LENGTH_SHORT).show();

                        }

                        userAdapter.notifyDataSetChanged();
                    }else{
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            User opoGenderUser = new User();
                            if (!blockedUsers.contains(postSnapshot.getValue(User.class).getUserID())
                                    && !authorizedUsers.contains(postSnapshot.getValue(User.class).getUserID())) {
                                opoGenderUser = postSnapshot.getValue(User.class);
                            }

                            if (opoGenderUser.getHomeCity().equals(userHomeCity)) {
                                if (opoGenderUser.getGender().equals(userGender)) {
                                    if (!opoGenderUser.getUserID().equals(auth.getCurrentUser().getUid())) {
                                        if (Integer.parseInt(opoGenderUser.getAge()) >= Integer.parseInt(thisUser.getMinAge())
                                                && Integer.parseInt(opoGenderUser.getAge()) <= Integer.parseInt(thisUser.getMaxAge())) {

                                            users.add(opoGenderUser);
                                            count++;
                                        }
                                    }
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    Log.d("Error message: ", "" + e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userAdapter = new UserAdapter(HomePage.this, users);
        recyclerView.setAdapter(userAdapter);

        configBottomNavBar();
    }

    private void configBottomNavBar(){
        BottomNavigationView navView = findViewById(R.id.bottomNav);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(HomePage.this, UserPvtProfile.class));
                        break;
                    case R.id.home:
                        startActivity(new Intent(HomePage.this, HomePage.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(HomePage.this, Messages.class));
                        break;
                }

                return false;
            }
        });
    }
}
