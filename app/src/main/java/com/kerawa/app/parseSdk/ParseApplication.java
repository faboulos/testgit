package com.kerawa.app.parseSdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.kerawa.app.BuildConfig;
import com.kerawa.app.R;
import com.kerawa.app.utilities.AndroideDevice;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

//import io.intercom.android.sdk.Intercom;

import static com.kerawa.app.utilities.Krw_functions.addShortcut;
import static com.kerawa.app.utilities.Krw_functions.isConnected;
import android.support.multidex.MultiDex;
public class ParseApplication extends Application {
    private String installID;

    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getResources().getString(R.string.first_string), getResources().getString(R.string.second_string));
       // Intercom.initialize(this, "android_sdk-76bf6befcb12551c003b592e709218f930e82f3c", "v3goct21");

        //ParseInstallation.getCurrentInstallation().saveInBackground();
        installID="";
        if(isConnected(this)) {
            try {
                ParseInstallation.getCurrentInstallation().saveInBackground();
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Techmical_Data");
                query.whereEqualTo("Imei", new AndroideDevice(this).getImei());
                query.setLimit(1);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(final List<ParseObject> list, com.parse.ParseException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                ParseObject InstallScore = new ParseObject("Techmical_Data");
                                try {
                                    InstallScore.put("currentID", ParseInstallation.getCurrentInstallation().getObjectId());
                                } catch (Exception ignored) {
                                }

                                if(new AndroideDevice(getApplicationContext()).getImei()!=null) InstallScore.put("Imei", new AndroideDevice(getApplicationContext()).getImei());
                                if(new AndroideDevice(getApplicationContext()).getCountry()!=null) InstallScore.put("Country", new AndroideDevice(getApplicationContext()).getCountry());
                                if(new AndroideDevice(getApplicationContext()).getOperator()!=null)  InstallScore.put("Operator", new AndroideDevice(getApplicationContext()).getOperator());
                                if(new AndroideDevice(getApplicationContext()).getEmailsList().get(0)!=null) InstallScore.put("Emails", new AndroideDevice(getApplicationContext()).getEmailsList().get(0));
                                if(new AndroideDevice(getApplicationContext()).getPhone_number()!=null) InstallScore.put("PhoneNumber", new AndroideDevice(getApplicationContext()).getPhone_number());
                                InstallScore.saveInBackground();
                                Log.d("Installation", "Saved ");
                                addShortcut(getApplicationContext());
                            } else {
                                ParseObject parseObject = list.get(0);
                                try {
                                    installID = ParseInstallation.getCurrentInstallation().getObjectId();
                                } catch (Exception ignored) {
                                    installID="";
                                }
                                final String CurrentInstallID = parseObject.getString("currentID");
                                if ((installID != null)&&!installID.equals("")){
                                    if ((installID != null) && !installID.equals(CurrentInstallID)) {
                                        parseObject.put("currentID", installID);
                                     if(new AndroideDevice(getApplicationContext()).getImei()!=null)   parseObject.put("Imei", new AndroideDevice(getApplicationContext()).getImei());
                                     if(new AndroideDevice(getApplicationContext()).getCountry()!=null)   parseObject.put("Country", new AndroideDevice(getApplicationContext()).getCountry());
                                     if(new AndroideDevice(getApplicationContext()).getOperator()!=null)   parseObject.put("Operator", new AndroideDevice(getApplicationContext()).getOperator());
                                     //if(new AndroideDevice(getApplicationContext()).getEmailsList().get(0)!=null)   parseObject.put("Emails", new AndroideDevice(getApplicationContext()).getEmailsList().get(0));
                                     if(new AndroideDevice(getApplicationContext()).getPhone_number()!=null) parseObject.put("PhoneNumber", new AndroideDevice(getApplicationContext()).getPhone_number());
                                        parseObject.saveInBackground(new SaveCallback() {
                                                                         @Override
                                                                         public void done(ParseException e) {
                                                                             ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Installation");
                                                                             query2.getInBackground(CurrentInstallID, new GetCallback<ParseObject>() {
                                                                                 @Override
                                                                                 public void done(ParseObject object, com.parse.ParseException e) {
                                                                                     if (e == null) {
                                                                                         object.deleteEventually();
                                                                                     }
                                                                                 }
                                                                             });
                                                                         }
                                                                     }

                                        );
                                        addShortcut(getApplicationContext());
                                        Log.d("Installation", "Updated " + parseObject.get("Imei"));
                                    }
                                }
                            }

                        } else {
                            Log.d("Installation", "Error: " + e.getMessage());
                        }
                    }

                });

            } catch (Exception e) {
                Log.e("some_error", BuildConfig.FLAVOR + e);
            }
            ParsePush.subscribeInBackground("kerawaAll");
        }
    }
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

}
