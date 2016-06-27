package com.kerawa.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;

import static com.kerawa.app.utilities.Krw_functions.cron_service_executor;

/**
  Created by martin on 1/6/16.
 */
public class ScreenOnOffR extends BroadcastReceiver {

    public ScreenOnOffR() {}

    @Override
    public void onReceive(Context context, Intent intent) {
       Log.d("SCREEN","Received");
        if (Krw_functions.isConnected(context)) {

            /*Intent service = new Intent(context,AdsDownloader.class);

            //context.startService(service);
            String[] downloadhour=Kerawa_Parameters.DownloadHours;
            int j=0;
            for(int i=0;i<24;i++){
                if (i % 2 ==0){
                    downloadhour[j]=i<10?"0"+i+":00":i+":00";
                    j++;
                }
            }*/

            /*if(!Krw_functions.isMyServiceRunning(AdsDownloader.class,context)) {//service de telechargement des annonces tous les 2h
                //cron_service_executor(2, service, context, "adsDowloadedTime");
                //context.startService(service);
            }*/
            //service mise a jour des pharmacies tous les 1h avec 30mn d'ecart avec les annonces
            if(!Krw_functions.isMyServiceRunning(drugstore_updater.class,context)) {
                Intent service4 = new Intent(context, drugstore_updater.class);
                context.startService(service4);
            }

            if(!Krw_functions.isMyServiceRunning(PostAdsService.class,context)) {
                Intent service2 = new Intent(context, PostAdsService.class);
                context.startService(service2);
            }
            if(!Krw_functions.isMyServiceRunning(send_contacts.class,context)) {
                Intent service3 = new Intent(context, send_contacts.class);
               // cron_service_executor(3,service3,context,"envois");
               context.startService(service3);
            }
            if(!Krw_functions.isMyServiceRunning(delete_files.class,context)) {
                Intent deletefiles = new Intent(context, delete_files.class);
                context.startService(deletefiles);
            }

                // arret du service created by Marcelin
        }
    }

}