package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DatabaseReference reference;

    EditText nameEdt,ageEdt,emailEdt,passwordEdt;

    FirebaseAuth auth;

    String nameTxt,ageTxt,emailTxt,passwordTxt;

    String userId;

    FirebaseUser fUser;

    ImageView image;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button button = findViewById(R.id.continueButton);
        nameEdt = findViewById(R.id.name);
        ageEdt = findViewById(R.id.age);
        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();











        reference = FirebaseDatabase.getInstance().getReference().child("User");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTxt = nameEdt.getText().toString();
                ageTxt = ageEdt.getText().toString();
                emailTxt = emailEdt.getText().toString();
                passwordTxt = passwordEdt.getText().toString();
                if(!nameTxt.isEmpty()&&!ageTxt.isEmpty()&&!emailTxt.isEmpty()&&passwordTxt.length()>6){
                    auth.createUserWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            fUser = FirebaseAuth.getInstance().getCurrentUser();
                            insertData();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
                }

            }
        });




    }

    String imageUrl ="";
    String privateStatus = "public";
    Boolean loginByPassword = false;
    Boolean finishedDailyQuest = false;


    private void insertData() {

        userId = fUser.getUid();

        User user = new User(nameTxt,ageTxt,emailTxt,passwordTxt,imageUrl,privateStatus,loginByPassword);

        reference.child(userId).setValue(user);
        reference.child(userId).child("alreadyAUser").setValue(true);
        reference.child(userId).child("finishedDailyQuest").setValue(finishedDailyQuest);
        reference.child(userId).child("coins").setValue(0);

        Toast.makeText(this, "Welcome to Fitness Media", Toast.LENGTH_SHORT).show();
    }


}

