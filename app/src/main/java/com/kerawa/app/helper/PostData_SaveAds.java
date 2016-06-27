package com.kerawa.app.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.kerawa.app.utilities.Ad_details;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 Created by lenovo on 19/08/2015.
 */
public class PostData_SaveAds extends AsyncTask<String,String,String>
{


    public Ad_details items = new Ad_details();
    public View v;
    ShowHide vx;
    public TextView v2;
    public Context ctx ;
    public int menuID;
    String retour;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        vx=new ShowHide(v);
        vx.Show();
    }

    @Override
    protected String doInBackground(String... args) {
        String url= Kerawa_Parameters.PreProdURL+"ads/create";
        String response="";
        HttpPost conn = new HttpPost(url);
        DefaultHttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
      /*  urlParameters.add(new BasicNameValuePair("catId ",String.valueOf(items.getCategoryId())));
        urlParameters.add(new BasicNameValuePair("title", items.getTitle()));
        urlParameters.add(new BasicNameValuePair("imei", new AndroideDevice(ctx).getImei()));
        urlParameters.add(new BasicNameValuePair("description", items.get_adDescription()));
        urlParameters.add(new BasicNameValuePair("cityId", items.getCityId().toString()));
        urlParameters.add(new BasicNameValuePair("regionId", items.getCountryId().toString()));
        urlParameters.add(new BasicNameValuePair("authorization_key",getAuthorisationKey(ctx)));*/
        conn.setHeader("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(ctx).Hide_meta_data());
        HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


        try {
            conn.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
             response="Encodage non supporté : "+String.valueOf(e);
        }

        HttpResponse res;
        try {
            res = client.execute(conn);
            //response="Response Code : "+res.getStatusLine().getStatusCode()+"\r\n";
            BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            response +=result;

        } catch (IOException e) {
            response= String.valueOf(e);
        }

        return response;

    }


   protected void onPostExecute(String result) {

   //v2.setText(result);
       Object json = null;
       try {
           json = new JSONTokener(result).nextValue();
           if (json instanceof JSONObject) {
              // v2.setText("Le serveur retourne un "+json.getClass());
               JSONObject data = new JSONObject(result);
               if(data.has("errors")){
                JSONArray errors=data.getJSONArray("errors");
                JSONObject error_resp = (JSONObject) errors.get(0);
               Krw_functions.Show_Toast(ctx, error_resp.getString("detail") + "\r\n" + error_resp.getString("message"), false);
               }else if(data.has("data")){
                   JSONObject succes=data.getJSONObject("data");
                   //v2.setText(succes.getString("detail"));
                   Krw_functions.Show_Toast(ctx, succes.getString("message"), false);
                   /*JSONObject user=new JSONObject();
                   String email=items.getEmail(),name=items.getName(),phone=items.getUserPhones(),user_Name=items.getUsername();
                   user.put("email",email);
                   user.put("name", user_Name);
                   user.put("phone_mobile", phone);
                   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                   SharedPreferences.Editor editor = prefs.edit();
                   editor.putString("username", user.toString());
                   editor.apply();
                   switch(menuID) {
                       case R.id.action_account:
                       ctx.startActivity(new Intent(ctx, ActivityUserAccount.class));
                       finishThisActivity(ctx);
                       break;
                       case R.id.action_add:
                           v2.setText("Fuctionality under construction...\r\n Click here to go to your Account");
                           v2.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v)
                               {
                                   ctx.startActivity(new Intent(ctx, ActivityUserAccount.class));
                                   finishThisActivity(ctx);
                               }
                           });
                       break;
                   }*/
               }

           } else if (json instanceof JSONArray){
              Krw_functions.Show_Toast(ctx, "Server en maintenance...", false);
           }

       } catch (JSONException e) {
          Krw_functions.Show_Toast(ctx, e.getLocalizedMessage(), true);
       }

   vx.Hide();

    }


}