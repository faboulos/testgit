package com.kerawa.app;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.utilities.FileExplore;
import com.kerawa.app.utilities.Krw_functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.newrelic.agent.android.NewRelic;
public class ActivityUpdater extends AppCompatActivity {

    private ImageButton google_updater ;
    private ImageButton site_updater ;
    private NotificationCompat.Builder mBuilder ;
    private NotificationManager mNotifyManager ;
    private TextView tv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Activity_Updater");
        setContentView(R.layout.activity_activity_updater);
        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        }


        tv = (TextView)findViewById(R.id.textView8);
        tv.setText("Vous n'avez pas la dernière version de l'application \"Kerawa.com\".\n" +
                "Il vous manque peut-être des mises à jour de sécurité, et de nouvelles fonctionnalités surprenantes.\n" +
                "\n" +
                "Pour installer la dernière version, veuillez cliquer sur l'une des icônes ci-dessous :\n" +
                "\n" +
                "1. Si vous avez un compte Google configuré sur votre téléphone, cliquez plutôt sur l'icône de Google Play\n\n" +
                "2. Dans les autres cas, cliquez sur la seconde icône. Vous devrez peut-être autoriser votre téléphone à installer des applications provenant de \"sources inconnues\" (merci de regarder dans les paramètres du téléphone)");
        google_updater = (ImageButton)findViewById(R.id.button_market);
        site_updater = (ImageButton)findViewById(R.id.button_kerawa);

        google_updater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open a webview and display the result inside
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/update_Android_Market");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.kerawa.app"));
                startActivity(intent);
            }
        });

        site_updater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/update_kerawa_website");
                Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                Toast.makeText(getApplicationContext(),"Votre Téléchargement a commencé",Toast.LENGTH_SHORT).show();
                if (isSDPresent) {
                    mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle("Telechargement de Kerawa.com")
                            .setContentText("Telechargement en cours")
                            .setSmallIcon(R.drawable.icon);
                    // Start a lengthy operation in a background thread
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {

                                    mBuilder.setProgress(0, 0, true);
                                    // Displays the progress bar for the first time.
                                    mNotifyManager.notify(0, mBuilder.build());
                                    // Sleeps the thread, simulating an operation
                                    // that takes time
                                    String PATH = Environment.getExternalStorageDirectory() + "/kerawaApp/";
                                    try {
                                        URL url = new URL("https://kerawa.com/rss_widget/krw_android/kerawa.apk ");
                                        HttpURLConnection c = (HttpURLConnection) url.openConnection();
                                        c.setRequestMethod("GET");
                                        c.setDoOutput(true);
                                        c.connect();


                                        File file = new File(PATH);
                                        file.mkdirs();
                                        File outputFile = new File(file, "kerawa.apk");
                                        FileOutputStream fos = new FileOutputStream(outputFile);

                                        InputStream is = c.getInputStream();

                                        byte[] buffer = new byte[1024];
                                        int len1 = 0;
                                        while ((len1 = is.read(buffer)) != -1) {
                                            fos.write(buffer, 0, len1);
                                        }
                                        fos.close();
                                        is.close();//till here, it works fine - .apk is download to my sdcard in download file
                                       /*
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);*/

                                    } catch (IOException e) {
                                        //   Toast.makeText(getApplicationContext(), "Update error!", Toast.LENGTH_LONG).show();
                                    }

                                    // When the loop is finished, updates the notification
                                    Intent mIntent = new Intent(getApplicationContext(), FileExplore.class);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentTitle("Cliquez ici pour mettre à jour")
                                            .setContentIntent(pendingIntent)
                                                    // Removes the progress bar
                                            .setProgress(0, 0, false)
                                            .setAutoCancel(true)
                                            .setContentText("Profitez de nos nouveautés");
                                    mNotifyManager.notify(0, mBuilder.build());
                                }
                            }
                            // Starts the thread by calling the run() method in its Runnable
                    ).start();

                }
                else
                {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.texttoast);
                    text.setText("la carte SD n'est pas inserée veuillez en insérer une pour démarer le  téléchargement");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_updater, menu);
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
               // onBackPressed();
                Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext())+"/update_activity_closed");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

}
