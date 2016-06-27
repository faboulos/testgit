package com.kerawa.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.interfaces.JavaScriptInterface;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.ShowHide;
import com.kerawa.app.utilities.Spinner_adapter;
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
import static com.kerawa.app.utilities.Krw_functions.price_formated;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class DetailsFromShare extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View loader;
    String adsID;
    int DisplayHeight,DisplayWidth;
    DisplayMetrics display;
    Annonce network_ad;
    String id_annonce = "";
 
    private ArrayList<String> img_url;
    private RelativeLayout footer ;//= (RelativeLayout)findViewById(R.id.footer2);
    private LinearLayout linear ;
    private LinearLayout linear2 ;
    private LinearLayout linear3 ;
    private LinearLayout linear4 ;
    private SliderLayout[] mDemoSlider = new SliderLayout[1];
    private TextView[] title = new TextView[1];
    private WebView[] description = new WebView[1];
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;

    private TextView[] price1 = new TextView[1];
    private TextView[] category = new TextView[1];
    private TextView[] date = new TextView[1];
    private TextView city;
    private ArrayList<Annonce> similarList ;
    private Annonce myad ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding the notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());
        setContentView(R.layout.activity_details_from_share);
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        similarList = new ArrayList<>();

        display = this.getResources().getDisplayMetrics();
        DisplayHeight=display.heightPixels;
        DisplayWidth=display.widthPixels;
        footer = (RelativeLayout)findViewById(R.id.footer2);
        footer.setVisibility(View.GONE);
        linear = (LinearLayout)findViewById(R.id.navigation_drawer_linearLayout_entries_root_view2);
        linear2 = (LinearLayout)findViewById(R.id.linear2);
        linear3 = (LinearLayout)findViewById(R.id.linear3);
        linear4 = (LinearLayout)findViewById(R.id.linear4);

        linear.setVisibility(View.INVISIBLE);

        mDemoSlider[0] = (SliderLayout) findViewById(R.id.slider2);
        mDemoSlider[0].getLayoutParams().height = (int) ((DisplayHeight / 3) * 1.5);
        description[0] = (WebView) findViewById(R.id.description2);
        price1[0] = (TextView) findViewById(R.id.price4);
        category[0] = (TextView) findViewById(R.id.cat4);

        hideContent();


        city = (TextView) findViewById(R.id.ville2);
        title[0] = (TextView) findViewById(R.id.title2);

        date[0] = (TextView) findViewById(R.id.date1);

        //network_ad = (Annonce) getIntent().getExtras().getSerializable("annonce");
        Uri data = getIntent().getData();
        List<String> params = data.getPathSegments();
        id_annonce = params.get(0).substring(0,params.get(0).indexOf("_")); // "ad_short_url"
        Krw_functions.Show_Toast(getApplicationContext(),id_annonce,true);

          if (id_annonce!=null ) {

              if (getSupportActionBar() != null)
                  getSupportActionBar().setTitle("Annonce N° " + id_annonce);


              loader = findViewById(R.id.loader2);

              if (getSupportActionBar() != null) {
                  //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
                  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                  getSupportActionBar().setDisplayShowHomeEnabled(true);
                  getSupportActionBar().setHomeButtonEnabled(true);
              }

              load_ads();
          }
        else
              Krw_functions.Show_Toast(getApplicationContext(),"une erreur est survenue",true);

    }

    private void hideContent() {
        linear2.setVisibility(View.GONE);
        linear3.setVisibility(View.GONE);
        linear4.setVisibility(View.GONE);
        category[0].setVisibility(View.GONE);
        description[0].setVisibility(View.GONE);
        title[0].setVisibility(View.GONE);
        date[0].setVisibility(View.GONE);
        price1[0].setVisibility(View.GONE);
    }

    public void load_ads() {




        final String[] phone1 = {""};
        final String[] phone2 = {""};
        Button call;
        Button sms;
        Button share;
        Button email;
        

      //  sartloading();

        if (Krw_functions.isConnected(getApplicationContext())){



            JsonObjectRequest movieReq1 = new JsonObjectRequest(Request.Method.GET,Kerawa_Parameters.PreProdURL+"ads/"+id_annonce, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // the response is already constructed as a JSONObject!
                    try {
                        Log.d("LastQuery",response.toString());
                        //response = response.getJSONObject("data");
                        JSONObject arr = response.getJSONObject("data");
                        
                            Annonce an = new Annonce();
                            an.setAdd_id(arr.get("ad_id").toString());
                            if (arr.has("ad_title")) {
                                an.setTitle(arr.get("ad_title").toString());
                              }
                            if (arr.has("ad_image_thumbnail_url")) {
                                an.setImage_thumbnail_url(arr.get("ad_image_thumbnail_url").toString().replace("https", "http"));
                            } else {
                                an.setImage_thumbnail_url("");
                            }

                            ArrayList<String> images=new ArrayList<>();


                            if (arr.has("ad_images_urls")) {
                                JSONArray liste_img=arr.getJSONArray("ad_images_urls");
                                for(int k=0;k<liste_img.length();k++){
                                    images.add(liste_img.get(k).toString().replace("https", "http"));
                                }
                                an.setImgList(images);
                            }
                            if (arr.has("ad_user_id")) {
                              an.setUser_id(arr.getString("ad_user_id"));
                            }


                            String price = "";
                            if (arr.has("ad_price")) {

                                //price = String.format("%,d", (arr.getString("price") == null ||arr.getString("price").equals("null") ) ? 0 : Integer.parseInt(arr.getString("price")));
                                price =price_formated(arr.getString("ad_price"));
                            }
                            String currency = "";
                            if (arr.has("ad_currency")) {

                                currency = arr.get("ad_currency").toString();
                                an.setCurrency(currency);
                                if (currency.equals("null")) currency = "";
                            }
                            if (price.equals("0")||price.equals("")) {
                                price = "";
                                currency = "";
                            }
                            if (price.equals("")) an.setPrice("Aucun");
                            else an.setPrice(price + " " + currency);

                            if (arr.has("ad_city_name")) {
                                an.setCity_name(arr.get("ad_city_name").toString());
                            }
                            if (arr.has("ad_short_link")) {
                                an.setFriendly_url(arr.get("ad_short_link").toString());
                            }
                           if (arr.has("ad_category_name")) {
                            an.setCategory_name(arr.getString("ad_category_name") + " -> " + arr.getString("ad_subcategory_name"));
                           }
                            if (arr.has("ad_phonenumber")) {
                                an.setPhone1(arr.get("ad_phonenumber").toString());
                            }

                            if (arr.has("ad_telephone02")) {
                                an.setPhone2(arr.get("ad_telephone02").toString());
                            }

                            if (arr.has("ad_description")) {
                                an.setDescription(arr.get("ad_description").toString());
                            }
                            if (arr.has("ad_contactemail")) {
                                an.setEmail(arr.get("ad_contactemail").toString());
                            }
                            if (arr.has("ad_email")) {
                                an.setEmail(arr.getString("ad_email"));
                            }


                            if (arr.has("ad_url")) {
                                an.setAd_url(arr.get("ad_url").toString());
                            }
                            an.setDate(arr.get("ad_date").toString());

                            //chargement des annonces similaires
                              mTitle = an.getTitle();

                             LoadSimilars(an.getTitle());
                             myad = an ;
                            network_ad = an ;
                            Annonce myads = an ;
                            Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/details/user="+myads.getUser_id()+"/city="+myads.getCity_name()+"/category&sub_category="+myads.getCategory_name()+"/ads_id=" + id_annonce+"/_from_share");

                            String titl;
                            titl = an.getTitle();
                            String pr = price_formated(an.getPrice());
                            String desc = an.getDescription();
                            String cat = an.getCategory_name();
                            String dat = an.getDate();
                            String cit = an.getCity_name();
                            city.setText(cit);
                            phone1[0] = an.getPhone1();
                            phone2[0] = an.getPhone2();
                            String cur = an.getCurrency();
                            img_url=an.getImgList();


                        title[0].setText(title[0].getText() + " " + titl);
                        description[0].addJavascriptInterface(new JavaScriptInterface(DetailsFromShare.this, description[0]), "MyHandler");
                        String Script="<script type=\"text/javascript\">" +
                                "self.addEventListener(\"load\",function(){" +
                                "document.querySelector('body')\n" +
                                ".addEventListener('click', function (event) {\n" +
                                "    if(event.target.tagName === 'A') { \n" +
                                "        event.preventDefault();\n" +
                                "window.MyHandler.Openurl(event.target.getAttribute(\"href\"));"+
                                "        show();\n" +
                                "    }\n" +
                                "});" +
                                "});" +
                            "</script>";
                            //description[0].loadDataWithBaseURL(null, "<html><head><style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;\"><pre style=\"max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + desc + "</pre></div></body></html>", "text/html", "UTF-8", null);
                            description[0].loadDataWithBaseURL(null, "<html><head>"+Script+"<style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + network_ad.getDescription().replace("\n","<br>") + "</div></body></html>", "text/html", "UTF-8", null);
                            description[0].getSettings().setJavaScriptEnabled(true);
                            if (pr.trim().equalsIgnoreCase("0")) {
                                pr = "--";
                                cur = "--";
                            }
                            if (pr.trim().equalsIgnoreCase("null")) {
                                pr = "N/A";
                                cur = "";
                            }
                            if (pr.trim().equalsIgnoreCase("")) {
                                pr = "";
                                cur = "--";
                            }
                            price1[0].setText(pr + " " + cur);
                            category[0].setText(cat);
                            date[0].setText(date[0].getText() + " " + dat);

                            //hidePDialog();
                            footer.setVisibility(View.VISIBLE);
                            linear.setVisibility(View.VISIBLE);
                            linear2.setVisibility(View.VISIBLE);
                            linear3.setVisibility(View.VISIBLE);
                            linear4.setVisibility(View.VISIBLE);
                            title[0].setVisibility(View.VISIBLE);
                            date[0].setVisibility(View.VISIBLE);
                            description[0].setVisibility(View.VISIBLE);
                            category[0].setVisibility(View.VISIBLE);

                            new ShowHide(loader).Hide();

                            HashMap<String, String> url_maps = new HashMap<String, String>();
                            if (img_url != null) {
                                if (img_url.size() > 0) {
                                    int max = 5;
                                    int Total = img_url.size();
                                    max = Total;
                                    //if (img_url[0].length() < max) max = img_url[0].length();
                                    for (int k = 0; k < max; k++) {
                                        int j = k + 1;
                                        url_maps.put("Photo " + j + "/" + Total, img_url.get(k).replace("https", "http"));
                                    }


                                    for (String name : url_maps.keySet()) {
                                        TextSliderView2 textSliderView = new TextSliderView2(getBaseContext());
                                        // initialize a SliderLayout
                                        textSliderView
                                                 .description(name)
                                                .image(url_maps.get(name))
                                                .error(R.drawable.logo2)
                                                .errorDisappear(false)
                                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                                .setOnSliderClickListener(DetailsFromShare.this);

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
                                    mDemoSlider[0].addOnPageChangeListener(DetailsFromShare.this);
                                    mDemoSlider[0].stopAutoCycle();

                                }
                            } else {
                                mDemoSlider[0].setVisibility(View.GONE);
                                mDemoSlider[0].getLayoutParams().height = 0;
                            }

                        


                        //save the response inside the database

                    } catch (JSONException e) {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.texttoast);
                        text.setText("une erreur est survenue l'ors du chargement de l'annonce");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                        //description[0].setVisibility(View.VISIBLE);
                        //description[0].loadDataWithBaseURL(null, "<html><head><style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;\"><pre style=\"max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + "cette annonce n'existe pas sur le serveur" + "</pre></div></body></html>", "text/html", "UTF-8", null);
                        new ShowHide(loader).Hide();

                    }


                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {
                                    case 500:

                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_layout,
                                                (ViewGroup) findViewById(R.id.toast_layout_root));

                                        TextView text = (TextView) layout.findViewById(R.id.texttoast);
                                        text.setText("une erreur est survenue l'ors du chargement de l'annonce");

                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();

                                        description[0].setVisibility(View.VISIBLE);
                                        description[0].loadDataWithBaseURL(null, "<html><head><style type=\"text/css\">img {width:100% !important;height:auto !important;}</style></head><body><div style=\"width:98%;max-width:" + DisplayWidth + "px;\"><pre style=\"max-width:" + DisplayWidth + "px;word-wrap: break-word;\">" + "cette annonce n'existe pas sur le serveur" + "</pre></div></body></html>", "text/html", "UTF-8", null);
                                        loader.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(),AdsListActivity.class));

                                }
                                //Additional cases
                            }
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());
                    return params;
                }
            };

            MySingleton.getInstance(this).addToRequestQueue(movieReq1);
            movieReq1.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));






        } else {
            Krw_functions.Show_Toast(getApplicationContext(),"veuillez vous connecter",true);
            Intent mintent = new Intent(getApplicationContext(),AdsListActivity.class);
            startActivity(mintent);
        }

       /* Contact=(TextView)findViewById(R.id.co1);
        Contact.setText(Contact.getText()+" "+Contacts) ;*/

        call = (Button)findViewById(R.id.call2);
        sms = (Button)findViewById(R.id.sms2);
        share = (Button)findViewById(R.id.share2);
        email = (Button)findViewById(R.id.email2);

        call.setTransformationMethod(null);//pour la mise en miniscules des caracteres sinon par defaut les textes associés seront en majuscule
        sms.setTransformationMethod(null);
        share.setTransformationMethod(null);
        email.setTransformationMethod(null);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/partage/share_button_pressed");


                try
                {
                    List<String> PackageName = Krw_functions.getShareApplication();
                    List<Intent> targetedShareIntents = new ArrayList<Intent>();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    List<ResolveInfo> resInfo = getApplication().getPackageManager().queryIntentActivities(share, 0);
                    String message = "L'annonce  \" "+network_ad.getTitle()+" \" de Kerawa.com/android peut t'intéresser, cliques sur le lien ci dessous pour en savoir plus.\n" +
                            network_ad.getFriendly_url()+"\n\n"+
                            "**************\n"+
                            "Pensez aussi à télécharger l'application Android Kerawa.com sur Playstore (https://play.google.com/store/apps/details?id=com.kerawa.app) ou sur Kerawa.com/android directement ";
                    //Log.d("ad-short_url",network_ad.getFriendly_url());
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
                        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                        startActivity(chooserIntent);
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
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/partage/share_button_pressed");

                if (!network_ad.getEmail().trim().equals("")) {
                    String[] addresses = {network_ad.getEmail()};
                    String subject = "réponse à votre annonce "+network_ad.getTitle()+" sur Kerawa.com";
                    String message = "Bonjour,\n" +
                            "\n" +
                            "Je réponds à votre annonce "+network_ad.getTitle()+" parue sur Kerawa.com\n" +
                            "Son URL est la suivante "+network_ad.getFriendly_url()+"\n" +
                            "\n" ;
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
                }
                else
                {
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
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/partage/call_button_pressed");

               // Toast.makeText(getApplicationContext(),"phone1 = "+network_ad.getPhone1()+" phone2= "+network_ad.getPhone2(),Toast.LENGTH_LONG).show();
                if(!network_ad.getPhone2().trim().equalsIgnoreCase("null") && !network_ad.getPhone2().trim().equalsIgnoreCase(""))
                {
                    if (!network_ad.getPhone1().trim().equalsIgnoreCase("null") && !network_ad.getPhone1().trim().equalsIgnoreCase("")) {
                        selectNumber("call",2);
                    }
                    else{
                        String telURI = "tel:" + network_ad.getPhone2();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                        startActivity(intent);
                    }

                }
                else
                {
                    if (!network_ad.getPhone1().trim().equalsIgnoreCase("null") && !network_ad.getPhone1().trim().equalsIgnoreCase("")) {
                        selectNumber("call",1);

                    } else {
                        //the user does not want to be contacted by phone
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.texttoast);
                        text.setText("le createur de cette annonce n'a pas souhaité être joint par Telephone");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }



            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/partage/sms_button_pressed");

                if (!network_ad.getPhone2().trim().equalsIgnoreCase("null") && !network_ad.getPhone2().trim().equalsIgnoreCase("")) {
                    if (!network_ad.getPhone1().trim().equalsIgnoreCase("null") && !network_ad.getPhone1().trim().equalsIgnoreCase("")) {
                        selectNumber("sms", 2);
                    } else {
                        String telURI = "sms:" + network_ad.getPhone2();
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(telURI));
                        startActivity(intent);
                    }

                } else {
                    if (!network_ad.getPhone1().trim().equalsIgnoreCase("null") && !network_ad.getPhone1().trim().equalsIgnoreCase("")) {
                        selectNumber("sms", 1);

                    } else {
                        //the user does not want to be contacted by phone
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.texttoast);
                        text.setText("le createur de cette annonce n'a pas souhaité être joint par sms");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }


            }
        });


    }




    @Override
    public void onSliderClick(BaseSliderView slider) {
        String imageurl= slider.getBundle().get("extra") + "";
        String imagename_=imageurl.substring(imageurl.lastIndexOf("/")+1);


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
        switch (item.getItemId())
        {
            case android.R.id.home:
               // startActivity(new Intent(getApplicationContext(),AdsListActivity.class));
                startActivity(new Intent(getApplicationContext(),AdsListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void LoadNext() {

    }

    private void LoadPrevious() {



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

    private void selectNumber(final String parameter,int paramsNumber) {
        final ArrayList<Model> items = new ArrayList<>();
        if (paramsNumber != 1){
            items.add(new Model((parameter.equalsIgnoreCase("call")) ? R.drawable.phoneicon : R.drawable.messaging, "0", network_ad.getPhone1(), false));
            items.add(new Model((parameter.equalsIgnoreCase("call")) ? R.drawable.phoneicon : R.drawable.messaging, "1", network_ad.getPhone2(), false));
        }
        else
        {
            items.add(new Model((parameter.equalsIgnoreCase("call")) ? R.drawable.phoneicon : R.drawable.messaging, "0", network_ad.getPhone1(), false));
        }
        final AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle(parameter.equalsIgnoreCase("call") ? "Merci de confirmer que vous souhaitez lancer l\'appel " : "Merci de confirmer que vous souhaitez envoyer le SMS") ;

        builder.setIcon(R.drawable.ic_dropdown);

        builder.setAdapter(new Spinner_adapter(this, R.layout.single_spinner,items), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    if(parameter.equalsIgnoreCase("call"))
                    //call the phone1
                    {
                        String telURI = "tel:" + network_ad.getPhone1();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                        startActivity(intent);
                    }
                    if(parameter.equalsIgnoreCase("sms"))
                    //sms the phone1
                    {
                        //sms
                        String telURI = "sms:"  + network_ad.getPhone2();
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(telURI));
                        startActivity(intent);
                    }

                } else {

                    //call or send sms to the phone 2
                    if(parameter.equalsIgnoreCase("call"))
                    //call the phone2
                    {
                        String telURI = "tel:" + network_ad.getPhone2();

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));

                        startActivity(intent);
                    }
                    if(parameter.equalsIgnoreCase("sms"))
                    //sms the phone2
                    {

                        String telURI = "sms:" + network_ad.getPhone2();
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(telURI));
                        startActivity(intent);
                    }
                }
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

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

                        if (arr.getJSONObject(i).has("ad_url")) {
                            an.setAd_url(arr.getJSONObject(i).get("ad_url").toString());
                        }
                        an.setDate(arr.getJSONObject(i).get("ad_date").toString());


                        Log.d("similarAD", arr.getJSONObject(i).getString("ad_title"));

                        similarList.add(an);

                    }

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
    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(network_ad.getDescription())
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/2787592_"))
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
}
