package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.helper.DatabaseHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 Created by Boris on 9/24/15.
 */
public class CitiesDownloader extends IntentService
{

    private   String url = Kerawa_Parameters.PreProdURL;
    private final IBinder mBinder = new MyBinder();
    private String next_url = "";
    JSONArray img_url = new JSONArray();
    private DatabaseHelper myDB ;
    public CitiesDownloader() {
        super("AdsDownloader");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // don't notify if they've played in last 24 hr
        myDB = new DatabaseHelper(getApplicationContext());
        myDB.deleteAllADs();
        //supression et recreation du fichier des images et creation d'un nouveau vide
        ImageStorage.createStorage();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        String nm = prefs.getString("Kerawa-count", null);

        final String nom = Krw_functions.countryName(nm, this.getApplicationContext());
        next_url = Kerawa_Parameters.PreProdURL + "regions/"+nom+"/cities" ;
            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, next_url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // the response is already constructed as a JSONObject!
                    try {

                        //response = response.getJSONObject("data");
                        JSONArray arr = response.getJSONArray("data");
                        //delete all ads from the DB

                        //delete all the associated files


                        for (int i = 0; i <= arr.length() - 1; i++) {

                           int id =0;
                            String name ="";

                            if (arr.getJSONObject(i).has("city_id")) {
                                id = arr.getJSONObject(i).getInt("city_id");
                            }
                            if (arr.getJSONObject(i).has("city_name")) {
                                name = arr.getJSONObject(i).get("city_name").toString();
                            }

                            //new Image_downloader(null).execute(an.getImage_thumbnail_url(), an.getAdd_id());
                            myDB.insertCity(id,name,nom);
                        }


                        JSONObject obj = response.getJSONObject("meta");
                        if (obj.has("next")) {
                            next_url = obj.getString("next");
                        }
                        //save the response inside the database

                    } catch (JSONException e) {
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

            // Creating volley request obj

            // Adding request to request queue
            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);




        stopSelf();       // arret du service created by Marcelin
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        CitiesDownloader getService() {
            return CitiesDownloader.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
