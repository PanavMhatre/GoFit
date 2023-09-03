package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentFriendsActivity extends AppCompatActivity {

    DatabaseReference FriendReference, UserReference;
    FirebaseUser user;
    private String Uid;
    TextView noFriend;

    RecyclerView Friends_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_friends);

        ChangeSystemElements();

        noFriend = findViewById(R.id.noFriend);
        user = FirebaseAuth.getInstance().getCurrentUser();
        InstantiateUserID();
        FriendReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(Uid);
        UserReference = FirebaseDatabase.getInstance().getReference().child("User");
        Friends_List = findViewById(R.id.FriendList);
        Friends_List.setLayoutManager(new LinearLayoutManager(this));

        FriendReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    noFriend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DisplayAllFriends();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void ChangeSystemElements() {
        ImageView leftIcon = findViewById(R.id.backIcon);
        TextView text = findViewById(R.id.activityText);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Friend_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        text.setText("Current Friends");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    private void InstantiateUserID() {
        Uid = user.getUid();
    }




    private void DisplayAllFriends() {
        FirebaseRecyclerOptions<UserDataRecView> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(FriendReference,UserDataRecView.class).build();
        FirebaseRecyclerAdapter<UserDataRecView, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserDataRecView, FriendsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull UserDataRecView model) {
//                holder.email.setText(model.getDate());

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
                            holder.button.setVisibility(View.INVISIBLE);
                            holder.name.setText(name);
                            holder.layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CurrentFriendsActivity.this, CurrentFriendPersonProfileActivity.class);
                                    intent.putExtra("ID_Friend_Request_Friend_Activity", userIds);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users,parent,false);
                return new FriendsViewHolder(view);
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