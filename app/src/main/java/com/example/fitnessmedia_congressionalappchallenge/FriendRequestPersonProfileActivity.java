package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestPersonProfileActivity extends AppCompatActivity {

    TextView name,age,ButtonMessage;
    CircleImageView profile;
    RelativeLayout acceptFriendRequest,declineFriendRequest;

    DatabaseReference ref,FriendRequestRef,SenderFriendRequestRef, FriendsRef;
    String receiverUserID,sendUserID;
    String Username,imageURl,ageTxt,CURRENT_STATE;

    FirebaseUser fUser;

    String saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request_person_profile);

        ChangeSystemElements();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        sendUserID = fUser.getUid();

        intializeFields();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Username = snapshot.child("name").getValue().toString();
                ageTxt = snapshot.child("age").getValue().toString();
                imageURl = snapshot.child("image").getValue().toString();

                name.setText(Username);
                age.setText(ageTxt);
                if(!imageURl.equals("")){
                    Picasso.get().load(imageURl).into(profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        acceptFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","Button Clicked");
                AcceptFriendRequest();
            }
        });

        declineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelFriendRequest();
            }
        });
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
                Intent intent = new Intent(getApplicationContext(),FriendRequestActivity.class);
                startActivity(intent);
                finish();
            }
        });
        text.setText("Profile");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    private void AcceptFriendRequest() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        Log.d("TAG","Reached CH 1");
        FriendsRef.child(sendUserID).child(receiverUserID).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG","Reached CH 2");
                if(task.isSuccessful()){
                    Log.d("TAG","Reached CH 3");
                    FriendsRef.child(receiverUserID).child(sendUserID).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SenderFriendRequestRef.child(sendUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FriendRequestRef.child(receiverUserID).child(sendUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    CURRENT_STATE = "friends";
                                                    Intent intent = new Intent(getApplicationContext(),FriendRequestActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void CancelFriendRequest() {
        SenderFriendRequestRef.child(sendUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(receiverUserID).child(sendUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getApplicationContext(),FriendRequestActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                }
            }
        });
    }



    private void intializeFields() {
        name = findViewById(R.id.nameProfile);
        age = findViewById(R.id.agePRofile);
        profile = findViewById(R.id.profilePicPerson);
        acceptFriendRequest = findViewById(R.id.acceptButton);
        declineFriendRequest = findViewById(R.id.declineButton);
        CURRENT_STATE = "request_received";
        receiverUserID = getIntent().getExtras().get("ID_Friend_Request").toString();
        profile = findViewById(R.id.profilePicPerson);
        ButtonMessage = findViewById(R.id.message);
        ref= FirebaseDatabase.getInstance().getReference("User").child(receiverUserID);
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        SenderFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");



    }
}