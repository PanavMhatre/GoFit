package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    DatabaseReference referencePassword;
    FirebaseUser user;
    String uid;
    Animation bottomAnim;
    ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(!isConnected(this)){
            Intent intent = new Intent(this, NoInternetActivity.class);
            startActivity(intent);
            finish();
        }



        user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();
            referencePassword = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    referencePassword.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean status = (Boolean) snapshot.child("loginByPassword").getValue();

                            if(status==null){
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                Log.d("TAG","Reached to Main Activity");
                                startActivity(intent);
                                finish();
                            }

                            Log.d("TAG","Print status" + status);
                            if (status){
                                Intent intent = new Intent(SplashScreen.this,PasswordLogin.class);
                                Log.d("TAG","Reached to Password");
                                startActivity(intent);
                                finish();
                            }else if (!status){
                                if((Boolean) snapshot.child("alreadyAUser").getValue()){
                                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                    Log.d("TAG","Reached to Home Activity");
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    Log.d("TAG","Reached to Main Activity");
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            },4000);
        }catch(NullPointerException e){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            Log.d("TAG","Reached to Main Activity");
            startActivity(intent);
            finish();
        }

        if(uid!=null){
            referencePassword = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        }
        logo = findViewById(R.id.logoImage);


        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        logo.setAnimation(bottomAnim);







    }

    private boolean isConnected(SplashScreen splashScreen) {
        ConnectivityManager connectivityManager = (ConnectivityManager) splashScreen.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiCon!=null&&wifiCon.isConnected()) || (mobileCon!=null&&mobileCon.isConnected())){
            return true;
        }else{
            return false;
        }
    }

}