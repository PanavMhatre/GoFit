package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ActivityProfile extends AppCompatActivity {


    private static final int REQUEST_CODE_IMAGE = 101;
    ImageView imageProfile;
    Button done,profilePic;

    StorageReference StorageRef;
    DatabaseReference userDataRef;

    Uri imageUri;
    boolean isImageAdded=false;


    FirebaseUser user;
    String uid;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        Log.d("TAG",uid);



        userDataRef = FirebaseDatabase.getInstance().getReference().child("User");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        EditText nameEdt = findViewById(R.id.name);
        EditText ageEdt = findViewById(R.id.age);
        EditText emailEdt = findViewById(R.id.email);
        EditText passwordEdt = findViewById(R.id.password);

        profilePic = findViewById(R.id.profilePic);
        done = findViewById(R.id.continueButton);

        imageProfile = findViewById(R.id.Profile);

        userDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.getChildren()){
                    if((child.getKey()).equals(uid)){
                        if(child.child("image").toString().equals("")) {
                            imageProfile.setImageResource(R.drawable.profile);
                        }else{
                            String imageUrl = child.child("image").getValue().toString();
                            Log.d("TAG",imageUrl);
                            if(!imageUrl.equals("")){
                                Picasso.get().load(imageUrl).into(imageProfile);
                            }
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_IMAGE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameEdt.getText().toString();
                final String age = ageEdt.getText().toString();
                final String email = emailEdt.getText().toString();
                final String password = passwordEdt.getText().toString();
                if(isImageAdded!=false){
                    uploadImage();
                }
                if(name.isEmpty()==false){
                    userDataRef.child(uid).child("name").setValue(name);
                }
                if(age.isEmpty()==false){
                    userDataRef.child(uid).child("age").setValue(age);
                }
                if(email.isEmpty()==false){
                    userDataRef.child(uid).child("email").setValue(email);
                    user.updateEmail(email);
                }
                if(password.length()>6){
                    userDataRef.child(uid).child("password").setValue(password);
                    user.updatePassword(password);
                }

                Intent intent = new Intent(ActivityProfile.this,SettingsActivity.class);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_IMAGE&&data!=null){
            imageUri=data.getData();
            isImageAdded = true;
            imageProfile.setImageURI(imageUri);

        }
    }

    private void uploadImage(){
        StorageRef.child(uid+"jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageRef.child(uid+"jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("image",uri.toString());

                        userDataRef.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","DATA UPLOADED");
                            }
                        });
                    }
                });
            }

        });
    }

}