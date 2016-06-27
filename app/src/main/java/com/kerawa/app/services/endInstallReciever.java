package com.kerawa.app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.kerawa.app.AdsListActivity;
import com.kerawa.app.R;
import com.kerawa.app.utilities.Krw_functions;

public class endInstallReciever extends BroadcastReceiver {

    private static final long NOTIFICATION_ID = System.currentTimeMillis();
    public endInstallReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Krw_functions.setAlarm(context);

        Intent mIntent = new Intent(context,AdsListActivity.class);
        //Bundle bundle = new Bundle();
        //bundle.putString("Kerawa", "Kerawa");
        //mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon))
                .setTicker("")
                .setAutoCancel(true)
                .setContentTitle("kerawa.com")
                .setVibrate(new long[]{1000,1000})
                .setSound(soundUri)
                .setContentText("Les bonnes affaires sont sur kerawa");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

       // Krw_functions.setAlarm(context);

    }
}
