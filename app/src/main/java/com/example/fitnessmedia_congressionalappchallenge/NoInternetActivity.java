package com.example.fitnessmedia_congressionalappchallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NoInternetActivity extends AppCompatActivity {

    Button checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        checkInternet = findViewById(R.id.checkButton);

        checkInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckConnection();
            }
        });

    }

    private void CheckConnection() {
        if(isConnected(this)){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Please Check Your Wifi Again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected(NoInternetActivity noInternetActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) noInternetActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected());
    }
}