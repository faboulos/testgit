package com.kerawa.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kerawa.app.utilities.Model;
import com.newrelic.agent.android.NewRelic;

import java.util.ArrayList;

import static com.kerawa.app.utilities.Krw_functions.create_menu;

public class Health_services extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_services);
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        ListView lemenu= (ListView) findViewById(R.id.health_menu);
        ArrayList<Model> lesElets=new ArrayList<>();
        lesElets.add(new Model(R.drawable.icon_pharmacy,"Drug_store_list","Pharmacies de garde",false));
        create_menu(lesElets, lemenu, this);
        lemenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Model itemValue = (Model) parent.getItemAtPosition(position);
                switch (itemValue.getTag()) {
                    case "Drug_store_list":
                        startActivity(new Intent(getApplicationContext(),Drug_store_list.class));
                    break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_health_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(Health_services.this, AdsListActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
