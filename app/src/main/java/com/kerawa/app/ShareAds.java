package com.kerawa.app;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShareAppsAdapter;
import com.newrelic.agent.android.NewRelic;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShareAds extends AppCompatActivity {

    ListView listView;
    private ArrayList data;
    String message;
    String title = "";
    String id = "";
    PackageManager  pm;
    TextView share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding the notification bar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.shareads);
        data = new ArrayList();
        share = (TextView)findViewById(R.id.shareD);
        KerawaAppRate.app_launches(this);
        share.setText(Html.fromHtml(getString(R.string.shareDesc)));

        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ShareAdsActivity");

        listView = (ListView)findViewById(R.id.sharedApplications);
        List<String> PackageName = Krw_functions.getShareApplication();

        Bundle bundle = getIntent().getExtras();
        message  = bundle.getString("sharedtext");
        title = bundle.getString("title");
        id = bundle.getString("ids");

       final PackageManager manager = this.getPackageManager();

           pm= getPackageManager();

        final List<PackageInfo> list1 = manager.getInstalledPackages(PackageManager.GET_META_DATA);// getting all the installed user applications
        final List<PackageInfo> list2 = manager.getInstalledPackages(0);
        try {
            list2.clear();
            for(int i = 0; i < list1.size(); i++){
                PackageInfo packageInfo = list1.get(i);
                if(((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) != true)
                {
                    try{

                        if(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equalsIgnoreCase("messenger")){
                            list2.add(list1.get(i));//add in 2nd list if it is user installed app

                        }
                           if(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equalsIgnoreCase("whatsapp")){
                            list2.add(list1.get(i));//add in 2nd list if it is user installed app
                        }
                        if(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equalsIgnoreCase("twitter")){
                            list2.add(list1.get(i));//add in 2nd list if it is user installed app
                        }
                        if(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equalsIgnoreCase("facebook"))
                        {
                            list2.add(list1.get(i));
                        }
                        if(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString().equalsIgnoreCase("lite"))
                        {
                            list2.add(list1.get(i));
                        }


                        Collections.sort(list1, new Comparator<PackageInfo>() {
                            //this will  sort apps list on the basis of the name
                            @Override
                            public int compare(PackageInfo o1, PackageInfo o2) {
                                return o1.applicationInfo.loadLabel(getPackageManager()).toString()
                                        .compareToIgnoreCase(o2.applicationInfo.loadLabel(getPackageManager()).toString());
                            }
                        });
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //instantiating the adapter
        ShareAppsAdapter adapter = new ShareAppsAdapter(this, list2, manager);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView)view.findViewById(R.id.appName);
                String ts = textView.getText().toString().toLowerCase();

                getPackageInfo(ts, message);

            }
        });
        if (getSupportActionBar() != null)
        {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
            getSupportActionBar().setTitle(title+id);
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
    //this method decides which app you want to share the ad on depending on the ones you have installed
    private void getPackageInfo(String appApp, String message)
    {
        Intent intent = new Intent();
        switch (appApp.toLowerCase()){
            case "messenger":
                try{
                    Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + title+id);
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    PackageInfo packageInfo = pm.getPackageInfo("com.facebook.orca", PackageManager.GET_META_DATA);
                    intent.setPackage("com.facebook.orca");
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(intent,"share with"));
                }catch (PackageManager.NameNotFoundException ne){
                    ne.printStackTrace();
                }

                break;
            case "whatsapp":
                try {
                    NewRelic.withApplicationToken(

                            "AA405c71a7c2a19a69b59aa42f1558b2036787b393"
                    ).start(ShareAds.this);
                    Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + title+id);

                    //here launching the Whatsapp application
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    PackageInfo packageInfo = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    intent.setPackage("com.whatsapp");
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(intent, "share with"));
                }catch (PackageManager.NameNotFoundException ne){
                    ne.printStackTrace();
                }
                break;
            case "lite":
            try {
                NewRelic.withApplicationToken(

                        "AA405c71a7c2a19a69b59aa42f1558b2036787b393"
                ).start(ShareAds.this);
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + title+id);

                //here launching the Whatsapp application
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                PackageInfo packageInfo = pm.getPackageInfo("com.facebook.lite", PackageManager.GET_META_DATA);
                intent.setPackage("com.facebook.lite");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, "share with"));
            }catch (PackageManager.NameNotFoundException ne){
                ne.printStackTrace();
            }
            break;
            case "facebook":
            try {
                Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + title+id);
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                PackageInfo packageInfo = pm.getPackageInfo("com.facebook.katana", PackageManager.GET_META_DATA);
                intent.setPackage("com.facebook.katana");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, "share with"));

            }catch (PackageManager.NameNotFoundException ne){
                ne.printStackTrace();
            }
            break;
            default:

        }

    }
}
