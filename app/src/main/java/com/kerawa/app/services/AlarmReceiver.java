package com.kerawa.app.services;

/**
 Created by root on 18/09/15.
 */
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "KERAWA-ALARM";
    Intent intent;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BroadcastReceiver has received alarm intent.");
        //Intent service1 = new Intent(context, AlarmService.class);
        //  context.startService(service1);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmRecieverFinal.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        Calendar alarmStartTime = new GregorianCalendar();
        Calendar cal = Calendar.getInstance();
        if((cal.get(Calendar.HOUR_OF_DAY)*3600+1800)>=45000) {
            alarmStartTime.set(Calendar.DAY_OF_WEEK, (cal.get(Calendar.DAY_OF_WEEK) != 7) ? cal.get(Calendar.DAY_OF_WEEK) + 1 : 1);
        }
        alarmStartTime.set(Calendar.HOUR_OF_DAY,12);
        alarmStartTime.set(Calendar.MINUTE,30);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC, alarmStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

    }

}