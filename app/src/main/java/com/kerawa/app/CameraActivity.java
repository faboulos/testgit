package com.kerawa.app;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.Button;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/* Import ZBar Class files */
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.CameraPreview;
import com.kerawa.app.utilities.CardDetails;
import com.kerawa.app.utilities.Krw_functions;
import com.newrelic.agent.android.NewRelic;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CameraActivity extends AppCompatActivity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    public static final String ERROR_INFO = "ERROR_INFO";

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isCameraAvailable()) {
            // Cancel request if there is no rear-facing camera.
            cancelRequest();
            return;
        }


        if (getSupportActionBar() != null)
        {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0691E6")));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setContentView(R.layout.activity_camera);

        //instantiating new relic to monitor unexpected errors
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);



        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        // scanText = (TextView)findViewById(R.id.scanText);
        scanButton = (Button)findViewById(R.id.ScanButton);
        scanButton.bringToFront();


        /*LinearLayout root = (LinearLayout) findViewById( R.id.linear);
        View view = findViewById(R.id.line);
        view.bringToFront();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];


        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels - (view.getMeasuredHeight()/2) - statusBarOffset;

        mPreview.getLocationOnScreen(originalPos);

        TranslateAnimation anim = new TranslateAnimation(0,0,mPreview.getBottom(), -mPreview.getHeight());
        anim.setDuration(5000);
        anim.setRepeatMode(TranslateAnimation.INFINITE);
        anim.setFillAfter(true);
        view.startAnimation(anim);*/



