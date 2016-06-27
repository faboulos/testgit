package com.kerawa.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.kerawa.app.DetailsActivity;
import com.kerawa.app.R;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.price_formated;

/**
  Classe de chargement des articles
 */
public class VipAds_loader extends AsyncTask<String,String,String> implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener  {

    public View v;
    ShowHide vx;
    public Context ctx ;
    public SliderLayout mDemoSlider;
    public String server_url;
    SharedPreferences prefs;
    private int size = 0 ;
    private SharedPreferences sp ;
    SharedPreferences.Editor mEdit1 ;
    @Override
    protected void onPreExecute() {
        sp = ctx.getSharedPreferences("lists_vip", Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEdit1 = sp.edit();


        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        super.onPreExecute();
        vx=new ShowHide(v);
        vx.Show();
    }

    @Override
    protected String doInBackground(String... args) {
        String url = server_url;
        String response = "";

        HttpGet conn = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        List<NameValuePair> urlParameters = new ArrayList<>();
        HttpParams httpParameters = new BasicHttpParams();
        conn.setHeader("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(ctx).Hide_meta_data());
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
       /* if(prefs.getLong("adsDowloadedTime",0)==0) {
            Intent downloader = new Intent(ctx, AdsDownloader.class);
            ctx.startService(downloader);
        }*/
        vx.Hide();
        Krw_functions.saveVipSession(ctx, result);
       // Log.d("Cle PROD",new Kerawa_Parameters(ctx).Yes_sir());
        show_vip(result);
    }

    private void show_vip(String result) {
        HashMap<Integer, Annonce> url_maps = new HashMap<>();
      final TextView  statusText= (TextView) ((Activity) ctx).findViewById(R.id.statusText);
         boolean vrai = false;
        try {
            vrai = true;
            JSONObject data = new JSONObject(result);
            JSONArray arr = data.getJSONArray("data");
            for (int i = arr.length() - 1; i >= 0; i--) {
                Annonce an = new Annonce();
                an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                if(arr.getJSONObject(i).get("ad_title").toString().length()>=20){
                    an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());}
                else {
                    an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());
                }
                if (arr.getJSONObject(i).has("ad_image_thumbnail_url")) {
                    an.setImage_thumbnail_url(arr.getJSONObject(i).get("ad_image_thumbnail_url").toString().replace("https", "http"));
                } else {
                    an.setImage_thumbnail_url("");
                }

                ArrayList<String> images=new ArrayList<>();


                if (arr.getJSONObject(i).has("ad_images_urls")) {
                    JSONArray liste_img=arr.getJSONObject(i).getJSONArray("ad_images_urls");
                    for(int k=0;k<liste_img.length();k++){
                        images.add(liste_img.get(k).toString().replace("https", "http"));
                    }
                    an.setImgList(images);
                }



                String price = "";
                if (arr.getJSONObject(i).has("ad_price")) {

                    //price = String.format("%,d", (arr.getJSONObject(i).getString("price") == null ||arr.getJSONObject(i).getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("price")));
                    price =price_formated(arr.getJSONObject(i).getString("ad_price"));
                }
                String currency = "";
                if (arr.getJSONObject(i).has("ad_currency")) {

                    currency = arr.getJSONObject(i).get("ad_currency").toString();
                    an.setCurrency(currency);
                    if (currency.equals("null")) currency = "";
                }
                if (price.equals("0") || price.equals("") || price.equals("null")) {
                    price = "";
                    currency = "";
                }
                if (price.equals("")) an.setPrice("Aucun");
                else an.setPrice(price);

                if (arr.getJSONObject(i).has("ad_city_name")) {
                    an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
                }

                if (arr.getJSONObject(i).has("ad_phonenumber")) {
                    an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
                }

                if (arr.getJSONObject(i).has("ad_telephone02")) {
                    an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                }

                if (arr.getJSONObject(i).has("ad_description")) {
                    an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
                }

                if (arr.getJSONObject(i).has("ad_category_name")) {
                    an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
                }
                if (arr.getJSONObject(i).has("ad_short_link")) {
                    an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
                }



                if (arr.getJSONObject(i).has("ad_url")) {
                    an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                }
                an.setDate(arr.getJSONObject(i).get("ad_date").toString());

                int _jj = i+1;
                url_maps.put(_jj, an);

                mEdit1.putString("ads_list_"+i, arr.getJSONObject(i).toString());
                mEdit1.apply();
            }

            List<Integer> peopleByAge = new ArrayList<>(url_maps.keySet());

            Collections.sort(peopleByAge);
            size = url_maps.keySet().size() ;
            for(Integer name : url_maps.keySet()){
                TextSliderView textSliderView = new TextSliderView(ctx);
                // initialize a SliderLayout
                Annonce ann= url_maps.get(name);
                textSliderView
                        .description(ann.getTitle().substring(0, (ann.getTitle().length() >= 20) ? 20 : ann.getTitle().length() - 1) + "... ( "+ price_formated(ann.getPrice()) + " " + (!(price_formated(ann.getPrice()).trim()).equals("") ?ann.getCurrency():"")  + " )")
                                .image(url_maps.get(name).getImgList().get(0).replace("https", "http"))
                                .setScaleType(BaseSliderView.ScaleType.CenterInside).setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", url_maps.get(name).getTitle());
                textSliderView.getBundle().putSerializable("annonce", url_maps.get(name));
                textSliderView.getBundle().putInt("position", name);
                mDemoSlider.addSlider(textSliderView);
            }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Left_Top);
            mDemoSlider.setDuration(4000);
            mDemoSlider.stopAutoCycle();
//            mDemoSlider.setCurrentPosition(0,true);
        } catch (JSONException e) {
          Errors_handler msg_err = new Errors_handler((Activity)ctx,ctx,"Le chargement des annonces recommandées a échoué.",true,statusText,R.drawable.errore);
          msg_err.execute(500,500,6000);
        }

    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Annonce ann= (Annonce) slider.getBundle().getSerializable("annonce");
        Intent i = new Intent(ctx, DetailsActivity.class);
        i.putExtra("annonce", ann);
        i.putExtra("size",size);
        i.putExtra("vip_list",true);
        i.putExtra("position",slider.getBundle().getInt("position"));
        ctx.startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
