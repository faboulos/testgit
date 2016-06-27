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
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.isConnected;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

public class ListAdsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    String category = "",ville="";
    private int l = 0 ;
    private   String url = Kerawa_Parameters.PreProdURL;
    private List<Annonce> movieList=new ArrayList<>();
    private ListView listView;
    private Ads_Adapter adapter, gridAdapter;
    private String next_url="";
    private View loader, gridLoader;
    private SwipeRefreshLayout swipeRefreshLayout, gridsSwipeRefreshLayout;
    private DatabaseHelper mydb;
    private ArrayList<String> vipCount;

    EditText searchText, searchTextG;
    String added_url = "";
    String cat_name ="",keywordDecoded="",keywordEncoded="";
    private Object statusText;
    String recherche = "";
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



    private  String nom = "", first_url = "";
    private int offSet = 0;
    TextView errorText, griderrorText;
    private int lastVipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting two view when activity loads in other for switching to be possible
        listViewL1 = getLayoutInflater().inflate(R.layout.colored_fragment, null);
        gridViewL2 = getLayoutInflater().inflate(R.layout.gridslayout, null);
        setContentView(listViewL1);


        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        Krw_functions.pushOpenScreenEvent(ListAdsActivity.this, "ListAdsActivity/" + getIntent().getStringExtra("customtitle") + " > " + getIntent().getStringExtra("subcattitle"));

        listbottombar = (RelativeLayout)findViewById(R.id.footer);
        gridbottombar = (RelativeLayout)gridViewL2.findViewById(R.id.bottombargrid);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        gridsSwipeRefreshLayout = (SwipeRefreshLayout)gridViewL2.findViewById(R.id.swipe_refresh_layout);

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

        searchTextG  = (EditText)gridViewL2.findViewById(R.id.entersearch);
        searchText = (EditText)listViewL1.findViewById(R.id.entersearch);

        refresh = (ImageButton)findViewById(R.id.refresh);

        //listview buttom bar icons start
        homeButton1 = (ImageButton)listViewL1.findViewById(R.id.backHome);
        homeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(ListAdsActivity.this, AdsListActivity.class);
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
                InputMethodManager imm = (InputMethodManager)ListAdsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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

                startActivity(new Intent(ListAdsActivity.this, Search.class));


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
                Intent home = new Intent(ListAdsActivity.this, AdsListActivity.class);
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
                startActivity(new Intent(ListAdsActivity.this, Search.class));

            }
        });

        searchclose2 = (ImageButton)gridViewL2.findViewById(R.id.searchclose);
        searchclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the search bar and submit searching text
                InputMethodManager imm = (InputMethodManager)ListAdsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        //end




        String title = getIntent().getStringExtra("subcattitle");
        category = getIntent().getStringExtra("custom");
        ville = getIntent().getStringExtra("ville");
        vipCount = new ArrayList<>();
//        String customtitle =  getIntent().getStringExtra("customtitle");
//        String parent = getIntent().getStringExtra("parent");
//        String test_cat = getIntent().getStringExtra("test_cat");


