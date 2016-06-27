package com.kerawa.app;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.CustomAdapter;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by mabian Etinge
 */
public class makeCallorText extends AppCompatActivity {
    Context context;
    ArrayList arrayList;
    ListView lv;
    String p1;
    int counter = 0;

    private static Integer[] calls = {R.drawable.phoneicon, R.drawable.phoneicon};
    private static Integer[] text = {R.drawable.ic_sms, R.drawable.ic_sms};
    TextView textView;
    String title = "";
    String id = "";
    Annonce myads;
    String phoneNumber;
    String categ = "";
    String userCity = "";
    String userID = "";
    String adID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding the notification bar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.smscall);
        //instantiating new relic to monitor app
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());
        myads = (Annonce) getIntent().getExtras().getSerializable("annonce");
        final Bundle bundle = getIntent().getExtras();
        String[] numbs = bundle.getStringArray("num");
        String action = bundle.getString("action");
        id = bundle.getString("ids");
        categ = bundle.getString("category");
        userCity = bundle.getString("usercity");
        userID = bundle.getString("userid");
        adID = bundle.getString("adsId");

        //Google Tag Manager call
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())
                + "/makeCallorText_Activity/user=" + userID + "/city=" + userCity + "/category&sub_category=" + categ + "/ads_id=" + adID);

        //app rate prompt here
        KerawaAppRate.app_launches(this);

        lv = (ListView) findViewById(R.id.listview);
        textView = (TextView) findViewById(R.id.textV);

        context = this;


        //checking for invalid character in telephone numbers
        for (int i = 0; i < numbs.length; i++) {
            if (numbs[i].contains(") ")) {
                String[] tmp = {numbs[i].replace(") ", "")};
                numbs = tmp;
            }
        }


        //checking if the bundle content value is for call or sms
        if (action.equalsIgnoreCase("call")) {
            title = "Appel-";
            textView.setText(Html.fromHtml(getString(R.string.callDesc)));
            lv.setAdapter(new CustomAdapter(getApplicationContext(), numbs, calls, "Appuyez sur pour appeler"));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // add PhoneStateListener for monitoring
                KerawaCallListener listener = new KerawaCallListener();

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // receive notifications of telephony state changes
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

                    Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())
                            + "/call_number_pressed/user=" + userID + "/ad_id=" + adID);

                    NewRelic.withApplicationToken(
                            "AA405c71a7c2a19a69b59aa42f1558b2036787b393"
                    ).start(makeCallorText.this);

                    TextView number = (TextView) view.findViewById(R.id.number);
                    phoneNumber = number.getText().toString();

                    String callUri = "tel:" + phoneNumber;
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(callUri));

                    startActivity(callIntent);
                }
            });
        }else{
            title = "Texte-";
            textView.setText(Html.fromHtml(getString(R.string.smsDesc)));
            lv.setAdapter(new CustomAdapter(getApplicationContext(), numbs, text, "taper au texte"));


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //newRelic integration to monitor app activity for errors
                    NewRelic.withApplicationToken(

                            "AA405c71a7c2a19a69b59aa42f1558b2036787b393"
                    ).start(makeCallorText.this);

                    //sending what ever the user click from the listview to Google Tag Manager
                    Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())
                            + "/send_text_number_pressed/user=" + userID + "/ad_id=" + adID);

                    TextView number = (TextView) view.findViewById(R.id.number);

                    String smsUri = "sms:"+number.getText().toString();
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsUri));
                    smsIntent.putExtra(Intent.EXTRA_TEXT, "hello");
                    startActivity(smsIntent);
                }
            });

        }

        //setting the action bar
        if (getSupportActionBar() != null)
        {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
            getSupportActionBar().setTitle(title + id);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
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
    //creating CustomCallListener
    private class KerawaCallListener extends  PhoneStateListener{
        private boolean onCall = false;
        long start_time, end_time;
        TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        @Override
        public void onCallStateChanged(int state, String outgoingNumber){


            switch (state){

                case TelephonyManager.CALL_STATE_RINGING:
                    //the phone is ringing
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold

                    if(outgoingNumber.equalsIgnoreCase(phoneNumber)){
                        //because user answers the call
                        //here we get the total time talk, we can also track the call on google etc
                        onCall = true;
                    }else{
                        //Toast.makeText(context, "i can't track you", Toast.LENGTH_LONG).show();
                    }

                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call
                    //this tells you that the call has ended

                        if(onCall == true) {


                            Handler handlers = new Handler();
                            //Put in delay because call log is not updated immediately when state changed
                            // The dialler takes a little bit of time to write to it 500ms seems to be enough
                            handlers.postDelayed(new Runnable() {
                                StringBuilder builder = new StringBuilder();
                                String callerNumber = manager.getLine1Number();

                                @Override
                                public void run() {
                                    // get start of cursor
                                    Log.i("makeCallorText", "Getting Log activity...");



                                    String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION};
                                    Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE + " desc");

                                    int date = cur.getColumnIndex(CallLog.Calls.DATE);
                                    cur.moveToFirst();
                                    String lastCallnumber = cur.getString(0);
                                    String callDate = cur.getString(date);
                                    Date dateDayTime = new Date(Long.valueOf(callDate));
                                    String callduration = cur.getString(2);

                                    builder.append("number_call="+lastCallnumber + ", Date="+dateDayTime.toString()+"," +
                                            " Call_Duration="+callduration+", Who_Calls_number="+callerNumber);
                                    Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())
                                            + "/call_details_for/user=" + userID+"/city="+userCity+"/category="+categ+"/ad_id="+adID+"/CallInfo="+builder.toString());

                                    int time = Integer.valueOf(callduration);
                                    if(time == 0){

                                        final Dialog dialog = new Dialog(context);
                                        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                                        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
                                        dialog.setContentView(R.layout.faildialog);
                                        dialog.setTitle("Kerawa");
                                        dialog.setCancelable(false);
                                        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.rate_icon);

                                        Button notAvail = (Button)dialog.findViewById(R.id.notAval);
                                        Button drop = (Button)dialog.findViewById(R.id.calldrop);

                                        notAvail.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //we can get the user feedback here
                                                //if the number is not available
                                                //if feedbacks comes more than 5 times means the add is not valid, just to say
                                                dialog.dismiss();
                                            }
                                        });
                                        drop.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //make be the user might be busy, so he/she drop the call
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();

                                    }else if((time > 0) && (time < 30)){
                                            //we can ask the user what he/she think we can do to help
                                    }
                                    else{
                                        //Ask the user to rate the app
                                            KerawaAppRate.app_launches(getApplicationContext());
                                    }

                                    //This block here is for developers to see if the listener returns some information

//                                    Intent intent = new Intent(getApplicationContext(), TestNotification.class);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("details", builder.toString());
//                                    bundle.putString("cate", categ);
//                                    bundle.putString("calledno", lastCallnumber);
//                                    bundle.putString("timecall", dateDayTime.toString());
//                                    bundle.putString("duration", callduration);
//                                    bundle.putString("userno", callerNumber);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);


                                }
                            }, 500);
                            onCall = false;
                        }
                    break;


                default:
                    break;
            }
        }
    }


}


