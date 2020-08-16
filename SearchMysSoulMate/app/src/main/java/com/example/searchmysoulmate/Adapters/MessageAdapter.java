package com.example.searchmysoulmate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.searchmysoulmate.HomePage;
import com.example.searchmysoulmate.Messages;
import com.example.searchmysoulmate.MessagingUser;
import com.example.searchmysoulmate.Models.Chat;
import com.example.searchmysoulmate.Models.User;
import com.example.searchmysoulmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ImageViewHolder> {

    public static final int msgTypeLeft = 0;
    public static final int msgTypeRight = 1;
    public static final int msgRequest = 2;

    private Context context;
    private List<Chat> chats;
    private String imageURL;
    private boolean isAuthorized;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private User requestingUser;

    public MessageAdapter(Context context, List<Chat> chats, String imageURL){
        this.context = context;
        this.chats = chats;
        this.imageURL = imageURL;

        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == msgTypeRight){
            View v = LayoutInflater.from(context).inflate(R.layout.msg_item_right, parent, false);
            return new MessageAdapter.ImageViewHolder(v);
        }else if(viewType == msgTypeLeft){
            View v = LayoutInflater.from(context).inflate(R.layout.msg_item_left, parent, false);
            return new MessageAdapter.ImageViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.msg_request, parent, false);
            return new MessageAdapter.ImageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ImageViewHolder holder, final int position) {
        final Chat chat = chats.get(position);
        Log.d("ADPT CHAT MESSAGE", "" + chat.getMessage());

        if(getItemViewType(position) == msgTypeRight){
            holder.sentMessage.setText(chat.getMessage());

        }else if(getItemViewType(position) == msgTypeLeft){
            holder.receivedMessage.setText(chat.getMessage());
            if(imageURL.equals("default")){
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            }else{
                Log.d("PARTNER IMAGE URL: ", "" + imageURL);
                Glide.with(context).load(imageURL).into(holder.profilePic);
            }
        }else{
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender());
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    requestingUser = dataSnapshot.getValue(User.class);

                    holder.msgRequestLbl.setText(requestingUser.getfName() + " " + requestingUser.getlName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid())
                            .child("Authorised contacts");
                    dbRef2.push().setValue(chat.getSender());

                    DatabaseReference dbRef3 = FirebaseDatabase.getInstance().getReference("Users").child(chat.getSender())
                            .child("Authorised contacts");
                    dbRef3.push().setValue(auth.getCurrentUser().getUid());

                    DatabaseReference dbRef4 = FirebaseDatabase.getInstance().getReference("Messaging");
                    dbRef4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                Chat msgInstance = postSnapshot.getValue(Chat.class);

                                if(msgInstance.getReceiver().equals(auth.getCurrentUser().getUid())){
                                    postSnapshot.getRef().removeValue();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            holder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference blockUserRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid()).child("Blocked Contacts");

                    blockUserRef.push().setValue(requestingUser.getUserID());
                    Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Messages.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView sentMessage, receivedMessage, msgRequestLbl;
        public ImageView profilePic;

        public Button acceptBtn, declineBtn;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            sentMessage = itemView.findViewById(R.id.sentMsg);
            receivedMessage = itemView.findViewById(R.id.receivedMsg);
            profilePic = itemView.findViewById(R.id.profilePic);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            declineBtn = itemView.findViewById(R.id.declineBtn);
            msgRequestLbl = itemView.findViewById(R.id.msgRequestLbl);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(chats.get(position).getSender().equals(user.getUid())){
            Log.d("USER MESSAGE DETECTED: ", "msgTypeRight");
            return msgTypeRight;
        }else{
            if(chats.get(position).getMessage().equals("Message Request")){
                return msgRequest;
            }

            Log.d("USER MESSAGE DETECTED: ", "msgTypeRight");
            return msgTypeLeft;
        }
    }
}
