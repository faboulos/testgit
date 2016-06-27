package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kerawa.app.utilities.Krw_functions;

import org.apache.commons.*;

import java.io.File;


public class delete_files extends IntentService {


    public delete_files() {
        super("delete_files");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //delete the .kerawa file Here
         if (Krw_functions.can_delete_files(getApplicationContext()))
         {

             File path = Environment.getExternalStorageDirectory();
             File f = new File(path.getAbsoluteFile(),".kerawa");
             if (Krw_functions.deleteDir(f))
             {

                 //edit the shared preference
                 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                 SharedPreferences.Editor editor = prefs.edit();
                 editor.putLong("delete", System.currentTimeMillis());
                 editor.apply();


                 stopSelf();
             }
         }

      stopSelf();       // arret du service created by Marcelin

        }

}
