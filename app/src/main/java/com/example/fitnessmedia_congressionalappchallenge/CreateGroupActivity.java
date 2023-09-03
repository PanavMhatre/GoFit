package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;
    EditText edttxt;
    Button button;
    DatabaseReference groups;

    CircleImageView change;
    StorageReference StorageRef;
    String imageURL = "";

    Uri imageUri;
    boolean isImageAdded=false;

    String name, push_key;

    CircleImageView groupImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        edttxt = findViewById(R.id. txtGroupName);
        groupImage = findViewById(R.id.circleImageView);
        ChangeSystemElements();

        button = findViewById(R.id.continueButtonGroup);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),GroupActivity.class));
            }
        });

        groups = FirebaseDatabase.getInstance().getReference().child("Group");
        push_key = groups.push().getKey();

        StorageRef = FirebaseStorage.getInstance().getReference().child("GroupImages");

        change = findViewById(R.id.change);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE_IMAGE);

            }
        });

        edttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edttxt.setText("");
            }
        });


            Button button = findViewById(R.id.continueButtonGroup);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if(edttxt.getHint().toString().equals("Group Name")){
//                                Log.d("TAG","Reached to Set Name to Group Name");
//                                Intent intent = new Intent(getApplicationContext(),AddFriendGroupChat.class);
//                                if(isImageAdded){
//                                    uploadImage();
//                                }
//                                intent.putExtra("Group_IMAGE",imageURL);
//
//                                intent.putExtra("Group_Push_ID_Key",push_key);
//                                intent.putExtra("Group_NAME","Group Name");
//
//                                startActivity(intent);
//
//                    }else{
                        if(edttxt.getText().toString().isEmpty()) {
                            edttxt.setError("Name Cannot Be Empty");
                        }
//                        }else{
                                name = edttxt.getText().toString();
                                Intent intent = new Intent(getApplicationContext(),AddFriendGroupChat.class);
                                if(isImageAdded){
                                    uploadImage();
                                    intent.putExtra("Group_IMAGE",imageURL);
                                }
                                intent.putExtra("Group_IMAGE",imageURL);
                                intent.putExtra("Group_Push_ID_Key",push_key);
                                intent.putExtra("Group_NAME",name);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

//                        }



                        }
//                    }


            });




    }

    private void ChangeSystemElements() {
        ImageView leftIcon = findViewById(R.id.backIcon);
        TextView text = findViewById(R.id.activityText);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
                startActivity(intent);
            }
        });
        text.setText("New Group");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_IMAGE&&data!=null){
            imageUri=data.getData();
            isImageAdded = true;
            Log.d("TAG","IMAGE URI ADDED");
            groupImage.setImageURI(imageUri);

        }
    }

    private void uploadImage(){
        Log.d("TAG","ABOUT TO UPLOAD TO STORAGE");
        StorageRef.child(push_key+"jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("TAG","DATA URI ABOUT TO GET");
                StorageRef.child(push_key+"jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        Log.d("TAG",imageURL);
                    }
                });
            }

        });
    }
}
