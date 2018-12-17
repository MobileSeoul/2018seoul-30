package com.js980112.hotweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import static android.content.Context.MODE_PRIVATE;

public class NotificationSet {

    SharedPreferences setting;
    SharedPreferences.Editor editor;


    public RemoteViews contentView;
    NotificationManager nm;

    Context context;
    NotificationSet(Context context){
        this.context=context;
        setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notificationSomethings() {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        //notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        contentView = new RemoteViews(context.getPackageName(), R.layout.notify);
        contentView.setTextViewText(R.id.tvArea_no, setting.getString("area1", "지역을") + " " + setting.getString("area2", "설정하세요"));
        contentView.setTextViewText(R.id.tvTemp_no, setting.getString("temp","0°C"));
        contentView.setTextViewText(R.id.tvDust, setting.getString("dust","지역/설정"));
        switch (setting.getString("weather","")) {
            case "맑음":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.sun);
                break;
            case "구름조금":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.fewcloud);
                break;
            case "구름많음":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.cloud);
                break;
            case "흐림":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.cloud);
                break;
            case "비":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.rain);
                break;
            case "눈":
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.snow);
                break;
            default:
                contentView.setImageViewResource(R.id.ivWeather_no,R.drawable.sun);
        }
        setBackground(Integer.parseInt(setting.getString("temp","0").replace("°C","")));
        //contentView.setOnClickPendingIntent(R.id.notifyReflash, pintent1);

        builder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon2)
                .setContent(contentView)
                .setContentIntent(contentIntent)
                .setOngoing(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        nm.notify(1234, builder.build());
    }

    public void cancel(){
        nm.cancel(1234);
    }
    public void setBackground(int temperature) {
        if (temperature > setting.getInt("tempPok",34))
            contentView.setInt(R.id.notiBack, "setBackgroundResource",R.drawable.gradient1 );
        else if (temperature > setting.getInt("tempChu",0))
            contentView.setInt(R.id.notiBack, "setBackgroundResource",R.drawable.gradient2 );
        else
            contentView.setInt(R.id.notiBack, "setBackgroundResource",R.drawable.gradient3 );
    }
}
