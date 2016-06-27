package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 Created by Boris on 9/24/15.
 */
public class MetasDownloader extends IntentService
{

    private   String initurl = Kerawa_Parameters.PreProdURL;
    private final IBinder mBinder = new MyBinder();


    private DatabaseHelper myDB ;
    private ArrayList<Annonce> val = new ArrayList();

    public MetasDownloader() {
        super("updater");
    }




    @Override
    protected void onHandleIntent(Intent intent) {

        myDB = new DatabaseHelper(getApplicationContext());
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String version = pInfo.versionName;
        ArrayList<Model> model = Krw_functions.generateCategoriesList();


        for (int i=0;i<=model.size()-1;i++) {

            HashMap<Integer, String> map = myDB.getAllCategories(model.get(i).getTag());
            if (map.size() !=0 ){
            for (int ident : map.keySet()) {
                final int k = ident;
                String url = initurl + "metas/category/" + ident;
                JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray array = response.getJSONArray("data");
                            myDB.deleteAllMetas();
                            for (int j = 0; j <= array.length(); j++) {
                                JSONObject obj = array.getJSONObject(j);
                                String name = obj.getString("name");
                                int id = obj.getInt("id");
                              //  myDB.insertMeta(name, id, k);
                               // Log.d("TAG", "meta name: " + name);
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

            }
        }
        }
            stopSelf();    // arret du service created by Marcelin
    }





    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        MetasDownloader getService() {
            return MetasDownloader.this;
        }
    }
}
