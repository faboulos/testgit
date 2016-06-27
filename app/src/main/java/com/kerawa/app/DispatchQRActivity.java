package com.kerawa.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kerawa.app.R;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DispatchQRActivity extends AppCompatActivity {

    Button lancerBtn2,lancerBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_qr);

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        lancerBtn2 = (Button)findViewById(R.id.lancerBtn2);
        lancerBtn1 = (Button)findViewById(R.id.lancerBtn1);

        lancerBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(DispatchQRActivity.this).initiateScan();
            }
        });

        lancerBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          Intent i = new Intent(getApplicationContext(), CameraActivity.class);
          startActivity(i);

            }
        });


      }

      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          // Inflate the menu; this adds items to the action bar if it is present.
          getMenuInflater().inflate(R.menu.menu_dispatch_qr, menu);
          return true;
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          // Handle action bar item clicks here. The action bar will
          // automatically handle clicks on the Home/Up button, so long
          // as you specify a parent activity in AndroidManifest.xml.
          int id = item.getItemId();

          //noinspection SimplifiableIfStatement
          if (id == R.id.action_settings) {
              return true;
          }

          return super.onOptionsItemSelected(item);
      }

      public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        try{


// nous utilisons la classe IntentIntegrator et sa fonction parseActivityResult pour parser le résultat du scan
          IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
          if (scanningResult != null) {


// nous récupérons le contenu du code barre
              String scanContent = scanningResult.getContents();

// nous récupérons le format du code barre
              String scanFormat = scanningResult.getFormatName();

              //TextView scan_format = (TextView) findViewById(R.id.scan_format);
              //TextView scan_content = (TextView) findViewById(R.id.scan_content);

// nous affichons le résultat dans nos TextView

              //scan_format.setText("FORMAT: " + scanFormat);
              //scan_content.setText("CONTENT: " + scanContent);


              try {
                  JSONObject jsonObj = new JSONObject(scanContent);

                  JSONArray dat = new JSONArray();
                  Toast.makeText(getApplicationContext(), "=========JSON=========" + jsonObj + "=========JSON========", Toast.LENGTH_LONG).show();

                         /*   if(data.has("type")) {
                            String title = jsonObj.getString("type");

                            dat = jsonObj.getJSONArray("data");*/

                  if (jsonObj.has("type")) {
                      String title = jsonObj.getString("type");
                      dat = jsonObj.getJSONArray("data");
                      // Toast.makeText(getApplicationContext(), "=========JSON========="+data.toString()+"=========JSON========", Toast.LENGTH_LONG).show();


                      // String dat2 = jsonObj.getString("data");
                      if (title.equalsIgnoreCase("01")) {//product description
                          Intent i = new Intent(getApplicationContext(), QrDetails.class);
                          i.putExtra("code", dat.toString());
                          if (dat.length() != 0) {
                              startActivity(i);
                          } else {
                              Toast.makeText(getApplicationContext(), "le QR code ne contient pas de données ou les données presentes sont illisibles", Toast.LENGTH_LONG).show();
                          }

                      }
                      if (title.equalsIgnoreCase("02")) {//sms
                          JSONObject fin = new JSONObject();
                          for (int i = 0; i <= dat.length() - 1; i++) {
                              JSONObject obj = dat.getJSONObject(i);
                              Iterator<String> it = obj.keys();
                              while (it.hasNext()) {
                                  String tit = it.next();
                                  fin.put(tit, obj.get(tit));
                              }
                          }
                          Log.d("02", fin.toString());
                          if (fin.has("to")) {
                              String to = fin.getString("to");
                              String message = "";
                              if (fin.has("message")) {
                                  message = fin.getString("message");
                              }
                              String telURI = "sms:" + to;
                              Intent intent1 = new Intent(Intent.ACTION_SENDTO, Uri.parse(telURI));
                              intent.putExtra(Intent.EXTRA_TEXT, message);
                              startActivity(intent1);
                          }
                          //launching intent
                          else
                              Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();
                      }
                      if (title.equalsIgnoreCase("03")) {//call
                          JSONObject fin = new JSONObject();
                          for (int i = 0; i <= dat.length() - 1; i++) {
                              JSONObject obj = dat.getJSONObject(i);
                              Iterator<String> it = obj.keys();
                              while (it.hasNext()) {
                                  String tit = it.next();
                                  fin.put(tit, obj.get(tit));
                              }
                          }
                          if (fin.has("to")) {
                              String call = fin.getString("to");
                              String telURI = "tel:" + call;
                              Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                              startActivity(intent2);
                          } else
                              Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();
                      }
                      if (title.equalsIgnoreCase("04")) {//whatsapp
                          JSONObject fin = new JSONObject();
                          for (int i = 0; i <= dat.length() - 1; i++) {
                              JSONObject obj = dat.getJSONObject(i);
                              Iterator<String> it = obj.keys();
                              while (it.hasNext()) {
                                  String tit = it.next();
                                  fin.put(tit, obj.get(tit));
                              }
                          }
                          if (fin.has("message")) {
                              Intent sendIntent = new Intent();
                              sendIntent.setAction(Intent.ACTION_SEND);
                              sendIntent.putExtra(Intent.EXTRA_TEXT, fin.getString("message"));
                              sendIntent.setType("text/plain");
                              if (isAppInstalled("com.whatsapp")) {
                                  sendIntent.setPackage("com.whatsapp");
                                  startActivity(sendIntent);
                              } else
                                  Toast.makeText(getApplicationContext(), "Veuillez installer whapsapp et rescannez svp", Toast.LENGTH_SHORT).show();
                          } else
                              Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();

                      }

                      if (title.equalsIgnoreCase("05")) {//email
                          JSONObject fin = new JSONObject();
                          for (int i = 0; i <= dat.length() - 1; i++) {
                              JSONObject obj = dat.getJSONObject(i);
                              Iterator<String> it = obj.keys();
                              while (it.hasNext()) {
                                  String tit = it.next();
                                  fin.put(tit, obj.get(tit));
                              }
                          }
                          if (fin.has("to") && fin.has("message") && fin.has("subject")) {
                              String[] addresses = {fin.getString("to")};

                              String subject = fin.getString("subject");
                              String message = fin.getString("message");
                              Intent intent3 = new Intent(Intent.ACTION_SENDTO);
                              intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                              intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                              intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                              intent.putExtra(Intent.EXTRA_TEXT, message);
                              startActivity(intent3);
                          } else
                              Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();

                      }
                      if (title.equalsIgnoreCase("06")) {//url
                          JSONObject fin = new JSONObject();
                          for (int i = 0; i <= dat.length() - 1; i++) {
                              JSONObject obj = dat.getJSONObject(i);
                              Iterator<String> it = obj.keys();
                              while (it.hasNext()) {
                                  String tit = it.next();
                                  fin.put(tit, obj.get(tit));
                              }
                          }
                          if (fin.has("url")) {

                              Intent i = new Intent(getApplicationContext(), WebPageLoader.class);
                              i.putExtra("lien", fin.getString("url"));
                              startActivity(i);
                          } else
                              Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();

                      }

                  } else
                      Toast.makeText(getApplicationContext(), scanContent, Toast.LENGTH_LONG).show();


                  //    Toast.makeText(getApplicationContext(),"decode success "+jsonObj.getString("data"),Toast.LENGTH_LONG).show();

              } catch (JSONException e) {
                  e.printStackTrace();
                  Toast.makeText(getApplicationContext(), scanContent, Toast.LENGTH_LONG).show();

              }


          } else {
              Toast toast = Toast.makeText(getApplicationContext(),
                      "Aucune donnée reçue!", Toast.LENGTH_SHORT);
              toast.show();
          }
        }
        catch (Exception e)
        {

        }


      }


      private boolean isAppInstalled(String uri) {
          PackageManager pm = getPackageManager();
          boolean installed = false;
          try {
              pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
              installed = true;
          } catch (PackageManager.NameNotFoundException e) {
              installed = false;
          }
          return installed;
      }


  }
