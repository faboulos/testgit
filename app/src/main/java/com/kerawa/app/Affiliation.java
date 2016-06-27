package com.kerawa.app;

import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kerawa.app.utilities.AndroideDevice;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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

import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions._Openurl;
import static com.kerawa.app.utilities.Krw_functions.price_formated;

public class Affiliation extends AppCompatActivity {

    private View loader;
    private TextView verifier,affactiv,participateText,infosloader,done,counter,myaffiliation,aff_link,code_taker;
    private EditText parentaffilation;
    private LinearLayout tasklister;
    private TextView up_adder;
    private ImageView refreshB;
    private String regex;
    private Button valid_button,cancel_button;
    private RelativeLayout the_popop;
    private ScrollView scroller;

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiliation);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Affiliation";
        mDescription = "How to do an affiliation in kerawa.com";

        loader=findViewById(R.id.loader);
        participateText= (TextView) findViewById(R.id.participate_text);
                up_adder= (TextView) findViewById(R.id.parent_adder);
                verifier= (TextView) findViewById(R.id.verification);
                counter= (TextView) findViewById(R.id.textView27);
                aff_link= (TextView) findViewById(R.id.affiliation_link);
                 tasklister= (LinearLayout) findViewById(R.id.task_lister);
                 affactiv= (TextView) findViewById(R.id.aff_activ);
                 infosloader= (TextView) findViewById(R.id.infos_loader);
                done= (TextView) findViewById(R.id.done);
                myaffiliation= (TextView) findViewById(R.id.editText3);
        parentaffilation= (EditText) findViewById(R.id.parent_affilation);
        refreshB= (ImageView) findViewById(R.id.refresh_button);
        valid_button= (Button) findViewById(R.id.valid_button);
        cancel_button= (Button) findViewById(R.id.cancel_button);
        the_popop= (RelativeLayout) findViewById(R.id.the_popop);
        code_taker= (TextView) findViewById(R.id.code_taker);
       scroller= (ScrollView) findViewById(R.id.scrollView4);

        regex = "\\d+";

        try {
            String myaff=Krw_functions.price_formated(Krw_functions.Load_sharedString(getApplicationContext(), "myaffi"));
            if (!myaff.trim().equals("")){
                myaffiliation.setText(myaff);
                myaffiliation.setEnabled(false);
            }

        }catch (Exception ignored){
            myaffiliation.setEnabled(true);
        }

        try {
            String parAff = Krw_functions.price_formated(Krw_functions.Load_sharedString(getApplicationContext(), "myparrent"));
            if (!parAff.trim().equals("")){
                parentaffilation.setText(parAff);
                parentaffilation.setEnabled(false);
                parentaffilation.setHint("Saisir ici...");
                up_adder.setEnabled(false);
            }

        }catch (Exception ignored){parentaffilation.setEnabled(true);}

        myaffiliation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Krw_functions.Load_sharedString(getApplicationContext(),"myaffi").trim().equals("")) {
                    load_lastAfiliate();
                } else {
                    Show_Toast(getApplicationContext(), getResources().getString(R.string.ready_aff), true);
                }

            }
        });

        refreshB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Count_affiliates();
            }
        });

        up_adder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = parentaffilation.getText().toString().replace(" ", "");
                if (s.matches(regex)) {
                    new ShowHide(the_popop).Show();
                    code_taker.setText(price_formated(s));
                    loader.bringToFront();
                } else {
                    Show_Toast(getApplicationContext(), "Le code saisi ne doit contenir que des chiffres", true);
                }
            }
        });

        aff_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Openurl("https://kerawa.com/blog/affiliation/", getApplicationContext());
            }
        });

        parentaffilation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                int act = event.getAction();
                if (act == KeyEvent.ACTION_UP) {
                    String contenu = parentaffilation.getText().toString();
                    parentaffilation.setText(price_formated(contenu));
                    parentaffilation.setSelection(parentaffilation.getText().length());
                }

                return false;
            }
        });

        valid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = parentaffilation.getText().toString().replace(" ", "");
                add_parrain(s);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new ShowHide(the_popop).Hide();
            }
        });

                up_adder.setFocusable(true);
                up_adder.requestFocus();
        Krw_functions.pushOpenScreenEvent(this, "kerawa/affiliate");
        scroller.setFocusable(true);
        scroller.requestFocus();

    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_affiliation, menu);
                return true;
            }

            private void participate(final String theaff) {
                participateText.setEnabled(false);

                if (!theaff.equals("Error")) {

                    try {
                        //ParseInstallation.getCurrentInstallation().saveInBackground();
                        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Techmical_Data");
                        query.whereEqualTo("Imei", new AndroideDevice(this).getImei());
                        query.setLimit(1);
                        final ShowHide charger = new ShowHide(loader), board = new ShowHide(tasklister), participe = new ShowHide(participateText);
                        charger.Show();
                        board.Show();

                        participateText.setVisibility(View.INVISIBLE);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(final List<ParseObject> list, ParseException e) {
                                if (e == null) {
                                    if (list.size() == 0) {
                                        verifier.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.errore, 0);
                                        Show_Toast(getApplicationContext(), "Votre compte n'est pas vérifié", true);
                                        charger.Hide();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                verifier.setCompoundDrawables(null, null, null, null);
                                                board.Hide();
                                                participateText.setVisibility(View.VISIBLE);
                                            }
                                        }, 3000);
                                    } else {
                                        verifier.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_green, 0);
                                        final ParseObject my_Affiliation = list.get(0);
                                        final String newAffiliation = ((my_Affiliation.get("affiliation") != "") && (my_Affiliation.get("affiliation") != null)) ? my_Affiliation.getString("affiliation") : String.valueOf(Long.parseLong(theaff) + 1);
                                        my_Affiliation.put("affiliation", newAffiliation);
                                        my_Affiliation.saveInBackground(new SaveCallback() {//on enregistre
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            affactiv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_green, 0);
                                                        }
                                                    }, 1000);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            infosloader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_green, 0);
                                                        }
                                                    }, 2000);
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            myaffiliation.setText(price_formated(newAffiliation));
                                                            done.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick_green, 0);
                                                        }
                                                    }, 3000);

                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            board.Hide();
                                                        //    participateText.setVisibility(View.VISIBLE);
                                                        //    participateText.setEnabled(true);
                                                            charger.Hide();
                                                        }
                                                    }, 4000);
                                                    try {
                                                        //participateText.setEnabled(true);
                                                        Krw_functions.destroy_sharedString(getApplicationContext(), "myaffi");
                                                        Krw_functions.Save_sharedString(getApplicationContext(), "myaffi", newAffiliation);
                                                    } catch (Exception ignored) {
                                                    }
                                                }
                                            }
                                        });
                                    }

                                } else {
                                    Log.d("Installation", "Error: " + e.getMessage());
                                }
                            }

                        });

                    } catch (
                            Exception e
                            )

                    {
                        Log.e("some_error", BuildConfig.FLAVOR + e);
                    }
                } else {

                    Show_Toast(getApplicationContext(), "Impossible d'exécuter la commande. Vérifiez votre connexion SVP", true);
                }
            }

            private void Count_affiliates() {
                refreshB.setEnabled(false);
                String my_affiliation = myaffiliation.getText().toString().replace(" ", "");

                if (my_affiliation.trim().equals("")) {
                    refreshB.setEnabled(true);
                    counter.setText("Nbre de téléchargement(s): 0");
                    return;
                }
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Techmical_Data");
                query.whereEqualTo("Parent_affiliation", my_affiliation);
                new ShowHide(loader).Show();
                final String[] retour = new String[1];
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(final List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            refreshB.setEnabled(true);
                            retour[0] = list.size() + "";
                            counter.setText("Nbre de téléchargement(s) : " + retour[0]);
                            new ShowHide(loader).Hide();
                        } else {
                            new ShowHide(loader).Hide();
                            refreshB.setEnabled(true);
                            Show_Toast(getApplicationContext(), "Veuillez vérifier votre connexion internet SVP.", true);
                            counter.setText("Nbre de téléchargement(s) : ?");
                        }
                    }
                });

            }

            private String load_lastAfiliate() {

                Show_Toast(getApplicationContext(), "Démarrage de l'opération...", true);
                final ParseQuery<ParseObject> query0 = ParseQuery.getQuery("Techmical_Data");
                query0.orderByDescending("affiliation");
                query0.setLimit(1);
                final ShowHide loder = new ShowHide(loader);
                loder.Show();
                final String[] retour = {""};
                query0.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(final List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            participate(list.get(0).getString("affiliation"));
                        } else {
                            participate("Error");
                            loder.Hide();
                        }
                    }
                });
                return retour[0];
            }

            private void add_parrain(final String Parrain_affiliation) {
                if (Parrain_affiliation == null || Parrain_affiliation.equals("")) {
                    Show_Toast(getApplicationContext(), "Le code parrain ne peut être vide", true);
                    return;
                }
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Techmical_Data");
                query.whereEqualTo("Imei", new AndroideDevice(this).getImei());
                query.setLimit(1);
                final ShowHide[] charger = {new ShowHide(loader)};
                charger[0].Show();
                final String[] myfather = {Parrain_affiliation};
                up_adder.setEnabled(false);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(final List<ParseObject> list, ParseException e) {
                        charger[0].Hide();
                        if (e == null) {
                            if (!(list.size() == 0)) {
                                ParseObject myaccount = list.get(0);
                                if (myaccount.getString("Parent_affiliation") != null && !myaccount.getString("Parent_affiliation").trim().equals(""))
                                    myfather[0] = myaccount.getString("Parent_affiliation");
                                myaccount.put("Parent_affiliation", myfather[0]);
                                myaccount.saveInBackground(new SaveCallback() {//on enregistre
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            up_adder.setEnabled(true);
                                            charger[0].Hide();
                                            new ShowHide(the_popop).Hide();
                                            Krw_functions.Save_sharedString(getApplicationContext(), "myparrent", myfather[0]);
                                            parentaffilation.setText(price_formated(myfather[0]));
                                            Show_Toast(getApplicationContext(), "Affilié ajouté avec succès.", true);
                                        } else {
                                            Show_Toast(getApplicationContext(), "Impossible d'enregistrer l'utilisateur.\n Veuillez vérifier votre accès à internet.", true);
                                            charger[0].Hide();
                                            new ShowHide(the_popop).Hide();
                                            parentaffilation.setText(price_formated(parentaffilation.getText().toString()));
                                        }
                                    }
                                });
                            }
                        } else {
                            Show_Toast(getApplicationContext(), "Une érreur est survenue lors de l'enregistrement.\n Veuillez vérifier que vous êtes connectés sur internet.", true);
                            charger[0].Hide();
                            new ShowHide(the_popop).Hide();
                        }
                    }
                });
            }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/blog/affiliation"))
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
                        onBackPressed();
                        return true;
                    default:
                        return super.onOptionsItemSelected(item);
                }
            }
        }
