package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.ActivityUpdater;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Annonce;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 Created by Boris on 9/24/15.
 */
public class updater extends IntentService
{

    private   String url = Kerawa_Parameters.PreProdURL;
    private final IBinder mBinder = new MyBinder();


    private DatabaseHelper myDB ;
    private ArrayList<Annonce> val = new ArrayList();

    public updater() {
        super("updater");
    }




    @Override
    protected void onHandleIntent(Intent intent) {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String version = pInfo.versionName;
        url = url + "mversion/android";



            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET , url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse( JSONObject response) {


                    try {
                        String server_version = response.getString("data");
                        String locversion = version ;
                        if (server_version.contains("."))
                            server_version = server_version.substring(0,server_version.indexOf("."));
                        if (version.contains("."))
                             locversion = version.substring(0,version.indexOf("."));

                        int int_server_version = Integer.valueOf(server_version);
                        int int_loc_version = Integer.valueOf(locversion);



                        if(int_loc_version<int_server_version)
                           {
                               Log.d("KerawaAndroid", response.toString());
                               Intent i = new Intent(getApplicationContext(),ActivityUpdater.class);
                               i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(i);
                           }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                    return params;
                }
            };




            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);

        stopSelf();   // arret du service created by Marcelin

        }





    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        updater getService() {
            return updater.this;
        }
    }
}
