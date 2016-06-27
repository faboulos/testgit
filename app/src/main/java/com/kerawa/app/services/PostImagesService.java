package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class PostImagesService extends IntentService {

    private String ad_id ;
    private int id ;
    private String nbpics ;
    private String  date;
    private DatabaseHelper myDB;
    private String boundary;

    public PostImagesService() {
        super("PostImagesService");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);


    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
          Bundle b = intent.getExtras();
          ad_id = b.getString("ID");
          nbpics = b.getString("nbreImgs");
          date = b.getString("date");
          id = b.getInt("id_database");
        }

        myDB = new DatabaseHelper(getApplicationContext());


        Krw_functions.ShowToastWithinService(getApplicationContext(),"post images request started");




        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Kerawa_Parameters.PreProdURL+ "ads/send/images", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!

                try {
                    JSONObject obj = response.getJSONObject("data");

                    //processing the response working

                    if (obj.getInt("code") == 200) {
                        myDB.updateofflineAdStatus(id, obj.getString("status"));
                    }
                    Log.d("Server response",response.toString()) ;

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

                                    Log.d("error",new String(response.data));
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
                params.put("count", nbpics);
                params.put("ad_id",ad_id);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/.kerawa/" + date);
                if (myDir.exists()) {
                    File[] filelist = myDir.listFiles();

                    Krw_functions.ShowToastWithinService(getApplicationContext(),"the ad has : "+filelist.length +" images" );
                    Krw_functions.ShowToastWithinService(getApplicationContext(),"the parameter nbpics is : "+nbpics +" images" );
                    for (int i = 0; i <= filelist.length - 1; i++) {
                        // do your stuff here
                        //File myFile = new File(filelist[i].getAbsolutePath());
                        //transform images in base 64
                        params.put("image"+i,"data:image/jpeg;base64,"+toBase64(filelist[i].getAbsolutePath()));
                      //  params.put("img_length",Krw_functions.toBase64(filelist[i].getAbsolutePath()).length()+"");
                        Log.d("IMAGE LOCAL URL", filelist[i].getAbsolutePath() );

                    }
                }
                //params.put("description", values.get(k).getDescription());

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
                objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);

        stopSelf();    // arret du service created by Marcelin To resolve problem
    }
    public String toBase64(String path)
    {
        Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
       // if(bm.getRowBytes()*bm.getHeight()<=2000)
       // {
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
       // }
       // else
       // {
          //compress image before sending it to the server

        //}
        byte[] b = baos.toByteArray();

        String temp = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
      return temp ;
    }
}
