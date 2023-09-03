package com.example.fitnessmedia_congressionalappchallenge;


import android.media.Image;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class MessagesAdaptar extends RecyclerView.Adapter<MessagesAdaptar.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    DatabaseReference userRef;
    FirebaseUser user;

    public  MessagesAdaptar (List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String messageSenderId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User").child(messageSenderId);
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        holder.recieverMessageText.setVisibility(View.INVISIBLE);
        holder.senderMessageText.setVisibility(View.INVISIBLE);
        holder.senderImage.setVisibility(View.INVISIBLE);
        holder.recieverImage.setVisibility(View.INVISIBLE);

        if(fromMessageType.equals("text")){


            if(fromUserID.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_reciever_layout);
                holder.senderMessageText.setText(messages.getMessage());
            }else{
                holder.recieverMessageText.setVisibility(View.VISIBLE);
                holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
                holder.recieverMessageText.setText(messages.getMessage());

            }
        }else if(fromMessageType.equals("image")){


            if(fromUserID.equals(messageSenderId)){
                holder.senderImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).resize(500,500).into(holder.senderImage);
            }else{
                holder.recieverImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).resize(500,500).into(holder.recieverImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public  class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, recieverMessageText;
        public ImageView senderImage, recieverImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.senderMessages);
            recieverMessageText = itemView.findViewById(R.id.recieverMessage);
            senderImage = itemView.findViewById(R.id.senderImage);
            recieverImage = itemView.findViewById(R.id.recieverImage);
        }
    }
}
