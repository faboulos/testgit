package com.kerawa.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.utilities.AndroideDevice;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Person;
import com.newrelic.agent.android.NewRelic;

import static com.kerawa.app.utilities.Krw_functions.log_in2;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class LoginActivity extends AppCompatActivity {
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // plateforme de notification des erreurs
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());




        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Login";
        mDescription = "login page of kerawa.com";

        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())+"/Activity_Login");
      //  if(getSupportActionBar() !=null)  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
        Button loggin_button = (Button) findViewById(R.id.login_button);
        final EditText telephone = (EditText) findViewById(R.id.votre_numero);
        final EditText mdp = (EditText) findViewById(R.id.password);
        final TextView v2= (TextView) findViewById(R.id.status_text);
        final Button register_opener= (Button) findViewById(R.id.register_text);



        // Intent charge de la reception des actions liees au menu
    /*    Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
         final String menuID= bundle.getString("MenuId");*/

       /* loggin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

         Toast.makeText(LoginActivity.this,"")
            }
        });

        register_opener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


        //Bundle extras = getIntent().getExtras();
        //if (menuID != null)  Show_Toast(getApplicationContext(),menuID,false);
        /*loggin_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/login_button_pressed");

                if (telephone.getText().toString().equals("") || mdp.getText().toString().equals("")) {
                    v2.setVisibility(View.VISIBLE);
                    v2.setText("Veuillez remplir tous les Champs s'il vous plait");

                    return;
                }
                Person user = new Person();
                user.SetImei(new AndroideDevice(LoginActivity.this).getImei());
                user.SetEmail(telephone.getText().toString());
                user.SetPass(mdp.getText().toString());
                //Toast.makeText(getApplicationContext(), "imei is "+(new AndroideDevice(LoginActivity.this).getImei()), Toast.LENGTH_SHORT).show();
                if (menuID != null) {
                    log_in2(LoginActivity.this, Integer.parseInt(menuID), user);
                }
            }
        });

        register_opener.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/register_from_login_button_pressed");

                //Open_form(LoginActivity.this,menuID,R.layout.dialog_layout_register);
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                //finishThisActivity(LoginActivity.this);
            }
        });*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_activity, menu);
        return true;
    }
    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/user/login"))
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action:
                onBackPressed();
                return true;
         //   case R.id.action_settings:
         //       return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
