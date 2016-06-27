package com.kerawa.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kerawa.app.Blog_Activity;
import com.kerawa.app.R;
import com.kerawa.app.utilities.Article;
import com.kerawa.app.utilities.Article_Adapter;
import com.kerawa.app.utilities.Errors_handler;
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


/**
  Classe de chargement des articles
 */
public class Blog_articles_loader extends AsyncTask<String,String,String> {

    public View v;
    ShowHide vx;
    public Context ctx ;
    public  List<Article> movieList = new ArrayList<>();
    public ListView listView;
    public Article_Adapter adapter;
    public TextView statusText;

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        vx=new ShowHide(v);
        vx.Show();
    }

    @Override
    protected String doInBackground(String... args) {
        String url = String.format("%sget_recent_posts/", Kerawa_Parameters.BlogBaseUrl);
        String response = "";
        HttpGet conn = new HttpGet(url);
        DefaultHttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<>();
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
    movieList= new ArrayList<>();

    boolean vrai=false;
        try {
             vrai=true;
            JSONObject data = new JSONObject(result);
            JSONArray data2=data.getJSONArray("posts");
            int count=data2.length();
            for(int i=count-1 ; i>= 0; i--){
                Article art=new Article();
                JSONObject jsonObject = data2.getJSONObject(i);  // get jsonObject @ i position
                art.setTitle(jsonObject.getString("title"));
                try {
                    art.setArt_plain_description(jsonObject.getString("excerpt"));
                }catch (Exception ignored){}
                try{
                    art.setArt_description(jsonObject.getString("content"));
                }catch (Exception ignored){}
                try{
                    art.setDate(jsonObject.getString("date"));
                }catch (Exception ignored){}
                try {
                    art.setImage_thumbnail_url(jsonObject.getJSONObject("thumbnail_images").getJSONObject("thumbnail").getString("url").replace("https", "http"));
                }catch (Exception ignored){}
                try {
                    art.setLargeImage(jsonObject.getJSONObject("thumbnail_images").getJSONObject("full").getString("url").replace("https", "http"));
                }catch (Exception ignored){}
                    movieList.add(art);
                }
            adapter = new Article_Adapter(ctx, R.layout.list_row,movieList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Article article = (Article) listView.getItemAtPosition(position);
                     Intent i = new Intent(ctx, Blog_Activity.class);
                     i.putExtra("title", article.getTitle());
                     i.putExtra("description", article.getArt_description());
                     i.putExtra("image", article.getLargeImage());
                     i.putExtra("date", article.getDate());
                     i.putExtra("art_url", article.getArt_url());
                     ctx.startActivity(i);
                 }
             });

        } catch (Exception e) {
            vrai=false;
            String texte_retour= Krw_functions.isConnected(ctx)?"Erreur survenue pendant le chargement":"Probleme de connexion internet";
            Errors_handler msg_err=new Errors_handler((Activity)ctx,ctx,texte_retour+"",false,statusText,R.drawable.errore);
            msg_err.execute(500, 500, 6000);
            vx.Hide();
        }finally {
         if(vrai)  adapter.notifyDataSetChanged();
         vx.Hide();
        }
     }
}
