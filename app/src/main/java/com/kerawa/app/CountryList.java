package com.kerawa.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.helper.Categories_loader;
import com.kerawa.app.helper.Currency_loader;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Home_Adapter;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCurrencies;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
public class CountryList extends AppCompatActivity {
    ArrayList<Model> value;
    ListView listView;
    ImageView refresher;
    DatabaseHelper mydb;
    private TextView mytext;
    private RelativeLayout the_popop;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       //platform of errors notifications
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_Country_list");
        setContentView(R.layout.country_list);

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Pays des Annonces";
        mDescription = "Page de selection des pays  de l'application kerawa.com";
        //instantiating new relic to monitor unexpected errors

      /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
        String title_country=Current_country(getApplicationContext())!=""?Current_country(getApplicationContext()):"Choix du pays";
        getSupportActionBar().setTitle((Html.fromHtml(String.format(getResources().getString(R.string.titre_html),title_country))));
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        */
        the_popop = (RelativeLayout) findViewById(R.id.the_popop);
        refresher = (ImageView) findViewById(R.id.refresher);

        mytext = (TextView) findViewById(R.id.mytext);
        mydb = new DatabaseHelper(this);
        Krw_functions.destroyvips(getApplicationContext());

        try {
            value = Krw_functions.generateCountryList(CountryList.this);
            //checking if the country table is filled with data
            if ((value.size() < 6) && value.size() == 0 ){

                new ShowHide(the_popop).Show();
               // new ShowHide(mytext).Show();
                refresher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       mydb.reset_countries();
                       startActivity(new Intent(getApplicationContext(), splash.class));
                       finish();
                    }
                });
            }
            Home_Adapter adapter = new Home_Adapter(this, value);
            listView = (ListView) findViewById(R.id.country_list);
            listView.setAdapter(adapter);
            start_page();
        }catch (Exception e){
           // Krw_functions.Show_Toast(CountryList.this, e.getLocalizedMessage(), true);
        }

    }
    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/index.php"))
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
        getMenuInflater().inflate(R.menu.menu_country_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    private void start_page(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Model itemValue = (Model) parent.getItemAtPosition(position);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Kerawa-count", itemValue.getTag());
                editor.apply();



                Krw_functions.Show_Toast(CountryList.this, String.format("Welcome to Kerawa %s", itemValue.getTitle()), true);
                startActivity(new Intent(CountryList.this, AdsListActivity.class).putExtra("pays",itemValue.getTag()));
                DatabaseHelper mydb = new DatabaseHelper(getApplicationContext());
                HashMap<Integer,String> VilLes,Categories;

                Categories = mydb.getAllCategories("null");
                String currencies = prefs.getString("currentcies", null);
                ArrayList<Model> listemonn = generateCurrencies(currencies);



                if(Categories.size() == 0) {
                    Categories_loader catload = new Categories_loader();
                    catload.CountryID = Integer.parseInt(countryName(itemValue.getTag(), getApplicationContext()));
                    catload.ctx = getApplicationContext();
                    catload.execute();
                }

                if(listemonn.size() == 0) {
                    Currency_loader monnaies = new Currency_loader();
                    monnaies.ctx = getApplicationContext();
                    monnaies.execute();
                }

                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/country_button_pressed");
                finish();


            }
        });

    }

}
