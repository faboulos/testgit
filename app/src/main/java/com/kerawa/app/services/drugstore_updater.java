package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.millisecondTodate;
import static com.kerawa.app.utilities.Krw_functions.show_drugstore_Notification;

/**
 Created by Boris on 9/24/15.
 */
public class drugstore_updater extends IntentService {

    private   String url = Kerawa_Parameters.PreProdURL;
    private final IBinder mBinder = new MyBinder();
    private String next_url = "";
    private DatabaseHelper myDB ;
    private String first_url;
    private JSONArray liste_phone;

    public drugstore_updater() {
        super("drugstore_updater");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nom = prefs.getString("Kerawa-count", null);
        if (nom != null) {
            if (!nom.toLowerCase().equals("cm")) {
                stopSelf();
                return;
            }
        }
        // don't notify if they've played in last 24 hr
        myDB = new DatabaseHelper(getApplicationContext());
       // myDB.deleteAllADs();
        //supression et recreation du fichier des images et creation d'un nouveau vide
        ImageStorage.createStorageTemp();
        final ImageLoader imageLoader = ImageLoader.getInstance();

        final Long ID0  = prefs.getLong("id_fichier", 0);
        //nom = countryName(nom, getApplicationContext());
        first_url=url + "pharmacy/"+nom+"?avaible";
        next_url=url + "pharmacy/"+nom;
        final SharedPreferences.Editor editor = prefs.edit();
        Log.d("drugstore","drugstore service started"+ID0);
        Log.d("drugstore",millisecondTodate(Calendar.getInstance().getTimeInMillis(),"HH:mm"));
            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {
                     //response = response.getJSONObject("data");
                    //JSONArray arr = response.getJSONArray("data");
                    Long ID=response.getLong("id");
                    Log.d("drugstore",ID+"");
                    prefs.edit().remove("drug_update").apply();
                    prefs.edit().putLong("drug_update", Calendar.getInstance().getTimeInMillis()).apply();
                    if (ID0<ID) {

                        editor.remove("pharmacy_list").apply();
                        prefs.edit().remove("last_drug_update").apply();
                        prefs.edit().putLong("last_drug_update",ID).apply();

                        //if(myDB.reset_drugstores()){
                           Log.d("drugstore", "drugstores resetted");
                          // ShowToastWithinService(getApplication(),"drugstores resetted");

                          load_drug_stores(next_url);
                       //}
                    }else{
                        prefs.edit().putLong("last_drug_update",System.currentTimeMillis());

                        stopSelf();      // arret du service created by Marcelin
                    }

                } catch (JSONException ignored) {
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                         Log.d("drugstore",error.toString()+" - "+first_url);
                        //ShowToastWithinService(getApplicationContext(), error.toString()+" - "+first_url);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                        return params;
            }
        };

        // Creating volley request obj

                // Adding request to request queue


                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
                movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }
    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    private void load_drug_stores(String url){
       // Log.d("drugstore", "drugstores download started");
        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
/*                try {

                    //response = response.getJSONObject("data");
                    JSONArray arr = response.getJSONArray("data");
                    Log.d("drugstore", arr.toString());
                    for(int i=0;i<arr.length();i++){
                        Drugstore drs=new Drugstore();
                        if (arr.getJSONObject(i).getString("region")!=null){
                            drs.setRegion(arr.getJSONObject(i).getString("region"));
                        }

                        if (arr.getJSONObject(i).getString("ville")!=null){
                            drs.setCity(arr.getJSONObject(i).getString("ville"));
                        }

                        if (arr.getJSONObject(i).getString("nom")!=null){
                            drs.setStoreName(arr.getJSONObject(i).getString("nom"));
                        }
                        if (arr.getJSONObject(i).getString("localisation")!=null){
                            drs.setLocalisation(arr.getJSONObject(i).getString("localisation"));
                        }

                        if (arr.getJSONObject(i).getJSONArray("telephone")!=null){
                            ArrayList<String> phones=new ArrayList<>();
                            liste_phone=arr.getJSONObject(i).getJSONArray("telephone");
                            for (int k=0;k<liste_phone.length();k++){
                                phones.add(liste_phone.get(k).toString());
                            }
                            drs.setPhones(phones);
                        }
                        myDB.insertDrugStore(drs);
                    }
                    Log.d("drugstore","Drugstore downloaded");
                    int id=response.getInt("id");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    prefs.edit().remove("id_fichier").apply();
                    prefs.edit().putInt("id_fichier",id).apply();
                    show_drugstore_Notification(getApplicationContext());
                } catch (JSONException e) {
                    Log.d("drugstore", e.toString());
                }
                String content = response.toString();
                Log.d("drugstore", "Drugstore downloaded");
                FileWriter fw;
                File sdcard = Environment.getExternalStorageDirectory();
                File traceFile = new File(sdcard.getAbsoluteFile(), "pharmacy.json");
                if (!traceFile.exists()) try {
                    boolean newFile = traceFile.createNewFile();
                } catch (IOException ignored) {}
                */
                try {
                   /* fw = new FileWriter(traceFile.getAbsolutePath());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(content);
                    bw.close();*/
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("pharmacy_list", response.toString());
                    editor.apply();
                    long id=response.getLong("id");
                    Log.d("id_fichier",String.valueOf(id));
                    prefs.edit().remove("id_fichier").apply();
                    prefs.edit().putLong("id_fichier", id).apply();
                    show_drugstore_Notification(getApplicationContext());
                } catch (JSONException e) {
                    Log.d("drugstore", e.toString());
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       // ShowToastWithinService(getApplicationContext(), error.toString());
                        Log.d("drugstore",error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                return params;
            }
        };

        // Creating volley request obj

        // Adding request to request queue


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stopSelf();        // arret du service created by Marcelin
    }

    public class MyBinder extends Binder {
        drugstore_updater getService() {
            return drugstore_updater.this;
        }
    }
}
