package com.kerawa.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kerawa.app.R;
import com.kerawa.app.RegisterActivity;
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

/**
 Created by lenovo on 19/08/2015.
 */
public class PostDataRegister extends AsyncTask<String,String,String>
{


    public Person items = new Person();
    public View v;
    ShowHide vx;
    public TextView v2;
    public Context ctx ;
    public int menuID;
    String retour;
    DatabaseHelper mbd;


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        vx=new ShowHide(v);
        vx.Show();

    }

    @Override
    protected String doInBackground(String... args) {
        String url= Kerawa_Parameters.PreProdURL+"signup";
        String response="";
        HttpPost conn = new HttpPost(url);
        DefaultHttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        //parametres de la requete
        urlParameters.add(new BasicNameValuePair("imei", new AndroideDevice(ctx).getImei()));
        urlParameters.add(new BasicNameValuePair("name", items.getUsername()));
        urlParameters.add(new BasicNameValuePair("email",items.getEmail()));
        urlParameters.add(new BasicNameValuePair("phone", items.getUserPhones()));
        urlParameters.add(new BasicNameValuePair("cityId", String.valueOf(items.getCityId())));
        urlParameters.add(new BasicNameValuePair("regionId", String.valueOf(items.getCountry())));

        urlParameters.add(new BasicNameValuePair("language", "fr_FR"));

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
       boolean vrai=false;
       try {
           json = new JSONTokener(result).nextValue();
           if (json instanceof JSONObject) {
              // v2.setText("Le serveur retourne un "+json.getClass());
               JSONObject data = new JSONObject(result);
               if(data.has("errors")){
                vrai=false;
                JSONArray errors=data.getJSONArray("errors");
                JSONObject error_resp = (JSONObject) errors.get(0);
              // Show_Toast(ctx,error_resp.getString("detail")+"\r\n"+error_resp.getString("message"),false);
                   Errors_handler msg_err= new Errors_handler((Activity)ctx,ctx,error_resp.getString("detail")+"\r\n"+error_resp.getString("message"),false,v2, R.drawable.errore);
                   msg_err.execute(500, 500, 3000);
               }else if(data.has("data")){
                  vrai=true;
                  JSONObject succes=data.getJSONObject("data");
                 // Show_Toast(ctx, succes.getString("message"), false);
                  //v2.setText(succes.getString("code"));
                   Errors_handler msg_err=new Errors_handler((Activity) ctx,ctx,succes.getString("message"),false,v2, R.drawable.errore);
                   msg_err.execute(500, 500, 3000);
                   JSONObject user = succes.getJSONObject("user");
                  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                  SharedPreferences.Editor editor = prefs.edit();
                  editor.remove("username");
                  editor.apply();
                  editor.putString("username", user.toString());
                  editor.apply();

                /*// recuperation des parametres du json retourne et stockage dans la base de donnees
                   mbd = new DatabaseHelper(ctx);
                   Person person = new Person();
                   person.setId(1);
                   person.SetName(user.getString("name"));
                   person.SetUserPhones(user.getString("phone_mobile"));
*/

               }

           } else if (json instanceof JSONArray){
              // v2.setText("Server en maintenance...");
               Errors_handler msg_err=new Errors_handler((Activity) ctx,ctx,"Kerawa est en cours maintenance...",true,v2, R.drawable.errore);
               msg_err.execute(500, 500, 63000);
           }

       } catch (JSONException e) {
           Errors_handler msg_err=new Errors_handler((Activity) ctx,ctx,"Impossible de s'enregistrer pour l'instant.\nVeuillez reéssayer ultérieurement.",true,v2, R.drawable.errore);
           msg_err.execute(500, 500, 63000);
       }

       vx.Hide();

    }




}