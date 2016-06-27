package com.kerawa.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.AdsListActivity;
import com.kerawa.app.CountryList;
import com.kerawa.app.DetailsActivity;
import com.kerawa.app.ListAdsActivity;
import com.kerawa.app.R;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Ads_Adapter;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.EndlessScrollListener;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.ShowHide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.isConnected;
import static com.kerawa.app.utilities.Krw_functions.price_formated;
/**
 * @author boris Nguimmo & martin kwedi
 * Editted by etinge edward mabian
 */
public class ColorFragment extends Fragment
{
    String category = "",ville="";
    private int l = 0 ;
    private   String url = Kerawa_Parameters.PreProdURL;
    private ProgressDialog pDialog;
    private List<Annonce> movieList=new ArrayList<>();
    private ListView listView;
    private Ads_Adapter adapter;
    private String next_url="";
    private View loader;

    private DatabaseHelper mydb;

    ImageView okButton;
    EditText searchText;
    String added_url = "";
    String cat_name ="",keywordDecoded="",keywordEncoded="";
    private Object statusText;
    String recherche = "";
    int curent_page ;
    SharedPreferences sp;
    SharedPreferences.Editor mEdit1 ;
    private boolean search = false;
    public static ColorFragment newInstance(Bundle bundle)
    {
        ColorFragment colorFragment = new ColorFragment();

        if(bundle!=null)
        {
            colorFragment.setArguments(bundle);
        }

        return colorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        startActivity(new Intent(getActivity().getApplicationContext(), ListAdsActivity.class));
        getActivity().finish();
        // The last two arguments ensure LayoutParams are inflated properly
        final View view;
        view = inflater.inflate(R.layout.colored_fragment, container, false);
        //instanciate GTM
        //initialise(view);
        //SharedPreferences pref = getActivity().getSharedPreferences()
        sp = getActivity().getSharedPreferences("lists",Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEdit1 = sp.edit();
        //initialisation ou reinitialisation de la shared preference
        mEdit1.clear();


        mydb=new DatabaseHelper(getActivity().getApplicationContext());
        statusText= getActivity().findViewById(R.id.statusText);
        view.getRootView().setBackgroundColor(Color.WHITE);
        category = getArguments().getString("categorie");
        //category = getArguments().getString("custom");
        keywordEncoded = getArguments().getString("keywordEncoded");
        keywordDecoded = getArguments().getString("keywordDecoded");
        Log.d("tag_categorie",category);

        ArrayList<Model> cname = generateCategoriesList();
        int k =0;
        while(k<=cname.size()-1)
        {

          if (cname.get(k).getTag().equalsIgnoreCase(category))
          {
              cat_name = cname.get(k).getTitle() ;
              Log.d("Model_from_category",cat_name);
              break;
          }
           k++;
        }

        ville=getArguments().getString("ville");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String nom = prefs.getString("Kerawa-count", null);

        if (nom == null) {

            this.startActivity(new Intent(getActivity().getApplicationContext(), CountryList.class));
            this.getActivity().finish();

        } else {

            nom = countryName(nom, getActivity().getApplicationContext());
            try {
                if (!ville.equals("") && !ville.equals("null")) added_url = "/city/" + ville;
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String first_url = url + "country/" + nom + added_url + "/list_ads?" + category;


            listView = (ListView) view.findViewById(R.id.list);
            adapter = new Ads_Adapter(getActivity(), R.layout.list_row, movieList);
            listView.setAdapter(adapter);

//            okButton = (ImageView) view.findViewById(R.id.okButton);
//            searchText = (EditText) view.findViewById(R.id.searchText);

//            okButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String keyword = searchText.getText().toString();
//                    launch_search(keyword);
//                    search = true ;
//                }
//            });



            pDialog = new ProgressDialog(this.getActivity());
            // Showing progress dialog before making http request
            pDialog.setMessage("Chargement...");
           /* pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);*/
            loader = view.findViewById(R.id.loader);
            loader.setVisibility(View.VISIBLE);
            // changing action bar color
            recherche =getArguments().getString("search")!=null?getArguments().getString("search"):"";
            assert recherche != null;
            assert keywordEncoded != null;
            assert keywordDecoded != null;
            if (!recherche.equals(""))  searchText.setText(keywordDecoded);
           // Show_Toast(getActivity(),keywordDecoded+" "+keywordEncoded,true);
            if (isConnected(getActivity().getApplicationContext()))
            {

                if (!recherche.equals("")) new ShowHide(getActivity().findViewById(R.id.linearLayout5)).Hide();
                if (added_url.equals("") && recherche.equals("")) {
                    JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL + "regions/" + nom + "/ads/vip?"+getArguments().getString("categorie"),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject reponse) {
                 //-------------traitement des vip --------------------//
                                    try {
                                        JSONArray arr = reponse.getJSONArray("data");
                                        //pref inside the shared preference


                                        for (int i = 0; i <= arr.length() - 1; i++) {
                                            Annonce an = new Annonce();
                                            an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                                            if (arr.getJSONObject(i).has("ad_title")) {
                                                an.setTitle(arr.getJSONObject(i).get("ad_title").toString());
                                             }
                                            if (arr.getJSONObject(i).has("ad_image_thumbnail_url")) {
                                                an.setImage_thumbnail_url(arr.getJSONObject(i).get("ad_image_thumbnail_url").toString().replace("https", "http"));
                                            } else {
                                                an.setImage_thumbnail_url("");
                                            }

                                            if (arr.getJSONObject(i).get("ad_enhanced")!=""&&arr.getJSONObject(i).get("ad_enhanced")!=null){
                                                an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
                                            }

                                            ArrayList<String> images=new ArrayList<>();


                                            if (arr.getJSONObject(i).has("ad_images_urls")) {
                                                JSONArray liste_img=arr.getJSONObject(i).getJSONArray("ad_images_urls");
                                                for(int k=0;k<liste_img.length();k++){
                                                    images.add(liste_img.get(k).toString().replace("https", "http"));
                                                }
                                                an.setImgList(images);
                                            }

                                            an.setStatus("vip");

                                            String price = "";
                                            if (arr.getJSONObject(i).has("ad_price")) {

                                                //price = String.format("%,d", (arr.getJSONObject(i).getString("price") == null ||arr.getJSONObject(i).getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("price")));
                                                price =price_formated(arr.getJSONObject(i).getString("ad_price"));
                                            }
                                            if (arr.getJSONObject(i).has("ad_short_link")) {
                                                an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
                                            }

                                            String currency = "";
                                            if (arr.getJSONObject(i).has("ad_currency")) {
                                                currency = arr.getJSONObject(i).get("ad_currency").toString();
                                                an.setCurrency(currency);
                                                if (currency.equals("null")) currency = "";
                                            }
                                            if (price.equals("0")||price.equals("")) {
                                                price = "";
                                                currency = "";
                                            }
                                            if (price.equals("")) an.setPrice("N/A");
                                            else an.setPrice(price + " " + currency);

                                            if (arr.getJSONObject(i).has("ad_city_name")) {
                                                an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
                                            }

                                            if (arr.getJSONObject(i).has("ad_phonenumber")) {
                                                an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
                                            }

                                            try {
                                                //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                                                JSONObject meta = arr.getJSONObject(i).getJSONObject("ad_metas");
                                                HashMap<String,String> val = new HashMap<>();

                                                Iterator<String> it =  meta.keys();
                                                Log.d("server metas", meta.toString()+","+an.getTitle());
                                                while (it.hasNext())
                                                {
                                                    JSONObject obj = meta.getJSONObject(it.next());
                                                    if (obj.get("meta_value")!=null) {
                                                        if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                                            val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                                    }
                                                }
                                                an.setMetas(val);
                                            }catch (Exception ignored){}

                                            if (arr.getJSONObject(i).has("ad_description")) {
                                                an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
                                            }
                                            if (arr.getJSONObject(i).has("ad_contactemail")) {
                                                an.setEmail(arr.getJSONObject(i).get("ad_contactemail").toString());
                                            }

                                            if (arr.getJSONObject(i).has("ad_category_name")) {
                                                an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
                                            }
                                            if (arr.getJSONObject(i).has("ad_email")) {
                                                an.setEmail(arr.getJSONObject(i).getString("ad_email"));
                                            }
                                            if (arr.getJSONObject(i).has("ad_user_id")) {
                                                an.setUser_id(arr.getJSONObject(i).getString("ad_user_id"));
                                            }
                                            if (arr.getJSONObject(i).has("ad_url")) {
                                                an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                                            }
                                            an.setDate(arr.getJSONObject(i).get("ad_date").toString());

                                            if (i < arr.length() - 1) {
                                                an.setNext(arr.getJSONObject(i + 1).get("ad_id").toString());
                                            } else {
                                                an.setNext("");
                                            }
                                            if (i > 0) {
                                                an.setPrevious(arr.getJSONObject(i - 1).get("ad_id").toString());
                                            } else {
                                                an.setPrevious("");
                                            }
                                            mEdit1.putString("ads_list_"+l, arr.getJSONObject(i).toString());
                                            l= l+1 ;
                                            mEdit1.apply();
                                            movieList.add(an);
                                        }
                                    } catch (JSONException e) {
                                        // Show_Toast(getActivity(),"Annonces VI",true);
                                    }

                              load_others(first_url,view);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //errore.printStackTrace();
                            JSONObject json = null;

                            NetworkResponse response = error.networkResponse;
                            //Additional cases
                            if(response != null && response.data != null)
                                switch (response.statusCode) {
                                    case 500:
                                        load_others(first_url,view);
                                    break;


                                }

                        }
                    })
                    {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getActivity()).Hide_meta_data());

                            return params;
                        }
                    };
                    MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(vipRequest);
                    vipRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            }else{
                        load_others(first_url,view);
                    }

            }
            else
            {
               List<Annonce> values =  mydb.getAllAdsFromCategory(cat_name) ;

              //  Show_Toast(getActivity().getApplicationContext(),"getting the offline ads for :"+cat_name+"that are:"+values.size(),true);
                for (int i = 0;i<=values.size()-1;i++) {

                   movieList.add(values.get(i));
                  }
                adapter.notifyDataSetChanged();
                loader.setVisibility(View.GONE);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Annonce anonc = (Annonce) listView.getItemAtPosition(position);
                    Krw_functions.pushAdclickedEvent(getActivity(), Krw_functions.Current_country(getActivity()) + "/ad_clicked/" + anonc.getAdd_id());
                    Intent i = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
                    i.putExtra("annonce",anonc);
                    i.putExtra("next",next_url);
                    i.putExtra("position",position);
                    i.putExtra("size",l);
                    i.putExtra("category",category);
                    i.putExtra("page",curent_page);
                    i.putExtra("city",ville);
                    i.putExtra("search", search);
                    getActivity().startActivity(i);

                }
            });

        }

        return view;
    }
    private void load_others(String first_url, final View view) {
        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= arr.length() - 1; i++) {
                        Annonce an = new Annonce();
                        an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                        if(arr.getJSONObject(i).has("ad_title")) {
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString());

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
                        if (arr.getJSONObject(i).has("ad_short_link")) {
                            an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_user_id")) {
                            an.setUser_id(arr.getJSONObject(i).getString("ad_user_id"));
                        }
                        String currency = "";
                        if (arr.getJSONObject(i).has("ad_currency")) {

                            currency = arr.getJSONObject(i).get("ad_currency").toString();
                            an.setCurrency(currency);
                            if (currency.equals("null")) currency = "";
                        }
                        if (price.equals("0")||price.equals("")) {
                            price = "";
                            currency = "";
                        }
                        if (price.equals("")) an.setPrice("Aucun");
                        else an.setPrice(price + " " + currency);

                        if (arr.getJSONObject(i).has("ad_city_name")) {
                            an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
                        }

                        if (arr.getJSONObject(i).has("ad_phonenumber")) {
                            an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
                        }

                        if (arr.getJSONObject(i).has("ad_metas")) {
                            //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                            JSONObject meta = arr.getJSONObject(i).getJSONObject("ad_metas");
                            HashMap<String,String> val = new HashMap<>();

                            Iterator<String> it =  meta.keys();
                            Log.d("server metas", meta.toString()+","+an.getTitle());
                            while (it.hasNext())
                            {
                                JSONObject obj = meta.getJSONObject(it.next());
                                if (obj.get("meta_value")!=null) {
                                    if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                        val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                }
                            }
                            an.setMetas(val);

                        }
                        an.setStatus("normal");

                        if (arr.getJSONObject(i).has("ad_description")) {
                            an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_contactemail")) {
                            an.setEmail(arr.getJSONObject(i).get("ad_contactemail").toString());
                        }

                        if (arr.getJSONObject(i).has("ad_category_name")) {
                            an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
                        }
                        if (arr.getJSONObject(i).has("ad_email")) {
                            an.setEmail(arr.getJSONObject(i).getString("ad_email"));
                        }
                        if (arr.getJSONObject(i).has("ad_url")) {
                            an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                        }
                        an.setDate(arr.getJSONObject(i).get("ad_date").toString());

                        if (i < arr.length() - 1) {
                            an.setNext(arr.getJSONObject(i + 1).get("ad_id").toString());
                        } else {
                            an.setNext("");
                        }
                        if (i > 0) {
                            an.setPrevious(arr.getJSONObject(i - 1).get("ad_id").toString());
                        } else {
                            an.setPrevious("");
                        }
                        mEdit1.putString("ads_list_"+l, arr.getJSONObject(i).toString());
                        l=l+1 ;
                        mEdit1.apply();
                        movieList.add(an);
                    }

                    loader.setVisibility(View.GONE);

                    JSONObject obj = response.getJSONObject("meta");
                    if (obj.has("next")) {

                        next_url = obj.getString("next");
                       if (!recherche.equals("")) {
                           next_url = next_url.replace(keywordDecoded, keywordEncoded);
                       }
                        listView.setOnScrollListener(new EndlessScrollListener() {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount) {
                                // Triggered only when new data needs to be appended to the list
                                // Add whatever code is needed to append new items to your AdapterView
                                //Krw_functions.Show_Toast(getActivity(),page+"",true);
                                curent_page = page ;
                                if (isConnected(getActivity().getApplicationContext())) {
                                    customLoadMoreDataFromApi(view, next_url);
                                    Krw_functions.pushOpenScreenEvent(getActivity(), Krw_functions.Current_country(getActivity()) +"/caterory="+cat_name +"/page=" + page);

                                }
                                // or customLoadMoreDataFromApi(totalItemsCount);
                            }
                            @Override
                            public void onScrollUp() {

                            }

                            @Override
                            public void onScrollDown() {

                            }

                        });
                    }
                    //save the response inside the database

                } catch (JSONException e) {
                    Errors_handler msg_err=new Errors_handler(getActivity(),getActivity().getApplicationContext(),"Un probleme est survenu lors du chargement des annonces.",true, (TextView) statusText,R.drawable.errore);
                    msg_err.execute(1500, 500, 6000);
                    new ShowHide(loader).Hide();
                    // Show_Toast(getActivity().getApplicationContext(),statusText.getClass().getName(),true);
                }
                adapter.notifyDataSetChanged();

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //errore.printStackTrace();
                        JSONObject json = null;

                        NetworkResponse response = error.networkResponse;
                        //Additional cases
                        if(response != null && response.data != null)
                            switch (response.statusCode) {
                                case 500:
                                    try {
                                        Errors_handler msg_err = new Errors_handler(getActivity(), getActivity().getApplicationContext(), "Aucune annonce dans cette categorie\r\nVeuillez changer", true, (TextView) statusText, R.drawable.errore);
                                        msg_err.execute(1500, 500, 6000);
                                        new ShowHide(loader).Hide();
                                    } catch (Exception ignored) {
                                        Log.d("Erreur_kerawa", error.toString() + "");
                                    }
                                    //  Show_Toast(getActivity().getApplicationContext(),statusText.getClass().getName(),true);
                                    break;


                            }

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getActivity()).Hide_meta_data());

                return params;
            }
        };

        // Creating volley request obj


        // Adding request to request queue

        try {
            MySingleton.getInstance(getActivity()).addToRequestQueue(movieReq);


            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }catch (Exception e){
            Show_Toast(getActivity(),e.toString(),true);
        }
    }
    private void launch_search(String keyword){
        //add a tag manager with the string search
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        Bundle bundle = new Bundle();
        String keyword0 = keyword;
        try {
            keyword = URLEncoder.encode(keyword,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bundle.putString("categorie", "0,desc," + keyword);
        bundle.putString("keywordDecoded",keyword0);
        bundle.putString("keywordEncoded",keyword);
        bundle.putString("search","search");
        if (!keyword.trim().equals("")){
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null){
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(String.format("Recherche: %s", keyword0));
            }
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
            }else{
            Show_Toast(getActivity(), "Bien vouloir remplir du texte", false);
        }
        Krw_functions.pushOpenScreenEvent(getActivity(), Krw_functions.Current_country(getActivity()) + "/search/"+keyword.replace(" ","_"));

    }

    private void customLoadMoreDataFromApi(View v,String next_ul) {
        String val = next_ul ;
        ville = getArguments().getString("ville");
        try{
            if (ville != null&& !ville.equals("")) {
                added_url="/city/"+ville;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        int index = val.indexOf("?");
        loader= v.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        if(!category.equals("")) next_ul = val.substring(0,index+1)+val.substring(index+1,val.length()) ;
        JsonObjectRequest movieReq1 = new JsonObjectRequest(Request.Method.GET, url+ next_ul.substring(1), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {

                    //response = response.getJSONObject("data");
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= arr.length() - 1; i++) {
                        Annonce an = new Annonce();
                        an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());

                        if (arr.getJSONObject(i).has("ad_title")){
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString());
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
                        if (arr.getJSONObject(i).has("ad_category_name")) {
                            an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
                        }
                        an.setStatus("normal");

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
                        if (price.equals("0")||price.equals("")) {
                            price = "";
                            currency = "";
                        }
                        if (arr.getJSONObject(i).has("ad_user_id")) {
                            an.setUser_id(arr.getJSONObject(i).getString("ad_user_id"));
                        }
                        if (price.equals("")) an.setPrice("Aucun");
                        else an.setPrice(price + " " + currency);

                        if (arr.getJSONObject(i).has("ad_city_name")) {
                            an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_short_link")) {
                            an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_phonenumber")) {
                            an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
                        }

                        if (arr.getJSONObject(i).has("ad_metas")) {
                            //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                            JSONObject meta = arr.getJSONObject(i).getJSONObject("ad_metas");
                            HashMap<String,String> val = new HashMap<>();

                            Iterator<String> it =  meta.keys();
                            Log.d("server metas", meta.toString()+","+an.getTitle());
                            while (it.hasNext()) {
                                JSONObject obj = meta.getJSONObject(it.next());
                                if (obj.get("meta_value") != null) {
                                    if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                        val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                }

                             }
                             an.setMetas(val);
                            }

                        if (arr.getJSONObject(i).has("ad_description")) {
                            an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_contactemail")) {
                            an.setEmail(arr.getJSONObject(i).get("ad_contactemail").toString());
                        }
                        if (arr.getJSONObject(i).has("ad_email")) {
                            an.setEmail(arr.getJSONObject(i).getString("ad_email"));
                        }


                        if (arr.getJSONObject(i).has("ad_url")) {
                            an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                        }
                        an.setDate(arr.getJSONObject(i).get("ad_date").toString());

                        if (i < arr.length() - 1) {
                            an.setNext(arr.getJSONObject(i + 1).get("ad_id").toString());
                        } else {
                            an.setNext("");
                        }
                        if (i > 0) {
                            an.setPrevious(arr.getJSONObject(i - 1).get("ad_id").toString());
                        } else {
                            an.setPrevious("");
                        }
                        mEdit1.putString("ads_list_"+l, arr.getJSONObject(i).toString());
                        l=l+1 ;
                        mEdit1.apply();
                        movieList.add(an);
                    }

                    loader.setVisibility(View.GONE);

                    JSONObject obj = response.getJSONObject("meta");
                    if(obj.has("next"))
                    {
                        next_url = obj.getString("next");
                        if (!recherche.equals(""))  next_url=next_url.replace(keywordDecoded, keywordEncoded);
                    }
                    //save the response inside the database

                } catch (JSONException e) {
                    Errors_handler msg_err=new Errors_handler(getActivity(),getActivity().getApplicationContext(),"Un probleme est survenu lors du chargement des annonces.",true, (TextView) getActivity().findViewById(R.id.statusText),R.drawable.errore);
                    msg_err.execute(500, 500, 6000);
                }
                adapter.notifyDataSetChanged();
                loader.setVisibility(View.GONE);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Errors_handler msg_err=new Errors_handler(getActivity(),getActivity().getApplicationContext(),"Un probleme est survenu lors du chargement des annonces.",true, (TextView) getActivity().findViewById(R.id.statusText),R.drawable.errore);
                        msg_err.execute(500, 500, 6000);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(getActivity()).Hide_meta_data());
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(movieReq1);
        movieReq1.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();


//            ShowHide VD=new ShowHide(getActivity().findViewById(R.id.linearLayout5));
//            (new ShowHide(getActivity().findViewById(R.id.statusText))).Hide();
           // if (recherche.equals(""))   VD.Show();
            //if (!recherche.equals("")) new ShowHide(getActivity().findViewById(R.id.linearLayout5)).Hide();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        search = false ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }



}