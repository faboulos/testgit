package com.kerawa.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;

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
import java.util.HashMap;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.add_Rabuttons_TO_RadioGroup;

/**
  Created by kwemart on 30/08/2015.
 */
public class Cities_loader   extends AsyncTask<String,String,String> {

    public View v;
    ShowHide vx;
    public Context ctx ;
    public int CountryID;
    public RadioGroup rgx;
    public String Texte;
    HashMap<Integer,String> VilLes;
    DatabaseHelper mydb;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(v!=null) {
            vx=new ShowHide(v);
            vx.Show();
        }
    }

    @Override
    protected String doInBackground(String... args) {
        mydb=new DatabaseHelper(ctx);
        VilLes= new HashMap<Integer,String>();
        VilLes=mydb.getAllCities_from_countryID(CountryID);
        if(VilLes.size()==0) {
            String url = String.format("%sregions/" + CountryID + "/cities", Kerawa_Parameters.PreProdURL);
            String response = "";
            HttpGet conn = new HttpGet(url);
            DefaultHttpClient client = new DefaultHttpClient();
            List<NameValuePair> urlParameters = new ArrayList<>();
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

            } catch (IOException e) {
                response = String.valueOf(e);
            }

            return response;
        }

        return "";

    }


    protected void onPostExecute(String result) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String nm = prefs.getString("Kerawa-count", null);
        final String nom = Krw_functions.countryName(nm, ctx);

        if (!result.equals("")) {

            try {
                JSONObject data = new JSONObject(result);
                JSONArray data2 = data.getJSONArray("data");
                int count = data2.length();
                for (int i = 0; i < count; i++) {
                    //iterate through jsonArray
                    JSONObject jsonObject = data2.getJSONObject(i);  // get jsonObject @ i position
                    VilLes.put(jsonObject.getInt("city_id"), jsonObject.getString("city_name"));
                    mydb.insertCity(jsonObject.getInt("city_id"), jsonObject.getString("city_name"), String.valueOf(CountryID));
                }

            } catch (Exception e) {
                Show_Toast(this.ctx, "Probleme de chargement temporaire", true);
            } finally {
                if(rgx!=null) {
                    Show_Toast(ctx, "Chargement terminé \r\nSélectionnez votre ville", false);
                    add_Rabuttons_TO_RadioGroup(VilLes, Texte, ctx, rgx);
                    rgx.setFocusable(true);
                    rgx.setFocusableInTouchMode(true);
                    rgx.requestFocus();
                    vx.Hide();
                }
            }
        }else{
            if(rgx!=null) {
                add_Rabuttons_TO_RadioGroup(VilLes, Texte, ctx, rgx);
                rgx.setFocusable(true);
                rgx.setFocusableInTouchMode(true);
                rgx.requestFocus();
                vx.Hide();
            }
        }
    }
}
