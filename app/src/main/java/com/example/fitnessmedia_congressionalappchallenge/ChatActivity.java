package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatLists;
    DatabaseReference chatsReference, usersReference;
    FirebaseUser user;
    String uid;
    TextView group;


    TabsAccessorAdapter mytabsAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(bottomNavMethod);

        group = findViewById(R.id. groups);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            uid = user.getUid();
        }

        chatLists =findViewById(R.id.group_list);
        chatLists.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        chatsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);
        usersReference = FirebaseDatabase.getInstance().getReference().child("User");



    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserDataRecView> options = new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(chatsReference,UserDataRecView.class).build();
        FirebaseRecyclerAdapter<UserDataRecView,ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<UserDataRecView, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull UserDataRecView model) {
                String usersIds = getRef(position).getKey();
                Log.d("TAG",usersIds);
                usersReference.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue().toString();
                        String image = snapshot.child("image").getValue().toString();

                        if(!image.equals("")){
                            Picasso.get().load(image).into(holder.profileImage);
                        }else{
                            holder.profileImage.setImageResource(R.drawable.profile);
                        }
                        holder.userName.setText(name);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(),UserChatActivity.class);
                                intent.putExtra("visit_user_id",usersIds);
                                intent.putExtra("visit_user_name",name);
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
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent,false);
                return new ChatsViewHolder(view);
            }
        };

        chatLists.setAdapter(adapter);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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