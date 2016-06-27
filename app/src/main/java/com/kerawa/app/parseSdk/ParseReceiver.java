package com.kerawa.app.parseSdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kerawa.app.services.ChatHeadService;
import com.parse.Parse;
import com.parse.ParseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseReceiver extends BroadcastReceiver {
    private ImageView chatHead;
    private WindowManager windowManager;

    public ParseReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Parse.setLogLevel(2);
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
           // Log.d("Parse Notification", "got action " + json.getString("header") + " on channel " + channel + " with:");
            //Log.d("parse Notification", json.toString());
            ParseAnalytics.trackAppOpenedInBackground(intent);
            context.startService(new Intent(context, ChatHeadService.class).putExtra("title", json.getString("header")).putExtra("image", json.getString("image")).putExtra("lien",json.getString("lien")));

        } catch (JSONException e) {
            Log.d("Parse Notification", "JSONException: " + e.getMessage());
        }


    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
