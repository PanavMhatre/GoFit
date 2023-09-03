package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatUserActivity extends AppCompatActivity {

    DatabaseReference reference, RootRef, messageRef, GroupMessageKeyRef, referenceGroup;
    FirebaseUser user;
    String messageSenderID;
    String groupReceiverID;
    TextInputLayout txtLayout;
    Uri fileUri;
    UploadTask uploadTask;
    String imageURl;

    RecyclerView userMessagesList;
    private final List<Group> groupMessageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupAdaptar messagesAdaptar;

    ImageButton sendMessage;
    EditText messageInputTxt;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        groupReceiverID = getIntent().getStringExtra("Group Visit ID");
        user = FirebaseAuth.getInstance().getCurrentUser();
        messageSenderID = user.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        txtLayout = findViewById(R.id.userMessageLayout);


        reference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(messageSenderID);

        referenceGroup = FirebaseDatabase.getInstance().getReference()
                .child("Group").child(messageSenderID).child(String.valueOf(groupReceiverID)).child("Information");

        ChangeSystemElements();

        sendMessage = findViewById(R.id.messageSend);
        messageInputTxt = findViewById(R.id.messageText);

        messagesAdaptar = new GroupAdaptar(groupMessageList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdaptar);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        txtLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Select Image"), 101);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

            String messageSenderRef = "GroupMessage/" + groupReceiverID + "/";

            DatabaseReference userMessageKeyRef = RootRef.child("GroupMessage").child(groupReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

            uploadTask = filePath.putFile(fileUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        imageURl = downloadUrl.toString();

                        Map messageTextBody = new HashMap();
                        messageTextBody.put("message", imageURl);
                        messageTextBody.put("name", fileUri.getLastPathSegment());
                        messageTextBody.put("type", "image");
                        messageTextBody.put("from", messageSenderID);
                        messageTextBody.put("messageID", messagePushID);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);

                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Log.d("TAG", " Complete Image Upload");
                                messageInputTxt.setText("");
                            }

                        });


                    }
                }
            });

        }
    }

    private void SendMessage() {
        String messagetext = messageInputTxt.getText().toString();

        if (messagetext.isEmpty()) {

        } else {
            String messageSenderRef = "GroupMessage/" + groupReceiverID + "/" ;

            DatabaseReference userMessageKeyRef = RootRef.child("GroupMessage").child(groupReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messagetext);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Log.d("TAG", " Complete Message Upload");
                    messageInputTxt.setText("");
                }

            });

        }
    }

    private void ChangeSystemElements() {
        ImageView leftIcon = findViewById(R.id.backIcon);
        TextView text = findViewById(R.id.activityText);
        CircleImageView profile = findViewById(R.id.profileChat);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                startActivity(intent);
                finish();
            }
        });


        referenceGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                if (image.equals("")) {
                    profile.setImageResource(R.drawable.profile);
                } else {
                    Picasso.get().load(image).into(profile);
                }

                text.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        groupMessageList.clear();

        RootRef.child("GroupMessage").child(groupReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Group group = snapshot.getValue(Group.class);
                groupMessageList.add(group);
                messagesAdaptar.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}