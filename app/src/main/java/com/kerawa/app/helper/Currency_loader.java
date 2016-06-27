package com.kerawa.app.helper;

import android.content.Context;
import android.os.AsyncTask;

import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
  Created by kwemart on 30/08/2015.
 */
public class Currency_loader extends AsyncTask<String,String,String> {

    public Context ctx ;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
          }

    @Override
    protected String doInBackground(String... args) {

            String url = String.format("%scurrencies", Kerawa_Parameters.PreProdURL);
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


    protected void onPostExecute(String result) {
        Krw_functions.saveCurrency(ctx, result);
    }
}
