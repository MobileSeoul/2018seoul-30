package com.js980112.hotweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public final int MAINACTIVITY_CODE = 1001;

    private static String DB_PATH = "/data/data/com.js980112.hotweather/databases/";
    private static String DB_NAME ="map.db"; // 데이터베이스 이름

    LinearLayout mainlayout,llMain;

    TextView tvTemp, tvArea, tvText, tvDust1, tvDust2, tvWeather,tvLoading;

    ImageView ivTemp, btnSetting, ivWeather;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    WeatherAPI weather;

    BackPressCloseHandler back;

    DBHelper dbHelper;

    NotificationSet notificationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        //editor=setting.edit();

        mainlayout = findViewById(R.id.mainlayout);

        back = new BackPressCloseHandler(this, "종료");

        dbHelper=new DBHelper(this);

        tvTemp = findViewById(R.id.tvTemp);
        tvArea = findViewById(R.id.tvArea);
        tvText = findViewById(R.id.tvText);
        tvDust1 = findViewById(R.id.tvDust1);
        tvDust2 = findViewById(R.id.tvDust2);
        tvWeather = findViewById(R.id.tvWeather);

        tvLoading=findViewById(R.id.tvLoading);
        llMain=findViewById(R.id.llMain);

        ivTemp = findViewById(R.id.ivTemp);
        ivWeather = findViewById(R.id.ivWeather);
        btnSetting = findViewById(R.id.btnSetting);

        notificationSet=new NotificationSet(this);

        setSetting();

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(it, MAINACTIVITY_CODE);
            }
        });

        if(setting.getBoolean("widget",true))
            notificationSet.notificationSomethings();
        else
            notificationSet.cancel();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //setSetting();
        //Toast.makeText(getApplicationContext(),"RESUME",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setSetting();
        if(setting.getBoolean("widget",true))
            notificationSet.notificationSomethings();
        else
            notificationSet.cancel();
        //Toast.makeText(getApplicationContext(), "RESULT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        back.onBackPressed();
    }

    private void setSetting() {
        //area1:시/도 area2:구/군
        String top = setting.getString("area1", "지역을") + " " + setting.getString("area2", "설정하세요");
        tvArea.setText(top);
        weather = new WeatherAPI(setting.getString("area1", "서울특별시"), setting.getString("area2", "강남구"),this,dbHelper);

        if(setting.getBoolean("widget",true))
            notificationSet.notificationSomethings();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvLoading.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
                try {
                    String[] dusts = weather.getDust();
                    tvDust1.setText(dusts[0]);
                    tvDust2.setText(dusts[1]);
                    setTextColor(dusts[0],tvDust1);
                    setTextColor(dusts[1],tvDust2);
                    tvTemp.setText(weather.getTemperature() + "°C");
                    try {
                        editor=setting.edit();
                        editor.putString("temp",weather.getTemperature() + "°C");
                        editor.putString("dust",dusts[0]+"/"+dusts[1]);
                        editor.putString("weather",weather.getWeather());
                        editor.apply();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //ivWeather
//            - 하늘상태(SKY) 코드 : 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
//            - 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3)
                    switch (weather.getWeather()) {
                        case "맑음":
                            ivWeather.setImageResource(R.drawable.sun);
                            break;
                        case "구름조금":
                            ivWeather.setImageResource(R.drawable.fewcloud);
                            break;
                        case "구름많음":
                            ivWeather.setImageResource(R.drawable.cloud);
                            break;
                        case "흐림":
                            ivWeather.setImageResource(R.drawable.cloud);
                            break;
                        case "비":
                            ivWeather.setImageResource(R.drawable.rain);
                            break;
                        case "눈":
                            ivWeather.setImageResource(R.drawable.snow);
                            break;
                        default:
                            ivWeather.setImageResource(R.drawable.sun);
                    }
                    tvWeather.setText(weather.getWeather());
                    //Toast.makeText(getApplicationContext(), weather.getDust() + " " + weather.getWeather() + " " + weather.getTemperature(), Toast.LENGTH_SHORT).show();
                    //tvText,ivTemp
                    //폭염,더움,적정,쌀쌀,추움,한파
                    //tempPok,tempDu,tempJuk,tempSal,tempChu,tempHan
                    if (weather.getTemperature()>setting.getInt("tempPok",34)) {
                        ivTemp.setImageResource(R.drawable.thermometer3);
                        tvText.setText(setting.getString("strPok","덥다더워"));
                    }else if (weather.getTemperature()>setting.getInt("tempDu",30)&&weather.getTemperature()>setting.getInt("tempJuk",25)) {
                        ivTemp.setImageResource(R.drawable.thermometer2);
                        tvText.setText(setting.getString("strDu","조급덥다"));
                    }else if (weather.getTemperature()>setting.getInt("tempSal",10)) {
                        ivTemp.setImageResource(R.drawable.thermometer1);
                        tvText.setText(setting.getString("strJuk","살만하다"));
                    }else if(weather.getTemperature()>setting.getInt("tempChu",0)){
                        ivTemp.setImageResource(R.drawable.thermometer4);
                        tvText.setText(setting.getString("strSal","쌀쌀하다"));
                    }else if (weather.getTemperature()>setting.getInt("tempHan",-12)) {
                        ivTemp.setImageResource(R.drawable.thermometer5);
                        tvText.setText(setting.getString("strSal","춥다"));
                    }else {
                        ivTemp.setImageResource(R.drawable.thermometer6);
                        tvText.setText(setting.getString("strHan","얼어죽겠네"));
                    }
                    setBackground(weather.getTemperature());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                }
            }
        }, 500);
    }

    public void setBackground(int temperature) {
        if (temperature > setting.getInt("tempPok",34))
            mainlayout.setBackgroundResource(R.drawable.gradient1);
        else if (temperature > setting.getInt("tempChu",0))
            mainlayout.setBackgroundResource(R.drawable.gradient2);
        else
            mainlayout.setBackgroundResource(R.drawable.gradient3);
    }

    public void setTextColor(String dust,TextView tv){
        tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        switch (dust){
            case "좋음":
                tv.setTextColor(Color.parseColor("#3AA18F"));
                break;
            case "보통":
                tv.setTextColor(Color.parseColor("#56D9CD"));
                break;
            case "나쁨":
                tv.setTextColor(Color.parseColor("#F5A503"));
                break;
            case "매우 나쁨":
                tv.setTextColor(Color.parseColor("#F2385A"));
                break;
        }
    }
}

