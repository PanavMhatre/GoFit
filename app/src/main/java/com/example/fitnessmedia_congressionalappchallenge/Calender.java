package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
//
//import com.github.sundeepk.compactcalendarview.CompactCalendarView;
//import com.github.sundeepk.compactcalendarview.domain.Event;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Locale;

public class Calender extends AppCompatActivity {
//
//    CompactCalendarView calender;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());

    RelativeLayout day1,day2,day3,day4,day5,day6,day7,day8,day9,day10,day11,day12,day13,day14,day15,day16,day17,day18,day19,day20,day21,
            day22,day23,day24,day25,day26,day27,day28,day29,day30,day31,day32,day33,day34,day35;

    RelativeLayout day36,day37,day38;

    int incrementAdder;
    DatabaseReference login, lastMonth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        ChangeSystemElements();

        intilizeViews();
        LocalDate now = LocalDate.now();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calender = Calendar.getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);


        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth(); //28
        YearMonth backThen = yearMonthObject.minusMonths(1);
        Month monthWord = yearMonthObject.getMonth();

        lastMonth = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Quest").child(String.valueOf(backThen));



        if(backThen == yearMonthObject){
            lastMonth.removeValue();
        }


        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        String dayofweek = getWeekDayName(firstDay.toString());

        Log.d("TAG",String.valueOf(dayofweek));

        day36.setVisibility(View.INVISIBLE);
        day37.setVisibility(View.INVISIBLE);
        day38.setVisibility(View.INVISIBLE);



        switch (dayofweek){
            case "Monday":
                incrementAdder = 0;
                if(daysInMonth==31){
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else if (daysInMonth == 28){
                    day29.setVisibility(View.INVISIBLE);
                    day30.setVisibility(View.INVISIBLE);
                    day31.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day31.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);

                }
                break;
            case "Tuesday":
                incrementAdder = 1;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day30.setVisibility(View.INVISIBLE);
                    day31.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }
                break;
            case "Wednesday":
                incrementAdder = 2;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day31.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }
                break;
            case "Thursday":
                incrementAdder = 3;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day32.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }
                break;
            case "Friday":
                incrementAdder = 4;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day36.setVisibility(View.VISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day33.setVisibility(View.INVISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.VISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }
                break;
            case "Saturday":
                incrementAdder = 5;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day5.setVisibility(View.INVISIBLE);
                    day6.setVisibility(View.VISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day5.setVisibility(View.VISIBLE);
                    day6.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day5.setVisibility(View.INVISIBLE);
                    day6.setVisibility(View.INVISIBLE);
                }
                break;
            case "Sunday":
                incrementAdder = 6;
                if(daysInMonth==31){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day5.setVisibility(View.INVISIBLE);
                    day38.setVisibility(View.VISIBLE);
                }else if (daysInMonth == 28){
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.INVISIBLE);
                    day5.setVisibility(View.VISIBLE);
                    day34.setVisibility(View.INVISIBLE);
                    day35.setVisibility(View.INVISIBLE);
                }else{
                    day1.setVisibility(View.INVISIBLE);
                    day2.setVisibility(View.INVISIBLE);
                    day3.setVisibility(View.INVISIBLE);
                    day4.setVisibility(View.VISIBLE);
                    day5.setVisibility(View.VISIBLE);
                }
                break;
        }

        int day = calender.get(Calendar.DAY_OF_MONTH);


        String todayString = year + "/" + month + "/" + day;

        login = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Quest").child(String.valueOf(monthWord));

        login.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("Finished Quest").getValue(Boolean.class) == true){
                        String day = "day" + (dataSnapshot.child("Day").getValue(Integer.class) + incrementAdder);
                        Log.d("TAG",day);

                        switch (day){
                            case "day1":
                                day1.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day2":
                                day2.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day3":
                                day3.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day4":
                                day4.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day5":
                                day5.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day6":
                                day6.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day7":
                                day7.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day8":
                                day8.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day10":
                                day10.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day11":
                                day11.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day12":
                                day12.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day13":
                                day13.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day14":
                                day14.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day15":
                                day15.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day16":
                                day16.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day17":
                                day17.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day18":
                                day18.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day19":
                                day19.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day20":
                                day20.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day21":
                                day21.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day22":
                                day22.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day23":
                                day23.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day24":
                                day24.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day25":
                                day25.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day26":
                                day26.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day27":
                                day27.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day28":
                                day28.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day29":
                                day29.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day30":
                                day30.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day31":
                                day31.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day32":
                                day32.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day33":
                                day33.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day34":
                                day34.setBackground(getDrawable(R.drawable.background_present));
                                break;
                            case "day35":
                                day35.setBackground(getDrawable(R.drawable.background_present));
                                break;

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static String getWeekDayName(String s) {
        DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("u-M-d", Locale.ENGLISH);
        DateTimeFormatter dtfOutput = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
        return LocalDate.parse(s, dtfInput).format(dtfOutput);
    }

    private void intilizeViews() {
        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        day7 = findViewById(R.id.day7);
        day8 = findViewById(R.id.day8);
        day9 = findViewById(R.id.day9);
        day10 = findViewById(R.id.day10);
        day11 = findViewById(R.id.day11);
        day12 = findViewById(R.id.day12);
        day13 = findViewById(R.id.day13);
        day14 = findViewById(R.id.day14);
        day15 = findViewById(R.id.day15);
        day16 = findViewById(R.id.day16);
        day17 = findViewById(R.id.day17);
        day18 = findViewById(R.id.day18);
        day19 = findViewById(R.id.day19);
        day20 = findViewById(R.id.day20);
        day21 = findViewById(R.id.day21);
        day22 = findViewById(R.id.day22);
        day23 = findViewById(R.id.day23);
        day24 = findViewById(R.id.day24);
        day25 = findViewById(R.id.day25);
        day26 = findViewById(R.id.day26);
        day27 = findViewById(R.id.day27);
        day28 = findViewById(R.id.day28);
        day29 = findViewById(R.id.day29);
        day30 = findViewById(R.id.day30);
        day31 = findViewById(R.id.day31);
        day32 = findViewById(R.id.day32);
        day33 = findViewById(R.id.day33);
        day34 = findViewById(R.id.day34);
        day35 = findViewById(R.id.day35);
        day36 = findViewById(R.id.day36);
        day37 = findViewById(R.id.day37);
        day38 = findViewById(R.id.day38);
    }

    private void ChangeSystemElements() {
        ImageView leftIcon = findViewById(R.id.backIcon);
        TextView text = findViewById(R.id.activityText);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        text.setText("Calender");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }
}