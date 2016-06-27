package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.offline_ad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdsService extends IntentService {


    public PostAdsService()  {
        super("PostAdsService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final DatabaseHelper myDB = new DatabaseHelper(getApplicationContext());
        final List<offline_ad> values = myDB.getAllOfflineAds();
        Krw_functions.ShowToastWithinService(getApplicationContext(),values.size()+"");
       try{ Log.i("TAG", Krw_functions.Variables_Session(getApplicationContext()).getAutorisationKey());}catch (Exception ignored){}

        if(Krw_functions.isConnected(getApplicationContext())) {
            if (values.size() > 0) {

                for (int i = 0; i <= values.size() - 1; i++) {
                    //save ads online
                    final int k = i;
                    Krw_functions.ShowToastWithinService(getBaseContext(),"database ID is:" +values.get(k).getId()+"");

                    JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.POST, Kerawa_Parameters.PreProdURL+ "ads/create", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // the response is already constructed as a JSONObject!
                            try {
                                JSONObject obj = response.getJSONObject("data");
                                if (obj.getInt("code") == 200) {
                                    if (obj.getString("detail").equalsIgnoreCase("granted")) {
                                       // values.get(k).setAd_id(String.valueOf(obj.getInt("item")));
                                        //values.get(k).setStatus(obj.getString("status"));

                                        myDB.updateofflineAdId(values.get(k).getId(), String.valueOf(obj.getInt("item")));
                                        Intent i = new Intent(getApplicationContext(),PostImagesService.class);
                                        i.putExtra("ID",String.valueOf(obj.getInt("item")));
                                        i.putExtra("nbreImgs", values.get(k).getNbpic());
                                        i.putExtra("date", values.get(k).getDate());
                                        i.putExtra("id_database", values.get(k).getId());
                                       if(Integer.parseInt(values.get(k).getNbpic())>0) {
                                           startService(i);
                                       }
                                       else
                                       {
                                           myDB.updateofflineAdStatus(values.get(k).getId(), obj.getString("status"));
                                       }
                                        Krw_functions.ShowToastWithinService(getApplicationContext(), "ad with the ID:" + obj.getInt("item") + " successfully created");
                                    }
                                } else {
                                    Krw_functions.ShowToastWithinService(getApplicationContext(), "service stopped because of error code");
                                   // stopSelf();
                                }

                               // myDB.updateofflineAd(values.get(k).getId(), obj.getString("status"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //errore.printStackTrace();
                                    JSONObject json = null;

                                    NetworkResponse response = error.networkResponse;
                                    if(response != null && response.data != null){
                                        switch(response.statusCode){
                                            case 500:

                                                    Log.d("errore",new String(response.data));
                                                    break;


                                        }
                                        //Additional cases
                                    }

                                }


                            }) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());


                            return params;
                        }
                        @Override
                        public String getBodyContentType() {
                            // TODO Auto-generated method stub
                            return "application/x-www-form-urlencoded";
                        }

                        @Override
                        public String getParamsEncoding() {
                            return "utf-8";
                        }

                        @Override
                        public byte[] getBody() {

                            Map<String, String> params = new HashMap<String, String>();

                            params.put("authorization_key", Krw_functions.Variables_Session(getApplicationContext()).getAutorisationKey());
                            params.put("catId", values.get(k).getSubcategory());
                            params.put("title", values.get(k).getTitle() );
                            params.put("description", values.get(k).getDescription());
                            params.put("regionId", values.get(k).getCountryId());
                            params.put("cityId", values.get(k).getCityId());
                            params.put("price", values.get(k).getPrice());
                            params.put("currency",values.get(k).getCurrency());
                            params.put("meta[2]", values.get(k).getPhone1());
                            params.put("meta[3]", values.get(k).getPhone2());
                            params.put("email", values.get(k).getEmail());
                            params.put("nbreImgs", values.get(k).getNbpic());

                            return encodeParameters(params, getParamsEncoding());
                        }

                        private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                            StringBuilder encodedParams = new StringBuilder();
                            try {
                                for (Map.Entry<String, String> entry : params.entrySet()) {
                                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                                    encodedParams.append('=');
                                    Log.d("TAG_k",entry.getKey()+"=>"+"TAG_v"+entry.getValue());
                                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                                    encodedParams.append('&');
                                }
                                return encodedParams.toString().getBytes(paramsEncoding);
                            } catch (UnsupportedEncodingException uee) {
                                throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
                            }
                        }
                    };

                    // Creating volley request obj

                    // Adding request to request queue
                    movieReq.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);


                }
                //passage dans le la base des annonces pour voir s'il en reste et si oui
            /*    if (myDB.getAllOfflineAds().size() > 0) {
                    startService(new Intent(getApplicationContext(), PostAdsService.class));
                } else {
                    stopSelf();
                }*/
            }
            else
            {
                startService(new Intent(getApplicationContext(),ImagesUploaderCron.class));
            }


            stopSelf();      // arret du service created by Marcelin


        }
        }
}
