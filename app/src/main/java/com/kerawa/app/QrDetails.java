package com.kerawa.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView2;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.utilities.CardDetails;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.QrDetailAdapter;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class QrDetails extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private String json ;
    private JSONArray images;
    private ArrayList<String> img_url = new ArrayList<>();
    private SliderLayout[] mDemoSlider = new SliderLayout[1];
    private int DisplayHeight,DisplayWidth;
    private DisplayMetrics display;
    private int number = 0 ;
    private LinkedHashMap<String,String> data_map = new LinkedHashMap<>();
    private Button btnTag ;
    private String url = "";
    private Button btn;

    TextView titre, desc, price;
    //Button integral;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_details);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/scanner_results/" + getIntent().getStringExtra("code"));
       // btnTag = new Button(this);

        // plateforme de notification des erreurs
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        titre = (TextView)findViewById(R.id.title);
        desc = (TextView)findViewById(R.id.desc);
        price = (TextView)findViewById(R.id.price);
        btn = (Button)findViewById(R.id.but);

        if (getSupportActionBar() != null)
        {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }



       // autre =(Button)findViewById(R.id.plus);

        json = getIntent().getStringExtra("code");
        //Toast.makeText(getApplicationContext(),json,Toast.LENGTH_LONG).show();
        mDemoSlider[0] = (SliderLayout) findViewById(R.id.sliderqr);

        display = this.getResources().getDisplayMetrics();
        DisplayHeight=display.heightPixels;
        mDemoSlider[0].getLayoutParams().height = (int) ((DisplayHeight / 3) * 1.5);
        images = new JSONArray();


         createList();

        if (images.length()==0)
        {
            mDemoSlider[0].setVisibility(View.GONE);
            mDemoSlider[0].getLayoutParams().height = 0;
        }


        int index = 3 ;
        Set<String> keys = data_map.keySet();
        Iterator<String> iterator = keys.iterator();

      try{

            JSONArray ar = new JSONArray(json);
          for(int k=0;k<ar.length();k++){


             String ti  = ar.getJSONObject(k).getString("ti") ;
             String descrip = ar.getJSONObject(k).getString("des");
             String prix = ar.getJSONObject(k).getString("pr");
              titre.setText(ti);
              desc.setText(descrip);
              price.setText(price.getText()+"          "+prix);

          }

        }catch (Exception e){
          e.printStackTrace();
          Toast.makeText(getApplicationContext(), "Impossible de decrypter le QR code", Toast.LENGTH_LONG).show();
          finish();

        }

        while (iterator.hasNext())
        {
            String title = iterator.next() ;

            if((data_map.get(title)).equalsIgnoreCase("ti")){
                titre.setText(data_map.get("ti"));
            }

            if((data_map.get(title)).equalsIgnoreCase("des")){
                desc.setText(data_map.get("des"));
            }

            if((data_map.get(title)).equalsIgnoreCase("pr")){
               price.setText(price.getText()+"             "+data_map.get("pr"));
            }

            /*if((data_map.get(title)).equalsIgnoreCase("url")){
              integ.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      integ.setVisibility(View.VISIBLE);
                  }
              });
            }*/

            if(title.equalsIgnoreCase("id_ad")){
                getSupportActionBar().setTitle("Annonce N°  " +data_map.get(title));

            }

            /*LinearLayout lin = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30);
            params.setMargins(0, 10, 0, 10);
            params1.setMargins(0,0, 0,0);
            params2.setMargins(0,0, 0,0);
            LinearLayout lin1 = new LinearLayout(getApplicationContext());
            lin1.setBackgroundColor(Color.parseColor("#2196f3"));
            lin1.setLayoutParams(params1);

            TextView tv1 = new TextView(getApplicationContext());
            tv1.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            tv1.setTextAppearance(getApplicationContext(), R.style.Kerawa_QRTextTitle);
            tv1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));


            LinearLayout lin2 = new LinearLayout(getApplicationContext());
            lin2.setBackgroundColor(Color.WHITE);
            lin2.setLayoutParams(params2);

            TextView tv2 = new TextView(getApplicationContext());
            tv2.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            tv2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            tv2.setTextAppearance(getApplicationContext(), R.style.Kerawa_QRTextDetail);
*/
       /*     tv1.setText(title);
            tv2.setText(data_map.get(title));


            lin1.addView(tv1, 0);
            lin2.addView(tv2,0);



            lin.addView(lin1,0);
            lin.addView(lin2,1);

            lin.setPadding(2,2,2,2);
            lin.setGravity(Gravity.CENTER);

            lin.setOrientation(LinearLayout.VERTICAL);

            lin.setLayoutParams(params);
            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.BLACK);//Color.parseColor("#2196f3")); //white background
            border.setStroke(-1, Color.BLACK);//Color.parseColor("#2196f3")); //black border with full opacity
            border.setShape(GradientDrawable.RECTANGLE);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                lin.setBackgroundDrawable(border);
            } else {
                lin.setBackground(border);
            }
            LinearLayout rel = (LinearLayout) this.findViewById(R.id.li);
            rel.addView(lin, index);
            index = index + 1;*/
        }
        if(!url.trim().equalsIgnoreCase(""))
        {
           /* btnTag.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btnTag.setText("Cliquez pour voir plus ");
            btnTag.setGravity(Gravity.CENTER_HORIZONTAL);
            btnTag.setBackgroundColor(Color.parseColor("#4cae4c"));
            btnTag.setTextColor(Color.WHITE);*/
            //btnTag.setR
            //btnTag.setId();
            btn.setVisibility(View.VISIBLE);
           /* LinearLayout rel = (LinearLayout) this.findViewById(R.id.li);
            rel.addView(btnTag);*/
        }

        for(int k=0;k<images.length();k++){
            try {
                //img_url.add("http://preprod.kerawa.com/oc-content/uploads/"+images.get(k).toString());
               // String img = "http://preprod.kerawa.com/oc-content/uploads/"+images.get(k).toString();
                //img_url.add(img.replace("https", "http"));
               // img_url.add(images.get(k).toString().replace("https", "http"));
               img_url.add(images.get(k).toString().replace("https", "http"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        HashMap<String, String> url_maps = new HashMap<String, String>();
        if (img_url != null) {
            if (img_url.size() > 0) {
                int max = 5;
                int Total = img_url.size();
                max = Total;
                //if (img_url[0].length() < max) max = img_url[0].length();
                for (int k = 0; k < max; k++) {
                    int j = k + 1;
                    url_maps.put("Photo " + j + " / " + Total, img_url.get(k).replace("https", "http"));
                }


                for (String name : url_maps.keySet()) {
                    TextSliderView2 textSliderView = new TextSliderView2(getBaseContext());
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(url_maps.get(name))
                            .errorDisappear(false)
                            .error(R.drawable.kerawa)
                            .setScaleType(BaseSliderView.ScaleType.CenterInside)
                            .setOnSliderClickListener(QrDetails.this);

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
                mDemoSlider[0].addOnPageChangeListener(QrDetails.this);
                mDemoSlider[0].stopAutoCycle();

            }
        } else {
            mDemoSlider[0].setVisibility(View.GONE);
            mDemoSlider[0].getLayoutParams().height = 0;
        }
    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(), WebPageLoader.class);
            i.putExtra("lien",url);
            startActivity(i);
        }
    });
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {
        String imageurl= slider.getBundle().get("extra") + "";
        String imagename_=imageurl.substring(imageurl.lastIndexOf("/")+1);


        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri uri=Uri.fromFile(ImageStorage.getImage(imagename_,String.valueOf(System.currentTimeMillis())));
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
        getMenuInflater().inflate(R.menu.menu_qr_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createList()
    {
        ArrayList<CardDetails> result = new ArrayList<>();
        try {
            JSONArray js = new JSONArray(json);
            // JSONObject jsonObj = new JSONObject(json);
            for (int i=0;i<=js.length()-1;i++){
            JSONObject jsonObj = js.getJSONObject(i);
            Log.d("json"+i,jsonObj.toString());
            Iterator<String> keys = jsonObj.keys();
            int len = js.length();
            if (len <= 0) {
                finish();
                Toast.makeText(getApplicationContext(), "le QR code ne contient pas de données", Toast.LENGTH_SHORT);

            } else {

                /*while (keys.hasNext()) {
                    String title = keys.next();
                    String detail = jsonObj.getString(title);
                    Log.d("json", detail);
                    number = number + 1;
                    if (title.equalsIgnoreCase("images")) {
                        images = jsonObj.getJSONArray("images");
                    } else if (title.equalsIgnoreCase("url")) {
                        url = jsonObj.getString("url");
                    } else {
                        data_map.put(title, detail);
                    }
                }*/

                while (keys.hasNext()) {
                    String title = keys.next();
                    String detail = jsonObj.getString(title);
                    Log.d("json", detail);
                    number = number + 1;
                    if (title.equalsIgnoreCase("images")) {
                        images = jsonObj.getJSONArray("images");
                    }
                    if (title.equalsIgnoreCase("url")) {
                        url = jsonObj.getString("url");
                    }

                    data_map.put(title, detail);
                }
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Impossible de decrypter le QR code", Toast.LENGTH_LONG).show();
            finish();
        }


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
}
