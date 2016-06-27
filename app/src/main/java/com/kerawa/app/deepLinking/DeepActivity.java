package com.kerawa.app.deepLinking;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kerawa.app.R;
import com.newrelic.agent.android.NewRelic;

public class DeepActivity extends AppCompatActivity {

    private final HelperDeepLinking mMapper = new HelperDeepLinking(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_deep_linking);

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        try {
            mMapper.dispatchIntent(getIntent());

        } catch (IllegalArgumentException iae) {
            // Malformed URL
            if (BuildConfig.DEBUG) {
                Log.e("Deep links", "Invalid URI", iae);
            }
        } finally {
            // Always finish the activity so that it doesn't stay in our history
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deep_linking, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
