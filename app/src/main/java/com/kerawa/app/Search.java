package com.kerawa.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.SubCatSAdapter;
import com.newrelic.agent.android.NewRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Search extends AppCompatActivity {
    private ArrayList<Model> cities, categories, subcategories;
    Spinner categoryspinner, cityspinner, subcatsp;
    private SharedPreferences prefs;
    Button searchitem;
    SubCatSAdapter  adapter;
    String categorySelected = "", catTitle = "", citySelected = "", subcatselected;
    EditText searchingword;
    TextView error, cate;
    DatabaseHelper helper;
    HashMap<Integer, String> maps;
    String country_abbr = "";
    String title = "";
    String ville = "";

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlayout);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Recherche");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }


        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        // List<String> params = data.getPathSegments();
        //Log.d("list", params.get(1));

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(
                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Search";
        mDescription = "kerawa search page";


        prefs = PreferenceManager.getDefaultSharedPreferences(Search.this);

        searchitem = (Button)findViewById(R.id.searchSubmit);
//            error = (TextView)findViewById(R.id.errormessage);
//        cate = (TextView)findViewById(R.id.cate);


        //initializing database to this class
        helper = new DatabaseHelper(Search.this);
        //since the method generatAllSubcategory returns hashmap object
        // hashmap instance needs to be created
        maps = new HashMap<>();

        searchingword = (EditText)findViewById(R.id.edtSearch);


        //getting the value which is stored in shareprefence
        country_abbr = prefs.getString("Kerawa-count", null);
        country_abbr = Krw_functions.countryName(country_abbr, Search.this);//getting the country name by using the country abbr

        categoryspinner = (Spinner)findViewById(R.id.catspinner);
        cityspinner = (Spinner)findViewById(R.id.cityspinner);
        subcatsp = (Spinner)findViewById(R.id.subcatspinner);



        //instantiating arraylists
        cities = new ArrayList<>();
        categories  = new ArrayList<>();
        subcategories = new ArrayList<>();






        //getting the cities
        cities = Krw_functions.generateCityList_from_CountryID(Search.this, country_abbr);
        Model m = new Model();
        m.setIcon(R.mipmap.ic_down);
        m.setTitle("--- Sélectionner ---");
        cities.add(0, m);


        //getting the categories
        categories = Krw_functions.generateCategoriesList();
        Model model = new Model();
        model.setTitle("--- Sélectionner ---");
        model.setIcon(R.mipmap.ic_down);
        categories.add(0, model);

        //generating values
        Krw_functions.generate_spinner(cities, Search.this, cityspinner);
        Krw_functions.generate_spinner(categories, Search.this, categoryspinner);





        //subcategories are generated depending on the category seleted
        categoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model model = (Model) categoryspinner.getSelectedItem();
                title = model.getTitle();//this is when you want to know the name of the category selected
                categorySelected = model.getTag(); //this is getting id of category
                maps = helper.getSubCategoryfromCategory(categorySelected);
                Set<Integer> keys =  maps.keySet();
                Iterator<Integer> it = keys.iterator();
                if(!subcategories.isEmpty()){
                    subcategories.clear();
                }

                //adding a default words
                Model model1 = new Model();
                model1.setTitle("--- Sélectionner ---");
                subcategories.add(0,model1);

                while (it.hasNext()) {
                    int id = it.next();
                    Model model2 = new Model();
                    model2.setTag(String.valueOf(id));
                    model2.setTitle(maps.get(id));
                    subcategories.add(model2);
                }

                adapter = new SubCatSAdapter(Search.this, subcategories, title);
                subcatsp.setAdapter(adapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model model = (Model) cityspinner.getSelectedItem();
                ville = model.getTitle();//this is when you want to know the name of the city selected
                citySelected = model.getTag(); //getting the id of the city because thats what is send as request to server
//                Toast.makeText(Search.this, citySelected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        subcatsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model model = (Model) subcatsp.getSelectedItem();
                String title = model.getTitle();
                subcatselected = model.getTag();
                Log.d("subcatID", "selected is is ===> " + subcatselected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      /*  subcatsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        //if the search icon on the soft key pad clicked
        searchingword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    openSearchActivity();
                    return true;
                }
                return false;
            }
        });


        //if the search button is clicked
        searchitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();

            }

        });


    }

    public void openSearchActivity() {
        Log.d("Search Value", categorySelected +
                " and city -----> " + citySelected + " and searching word is " +
                searchingword.getText().toString());
        Krw_functions.pushSearchEvent(Search.this, "/Search/keyword=" + searchingword.getText().toString() + "/Category/" + categorySelected + "/SubCategory/" + subcatselected + "/City/" + citySelected + "/Country=" + country_abbr);
        Intent intentListAdsActivity = new Intent(Search.this, ListAdsResultActivity.class);
        intentListAdsActivity.putExtra("search", searchingword.getText().toString());
        intentListAdsActivity.putExtra("ville", citySelected);
        intentListAdsActivity.putExtra("villeName", ville);
        intentListAdsActivity.putExtra("custom", categorySelected);
        intentListAdsActivity.putExtra("subcategory", subcatselected);
        intentListAdsActivity.putExtra("fromsearch", "true");
        intentListAdsActivity.putExtra("categoryTitle", title);
        startActivity(intentListAdsActivity);

    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/search"))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getAction());
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
