package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.icu.text.Edits;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    DatabaseReference GroupReference;

    private RecyclerView groupList;
    TextView chat;
    ImageButton add;

    String uid;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }

        groupList =findViewById(R.id.group_list);

        groupList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        GroupReference = FirebaseDatabase.getInstance().getReference().child("Group").child(uid);
        chat = findViewById(R.id.chat_text);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                overridePendingTransition(R.anim.bottom_anim, R.anim.top_anim);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(bottomNavMethod);

        add = findViewById(R.id.create_group_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateGroupActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserDataRecView> options = new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(GroupReference,UserDataRecView.class).build();
        FirebaseRecyclerAdapter<UserDataRecView, ChatActivity.ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<UserDataRecView, ChatActivity.ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatActivity.ChatsViewHolder holder, int position, @NonNull UserDataRecView model) {
                String usersIds = getRef(position).getKey();
                Log.d("TAG",usersIds);
                GroupReference.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("Information").child("name").getValue().toString();
                        String image = snapshot.child("Information").child("image").getValue().toString();

                        if(!image.equals("")){
                            Picasso.get().load(image).into(holder.profileImage);
                        }else{
                            holder.profileImage.setImageResource(R.drawable.profile);
                        }
                        holder.userName.setText(name);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),GroupChatUserActivity.class);
                                intent.putExtra("Group Visit ID",usersIds);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            @NonNull
            @Override
            public ChatActivity.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent,false);
                return new ChatActivity.ChatsViewHolder(view);
            }
        };

        groupList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName,userLastMessage;
        Button button;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImageRec);
            userName = itemView.findViewById(R.id.nameRecView);
            userLastMessage = itemView.findViewById(R.id.emailRecView);
            button = itemView.findViewById(R.id.addBtn);
            button.setVisibility(View.INVISIBLE);
        }
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod= item -> {
        switch (item.getItemId()){
            case R.id.home:
                Intent intentChat = new Intent(getApplicationContext(),HomeActivity.class);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                startActivity(intentChat);
                finish();
                break;
            case R.id.setting:
                Intent intentSetting = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intentSetting);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

        }

        return false;
    };



}