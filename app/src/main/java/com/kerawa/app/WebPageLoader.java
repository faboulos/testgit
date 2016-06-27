package com.kerawa.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;

import static com.kerawa.app.utilities.Krw_functions.setupWebViewClient;

public class WebPageLoader extends AppCompatActivity{
    WebView myWebView;
    TextView myResultView;
    private String lien;

    /** Called when the activity is first created. */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.krw_webview);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        myWebView = (WebView)this.findViewById(R.id.myWebView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        lien= getIntent().getExtras().getString("lien");
        myWebView.loadUrl(lien);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/InternalBrowser/"+lien);
        setupWebViewClient(myWebView,this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(lien);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webloader, menu);
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
            onBackPressed();
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