//        keywordEncoded = getIntent().getStringExtra("keywordEncoded");
//        keywordDecoded = getIntent().getStringExtra("keywordDecoded");
        Log.d("tag_categorie", category+"");

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getIntent().getStringExtra("customtitle")+" > "+title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        sp = this.getSharedPreferences("lists", Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEdit1 = sp.edit();
        //initialisation ou reinitialisation de la shared preference
        mEdit1.clear();

        mydb = new DatabaseHelper(getApplicationContext());


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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        nom = prefs.getString("Kerawa-count", null);

        if(nom == null){
            this.startActivity(new Intent(this, CountryList.class));
            this.finish();
        }else{
            nom = countryName(nom, this);
            try {
                if (!ville.equals("") && !ville.equals("null")){
                    added_url = "/city/" + ville;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            //construction the first url to search ads on server
//             first_url = url + "regions/" + nom + added_url + "/ads?" + category;
            first_url = url +"list_ads?limit=5&category=" + getIntent().getStringExtra("custom")+"&country="+nom;


            //listView = (ListView)findViewById(R.id.list);
            listView.setVerticalScrollBarEnabled(false );


            adapter = new Ads_Adapter(this, R.layout.list_row, movieList);
            gridAdapter = new Ads_Adapter(this, R.layout.grid_rows, movieList);
            listView.setAdapter(adapter);
            gridView.setAdapter(gridAdapter);
            swipeRefreshLayout.setOnRefreshListener(this);
            gridsSwipeRefreshLayout.setOnRefreshListener(this);
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(false);
//                    refreshAds();
//                }
//            });




            loader = (pl.droidsonroids.gif.GifImageView)listViewL1.findViewById(R.id.loader);
            gridLoader =  (pl.droidsonroids.gif.GifImageView)gridViewL2.findViewById(R.id.loader);

            loader.setVisibility(View.VISIBLE);
            gridLoader.setVisibility(View.VISIBLE);

            //getting the entered value from search, but now there is no search
            recherche = getIntent().getStringExtra("search")!=null? getIntent().getStringExtra("search"):"";
            assert recherche != null;
            assert keywordEncoded != null;
            assert keywordDecoded != null;

            if (!recherche.equals(""))  searchText.setText(keywordDecoded);

            if (isConnected(this))
            {

                // if (!recherche.equals(""));
//                if (added_url.equals("") && recherche.equals("")) {

                if (getIntent().getStringExtra("fromsearch").equals("false")) {

//                    Toast.makeText(ListAdsActivity.this, "added_url is "+added_url, Toast.LENGTH_SHORT).show();
                    //making a JSON request to the server
//                    JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL + "regions/"
//                            + nom + "/ads/vip?"+getIntent().getStringExtra("custom"),
                    JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL +
                            "list_ads?limit=3&category="+getIntent().getStringExtra("custom")+"&country="+nom+"&onlyvip=true",
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

                                            if (arr.getJSONObject(i).get("ad_enhanced") != "" && arr.getJSONObject(i).get("ad_enhanced") != null){
                                                an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
                                                vipCount.add(arr.getJSONObject(i).get("ad_enhanced").toString());
                                                Log.d("gotVIP", "index of VIP "+i);
                                            }




                                            an.setVipCount(vipCount);
                                            ArrayList<String> images=new ArrayList<>();



                                            if (arr.getJSONObject(i).has("ad_images_urls")) {
                                                JSONArray liste_img = arr.getJSONObject(i).getJSONArray("ad_images_urls");

                                                for(int k = 0; k < liste_img.length(); k++){


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
                                            mEdit1.putString("ads_list_"+ l, arr.getJSONObject(i).toString());
                                            l= l+1 ;
                                            mEdit1.apply();
                                            movieList.add(an);

                                        }
                                    } catch (JSONException e) {
                                        // Show_Toast(getActivity(),"Annonces VI",true);
                                    }


                                    load_others(first_url);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //errore.printStackTrace();
                            NetworkResponse response = error.networkResponse;
                            //Additional cases
                            if(response != null && response.data != null)
                                switch (response.statusCode) {
                                    case 250:
                                        load_others(first_url);
                                        break;


                                }

                        }
                    })
                    {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();
                            params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(vipRequest);
                    vipRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                }else{

//                    load_others(first_url);
                    launch_search(getIntent().getStringExtra("search"));
                    search = true;

                }
                adapter.notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();
                loader.setVisibility(View.GONE);
                gridLoader.setVisibility(View.GONE);

            }else
            {
                //here if the user is not connected to the internet, retrieve the ads from the database and present it to the user
//                //setContentView(R.layout.offlineview);
//                List<Annonce> values =  mydb.getAllAdsFromCategory(cat_name) ;
//
//                //  Show_Toast(getActivity().getApplicationContext(),"getting the offline ads for :"+cat_name+"that are:"+values.size(),true);
//                if(!values.isEmpty()){
//                    for (int i = 0;i<=values.size()-1;i++) {
//
//                        movieList.add(values.get(i));
//                    }
//                }
                //Toast.makeText(ListAdsActivity.this, "Please check your connection and try again", Toast.LENGTH_SHORT).show();
                //here i am just making sure that the offline view is seen when the connection is down and refreshes the view
                listbottombar = (RelativeLayout)findViewById(R.id.offline);
                listbottombar.setVisibility(View.VISIBLE);
                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected(ListAdsActivity.this)) {
                            load_others(first_url);
                            listbottombar.setVisibility(View.INVISIBLE);
                        } else {
                            listbottombar.setVisibility(View.VISIBLE);
                        }

                    }
                });

