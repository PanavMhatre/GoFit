package com.example.fitnessmedia_congressionalappchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class StatsActivity extends AppCompatActivity {

    XYPlot weeklyPlot, dailyPlot, monthlyPlot;
    DatabaseReference stepsRefWeekly, stepsRefMonthly;
    String uid;
    String monday;

    List<Integer> weekly = new ArrayList<Integer>();


    int[] monthly = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    Number[] weeklyNum = {0, 0, 0, 0, 0, 0, 0};
    Number[] monthlyNum = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ChangeSystemElements();

        weeklyPlot = findViewById(R.id.plotWeekly);
        dailyPlot = findViewById(R.id.plotMontly);
        monthlyPlot = findViewById(R.id.plotMontly);

        for (int i =0; i<monthlyNum.length;i++){
            Random rand = new Random();
            int x = rand. nextInt(41);
            monthlyNum[i] = x;
        }


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for(int i = 0; i<7;i++){
            weekly.add(0);
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int weekOfMonth = c.WEEK_OF_MONTH;
        int monthOfWay = c.DAY_OF_MONTH;
        YearMonth yearMonthObject = YearMonth.of(year, month);;
        Month monthWord = yearMonthObject.getMonth();
        int day = c.get(Calendar.DAY_OF_MONTH);

        Log.d("MONTH", String.valueOf(monthWord));

        weekly.set(5, 10);
        weekly.set(3, 5);
        weekly.set(6, 13);
        weekly.set(1, 3);
        weekly.set(0, 18);
        weekly.set(2,8);
        weekly.set(4,5);


        stepsRefMonthly =  FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Steps").child(String.valueOf(monthWord));
        stepsRefWeekly = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Steps").child(String.valueOf(monthWord)).child(String.valueOf(weekOfMonth));
        for(int i =0; i<=3;i++){
              List<String> steps = new ArrayList<String>();
//            final String[] dayNum = {"0"};
//            stepsRefWeekly.child(String.valueOf(i)).child("dayNum").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                    if(task.isSuccessful()){
//                        dayNum[0] = String.valueOf(task.getResult().getValue());
//                    }
//                }
//            });
//
            stepsRefWeekly.child(String.valueOf(i)).child("Steps").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().getValue() == null){
                            weekly.set(1,0);
                        }else{
                            steps.add(String.valueOf(task.getResult().getValue()));
                            Log.d("STEPS", steps.get(0));
                            weekly.set(1,Integer.valueOf(steps.get(0)));
                        }

                    }
                }
            });


            steps.clear();

        }



        Log.d("WEEK", Arrays.toString(weekly.toArray()));


        stepsRefMonthly.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    for(DataSnapshot snapshot2: snapshot1.getChildren()){
                        monthly[monthOfWay-1]  = snapshot2.child("Steps").getValue(Integer.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        for(int i = 0;i<7;i++){
            weeklyNum[i] = weekly.get(i);
        }

        weeklyPlot.getGraph().setMarginRight(40);
        weeklyPlot.setDomainLabel("Days");



        XYSeries series1 = new SimpleXYSeries(Arrays.asList(weeklyNum),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series One");


        MyBarFormatter formatter1 = new MyBarFormatter(Color.RED, Color.LTGRAY);


        weeklyPlot.addSeries(series1,formatter1);
        weeklyPlot.getGraph().setMarginBottom(0);
        weeklyPlot.getGraph().setPaddingBottom(0);
        weeklyPlot.getGraph().getDomainGridLinePaint().setColor(getColor(R.color.white));
        weeklyPlot.getGraph().getRangeGridLinePaint().setColor(getColor(R.color.white));

        weeklyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL,10);
        weeklyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL,10);
        weeklyPlot.setRangeBoundaries(0,40, BoundaryMode.FIXED);
        weeklyPlot.setDomainBoundaries(-.4,6.6, BoundaryMode.FIXED);
        weeklyPlot.getLegend().setVisible(false);

        MyBarRender render = (MyBarRender) weeklyPlot.getRenderer(MyBarRender.class);
        render.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
        render.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH,150);
        render.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP,20);

        monthlyPlot.getGraph().setMarginRight(40);
        monthlyPlot.setDomainLabel("Days");

        XYSeries series2 = new SimpleXYSeries(Arrays.asList(monthlyNum),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series One");


        MyBarFormatter formatter2 = new MyBarFormatter(Color.RED, Color.LTGRAY);


        monthlyPlot.addSeries(series2,formatter2);
        monthlyPlot.getGraph().setMarginBottom(0);
        monthlyPlot.getGraph().setPaddingBottom(0);
        monthlyPlot.getGraph().getDomainGridLinePaint().setColor(getColor(R.color.white));
        monthlyPlot.getGraph().getRangeGridLinePaint().setColor(getColor(R.color.white));

        monthlyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL,10);
        monthlyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL,10);
        monthlyPlot.setRangeBoundaries(0,40, BoundaryMode.FIXED);
        monthlyPlot.setDomainBoundaries(-.4,30.6, BoundaryMode.FIXED);
        monthlyPlot.getLegend().setVisible(false);

        MyBarRender render2 = (MyBarRender) weeklyPlot.getRenderer(MyBarRender.class);
        render2.setBarOrientation(BarRenderer.BarOrientation.SIDE_BY_SIDE);
        render2.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH,150);
        render2.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP,0);
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
                finish();
            }
        });
        text.setText("Stats");
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.log_blue));
        }

    }
}

class MyBarFormatter extends BarFormatter{

    public MyBarFormatter(int fillColor, int borderColor) {
        super(fillColor, borderColor);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return MyBarRender.class;
    }

    @Override
    public SeriesRenderer doGetRendererInstance(XYPlot plot) {
        return new MyBarRender(plot);
    }
}

class MyBarRender extends BarRenderer<MyBarFormatter> {
    public MyBarRender(XYPlot plot) {
        super(plot);
    }
}