package com.kerawa.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Model;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.ShowToastWithinService;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;

/**
 Created by Boris on 9/24/15.
 */
public class AdsDownloader extends IntentService
{

    private   String url = Kerawa_Parameters.PreProdURL;
    private final IBinder mBinder = new MyBinder();
    private String next_url = "";
    JSONArray  img_url = new JSONArray();
    private DatabaseHelper myDB ;
    private Annonce an ;
    private String thumbname = "";
    public AdsDownloader() {
        super("AdsDownloader");
    }
    private int index = 0 ;



    @Override
    protected void onHandleIntent(Intent intent) {
        // don't notify if they've played in last 24 hr
        myDB = new DatabaseHelper(getApplicationContext());
       // myDB.deleteAllADs();
        //supression et recreation du fichier des images et creation d'un nouveau vide
        ImageStorage.createStorageTemp();
        final ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        String nom = prefs.getString("Kerawa-count", null);

        nom = countryName(nom, this.getApplicationContext());
        next_url = url + "list_ads?country=" + nom;

        final ArrayList<Model> liste = generateCategoriesList();
        //ShowToastWithinService(getApplicationContext(), "starting gettting the result of the service");
        for (int j=0;j<=liste.size()-1;j++){//parcours des categories

            final String tag = liste.get(j).getTag();
            final int k = j;
            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, next_url+tag, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {

                    //response = response.getJSONObject("data");
                    JSONArray arr = response.getJSONArray("data");

                     myDB.deleteAllADsFromCategory(liste.get(k).getTitle());


                    for (int i = 0; i <= arr.length() - 1; i++) {
                        an = new Annonce();
                        an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());

                        /*File sdcard = Environment.getExternalStorageDirectory();
                        File folder = new File(sdcard.getAbsoluteFile(), "/kerawa");//the dot makes this directory hidden to the user
                        folder.mkdir();
                        File adsDir = new File(folder.getAbsoluteFile(), arr.getJSONObject(i).get("ad_id").toString());
                        if (!adsDir.exists())
                        {*/

                        if (arr.getJSONObject(i).get("ad_title").toString().length() >= 45) {
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString().substring(0, 45).toLowerCase() + "...");

                        } else {
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());
                        }
                        if (arr.getJSONObject(i).has("ad_image_thumbnail_url")) {
                            an.setImage_thumbnail_url(arr.getJSONObject(i).get("ad_image_thumbnail_url").toString().replace("https", "http"));
                            Log.d("ANDROID_TAG", arr.getJSONObject(i).get("ad_image_thumbnail_url").toString());
                        } else {
                            an.setImage_thumbnail_url("");
                        }
                        String price = "";
                        if (arr.getJSONObject(i).has("ad_price")) {

                            price = String.format("%,d", (arr.getJSONObject(i).getString("ad_price") == null || arr.getJSONObject(i).getString("ad_price").equals("null")) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("ad_price")));
                        }
                        String currency = "";
                        if (arr.getJSONObject(i).has("ad_currency")) {

                            currency = arr.getJSONObject(i).get("ad_currency").toString();
                            if (currency.equals("null")) currency = "";
                            an.setCurrency(currency);
                        }
                        if (price.equals("0")) {
                            price = "";
                            currency = "";
                            an.setCurrency(currency);
                        }
                        if (price.equals("")) an.setPrice("---");
                        else an.setPrice(price + " " + currency);

