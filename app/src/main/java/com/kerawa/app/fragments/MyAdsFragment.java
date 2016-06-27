package com.kerawa.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.DetailsActivity;
import com.kerawa.app.R;
import com.kerawa.app.utilities.Ads_Adapter;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.EndlessScrollListener;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.isConnected;
import static com.kerawa.app.utilities.Krw_functions.price_formated;


/**
 * Fragment showing a solid background color
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class MyAdsFragment extends Fragment
{

    private   String url = Kerawa_Parameters.PreProdURL;
    private ProgressDialog pDialog;
    private List<Annonce> movieList = new ArrayList<>();
    private ListView listView;
    private Ads_Adapter adapter;
    private String next_url="";
    private String real_title ;
    private View loader;
    private TextView statusText;

    public static MyAdsFragment newInstance(Bundle bundle)
    {
        MyAdsFragment colorFragment = new MyAdsFragment();

        if(bundle!=null)
        {
            colorFragment.setArguments(bundle);
        }

        return colorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // The last two arguments ensure LayoutParams are inflated properly
        final View view;
        view = inflater.inflate(R.layout.colored_fragment, container, false);
        //instanciate GTM
        Krw_functions.pushOpenScreenEvent(getActivity(), Krw_functions.Current_country(getActivity()) +"/my_ads");

        //initialise(view);
        statusText= (TextView) getActivity().findViewById(R.id.statusText);
        view.getRootView().setBackgroundColor(Color.WHITE);


            listView = (ListView) view.findViewById(R.id.list);
            adapter = new Ads_Adapter(getActivity(), R.layout.list_row, movieList);
            listView.setAdapter(adapter);





            pDialog = new ProgressDialog(this.getActivity());
            // Showing progress dialog before making http request
            pDialog.setMessage("Chargement...");
           /* pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);*/
            loader = view.findViewById(R.id.loader);
            loader.setVisibility(View.VISIBLE);
            // changing action bar color

            if (isConnected(getActivity().getApplicationContext()))
            {
                if (Krw_functions.Variables_Session(getActivity().getApplicationContext()).getuserID()!=null&&Krw_functions.isConnected(getActivity()))
                {
                    String SessionUID= String.valueOf(Krw_functions.Variables_Session(getActivity().getApplicationContext()).getuserID());
                    JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url+"subscriber/"+SessionUID+"/ads/normal", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // the response is already constructed as a JSONObject!
                            try {
                                JSONArray arr = response.getJSONArray("data");
                                for (int i = 0; i <= arr.length() - 1; i++) {
                                    Annonce an = new Annonce();
                                    an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                                    if (arr.getJSONObject(i).get("ad_title").toString().length() >= 45) {
                                        an.setTitle(arr.getJSONObject(i).get("ad_title").toString().substring(0, 45).toLowerCase() + "...");
                                        real_title = arr.getJSONObject(i).get("ad_title").toString() ;
                                    } else {
                                        an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());
                                        real_title = arr.getJSONObject(i).get("ad_title").toString() ;
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

                                    if (arr.getJSONObject(i).has("ad_telephone02")) {
                                        an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                                    }

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

                                    movieList.add(an);
                                }

                                loader.setVisibility(View.GONE);

                                JSONObject obj = response.getJSONObject("meta");
                                if (obj.has("next")) {
                                    next_url = obj.getString("next");
                                    listView.setOnScrollListener(new EndlessScrollListener() {
                                        @Override
                                        public void onLoadMore(int page, int totalItemsCount) {
                                            // Triggered only when new data needs to be appended to the list
                                            // Add whatever code is needed to append new items to your AdapterView
                                            Krw_functions.Show_Toast(getActivity(),page+"",true);
                                            if (isConnected(getActivity().getApplicationContext())) {
                                                customLoadMoreDataFromApi(view, next_url);
                                                Krw_functions.pushScrollEvent(getActivity(), Krw_functions.Current_country(getActivity()) + "/" + page);

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
                                Errors_handler msg_err=new Errors_handler(getActivity(),getActivity().getApplicationContext(),"Un probleme est survenu lors du chargement des annonces.",true, statusText,R.drawable.errore);
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
                                                    Log.d("Erreur_kerawa", error.toString()+"");
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


                    MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(movieReq);


                    movieReq.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                }
        }
            else
            {
               //you are not connected and you must be to view your offline ads
                Errors_handler msg_err=new Errors_handler(getActivity(),getActivity().getApplicationContext(),"vous devez être connecté pour visualiser vos annonces.",true, (TextView) getActivity().findViewById(R.id.statusText),R.drawable.errore);
                msg_err.execute(500, 500, 6000);

            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Annonce anonc = (Annonce) listView.getItemAtPosition(position);
                    Krw_functions.pushAdclickedEvent(getActivity(), Krw_functions.Current_country(getActivity()) + "/"+anonc.getAdd_id());

                    Intent i = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
                    i.putExtra("annonce", (Serializable) anonc);
                    getActivity().startActivity(i);

                }
            });



        return view;
    }



    private void customLoadMoreDataFromApi(View v,String next_ul) {

        JsonObjectRequest movieReq1 = new JsonObjectRequest(Request.Method.GET, url+next_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {

                    //response = response.getJSONObject("data");
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= arr.length() - 1; i++) {
                        Annonce an = new Annonce();
                        an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                        if (arr.getJSONObject(i).get("ad_title").toString().length() >= 45) {
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString().substring(0, 45).toLowerCase() + "...");
                            real_title = arr.getJSONObject(i).get("ad_title").toString() ;
                        } else {
                            an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());
                            real_title = arr.getJSONObject(i).get("ad_title").toString() ;
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

                        if (arr.getJSONObject(i).has("ad_telephone02")) {
                            an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
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

                        movieList.add(an);
                    }

                    loader.setVisibility(View.GONE);

                    JSONObject obj = response.getJSONObject("meta");
                    if(obj.has("next"))
                    {
                        next_url = obj.getString("next");
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
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getActivity()).Hide_meta_data());
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
        //ShowHide VD=new ShowHide(getActivity().findViewById(R.id.linearLayout5));
        //(new ShowHide(getActivity().findViewById(R.id.statusText))).Hide();
      //  VD.Show();
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