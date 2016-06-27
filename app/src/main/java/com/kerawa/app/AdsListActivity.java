package com.kerawa.app;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kerawa.app.fragments.Blog_Fragment;
import com.kerawa.app.fragments.ColorFragment;
import com.kerawa.app.fragments.MyAdsFragment;
import com.kerawa.app.helper.VipAds_loader;
import com.kerawa.app.services.delete_files;
import com.kerawa.app.services.drugstore_updater;
import com.kerawa.app.services.updater;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.CustomGrid;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.Current_country;
import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.generateCityList_from_CountryID;
import static com.kerawa.app.utilities.Krw_functions.isMyServiceRunning;
import static com.kerawa.app.utilities.Krw_functions.price_formated;



/**
 * activite principale de l'application menant a l'interface d'accueil
 */
public class AdsListActivity extends AppCompatActivity implements Animation.AnimationListener,
        BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener, OnConnectionFailedListener {
    private static DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    TextView NameChanger,CountryTitle,BlogTitle,Account,statusText,contact_text,mes_annonces, affiliatetitle,qr_code,health_services, appInviteTv;
    SliderLayout mDemoSlider;
    FrameLayout laframe;
    String[] catName;
    GridView grid;
    int[] iconsId;
    ImageView icone_pays;
    String[] tags;
    ArrayList<Model> models;
    int DisplayHeight, DisplayWidth;
    DisplayMetrics display;
    RelativeLayout drawer;
    private Spinner Cities;
    private ArrayList<Model> villes;
    private SharedPreferences prefs;
    private String viplist;
    private Spinner Categories;
    private ArrayList<Model> lescat;
    private LinearLayout Topbar;
    private ShowHide VD;
    private boolean doubleBackToExitPressedOnce;
    private TextView title_contacts;
    private  TextView version;
    private String title_country;
    private int sel_item = 0;
    private String nom;
    //private PublisherAdView mPublisherAdView;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    private String encoded = "";
    private String decoded = "";
    private String cat_to_subcategory = "";
    private  String search = "";
    Intent service4, deletefiles;

    //AppInvite settings
    private static final String TAG = AdsListActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    private GoogleApiClient googleApiClient;
    //end of AppInvite settings


    @Override
	protected void onCreate(Bundle savedInstanceState) {

        //hiding the notification bar
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_Ads_List");


        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        prefs = PreferenceManager.getDefaultSharedPreferences(AdsListActivity.this);
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
       // mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Ads List";
        mDescription = "liste des annonces de kerawa.com";
        boolean loggedIn = prefs.getBoolean("loggedIn", false);

        //stopService(new Intent(this, ScreenOnOff.class));
        /*if(loggedIn){

            String mac = Build.SERIAL;
            try {
                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                mac = info.getMacAddress();
            }
            catch (Exception ex){
                ex.printStackTrace();}
            String userid = prefs.getString("userId", (Krw_functions.getMainemail(getApplicationContext())!="")?Krw_functions.getMainemail(getApplicationContext()):mac);
            Intercom.client().registerIdentifiedUser(new Registration().withUserId(userid));
        } else {
            // Since we aren't logged in, we are an unidentified user. Lets register.
            Intercom.client().registerUnidentifiedUser();
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        appInviteTv = (TextView)findViewById(R.id.appShare);

        KerawaAppRate.app_launches(this);

        String nom = prefs.getString("Kerawa-count", null);
        nom = Krw_functions.countryName(nom, AdsListActivity.this);
        //Categories = (Spinner) findViewById(R.id.liste_cat);
        //Cities = (Spinner) findViewById(R.id.liste_villes);
         service4 = new Intent(getApplicationContext(), drugstore_updater.class);
        //checking if service is running
        if(!Krw_functions.isMyServiceRunning(drugstore_updater.class,getApplicationContext())) {
            startService(service4);
        }

          deletefiles = new Intent(getApplicationContext(), delete_files.class);
        if(!Krw_functions.isMyServiceRunning(delete_files.class, getApplicationContext())) {

            startService(deletefiles);

        }


        villes = generateCityList_from_CountryID(getApplicationContext(), nom);
        lescat = generateCategoriesList();

        //generate_spinner(villes, getApplicationContext(), Cities);
        //generate_spinner(lescat, getApplicationContext(), Categories);

        String country = "";
        String categorie = "";
        String categorie1 = "";
        String cat = "";

        if(getIntent().getStringExtra("custom")!=null) {
//            int ii = 0;
//            do {
//                Model elt = (Model) Categories.getItemAtPosition(ii);
//                if (elt.getTag().equals(getIntent().getStringExtra("custom"))) Categories.setSelection(ii);
//                ii++;
//            } while (ii < Categories.getCount());

            Bundle bundle = new Bundle();
            bundle.putString("categorie", getIntent().getStringExtra("custom"));
            bundle.putString("title", getIntent().getStringExtra("customtitle"));



//            Intent intent = new Intent(getApplicationContext(), SubCatActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Krw_functions.Current_country(getApplicationContext()) + " - " + getIntent().getStringExtra("subcattitle"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

//        //Create an auto-managed GoogleApiClient with access to App Invites
        googleApiClient= new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this,this)
                .build();

        //Check for App invite invitations and launch deep-link  activity if possible
        //Requires that an activity is registered in AndroidManifest.xml to handle
        //deep-link URLs
        boolean autolaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autolaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult appInviteInvitationResult) {
                                Log.d(TAG, "getInvitation:onResult:"+ appInviteInvitationResult.getStatus());
                                // Because autoLaunchDeepLink = true we don't have to do anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                                //for example i can launch AdsActivity
//                                Intent intent = new Intent(getApplicationContext(), AdsListActivity.class);
//                                startActivity(intent);
                            }
                        }
                );

        String name = Krw_functions.Current_country(AdsListActivity.this);
        //    setAlarm(getApplicationContext());
        if (getIntent().getData() != null) {
            Uri data = getIntent().getData();




            List<String> params = data.getPathSegments();
            if (params.get(0) != null && !params.get(0).trim().equalsIgnoreCase("") )
            {
                country = params.get(0);//getting the new country from application
                //si le pays est different du pays en cours alors on lance le dialog builder
                country = country.substring(0,country.lastIndexOf("-"));//elimination du r-4XXX
                if (!Current_country(this).trim().equalsIgnoreCase(country) )
                {
                     show_dialog(name,country);
                }

            }
            //==========   deeplinking block ============
       if (params.size()>=2) {
           if (params.get(1) != null) {
               categorie1 = params.get(1); // "category"
               //faire le tableau de correspondance entre les noms de categorie du site et ceux de lappli pour pouvoir en degager les noms reels
               if (categorie1.equalsIgnoreCase("emploi")) getIntent().putExtra("categorie", "17");
               if (categorie1.equalsIgnoreCase("automobile"))
                   getIntent().putExtra("categorie", "18");
               if (categorie1.equalsIgnoreCase("mode")) getIntent().putExtra("categorie", "137");
               if (categorie1.equalsIgnoreCase("immobilier"))
                   getIntent().putExtra("categorie", "12");
               if (categorie1.equalsIgnoreCase("high-tech"))
                   getIntent().putExtra("categorie", "124");
               if (categorie1.equalsIgnoreCase("a-vendre")) getIntent().putExtra("categorie", "11");
               if (categorie1.equalsIgnoreCase("services"))
                   getIntent().putExtra("categorie", "129");

               if(params.get(0).equalsIgnoreCase("search")) {
                   Bundle bun = new Bundle();
                   bun.putString("categorie", "0,desc," + params.get(1));
                   bun.putString("keywordDecoded", params.get(1).replace("+", " "));
                   bun.putString("keywordEncoded", params.get(1));
                   encoded = params.get(1).replace("+", " ");
                   decoded = params.get(1);
                   cat_to_subcategory = "0,desc,"+ params.get(1);
                   search = "search";
                   bun.putString("search","search");
                   getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bun)).commit();

               }
           }
       }

       /*     if (params.get(1) != null)
            {
                categorie1 = params.get(1); // "category"
                //faire le tableau de correspondance entre les noms de categorie du site et ceux de lappli pour pouvoir en degager les noms reels
                if (categorie1.equalsIgnoreCase("emploi")) getIntent().putExtra("categorie", "17");
                if (categorie1.equalsIgnoreCase("automobile")) getIntent().putExtra("categorie", "18");
                if (categorie1.equalsIgnoreCase("mode")) getIntent().putExtra("categorie", "137");
                if (categorie1.equalsIgnoreCase("immobilier")) getIntent().putExtra("categorie", "12");
                if (categorie1.equalsIgnoreCase("high-tech")) getIntent().putExtra("categorie", "124");
                if (categorie1.equalsIgnoreCase("a-vendre")) getIntent().putExtra("categorie", "11");
                if (categorie1.equalsIgnoreCase("services")) getIntent().putExtra("categorie", "129");
            }*/
}

        if (getIntent().getExtras() != null) {
            String lepays =getIntent().getExtras().getString("pays");

            // stockage du pays de l'utilisateur
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AdsListActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("country", lepays);
            editor.apply();



            if (lepays != null) {
                if (!lepays.toLowerCase().equals("cm")) {
                    new ShowHide(findViewById(R.id.emergency)).Hide();
                }
            }
        }

        if(getIntent().getExtras() != null) {
                if (getIntent().getExtras().getString("categorie") != null) {
                    categorie = getIntent().getExtras().getString("categorie");
                    if (categorie != null && !categorie.trim().equals("")) {
                        for (int i = 0; i <= lescat.size() - 1; i++) {
                            if (lescat.get(i).getTag().equalsIgnoreCase(categorie)) {
                                cat = lescat.get(i).getTitle();
                                break;
                            }
                        }
                        Log.d("test_bundle", cat + " -----  " + categorie);

                        int k = 0;
                        try {
                            do {

                                Model elt = (Model) Categories.getItemAtPosition(k);
                                Log.d("TAG_bundle", elt.getTag());
                                if (elt.getTag().equals(categorie)) {
                                    sel_item = k;
                                    Categories.setSelection(k);
                                    break;
                                }


                                k++;
                            } while (k < Categories.getCount());

                            Bundle bundle = new Bundle();
                            bundle.putString("categorie", categorie);
                            bundle.putString("ville", "");
                            Log.d("fragmennt", "notification");
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(Krw_functions.Current_country(getApplicationContext()) + " - " + getIntent().getStringExtra("subcattitle"));
                            }

                        } catch (Exception e) {
                            Intent intent = new Intent(AdsListActivity.this, splash.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }

            }

        doubleBackToExitPressedOnce=false;

         title_country = Current_country(AdsListActivity.this);
        //if(getSupportActionBar() !=null)  getSupportActionBar().setTitle(title_country);
        drawer= (RelativeLayout) findViewById(R.id.drawer);
        Topbar=(LinearLayout) findViewById(R.id.linearLayout5);
        NameChanger=(TextView) findViewById(R.id.country_change);
        qr_code=(TextView) findViewById(R.id.qr_code_text);
        health_services= (TextView) findViewById(R.id.health_text);

        icone_pays= (ImageView) findViewById(R.id.home_icon);
        affiliatetitle = (TextView) findViewById(R.id.pharmacy_de_garde);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        prefs = PreferenceManager.getDefaultSharedPreferences(AdsListActivity.this);
        viplist = prefs.getString("currentVip", null);
        //Log.d("there1","1");
       // VD=new ShowHide(Topbar);
       // VD.Hide();
        CountryTitle = (TextView) findViewById(R.id.country_title);
        Account= (TextView) findViewById(R.id.my_account_text);
        mes_annonces= (TextView) findViewById(R.id.my_ads_text);
      //  Cities = (Spinner) findViewById(R.id.liste_villes);
      //  villes = generateCityList_from_CountryID(getApplicationContext(), Integer.valueOf(nom));
        BlogTitle = (TextView) findViewById(R.id.blog_title);

        statusText= (TextView) findViewById(R.id.statusText);
        (new ShowHide(statusText)).Hide();
       // Categories = (Spinner) findViewById(R.id.liste_cat);
       // lescat = generateCategoriesList();
        BlogTitle = (TextView) findViewById(R.id.blog_title);
        contact_text= (TextView) findViewById(R.id.contact_text);
        version = (TextView)findViewById(R.id.version);

        //  generate_spinner(villes, getApplicationContext(), Cities);
       // generate_spinner(lescat, getApplicationContext(), Categories);
        //Log.d("there2", "2");

        //Categories.setSelected(false);
        //Cities.setSelected(false);

        //commented this on the 28/03/2016
//        Cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                Model model = (Model) Cities.getSelectedItem();
//
//                Model model3 = (Model) Categories.getSelectedItem();
//
//
//                String category = model3.getTag();
//                String ct = model3.getTitle();
//                Log.d("category_from_city", category);
//                String laville = model.getTag();
//                String lv = model.getTitle();
//                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ads_list/ city = " + lv + "/category=" + ct);
//
//                //if (!(model.getTag() == null)) {
//                // if (!model.getTag().equals("")) {
//                try {
//
//                    //Show_Toast(getApplicationContext(), "Chargement des annonces de " + model.getTitle(), true);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("categorie", category);
//                    bundle.putString("ville", laville);
//                    Log.d("fragmennt", "cities");
//                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
//                    //   if (getSupportActionBar() != null)
//                    //   getSupportActionBar().setTitle(" Kerawa - " + model.getTitle());
//                } catch (Exception e) {
//                    Show_Toast(getApplicationContext(), e.getMessage() + " " + e.getCause(), true);
//                    // }
//                }
//                //}
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        //commented this on the 28/03/2016

//        Categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Model model3 = (Model) Categories.getSelectedItem();
//                Model model = (Model) Cities.getSelectedItem();
//
//                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ads_list/city=" + model.getTitle() + "/category=" + model3.getTitle());
//
//                if (!model3.getTag().equals(""))
//                    update_countryPage(Integer.parseInt(model3.getTag()), model3.getTitle(), model.getTag());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        Account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Krw_functions.Open_Connection_or_register(AdsListActivity.this, R.id.action_account);
            }
        });
        mes_annonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (!model.getTag().equals("")) {


                Bundle bundle = new Bundle();
                try {
                    Log.d("fragmennt", "mes annonces");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, MyAdsFragment.newInstance(bundle)).commit();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(Krw_functions.Current_country(getApplicationContext()) + " - " + "Mes Annonces");
                    }
                    mDrawerLayout.closeDrawer(GravityCompat.START);

                } catch (Exception e) {
                    Show_Toast(getApplicationContext(), e.getMessage() + " " + e.getCause(), true);
                    // }
                }

            }
        });
        //Log.d("there3", "3");
        contact_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send_email();
                startActivity(new Intent(getApplicationContext(), Contact_us.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        int imgResource = Krw_functions.getCountryIcon(AdsListActivity.this);
        icone_pays.setImageResource(imgResource);
//        CountryTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);

        //CountryTitle.setText(Krw_functions.Current_country(AdsListActivity.this));
        CountryTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                intent.removeExtra("categorie");
                startActivity(intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        affiliatetitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Affiliation.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        title_contacts= (TextView) findViewById(R.id.contact_inviter);

        title_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent i = new Intent(getApplicationContext(), Mes_contacts.class);
                startActivity(i);*/
                Uri uri = Uri.parse("market://details?id=com.kerawa.app");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.kerawa.app")));
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });



        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);

                Intent i = new Intent(getApplicationContext(), DispatchQRActivity.class);
                startActivity(i);
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/activity_scanner/");

            }
        });


     /*   qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);

                new IntentIntegrator(AdsListActivity.this).initiateScan();
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/activity_scanner/");

            }
        });*/

      /*  qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/activity_scanner/");

                startActivity(i);
            }
        });*/

