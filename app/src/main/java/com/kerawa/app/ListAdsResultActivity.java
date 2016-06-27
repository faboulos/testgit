package com.kerawa.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Ads_Adapter;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.EndlessScrollListener;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.newrelic.agent.android.NewRelic;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.finishThisActivity;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.isConnected;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

public class ListAdsResultActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    String category = "",ville="";
    private int l = 0 ;
    private   String url = Kerawa_Parameters.PreProdURL;
    private List<Annonce> movieList=new ArrayList<>();
    private ListView listView;
    private Ads_Adapter adapter, gridAdapter;
    private String next_url=null;
    private View loader, gridLoader;
    private SwipeRefreshLayout swipeRefreshLayout, gridsSwipeRefreshLayout;
    private DatabaseHelper mydb;
    private ArrayList<String> vipCount =  new ArrayList<>();

    EditText searchText, searchTextG;
    String added_url = "";
    String cat_name ="",keywordDecoded="",keywordEncoded="";
    private TextView statusText;
    String recherche;
    int curent_page ;
    Annonce an = new Annonce();
    SharedPreferences sp;
    SharedPreferences.Editor mEdit1 ;
    private boolean search = false;
    private String next_pull = "";
    GridView gridView;
    private View listViewL1, gridViewL2;

    ImageButton  homeButton1, profileButton1, createAdsButton1, searchclose1, searchButton, refresh;
    ImageButton  homeButton2, profileButton2, createAdsButton2, searchclose2, searchButton2;

    RelativeLayout gridbottombar, listbottombar, noresultlayout, gridnoresult;
    LinearLayout homecontainer,homesearchbar,gridhomecontainer, gridsearchbar;
    Menu menuItemGridDisplay;
    private boolean isOn = true;
    private String makeUpUrl;
    private Context context;


    private  String nom = "", first_url = "";
    private int offSet = 0;
    TextView errorText, griderrorText;
    private int lastVipId;
    private String title;
    private String subcat;
    private String country="";
    private String fromSearch;
    SharedPreferences prefs;
    String barTitle="Recherche";

    String search_url="";
    private String villeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ListAdsResultActivity.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sp = this.getSharedPreferences("lists", Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEdit1 = sp.edit();
        //initialisation ou reinitialisation de la shared preference
        mEdit1.clear();



        title = getIntent().getStringExtra("categoryTitle");
        category = getIntent().getStringExtra("custom");
        subcat = getIntent().getStringExtra("subcategory");
        ville = getIntent().getStringExtra("ville");
        villeName = getIntent().getStringExtra("villeName");
        recherche = getIntent().getStringExtra("search");
        country = countryName(prefs.getString("Kerawa-count", null), this);
        fromSearch = getIntent().getStringExtra("fromsearch");

        search_url = buildSearchUrl();
        initViews();


        query();

    }

    RelativeLayout networkLayout;
    private void query(){

        if (isConnected(context)) {
            search(search_url + "&limit=10", true);
            networkLayout.setVisibility(View.INVISIBLE);
        } else {
            networkLayout.setVisibility(View.VISIBLE);
        }

    }

    private void displayResult(){
        adapter.notifyDataSetChanged();
        gridAdapter.notifyDataSetChanged();

        loader.setVisibility(View.GONE);
        gridLoader.setVisibility(View.GONE);

        swipeRefreshLayout.setRefreshing(false);
        gridsSwipeRefreshLayout.setRefreshing(false);

        if(movieList.size() ==0) {
            noresultlayout.setVisibility(View.VISIBLE);
            gridnoresult.setVisibility(View.VISIBLE);
        }
        else {
            noresultlayout.setVisibility(View.GONE);
            gridnoresult.setVisibility(View.GONE);
        }
    }

    private String buildSearchUrl(){

        String url=  Kerawa_Parameters.PreProdURL+"list_ads?country="+country;
        if(recherche.trim().length()>0) {
            url += "&search=" + recherche;
            barTitle+=" > "+recherche;
        }
        if( category!= null) {
            url += "&category=" + category;
            barTitle+=" > "+title;
        }
        if(ville != null) {
            url += "&city=" + ville;
            barTitle+=" > "+villeName;
        }

        return url;
    }

    private void search(String url, final boolean display){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        movieList.addAll(parseAnnonce(response));

                        if(display)
                            displayResult();
                        }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context,"Erreur lors de la connexion",Toast.LENGTH_SHORT).show();
                    }
                }){

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                        return params;
                    }
                };

                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
                jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }


    private List<Annonce> parseAnnonce(JSONObject reponse){
        List<Annonce> lAnnonce = new ArrayList<Annonce>();
        try {

            JSONArray arr = reponse.getJSONArray("data");
            //pref inside the shared preference
            for (int i = 0; i < arr.length(); i++) {
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

               //if (arr.getJSONObject(i).get("ad_enhanced") !="" && arr.getJSONObject(i).get("ad_enhanced") != null){
               //   an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());

               //   }

                ArrayList<String> images=new ArrayList<>();


                if (arr.getJSONObject(i).has("ad_images_urls")) {
                    JSONArray liste_img=arr.getJSONObject(i).getJSONArray("ad_images_urls");
                    for(int k=0;k<liste_img.length();k++){
                        images.add(liste_img.get(k).toString().replace("https", "http"));
                    }
                    an.setImgList(images);
                }

               // an.setStatus("vip");

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
                    an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> "
                            + arr.getJSONObject(i).getString("ad_subcategory_name"));
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
                //an.setDate(arr.getJSONObject(i).get("ad_date").toString());
                String date_with_time = arr.getJSONObject(i).get("ad_date").toString();
                String date_formated = "";
                if (!date_with_time.isEmpty()){
                    date_formated = date_with_time.substring(0, 10);
                }
                an.setDate(date_formated);
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


                mEdit1.putString("ads_list_" + l, arr.getJSONObject(i).toString());
                l= l+1 ;
                mEdit1.apply();
                lAnnonce.add(an);

            }

            JSONObject meta = reponse.getJSONObject("meta");
            if (meta.has("next")) {
                next_url = meta.getString("next").substring(1);
            }
            else
                next_url =null;


        } catch (JSONException e) {
            Log.e("errorJson", "====> "+e.getMessage());
        }

        return lAnnonce;
    }

    private void loadMore(int page){


        loader= (pl.droidsonroids.gif.GifImageView)listViewL1.findViewById(R.id.loader);
        gridLoader = (pl.droidsonroids.gif.GifImageView)gridViewL2.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        gridLoader.setVisibility(View.VISIBLE);

        Krw_functions.pushOpenScreenEvent(ListAdsResultActivity.this, Krw_functions.Current_country(getApplicationContext()) + "/caterory=" + cat_name + "/page=" + page);
        search(Kerawa_Parameters.PreProdURL + next_url, true);
    }

    private void openAd(int position){
        Annonce anonc = (Annonce) listView.getItemAtPosition(position);
        Krw_functions.pushAdclickedEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ad_clicked/" + anonc.getAdd_id());
        Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
        i.putExtra("annonce", anonc);
        i.putExtra("next", next_url);
        i.putExtra("position", position);
        i.putExtra("size", l);
        i.putExtra("category", category);
        i.putExtra("page", curent_page);
        i.putExtra("city", ville);
        i.putExtra("search", search);
        startActivity(i);
    }

    private void initViews(){
        //getting two view when activity loads in other for switching to be possible
        listViewL1 = getLayoutInflater().inflate(R.layout.colored_fragment, null);
        gridViewL2 = getLayoutInflater().inflate(R.layout.gridslayout, null);
        setContentView(listViewL1);


        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(barTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }



        networkLayout = (RelativeLayout)findViewById(R.id.offline);

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        Krw_functions.pushOpenScreenEvent(context, "ListAdsResultActivity/" + getIntent().getStringExtra("customtitle") + " > " + getIntent().getStringExtra("subcattitle"));

        listbottombar = (RelativeLayout)findViewById(R.id.footer);
        gridbottombar = (RelativeLayout) gridViewL2.findViewById(R.id.bottombargrid);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        gridsSwipeRefreshLayout = (SwipeRefreshLayout) gridViewL2.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        gridsSwipeRefreshLayout.setOnRefreshListener(this);


        //container the UI with background codes
        homecontainer = (LinearLayout)findViewById(R.id.homecontainer);//this is the layout that holds the bottom bar icons

        homesearchbar = (LinearLayout)findViewById(R.id.homesearchbar);// this is the layout that holds the search bar on the bottom bar icon
        homesearchbar.setVisibility(View.INVISIBLE);

        noresultlayout = (RelativeLayout)findViewById(R.id.no_results);
        errorText = (TextView)findViewById(R.id.error_msg);
        gridnoresult = (RelativeLayout)gridViewL2.findViewById(R.id.no_results_grid);
        griderrorText = (TextView)gridViewL2.findViewById(R.id.errormessage);

        //linking interfeace wit the backend ie getting the ids of the views objects
        gridView = (GridView)gridViewL2.findViewById(R.id.gridView);
        listView = (ListView)listViewL1.findViewById(R.id.list);

        adapter = new Ads_Adapter(this, R.layout.list_row, movieList);
        gridAdapter = new Ads_Adapter(this, R.layout.grid_rows, movieList);
        listView.setAdapter(adapter);
        gridView.setAdapter(gridAdapter);

        listView.setVerticalScrollBarEnabled(false);
        gridView.setVerticalScrollBarEnabled(false );

        //opening a specific ad
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAd(position);
            }
        });

        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //Krw_functions.Show_Toast(getActivity(),page+"",true);
                if(next_url != null) {
                    curent_page = page;
                    loadMore(page);
                }
                // or customLoadMoreDataFromApi(totalItemsCount);
            }

            @Override
            public void onScrollUp() {
                listbottombar.animate()
                        .translationY(1)
                        .alpha(1.0f)
                        .setDuration(300);


            }

            @Override
            public void onScrollDown() {
                listbottombar.animate()
                        .translationY(listbottombar.getHeight())
                        .alpha(0.0f)
                        .setDuration(300);

            }
        });

        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //Krw_functions.Show_Toast(getActivity(),page+"",true);
                if(next_url != null) {
                    curent_page = page;
                    loadMore(page);
                }
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
            @Override
            public void onScrollUp() {
                gridbottombar.animate()
                        .translationY(1)
                        .alpha(1.0f)
                        .setDuration(300);


            }

            @Override
            public void onScrollDown() {
                gridbottombar.animate()
                        .translationY(listbottombar.getHeight())
                        .alpha(0.0f)
                        .setDuration(300);

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openAd(position);
            }
        });

        searchTextG  = (EditText)gridViewL2.findViewById(R.id.entersearch);
        searchText = (EditText)listViewL1.findViewById(R.id.entersearch);

        refresh = (ImageButton)findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });

        //listview buttom bar icons start
        homeButton1 = (ImageButton)listViewL1.findViewById(R.id.backHome);
        homeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(context, AdsListActivity.class);
                startActivity(home);
                finish();
            }
        });
        createAdsButton1 = (ImageButton)findViewById(R.id.createAds);
      /*  createAdsButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Krw_functions.Open_Connection_or_register(ListAdsActivity.this, R.id.action_add);
            }
        });*/
        searchclose1 = (ImageButton)findViewById(R.id.searchclose); // this is the button that close the search and display homecontainer
        searchclose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the search bar and submit searching text
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(homesearchbar.getWindowToken(), 0);
                // launch_search(searchText.getText().toString());
                homecontainer.setVisibility(View.VISIBLE);
                homesearchbar.setVisibility(View.INVISIBLE);
                searchText.setText("");
            }
        });

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    homecontainer.setVisibility(View.VISIBLE);
                    homesearchbar.setVisibility(View.INVISIBLE);
                    // launch_search(searchText.getText().toString());
                    searchText.setText("");
                    return true;
                }
                return false;
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int after) {
                Log.d("onTextChange", "start is " + start + " and before is " + before + " and after is " + after);
                searchclose1.setImageResource(switchIcons(start, after));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchButton = (ImageButton)listViewL1.findViewById(R.id.searchButton); // this is the search button on the container bar
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //making search bar visible and making the container invisible
//                homecontainer.setVisibility(View.INVISIBLE);
//                homesearchbar.setVisibility(View.VISIBLE);
//                InputMethodManager imm = (InputMethodManager)ListAdsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(searchText, 0);
//                searchText.requestFocus();

                finish();


            }
        });

        profileButton1 = (ImageButton)findViewById(R.id.goToProfile);
 /*       profileButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Krw_functions.Open_Connection_or_register(ListAdsActivity.this, R.id.action_account);
            }
        });*/

        //end


        //gridview  buttom bar icons start
        homeButton2 = (ImageButton)gridViewL2.findViewById(R.id.backHome);
        homeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(context, AdsListActivity.class);
                startActivity(home);
                finish();
            }
        });

        createAdsButton2 = (ImageButton)gridViewL2.findViewById(R.id.createAds);
     /*   createAdsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Krw_functions.Open_Connection_or_register(ListAdsActivity.this, R.id.action_add);
            }
        });*/

        profileButton2 = (ImageButton)gridViewL2.findViewById(R.id.goToProfile);
     /*   profileButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Krw_functions.Open_Connection_or_register(ListAdsActivity.this, R.id.action_account);
            }
        });*/

        gridhomecontainer = (LinearLayout)gridViewL2.findViewById(R.id.homecontainer);//this is the layout that holds the bottom bar icons

        gridsearchbar = (LinearLayout)gridViewL2.findViewById(R.id.homesearchbar);// this is the layout that holds the search bar on the bottom bar icon
        gridsearchbar.setVisibility(View.INVISIBLE);

        searchButton2 = (ImageButton)gridViewL2.findViewById(R.id.searchButton); // this is the search button on the container bar

        searchButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SearchButton2", "gridsearchbar is visible now");
