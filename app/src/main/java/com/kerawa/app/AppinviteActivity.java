package com.kerawa.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;

public class AppinviteActivity extends AppCompatActivity implements
GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    //AppInvite settings
    private static final String TAG = AdsListActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    private GoogleApiClient googleApiClient;
    //end of AppInvite settings

    TextView procedure;
    Button invite;

    Krw_functions krw_functions;
 private String title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinvite);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());
        procedure = (TextView)findViewById(R.id.directions);
        procedure.setText(Html.fromHtml(getString(R.string.message_procedure)));
        invite = (Button)findViewById(R.id.inviteFriends);
        invite.setOnClickListener(this);

        //getting the title from the navigation drawer passed via intent
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");



        //Create an auto-managed GoogleApiClient with access to App Invites
        googleApiClient= new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        //Check for App invite invitations and launch deep-link  activity if possible
        //Requires that an activity is registered in AndroidManifest.xml to handle
        //deep-link URLs
        boolean autolaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autolaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult appInviteInvitationResult) {
                                Log.d(TAG, "getInvitation:onResult:" + appInviteInvitationResult.getStatus());
                                // Because autoLaunchDeepLink = true we don't have to do anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                                //for example i can launch AdsActivity
                                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())+"/AppInvite_Invitation_Received="+appInviteInvitationResult.getStatus());

//                                Intent intent = new Intent(getApplicationContext(), AdsListActivity.class);
//                                startActivity(intent);
                            }
                        }
                );
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.invitation_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        showMessage(getString(R.string.google_play_service_error));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
                //looping via the numbers of invitation sent, and sending it for google analytics
                for(int i = 0; i < ids.length; i++)
                {
                    Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Invitation_Sent_id=" + ids[i]);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                showMessage(getString(R.string.send_failed));
            }
        }
    }
    //Display a message if invitation is cancel or not
    private  void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.inviteFriends:
                NewRelic.withApplicationToken(

                        "AA405c71a7c2a19a69b59aa42f1558b2036787b393"
                ).start(this.getApplication());
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Invite_Button_Clicked");
                  intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);

                break;
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