/*
       if(Krw_functions.isConnected(getApplication().getApplicationContext())) {
            //check : 1. SD card available , 2. if available space > 10 MB and 3.available and available rigths on disk
             Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

            if(isSDPresent) {
                if (Krw_functions.megabytesAvailable() > 10) {
                    if (Krw_functions.checkrigths()) {
                         Intent downloader = new Intent(getApplication().getApplicationContext(), AdsDownloader.class);
                         startService(downloader);
                    }
                    else
                    {
                        Krw_functions.Show_Toast(getApplicationContext(), "Please check the ritgths of application on external files", true);
                    }
                }
                else
                {
                    Krw_functions.Show_Toast(getApplicationContext(), "there is no enough space to save your data", true);
                }
            }
            else
            {
                Krw_functions.Show_Toast(getApplicationContext(), "there is no SD card available to save data offline", true);
            }

            if(!isMyServiceRunning(updater.class,getApplicationContext())) {
                Intent updat = new Intent(getApplication().getApplicationContext(), updater.class);
                startService(updat);

            }
        }*/


        BlogTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                VD.Hide();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Bundle bundle = new Bundle();
                bundle.putString("categorie", "");
                getSupportActionBar().setTitle("Le Blog de Kerawa");
                Log.d("fragmennt", "blog");
                getSupportFragmentManager().beginTransaction().add(R.id.content_frame, Blog_Fragment.newInstance(bundle)).commit();
            }
        });
        //startService(new Intent(getApplicationContext(), send_contacts.class));
        JSONObject obj = new JSONObject();
        //getAllPhoneContacts(getApplicationContext(),null,obj);

        //getAllPhoneEmails(getApplicationContext(), obj);
        Log.d("emails", obj.toString());


       /* mPublisherAdView.setAdSizes(AdSize.SMART_BANNER);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
       */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getSupportActionBar().setTitle(Krw_functions.Current_country(AdsListActivity.this));

        if (getSupportActionBar() != null)

                getSupportActionBar().setTitle(Html.fromHtml(title_country));


        // Creating a ToggleButton for NavigationDrawer with drawer event listener
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                /*	highlightSelectedCountry();            	*/
                supportInvalidateOptionsMenu();
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };

        // Setting event listener for the drawer
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Enabling Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NameChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CountryList.class));
                finish();
            }
        });

        health_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Health_services.class));
            }
        });

        //[START AppInviteActivity]
        appInviteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/AppInvite_button_pressed");

                String title = appInviteTv.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                Intent intent = new Intent(AdsListActivity.this, AppinviteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), VersionActivity.class));
            }
        });


        //[END app invite]

        //setListViewHeightBasedOnChildren(listView);
        Start_Country();

        //

    }
    public Action getAction() {
        String country_name = Krw_functions.Current_country(getApplicationContext()) ;
        String nomISo = prefs.getString("Kerawa-count", "");
        String countryID = countryName(nomISo,getApplicationContext());
        countryID = "r-"+countryID ;
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.kerawa.com/"+country_name+nomISo))
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
    protected void onDestroy() {
        super.onDestroy();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int menuID = item.getItemId();
        switch (menuID) {
            // Respond to the action bar's Up/Home button

            case R.id.search:
//                Krw_functions.Open_Connection_or_register(AdsListActivity.this, menuID);
                startActivity(new Intent(AdsListActivity.this, Search.class));
                Krw_functions.pushOpenScreenEvent(getApplicationContext(),
                        Krw_functions.Current_country(getApplicationContext()) +
                                "/Search_Activity");
                return true;
            case R.id.action_account:
                Krw_functions.Open_Connection_or_register(AdsListActivity.this, menuID);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   /* @Override
    public void onBackPressed() {


        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) { //replace this with actual function which returns if the drawer is open
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            } else {
                Show_Toast(this, "Tapez encore pour quitter", false);// replace this with actual function which closes drawer
                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3000);
            }
        }


    }*/


    @Override
    public boolean onKeyDown(int keyCode,KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // your action...

            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }


    public void Start_Country() {
        display = AdsListActivity.this.getResources().getDisplayMetrics();
        DisplayHeight = display.heightPixels;
        DisplayWidth = display.widthPixels;
        models = Krw_functions.generateCategoriesList();
        drawer.getLayoutParams().width = 80 * (DisplayWidth / 100);
        models = generateCategoriesList();
        try {
            nom = prefs.getString("Kerawa-count", null);
            nom = Krw_functions.countryName(nom, AdsListActivity.this);
            if (nom.equals("40"))  models.add(new Model(R.drawable.icon_emergency,"Health_services",getApplicationContext().getString(R.string.title_activity_health_services),false));
            iconsId = new int[models.size()];
            catName = new String[models.size()];
            tags = new String[models.size()];
            for (int i = 0; i < models.size(); i++) {
                Model model = models.get(i);
                iconsId[i] = model.getIcon();
                catName[i] = model.getTitle();
                tags[i] = model.getTag();
            }
            Log.d("there4","4");

            CustomGrid adapter = new CustomGrid(getApplicationContext(), catName, iconsId, tags,R.layout.grid_single);

            grid = (GridView) findViewById(R.id.grid);

            grid.setAdapter(adapter);

            grid.setBackgroundColor(Color.WHITE);

            grid.getLayoutParams().height = 2 * (DisplayHeight / 3);

            SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);

            mDemoSlider.getLayoutParams().height = (DisplayHeight / 3);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AdsListActivity.this);
            String viplist = prefs.getString("currentVip", null);
            View v = findViewById(R.id.loader);
            if (viplist == null) {
                if (!isMyServiceRunning(updater.class, getApplicationContext()) && can_call_update()) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("update", "non");
                    editor.apply();
                    Intent updat = new Intent(getApplication().getApplicationContext(), updater.class);
                    startService(updat);

                }
                try {
                    VipAds_loader get_vip = new VipAds_loader();
                    nom = prefs.getString("Kerawa-count", null);
                    nom = Krw_functions.countryName(nom, AdsListActivity.this);
                    get_vip.ctx = AdsListActivity.this;
                    get_vip.mDemoSlider = mDemoSlider;
                    //test url link
                    get_vip.server_url = Kerawa_Parameters.PreProdURL + "list_ads?onlyvip=true&country="+nom;
//                    correct url link
//                    get_vip.server_url = Kerawa_Parameters.PreProdURL + "regions/" + nom + "/ads/vip";
                    get_vip.v = v;
                    get_vip.execute();
                } catch (Exception e) {
                    Krw_functions.Show_Toast(AdsListActivity.this, e.toString(), true);
                }
            } else {
                Show_vip(viplist);
            }
            Log.d("there5", "5");
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //where i did changes to open up SubCatActivity with data

                    TextView Tv = (TextView) v.findViewById(R.id.grid_text);
                    String T = (String) v.getTag();
                    String Title = Tv.getText().toString();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", Title);
//                    Intent intent = new Intent(AdsListActivity.this, SubCatActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);

                    if (T.equalsIgnoreCase("Health_services")){
                        startActivity(new Intent(getApplicationContext(),Health_services.class));
                        return;
                    }
                   // int ii = 0;
//                    do {
//                        Model elt = (Model) Categories.getItemAtPosition(ii);
//                        if (elt.getTag().equals(v.getTag())) Categories.setSelection(ii);
//                        ii++;
//                    } while (ii < Categories.getCount());
                    try {
                        Krw_functions.pushOpenScreenEvent(AdsListActivity.this,"SubCategories");
                        Bundle bundle = new Bundle();
                        bundle.putString("categorie", T);
                        bundle.putString("title", Title);
                        bundle.putString("keywordEncoded", encoded);
                        bundle.putString("keywordDecoded", decoded);
                        bundle.putString("cat_sub", cat_to_subcategory);
                        Log.d("fragmennt", "grid_" + T);
                        Intent intent = new Intent(getApplicationContext(), SubCatActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

//                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
//                        if (getSupportActionBar() != null) {
//                            getSupportActionBar().setTitle(Krw_functions.Current_country(getApplicationContext()) + " - " + Title);
//                        }
                    } catch (Exception e) {
                        Krw_functions.Show_Toast(getApplicationContext(), e.getMessage() + " " + e.getCause(), true);
                    }
                }
            });

        } catch (Exception e) {
            Krw_functions.Show_Toast(AdsListActivity.this, e.toString(), true);
        }

    }

    private boolean can_call_update() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String nom = prefs.getString("update", null);
        return nom == null;
    }


    public void update_countryPage(int CatID, String CatName, String CityID) {
       //x VD.Show();
        Bundle bundle = new Bundle();
        String bundleString = CatID > 0 ? String.valueOf(CatID) : "";
        if (CityID != null && CityID != "") bundle.putString("ville", CityID);
        bundle.putString("categorie", bundleString);
        if(getSupportActionBar() != null && CatID != 0) {
            getSupportActionBar().setTitle(Krw_functions.Current_country(AdsListActivity.this) + " - " + getIntent().getStringExtra("subcattitle"));
        }
        Log.d("fragmennt", "update_country");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ColorFragment.newInstance(bundle)).commit();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Annonce ann = (Annonce) slider.getBundle().getSerializable("annonce");
        Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
        i.putExtra("annonce", ann);
        startActivity(i);
    }

    private void Show_vip(String result) {
        HashMap<Integer, Annonce> url_maps = new HashMap<>();

        boolean vrai = false;
        try {
            vrai = true;
            JSONObject data = new JSONObject(result);
            JSONArray arr = data.getJSONArray("data");
            for (int i = arr.length() - 1; i >= 0; i--) {
                Annonce an = new Annonce();
                an.setAdd_id(arr.getJSONObject(i).get("ad_id").toString());
                if(arr.getJSONObject(i).get("ad_title").toString().length()>=20){
                    an.setTitle(arr.getJSONObject(i).get("ad_title").toString().substring(0,20).toLowerCase());}
                else {
                    an.setTitle(arr.getJSONObject(i).get("ad_title").toString().toLowerCase());
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
                else an.setPrice(price);

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

                if (arr.getJSONObject(i).has("ad_category_name")) {
                    an.setCategory_name(arr.getJSONObject(i).getString("ad_category_name") + " -> " + arr.getJSONObject(i).getString("ad_subcategory_name"));
                }



                if (arr.getJSONObject(i).has("ad_url")) {
                    an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                }
                an.setDate(arr.getJSONObject(i).get("ad_date").toString());
        int _jj=i+1;


                url_maps.put(_jj, an);

            }

            List<Integer> peopleByAge = new ArrayList<>(url_maps.keySet());

            Collections.sort(peopleByAge);

            for(Integer name : url_maps.keySet()){
                TextSliderView textSliderView = new TextSliderView(getApplicationContext());
                // initialize a SliderLayout
               Annonce ann= url_maps.get(name);
                textSliderView
                        .description(ann.getTitle() + "... ( " +price_formated(ann.getPrice()) + " " + (!(price_formated(ann.getPrice()).trim()).equals("") ?ann.getCurrency():"")  + " )")
                        .image(url_maps.get(name).getImgList().get(0).replace("https", "http"))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside).setOnSliderClickListener(AdsListActivity.this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", url_maps.get(name).getTitle());
                textSliderView.getBundle().putSerializable("annonce", url_maps.get(name));
                mDemoSlider.addSlider(textSliderView);
            }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Left_Top);
            mDemoSlider.setDuration(4000);
            mDemoSlider.stopAutoCycle();

        } catch (JSONException e) {
            Errors_handler msg_err=new Errors_handler(this,getApplicationContext(),"Le chargement des annonces recommandées a échoué.",true,statusText,R.drawable.errore);
            msg_err.execute(500, 500, 6000);
        }

    }



    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    public void show_dialog(String old_country, final String new_country)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Vous êtes sur le point de passer de  "+old_country+" \n a  "+new_country+" cliquez sur oui pour continuer ou sur non pour annuler.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Oui",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();

                        ArrayList<Model> value = Krw_functions.generateCountryList(getApplicationContext());

                        for (int i = 0; i <= value.size() - 1; i++) {
                            if (value.get(i).getTitle().equalsIgnoreCase(new_country)) {
                                editor.putString("Kerawa-count",value.get(i).getTag());
                                editor.apply();
                                break;
                            }
                        }

                        Intent i = getIntent();
                        i.setData(null);
                        startActivity(i);
                    }
                });

        builder1.setNegativeButton(
                "Non",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage(getString(R.string.google_play_service_error));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
                Toast.makeText(getApplicationContext(), ids.length,Toast.LENGTH_LONG).show();
            } else {
                // Sending failed or it was canceled, show failure message to the user
                showMessage(getString(R.string.send_failed));
            }
        }
    }


    private void showMessage(String msg){
        Toast.makeText(AdsListActivity.this, msg, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(Krw_functions.isMyServiceRunning(drugstore_updater.class, getApplicationContext())){
            stopService(service4);


        }
        if(Krw_functions.isMyServiceRunning(delete_files.class, getApplicationContext())){
            stopService(deletefiles);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }









}