/*
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"bonjour marcelin",Toast.LENGTH_LONG).show();

            }
        });*/


        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    //if (barcodeScanned) {
                    barcodeScanned = false;

                    if(mCamera!=null) {
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        previewing = true;
                        mCamera.autoFocus(autoFocusCB);
                    }
                    else
                    {
                        mCamera = getCameraInstance();
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        previewing = true;
                        mCamera.autoFocus(autoFocusCB);

                    }
                }
            }
        });
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

    public void onPause() {
        super.onPause();
        releaseCamera();

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

    public void onResume() {
        super.onResume();
        // setContentView(R.layout.activity_camera);
        releaseCamera();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;
        mCamera.autoFocus(autoFocusCB);

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
            preview.removeView(mPreview);
            mPreview = null ;
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

           String json=null;

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
               // Toast.makeText(getApplicationContext(), "====INT===="+result+"====INT====", Toast.LENGTH_LONG).show();

                SymbolSet syms = scanner.getResults();
          //  int a = Symbol.QRCODE;

                for (Symbol sym : syms) {
                    // scanText.setText("barcode result " + sym.getData());

                     json = sym.getData();
                    //Toast.makeText(getApplicationContext(), "====COUNT===="+sym.getCount()+"====COUNT====", Toast.LENGTH_LONG).show();

                  //  Toast.makeText(getApplicationContext(), "=========DATA========     "+sym.getData()+"       =========DATA=========", Toast.LENGTH_LONG).show();

                 //   Toast.makeText(getApplicationContext(), "=========TYPE=========   "+sym.getType()+"    =========TYPE========", Toast.LENGTH_LONG).show();

                  //  Toast.makeText(getApplicationContext(), sym.getComponen, Toast.LENGTH_LONG).show();
                    barcodeScanned = true;


                    try {
                       JSONObject jsonObj = new JSONObject(sym.getData());

                        JSONArray dat = new JSONArray();
                     //   Toast.makeText(getApplicationContext(), "=========JSON========="+jsonObj+"=========JSON========", Toast.LENGTH_LONG).show();

                         /*   if(data.has("type")) {
                            String title = jsonObj.getString("type");

                            dat = jsonObj.getJSONArray("data");*/

                   if(jsonObj.has("type")) {
                            String title = jsonObj.getString("type");
                       dat = jsonObj.getJSONArray("data");
                   //   Toast.makeText(getApplicationContext(), "=========JSON========="+data+"=========JSON========", Toast.LENGTH_LONG).show();

//=========================================================================================================================

                           // String dat2 = jsonObj.getString("data");
                            if (title.equalsIgnoreCase("01")) {//product description
                               // Intent in = new Intent(getApplicationContext(), DetailsActivity.class);

                                Intent in = new Intent(getApplicationContext(), QrDetails.class);
                                in.putExtra("code", dat.toString());
                                if (dat.length() != 0){

                                    startActivity(in);


//=================================================================================




                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "PAS DE DATA"+"le QR code ne contient pas de données ou les données presentes sont illisibles", Toast.LENGTH_LONG).show();
                                }

                            }
//=========================================================================================================================

                      /*    if (title.equalsIgnoreCase("01")) {//product description
                              Intent i = new Intent(getApplicationContext(), QrDetails.class);
                               i.putExtra("code", dat.toString());
                              if (dat.length() != 0){
                                 startActivity(i);
                             }
                           else{
                               Toast.makeText(getApplicationContext(), "le QR code ne contient pas de données ou les données presentes sont illisibles", Toast.LENGTH_LONG).show();
                            }

                           }*/
                            if (title.equalsIgnoreCase("02")) {//sms
                              JSONObject fin = new JSONObject();
                              for (int i=0;i<=dat.length()-1;i++)
                               {
                                  JSONObject obj = dat.getJSONObject(i);
                                  Iterator<String> it = obj.keys();
                                  while (it.hasNext())
                                  {
                                      String tit = it.next() ;
                                      fin.put(tit,obj.get(tit));
                                  }
                              }
                               Log.d("02",fin.toString());
                                if (fin.has("to")) {
                                    String to = fin.getString("to");
                                    String message = "";
                                    if (fin.has("message")) {
                                        message = fin.getString("message");
                                    }
                                    String telURI = "sms:" + to;
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(telURI));
                                    intent.putExtra(Intent.EXTRA_TEXT, message);
                                    startActivity(intent);
                                }
                                //launching intent
                                else
                                    Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();
                            }
                            if (title.equalsIgnoreCase("03")) {//call
                                JSONObject fin = new JSONObject();
                                for (int i=0;i<=dat.length()-1;i++)
                                {
                                    JSONObject obj = dat.getJSONObject(i);
                                    Iterator<String> it = obj.keys();
                                    while (it.hasNext())
                                    {
                                        String tit = it.next() ;
                                        fin.put(tit,obj.get(tit));
                                    }
                                }
                               if (fin.has("to")) {
                                    String call = fin.getString("to");
                                    String telURI = "tel:" + call;
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();
                            }
                            if (title.equalsIgnoreCase("04")) {//whatsapp
                                JSONObject fin = new JSONObject();
                                for (int i=0;i<=dat.length()-1;i++)
                                {
                                    JSONObject obj = dat.getJSONObject(i);
                                    Iterator<String> it = obj.keys();
                                    while (it.hasNext())
                                    {
                                        String tit = it.next() ;
                                        fin.put(tit,obj.get(tit));
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
                                for (int i=0;i<=dat.length()-1;i++)
                                {
                                    JSONObject obj = dat.getJSONObject(i);
                                    Iterator<String> it = obj.keys();
                                    while (it.hasNext())
                                    {
                                        String tit = it.next() ;
                                        fin.put(tit,obj.get(tit));
                                    }
                                }
                                if (fin.has("to") && fin.has("message") && fin.has("subject")) {
                                    String[] addresses = {fin.getString("to")};

                                    String subject = fin.getString("subject");
                                    String message = fin.getString("message");
                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    intent.putExtra(Intent.EXTRA_TEXT, message);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();

                            }
                            if (title.equalsIgnoreCase("06")) {//url
                                JSONObject fin = new JSONObject();
                                for (int i=0;i<=dat.length()-1;i++)
                                {
                                    JSONObject obj = dat.getJSONObject(i);
                                    Iterator<String> it = obj.keys();
                                    while (it.hasNext())
                                    {
                                        String tit = it.next() ;
                                        fin.put(tit,obj.get(tit));
                                    }
                                }
                                if (fin.has("url")) {

                                    Intent i = new Intent(getApplicationContext(), WebPageLoader.class);
                                    i.putExtra("lien", fin.getString("url"));
                                    startActivity(i);
                                } else
                                    Toast.makeText(getApplicationContext(), "données eronnées rescannez svp", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else
                            Toast.makeText(getApplicationContext(),sym.getData(),Toast.LENGTH_LONG).show();


                        //    Toast.makeText(getApplicationContext(),"decode success "+jsonObj.getString("data"),Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                      /*Toast.makeText(getApplicationContext(),sym.getData(),Toast.LENGTH_LONG).show();*/

                    }





                    /*if (Uri.parse(sym.getData()).getScheme().equals("http") ||Uri.parse(sym.getData()).getScheme().equals("https") ){
                        if (URLUtil.isValidUrl(sym.getData())) {

                            findViewById(R.id.line).setVisibility(View.GONE);
                            if (sym.getData().equalsIgnoreCase("https://www.kerawa.com")) {
                                Intent i = new Intent(getApplicationContext(), AdsListActivity.class);
                                i.setData(Uri.parse(sym.getData().trim()));//put the uri here
                                startActivity(i);

                            } else {
                                //open the webview activity with the pameters
                                Bundle b = new Bundle();
                                b.putString("lien", sym.getData());

                            }

                        }
                     }*/
                }
                //Toast.makeText(getApplicationContext(),"RESULT"+json+"======RESULT",Toast.LENGTH_LONG).show();
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };




    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void cancelRequest() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
        setResult(Activity.RESULT_CANCELED, dataIntent);
        finish();
    }


    public static String price_formated(String price){

        price=price.replaceAll("[^\\d]", "");

        int ptpos=price.indexOf(".");

        if(ptpos>-1){price=price.substring(0,ptpos);}


        int l=price.length();
        int j=0;

        for(; l>=0;l--){

            String Ch=price.substring(l);
            String Ch2=price.substring(0,l);
            if(j % 3 == 0) price=Ch2+" "+Ch;
            j++;
        }
        if (price.equals("0")||price.equals("")||price.equals("null")||price.equals(null)) {
            price = "";
        }
        return !price.equals("") ?price:"";

    }


}