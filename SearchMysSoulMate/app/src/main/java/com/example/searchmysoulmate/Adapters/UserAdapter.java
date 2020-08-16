package com.example.searchmysoulmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.HomePage;
import com.example.searchmysoulmate.MessagingUser;
import com.example.searchmysoulmate.R;
import com.example.searchmysoulmate.Models.User;
import com.example.searchmysoulmate.userPblcProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context context;
    private List<User> users;
    private FirebaseAuth auth;

    public UserAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;

        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ImageViewHolder holder, int position) {
        final User currentUser = users.get(position);

        String fullName = currentUser.getfName()+ "\n" + currentUser.getlName();

        holder.userName.setText(fullName);
        Picasso.with(context)
                .load(currentUser.getMainProfileImgPath())
                .into(holder.showProfile);

        Log.d("OTHER RV USER DETAILS", "" + currentUser.getfName());

        holder.messageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(auth.getCurrentUser().getUid(), currentUser.getUserID());
                Toast.makeText(context, "Message Request sent", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference blockUserRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("Blocked Contacts");

                blockUserRef.push().setValue(currentUser.getUserID());
                Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, HomePage.class);
                context.startActivity(intent);
            }
        });

        holder.showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, userPblcProfile.class);
                intent.putExtra("Partner", currentUser.getUserID());
                context.startActivity(intent);
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

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public ImageView deleteProfile, showProfile, messageUser;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNameLbl);
            deleteProfile = itemView.findViewById(R.id.deleteUserPic);
            showProfile = itemView.findViewById(R.id.userProfilePic);
            messageUser = itemView.findViewById(R.id.messageUserPic);

        }
    }
}