//                gridhomecontainer.setVisibility(View.INVISIBLE);
//                gridsearchbar.setVisibility(View.VISIBLE);
//                InputMethodManager imm = (InputMethodManager)ListAdsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(searchTextG, 0);
//                searchTextG.requestFocus();
                finish();

            }
        });

        searchclose2 = (ImageButton)gridViewL2.findViewById(R.id.searchclose);
        searchclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the search bar and submit searching text
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(gridsearchbar.getWindowToken(), 0);
                //launch_search(searchTextG.getText().toString());
                gridhomecontainer.setVisibility(View.VISIBLE);
                gridsearchbar.setVisibility(View.INVISIBLE);
                searchTextG.setText("");
            }
        });
        searchTextG.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    gridhomecontainer.setVisibility(View.VISIBLE);
                    gridsearchbar.setVisibility(View.INVISIBLE);
                    // launch_search(searchTextG.getText().toString());
                    searchTextG.setText("");
                    return true;
                }
                return false;
            }
        });

        searchTextG.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int after) {
                searchclose2.setImageResource(switchIcons(start, after));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loader = (pl.droidsonroids.gif.GifImageView)listViewL1.findViewById(R.id.loader);
        gridLoader =  (pl.droidsonroids.gif.GifImageView)gridViewL2.findViewById(R.id.loader);

        loader.setVisibility(View.VISIBLE);
        gridLoader.setVisibility(View.VISIBLE);
        //end
    }

    //method that is used to switch between close and search icons
    private Integer switchIcons(int start, int after){
        Integer search;
        if(start == after){
            search = R.mipmap.ic_closewhite;
        }else{
            search = R.mipmap.search_icon;
        }
        return search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listadssearchmenu, menu);
        menuItemGridDisplay = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.switchLayouts:
                if (isOn){
                    setContentView(listViewL1);
//                    setActionIcon(isOn);
                    item.setIcon(R.mipmap.ic_testgrid);
                    isOn = false;
                }else {
                    setContentView(gridViewL2);
                    //setActionIcon(isOn);
                    item.setIcon(R.mipmap.ic_whitelist);
                    isOn = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isOn){
            //setActionIcon(isOn);
            this.isOn = false;
        }else if (!isOn){
            //setActionIcon(false);
            this.isOn = true;
        }
        return true;

    }






    @Override
    public void onRefresh() {
        movieList.clear();
        query();

    }



}
