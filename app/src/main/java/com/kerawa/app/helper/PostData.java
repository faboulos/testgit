package com.kerawa.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.kerawa.app.ActivityUserAccount;
import com.kerawa.app.R;
import com.kerawa.app.RegisterActivity;
import com.kerawa.app.createAdsActivity;
import com.kerawa.app.utilities.AndroideDevice;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Person;
import com.kerawa.app.utilities.ShowHide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.finishThisActivity;

/**
 Created by lenovo on 19/08/2015.
 */
public class PostData extends AsyncTask<String,String,String>
{


    public Person items = new Person();
    public View v;
    ShowHide vx;
    public TextView v2;
    public Context ctx ;
    public int menuID;
    private boolean state = false;


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
       if(this.v!=null) {
           vx = new ShowHide(v);
           vx.Show();
       }


    }

    @Override
    protected String doInBackground(String... args) {
        String url= String.format("%ssignin", Kerawa_Parameters.PreProdURL);
        String response="";
        HttpPost conn = new HttpPost(url);
        DefaultHttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("email",items.getEmail()));
        urlParameters.add(new BasicNameValuePair("imei", new AndroideDevice(ctx).getImei()));
        urlParameters.add(new BasicNameValuePair("password", items.getPass()));
        urlParameters.add(new BasicNameValuePair("authorization_key", items.getAutorisationKey()));
        urlParameters.add(new BasicNameValuePair("phone", items.getUserPhones()));
        urlParameters.add(new BasicNameValuePair("secretcode", items.getSecretCode()));
        conn.setHeader("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(ctx).Hide_meta_data());
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
        //Show_Toast(ctx, response,true);
        return response;

    }


   protected void onPostExecute(String result) {
       final TextView statusText = (TextView) ((Activity)ctx).findViewById(R.id.status_text);
       // v2.setText(result);
       boolean vrai=false;
       Object json = null;
       try {
           json = new JSONTokener(result).nextValue();
           if (json instanceof JSONObject) {
               JSONObject data = new JSONObject(result);
               if(data.has("errors")){
                JSONArray errors=data.getJSONArray("errors");
                JSONObject error_resp = (JSONObject) errors.get(0);
              // if(v2!=null){ v2.setText(error_resp.getString("detail"));}
               //Show_Toast(ctx,"Erreur: "+error_resp.toString(),true);
                 try {
                     Errors_handler msg_err=new Errors_handler((Activity)ctx,ctx,error_resp.getString("detail"),true,statusText,R.drawable.errore);
                     msg_err.execute(500, 500, 6000);
                 }catch (Exception e){
                     Show_Toast(ctx,"Erreur: "+e.toString(),true);
                 }

               }else if(data.has("data")){
                  vrai=true;
                  JSONObject succes=data.getJSONObject("data");
                   Errors_handler msg_err=new Errors_handler((Activity)ctx,ctx,succes.getString("message"),false,statusText,R.drawable.errore);
                   msg_err.execute(500, 500, 6000);
                   this.setState(false);
                 if(succes.has("user")) {
                   JSONObject user=succes.getJSONObject("user");
                  // Show_Toast(ctx,user.toString(),true);
                   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                   SharedPreferences.Editor editor = prefs.edit();
                   editor.putString("username", user.toString());
                   editor.apply();
                     this.setState(true);

                 }

               }

           } else if (json instanceof JSONArray){
               vrai=false;
              // if(v2!=null){v2.setText(result);}
               //le serveur renvoit un mauvais format
               //Show_Toast(ctx, "1 Serveur en cours de maintenance..."+result,true);
               Errors_handler msg_err=new Errors_handler((Activity)ctx,ctx,result,true,statusText,R.drawable.errore);
               msg_err.execute(500, 500, 6000);
           }

       } catch (JSONException e) {
           //v2.setText(e.getLocalizedMessage());
           //root.addView(statusText);
           Errors_handler msg_err=new Errors_handler((Activity)ctx,ctx,result,true,statusText,R.drawable.errore);
           msg_err.execute(500, 500, 6000);
           vrai=false;
       }finally {
           if(vrai) {
               switch (menuID) {
                   case R.id.action_add:
                       ctx.startActivity(new Intent(ctx,createAdsActivity.class));
                       break;
                   case R.id.action_account:
                       ctx.startActivity(new Intent(ctx,ActivityUserAccount.class));
                       break;
               }
               finishThisActivity(ctx);
           }
       }

       if(this.v!=null) {
           vx.Hide();
       }

}

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}