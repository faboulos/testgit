package com.kerawa.app;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Listadapter;
import com.kerawa.app.utilities.ShowHide;
import com.newrelic.agent.android.NewRelic;

import java.util.ArrayList;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;

public class Mes_contacts extends AppCompatActivity implements AdapterViewCompat.OnItemClickListener, AdapterView.OnItemClickListener {

    ListView apps;
    PackageManager packageManager;
    ArrayList<String> checkedValue;
    ArrayList<Krw_functions.PhoneContactInfo> liste_contact;
    Button bt1;
    View loader;
    private CheckBox check_all ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_contacts);
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());
        bt1 = (Button) findViewById(R.id.button1);
        apps = (ListView) findViewById(R.id.listView1);
        packageManager = getPackageManager();
        loader=findViewById(R.id.loader);
        checkedValue=new ArrayList<>();
        liste_contact= new ArrayList<>();
        check_all = (CheckBox)findViewById(R.id.cb1);
        new LongOperation().execute();


    }

    private class LongOperation extends AsyncTask<String, Void, ArrayList<Krw_functions.PhoneContactInfo>> {

        @Override
        protected ArrayList<Krw_functions.PhoneContactInfo> doInBackground(String... params) {
           try{
               return  Krw_functions.getAllPhoneContacts(getApplicationContext(), liste_contact,null);
           }catch (Exception e){
               Log.d("Chutte",e.toString() + "");
           }
            return liste_contact;
        }

        @Override
        protected void onPostExecute(ArrayList<Krw_functions.PhoneContactInfo> liste) {
            new ShowHide(loader).Hide();
            try {
                liste_contact=liste;
                Listadapter Adapter = new Listadapter(Mes_contacts.this,liste_contact);
                apps.setAdapter(Adapter);
                apps.setOnItemClickListener((Mes_contacts.this));

                check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (check_all.isChecked())
                        {
                          //get all the items of listview and add them to the final dataset


                        }
                    }
                });

                bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(Mes_contacts.this, "Invitation envoyée à vos contacts avec succès.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
            }catch (Exception e){
                Show_Toast(getApplicationContext(),e.toString(),true);
            }

        }

        @Override
        protected void onPreExecute() {
            new ShowHide(loader).Show();
            Show_Toast(getApplicationContext(),"Chargement des contacts...",true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mes_contacts, menu);
        return true;
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

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

    }
    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        // TODO Auto-generated method stub
        Krw_functions.PhoneContactInfo model=liste_contact.get(position);
        CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
        TextView tv = (TextView) v.findViewById(R.id.textView1);
        cb.performClick();
        if (cb.isChecked()) {
            checkedValue.add(model.getContactName());
        } else {
            checkedValue.remove(model.getContactName());
        }



    }

    @Override
    public void onItemClick(AdapterViewCompat<?> parent, View view, int position, long id) {

    }
}
