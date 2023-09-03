package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PasswordLogin extends AppCompatActivity {

    TextView nameTxt;
    CircleImageView profile;
    DatabaseReference reference;
    EditText passwordEdt;
    FirebaseUser user;
    String uid;
    Button loginButton;
    TextInputLayout passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_login);
        nameTxt = findViewById(R.id.nameLogin);
        profile = findViewById(R.id.imageLogin);
        passwordLayout = findViewById(R.id.passwordLayout);
        loginButton = (Button) findViewById(R.id.loginButton);


        passwordEdt = findViewById(R.id.password);

        user = FirebaseAuth.getInstance().getCurrentUser();
        GenerateUid();

        reference = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                nameTxt.setText(name);
                if(snapshot.child("image").toString().equals("")) {
                    profile.setImageResource(R.drawable.profile);
                }else{
                    String imageUrl = snapshot.child("image").getValue().toString();
                    Log.d("TAG",imageUrl);
                    if(!imageUrl.equals("")){
                        Picasso.get().load(imageUrl).into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordTxt = passwordEdt.getText().toString();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String password = snapshot.child("password").getValue().toString();
                        if (passwordTxt.equals(password)){
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            passwordLayout.setError("Incorrect Password");
                            passwordLayout.setErrorEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });




    }

    private void GenerateUid() {
        uid = user.getUid();
    }
}