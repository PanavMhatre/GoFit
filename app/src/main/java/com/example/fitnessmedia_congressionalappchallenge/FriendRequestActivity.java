package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FriendRequestActivity extends AppCompatActivity {


    RecyclerView request_list;

    FirebaseUser user;
    FirebaseAuth.AuthStateListener authListner;
    FirebaseDatabase database;
    DatabaseReference reference,mFriend_Ref,userRef,mRecieverReference;

    String my_id,image,name,email,date;

    TextView noRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        ChangeSystemElements();

        user = FirebaseAuth.getInstance().getCurrentUser();
        InstantiateUserID();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("FriendRequests").child(my_id);
        mFriend_Ref = database.getReference().child("FriendRequests");
        userRef  =database.getReference().child("User");
        mRecieverReference = database.getReference();

        request_list = findViewById(R.id.recViewFriendRequest);
        request_list.setLayoutManager(new LinearLayoutManager(this));
//        noRequests = findViewById(R.id.noRequests);

        Request_Friend_List();

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    noRequests.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



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
        text.setText("Friend Request");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    private void InstantiateUserID() {
        my_id = user.getUid();
    }

    private void Request_Friend_List() {


        FirebaseRecyclerOptions<UserDataRecView> options = new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(reference, UserDataRecView.class).build();
        FirebaseRecyclerAdapter<UserDataRecView,RecViewHolder> adapter = new FirebaseRecyclerAdapter<UserDataRecView, RecViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull RecViewHolder holder, int position, @NonNull UserDataRecView model) {
                String friend_key = getRef(position).getKey();
                Log.d("TAG","" + friend_key);

                mRecieverReference.child("FriendRequests").child(my_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(friend_key)){
                            String request = snapshot.child(friend_key).child("request_type").getValue().toString();


                            if(request.equals("received")){
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        name = snapshot.child(friend_key).child("name").getValue().toString();
                                        email = snapshot.child(friend_key).child("email").getValue().toString();
                                        image = snapshot.child(friend_key).child("image").getValue().toString();

                                        holder.name.setText(name);


                                        holder.button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(FriendRequestActivity.this, FriendRequestPersonProfileActivity.class);
                                                intent.putExtra("ID_Friend_Request", friend_key);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                            }

                                        });



                                    };

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                request_list.setVisibility(View.INVISIBLE);
//                                noRequests.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users,parent,false);
                return new RecViewHolder(view);
            }
        };

        adapter.startListening();
        request_list.setAdapter(adapter);

    }


}