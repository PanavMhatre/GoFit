package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public Button profile;
    public TextView txtName;

    RelativeLayout notificationButton,friendButton,securityButton;

    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;

    String key;

    private FirebaseAuth auth;
    private String uid;

    String userImage;
    ImageView profilePicture;

    String name;

    FirebaseUser user;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profilePicture = findViewById(R.id.profilePic);

        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.blue));
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("User");
        mDataRef = FirebaseDatabase.getInstance().getReference("User");

        securityButton = findViewById(R.id.relativeLayoutButtonSecurity);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        Log.d("TAG",uid);

        notificationButton = findViewById(R.id.relativeLayoutButtonNotificiation);
        friendButton = findViewById(R.id.relativeLayoutButtonFriends);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        securityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SecurityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        String userName = getIntent().getStringExtra("Name");
        System.out.println(userName);
        Log.d("TAG","Reached");


        txtName = findViewById(R.id.name);

        profile = findViewById(R.id.profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ActivityProfile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,Friend_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    if((child.getKey()).equals(uid)){
                        String imageUrl = child.child("image").getValue().toString();
                        if(!imageUrl.equals("")){
                            Picasso.get().load(imageUrl).into(profilePicture);
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    Log.d("TAG",child+"");
                    if((child.getKey()).equals(uid)){
                        name = child.child("name").getValue().toString();
                        Log.d("TAG",name);
                        txtName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(bottomNavMethod);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod= item -> {
        switch (item.getItemId()){
            case R.id.media:
                Intent intentChat = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intentChat);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;
            case R.id.home:
                Intent intentSetting = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intentSetting);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;

        }
        return false;
    };




}