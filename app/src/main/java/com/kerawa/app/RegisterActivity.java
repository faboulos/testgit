package com.kerawa.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.helper.PostData;
import com.kerawa.app.utilities.AndroideDevice;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.Person;
import com.newrelic.agent.android.NewRelic;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.getemailData;

public class RegisterActivity extends AppCompatActivity {
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    RadioGroup mes_emails;
    private TextView statusText;
    private Spinner spinner;
    DatabaseHelper mbd;
    private HashMap<Integer,String> cities;
    Person user;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/register_activity");
        setContentView(R.layout.activity_register);


            user = new Person();
        mbd = new DatabaseHelper(this);

        //code de monitoring des eventuels bugs sur la plateforme New Relic
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Sign In";
        mDescription = "Register page of kerawa.com";

      //  init_registration();

        if(getSupportActionBar() !=null)  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
      //  registerReceiver(receiver_SMS, filter);
        statusText= (TextView) findViewById(R.id.status_text);
        spinner = (Spinner)findViewById(R.id.liste_villes);
        cities = new HashMap<>();
        cities = mbd.getAllCities(this);
        ArrayList<String> cityName = new ArrayList<>();
       final ArrayList<Integer> cityId = new ArrayList<>();
        Iterator it = cities.entrySet().iterator();
          while(it.hasNext()){
              Map.Entry pair = (Map.Entry)it.next();
              String st = pair.getValue().toString();
              Integer id = Integer.valueOf(pair.getKey().toString());
              cityName.add(st);
              cityId.add(id);
          }
        ArrayAdapter<String> adapt= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,cityName);
        spinner.setAdapter(adapt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RegisterActivity.this, "city id is " + cityId.get(position), Toast.LENGTH_SHORT).show();

                user.SetCityId(cityId.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        init_registration();


    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/user/register"))
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
        // SMS de reception

    BroadcastReceiver receiver_SMS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //---get the SMS message passed in---
            Log.i("message","=========================================message bien recu veuillez===========================patienter");
            Log.i("message","=========================================message bien recu veuillez===========================patienter");
            Log.i("message","=========================================message bien recu veuillez===========================patienter");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            boolean checking;
            String msg;
            if (intent.getAction().equals(SMS_RECEIVED))
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String from = msgs[i].getOriginatingAddress();
                    msg = msgs[i].getMessageBody();
//                    checking = msg.contains("KERAWA SMS CODE");
                    if(msg.contains("KERAWA SMS CODE") || !msg.split(":")[1].trim().equals("")){
                        Krw_functions.Show_Toast(context, "Activation du compte en cours", true);
                        String SMScode=msg.split(":")[1].trim();
                        Person user;
                        user= Krw_functions.Variables_Session(context);
                        user.SetSecretCode(SMScode);
                        PostData activate = new PostData();
                        activate.items=user;
                        activate.ctx=context;
                        activate.v=null;
                        activate.v2=null;
                        activate.menuID=R.id.action_account;
                        activate.execute();
                        if(activate.isState()){
                            Log.d("Register", "you are logined in now!!!");
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, "compte non active!!!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        continue;
                    }
                }
            }

        }


    };

    private void init_registration(){

        final Activity ctx= RegisterActivity.this;
        final LinearLayout first_step = (LinearLayout)findViewById(R.id.first_step);
        final IntlPhoneInput phone = (IntlPhoneInput) first_step.findViewById(R.id.votre_mobile);
        final EditText email = (EditText) first_step.findViewById(R.id.your_email);
        final LinearLayout step_two= (LinearLayout)findViewById(R.id.step_two);
        //final RadioGroup pays= (RadioGroup) step_two.findViewById(R.id.liste_pays);
        final EditText Name= (EditText) step_two.findViewById(R.id.user_name);
        //final RadioGroup villes= (RadioGroup) step_two.findViewById(R.id.liste_villes);
      //  final Spinner Cities= (Spinner) findViewById(R.id.liste_villes);
      //  final Spinner pays= (Spinner) findViewById(R.id.liste_pays);
        final Button b = (Button) findViewById(R.id.register_button);
        final int MenuID=R.id.action_account;
       // lespays=generateCountryList(getApplicationContext());
       // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // String nom=prefs.getString("Kerawa-count",null);

       // villes=generateCityList_from_CountryID(getApplicationContext(), countryName(nom, getApplicationContext()));
       // codes=countries_code(getApplicationContext());

      //  generate_spinner(villes, getApplicationContext(),Cities);
      //  generate_spinner(lespays, getApplicationContext(), pays);
       // generate_spinner(codes, getApplicationContext(), codelist);

         mes_emails = (RadioGroup) findViewById(R.id.email_list);
         getemailData(this, mes_emails);

         Name.setText(Build.MODEL+""+Build.SERIAL);

         mes_emails.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked = (RadioButton) mes_emails.findViewById(checkedId);
                email.setText(checked.getText());
            }
         });

        /*int ii = 0;
        do {
            Model elt = (Model) pays.getItemAtPosition(ii);
            if (elt.getTag().equals(nom)) pays.setSelection(ii);
            ii++;
        } while (ii < pays.getCount());

        int ki = 0;*/
      //  String iso_pays=new AndroideDevice(this).getCountry();
      //  codelist.setSelection(0);
      //  Show_Toast(this,iso_pays,true);

       /* do {
            Model elt= (Model) codelist.getItemAtPosition(ki);
            if(elt.getTag().split("-")[0].equalsIgnoreCase(iso_pays)) codelist.setSelection(ki);
            ki++;
        }while (ki<codelist.getCount());
*/

        /*pays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model model = (Model) pays.getSelectedItem();
                String nom = countryName(model.getTag(), getApplicationContext());
                villes = generateCityList_from_CountryID(getApplicationContext(),nom);
                generate_spinner(villes, getApplicationContext(), Cities);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
/* Model lepays= (Model) pays.getSelectedItem();
                    Model laville= (Model) Cities.getSelectedItem();
                   */
        //  Model lecode= (Model) codelist.getSelectedItem();


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String _error_text="";
                Model model = (Model) Cities.getSelectedItem();
                if(phone.getText().toString().equals("")) _error_text += String.format("%s- Veuillez entrer un numéro", !_error_text.equals("") ? "\r\n" : "");
                //if(email.getText().toString().equals("")) _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez entrer une adresse mail";
                //if(!(pays.getCheckedRadioButtonId()>0))   _error_text += (!_error_text.equals("") ? "\r\n- " : "") + "Veuillez choisir un pays pour vos annonces";
                if(model.getTag().equals("")) _error_text += (!_error_text.equals("") ? "\r\n- " : "") + "Veuillez choisir la ville de vos annonces";
                *//*if((Name.getText().toString().equals(""))) {
                    Name.requestFocus();                  _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez Préciser un nom";
                }*//*
                if (!checkEmail(email.getText().toString())) _error_text+= (!_error_text.equals("") ? "\r\n- " : "") + "Adresse email invalide";
               */

                String userEmail = email.getText().toString();
                String userID = Name.getText().toString();
                String userName = Name.getText().toString();
                String userPhoneNumber = phone.getNumber();
                final Person user = new Person();
                user.SetEmail(email.getText().toString());
                user.SetName(Name.getText().toString());
                user.SetUsername(Name.getText().toString());
                user.SetUserPhones(phone.getNumber());
                    /*user.SetCountry(Integer.valueOf(countryName(lepays.getTag(), getApplicationContext())));
                    user.SetCityId(Integer.valueOf(laville.getTag()));*/
                user.SetImei(new AndroideDevice(ctx).getImei());
                // Show_Toast(getApplicationContext(),code_from_text(lecode.getTag()) + phone.getText().toString(),true);




