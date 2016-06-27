package com.kerawa.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.interfaces.JavaScriptInterface;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;
import com.squareup.picasso.Picasso;


public class Blog_Activity extends AppCompatActivity {

    String andImage;
    String artDescription;
    String artDate;
    String ArtTitle;
    String ArtID;
    WebView description;
    TextView titre;
    DisplayMetrics display;
    TextView ladate;
    ImageView image;
    int taille;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        Intent itender=getIntent();
        if(getSupportActionBar()!=null) getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
         Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Blog_Article/" +itender.getStringExtra("title").replace(" ","_"));
        andImage = getIntent().getStringExtra("image");
        artDescription=itender.getStringExtra("description");
        artDate= itender.getStringExtra("date");
        ArtTitle=itender.getStringExtra("title");
        display = this.getResources().getDisplayMetrics();
        taille=display.widthPixels;
        description= (WebView) findViewById(R.id.code_source);
        titre= (TextView) findViewById(R.id.title);
        titre.setText(ArtTitle);
        ladate= (TextView) findViewById(R.id.ladate);
        ladate.setText(artDate);
        image= (ImageView) findViewById(R.id.image);
        Picasso.with(getApplicationContext())
                .load(andImage)
                .placeholder(R.drawable.logo2)
                .into(image);
        description.addJavascriptInterface(new JavaScriptInterface(this, description), "MyHandler");
        String Script="<script type=\"text/javascript\">" +
                "self.addEventListener(\"load\",function(){" +
                "document.querySelector('body')\n" +
                ".addEventListener('click', function (event) {\n" +
                "    switch(event.target.tagName) { \n" +
                "case 'A':"+
                "        event.preventDefault();\n" +
                "window.MyHandler.Openurl(event.target.getAttribute(\"href\"));"+
                "        show();\n" +
                "break;" +
                "case 'IMG':" +
                "event.preventDefault();\n" +
                "window.MyHandler.Openurl(event.target.getAttribute(\"src\"));" +
                "break;"+
                "    }\n" +
                "});" +
                "});" +
                "</script>";
        description.getSettings().setJavaScriptEnabled(true);
        description.loadDataWithBaseURL(null, "<html><head><style type=\"text/css\">img {width:100% !important;height:auto !important;}</style>\n"+Script+"</head><body><div style=\"width:98%;max-width:" + taille + "px;\">" + artDescription + "</div></body></html>", "text/html", "UTF-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
               // Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/register_from_login_button_pressed");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
