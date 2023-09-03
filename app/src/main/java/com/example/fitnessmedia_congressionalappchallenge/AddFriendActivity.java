package com.example.fitnessmedia_congressionalappchallenge;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<UserDataRecView> options;
    FirebaseRecyclerAdapter<UserDataRecView,RecViewHolder>adapter;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    RecyclerView recView;
    EditText search;
    ImageView searchIcon;
    RelativeLayout layout;
    FirebaseUser fUser;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        ChangeSystemElements();

        reference = FirebaseDatabase.getInstance().getReference().child("User");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        search = findViewById(R.id.userSearch);
        searchIcon = findViewById(R.id.searchIcon);
        layout = findViewById(R.id.searchLayout);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        loadUser("");

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = search.getText().toString();
                loadUser(s);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUser("");
            }
        });





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
        text.setText("Add Friends");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    private void loadUser(String s){
        Query query = reference.orderByChild("name").startAt(s).endAt(s + "\uf8ff");
        Log.d("TAG", String.valueOf(query));
//        .orderByChild("name").startAt(s).endAt(s+"\uf8ff")
        options=new FirebaseRecyclerOptions.Builder<UserDataRecView>().setQuery(query, UserDataRecView.class).build();
        adapter = new FirebaseRecyclerAdapter<UserDataRecView, RecViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull UserDataRecView model) {
                String friend_key = getRef(position).getKey();
                Log.d("TAG","" + friend_key);


                    if(!fUser.getUid().equals(getRef(position).getKey().toString())){
                        reference.child(friend_key).child("privateStatus").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String status = snapshot.getValue().toString();

                                if(status.equals("private")){
                                    holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(0,0));
                                }

                                Log.d("TAG",status);
                                if(status.equals("public")){
                                    if(holder.profile!=null){
                                        Picasso.get().load(model.getImage()).into(holder.profile);
                                        holder.profile.setImageResource(R.drawable.profile);
                                    }
                                    holder.name.setText(model.getName());


                                    holder.button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(AddFriendActivity.this,PersonProfileActivity.class);
                                            intent.putExtra("ID",getRef(position).getKey());
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




                        } else{
                            holder.itemView.setVisibility(View.GONE);
                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                        }






            }

            @NonNull
            @Override
            public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users,parent,false);
                return new RecViewHolder(view);
            }
        };

        adapter.startListening();
        recView.setAdapter(adapter);
    }

}