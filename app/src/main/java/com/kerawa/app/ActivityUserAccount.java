package com.kerawa.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Person;

import org.json.JSONObject;

import static com.kerawa.app.utilities.Krw_functions.Open_form;
import static com.kerawa.app.utilities.Krw_functions.Variables_Session;
import static com.kerawa.app.utilities.Krw_functions.log_out;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.newrelic.agent.android.NewRelic;


public class ActivityUserAccount extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    SharedPreferences session_name;
    JSONObject json;
    String Nom_user,telephone,email;
    TextView name_cont,phone_cont,email_cont;
    Button ads_adder;
    Button session_closer;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    private TextView  pays;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_user_account");
        setContentView(R.layout.activity_user_account);
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Profile";
        mDescription = "kerawa user profile page";

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        Krw_functions.pushOpenScreenEvent(getApplicationContext(), "Activity User Account");
        session_name = PreferenceManager.getDefaultSharedPreferences(this);
        String username=session_name.getString("username",null);
        name_cont= (TextView) findViewById(R.id.nom_user);
        phone_cont= (TextView) findViewById(R.id.user_phone);
        email_cont= (TextView) findViewById(R.id.user_email);
        pays = (TextView)findViewById(R.id.country);
        ads_adder= (Button) findViewById(R.id.action_add);
        session_closer= (Button) findViewById(R.id.action_logout);
        ads_adder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/create_ads_from_login_button_pressed");
             Open_form(ActivityUserAccount.this, R.id.action_add,R.layout.dialog_layout);
            }
        });

        session_closer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               log_out(ActivityUserAccount.this);
            }
        });

            final Person curr_user = Variables_Session(ActivityUserAccount.this);
            name_cont.setText(curr_user.getUsername());
        try {
            phone_cont.setText(curr_user.getUserPhones());
            if(!curr_user.getUserPhones().equals("")) phone_cont.setVisibility(View.VISIBLE);
            email_cont.setText(curr_user.getEmail());
            if(!curr_user.getEmail().equals("")) email_cont.setVisibility(View.VISIBLE);
        }catch(Exception e){
            Log.d("UserAccount","an error occurred ======> "+e.getLocalizedMessage());
        }






//
//            final Person curr_user= Variables_Session(ActivityUserAccount.this);
//           // name_cont.setText(curr_user.getUsername());
//            phone_cont.setText(curr_user.getUserPhones());
            if(!curr_user.getUserPhones().equals("")) phone_cont.setVisibility(View.VISIBLE);
           // email_cont.setText(curr_user.getEmail());
           // if(!curr_user.getEmail().equals("")) email_cont.setVisibility(View.VISIBLE);



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivityUserAccount.this);
        String pay = prefs.getString("country", null);
        pays.setText(pay);

       // Show_Toast(this,"Under Construction...",false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,R.string.drawer_close){

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(curr_user.getUsername());
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Enabling Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.kerawa.com/user/profile"))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_user_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup content_frame,Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_user_account, content_frame, false);
            return rootView;
        }
    }
}