//                Toast.makeText(getApplicationContext(),phone.getNumber(),Toast.LENGTH_LONG).show();
                boolean bol = true ;

                if(!bol){


                   /* LinearLayout lebas=(LinearLayout) findViewById(R.id.linearLayout3);
                           lebas.requestFocus();*/
                    //Krw_functions.Show_Toast(ctx, _error_text, true);
                    /*Errors_handler msg_err=new Errors_handler(ctx,getApplicationContext(), _error_text,false,statusText,R.drawable.errore);
                    msg_err.execute(500, 500, 3000);

                   */
                }else{
                 /* Model lepays= (Model) pays.getSelectedItem();
                    Model laville= (Model) Cities.getSelectedItem();*/

                  //  Model lecode= (Model) codelist.getSelectedItem();

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                    String nom = prefs.getString("Kerawa-count", null);
                    nom = countryName(nom, RegisterActivity.this);
                    if(!checkEmail(email.getText().toString()))
                    {
                        statusText = (TextView)findViewById(R.id.status_text);
                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText("Veuillez entrer une adresse mail valide");
                    }
                    user.SetEmail(email.getText().toString());
                    user.SetName(Name.getText().toString());
                    user.SetUsername(Name.getText().toString());
                    user.SetUserPhones(phone.getNumber());
                    user.SetCountry(Integer.valueOf(nom));
                    user.SetImei(new AndroideDevice(ctx).getImei());


                    Log.i("number", "============================= mon numero de telephone  " + phone.getNumber() + "  ====================================");
                    Log.i("nom", "============================= mon nom  " + Name.getText() + "   ===========================");
                    Log.i("email", "============================= mon adresse mail  " + email.getText().toString() + "   ===========================");


                                    // Show_Toast(getApplicationContext(),code_from_text(lecode.getTag()) + phone.getText().toString(),true);
                                    Krw_functions.register2(ctx, user);



                    if (userEmail.equalsIgnoreCase("") || userID.equalsIgnoreCase("") || userPhoneNumber.equalsIgnoreCase("")) {
                        Toast.makeText(RegisterActivity.this, "Please errors found", Toast.LENGTH_LONG).show();
                    } else {
                        registeringDialog();
                        Krw_functions.register2(ctx, user);
                    }
//                }catch(Exception e){
//                    Log.d("Registration", e.getLocalizedMessage()+"");
//                }


//                boolean bol = true ;

//                if(bol){
//                   /* LinearLayout lebas=(LinearLayout) findViewById(R.id.linearLayout3);
//                           lebas.requestFocus();*/
//                    //Krw_functions.Show_Toast(ctx, _error_text, true);
//                    /*Errors_handler msg_err=new Errors_handler(ctx,getApplicationContext(), _error_text,false,statusText,R.drawable.errore);
//                    msg_err.execute(500, 500, 3000);
//
//                   */
//                }else{
//
//                    Krw_functions.register2(ctx, user);
//
//
               }
            Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())+"/register_button_pressed");

            }});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
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
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public void registeringDialog(){
        final  ProgressDialog ring = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Registering in progress....", true);
        ring.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    Log.d("Registering", e.getMessage());
                }
                ring.dismiss();
            }
        }).start();
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver_SMS);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver_SMS);
    }*/
}
