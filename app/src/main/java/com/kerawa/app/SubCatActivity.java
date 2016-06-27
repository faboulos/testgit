package com.kerawa.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.SubCatAdapter;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import static com.kerawa.app.utilities.Krw_functions.Open_form;
import static com.kerawa.app.utilities.Krw_functions.Variables_Session;
import static com.kerawa.app.utilities.Krw_functions.log_out;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.appindexing.Thing;
import com.newrelic.agent.android.NewRelic;

import static com.kerawa.app.utilities.Krw_functions.Current_country;
import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.generateCityList_from_CountryID;
import static com.kerawa.app.utilities.Krw_functions.isMyServiceRunning;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SubCatActivity extends AppCompatActivity {
    ListView listView;
    SubCatAdapter adapter;
    HashMap<Integer,String> listData, list;
    List<Integer> icons;
    private String categoryName = "";
    private String category = "";
    private DatabaseHelper databaseHelper;
    String keywordDecoded="",keywordEncoded="";
    private GoogleApiClient mClient;
    String cat_sub = "";
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding the notification bar

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Sub Category";
        mDescription = "list of sub category of kerawa.com";

        setContentView(R.layout.subcatlayout);
        Bundle bundle = getIntent().getExtras();
        //this are from AdsListActivity
        categoryName = bundle.getString("title");
        category = bundle.getString("categorie");
        keywordEncoded = bundle.getString("keywordEncoded");
        keywordDecoded = bundle.getString("keywordDecoded");
        cat_sub = bundle.getString("cat_sub");
        listData = new HashMap<>();
        icons = new ArrayList<>();
        listView = (ListView)findViewById(R.id.subList);
        ArrayList<Model> arrayList = new ArrayList<>();

        databaseHelper = new DatabaseHelper(this);
        listData =  databaseHelper.getSubCategoryfromCategory(getIntent().getStringExtra("categorie"));


        Model models = new Model();
        //model.setIcon();
        models.setTag(category);
        models.setTitle("toutes les annonces");
        arrayList.add(models);


        Set<Integer> keys = listData.keySet();
        Iterator<Integer> it = keys.iterator();

        while (it.hasNext()) {
            Integer nn = it.next();

            //model.setIcon();
            Model model = new Model();
            model.setTag(String.valueOf(nn));
            model.setTitle(listData.get(nn));
            arrayList.add(model);
            Log.d("what is wrong", model.getTitle());

        }

        adapter = new SubCatAdapter(this, arrayList, categoryName);
        listView.setAdapter(adapter);

        //WHAT HAPPENS WHEN THE LIST ITEMS ARE CLICKED
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Krw_functions.pushOpenScreenEvent(SubCatActivity.this, "ListAdsActivity/Category="+category+"/SubCategory="+cat_sub);
                Model model = (Model)listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(),ListAdsActivity.class);
                intent.putExtra("custom",model.getTag());//this is an int representing category code
                intent.putExtra("customtitle",categoryName);
                intent.putExtra("subcattitle",model.getTitle());
                intent.putExtra("parent",getIntent().getStringExtra("categorie"));
                intent.putExtra("fromsearch", "false");
                intent.putExtra("test_cat", cat_sub);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), model.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        //checking support for actionbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    public Action getAction() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SubCatActivity.this);
        String country_name = Krw_functions.Current_country(getApplicationContext()) ;
        String nomISo = prefs.getString("Kerawa-count", "");
        String countryID = countryName(nomISo,getApplicationContext());
        countryID = "r-"+countryID ;

        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/"+country_name+countryID+"/"+category+"/"+cat_sub))
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(SubCatActivity.this, AdsListActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

    }
}
