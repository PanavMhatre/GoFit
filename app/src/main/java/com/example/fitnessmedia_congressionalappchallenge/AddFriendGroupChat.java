package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendGroupChat extends AppCompatActivity {

    DatabaseReference GroupReference, UserReference, FriendReference, GroupMessagesReference;
    FirebaseUser user;
    RecyclerView Friends_List;
    Button button;
    String push_id, name_group;
    String UID;
    Boolean onCLicked = false;
    StorageReference StorageRef;
    String imageURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_group_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageRef = FirebaseStorage.getInstance().getReference().child("GroupImages");
        push_id = getIntent().getStringExtra("Group_Push_ID_Key");
        name_group = getIntent().getStringExtra("Group_NAME");


        Log.d("TAG","About to retrieve the image url and " + push_id);

        imageURL = getIntent().getStringExtra("Group_IMAGE");
        UID = user.getUid();
        FriendReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(UID);

        GroupReference = FirebaseDatabase.getInstance().getReference().child("Group");
        UserReference = FirebaseDatabase.getInstance().getReference().child("User");
        Friends_List = findViewById(R.id.list_of_Friends);
        Friends_List.setLayoutManager(new LinearLayoutManager(this));

        GroupMessagesReference = FirebaseDatabase.getInstance().getReference().child("GroupMessages");

        DisplayAllFriends();

        button = findViewById(R.id.createButtonGroup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupReference.child(UID).child(push_id).child("Information").child("name").setValue(name_group);
                GroupReference.child(UID).child(push_id).child("Information").child("image").setValue(imageURL);

                startActivity(new Intent(getApplicationContext(),GroupActivity.class));
            }
        });

    }

    private void DisplayAllFriends() {
        FirebaseRecyclerOptions<UserDataRecView> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(FriendReference,UserDataRecView.class).build();
        FirebaseRecyclerAdapter<UserDataRecView, CurrentFriendsActivity.FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserDataRecView, CurrentFriendsActivity.FriendsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CurrentFriendsActivity.FriendsViewHolder holder, int position, @NonNull UserDataRecView model) {
                holder.email.setVisibility(View.INVISIBLE);

                String userIds = getRef(position).getKey();

                UserReference.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String name = snapshot.child("name").getValue().toString();
                            String image = snapshot.child("image").getValue().toString();
                            if(!image.equals("")){
                                Picasso.get().load(image).into(holder.profile);
                            }else{
                                holder.profile.setImageResource(R.drawable.profile);
                            }
                            holder.name.setText(name);

                                holder.button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(onCLicked){
                                            GroupReference.child(userIds).child(push_id).child("Information").removeValue();
                                            holder.button.setText("Add");
                                            holder.button.setBackgroundColor(getResources().getColor(R.color.log_blue));
                                            onCLicked = false;
                                        }else{
                                            GroupReference.child(userIds).child(push_id).child("Information").child("name").setValue(name_group);
                                            GroupReference.child(userIds).child(push_id).child("Information").child("image").setValue(imageURL);
                                            holder.button.setText("Remove");
                                            holder.button.setBackgroundColor(Color.RED);
                                            onCLicked = true;
                                        }
                                    }

                                });



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public CurrentFriendsActivity.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users,parent,false);
                return new CurrentFriendsActivity.FriendsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        Friends_List.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView name,email;
        Button button;
        CardView layout;
        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            profile = itemView.findViewById(R.id.profileImageRec);
            name = itemView.findViewById(R.id.nameRecView);
            email = itemView.findViewById(R.id.emailRecView);
            button = itemView.findViewById(R.id.addBtn);
            layout = itemView.findViewById(R.id.layout);
        }
    }

}