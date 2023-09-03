package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    TextView name,age,ButtonMessage;
    CircleImageView profile;
    RelativeLayout sendFriendRequest,cancelFriendRequest;

    DatabaseReference ref,FriendRequestRef,SenderFriendRequestRef, FriendsRef;
    String receiverUserID,sendUserID;
    String Username,imageURl,ageTxt,CURRENT_STATE;

    FirebaseUser fUser;

    private void ChangeSystemElements() {
        ImageView leftIcon = findViewById(R.id.backIcon);
        TextView text = findViewById(R.id.activityText);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddFriendActivity.class);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);
        intializeFields();
        ChangeSystemElements();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        sendUserID = fUser.getUid();


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

                MaintananceOfButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CheckIfAlreadyFriends();


        sendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequest.setEnabled(false);

                if(CURRENT_STATE.equals("not_friends")){
                    SendFriendRequest();
                }

                if(CURRENT_STATE.equals("request_sent")){
                    CancelFriendRequest();
                }


            }
        });


    }

    private void CheckIfAlreadyFriends() {
        FriendsRef.child(sendUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(receiverUserID)){
                    sendFriendRequest.setBackgroundResource(R.drawable.alreadyfriend_box);
                    ButtonMessage.setText("Already Friends");
                    sendFriendRequest.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                    if(task.isSuccessful()){
                                        sendFriendRequest.setEnabled(true);
                                        CURRENT_STATE = "not_friends";
                                        ButtonMessage.setText("Send Friend Request");
                                        sendFriendRequest.setBackgroundResource(R.drawable.green_box);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void MaintananceOfButton() {
        SenderFriendRequestRef.child(sendUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(receiverUserID)){
                    String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                    if(request_type.equals("sent")){
                        CURRENT_STATE="request_sent";
                        ButtonMessage.setText("Cancel Friend Request");
                        sendFriendRequest.setBackgroundResource(R.drawable.red_box);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendFriendRequest() {
        SenderFriendRequestRef.child(sendUserID).child(receiverUserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(receiverUserID).child(sendUserID).child("request_type")
                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                ButtonMessage.setText("Cancel Friend Request");
                                sendFriendRequest.setBackgroundResource(R.drawable.red_box);
                            }
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
        sendFriendRequest = findViewById(R.id.sendButton);
        CURRENT_STATE = "not_friends";
        receiverUserID = getIntent().getExtras().get("ID").toString();
        profile = findViewById(R.id.profilePicPerson);
        ButtonMessage = findViewById(R.id.message);
        ref= FirebaseDatabase.getInstance().getReference("User").child(receiverUserID);
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        SenderFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");



    }
}