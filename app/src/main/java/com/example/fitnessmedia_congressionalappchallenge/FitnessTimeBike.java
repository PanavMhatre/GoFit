package com.example.fitnessmedia_congressionalappchallenge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.protobuf.StringValue;

public class FitnessTimeBike extends AppCompatActivity {

    Boolean playTimer = true;
    Handler handler = new Handler();
    int time;
    final int[] currentProgress = {0};
    final int[] minutesPassed = {0};
    ProgressBar pb;
    TextView minutes;

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
                AlertDialog alertDialog = new AlertDialog.Builder(FitnessTimeBike.this).create();
                alertDialog.setTitle("End Workout");
                alertDialog.setMessage("Do you wish to end your workout? You will not be awarded any XP");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(),DailyQuest.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        text.setText("Biking");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_time);
        ChangeSystemElements();

        pb = findViewById(R.id.progressBarBike);
        minutes = findViewById(R.id.progress_bike);


        ImageView pause = (ImageView) findViewById(R.id.pause);
        ImageView play = (ImageView) findViewById(R.id.play);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                pause.setClickable(false);
                play.setVisibility(View.VISIBLE);
                play.setClickable(true);
                handler.removeCallbacks(timerRunnable);
                Log.d("TAG","STOPPED TIMER");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                play.setClickable(false);
                pause.setVisibility(View.VISIBLE);
                pause.setClickable(true);
                timerRunnable.run();
            }
        });

        // for eg: if countdown is to go for 30 seconds
        pb.setMax(1800);

        // the progress in our progressbar decreases with the decrement
        // in the remaining time for countdown to be over
        pb.setProgress(0);

        // keep track of current progress

        timerRunnable.run();



    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentProgress[0] += 1;
            pb.setProgress(currentProgress[0]);

            if(currentProgress[0]%60==0){
                minutesPassed[0] += 1;
                minutes.setText(String.valueOf(minutesPassed[0]));
            }
            if(currentProgress[0] != 1800){
                handler.postDelayed(this, 1000);
            }else{
                Intent intent = new Intent(getApplicationContext(),GetXpActivity.class);
                startActivity(intent);
            }

        }
    };
}