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
import android.util.Log;

import com.kerawa.app.AdsListActivity;
import com.kerawa.app.R;

public class AlarmRecieverFinal extends BroadcastReceiver {
    public AlarmRecieverFinal() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("TAG","alarm fired");

        Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);

       /* Intent mIntent = new Intent(context, AdsListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Kerawa", "Kerawa");
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icon))
                .setTicker("")
                .setAutoCancel(true)
                .setContentTitle("KERAWA")
                .setVibrate(new long[]{1000,1000})
                .setSound(soundUri)
                .setContentText("Passez une bonne journée avec kerawa");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());*/
    }
}
