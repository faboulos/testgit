package com.kerawa.app.deepLinking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.AdsListActivity;
import com.kerawa.app.Contact_us;
import com.kerawa.app.CountryList;
import com.kerawa.app.DetailsActivity;
import com.kerawa.app.ListAdsActivity;
import com.kerawa.app.LoginActivity;
import com.kerawa.app.R;
import com.kerawa.app.WebPageLoader;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.EndlessScrollListener;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.isConnected;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

/**
 * Created by root on 4/22/16.
 */
public class HelperDeepLinking {

    private Context ctx;




    public HelperDeepLinking(Context ctx) {

        this.ctx = ctx;
    }

    public  String getCountryIdByCity(String cityId){

        String[] citiesId = ctx.getResources().getStringArray(R.array.citiesID);
        String[] countriesId = ctx.getResources().getStringArray(R.array.countriesID);

        int pos=0;

        for(int i =0;i<citiesId.length;i++){
            if (cityId.equalsIgnoreCase(citiesId[i])){
                pos=i;

            }
        }

        return countriesId[pos];
    }


    public void dispatchIntent(Intent intent) {

        final String action = intent.getAction();
        final Uri data = intent.getData();


        if (data == null) throw new IllegalArgumentException("Uri cannot be null");

        // final String scheme = data.getScheme().toLowerCase();
        // final String host = data.getHost().toLowerCase();
        final String last = data.getLastPathSegment();
        final String url = intent.getDataString();
        final String path = data.getPath();
        final List<String> segments =  data.getPathSegments();


        final int cId = path.lastIndexOf("-c");
        final int rId = path.lastIndexOf("-r");

        if (Intent.ACTION_VIEW.equals(action)) {

            // toutes les annonces d'un pays
            // List Ads by country   GET /regions/{region_id}/ads
            if(segments.size()==1 &&(path.endsWith("-r40")|| path.endsWith("-r40/") ||path.endsWith("-r41")|| path.endsWith("-r41/")||
                    path.endsWith("-r42")|| path.endsWith("-r42/")|| path.endsWith("-r43")|| path.endsWith("-r43/")||
                    path.endsWith("-r55")|| path.endsWith("-r55/")||path.endsWith("-r68")|| path.endsWith("-r68/")) )
            {
                // Toast.makeText(ctx,"bravo",Toast.LENGTH_LONG).show();
                // https://kerawa.com/cote-d-ivoire-r43/
                // recuperation du code iso du pays

                String  regionId = path.substring(rId + 2, rId + 4);


                String[] pays_id = ctx.getResources().getStringArray(R.array.pays_id);
                String[] pays_iso = ctx.getResources().getStringArray(R.array.pays_iso);

                int pos=0;
                for (int j=0;j<pays_id.length;j++){
                    if (regionId.equalsIgnoreCase(pays_id[j])){
                        pos=j;
                        break;
                    }
                }

                String iso = pays_iso[pos];


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Kerawa-count", iso);
                editor.apply();

                Intent i =  new Intent(ctx,DeepLinkingActivity.class);
                i.putExtra("PAYSID1", regionId);
                i.putExtra("title", last);
                ctx.startActivity(i);
                Toast.makeText(ctx,"ANNONCES D'UN PAYS REGION_ID   "+regionId,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCES D'UN PAYS LAST  "+last,Toast.LENGTH_LONG).show();


            }
            // toutes les annonces d'un pays et d'une ville    extraction du pays et de la ville
            else if( (segments.size()==2 && url.contains("-r") && url.contains("-c") && rId < cId )&& (!url.contains("emploi") &&
                    !url.contains("immobilier") && !url.contains("automobile") && !url.contains("services") &&
                    !url.contains("a-vendre") && !url.contains("mode") && !url.contains("high-tech"))  )
            {

                // List all Ads by a specified City in a specified Country methode API   GET /regions/{region_id}/city/{city_id}

                String  regionId = path.substring(rId + 2, rId + 4);

                String cityId = path.substring(cId + 2, cId + 5);   //extraction de l'id de la ville

                // demarrage de l'activite d'affichage de ces annonces
                Intent i =  new Intent(ctx, DeepLinkingActivity.class);
                i.putExtra("PAYSID2", regionId);   // identifiant du pays
                i.putExtra("CITYID2", cityId);     // identifiant de la ville
                i.putExtra("title", last);
                ctx.startActivity(i);

                Toast.makeText(ctx,"ANNONCES D'UN VILLE REGIONID  "+regionId,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCES D'UN VILLE  CITYID  "+cityId,Toast.LENGTH_LONG).show();

            }
            // toutes les annonces d'une categorie    extraction du pays, et de la categorie
            //list_ads?category=141&country=40
            else if ( ( segments.size()==3 && url.contains("-r") && url.contains("-c") && rId < cId) && (url.endsWith("/emploi") || url.endsWith("/immobilier") || url.endsWith("/automobile")
                    || url.endsWith("/services") || url.endsWith("/a-vendre") || url.endsWith("/mode") || url.endsWith("/high-tech") || url.endsWith("/emploi/") || url.endsWith("/immobilier/")
                    || url.endsWith("/automobile/") || url.endsWith("/services/") || url.endsWith("/a-vendre/") || url.endsWith("/mode/") || url.endsWith("/high-tech/") )    )
            {

                //https://kerawa.com/cameroun-r40/yaounde-c333/mode/


                String[] categories = ctx.getResources().getStringArray(R.array.categories);
                String[] categories_id = ctx.getResources().getStringArray(R.array.categories_id);

                int pos=0;
                for (int j=0;j<categories.length;j++){
                    if (last.equalsIgnoreCase(categories[j])){
                        pos=j;
                        break;
                    }
                }

                String idCat = categories_id[pos];
                String  regionId = path.substring(rId + 2, rId + 4);

                String cityId = path.substring(cId+2,cId+5);   //extraction de l'id de la ville

                //   String categoryId = findCatByName(categorie);

                // demarrage de l'activite d'affichage de ces annonces
                Intent i =  new Intent(ctx,DeepLinkingActivity.class);
                i.putExtra("PAYSID3", regionId);  //identifiant du pays
                i.putExtra("CATID3", idCat);     //identifiant de la categorie
                i.putExtra("title", last);
                i.putExtra("CIT3", cityId);
                ctx.startActivity(i);

/*                Toast.makeText(ctx,"COUNTRYID   "+regionId,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"CATEGORIE NAME   "+last,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"CATEGORIEID   "+idCat,Toast.LENGTH_LONG).show();*/
                Toast.makeText(ctx,"ANNONCE D'UNE CATEGORIE  COUNTRYID   "+regionId,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCE D'UNE CATEGORIE  CATEGORIE NAME   "+last, Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCE D'UNE CATEGORIE  CATEGORIEID   "+idCat, Toast.LENGTH_LONG).show();




            }
            // toutes les annonces d'une sous categorie   extraction du pays et de la sous-categorie
            // list_ads?category=141&country=40
            else  if ( (segments.size()==4 && url.contains("-r") && url.contains("-c") && rId < cId && !url.contains("user") && !url.contains("country") ) && ( url.contains("terrains-a-vendre-ou-a-louer")|| url.contains("maisons-appartements-a-louer")|| url.contains("autres-immobilier")
                    || url.contains("bureaux-locaux-commerciaux")|| url.contains("maisons-appartements-a-vendre")|| url.contains("demandes-d-emploi-cvtheque")
                    || url.contains("offres-d-emploi")||url.contains("habillement-femme")||url.contains("accessoires-de-mode")||url.contains("habillement-homme")||url.contains("autres-articles-de-mode")
                    ||url.contains("montres-bijoux-lunettes")||url.contains("habillement-enfants")||url.contains("telephones-tablettes-tactiles")||url.contains("ordinateurs")
                    ||url.contains("autres-high-tech")||url.contains("audio-video")||url.contains("consoles-jeux-videos")||url.contains("voitures")||url.contains("autres-automobile")
                    ||url.contains("pieces-detachees-accessoires")||url.contains("motos")||url.contains("evenementiel")||url.contains("offres-de-service")||url.contains("autres-services")
                    ||url.contains("recherche-de-services")||url.contains("autres-a-vendre")||url.contains("mobiliers-decoration")||url.contains("electromenager"))     )
            {

                //https://kerawa.com/cameroun-r40/douala-c334/a-vendre/electromenager



                String[] sub_categories = ctx.getResources().getStringArray(R.array.sub_categories);
                String[] sub_categories_id = ctx.getResources().getStringArray(R.array.sub_categories_id);

                int pos=0;
                for (int j=0;j<sub_categories.length;j++){
                    if (last.equalsIgnoreCase(sub_categories[j])){
                        pos=j;
                        break;
                    }
                }

                String idCat = sub_categories_id[pos];
                String  regionId = path.substring(rId + 2, rId + 4);

                String cityId = path.substring(cId+2,cId+5);   //extraction de l'id de la ville

                //  String subcategoryId = findCatByName(subcategorie);

                // demarrage de l'activite d'affichage de ces annonces
                Intent i =  new Intent(ctx,DeepLinkingActivity.class);
                i.putExtra("COUNTRYID", regionId);  //identifiant de la region
                i.putExtra("SUBCATID", idCat);     //identifiant de sous categorie
                i.putExtra("CIT",cityId);
                i.putExtra("title", last);
                ctx.startActivity(i);

                Toast.makeText(ctx,"ANNONCE D'UNE SOUSCATEGORIE  SUB_COUNTRYID   "+regionId,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCE D'UNE SOUSCATEGORIE  SUB_CATEGORIE_ID   "+idCat,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"ANNONCE D'UNE SOUSCATEGORIE  SUB_CATEGORIE NAME   "+last,Toast.LENGTH_LONG).show();


            }
            // capture d'une annonce    extraction de l'ID de l'annonce, country et si city si necessaire
            // show all add details    GET /ads/{ad_id}
            else if (segments.size()==1 && url.contains("_")&& url.contains("?") && url.contains("user") && url.contains("country"))
            {
                /*Toast.makeText(ctx,"AFFICHER UNE ANNONCE  ",Toast.LENGTH_LONG).show();*/

                //    https://kerawa.com/2745726_recherche-d-emploi?user=104995&country=ci&city=abidjan&cat=emploi&scat=offres-d-emploi&p-next=999

                String idAds = url.substring((url.indexOf("com") + 4), (url.indexOf("_")));
                /*String country = data.getQueryParameter("country");
                String city = data.getQueryParameter("city");*/

             /*   Toast.makeText(ctx,"NUMERO ANNONCE  "+idAds,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"COUNTRY ANNONCE  "+country,Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"CITY ANNONCE  " + city,Toast.LENGTH_LONG).show();*/


               /* i.putExtra("country",country);
                i.putExtra("city",city); */
                Toast.makeText(ctx,"AFFICHER L'ANNONCE NUMERO:  "+idAds,Toast.LENGTH_LONG).show();
                String first_url = Kerawa_Parameters.PreProdURL +"ad/show/"+idAds;
                //  Annonce annonce =   load_others(first_url);
//====================================================================================================================
                JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        //       Toast.makeText(ctx,"JSON RETOURNE:  "+response,Toast.LENGTH_LONG).show();
                        if (response.has("data")){

                            try {
                                Annonce an = new Annonce();
                                JSONObject arr = response.getJSONObject("data");
                              //  Toast.makeText(ctx,"JSON RETOURNE:  "+arr,Toast.LENGTH_LONG).show();
                                if(arr.has("ad_id")) {
                                    an.setAdd_id(arr.get("ad_id").toString());
                                 //   Toast.makeText(ctx,"AD_ID:  " + an.getAdd_id(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_id", "===================ad_id=====" + arr.get("ad_id").toString() + "============================");

                                }

                                if(arr.has("ad_title")) {
                                    an.setTitle(arr.get("ad_title").toString());
                                 //   Toast.makeText(ctx,"AD_TITLE:  " + an.getTitle(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_title", "===================ad_title=====" + arr.get("ad_title").toString() + "============================");

                                }
                                if (arr.has("ad_image_thumbnail_url")) {
                                    an.setImage_thumbnail_url(arr.get("ad_image_thumbnail_url").toString().replace("https", "http"));
                                 //   Toast.makeText(ctx,"AD_IMAGETHUMBAIL:  " + an.getImage_thumbnail_url(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_image_thumbnail_url", "===================ad_image_thumbnail_url=====" + arr.get("ad_image_thumbnail_url").toString() + "============================");
                                } else {
                                    an.setImage_thumbnail_url("");
                                }

                                ArrayList<String> images=new ArrayList<>();


                                if (arr.has("ad_images_urls")) {
                                    JSONArray liste_img=arr.getJSONArray("ad_images_urls");
                                    for(int k=0;k<liste_img.length();k++){
                                        images.add(liste_img.get(k).toString().replace("https", "http"));
                                      //  Toast.makeText(ctx,"AD_IMAGE:"+k+"   "+liste_img.get(k).toString(),Toast.LENGTH_LONG).show();
                                        Log.i("ad_images_urls", "===================ad_images_urls=====" + liste_img.get(k).toString() + "============================");
                                    }
                                    an.setImgList(images);
                                }



                                String price = "";
                                if (arr.has("ad_price")) {
                                    price = arr.getString("ad_price");
                                    Log.i("ad_price","===================ad_price====="+arr.get("ad_price").toString()+"========");

                                }
                                if (arr.has("ad_short_link")) {
                                    an.setFriendly_url(arr.get("ad_short_link").toString());
                                   // Toast.makeText(ctx,"AD_SHORT_LINK: "+ an.getFriendly_url(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_short_link", "===================ad_short_link=====" + arr.get("ad_short_link").toString() + "============================");
                                }
                                if (arr.has("ad_user_id")) {
                                    an.setUser_id(arr.getString("ad_user_id"));
                                  //  Toast.makeText(ctx,"AD_USER_ID: "+ an.getUser_id(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_user_id", "===================ad_user_id=====" + arr.get("ad_user_id").toString() + "========");
                                }
                                String currency = "";
                                if (arr.has("ad_currency")) {

                                    currency = arr.get("ad_currency").toString();
                                    Log.i("ad_currency", "===================ad_currency=====" + arr.get("ad_currency").toString() + "========");
                                    an.setCurrency(currency);
                                  //  Toast.makeText(ctx,"AD_CURRENCY: "+ an.getCurrency(),Toast.LENGTH_LONG).show();
                                    if (currency.equals("null")) currency = "";
                                }
                                if (price.equals("0")||price.equals("")) {
                                    price = "";
                                    currency = "";
                                }
                                if (price.equals("")) an.setPrice("Aucun");
                                else an.setPrice(price);
                               // else an.setPrice(price + " " + currency);


                              //  Toast.makeText(ctx,"AD_PRICE:"+ an.getPrice(),Toast.LENGTH_LONG).show();

                                if (arr.has("ad_city_name")) {
                                    an.setCity_name(arr.get("ad_city_name").toString());
                                //    Toast.makeText(ctx,"AD_CITY_NAME: "+ an.getCity_name(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_city_name", "===================ad_city_name=====" + arr.get("ad_city_name").toString() + "========");
                                }

                                if (arr.has("ad_phonenumber")) {
                                    an.setPhone1(arr.get("ad_phonenumber").toString());
                                //    Toast.makeText(ctx,"AD_PHONENUMBER: "+ an.getPhone1(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_phonenumber", "===================ad_phonenumber=====" + arr.get("ad_phonenumber").toString() + "========");
                                }


                                an.setStatus("normal");
                               // Toast.makeText(ctx,"STATUS: "+ an.getStatus(),Toast.LENGTH_LONG).show();

                                if (arr.has("ad_description")) {
                                    an.setDescription(arr.get("ad_description").toString());
                                 //   Toast.makeText(ctx,"AD_DESCRIPTION: "+ an.getDescription(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_description", "===================ad_description=====" + arr.get("ad_description").toString() + "========");
                                }
                                if (arr.has("ad_contactemail")) {
                                    an.setEmail(arr.get("ad_contactemail").toString());
                                 //   Toast.makeText(ctx,"AD_CONTACTEMAIL: "+ an.getEmail(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_contactemail", "===================ad_contactemail=====" + arr.get("ad_contactemail").toString() + "========");
                                }

                                if (arr.has("ad_category_name")) {
                                    an.setCategory_name(arr.getString("ad_category_name") + " -> " + arr.getString("ad_subcategory_name"));
                                   // Toast.makeText(ctx,"AD_CATEGORYNAME: "+an.getCategory_name() ,Toast.LENGTH_LONG).show();
                                   // Toast.makeText(ctx,"AD_SUBCATEGORY: "+ an.getSubcategory_name(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_category_name", "===================ad_category_name=====" + arr.get("ad_category_name").toString() + "========");
                                }
                                if (arr.has("ad_email")) {
                                    an.setEmail(arr.getString("ad_email"));
                                  //  Toast.makeText(ctx,"AD_EMAIL: "+ an.getEmail(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_email", "===================ad_email=====" + arr.get("ad_email").toString() + "========");
                                }
                                if (arr.has("ad_url")) {
                                    an.setAd_url(arr.get("ad_url").toString());
                                  //  Toast.makeText(ctx,"AD_URL: "+ an.getAd_url(),Toast.LENGTH_LONG).show();
                                    Log.i("ad_url", "===================ad_url=====" + arr.get("ad_url").toString() + "========");
                                }

                                String date1="";
                                if(arr.has("ad_date")){
                                    date1 = arr.get("ad_date").toString();
                                }


                                if (date1.equals("")){
                                    an.setDate("");
                                }else{
                                    an.setDate(date1.substring(0, 10));
                                }
                           /*     String date_with_time = arr.getString("ad_date");
                                String date_formated = "";
                                if (!date_with_time.isEmpty()){
                                    date_formated = date_with_time.substring(0, 10);
                                }
                                an.setDate(date_formated);*/
                                //Toast.makeText(ctx,"AD_DATE: "+ an.getDate(),Toast.LENGTH_LONG).show();

                               /* if (arr.has("ad_metas")) {
                                    // if (arr.getJSONObject(i).has("ad_metas")) {
                                    //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                                    JSONObject meta = arr.getJSONObject("ad_metas");
                                    HashMap<String,String> val = new HashMap<>();
                                    Toast.makeText(ctx,"=======METAS==="+ meta+"============",Toast.LENGTH_LONG).show();
                                    Iterator<String> it =  meta.keys();
                                    Log.d("server metas", meta.toString()+","+an.getTitle());
                                    while (it.hasNext())
                                    {
                                        JSONObject obj = meta.getJSONObject(it.next());
                                        if (obj.get("meta_value")!=null) {
                                            if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                                val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                            Toast.makeText(ctx,"META_VALUE: "+ obj.get("meta_value"),Toast.LENGTH_LONG).show();
                                            Log.i("ad_price","===================ad_price====="+obj.get("meta_value").toString()+"========");
                                        }
                                    }
                                    an.setMetas(val);

                                }*/

                                Intent i =  new Intent(ctx,AdsDeepLinkingActivity.class);
                                i.putExtra("annonce",an);
                                ctx.startActivity(i);
                                //  i.putExtra("idAds", idAds);       // id de l'annonce a afficher


                        /*if (i < arr.length() - 1) {
                            an.setNext(arr.getJSONObject(i + 1).get("ad_id").toString());
                            Log.i("ad_id", "===================ad_id=====" + arr.getJSONObject(i + 1).get("ad_id").toString() + "========");
                        } else {
                            an.setNext("");
                        }
                        if (i > 0) {
                            an.setPrevious(arr.getJSONObject(i - 1).get("ad_id").toString());
                            Log.i("ad_id", "===================ad_id=====" + arr.getJSONObject(i - 1).get("ad_id").toString() + "========");
                        } else {
                            an.setPrevious("");
                        }*/


                                // return  an;

                                //save the response inside the database

                            } catch (JSONException e) {


                            }


                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(ctx).Hide_meta_data());

                        return params;
                    }
                };



                MySingleton.getInstance(ctx).addToRequestQueue(movieReq);


                movieReq.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



                //====================================================================================================================
                /*Toast.makeText(ctx,"AFFICHE1:  "+annonce.getAdd_id(),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"AFFICHE2:  "+annonce.getAd_url(),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"AFFICHE3:  "+annonce.getCategory_name(),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"AFFICHE4:  "+annonce.getPrice(),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"AFFICHE5:  "+annonce.getCountry_name(),Toast.LENGTH_LONG).show();
                Toast.makeText(ctx,"AFFICHE6:  "+annonce.getCity_name(),Toast.LENGTH_LONG).show();*/




            }

            else if (segments.size()==0 && last.equalsIgnoreCase(null)&& last.equalsIgnoreCase(" "))
            {
                Toast.makeText(ctx," CHOOSE YOUR COUNTRY   ",Toast.LENGTH_LONG).show();
                ctx.startActivity(new Intent(ctx, CountryList.class));

            }

            else if (url.contains("/search"))
            {
                Toast.makeText(ctx," FAIRE LA RECHERCHE   ",Toast.LENGTH_LONG).show();

            }

              else if (url.contains("/contact"))
            {

                ctx.startActivity(new Intent(ctx, Contact_us.class));
            }
            else if (url.contains("/user/register"))
            {
                //Toast.makeText(ctx,"S'inscrire  ",Toast.LENGTH_LONG).show();

                ctx.startActivity(new Intent(ctx, LoginActivity.class));
            }
            else if (url.contains("/user/login"))
            {
               // Toast.makeText(ctx,"Se connecter  ",Toast.LENGTH_LONG).show();
                ctx.startActivity(new Intent(ctx, LoginActivity.class));

            }
            else if (url.contains("/about-us"))
            {
                Intent i = new Intent(ctx, WebPageLoader.class);
                i.putExtra("lien",url);
                ctx.startActivity(i);
            }
            else if (url.contains("/privacy"))
            {
                Intent i = new Intent(ctx, WebPageLoader.class);
                i.putExtra("lien",url);
                ctx.startActivity(i);

            }
            else if (url.contains("/terms-of-use"))
            {
                Intent i = new Intent(ctx, WebPageLoader.class);
                i.putExtra("lien",url);
                ctx.startActivity(i);

            }

            else if (url.contains("/faq-p41"))
            {
                Intent i = new Intent(ctx, WebPageLoader.class);
                i.putExtra("lien",url);
                ctx.startActivity(i);

            }

            else if (url.contains("/android"))
            {
                Intent i = new Intent(ctx, WebPageLoader.class);
                i.putExtra("lien",url);
                ctx.startActivity(i);

            }
            // redirection vers l'interface principale
            else
            {
                //  ctx.startActivity(new Intent(ctx, AdsListActivity.class));

                String  regionId = path.substring(rId + 2, rId + 4);


                String[] pays_id = ctx.getResources().getStringArray(R.array.pays_id);
                String[] pays_iso = ctx.getResources().getStringArray(R.array.pays_iso);
                int pos=0;
                for (int j=0;j<pays_id.length;j++){
                    if (regionId.equalsIgnoreCase(pays_id[j])){
                        pos=j;
                    }
                }

                String iso = pays_iso[pos];


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Kerawa-count", iso);
                editor.apply();

                Intent i =  new Intent(ctx,AdsListActivity.class);
                i.putExtra("pays",iso);
                ctx.startActivity(i);
                //Toast.makeText(ctx,"MESSAGE PAR DEFAUT  "+iso,Toast.LENGTH_LONG).show();
            }





        }





    }




    private Annonce load_others(String first_url) {

        final   Annonce an = new Annonce();


        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                //       Toast.makeText(ctx,"JSON RETOURNE:  "+response,Toast.LENGTH_LONG).show();
                if (response.has("data")){

                    try {

                        JSONObject arr = response.getJSONObject("data");
                        Toast.makeText(ctx,"JSON RETOURNE:  "+arr,Toast.LENGTH_LONG).show();
                        if(arr.has("ad_id")) {
                            an.setAdd_id(arr.get("ad_id").toString());
                            Log.i("ad_id", "===================ad_id=====" + arr.get("ad_id").toString() + "============================");

                        }

                        if(arr.has("ad_title")) {
                            an.setTitle(arr.get("ad_title").toString());
                            Log.i("ad_title", "===================ad_title=====" + arr.get("ad_title").toString() + "============================");

                        }
                        if (arr.has("ad_image_thumbnail_url")) {
                            an.setImage_thumbnail_url(arr.get("ad_image_thumbnail_url").toString().replace("https", "http"));
                            Log.i("ad_image_thumbnail_url", "===================ad_image_thumbnail_url=====" + arr.get("ad_image_thumbnail_url").toString() + "============================");
                        } else {
                            an.setImage_thumbnail_url("");
                        }

                        ArrayList<String> images=new ArrayList<>();


                        if (arr.has("ad_images_urls")) {
                            JSONArray liste_img=arr.getJSONArray("ad_images_urls");
                            for(int k=0;k<liste_img.length();k++){
                                images.add(liste_img.get(k).toString().replace("https", "http"));
                                Log.i("ad_images_urls", "===================ad_images_urls=====" + liste_img.get(k).toString() + "============================");
                            }
                            an.setImgList(images);
                        }



                        String price = "";
                        if (arr.has("ad_price")) {

                            //price = String.format("%,d", (arr.getJSONObject(i).getString("price") == null ||arr.getJSONObject(i).getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("price")));
                            // price = price_formated(arr.getJSONObject(i).getString("ad_price"));
                            price = arr.getString("ad_price");
                            Log.i("ad_price","===================ad_price====="+arr.get("ad_price").toString()+"========");

                        }
                        if (arr.has("ad_short_link")) {
                            an.setFriendly_url(arr.get("ad_short_link").toString());
                            Log.i("ad_short_link", "===================ad_short_link=====" + arr.get("ad_short_link").toString() + "============================");
                        }
                        if (arr.has("ad_user_id")) {
                            an.setUser_id(arr.getString("ad_user_id"));
                            Log.i("ad_user_id", "===================ad_user_id=====" + arr.get("ad_user_id").toString() + "========");
                        }
                        String currency = "";
                        if (arr.has("ad_currency")) {

                            currency = arr.get("ad_currency").toString();
                            Log.i("ad_currency","===================ad_currency====="+arr.get("ad_currency").toString()+"========");
                            an.setCurrency(currency);
                            if (currency.equals("null")) currency = "";
                        }
                        if (price.equals("0")||price.equals("")) {
                            price = "";
                            currency = "";
                        }
                        if (price.equals("")) an.setPrice("Aucun");
                        else an.setPrice(price + " " + currency);

                        if (arr.has("ad_city_name")) {
                            an.setCity_name(arr.get("ad_city_name").toString());
                            Log.i("ad_city_name", "===================ad_city_name=====" + arr.get("ad_city_name").toString() + "========");
                        }

                        if (arr.has("ad_phonenumber")) {
                            an.setPhone1(arr.get("ad_phonenumber").toString());
                            Log.i("ad_phonenumber", "===================ad_phonenumber=====" + arr.get("ad_phonenumber").toString() + "========");
                        }

                        if (arr.has("ad_metas")) {
                            // if (arr.getJSONObject(i).has("ad_metas")) {
                            //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                            JSONObject meta = arr.getJSONObject("ad_metas");
                            HashMap<String,String> val = new HashMap<>();

                            Iterator<String> it =  meta.keys();
                            Log.d("server metas", meta.toString()+","+an.getTitle());
                            while (it.hasNext())
                            {
                                JSONObject obj = meta.getJSONObject(it.next());
                                if (obj.get("meta_value")!=null) {
                                    if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                        val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                    Log.i("ad_price","===================ad_price====="+obj.get("meta_value").toString()+"========");
                                }
                            }
                            an.setMetas(val);

                        }
                        an.setStatus("normal");

                        if (arr.has("ad_description")) {
                            an.setDescription(arr.get("ad_description").toString());
                            Log.i("ad_description", "===================ad_description=====" + arr.get("ad_description").toString() + "========");
                        }
                        if (arr.has("ad_contactemail")) {
                            an.setEmail(arr.get("ad_contactemail").toString());
                            Log.i("ad_contactemail", "===================ad_contactemail=====" + arr.get("ad_contactemail").toString() + "========");
                        }

                        if (arr.has("ad_category_name")) {
                            an.setCategory_name(arr.getString("ad_category_name") + " -> " + arr.getString("ad_subcategory_name"));
                            Log.i("ad_category_name", "===================ad_category_name=====" + arr.get("ad_category_name").toString() + "========");
                        }
                        if (arr.has("ad_email")) {
                            an.setEmail(arr.getString("ad_email"));
                            Log.i("ad_email", "===================ad_email=====" + arr.get("ad_email").toString() + "========");
                        }
                        if (arr.has("ad_url")) {
                            an.setAd_url(arr.get("ad_url").toString());
                            Log.i("ad_url", "===================ad_url=====" + arr.get("ad_url").toString() + "========");
                        }
                        //an.setDate(arr.getJSONObject(i).get("ad_date").toString());
                        String date_with_time = arr.getString("ad_date").toString();
                        String date_formated = "";
                        if (!date_with_time.isEmpty()){
                            date_formated = date_with_time.substring(0, 10);
                        }
                        an.setDate(date_formated);

                        /*if (i < arr.length() - 1) {
                            an.setNext(arr.getJSONObject(i + 1).get("ad_id").toString());
                            Log.i("ad_id", "===================ad_id=====" + arr.getJSONObject(i + 1).get("ad_id").toString() + "========");
                        } else {
                            an.setNext("");
                        }
                        if (i > 0) {
                            an.setPrevious(arr.getJSONObject(i - 1).get("ad_id").toString());
                            Log.i("ad_id", "===================ad_id=====" + arr.getJSONObject(i - 1).get("ad_id").toString() + "========");
                        } else {
                            an.setPrevious("");
                        }*/


                        // return  an;

                        //save the response inside the database

                    } catch (JSONException e) {


                    }


                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(ctx).Hide_meta_data());

                return params;
            }
        };



        MySingleton.getInstance(ctx).addToRequestQueue(movieReq);


        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        return  an;


    }



}
