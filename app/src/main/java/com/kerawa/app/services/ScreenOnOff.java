package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenOnOff extends IntentService {

    private ScreenOnOffR mReceiver;

    public ScreenOnOff() {
        super("ScreenOnOff");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenOnOffR();
        registerReceiver(mReceiver, filter);

     //   stopSelf();     // arret du service created by Marcelin To resolve problem

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

       /* boolean screenOn = false;

        try{
            screenOn = intent.getBooleanExtra("screen_state", false);

        }catch(Exception ignored){}
        if (!screenOn) {
            Toast.makeText(getBaseContext(), "Screen on, ", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getBaseContext(), "Screen off,", Toast.LENGTH_SHORT).show();
        }*/
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //not sure if it works, just checking

    }



}