//                adapter.notifyDataSetChanged();
//                gridAdapter.notifyDataSetChanged();


                loader.setVisibility(View.GONE);
                gridLoader.setVisibility(View.GONE);
            }


            //opening a specific ad
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Annonce anonc = (Annonce) listView.getItemAtPosition(position);
                    Krw_functions.pushAdclickedEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ad_clicked/" + anonc.getAdd_id());
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("annonce",anonc);
                    i.putExtra("next",next_url);
                    i.putExtra("position",position);
                    i.putExtra("size",l);
                    i.putExtra("category",category);
                    i.putExtra("page",curent_page);
                    i.putExtra("city", ville);
                    i.putExtra("search", search);
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(), "you selected "+ anonc.getTitle(), Toast.LENGTH_LONG).show();
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Annonce anonc = (Annonce) gridView.getItemAtPosition(position);
                    Krw_functions.pushAdclickedEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ad_clicked/" + anonc.getAdd_id());
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("annonce",anonc);
                    i.putExtra("next",next_url);
                    i.putExtra("position",position);
                    i.putExtra("size",l);
                    i.putExtra("category",category);
                    i.putExtra("page",curent_page);
                    i.putExtra("city", ville);
                    i.putExtra("search", search);
                    startActivity(i);
                }
            });
        }






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




    private void load_others(String first_url) {

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
//                        if (arr.getJSONObject(i).get("ad_enhanced") !="" && arr.getJSONObject(i).get("ad_enhanced") != null){
//                            an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
//                        }

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
                        //an.setDate(arr.getJSONObject(i).get("ad_date").toString());
                        String date_with_time = arr.getJSONObject(i).getString("ad_date").toString();
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
                        mEdit1.putString("ads_list_"+l, arr.getJSONObject(i).toString());
                        l = l + 1 ;
                        mEdit1.apply();
                        movieList.add(an);
                    }

                    loader.setVisibility(View.GONE);
                    gridLoader.setVisibility(View.GONE);

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
                                if (isConnected(ListAdsActivity.this)) {
                                    customLoadMoreDataFromApi(next_url);
                                    Krw_functions.pushOpenScreenEvent(ListAdsActivity.this, Krw_functions.Current_country(getApplicationContext()) + "/caterory=" + cat_name + "/page=" + page);

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
                    }

                    gridView.setOnScrollListener(new EndlessScrollListener() {


                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            curent_page = page;
                            if(isConnected(ListAdsActivity.this)){
                                customLoadMoreDataFromApi(next_url);
                                Krw_functions.pushOpenScreenEvent(ListAdsActivity.this, Krw_functions.Current_country(getApplicationContext()) + "/caterory=" + cat_name + "/page=" + page);
                            }
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
                                    .translationY(gridbottombar.getHeight())
                                    .alpha(0.0f)
                                    .setDuration(300);
                        }


                    });


                    //save the response inside the database

                } catch (JSONException e) {
                    try {
                        Errors_handler msg_err = new Errors_handler(ListAdsActivity.this, getApplicationContext(), "Un probleme est survenu lors du chargement des annonces.", true, (TextView) statusText, R.drawable.errore);
                        msg_err.execute(1500, 500, 6000);
                    }catch (Exception e1){}
                    loader.setVisibility(View.GONE);
                    gridLoader.setVisibility(View.GONE);
                    // Show_Toast(getActivity().getApplicationContext(),statusText.getClass().getName(),true);
                }
                adapter.notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();

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
                                        Errors_handler msg_err = new Errors_handler(ListAdsActivity.this, getApplicationContext(), "Aucune annonce dans cette categorie\r\nVeuillez changer", true, (TextView) statusText, R.drawable.errore);
                                        msg_err.execute(1500, 500, 6000);
                                        loader.setVisibility(View.GONE);
                                        gridLoader.setVisibility(View.GONE);
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
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                return params;
            }
        };

        // Creating volley request obj


        // Adding request to request queue

        try {
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);


            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }catch (Exception e){
            Show_Toast(getApplicationContext(),e.toString(),true);
        }
    }
  String search_more = "";
    //this is the function that handle search, if someone is looking for something specfic
    private void launch_search(String keyword){
        //add a tag manager with the string search

Log.d("Search key", keyword);
        String key = getIntent().getStringExtra("search");
        String catID = getIntent().getStringExtra("custom");
        String subCatID = getIntent().getStringExtra("subcategory");
        String city = getIntent().getStringExtra("ville");
        String url ="";

        Log.i("KEY", "======key====="+key+"");
        Log.i("CATID", "======catId====="+catID+"");
        Log.i("SUBCATID", "=====subcatId======"+subCatID+"");
        Log.i("CITY", "=======City===="+city+"");
        Log.i("NOM_PAYS", "=======nom===="+nom+"");
//        InputMethodManager imm = (InputMethodManager)ListAdsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(homesearchbar.getWindowToken(), 0);

        String keyword0 = "";
        try {
            keyword0 = URLEncoder.encode(keyword, "UTF-8");
            Log.i("KEYWORD_ENCODED", "=======keyword0===="+keyword0+"");

        } catch (UnsupportedEncodingException e) {
            Log.d("search error", "Wasn't able to encode "+e.getLocalizedMessage());
        }

        //==================================================================================================

        if(key==null && city==null && catID==null){
          url = "list_ads?country="+nom;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }
        else if(key!=null && city==null && catID==null){
            url = "list_ads?country="+nom+"&search="+keyword0;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }
        else if (city!=null & catID==null & key==null){
            url ="list_ads?country="+nom+"&city="+city;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }

        else if(city!=null & catID==null & key!=null){
            url = "list_ads?country="+nom+"&city="+city+"&search="+keyword0;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }

        else if(city!=null & catID != null & key==null){
            url = "list_ads?country="+nom+"&city="+city+"&category="+catID;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }
        else if(city!=null & catID != null & key!=null){
            url ="list_ads?country="+nom+"&city="+city+"&category="+catID+"&search="+keyword0;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }
        else if(city != null & subCatID != null & key == null){
            url ="list_ads?country="+nom+"&city="+city+"&category="+subCatID;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }

        else if(city != null & subCatID != null & key != null){
            url ="list_ads?country="+nom+"&city="+city+"&category="+subCatID+"&search="+keyword0;
            Log.i("URL_SEARCH", "=======URL====   "+url+"");
        }


        //==================================================================================================
        keywordDecoded = keyword;
        keywordEncoded = keyword0;

        if (Krw_functions.isConnected(ListAdsActivity.this)){
            if ((ListAdsActivity.this).getSupportActionBar() != null){
                    getSupportActionBar().setTitle(getIntent().getStringExtra("categoryTitle"));
            }
            //======================================//make a search request here============================
            //if (added_url.equals("")) {

            //making a JSON request to the server
//                JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL + "regions/"
//                        + nom + "/ads/vip?"+getIntent().getStringExtra("custom")+"&search="+keyword0+"&city="+getIntent().getStringExtra("ville"),
            //search url
            JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL
//                    +"list_ads?limit=10&category="+getIntent().getStringExtra("custom")
//                    +"&city="+getIntent().getStringExtra("ville")+"&country="+nom+"&search="+keyword0,
                    +url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject reponse) {
                            String next_page = "";
                            //-------------traitement des vip --------------------//
                            try {
                                //movieList.clear();
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

//                                    if (arr.getJSONObject(i).get("ad_enhanced") !="" && arr.getJSONObject(i).get("ad_enhanced") != null){
//                                        an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
//
//                                    }

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

                                    JSONObject meta = reponse.getJSONObject("meta");
                                    if(meta.has("next")){
                                        next_page = meta.getString("next");
                                    }
                                    mEdit1.putString("ads_list_" + l, arr.getJSONObject(i).toString());
                                    l= l+1 ;
                                    mEdit1.apply();
                                    movieList.add(an);

                                }
                            } catch (JSONException e) {
                                Log.e("errorJson", "====> "+e.getMessage());
                            }

                            if(movieList.isEmpty()){
                                noresultlayout.setVisibility(View.VISIBLE);
                               // errorText.setText("Aucune annonce trouvée.\nS\'il vous plaît changer la ville");
                                errorText.setText("Aucune annonce trouvée.\nS\'il vous plaît changer vos criteres de recherche");
                                gridnoresult.setVisibility(View.VISIBLE);
                                griderrorText.setText("Aucune annonce trouvée.\nS\'il vous plaît changer vos criteres de recherche");
                                //griderrorText.setText("Aucune annonce trouvée.\nS\'il vous plaît changer la ville");


                            }else {
                                load_others(Kerawa_Parameters.PreProdURL+"list_ads?country="+nom+"&category="+getIntent().getStringExtra("custom"));

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //errore.printStackTrace();
                    String json = null;

                    NetworkResponse response = error.networkResponse;
                    //Additional cases
                    if(response != null && response.data != null)
                        switch (response.statusCode) {
                            case 250:
                                //retrieving the error message sent by the server
                                try {
                                    json = new String(response.data, "UTF-8");
                                    json = trimMessage(json, "message");
                                }catch (Exception e){
                                    Log.d("Error", e.getMessage());
                                }finally {
                                }


                                break;


                        }

                }
            })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(vipRequest);
            vipRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //}
