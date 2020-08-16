package com.example.searchmysoulmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.MessagingUser;
import com.example.searchmysoulmate.Models.Chat;
import com.example.searchmysoulmate.Models.User;
import com.example.searchmysoulmate.R;
import com.example.searchmysoulmate.userPblcProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageHubAdapter extends RecyclerView.Adapter<MessageHubAdapter.ImageViewHolder>{

    private Context context;
    private List<User> users;
    private FirebaseAuth auth;

    public MessageHubAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;

        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contact, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final User user = users.get(position);

        holder.userName.setText(user.getfName() + " " + user.getlName());
        Glide.with(context)
                .load(user.getMainProfileImgPath())
                .into(holder.profileImg);

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessagingUser.class);
                intent.putExtra("Partner", user.getUserID());
                context.startActivity(intent);
            }
        });

        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, userPblcProfile.class);
                intent.putExtra("Partner", user.getUserID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(!users.isEmpty()){
            return users.size();
        }else{
            return 0;
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImg;
        public TextView userName;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.userProfileImg);
            userName = itemView.findViewById(R.id.nameSurnameLbl);
        }
    }
}
