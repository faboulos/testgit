package com.kerawa.app.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kerawa.app.utilities.Kerawa_Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;

import static com.kerawa.app.utilities.Krw_functions.Variables_Session;

/**
 Created by martin on 11/2/15.
 */
public class upload_photos extends AsyncTask<String,String,String> {

    public File[] Images;
    public String ServerURL;
    public Context ctx;
    public String ad_id;

    @Override
    protected String doInBackground(String... params) {
        String boundary = "*************";
        String serverResponse = "Pas de reponse";
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (File image : Images) {
            FileBody fileBody = new FileBody(new File(image.getAbsolutePath()));//pass your image path to make a file
            builder.addPart("images[]", fileBody);
        }
        builder.addPart("authorization_key", new StringBody(Variables_Session(ctx).getAutorisationKey(), ContentType.TEXT_PLAIN));//pass our jsonObject  here
        builder.addPart("ad_id", new StringBody(ad_id, ContentType.TEXT_PLAIN));//pass our jsonObject  here
        builder.addPart("count", new StringBody(Images.length+"", ContentType.TEXT_PLAIN));//pass our jsonObject  here

        HttpEntity entity = builder.build();

        URL url = null;

        try {
            url = new URL(ServerURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(ctx).Hide_meta_data());
            urlConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
            urlConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
            OutputStream os = urlConnection.getOutputStream();
            entity.writeTo(urlConnection.getOutputStream());
            os.close();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            StringBuilder stringBuilder = new StringBuilder("");
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
            serverResponse = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverResponse;
    }
    protected void onPostExecute(String result) {
        DatabaseHelper Mydb=new DatabaseHelper(ctx);
        Log.d("Reponse_SERVER", result);
        Mydb.updateofflineAdStatus(Integer.parseInt(ad_id), "true");
    }
}