//            else{
//                load_others(first_url+"&search="+keyword0);
//
//            }
            loader.setVisibility(View.GONE);
            gridLoader.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            gridAdapter.notifyDataSetChanged();


        }else{
            Show_Toast(ListAdsActivity.this, "Bien vouloir remplir du texte", false);
        }
        Krw_functions.pushOpenScreenEvent(ListAdsActivity.this, Krw_functions.Current_country(ListAdsActivity.this) + "ListAdsActivity/search/" + keyword0+"/country/"+nom);

    }

    //function to load more data as the list is being scrolled
    private void customLoadMoreDataFromApi(String next_ul) {
        Log.d("Loading more", "True");
        String val = next_ul ;
        ville = getIntent().getStringExtra("ville");
        try{
            if (ville != null && !ville.equals("")) {
                added_url="/city/"+ville;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        int index = url.indexOf("?");

       // int index = val.indexOf("?");

        loader= (pl.droidsonroids.gif.GifImageView)listViewL1.findViewById(R.id.loader);
        gridLoader = (pl.droidsonroids.gif.GifImageView)gridViewL2.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        gridLoader.setVisibility(View.VISIBLE);


        if(!category.equals("")) next_ul = val.substring(0,index+1) + val.substring(index + 1, val.length());

        JsonObjectRequest movieReq1 = new JsonObjectRequest(Request.Method.GET, url+next_ul.substring(1), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {

                    //response = response.getJSONObject("data");
                    JSONArray arr = response.getJSONArray("data");
                    for (int i = 0; i <= arr.length()-1; i++) {
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

                        ArrayList<String> images = new ArrayList<>();


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
                            Log.d("server metas", meta.toString() + "," + an.getTitle());
                            while (it.hasNext()) {
                                JSONObject obj = meta.getJSONObject(it.next());
                                if (obj.get("meta_value") != null) {
                                    if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
                                        val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
                                }

                            }
                            an.setMetas(val);
                            Log.d("meta", "The meta value is "+val);
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
                        //an.setDate(arr.getJSONObject(i).get("ad_date").toString());
                        String date_with_time = arr.getJSONObject(i).getString("ad_date").toString();
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
                        mEdit1.putString("ads_list_"+l, arr.getJSONObject(i).toString());
                        l=l+1 ;
                        mEdit1.apply();
                        movieList.add(an);
                    }

                    loader.setVisibility(View.INVISIBLE);
                    gridLoader.setVisibility(View.INVISIBLE);

                    JSONObject obj = response.getJSONObject("meta");
                    if(obj.has("next"))
                    {
                        next_url = obj.getString("next");
                        if (!recherche.equals(""))  next_url = next_url.replace(keywordDecoded, keywordEncoded);
                    }
                    //save the response inside the database

                } catch (JSONException e) {
                    try {
                        Errors_handler msg_err=new Errors_handler(ListAdsActivity.this, getApplicationContext(),
                                "Un probleme est survenu lors du chargement des annonces.",true,
                                (TextView) ListAdsActivity.this.findViewById(R.id.statusText),R.drawable.errore);
                        msg_err.execute(500, 500, 6000);
                    }catch (Exception ex){
                        Log.d("Error_Handler", "error gotton is "+ex.getMessage());
                    }

                }
                adapter.notifyDataSetChanged();
                gridAdapter.notifyDataSetChanged();
                loader.setVisibility(View.INVISIBLE);
                gridLoader.setVisibility(View.INVISIBLE);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Errors_handler msg_err=new Errors_handler(ListAdsActivity.this,getApplicationContext(),
                                    "Un probleme est survenu lors du chargement des annonces.",true,
                                    (TextView) ListAdsActivity.this.findViewById(R.id.statusText),R.drawable.errore);
                            msg_err.execute(500, 500, 6000);
                        }catch (Exception e){
                            Log.d("Error_handler", "What type of error is this ===> "+e.getMessage());
                        }

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(ListAdsActivity.this).Hide_meta_data());
                return params;
            }
        };

        MySingleton.getInstance(ListAdsActivity.this).addToRequestQueue(movieReq1);
        movieReq1.setRetryPolicy(new DefaultRetryPolicy
                (
                        10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



//    private void fetchAds() {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String nom = prefs.getString("Kerawa-count", null);
//        if (nom == null) {
//            startActivity(new Intent(getApplicationContext(),CountryList.class));
//        }
//
//
//        nom = countryName(nom,getApplicationContext());
//        final String  nomfinal = countryName(nom,getApplicationContext());
////        movieList.get(0).getAdd_id()
////        Toast.makeText(ListAdsActivity.this, "first ad id is "+movieList.get(0).getAdd_id(), Toast.LENGTH_SHORT).show();
//        Log.d("First Ad is", movieList.get(vipCount.size()).getAdd_id());
//
//        final JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL+
////                "list_recents_ads_from/"+movieList.get(0).getAdd_id(),
//                "list_ads?limit=3&category="+getIntent().getStringExtra("custom")+"&country="+nom+"&onlyvip=true",
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // the response is already constructed as a JSONObject!
//                        try {
//                            //movieList.clear();
//                             JSONArray arr = response.getJSONArray("data");
//
//                            //getting normal ads
//                            for (int i = 0; i < arr.length(); i++) {
//                                Annonce an = new Annonce();
//                                an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
//                                if(arr.getJSONObject(i).has("ad_title")) {
//                                    an.setTitle(arr.getJSONObject(i).get("ad_title").toString());
//
//                                }
//                                if (arr.getJSONObject(i).has("ad_image_thumbnail_url")) {
//                                    an.setImage_thumbnail_url(arr.getJSONObject(i).get("ad_image_thumbnail_url").toString().replace("https", "http"));
//                                } else {
//                                    an.setImage_thumbnail_url("");
//                                }
//
//                                ArrayList<String> images = new ArrayList<>();
//
//
//                                if (arr.getJSONObject(i).has("ad_images_urls")) {
//                                    JSONArray liste_img=arr.getJSONObject(i).getJSONArray("ad_images_urls");
//                                    for(int k = 0; k < liste_img.length(); k++){
//                                        images.add(liste_img.get(k).toString().replace("https", "http"));
//                                    }
//                                    an.setImgList(images);
//                                }
//
//                                String price = "";
//                                if (arr.getJSONObject(i).has("ad_price")) {
//
//                                    //price = String.format("%,d", (arr.getJSONObject(i).getString("price") == null ||arr.getJSONObject(i).getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("price")));
//                                    price =price_formated(arr.getJSONObject(i).getString("ad_price"));
//                                }
//
////                                if (arr.getJSONObject(i).get("ad_enhanced") != "" && arr.getJSONObject(i).get("ad_enhanced") != null){
////                                    an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
////                                    //checking
////                                    vipCount.add(arr.getJSONObject(i).get("ad_enhanced").toString());
////                                    Log.d("gotVIP", "index of VIP "+i);
////                                }
//                                if (arr.getJSONObject(i).has("ad_short_link")) {
//                                    an.setFriendly_url(arr.getJSONObject(i).get("ad_short_link").toString());
//                                }
//                                if (arr.getJSONObject(i).has("ad_user_id")) {
//                                    an.setUser_id(arr.getJSONObject(i).getString("ad_user_id"));
//                                }
//                                String currency = "";
//                                if (arr.getJSONObject(i).has("ad_currency")) {
//
//                                    currency = arr.getJSONObject(i).get("ad_currency").toString();
//                                    an.setCurrency(currency);
//                                    if (currency.equals("null")) currency = "";
//                                }
//                                if (price.equals("0")||price.equals("")) {
//                                    price = "";
//                                    currency = "";
//                                }
//                                if (price.equals("")) an.setPrice("Aucun");
//                                else an.setPrice(price + " " + currency);
//
//                                if (arr.getJSONObject(i).has("ad_city_name")) {
//                                    an.setCity_name(arr.getJSONObject(i).get("ad_city_name").toString());
//                                }
//
//                                if (arr.getJSONObject(i).has("ad_phonenumber")) {
//                                    an.setPhone1(arr.getJSONObject(i).get("ad_phonenumber").toString());
//                                }
//
//
//
//                                if (arr.getJSONObject(i).has("ad_metas")) {
//                                    //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
//                                    JSONObject meta = arr.getJSONObject(i).getJSONObject("ad_metas");
//                                    HashMap<String,String> val = new HashMap<>();
//
//                                    Iterator<String> it =  meta.keys();
//                                    Log.d("server metas", meta.toString()+","+an.getTitle());
//                                    while (it.hasNext())
//                                    {
//                                        JSONObject obj = meta.getJSONObject(it.next());
//                                        if (obj.get("meta_value")!=null) {
//                                            if (!obj.get("meta_value").toString().equalsIgnoreCase("null"))
//                                                val.put(obj.get("meta_description").toString(), obj.get("meta_value").toString());
//                                        }
//                                    }
//                                    an.setMetas(val);
//
//                                }
//
//                                an.setStatus("normal");
//
//                                if (arr.getJSONObject(i).has("ad_description")) {
//                                    an.setDescription(arr.getJSONObject(i).get("ad_description").toString());
//                                }
//                                if (arr.getJSONObject(i).has("ad_contactemail")) {
//                                    an.setEmail(arr.getJSONObject(i).get("ad_contactemail").toString());
//                                }
//
//                                if (arr.getJSONObject(i).has("ad_category_name")) {
//                                    an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
//                                }
//
//                                if (arr.getJSONObject(i).has("ad_url")) {
//                                    an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
//                                }
//
//                                //an.setDate(arr.getJSONObject(i).get("ad_date").toString());
//                                String date_with_time = arr.getJSONObject(i).getString("ad_date").toString();
//                                String date_formated = "";
//                                if (!date_with_time.isEmpty()){
//                                    date_formated = date_with_time.substring(0, 10);
//                                }
//                                an.setDate(date_formated);
//                                movieList.remove(vipCount);
//                                movieList.add(vipCount.size(), an);
//                                next_pull = "" ;
//                                //TODO add the newly fetched ads to the preferences so that they can be moved from an activity to the other
//
//                                mEdit1.putString("ads_list_" + i, arr.getJSONObject(i).toString());
//
//                                mEdit1.apply();
//
//                                if (arr.getJSONObject(i).has("next")) {
//                                    next_pull = arr.getJSONObject(i).get("next").toString();
//                                }
//
//                            }
//
//                            int len = arr.length();
//                            for (int j = len; j <= movieList.size() - 1; j++)
//                            {
//                                mEdit1.putString("ads_list_" + j, Krw_functions.AdtoJson(movieList.get(j)));
//                            }
//
//
//                            l = movieList.size();
//                            adapter.notifyDataSetChanged();
//                            gridAdapter.notifyDataSetChanged();
//                            swipeRefreshLayout.setRefreshing(false);
//
//
//                        } catch (JSONException e) {
//                            Log.d("error", e.toString());
//                        }
//
//                        if(!next_pull.equalsIgnoreCase(""))
//                        {
//                            load_others(next_pull);
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        swipeRefreshLayout.setRefreshing(false);
//                        //errore.printStackTrace();
//                        JSONObject json = null;
//
//                        NetworkResponse response = error.networkResponse;
//                        //Additional cases
//                        if(response != null && response.data != null)
//                            switch (response.statusCode) {
//                                case 250:
//                                    Log.d("Erreur_kerawa", error.toString());
//                                    loader.setVisibility(View.INVISIBLE);
//                                    break;
//
//                            }
//
//                    }
//                }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Map<String, String> params = new HashMap<>();
//                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());
//                return params;
//            }
//            @Override
//            public String getBodyContentType() {
//                // TODO Auto-generated method stub
//                return "application/x-www-form-urlencoded";
//            }
//
//            @Override
//            public String getParamsEncoding() {
//                return "utf-8";
//            }
//
//            @Override
//            public byte[] getBody() {
//
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("sort","date");
////                params.put("category",category);
//                params.put("city","true");
//                params.put("category","true");
//                params.put("subcategory","true");
////                params.put("country",nomfinal);
//
//
//
//
//                return encodeParameters(params, getParamsEncoding());
//            }
//
//            private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
//                StringBuilder encodedParams = new StringBuilder();
//                try {
//                    for (Map.Entry<String, String> entry : params.entrySet()) {
//                        encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
//                        encodedParams.append('=');
//                        Log.d("TAG_k", entry.getKey() + "=>" + "TAG_v" + entry.getValue());
//                        encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
//                        encodedParams.append('&');
//                    }
//                    return encodedParams.toString().getBytes(paramsEncoding);
//                } catch (UnsupportedEncodingException uee) {
//                    throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
//                }
//            }
//        };
//
//
//
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
//        movieReq.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    }

    private void Load_more_from_pull(String next_pull) {


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
    private String trimMessage(String json, String key){
        String trimedString = null;
        try {
            JSONObject object = new JSONObject(json);
            trimedString = object.getString(key);
        }catch(JSONException e){
            Log.d("trimMessage()", " "+e.getMessage());
            return null;
        }
        return  trimedString;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void refreshAds(){
        if (isConnected(this))
        {
            movieList.clear();
            // if (!recherche.equals(""));
//                if (added_url.equals("") && recherche.equals("")) {

           // if (getIntent().getStringExtra("fromsearch").equals("false")) {

//                    Toast.makeText(ListAdsActivity.this, "added_url is "+added_url, Toast.LENGTH_SHORT).show();
                //making a JSON request to the server
//                    JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL + "regions/"
//                            + nom + "/ads/vip?"+getIntent().getStringExtra("custom"),
                JsonObjectRequest vipRequest = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL +
                        "list_ads?limit=3&category="+getIntent().getStringExtra("custom")+"&country="+nom+"&onlyvip=true",
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

                                        if (arr.getJSONObject(i).get("ad_enhanced") != "" && arr.getJSONObject(i).get("ad_enhanced") != null){
                                            an.setEhanced(arr.getJSONObject(i).get("ad_enhanced").toString());
                                            vipCount.add(arr.getJSONObject(i).get("ad_enhanced").toString());
                                            Log.d("gotVIP", "index of VIP "+i);
                                        }
                                        an.setVipCount(vipCount);
                                        ArrayList<String> images=new ArrayList<>();


                                        if (arr.getJSONObject(i).has("ad_images_urls")) {
                                            JSONArray liste_img = arr.getJSONObject(i).getJSONArray("ad_images_urls");
                                            for(int k = 0; k<liste_img.length();k++){
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
                                        mEdit1.putString("ads_list_"+ l, arr.getJSONObject(i).toString());
                                        l= l+1 ;
                                        mEdit1.apply();
                                        movieList.add(an);

                                    }
                                } catch (JSONException e) {
                                    // Show_Toast(getActivity(),"Annonces VI",true);
                                    Log.d("LIstAdsActivity", "JSON Parsing error:"+e.getMessage());
                                }

                                adapter.notifyDataSetChanged();
                                gridAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                                gridsSwipeRefreshLayout.setRefreshing(false);
                                load_others(first_url);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //errore.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                        gridsSwipeRefreshLayout.setRefreshing(false);
                        NetworkResponse response = error.networkResponse;
                        //Additional cases
                        if(response != null && response.data != null)
                            switch (response.statusCode) {
                                case 250:
                                    load_others(first_url);
                                    break;


                            }

                    }
                })
                {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> params = new HashMap<>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                        return params;
                    }
                };

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(vipRequest);
                vipRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//            }else{
//
////                    load_others(first_url);
//                launch_search(getIntent().getStringExtra("search"));
//                search = true;
//
//            }
            adapter.notifyDataSetChanged();
            gridAdapter.notifyDataSetChanged();
            loader.setVisibility(View.GONE);
            gridLoader.setVisibility(View.GONE);

        }

    }

    @Override
    public void onRefresh() {
        refreshAds();

    }



}
