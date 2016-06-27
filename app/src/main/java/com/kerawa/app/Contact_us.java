package com.kerawa.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.kerawa.app.R;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.appindexing.Thing;

public class Contact_us extends ActionBarActivity {



    private ImageButton email_btn ;
    private ImageButton appel_cbr_btn ;
    private ImageButton appel_ckin_btn ;
    private ImageButton appel_sn_btn ;
    private ImageButton appel_civ_btn ;
    private ImageButton appel_gbn_btn ;
    private ImageButton appel_cmr_btn ;

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_Contact_us");

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Contact";
        mDescription = "Contact page of kerawa.com";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }

       email_btn =      (ImageButton)findViewById(R.id.imageButton);
       appel_cbr_btn =  (ImageButton)findViewById(R.id.image5);
       appel_civ_btn =  (ImageButton)findViewById(R.id.image2);
       appel_ckin_btn = (ImageButton)findViewById(R.id.image6);
       appel_cmr_btn =  (ImageButton)findViewById(R.id.image1);
       appel_gbn_btn =  (ImageButton)findViewById(R.id.image3);
       appel_sn_btn =   (ImageButton)findViewById(R.id.image4);



       appel_cbr_btn.setClickable(false);
       appel_civ_btn.setClickable(false);
       appel_ckin_btn.setClickable(false);
       appel_gbn_btn.setClickable(false);
       appel_sn_btn.setClickable(false);

        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send_email();
            }
        });
        appel_cmr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telURI = "tel:+237 699685142";
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                startActivity(intent);
            }
        });

    }
    private void send_email(){
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

         /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@kerawa.zendesk.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FeedBack Mobile");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

         /* Send it off to the Activity-Chooser */
        startActivity(Intent.createChooser(emailIntent, "Envoyer un email"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_us, menu);
        return true;
    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/contact"))
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
                // onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
