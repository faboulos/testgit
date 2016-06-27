package com.kerawa.app.deepLinking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView2;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kerawa.app.CountryList;
import com.kerawa.app.R;
import com.kerawa.app.ShareAds;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.GetImages;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.interfaces.JavaScriptInterface;
import com.kerawa.app.makeCallorText;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.SImilarAdapter;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.firt_integer;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

public class AdsDeepLinkingActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View loader;
    String adsID;
    int DisplayHeight,DisplayWidth;
    DisplayMetrics display;
    Annonce myads;
    String cattocall = "";
    int last = 0;
    int position ;
    private JSONArray liste ;
    private ArrayList<Annonce> similarList ;
    private ListView liste_similaires ;
    private SImilarAdapter adapter ;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    private String category = "";
    private String page = "";
    private String city = "";
    private String next_url = "";
    private boolean search =false;
    private boolean value = false;
    private Button next;
    private Button previous;
    boolean control = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        setContentView(R.layout.activity_ads_deep_linking);


        /*category = getIntent().getStringExtra("category");
        page = getIntent().getStringExtra("page");
        city = getIntent().getStringExtra("city");
        next_url= getIntent().getStringExtra("next");
        search = getIntent().getBooleanExtra("search", false);

        position = getIntent().getIntExtra("position", 0);*/


        display = this.getResources().getDisplayMetrics();
        DisplayHeight=display.heightPixels;
        DisplayWidth=display.widthPixels;


        myads = (Annonce) getIntent().getExtras().getSerializable("annonce");

        if (myads != null) {
            adsID= myads.getAdd_id();
            if (getSupportActionBar() != null)  getSupportActionBar().setTitle("Annonce N° "+adsID);
        }


        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/details/user=" + myads.getUser_id() + "/city=" + myads.getCity_name() + "/category&sub_category=" + myads.getCategory_name() + "/ads_id=" + adsID);

        loader=findViewById(R.id.loader);

        if (getSupportActionBar() != null)
        {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        load_ads(myads);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

// ========================================================================================================
    //======================================================================================================



    @SuppressLint("AddJavascriptInterface")
    public void load_ads(final Annonce myadsTag) {

        myads = myadsTag;
        // Log.d("daate",myadsTag.getDate());
        mTitle = myadsTag.getFriendly_url();
        mDescription = myadsTag.getDescription();

        String ads = "";
        if (myadsTag != null) {
            ads = myadsTag.getAdd_id();
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Annonce N° " + ads);
        }
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/details/user=" + myadsTag.getUser_id() + "/city=" + myadsTag.getCity_name() + "/category&sub_category=" + myadsTag.getCategory_name() + "/ads_id=" + ads);

        ScrollView scr = (ScrollView) findViewById(R.id.scroller);
        scr.setVisibility(View.GONE);
        RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer);
        footer.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

            }
        }, 2000);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        final SliderLayout[] mDemoSlider = new SliderLayout[1];
        final TextView[] title = new TextView[1];
        final WebView[] description = new WebView[1];

        final TextView[] price = new TextView[1];
        final TextView[] category = new TextView[1];
        final TextView[] date = new TextView[1];
        final TextView city;
        TextView m = (TextView) findViewById(R.id.sim);
        m.setTransformationMethod(null);
        city = (TextView) findViewById(R.id.ville);
        String url = Kerawa_Parameters.PreProdURL;
        final String[] phone1 = {""};
        final String[] phone2 = {""};

        Button call;
        Button sms;
        Button share;
        Button email;
        mDemoSlider[0] = (SliderLayout) findViewById(R.id.slider);
        mDemoSlider[0].removeAllSliders();
        mDemoSlider[0].getLayoutParams().height = (int) ((DisplayHeight / 3) * 1.5);
        price[0] = (TextView) findViewById(R.id.price2);
        ArrayList<String> img_url;
        //description[0] = (WebView) findViewById(R.id.description1);
        description[0] = new WebView(this);
        //description[0].setId(R.id.des);
        Annonce data = myadsTag;

        LinearLayout rel = (LinearLayout) this.findViewById(R.id.extra);
        rel.removeAllViews();
        rel.setVisibility(View.GONE);

        LinearLayout de = (LinearLayout) this.findViewById(R.id.desc);
        de.removeAllViews();
        de.setVisibility(View.GONE);


        if (Krw_functions.isConnected(getApplicationContext())) {
            HashMap<String, String> map = myadsTag.getMetas();

            if (map != null) {
                Set<String> keys = map.keySet();
                Iterator<String> it = keys.iterator();
                Log.d("metas size", map.size() + "");
                while (it.hasNext()) {
                    String nn = it.next();
                    String vl = map.get(nn);
                    if (nn.equalsIgnoreCase("Téléphone #2") && !vl.trim().equalsIgnoreCase("")) {
                        data.setPhone2(vl);
                    }
                    Log.d("metas details", nn + "," + vl);
                    if (vl != null && !nn.equalsIgnoreCase("Téléphone #2")) {
                        if (!vl.equalsIgnoreCase("null")) {
                            LinearLayout lin = new LinearLayout(getApplicationContext());
                            TextView tv1 = new TextView(getApplicationContext());
                            tv1.setGravity(Gravity.LEFT);
                            tv1.setTextAppearance(getApplicationContext(), R.style.Kerawa_NiceText);
                            tv1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                            ;
                            TextView tv2 = new TextView(getApplicationContext());
                            tv2.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                            p.setMargins(0, 0, 5, 0);

                            tv2.setLayoutParams(p);
                            tv2.setTextAppearance(getApplicationContext(), R.style.Kerawa_NiceText);

                            tv1.setText(nn);
                            tv2.setText(vl);
                            lin.addView(tv1);
                            lin.addView(tv2);
                            lin.setBackgroundColor(Color.parseColor("#eeeeee"));
                            lin.setPadding(8, 8, 8, 8);
                            lin.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 10, 0, 10);
                            lin.setLayoutParams(params);
                            rel.addView(lin);

                        }
                    }
                    rel.setVisibility(View.VISIBLE);
                }
            }

            String titl = data.getTitle();
           // String pr = price_formated(data.getPrice());
            String pr = data.getPrice();
            String desc = data.getDescription();
            String cat = data.getCategory_name();
            String dat = data.getDate();
            String cit = data.getCity_name();
            city.setText(cit);
            phone1[0] = data.getPhone1();
            phone2[0] = data.getPhone2();
            String cur = data.getCurrency();
            img_url = data.getImgList();
            cattocall = data.getCategory_name();


            title[0] = (TextView) findViewById(R.id.title1);
            title[0].setText(titl);
           if (pr.trim().equalsIgnoreCase("0")) {
                pr = "N/A";
                cur = "";
            }
            if (pr.trim().equalsIgnoreCase("null")) {
                pr = "N/A";
                cur = "";
            }
            if (pr.trim().equalsIgnoreCase("")) {
                pr = "N/A";
                cur = "";
            }

            if (pr.trim().equalsIgnoreCase("Aucun")) {
                pr = "N/A";
                cur = "";
            }

            /*if (pr.equalsIgnoreCase("0")) {
                pr = "N/A";
                cur = "";
            }
            if (pr.equalsIgnoreCase("null")) {
                pr = "N/A";
                cur = "";
            }
            if (pr.equalsIgnoreCase("")) {
                pr = "N/A";
                cur = "";
            }*/

            price[0].setText("Prix : " + pr + " " + cur);

            category[0] = (TextView) findViewById(R.id.cat2);
            category[0].setText(cat);

            date[0] = (TextView) findViewById(R.id.date1);
            String testDate = myadsTag.getDate();
            String newDate = testDate.substring(0, 10);
            date[0].setText(newDate);

            //hidePDialog();
            hide_loader();
            HashMap<Integer, String> url_maps = new HashMap<>();
            if (img_url != null) {
                if (img_url.size() > 0) {
                    int max = 5;
                    int Total = img_url.size();
                    max = Total;
                    //if (img_url[0].length() < max) max = img_url[0].length();
                    for (int i = 0; i < max; i++) {
                        int j = i + 1;
                        url_maps.put(j, img_url.get(i).replace("https", "http"));
                    }
                    ArrayList<Integer> keys = new ArrayList<>(url_maps.keySet());


                    Collections.sort(keys);

                    for (Integer name : url_maps.keySet()) {
                        TextSliderView2 textSliderView = new TextSliderView2(AdsDeepLinkingActivity.this);
                        // initialize a SliderLayout
                        textSliderView
                                .description("Photo " + name + " / " + Total)
                                .image(url_maps.get(name))
                                .error(R.drawable.logo2)
                                .errorDisappear(false)
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                .setOnSliderClickListener(AdsDeepLinkingActivity.this);
                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", url_maps.get(name));

                        mDemoSlider[0].addSlider(textSliderView);
                    }

                    mDemoSlider[0].setPresetTransformer(SliderLayout.Transformer.Tablet);
                    mDemoSlider[0].setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);
                    mDemoSlider[0].setCustomAnimation(new DescriptionAnimation());
                    mDemoSlider[0].setDuration(4000);
                    mDemoSlider[0].addOnPageChangeListener(AdsDeepLinkingActivity.this);
                    mDemoSlider[0].stopAutoCycle();
                }
            } else {
                mDemoSlider[0].setVisibility(View.GONE);
                mDemoSlider[0].getLayoutParams().height = 0;
            }

            description[0].addJavascriptInterface(new JavaScriptInterface(this, description[0]), "MyHandler");
            String Script = "<script type=\"text/javascript\">" +
                    "self.addEventListener(\"load\",function(){" +
                    "document.querySelector('body')\n" +
                    ".addEventListener('click', function (event) {\n" +
                    "    if(event.target.tagName === 'A') { \n" +
                    "        event.preventDefault();\n" +
                    "window.MyHandler.Openurl(event.target.getAttribute(\"href\"));" +
                    "        show();\n" +
                    "    }\n" +
                    "});" +
                    "});" +
                    "</script>";
            description[0].loadDataWithBaseURL(null, "<html><head>" + Script + "<style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + desc.replace("\n", "<br>") + "<br><br></div></body></html>", "text/html", "UTF-8", null);
            description[0].getSettings().setJavaScriptEnabled(true);
            description[0].invalidate();

            de.addView(description[0]);
            de.setVisibility(View.VISIBLE);


            // RelativeLayout r = (RelativeLayout)findViewById (R.id.rel);
            // r.invalidate();
            // ScrollView scrollView = (ScrollView)findViewById(R.id.scroller);
            liste_similaires = (ListView) findViewById(R.id.similar);
            scr.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("PARENT", "PARENT TOUCH");
                    findViewById(R.id.similar).getParent()
                            .requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });
            liste_similaires.setEnabled(true);
            liste_similaires.setFocusable(true);
            /*liste_similaires.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("CHILD", "CHILD TOUCH");
                    // Handle ListView touch events.
                    return false;
                }
            });*/

            //liste_similaires.removeAllViews();
            similarList = new ArrayList<>();

            adapter = new SImilarAdapter(this, R.layout.similar_row, similarList);
            liste_similaires.setAdapter(adapter);
            //liste_similaires.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
            //liste_similaires.setVisibility(View.VISIBLE);
            liste_similaires.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    // view.getParent().requestDisallowInterceptTouchEvent(true);
                    Log.d("itemclicked", "test");
                    Annonce anonc = (Annonce) liste_similaires.getItemAtPosition(pos);
                    load_ads(anonc);
                }
            });


            LoadSimilars(myadsTag.getTitle());

            scr.setVisibility(View.VISIBLE);
            //footer.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
        } else {
            //handle the offline mode here
            // getting id detais using its ID
            title[0] = (TextView) findViewById(R.id.title1);
            title[0].setText(myads.getTitle());
            Krw_functions.Show_Toast(getApplication().getApplicationContext(), title[0].getText().toString(), false);

            description[0].addJavascriptInterface(new JavaScriptInterface(this, description[0]), "MyHandler");
            String Script = "<script type=\"text/javascript\">" +
                    "self.addEventListener(\"load\",function(){" +
                    "document.querySelector('body')\n" +
                    ".addEventListener('click', function (event) {\n" +
                    "    if(event.target.tagName === 'A') { \n" +
                    "        event.preventDefault();\n" +
                    "window.MyHandler.toastString(\"Vous n'etes pas connecté à internet\");" +
                    "        show();\n" +
                    "    }\n" +
                    "});" +
                    "});" +
                    "</script>";
            description[0].loadDataWithBaseURL(null, "<html><head>" + Script + "<style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + myads.getDescription().replace("\n", "<br>") + "</div></body></html>", "text/html", "UTF-8", null);
            price[0] = (TextView) findViewById(R.id.price2);


            String cur = myads.getCurrency();
            String pr = myads.getPrice();
            if (pr.trim().equalsIgnoreCase("0")) {
                pr = "--";
                cur = "--";
            }
            if (pr.trim().equalsIgnoreCase("null")) {
                pr = "--";
                cur = "--";
            }

            price[0].setText("Prix : " + pr);
            city.setText(myads.getCity_name());

            date[0] = (TextView) findViewById(R.id.date1);
            date[0].setText(date[0].getText() + " " + myads.getDate());


            category[0] = (TextView) findViewById(R.id.cat2);
            category[0].setText(myads.getCategory_name());

            phone1[0] = myads.getPhone1();
            phone2[0] = myads.getPhone2();

            hide_loader();

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/.kerawa/" + myads.getAdd_id());
            String[] urls = null;
            if (myDir.exists()) {
                File[] filelist = myDir.listFiles();
                urls = new String[filelist.length];
                for (int i = 0; i <= filelist.length - 1; i++) {
                    // do your stuff here
                    urls[i] = filelist[i].getAbsolutePath();
                }

            }

            HashMap<String, String> url_maps = new HashMap<String, String>();
            if (urls != null) {
                if (urls.length > 0) {
                    int max = 5;
                    int Total = urls.length;
                    max = Total;
                    //if (img_url[0].length() < max) max = img_url[0].length();
                    for (int i = 0; i < max; i++) {
                        int j = i + 1;
                        url_maps.put(j + "", "file://" + urls[i]);
                    }

                    ArrayList<String> keys = new ArrayList<>(url_maps.keySet());

                    Collections.sort(keys, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            return firt_integer(lhs) - firt_integer(rhs);
                        }
                    });

                    for (String name : url_maps.keySet()) {
                        TextSliderView2 textSliderView = new TextSliderView2(AdsDeepLinkingActivity.this);
                        // initialize a SliderLayout
                        textSliderView
                                .description(name)
                                .image(url_maps.get(name))
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                .setOnSliderClickListener(AdsDeepLinkingActivity.this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", url_maps.get(name));

                        mDemoSlider[0].addSlider(textSliderView);
                    }

                    mDemoSlider[0].setPresetTransformer(SliderLayout.Transformer.Tablet);
                    mDemoSlider[0].setPresetIndicator(SliderLayout.PresetIndicators.Left_Top);
                    mDemoSlider[0].setDuration(4000);
                    mDemoSlider[0].stopAutoCycle();

                }
            } else {
                mDemoSlider[0].setVisibility(View.GONE);
                mDemoSlider[0].getLayoutParams().height = 0;
            }

            //loader.setVisibility(View.GONE);
        }

       /* Contact=(TextView)findViewById(R.id.co1);
        Contact.setText(Contact.getText()+" "+Contacts) ;*/

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);


        next.setTransformationMethod(null);//pour la mise en miniscules des caracteres sinon par defaut les textes associés seront en majuscule
        previous.setTransformationMethod(null);//pour la mise en miniscules des caracteres sinon par defaut les textes associés seront en majuscule

        call = (Button)findViewById(R.id.call);
        sms = (Button)findViewById(R.id.sms);
        share = (Button)findViewById(R.id.share);
        email = (Button)findViewById(R.id.email);

        call.setTransformationMethod(null);//pour la mise en miniscules des caracteres sinon par defaut les textes associés seront en majuscule
        sms.setTransformationMethod(null);
        share.setTransformationMethod(null);
        email.setTransformationMethod(null);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/share_button_pressed/user="+myads.getUser_id()+"/ad_id="+myads.getAdd_id());


                try
                {
                    List<String> PackageName = Krw_functions.getShareApplication();
                    List<Intent> targetedShareIntents = new ArrayList<Intent>();
                    ArrayList arrayList = new ArrayList();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    List<ResolveInfo> resInfo = getApplication().getPackageManager().queryIntentActivities(share, 0);
                    String message = "L'annonce  \" "+myads.getTitle()+" \" de Kerawa.com peut t'intéresser, cliques sur le lien ci dessous pour en savoir plus.\n" +
                            myads.getFriendly_url()+"\n\n"+
                            "**************\n"+
                            "Pensez aussi à télécharger l'application Android Kerawa.com sur Playstore (https://play.google.com/store/apps/details?id=com.kerawa.app) ou sur Kerawa.com/Android directement ";
                    //Log.d("ad-short_url",myads.getFriendly_url());
                    if (!resInfo.isEmpty()){
                        for (ResolveInfo info : resInfo) {
                            Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
                            targetedShare.setType("text/plain"); // put here your mime type
                            if (PackageName.contains(info.activityInfo.packageName.toLowerCase())) {
                                targetedShare.putExtra(Intent.EXTRA_TEXT,message);
                                targetedShare.setPackage(info.activityInfo.packageName.toLowerCase());
                                targetedShareIntents.add(targetedShare);

                            }
                        }
//                        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share oops");
//                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                        //startActivity(chooserIntent);
                        Bundle bundle = new Bundle();
                        bundle.putString("sharedtext", message);
                        bundle.putString("title", "Partager-");
                        bundle.putString("ids", "Annonce N° "+adsID);
                        bundle.putString("userid", myads.getUser_id());
                        bundle.putString("adsId", adsID);
                        bundle.putString("usercity", myads.getCity_name());
                        Intent intent = new Intent(getApplicationContext(), ShareAds.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/email_button_pressed/user=" + myads.getUser_id() + "/ad_id=" + myads.getAdd_id());

                if (!myads.getEmail().trim().equals("")) {
                    String[] addresses = {myads.getEmail()};
                    String subject = "Réponse à votre annonce " + myads.getTitle() + " sur Kerawa.com";
                    String message = "Bonjour,\n" +
                            "\n" +
                            "Je réponds à votre annonce " + myads.getTitle() + " parue sur Kerawa.com.\n" +
                            "Son URL est la suivante " + myads.getFriendly_url() + ".\n" +
                            "\n";
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    if (intent.resolveActivity(getPackageManager()) != null) {

                        startActivity(intent);

                    } else {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.texttoast);
                        text.setText("Veuillez installer une application de messagerie pour effectier cette operation");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                } else {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.texttoast);
                    text.setText("le createur de cette annonce n'a pas souhaité être joint par email");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }

            }
        });

        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // initializing trackers
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())
                        + "/call_button_pressed/user=" + myads.getUser_id() + "/ad_id=" + myads.getAdd_id());


                //Toast.makeText(getApplicationContext(),"phone1 = "+myads.getPhone1()+" phone2= "+myads.getPhone2(),Toast.LENGTH_LONG).show();
                if (!myads.getPhone2().trim().equalsIgnoreCase("null") && !myads.getPhone2().trim().equalsIgnoreCase("")) {
                    if (!myads.getPhone1().trim().equalsIgnoreCase("null") && !myads.getPhone1().trim().equalsIgnoreCase("")) {

                        String[] num = {myads.getPhone1(), myads.getPhone2()};
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("num", num);
                        bundle.putString("action", "call");
                        bundle.putString("ids", "Annonce N° " + adsID);
                        bundle.putString("category", cattocall);
                        bundle.putString("userid", myads.getUser_id());
                        bundle.putString("adsId", adsID);
                        bundle.putString("usercity", myads.getCity_name());
                        Intent callsms = new Intent(AdsDeepLinkingActivity.this, makeCallorText.class);
                        callsms.putExtras(bundle);
                        startActivity(callsms);

                    }


                } else {
                    if (!myads.getPhone1().trim().equalsIgnoreCase("null") && !myads.getPhone1().trim().equalsIgnoreCase("")) {
                        String[] nums = {myads.getPhone1()};
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("num", nums);
                        bundle.putString("action", "call");
                        bundle.putString("ids", "Annonce N° " + adsID);
                        bundle.putString("userid", myads.getUser_id());
                        bundle.putString("category", cattocall);
                        bundle.putString("adsId", adsID);
                        bundle.putString("usercity", myads.getCity_name());
                        Intent intent1 = new Intent(AdsDeepLinkingActivity.this, makeCallorText.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);


                    }
                }


            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/sms_button_pressed/user=" + myads.getUser_id() + "/ad_id=" + myads.getAdd_id());

                if (!myads.getPhone2().trim().equalsIgnoreCase("null") && !myads.getPhone2().trim().equalsIgnoreCase("")) {
                    if (!myads.getPhone1().trim().equalsIgnoreCase("null") && !myads.getPhone1().trim().equalsIgnoreCase("")) {
                        // selectNumber("sms",2);

                        String[] num = {myads.getPhone1(), myads.getPhone2()};
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("num", num);
                        bundle.putString("action", "text");
                        bundle.putString("ids", "Annonce N° " + adsID);
                        bundle.putString("userid", myads.getUser_id());
                        bundle.putString("adsId", adsID);
                        bundle.putString("category", cattocall);
                        bundle.putString("usercity", myads.getCity_name());
                        Intent intent1 = new Intent(AdsDeepLinkingActivity.this, makeCallorText.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);


                    }

                } else {
                    if (!myads.getPhone1().trim().equalsIgnoreCase("null") && !myads.getPhone1().trim().equalsIgnoreCase("")) {
                        //selectNumber("sms",1);
                        String[] num = {myads.getPhone1()};
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("num", num);
                        bundle.putString("action", "text");
                        bundle.putString("ids", "Annonce N° " + adsID);
                        bundle.putString("userid", myads.getUser_id());
                        bundle.putString("category", cattocall);
                        bundle.putString("adsId", adsID);
                        bundle.putString("usercity", myads.getCity_name());
                        Intent intent1 = new Intent(AdsDeepLinkingActivity.this, makeCallorText.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);


                    }
                }


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ici
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/next_button_pressed");
                LoadNext();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/previous_button_pressed");
                LoadPrevious();
            }
        });

    }


    //=====================================================================================

    private void hide_loader(){
        View loader=findViewById(R.id.loader);
        ScrollView scroller= (ScrollView) findViewById(R.id.scroller);
        RelativeLayout Body= (RelativeLayout) findViewById(R.id.rel);
        RelativeLayout footer= (RelativeLayout) findViewById(R.id.footer);
        Body.setVisibility(View.VISIBLE);
        Body.setBackgroundColor(Color.TRANSPARENT);
        footer.setVisibility(View.VISIBLE);
        scroller.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }


    //======================================================================================



    @Override
    public void onSliderClick(BaseSliderView slider) {
        String imageurl= slider.getBundle().get("extra") + "";
        String imagename_=imageurl.substring(imageurl.lastIndexOf("/") + 1);
        //Show_Toast(getApplicationContext(), String.valueOf(ImageStorage.checkifImageExists(imagename_, adsID)),true);

        if(!ImageStorage.checkifImageExists(imagename_, adsID))
        {
            GetImages imagegeter=new GetImages(imageurl, imageurl.substring(imageurl.lastIndexOf("/")+1),adsID,true);
            imagegeter.ctx=getApplicationContext();
            imagegeter.v=loader;
            imagegeter.execute();
        }
        else{
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri=Uri.fromFile(ImageStorage.getImage(imagename_,adsID));
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                i.setDataAndType(uri,mimetype);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Krw_functions.Show_Toast(getApplicationContext(), uri.toString(), true);
                startActivity(i);
            }catch (Exception e){

                Krw_functions.Show_Toast(getApplicationContext(), e.toString(), true);
            }
        }

    }


    //====================================================================================

    private void LoadPrevious() {
        if(position!=0) {
            position = position -1;
            try {
                load_ads(JSONTOAD(liste.getJSONObject(position)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"vous êtes au sommet de la liste",Toast.LENGTH_LONG).show();
    }


    //===================================================================

    private void LoadNext() {

        if(liste!=null) {
            if (position == liste.length()- 1) {
                //chargement des nouvelles annonces

                try {
                    LoadSimilars(liste.getJSONObject(position).getString("ad_title"));
                }
                catch (Exception ex)
                {
                    Log.d("exception",ex.toString());
                }
                position=position+1 ;
            }






            if (position >= Math.floor((liste.length() - 1) / 2)) {
                if(!search && !value){
                    LoadMore();
                }
                if (value || search)
                {
                    try {
                        LoadSimilars(liste.getJSONObject(position).getString("ad_title"));
                    }
                    catch (Exception ex)
                    {Log.d("exception",ex.toString());}
                }
                position = position+1 ;
                try {
                    Log.d("hope", liste.getJSONObject(position).getString("ad_date"));
                    load_ads(JSONTOAD(liste.getJSONObject(position)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (position < Math.floor((liste.length() - 1) / 2)) {
                //Annonce item = liste.get(position);
                position = position+1;
                try {
                    Log.d("hope",liste.getJSONObject(position).getString("ad_date"));
                    load_ads(JSONTOAD(liste.getJSONObject(position)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            Toast.makeText(getApplicationContext(),"erreur lors du chargement",Toast.LENGTH_LONG).show();
    }

    //======================================================================================

    private void LoadSimilars(String ad_title) {
        String encoded_title = "";
        try {
            encoded_title = URLEncoder.encode(ad_title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nom = prefs.getString("Kerawa-count", null);
        if (nom == null) {
            startActivity(new Intent(getApplicationContext(),CountryList.class));
        }
        nom = countryName(nom,getApplicationContext());
        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, Kerawa_Parameters.PreProdURL+"regions/"+nom+"/ads?"+getIntent().getStringExtra("category")+",-date,"+encoded_title, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // the response is already constructed as a JSONObject!
                try {
                    similarList.clear();
                    //  linearLayout.removeAllViews();
                    JSONArray arr = response.getJSONArray("data");
                    int max = 5 ;
                    if(max>=arr.length()) max = arr.length() ;

                    ViewGroup.LayoutParams params = liste_similaires.getLayoutParams();
                    params.height = max*80;
                    liste_similaires.setLayoutParams(params);
                    liste_similaires.requestLayout();

                    for (int i = 0; i <= max-1; i++) {
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

                        if (arr.getJSONObject(i).has("ad_url")) {
                            an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                        }
                        an.setDate(arr.getJSONObject(i).get("ad_date").toString());


                        Log.d("similarAD", arr.getJSONObject(i).getString("ad_title"));

                        liste.put(arr.getJSONObject(i));

                        similarList.add(an);

                        //ajout de l'annonce au layout
                        //  ImageButton btn = new ImageButton(getApplicationContext());
                        //  btn.setImageDrawable();
                        //  btn.setTe
                        // linearLayout.addView(btn);


                    }
                    liste_similaires.setVisibility(View.VISIBLE);
                    //  linearLayout.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.d("error",e.toString());
                }


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
                                    Log.d("Erreur_kerawa", error.toString());
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


        try {
            MySingleton.getInstance(this).addToRequestQueue(movieReq);
            movieReq.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }catch (Exception e){
            Show_Toast(this, e.toString(), true);}


    }



 //=================================================================================================



    private Annonce JSONTOAD(JSONObject jsonObject) {

        Annonce an = new Annonce();
        try {
            Log.d("json2ad",jsonObject.getString("ad_date"));

            an.setTitle(jsonObject.getString("ad_title"));
            an.setAdd_id(jsonObject.getString("ad_id"));
            an.setImage_thumbnail_url(jsonObject.getString("ad_image_thumbnail_url"));
            an.setEhanced(jsonObject.getString("ad_enhanced"));
            ArrayList<String> images=new ArrayList<>();

            JSONArray liste_img=jsonObject.getJSONArray("ad_images_urls");
            for(int k=0;k<liste_img.length();k++){
                images.add(liste_img.get(k).toString().replace("https", "http"));
            }
            an.setImgList(images);
            an.setPrice(price_formated(jsonObject.getString("ad_price")));
            an.setFriendly_url(jsonObject.getString("ad_short_link"));
            String currency = "";
            String price = "" ;
            if (jsonObject.has("ad_currency")) {
                currency = jsonObject.get("ad_currency").toString();
                an.setCurrency(currency);
                if (currency.equals("null")) currency = "";
            }
            if (price.equals("0")||price.equals("")) {
                price = "";
                currency = "";
            }
            if (price.equals("")) an.setPrice("Aucun");
            else an.setPrice(price + " " + currency);
            an.setCity_name(jsonObject.getString("ad_city_name"));
            an.setPhone1(jsonObject.getString("ad_phonenumber"));
            an.setDescription(jsonObject.get("ad_description").toString());
            an.setEmail(jsonObject.get("ad_contactemail").toString());
            an.setCategory_name(jsonObject.getString("ad_category_name") + " -> " + jsonObject.getString("ad_subcategory_name"));

            if(jsonObject.has("ad_user_id"))
                an.setUser_id(jsonObject.getString("ad_user_id"));

            an.setDate(jsonObject.getString("ad_date"));
            an.setAd_url(jsonObject.getString("ad_url"));


            Log.d("dce",jsonObject.get("ad_date").toString());

            try {
                //an.setPhone2(arr.getJSONObject(i).get("ad_telephone02").toString());
                JSONObject meta = jsonObject.getJSONObject("ad_metas");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return an;
    }



    //===========================================================================================




    private void LoadMore() {
        //page
        //categorie
        //ville

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nom = prefs.getString("Kerawa-count", null);

        String added_url = "";
        if (nom == null) {

            this.startActivity(new Intent(getApplicationContext(), CountryList.class));
            this.finish();

        } else {

            nom = countryName(nom, getApplicationContext());
            try {
                if (!city.equals("") && !city.equals("null")) added_url = "/city/" + city;
            } catch (Exception e) {
                e.printStackTrace();
            }

            String first_url = Kerawa_Parameters.PreProdURL + next_url;
            Log.d("loading",first_url);
            JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // the response is already constructed as a JSONObject!
                    try {
                        JSONArray arr = response.getJSONArray("data");
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

                            ArrayList<String> images = new ArrayList<>();


                            if (arr.getJSONObject(i).has("ad_images_urls")) {
                                JSONArray liste_img = arr.getJSONObject(i).getJSONArray("ad_images_urls");
                                for (int k = 0; k < liste_img.length(); k++) {
                                    images.add(liste_img.get(k).toString().replace("https", "http"));
                                }
                                an.setImgList(images);
                            }


                            String price = "";
                            if (arr.getJSONObject(i).has("ad_price")) {

                                //price = String.format("%,d", (arr.getJSONObject(i).getString("price") == null ||arr.getJSONObject(i).getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getJSONObject(i).getString("price")));
                                price = price_formated(arr.getJSONObject(i).getString("ad_price"));
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
                            if (price.equals("0") || price.equals("")) {
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
                                HashMap<String, String> val = new HashMap<>();

                                Iterator<String> it = meta.keys();
                                Log.d("server metas", meta.toString() + "," + an.getTitle());
                                while (it.hasNext()) {
                                    JSONObject obj = meta.getJSONObject(it.next());
                                    if (obj.get("meta_value") != null) {
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
                                an.setPrevious(arr.getJSONObject(i-1).get("ad_id").toString());
                            } else {
                                an.setPrevious("");
                            }

                            liste.put(arr.getJSONObject(i));

                        }

                        loader.setVisibility(View.GONE);

                        JSONObject obj = response.getJSONObject("meta");
                        if (obj.has("next")) {
                            next_url = obj.getString("next");
                        }
                        //save the response inside the database

                    } catch (JSONException e) {
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
                            if (response != null && response.data != null)
                                switch (response.statusCode) {
                                    case 500:
                                        try {
                                            Log.d("network error", error.toString());
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
                    params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                    return params;
                }
            };


            try {
                MySingleton.getInstance(this).addToRequestQueue(movieReq);


                movieReq.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } catch (Exception e) {
                //Show_Toast(this,e.toString(),true);
            }


        }

    }



  //============================================================================


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }




}
