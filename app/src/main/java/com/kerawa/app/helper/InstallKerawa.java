package com.kerawa.app.helper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kerawa.app.WrongVersion;
import com.kerawa.app.splash;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.LaunchSplash;
import static com.kerawa.app.utilities.Krw_functions.startThisActivity;


/**
 Created by lenovo on 19/08/2015.
 */
public class InstallKerawa   extends AsyncTask<String,String,String> {

    public TextView textview;
    public Context ctx;
    private DatabaseHelper mydb;
    public TextView mytext;
    public ImageView refresher;
    public RelativeLayout popop_win;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        textview.setText("Configuration kerawa en cours...");

    }

    @Override
    protected String doInBackground(String... args) {

//        String url = String.format("%sregions", Kerawa_Parameters.PreProdURL);

        String url = String.format("%scountries", Kerawa_Parameters.PreProdURL);
        String response = "";
        HttpGet conn = new HttpGet(url);
        DefaultHttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<>();
       conn.setHeader("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(ctx).Hide_meta_data());
        // configure setHeader in PreProd app
       // conn.setHeader("X-Kerawa-Api-Consumer-Key", "97d5aeec9296c304634d85cf214308a669ee29f9");
        HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


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
            response += result;

        } catch (Exception e) {
            response = String.valueOf(e);

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                // Do something for lollipop and above versions
               startThisActivity(ctx, new Intent(ctx,WrongVersion.class));
            }

        }

        return response;

    }


    protected void onPostExecute(String result) {
     boolean ok=true;


        try {
                 mydb=new DatabaseHelper(this.ctx);
                JSONObject data = new JSONObject(result);
                JSONArray data2=data.getJSONArray("data");
                String Sortie = "";
                int count=data2.length();

            for(int i = 0 ; i < count; i++){
              // iterate through jsonArray
                textview.setText("Configuration des Régions...");
                JSONObject jsonObject = data2.getJSONObject(i);  // get jsonObject @ i position
                mydb.insertCountry(jsonObject.getInt("region_id"), jsonObject.getString("region_name"), jsonObject.getString("country_code"));
                Sortie+="Country : "+jsonObject.getString("region_name")+", ID: "+jsonObject.getInt("region_id")+", ISO: "+jsonObject.getString("country_code")+"\r\n";
                Cities_loader villes_init=new Cities_loader();
                villes_init.ctx=ctx;
                villes_init.CountryID=jsonObject.getInt("region_id");
                villes_init.execute();
            }
               // Show_Toast(this.ctx,Sortie,true);

        } catch (Exception e) {
           ok=false;
        }finally {
          if (ok) {
              LaunchSplash(ctx, 500, this.textview);
          }else{
              new ShowHide(popop_win).Show();
              new ShowHide(textview).Hide();
              refresher.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mydb.reset_countries();
                      Krw_functions.finishThisActivity(ctx);
                      Krw_functions.startThisActivity(ctx, new Intent(ctx, splash.class));
                    }
              });
          }
        }

    }
}



