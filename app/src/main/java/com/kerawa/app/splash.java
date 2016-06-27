package com.kerawa.app;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;

//import io.intercom.android.sdk.Intercom;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

// activite de lancement et de configuration des regions
public class splash extends AppCompatActivity {
    DatabaseHelper mydb;
    TextView text_loader;
    private WebView description;
    private TextView mytext;
    private ImageView refresher;
    private RelativeLayout popopwin;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

      //plateforme de gestion des erreurs
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        refresher = (ImageView) findViewById(R.id.refresher);
        mytext= (TextView) findViewById(R.id.mytext);
        text_loader = (TextView) findViewById(R.id.chargement);
        description = (WebView) findViewById(R.id.description);
        popopwin= (RelativeLayout) findViewById(R.id.the_popop);
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
       // mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Acceuil";
        mDescription = "Page d'acceuil de l'application kerawa.com";
       // description.setBackgroundColor(0); description.setBackgroundResource(R.drawable.gradient_bg);
        description.loadDataWithBaseURL(null, "<html><head><style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body style=\"width:98%;\"><div style=\"width:98%;heighjt:100%;background: linear-gradient(#eee, #fff);\"></div></body></html>", "text/html", "UTF-8", null);


        // Krw_functions.pushOpenScreenEvent(getApplicationContext(),Krw_functions.Current_country(getApplicationContext())+"/Splash_Screen");
        // setAlarm(getApplicationContext());
        Krw_functions.destroyvips(getApplicationContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("update");
        editor.apply();
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        mydb = new DatabaseHelper(this);
   //     if (  mydb.numberOfRows() < 6 &&) {
            if (  mydb.numberOfRows() < 6) {
            try {
                mydb.reset_countries();
                Krw_functions.AutoConfig(this, text_loader, refresher, mytext, popopwin);

            } catch (Exception e) {
                text_loader.setText(e.getMessage() + "\r\n" + e.toString());
            }
        } else {
            Krw_functions.LaunchSplash(this, 1500, text_loader);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.kerawa.com/home"))
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

}
