package com.kerawa.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by mabian etinge on 3/8/16.
 */
public class KerawaAppRate {
    private final static String APP_TITLE = "Kerawa";
    private final static String   APP_PNAME = "com.kerawa.app";


    private  static int LAUNCHES_UNTIL_PROMPT = 2;

    static TextView message;
    static long date_firstLaunch;
    static long launch_count;
    private  static SharedPreferences  preferences;
    public static void app_launches(Context context){
        preferences = context.getSharedPreferences("apprater", 0);
        if(preferences.getBoolean("dontshowagain", false))
            return;

        SharedPreferences.Editor editor = preferences.edit();


        //Incrementing  launch counter
        long launch_count = preferences.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);


        if(launch_count == (LAUNCHES_UNTIL_PROMPT))
        {
            showRateDialog(context, editor);

        }

        editor.commit();
    }

    private static void showRateDialog(final Context context,final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.raterapp);
        TextView title = new TextView(context);
        title.setText("Kerawa");
        title.setTextColor(Color.BLACK);
        dialog.setTitle(title.getText().toString());
        dialog.setCancelable(false);
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.rate_icon);

        message = (TextView)dialog.findViewById(R.id.message);
            message.setText(R.string.ratemessage);



        Button rate = (Button)dialog.findViewById(R.id.rateme);
        Button later = (Button)dialog.findViewById(R.id.later);

        //making buttons clickable and responsive
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                //multiple the number of launch days by 10 if the user has rated the app
                LAUNCHES_UNTIL_PROMPT *= 10;
                launch_count = preferences.getLong("launch_count", launch_count) * 0;
                editor.putLong("launch_count", launch_count);
                editor.commit();
                dialog.dismiss();
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add 7 launches when the user says later
                LAUNCHES_UNTIL_PROMPT += 7;
                launch_count = preferences.getLong("launch_count", launch_count) * 0;
                editor.putLong("launch_count", launch_count);
                editor.commit();
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}
