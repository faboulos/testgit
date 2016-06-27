package com.kerawa.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kerawa.app.utilities.Krw_functions;


public class ConnectionReciever extends BroadcastReceiver {
    public ConnectionReciever() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent onoff = new Intent(context,ScreenOnOff.class);
        if(!Krw_functions.isMyServiceRunning(ScreenOnOffR.class, context)) {
            context.startService(onoff);

        }
    }
}
