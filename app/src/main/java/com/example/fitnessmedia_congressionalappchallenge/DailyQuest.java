package com.example.fitnessmedia_congressionalappchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;

public class DailyQuest extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;

    TextView steps;
    int month, week;
    String dayOfWeek;

    CardView bike;

    ProgressBar progress;

    DatabaseReference stepsRef;
    String uid;

    boolean running = false;
    DatabaseReference login, stepsRefDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quest);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        bike = findViewById(R.id.bike);

        progress = findViewById(R.id.stepsProgressbar);
        ChangeSystemElements();



        Calendar calender = Calendar.getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int weekOfMonth = calender.WEEK_OF_MONTH;
        YearMonth yearMonthObject = YearMonth.of(year, month);
        Month monthWord = yearMonthObject.getMonth();
        Log.d("MONTH", String.valueOf(monthWord));
        int day = calender.get(Calendar.DAY_OF_MONTH);
        int dayNum = calender.get(Calendar.DAY_OF_WEEK);

        login = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Quest").child(String.valueOf(monthWord));
        login.child(String.valueOf(day)).child("Finished Quest").setValue(false);
        login.child(String.valueOf(day)).child("Day").setValue(day);

        stepsRefDB = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Steps").child(String.valueOf(monthWord)).child(String.valueOf(weekOfMonth)).child(String.valueOf(day));
        stepsRefDB.child("Steps").setValue(0);
        stepsRefDB.child("dayNum").setValue(dayNum);

        bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FitnessTimeBike.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        progress.setMax(10000);

        if(progress.getProgress() == 10000){
            login.child(String.valueOf(day)).child("Finished Quest").setValue(true);
            login.child(String.valueOf(day)).child("Day").setValue(day);
        }






        stepsRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Steps");




        steps = findViewById(R.id.txtSteps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        text.setText("Quests");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running){
            steps.setText(String.valueOf(event.values[0]));
            progress.setProgress((int) event.values[0]);
            stepsRefDB.setValue(event.values[0]);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}