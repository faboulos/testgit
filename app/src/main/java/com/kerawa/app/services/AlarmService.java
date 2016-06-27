package com.kerawa.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.kerawa.app.AdsListActivity;
import com.kerawa.app.Affiliation;
import com.kerawa.app.R;
import com.kerawa.app.helper.DatabaseHelper;

import java.util.Calendar;

/**
 Created by Boris on 9/24/15.
 */
public class AlarmService extends IntentService
{

    private static final int NOTIFICATION_ID = 1;


    public AlarmService() {
        super("AlarmService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        // models = generateCategoriesList();
        // String T = (String) v.getTag();
        Intent mIntent =  new Intent(this, AdsListActivity.class);

        DatabaseHelper mydb = new DatabaseHelper(this);
        String message = "";
        String catID = "";
        Calendar cal = Calendar.getInstance();
        int resource = R.drawable.icon ;
        String action_name = "Cliquer pour voir";
        int day = cal.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY){

            message = "Bon dimanche à tous de la part de Kerawa.com. Reposez-vous, profitez de la famille et des amis.";
            catID = "";
            action_name = "Ouvrir";
            mIntent = new Intent(this, AdsListActivity.class);
        }
        else if (day == Calendar.SATURDAY) {
            resource = R.drawable.save_money ;//win money here
            message = "Voulez-vous gagner de l'argent en installant l'application Kerawa.com chez vos proches ou vos contacts? Cliquez ci-dessous pour commencer";
            //catID = "18";//
            action_name = "Commencer";
            mIntent = new Intent(this, Affiliation.class);
        }

        else if (day  ==  Calendar.FRIDAY){
            resource = R.drawable.auto ;
            message = "Vous voulez acheter ou vendre un véhicule, une moto? Cliquez ci-dessous pour en savoir plus";
            catID = "18";
            action_name = "Voir les annonces dispo";
            mIntent = new Intent(this, AdsListActivity.class);

        }
        else if(day == Calendar.THURSDAY) {
            resource = R.drawable.hight_tech ;
            message = "Smartphones, TVs, PCs, Laptops, tablettes tactiles, etc. Le high-tech dans l'appli Androïd de Kerawa.com";
            catID = "124";
            action_name = "Achetez ici";
            mIntent = new Intent(this, AdsListActivity.class);

        }
        else if (day == Calendar.WEDNESDAY) {
            resource = R.drawable.save_money ;//win money here
            message = "Voulez-vous gagner de l'argent en installant l'application Kerawa.com chez vos proches ou vos contacts? Cliquez ci-dessous pour commencer";
            //catID = "18";//
            action_name = "commencer";
            mIntent = new Intent(this, Affiliation.class);
        }
        else if (day == Calendar.TUESDAY) {
            resource = R.drawable.immo ;
            message = "Vous cherchez à acheter ou louer un terrain, un studio, une maison? Meublé? Non meublé ?";
            catID = "12";
            action_name = "Visitez nos offres ici";
            mIntent = new Intent(this, AdsListActivity.class);
        }
        else if (day == Calendar.MONDAY) {
            resource = R.drawable.job ;
            message = "Bon début de semaine ! Elle commence bien? De nouvelles offres d'emploi pour vous ou des proches sur Kerawa.com";
            catID = "17";
            action_name = "Voir ou Postuler";

            mIntent = new Intent(this, AdsListActivity.class);
        }





        Bundle bundle = new Bundle();
        bundle.putString("categorie", catID);
        try {
            mIntent.putExtras(bundle);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(res, resource))
                .setAutoCancel(true)
                .setContentTitle("Kerawa.com")
                .setVibrate(new long[]{1000, 1000})
                .setSound(soundUri)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .addAction(R.drawable.click, action_name, pendingIntent);// #0


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        //  }

        stopSelf();    // arret du service created by Marcelin
    }

}