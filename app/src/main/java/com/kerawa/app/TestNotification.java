package com.kerawa.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.newrelic.agent.android.NewRelic;

import org.w3c.dom.Text;

public class TestNotification extends AppCompatActivity {

    TextView det,cats, num, user, time, dur, mess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);

        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        det = (TextView)findViewById(R.id.details);
        cats = (TextView)findViewById(R.id.category);
        num = (TextView)findViewById(R.id.calledno);
        user = (TextView)findViewById(R.id.userno);
        time = (TextView)findViewById(R.id.timecall);
        dur  = (TextView)findViewById(R.id.duration);
        mess = (TextView)findViewById(R.id.message);

        Bundle bundle = getIntent().getExtras();
        String detail = bundle.getString("details");
        String category = bundle.getString("cate");
        String no = bundle.getString("calledno");
        String userno = bundle.getString("userno");
        String timecall = bundle.getString("timecall");
        String dura = bundle.getString("duration");


        det.setText(detail);
        cats.setText("Ad Category: "+category);
        num.setText("Call No: "+no);
        user.setText("User No: "+userno);
        time.setText("Call Date n Time: "+timecall);
        dur.setText("Call Duration: "+dura);

        int timeduration = Integer.valueOf(dura);
        if(timeduration == 0){
            mess.setText("You didn't get the dealer");
            mess.setBackgroundColor(Color.RED);
        }else if((timeduration > 0 ) && timeduration < 30){
            mess.setText("how can kerawa help you?");
            mess.setBackgroundColor(Color.blue(221));
        }else{
            mess.setText("Thanks for using kerawa");
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Call Details");
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
