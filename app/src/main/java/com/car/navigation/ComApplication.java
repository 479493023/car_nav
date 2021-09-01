package com.car.navigation;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.car.navigation.service.LocationService;

public class ComApplication extends Application {

    public static Context instance;

    public static boolean isConnectionTcp;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
        startAlarm();
    }

    public void startAlarm() {
        //首先获得系统服务
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //设置闹钟的意图，我这里是去调用一个服务，该服务功能就是获取位置并且上传
        Intent intent = new Intent(this, LocationService.class);
        PendingIntent pendSender = PendingIntent.getService(this, 0, intent, 0);
        am.cancel(pendSender);
        //AlarmManager.RTC_WAKEUP ;这个参数表示系统会唤醒进程；设置的间隔时间是1分钟
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, pendSender);
    }
}