                        if (arr.getJSONObject(i).has("ad_city_name")) {
                            an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_short_link")) {
                            an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_description")) {
                            an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_url")) {
                            an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_category_name")) {
                            an.setCategory_name(arr.getJSONObject(i).get("ad_category_name").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_phonenumber")) {
                            an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_contactemail")) {
                            an.setEmail(arr.getJSONObject(i).get("ad_contactemail").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_metas")) {
                           JSONObject obj =  arr.getJSONObject(i).getJSONObject("ad_metas");
                            if(obj.has("ad_telephone02"))
                            {
                               if (obj.getString("ad_telephone02")!=null && !obj.getString("ad_telephone02").equals("null"))
                               {
                                   an.setPhone2(obj.getString("ad_telephone02"));
                               }

                            }
                        }

                        an.setDate(arr.getJSONObject(i).get("ad_date").toString());


                        if (arr.getJSONObject(i).has("ad_images_urls")) {
                            img_url = arr.getJSONObject(i).getJSONArray("ad_images_urls");
                        } else {
                            img_url = null;
                        }

                        final ArrayList<String> images=new ArrayList<>();


                        if (arr.getJSONObject(i).has("ad_images_urls")) {
                            JSONArray liste_img=arr.getJSONObject(i).getJSONArray("ad_images_urls");
                            for(int k=0;k<liste_img.length();k++){
                                images.add(liste_img.get(k).toString().replace("https", "http"));
                            }
                            an.setImgList(images);
                        }



                        String real_url = an.getImage_thumbnail_url();
                        thumbname = "thumbnail" + an.getImage_thumbnail_url().replace("https","http").substring(an.getImage_thumbnail_url().replace("https", "http").lastIndexOf("."));
                        an.setImage_thumbnail_url(thumbname);
                        myDB.insertAd(an);
                        //ShowToastWithinService(getApplicationContext(),an.getTitle()+ " inserted");
                        Log.d("thumbname", an.getImage_thumbnail_url());
                        //save ad thumbnail inside files

                        //new Image_downloader(null).execute(an.getImage_thumbnail_url(), an.getAdd_id());
                        //
                        //val.add(an);


                        File sdcard = Environment.getExternalStorageDirectory();
                        File folder = new File(sdcard.getAbsoluteFile(), ".kerawa");//the dot makes this directory hidden to the user
                        folder.mkdir();
                        File adsDir = new File(folder.getAbsoluteFile(), arr.getJSONObject(i).get("ad_id").toString());
                        if (!adsDir.exists()) {

                            imageLoader.loadImage(real_url, new SimpleImageLoadingListener() {


                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    // save it inside the file
                                    ImageStorage.saveToSdCard(loadedImage, thumbname, an.getAdd_id());

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    ImageStorage.saveToSdCard(null, thumbname, an.getAdd_id());

                                }


                            });

                            for (int j = 0; j < 1; j++) {
                                index = j;
                                //boucle de telechargement des images associees a une annonce
                                    imageLoader.loadImage( images.get(index), new SimpleImageLoadingListener() {


                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        // save it inside the file

                                        try {
                                            ImageStorage.saveToSdCard(loadedImage, "image_" + index + "_" +  images.get(index).substring( images.get(index).lastIndexOf("."))
                                                    , an.getAdd_id());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                    }


                                });

                            }
                            //}
                        }
                    }


                    JSONObject obj = response.getJSONObject("meta");
                    if (obj.has("next")) {
                        next_url = obj.getString("next");
                    }
                    //save the response inside the database

                } catch (JSONException e) {
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ShowToastWithinService(getApplicationContext(), error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                        return params;
            }
        };

        // Creating volley request obj

                // Adding request to request queue


                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //telechargement des images
        //  ShowToastWithinService(getApplicationContext(), "end loading " + liste.get(k).getTitle());
        //val = new ArrayList<Annonce>();
        }
        //ShowToastWithinService(getApplicationContext(),"Offline loading complete");

        stopSelf();     // arret du service created by Marcelin

        prefs.edit().remove("adsDowloadedTime").apply();
        prefs.edit().putLong("adsDowloadedTime", Calendar.getInstance().getTimeInMillis()).apply();


    }


    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }


    public class MyBinder extends Binder {
        AdsDownloader getService() {
            return AdsDownloader.this;
        }
    }

/*

    // arret de l"intent service   created by marcelin
    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }


    // destruction du service  created by marcelin
    @Override
    public void onDestroy() {
        super.onDestroy();
    }*/

}
