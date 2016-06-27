package com.kerawa.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kerawa.app.fragments.MySingleton;
import com.kerawa.app.services.drugstore_updater;
import com.kerawa.app.utilities.Errors_handler;
import com.kerawa.app.utilities.Kerawa_Parameters;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.create_drugstore_list;
import static com.kerawa.app.utilities.Krw_functions.cron_service_executor;
import static com.kerawa.app.utilities.Krw_functions.generate_spinner;
import static com.kerawa.app.utilities.Krw_functions.getAllPhoneEmails;
import static com.kerawa.app.utilities.Krw_functions.show_drugstore_Notification;

public class Drug_store_list extends AppCompatActivity {
   ListView lespharmacies;
    private Spinner RegionList;
    private Spinner Cities;
    private   String url = Kerawa_Parameters.PreProdURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.getLong("drug_update", Calendar.getInstance().getTimeInMillis());
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_drugstore_list");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_store_list);

         // plateform of notifications errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());



        lespharmacies= (ListView) findViewById(R.id.drugstores_list);
        RegionList= (Spinner) findViewById(R.id.region);
        Cities= (Spinner) findViewById(R.id.cities);
        drugstore_error();
        generate_spinner(Krw_functions.drugstore_regions(getApplicationContext(),"",""), getApplicationContext(), RegionList);

        Cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model laville = (Model) Cities.getSelectedItem();
                Model laregion = (Model) RegionList.getSelectedItem();
                create_drugstore_list(getApplicationContext(), lespharmacies, laregion.getTag(), laville.getTag());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RegionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model laregion= (Model) RegionList.getSelectedItem();
                generate_spinner(Krw_functions.drugstore_regions(getApplicationContext(), "ville",laregion.getTag()), getApplicationContext(), Cities);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            prefs.getLong("drug_update", Calendar.getInstance().getTimeInMillis());

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(prefs.getLong("last_drug_update",Calendar.getInstance().getTimeInMillis())*1000l);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH);
            String date = sdf.format(calendar.getTime());//(String) dateFormat.format("yyyy-MM-dd HH:mm:ss",calendar.getTime());

            // String date = formatter.format(calendar.getTime());
            getSupportActionBar().setSubtitle(date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drug_store_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_refresh:

                //make the other components invisible and display only the gif loader
                LinearLayout lin = (LinearLayout)findViewById(R.id.linearLayout8);
                lin.setVisibility(View.GONE);
                ListView lv = (ListView)findViewById(R.id.drugstores_list);
                lv.setVisibility(View.GONE);
                GifImageView load = (GifImageView)findViewById(R.id.loader2);
                load.setVisibility(View.VISIBLE);
                if (getSupportActionBar()!=null)
                getSupportActionBar().setSubtitle("");

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String nom = prefs.getString("Kerawa-count", "cm");
                final String first_url=url + "pharmacy/"+nom+"?avaible";
                final String next_url=url + "pharmacy/"+nom;
                final Long ID0  = prefs.getLong("id_fichier", 0);
                final SharedPreferences.Editor editor = prefs.edit();

                JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, first_url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            //response = response.getJSONObject("data");
                            //JSONArray arr = response.getJSONArray("data");
                            Long ID=response.getLong("id");
                            Log.d("drugstore",ID+"");
                            prefs.edit().remove("drug_update").apply();
                            prefs.edit().putLong("drug_update", Calendar.getInstance().getTimeInMillis()).apply();

                            if (ID0<ID) {

                                editor.remove("pharmacy_list").apply();
                                prefs.edit().putLong("last_drug_update", ID).apply();
                                Log.d("id_fichier", String.valueOf(ID));
                                //if(myDB.reset_drugstores()){
                                Log.d("drugstore", "drugstores resetted");
                                // ShowToastWithinService(getApplication(),"drugstores resetted");
                                load_drug_stores(next_url);
                                //}
                            }else{
                                LinearLayout lin = (LinearLayout)findViewById(R.id.linearLayout8);
                                lin.setVisibility(View.VISIBLE);
                                ListView lv = (ListView)findViewById(R.id.drugstores_list);
                                lv.setVisibility(View.VISIBLE);
                                GifImageView load = (GifImageView)findViewById(R.id.loader2);
                                load.setVisibility(View.GONE);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String date = sdf.format(calendar.getTime());//(String) dateFormat.format("yyyy-MM-dd HH:mm:ss",calendar.getTime());

                                if (getSupportActionBar()!=null)
                                    getSupportActionBar().setSubtitle(date);
                                 }

                        } catch (JSONException ignored) {
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("drugstore", error.toString() + " - " + first_url);
                                //Show_Toast(getApplicationContext(),"la liste des pharmacies n'a pas pu etre mise a jour",true);
                                GifImageView load = (GifImageView)findViewById(R.id.loader2);
                                load.setVisibility(View.GONE);
                                drugstore_error();
                                if (getSupportActionBar()!=null)
                                    getSupportActionBar().setSubtitle("");

                                //ShowToastWithinService(getApplicationContext(), error.toString()+" - "+first_url);
                            }
                        }) {

                    @Override
                    public Map<String, String> getHeaders () throws AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                        return params;
                    }
                };

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
                movieReq.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));




                return true ;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

    }

    private void drugstore_error(){
        String ErrorString="";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nom = prefs.getString("Kerawa-count", null);
        int nbPharma=Krw_functions.drugstore_regions(getApplicationContext(),"","").size();
        long currentdrugstoreTime= PreferenceManager.getDefaultSharedPreferences(this).getLong("id_fichier",0);
        if(nbPharma==0||currentdrugstoreTime==0) ErrorString="La liste de pharmacies n'est pas encore disponible";
        long today=new Date().getTime();
        String ladate=Krw_functions.millisecondTodate(today,"dd:M:yyyy");
        if(!Krw_functions.millisecondTodate(currentdrugstoreTime*1000l,"dd:M:yyyy").equals(ladate)) ErrorString="Désolé,\n La liste de pharmacies n'a pas encore été mise à jour pour cette journée.\n Veuillez reéssayer plus tard";
        Log.d("drugstore",Krw_functions.millisecondTodate(currentdrugstoreTime*1000l,"dd:M:yyyy")+" "+ladate);
        if(nom!=null){
            if (!nom.toLowerCase().equals("cm")) ErrorString="Désolé service non disponible pour ce pays";
        }
        if (!ErrorString.equals("")){
            new Errors_handler(this,getApplicationContext(),ErrorString,false, (TextView) findViewById(R.id.statusText),R.drawable.errore).execute(500,500,6000);
            new ShowHide(lespharmacies).Hide();
            new ShowHide(Cities).Hide();
            new ShowHide(RegionList).Hide();
        }
    }
    private void load_drug_stores(String url){
        // Log.d("drugstore", "drugstores download started");
        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("pharmacy_list", response.toString());
                    editor.apply();
                    long id=response.getLong("id");
                    prefs.edit().remove("id_fichier").apply();
                    prefs.edit().putLong("id_fichier", id).apply();
                    Log.d("id_fichier", String.valueOf(id));

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                } catch (JSONException e) {
                      e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // ShowToastWithinService(getApplicationContext(), error.toString());

                        GifImageView load = (GifImageView)findViewById(R.id.loader2);
                        load.setVisibility(View.GONE);
                        Log.d("drugstore",error.toString());
                        drugstore_error();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("X-Kerawa-Api-Consumer-Key", new Kerawa_Parameters(getApplicationContext()).Hide_meta_data());

                return params;
            }
        };

        // Creating volley request obj

        // Adding request to request queue


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(movieReq);
        movieReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
