package com.kerawa.app.utilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.kerawa.app.ActivityUserAccount;
import com.kerawa.app.AdsListActivity;
import com.kerawa.app.CountryList;
import com.kerawa.app.Drug_store_list;
import com.kerawa.app.LoginActivity;
import com.kerawa.app.R;
import com.kerawa.app.WebPageLoader;
import com.kerawa.app.createAdsActivity;
import com.kerawa.app.helper.Cities_loader;
import com.kerawa.app.helper.ContainerHolderSingleton;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.helper.InstallKerawa;
import com.kerawa.app.helper.PostData;
import com.kerawa.app.helper.PostDataRegister;
import com.kerawa.app.services.AlarmRecieverFinal;
import com.kerawa.app.splash;

import org.apache.commons.lang.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 Created by kwemart on 03/01/2015.
 */
public class Krw_functions  {
    public static ContentResolver cr;

    public static Uri ussdToCallableUri(String ussd) {                                                                                                                                                                                                                                                                                                                                                                                                                              

        String uriString = "";

        if (!ussd.startsWith("tel:"))
            uriString += "tel:";

        for (char c : ussd.toCharArray()) {

            if (c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }


    public static void startCall(String Number, Activity ClassName) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(ussdToCallableUri(Number));
        ClassName.startActivity(callIntent);
    }



 public static void add_Rabuttons_TO_RadioGroup (HashMap<Integer,String> liste,String RgTitle, final Context ctx,RadioGroup rg){

        RadioButton rb;
        rg.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        TextView tx = new TextView(ctx);
        tx.setLayoutParams(llp);
        tx.setText(RgTitle);
        rg.removeAllViews();
        rg.addView(tx);
     for (HashMap.Entry<Integer, String> entry : liste.entrySet()) {
         rb = new RadioButton(ctx);
         int key = entry.getKey();
         String value = entry.getValue();
         rb.setText(value);
         rb.setId(key);
         rg.addView(rb);
     }
    }

    public static void generate_spinner(ArrayList<Model> liste, final Context ctx, Spinner rg){
        //rg.removeAllViewsInLayout();
        rg.setAdapter(new Spinner_adapter(ctx, R.layout.single_spinner, liste));

    }

    public static void generate_spinner(ArrayList<Model> subcat, final Context context, Spinner spinner, String category){
        spinner.setAdapter(new SubCatSAdapter(context, subcat, category));
    }


    public static void Show_Toast(Context ctx, String msg, boolean length) {
        Toast.makeText(ctx, msg, (length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
    }


    public static LinearLayout CreateLinear(Activity ClassName) {

        LinearLayout linearLayout = new LinearLayout(ClassName);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        linearLayout.setLayoutParams(llp);
        linearLayout.setPadding(16, 16, 16, 16);
        return linearLayout;
    }



    /*public static ListView CreateList(Activity ClassName) {

        ListView linearLayout = new ListView(ClassName);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        linearLayout.setLayoutParams(llp);

        return linearLayout;
    }
*/


//Connexion à un compte Kerawa via popup

  public static void log_in(Activity ctx, int menuID, View dialoglayout, final Person person){
        // check_payment();
        PostData signin= new PostData();
        signin.ctx=ctx;
        signin.menuID=menuID;
        signin.items=person;
        signin.v=dialoglayout.findViewById(R.id.loader2);
        signin.v2= (TextView) dialoglayout.findViewById(R.id.status_text);
        signin.execute();

    }



 //connexion normale à un compte Kérawa

    public static void log_in2(Activity ctx, int menuID, final Person person){
        PostData signin= new PostData();
        signin.ctx=ctx;
        signin.menuID=menuID;
        signin.items=person;
        signin.v=ctx.findViewById(R.id.loader2);
        signin.v2= (TextView) ctx.findViewById(R.id.status_text);
        signin.execute();
    }


 //Validaton d'un compte Kerawa

 public static void validate_account(Activity ctx, int menuID, final Person person){
     PostData signin= new PostData();
     signin.ctx=ctx;
     signin.menuID=menuID;
     signin.items=person;
     signin.v=null;
     signin.v2= null;
     signin.execute();
 }

//checking if phone is connected to network
    public static boolean isConnected(Context ctx){
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    //fonction qui détermine qu'un numero est Camerounais

   /* public static String Bon_Numero_etReseau(String numero) {
        String devenir_riche = "";
        numero = numero.trim().replace("-", "").replace(" ", "").replace("+", "").trim();
        if (numero.substring(0, 2).equals("00")) numero = numero.substring(2);
        String start = numero.substring(0, 3);
        if (!start.equals("237") && numero.length() > 9) {
            devenir_riche = "";
        } else if (start.equals("237") && numero.length() > 9) {
            devenir_riche = numero.substring(3);
        } else if (numero.length() == 9) {
            devenir_riche = numero;
        }

        return devenir_riche;
    }*/

  //fonction qui genère la liste des pays
    public static ArrayList<Model> generateCountryList(Context ctx) {

        ArrayList<Model> models = new ArrayList<>();

        DatabaseHelper mydb = new DatabaseHelper(ctx);
        HashMap<Integer,String> Country_list = mydb.getAllCountries();
        for (HashMap.Entry<Integer, String> entry : Country_list.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
            Cursor liste_des_pays;
            liste_des_pays=mydb.getCountry(key);
           if(liste_des_pays!=null&&liste_des_pays.moveToFirst()) {
               int icone = getResourceId(liste_des_pays.getString(2).toLowerCase(), "drawable", ctx.getPackageName(), ctx);
               models.add(new Model(icone, liste_des_pays.getString(2), value, false));
               liste_des_pays.close();
           }
        }

        Collections.sort(models, Model.TitleComparator);
        return models;
    }



    //fonction qui genère la liste des villes
    public static ArrayList<Model> generateCityList(Context ctx) {
        ArrayList<Model> models = new ArrayList<Model>();
        DatabaseHelper mydb=new DatabaseHelper(ctx);
        HashMap<Integer,String> Cities_list=mydb.getAllCities(ctx);
        for (HashMap.Entry<Integer, String> entry : Cities_list.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();

               models.add(new Model(R.drawable.ic_location,key+"", value, false));

        }
        Collections.sort(models, Model.TitleComparator);
        return models;
    }

    //fonction qui genère la liste des villes
    public static ArrayList<Model> generateCityList_from_CountryID(Context ctx,String nom) {
        ArrayList<Model> models = new ArrayList<Model>();
        DatabaseHelper mydb=new DatabaseHelper(ctx);
        HashMap<Integer,String> Cities_list = new HashMap<>() ;
        if (!nom.trim().equalsIgnoreCase(""))
        {
         Integer ent = Integer.valueOf(nom);
         Cities_list = mydb.getAllCities_from_countryID(ent);
        }
        else
        {
          startThisActivity(ctx,new Intent(ctx,CountryList.class));
        }

//        models.add(new Model(R.drawable.ic_location, "", "-- Ville --", false));
        for (HashMap.Entry<Integer, String> entry : Cities_list.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();

               models.add(new Model(R.drawable.ic_location,key+"", value, false));

        }
        Collections.sort(models, Model.TitleComparator);
        return models;
    }

     //fonction qui genère la liste des villes
    public static ArrayList<Model> generateCurrencies(String currencies) {
        ArrayList<Model> models = new ArrayList<Model>();
        if (currencies==null) return models;
        boolean vrai = false;
        try {
            vrai = true;
            JSONObject data = new JSONObject(currencies);
            JSONArray arr = data.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                models.add(new Model(-1, arr.getJSONObject(i).getString("pk_c_code"),arr.getJSONObject(i).getString("s_description")+" ( "+arr.getJSONObject(i).getString("s_name")+" )", false));
            }

        } catch (JSONException e) {
            vrai=false;
        }
        Collections.sort(models, Model.TitleComparator);
        return models;
    }

//fonction qui génère la liste des catégories
    public static ArrayList<Model> generateCategoriesList() {
        ArrayList<Model> models = new ArrayList<Model>();
        models.add(new Model(R.drawable.auto,"18","Automobile", false));
        models.add(new Model(R.drawable.immo,"12","Immobilier", false));
        models.add(new Model(R.drawable.job,"17","Emplois", false));
        models.add(new Model(R.drawable.style,"137","Mode", false));
        models.add(new Model(R.drawable.hight_tech,"124","High Tech", false));
        models.add(new Model(R.drawable.sale,"11","A vendre", false));
        models.add(new Model(R.drawable.services,"129","Services", false));
        Collections.sort(models, Model.TitleComparator);
        return models;
    }

//fonction qui renvoi le code kerawa d'un pays en fonction du code iso

    public static String countryName(String CountryISO,Context ctx){
        String value = "";
        Cursor cursor=new DatabaseHelper(ctx).getCountryFromISO(CountryISO);
        if(cursor!=null&&cursor.moveToFirst()) {
            value = String.valueOf(cursor.getInt(0));
        }
        return value;
    }

   //fonction qui renvoi le nom du pays actuel enfonction du code iso
    public static String Current_country(Context ctx){
        String CountryISO;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        CountryISO = prefs.getString("Kerawa-count", null);
        DatabaseHelper mydb=new DatabaseHelper(ctx);
        Cursor the_Country;
       if(CountryISO!=null) {
          the_Country = mydb.getCountryFromISO(CountryISO.toLowerCase());
        if(the_Country.moveToFirst()) {
            return the_Country.getString(1);
        }

       }
        return "";
    }


    public static boolean isMyServiceRunning(Class<?> serviceClass,Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
//ouverture de la page du post de l'annonce
    public static void Open_form(final Activity ctx, final int menuID, final int layoutID) {
        SharedPreferences session_name;
        final AlertDialog.Builder builder;
        session_name = PreferenceManager.getDefaultSharedPreferences(ctx);
        String username=session_name.getString("username",null);
        TelephonyManager tm;
        final TextView[] formswitcher = new TextView[1];

        //recuperation de l'IMEI du telephone
        tm= (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        final String imei=tm.getDeviceId();
        final Person user=new Person();
        builder = new AlertDialog.Builder(ctx);
        AlertDialog alertDialog = null;
        if(username==null) {
            String BoxTitle=layoutID==R.layout.dialog_layout ?"Connexion":"Création du compte";
            builder.setTitle(BoxTitle);
            final LayoutInflater inflater = ctx.getLayoutInflater();
            final View dialoglayout = inflater.inflate(layoutID, null);
            builder.setView(dialoglayout);
           String connectText=layoutID==R.layout.dialog_layout? "Se connecter":"Terminer";
            builder.setCancelable(false).setPositiveButton(connectText, new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int id) {
                   // log_in(ctx,dialoglayout,"https://188.226.250.218/signin",user);
                }
            }).setNegativeButton(R.string.plustard, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            alertDialog = builder.create();
            final AlertDialog finalAlertDialog = alertDialog;
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final Button b = finalAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                switch (layoutID) {
                case R.layout.dialog_layout:
                final EditText telephone = (EditText) dialoglayout.findViewById(R.id.votre_numero);
                final EditText mdp = (EditText) dialoglayout.findViewById(R.id.password);
                formswitcher[0] = (TextView) finalAlertDialog.findViewById(R.id.register_text);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (telephone.getText().toString().equals("") || mdp.getText().toString().equals("")) {
                            Show_Toast(ctx, "Veuillez remplir tous les Champs", true);
                            return;
                        }
                        user.SetImei(imei);
                        user.SetEmail(telephone.getText().toString());
                        user.SetPass(mdp.getText().toString());
                        log_in(ctx, menuID, dialoglayout, user);
                    }
                });

                formswitcher[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finalAlertDialog.cancel();
                        Open_form(ctx,menuID,R.layout.dialog_layout_register);
                    }

                });
                    break;
                    case R.layout.dialog_layout_register:
                    final LinearLayout first_step= (LinearLayout) dialoglayout.findViewById(R.id.first_step);
                    final EditText phone= (EditText) first_step.findViewById(R.id.votre_mobile);
                    final EditText email= (EditText) first_step.findViewById(R.id.your_email);
                    final LinearLayout step_two= (LinearLayout) dialoglayout.findViewById(R.id.step_two);
                    final RadioGroup pays= (RadioGroup) step_two.findViewById(R.id.liste_pays);
                    final EditText Name= (EditText) step_two.findViewById(R.id.user_name);
                    final RadioGroup villes= (RadioGroup) step_two.findViewById(R.id.liste_villes);
                    final ShowHide[] v1 = new ShowHide[1],v2= new ShowHide[1];
                    try{phone.setText(new AndroideDevice(ctx).getPhone_number());}catch (Exception e){Show_Toast(ctx,e.toString(),true);}
                    DatabaseHelper mydb=new DatabaseHelper(ctx);

                    HashMap<Integer,String> Country_List;

                    Country_List=mydb.getAllCountries();

                    add_Rabuttons_TO_RadioGroup ( Country_List,"Pays de vos annonces",ctx,pays);

                    pays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                Cities_loader CL=new Cities_loader();
                                CL.ctx=ctx;
                                CL.v=dialoglayout.findViewById(R.id.loader2);
                                CL.CountryID=checkedId;
                                CL.rgx=villes;
                                CL.Texte="Ville de vos annonces";
                                pays.setEnabled(false);
                                CL.execute();
                                pays.setEnabled(true);
                            }
                        });



                    b.setText("Terminer");

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                         String _error_text="";

                         if(phone.getText().toString().equals("")) _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez entrer un numero";
                         if(email.getText().toString().equals("")) _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez entrer une adresse mail";
                         if(!(pays.getCheckedRadioButtonId()>0))   _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez choisir un pays";
                         if(!(villes.getCheckedRadioButtonId()>0)) _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez choisir une ville";
                         if((Name.getText().toString().equals(""))) {
                             Name.requestFocus();                  _error_text += (!_error_text.equals("") ? "\r\n" : "") + "Veuillez Préciser un nom";
                         }
                        if(!_error_text.equals("")){

                            Show_Toast(ctx,_error_text,true);

                        }else{


                            user.SetEmail(email.getText().toString());
                            user.SetName(Name.getText().toString());
                            user.SetUsername(Name.getText().toString());
                            user.SetUserPhones(phone.getText().toString());
                            user.SetCountry(pays.getCheckedRadioButtonId());
                            user.SetCityId(villes.getCheckedRadioButtonId());
                            user.SetImei(new AndroideDevice(ctx).getImei());
                            register(ctx, menuID, dialoglayout, user);


                        }

                        }});
                    break;

                }
     }
        });

        alertDialog.show();

             }else{

            switch(menuID) {
                case R.id.action_account:
                ctx.startActivity(new Intent(ctx, ActivityUserAccount.class));
                ctx.finish();
                break;
                case R.id.action_add:
                //open_ads_form(ctx);
                break;
            }

        }
    }


    //fermeture de session

    public static void log_out(Activity ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("username");
        editor.apply();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edito = pref.edit();
        edito.remove("country");


        ctx.startActivity(new Intent(ctx, AdsListActivity.class));
        ctx.finish();
    }

    public static void Save_sharedString(Context ctx, String SharedName, String SharedValue){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedName, SharedValue);
        editor.apply();
    }


    public static String Load_sharedString(Context ctx, String SharedName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String retour=prefs.getString(SharedName, null);
        if (retour==null){
           return "";
        }else{
            return retour;
        }

    }

    public static void destroy_sharedString(Context ctx, String SharedName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(SharedName).apply();
    }



    public static void destroyvips(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("currentVip");
        editor.apply();
    }

    public static void saveVipSession(Context ctx,String vipList){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currentVip", vipList);
        editor.apply();
    }

    public static String get_current_VipList(Context ctx){

        SharedPreferences session_name;
        session_name = PreferenceManager.getDefaultSharedPreferences(ctx);
        return session_name.getString("currentVip",null);

    }


//finish an actifity anywhere
public static void finishThisActivity(Context ctx) {   ((Activity) ctx).finish();
}
    public static Activity activite(Context ctx){
      return  (Activity) ctx;
    }
    public  static void startThisActivity(Context ctx,Intent i) {
     ctx.startActivity(i);
}
    public  static void startthisService(Context ctx,Intent i) {
     ctx.startService(i);
}

//Méthode permettant d'enregistrer un utilisateur à partir de n'importe quel contexte

public static void register(Activity ctx, int menuID, View dialoglayout, final Person person){

    PostDataRegister signup= new PostDataRegister();
    signup.ctx=ctx;
    signup.menuID=menuID;
    signup.items=person;
    signup.v=dialoglayout.findViewById(R.id.loader2);
    signup.v2= (TextView) dialoglayout.findViewById(R.id.status_text);
    signup.execute();


}

    public static void register2(Activity ctx, final Person person){

    PostDataRegister signup= new PostDataRegister();
    signup.ctx=ctx;
    signup.menuID=R.id.action_account;
    signup.items=person;
    signup.v= ctx.findViewById(R.id.linearLayout4);
    signup.v2= (TextView) ctx.findViewById(R.id.status_text);
    signup.execute();


}


    // Saving offline ads in local database (In the queue)...............

    public static boolean add_ads_to_queue(Annonce ads, Context ctx){
        String errorText="";
        DatabaseHelper db = new DatabaseHelper(ctx);
        ContentValues contentValues = new ContentValues();
        contentValues.put("ad_id","");
        contentValues.put("title", ads.getTitle());
        if (ads.getTitle().trim().equals("")) errorText+="Le titre est réquis"+add_breakline(errorText.equals(""));
        contentValues.put("nbpic",ads.getNbpics());
        contentValues.put("date",ads.getDate());
        contentValues.put("description",ads.getDescription());
        if (ads.getDescription().trim().equals("")) errorText+=add_breakline(errorText.equals(""))+"La description est réquise";
        contentValues.put("category",ads.getCategory_id()+"");
        contentValues.put("subcategory",ads.getsubCategory_id()+"");
        if ((ads.getsubCategory_id() + "").equals("")||ads.getsubCategory_id()==0) errorText+=add_breakline(errorText.equals(""))+"Merci de choisir une sous catégorie";
        contentValues.put("price",ads.getPrice()+"");
        contentValues.put("currency",ads.getCurrency());
        contentValues.put("phone1",ads.getPhone1());
        contentValues.put("phone2",ads.getPhone2());
        contentValues.put("countryID",ads.getCountry_id()+"");
        contentValues.put("cityID",ads.getCity_ID()+"");
        if ((ads.getCity_ID() + "").equals("")||ads.getCity_ID()==0) errorText+=add_breakline(errorText.equals(""))+"Merci de choisir une ville";
        contentValues.put("email", Variables_Session(ctx).getEmail());
        contentValues.put("status", "false");
        if(!errorText.equals("")){
            try {
                TextView statusText= (TextView) ((Activity)ctx).findViewById(R.id.status_text);
                Errors_handler msg_err = new Errors_handler((Activity) ctx,ctx, errorText, true, statusText, R.drawable.errore);
                msg_err.execute(500, 500, 6000);
            }catch (Exception e){
                Show_Toast(ctx,e.toString(),true);
            }
            return false;
        }

        if (db.db_save_ads(contentValues)!=0) {
            Show_Toast(ctx, "Votre annonce a été ajoutée à la fille d'attente \r\nVous avez maintenant " + db.get_onQueue_Ads() + " en attente", true);
            return true;
        }

        return false;

    }

    private static String add_breakline(boolean condition){
        if(!condition) return "";
        return "\r\n";
    }


    public static int getResourceId(String pVariableName, String pResourcename, String pPackageName,Context ctx)
    {
        try {
            return ctx.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getCountryIcon(Context ctx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String nom = prefs.getString("Kerawa-count", null);
        int  CountryID= Integer.parseInt(countryName(nom,ctx));
        DatabaseHelper db=new DatabaseHelper(ctx);
        Cursor country=db.getCountry(CountryID);
        if(country.moveToFirst()) {
            String CountryIcon = country.getString(2);
            return getResourceId(CountryIcon, "drawable", ctx.getPackageName(), ctx);
        }
        country.close();
        return 0;
    }

    public static void LaunchSplash(final Context ctx, final int Sleep,TextView vx){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String nom = prefs.getString("Kerawa-count",null);
        Thread timerThread = new Thread(){
            public void run(){
               // try{
                try {
                    sleep(Sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TagManager tagManager = TagManager.getInstance(ctx);
                    tagManager.setVerboseLoggingEnabled(true);

                    PendingResult<ContainerHolder> pending =
                            tagManager.loadContainerPreferNonDefault("GTM-KZ775K",
                                    R.raw.gtm_wkwkcp);

                    pending.setResultCallback(new ResultCallback<ContainerHolder>() {
                        @Override
                        public void onResult(ContainerHolder containerHolder) {
                            ContainerHolderSingleton.setContainerHolder(containerHolder);
                            Container container = containerHolder.getContainer();
                            if (!containerHolder.getStatus().isSuccess()) {
                                Log.e("KerawaMobile", "failure loading container");

                                return;
                            }
                            ContainerHolderSingleton.setContainerHolder(containerHolder);
                            ContainerLoadedCallback.registerCallbacksForContainer(container);
                            containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                            Krw_functions.pushOpenScreenEvent(ctx, Krw_functions.Current_country(ctx) + "/splash_screen_opened");

                            if (nom != null) {
                                ctx.startActivity(new Intent(ctx, AdsListActivity.class).putExtra("pays",nom));
                                finishThisActivity(ctx);
                            }
                            else {
                                ctx.startActivity(new Intent(ctx,CountryList.class));
                                finishThisActivity(ctx);
                            }


                        }


                    }, 2, TimeUnit.SECONDS);
                /*}catch(InterruptedException e){
                    e.printStackTrace();
                }finally{*/

                }
         //   }
        };
        if (nom != null) {
        try{
            vx.setText("Chargement Kerawa " + Current_country(ctx));}
            catch(Exception e){
            Show_Toast(ctx,e.getLocalizedMessage(),true);
          }
        }
        timerThread.start();
    }

//Configuration automatique de Kerawa By Martin
    public static void AutoConfig(Context ctx, TextView vx, ImageView refresher, TextView mytext, RelativeLayout popopwin){
        InstallKerawa loader= new InstallKerawa();
        loader.ctx=ctx;
        loader.refresher=refresher;
        loader.mytext=mytext;
        loader.popop_win=popopwin;
        loader.textview= vx;
        loader.execute();
    }

//Fonction qui renvoit la clé d'autorisation de session  By Martin
    public static String getAuthorisationKey(Context ctx) {
          String name=get_current_Session(ctx);
        try {
                JSONObject json=new JSONObject(name);
                return json.getString("authorization_key");
        } catch (JSONException e) {
           // Show_Toast(ctx,String.valueOf(e),true);
            return null;
        }
    }

//Fonction qui renvoit la variable de session courante   By Martin

    public static String get_current_Session(Context ctx){

        SharedPreferences session_name;
        session_name = PreferenceManager.getDefaultSharedPreferences(ctx);
       return session_name.getString("username",null);

    }

    public static void Open_Connection_or_register(Context ctx,int MenuID){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String username = prefs.getString("username", null);
        if(username==null) {
            Intent i = new Intent(ctx,  LoginActivity.class);
            Bundle extras = new Bundle();
            extras.putString("MenuId", MenuID+"");
            i.putExtras(extras);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
           // finishThisActivity(ctx);
        }else{
            Intent i = new Intent(ctx, ActivityUserAccount.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent j = new Intent(ctx,createAdsActivity.class);
            j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          switch (MenuID){
              case R.id.action_add:
                  ctx.startActivity(j);
              break;
              case R.id.action_account:
                  ctx.startActivity(i);
              break;

          }
        }
    }


    public static void setAlarm(Context ctx){


        //Log.d("Alarm function", "alarm setted from the function");

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(ctx, AlarmRecieverFinal.class);
        //alarmIntent.putExtra(AlarmRecieverFinal.EXTRA_ID, 1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
        Calendar alarmStartTime = new GregorianCalendar();
        Calendar cal = Calendar.getInstance();


        if((cal.get(Calendar.HOUR_OF_DAY)*60+30)>751) {
          alarmStartTime.set(Calendar.DAY_OF_WEEK, (cal.get(Calendar.DAY_OF_WEEK) != 7) ? cal.get(Calendar.DAY_OF_WEEK) + 1 : 1);
        }

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 12);
        alarmStartTime.set(Calendar.MINUTE, 30);
        alarmStartTime.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        // Show_Toast(ctx, "alarm registered", false);
    }




    public static float megabytesAvailable() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        return bytesAvailable / (1024.f * 1024.f);
    }

    public static Boolean checkrigths()
   {
       Boolean ans = false ;
       File f = new File(Environment.getExternalStorageDirectory().getPath());
       if(f.canWrite()) {
           ans = true ;
       }
      return ans ;
   }

    public static void getemailData(Context ctx, RadioGroup rgx) {
        HashMap<Integer, String> accountsList = new HashMap<>();
        Integer cmt = 1;
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        String[] concat=new String[accounts.length];
        //Getting all registered Google Accounts;
        try {
            for (Account account : accounts) {
                if(account.name.indexOf("@")>0&&!ArrayUtils.contains(concat, account.name) ) {
                    accountsList.put(cmt, account.name);
                    concat[cmt]=account.name;
                    cmt++;
                }
            }
        } catch (Exception e) {
            Show_Toast(ctx, "Exception:" + e, true);
        }

        if(accountsList.size()>0) add_Rabuttons_TO_RadioGroup(accountsList, "Choisir une de vos adresses", ctx, rgx);
    }
    public static String getMainemail(Context ctx) {
        HashMap<Integer, String> accountsList = new HashMap<>();
        Integer cmt = 1;
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        ArrayList<String> contact = new ArrayList<>();
        String res = "";
        //Getting all registered Google Accounts;
        try {
            for (Account account : accounts) {
                if(account.name.indexOf("@")>0&&!contact.contains(account.name) ) {
                    accountsList.put(cmt, account.name);
                    contact.add(account.name);
                    cmt++;
                }
            }
        } catch (Exception e) {
            Show_Toast(ctx, "Exception:" + e, true);
        }

        if(accountsList.size()>0)  res = contact.get(0);
      return res;
    }

    public static ArrayList<String> getemailsList(Context ctx) {
        ArrayList<String> accountsList = new ArrayList<>();
        Integer cmt = 1;
        Account[] accounts = AccountManager.get(ctx).getAccounts();
        String[] concat=new String[accounts.length];
        //Getting all registered Google Accounts;
        try {
            for (Account account : accounts) {
                if(account.name.indexOf("@")>0&&!ArrayUtils.contains(concat, account.name) ) {
                    accountsList.add(account.name);
                    concat[cmt]=account.name;
                    cmt++;
                }
            }
        } catch (Exception e) {
            Show_Toast(ctx, "Exception:" + e, true);
        }


        return accountsList;
    }

    public static Person Variables_Session(Context ctx){

                     JSONObject json;
                     Person moi=new Person();

                  try {
                    json=new JSONObject(get_current_Session(ctx));
                   // String  Nom_user= json.getString("name");
                    String  telephone=json.getString("phone_mobile");
                  //  String  email=json.getString("email");
                    String  City=json.getString("city");
                    String user_id = json.getString("user_id");

                    String AuthorisationKey=getAuthorisationKey(ctx);
                 //    moi.SetUsername(Nom_user);
                     moi.SetImei(new AndroideDevice(ctx).getImei());
                  //   moi.SetEmail(email);
                     moi.SetUserPhones(telephone);
                     moi.SetAutorisationKey(AuthorisationKey);
                     moi.SetCity(City);
                     moi.SetUserID(Integer.parseInt(user_id));

                  } catch (Exception e) {
                     // Show_Toast(ctx,e.toString(),false);
                      e.printStackTrace();
                  }

              return moi;
              }
    public static void saveCurrency(Context ctx, String result) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currentcies", result);
        editor.apply();
    }

   public static void ShowToastWithinService(final Context ctx, final String message)
   {
    Handler h = new Handler(ctx.getMainLooper());
       h.post(new Runnable() {
           @Override
           public void run() {
               //Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
           }
       });
    }

    public static Map<String,String> getMetas()
    {

        return null ;
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

    // Tag Manager
    public static void pushOpenScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "openScreen", "screenName", screenName));
        //dataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", screenName));
    }

    public static void pushButtonEvent(Context context, String Bname) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "ButtonClicked", "buttonName", Bname));


//        dataLayer.pushEvent("ButtonClicked", DataLayer.mapOf("buttonName", Bname));
    }
    public static void pushScrollEvent(Context context, String PageName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "PageScrolled", "PageName", PageName));

//        dataLayer.pushEvent("PageScrolled", DataLayer.mapOf("PageName", PageName));
    }
    public static void pushAdclickedEvent(Context context, String AdUrl) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "AdClicked", "AdUrl", AdUrl));

//        dataLayer.pushEvent("AdClicked", DataLayer.mapOf("AdUrl", AdUrl));
    }

    public static void pushParseAdclickedEvent(Context context, String AdUrl) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "ParseAdClicked", "AdUrl", AdUrl));

//        dataLayer.pushEvent("ParseAdClicked", DataLayer.mapOf("AdUrl", AdUrl));
    }
    public static void pushCountryEvent(Context context, String CountryName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("CountrySelected", DataLayer.mapOf("CountryName", CountryName));
    }
    public static void pushContextMenuEvent(Context context, String ContextName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("ContextMenu", DataLayer.mapOf("CountryName", ContextName));
    }
    public static void pushSearchEvent(Context context, String SearchName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.push(DataLayer.mapOf("event", "SearchString", "SearchString", SearchName));

        //dataLayer.pushEvent("SearchString", DataLayer.mapOf("SearchString", SearchName));
    }


    public static ArrayList<String> get_user_contact(Context ctx){
        ArrayList<String> liste_contacts = new ArrayList<>();
        //liste_contacts.clear();
        int nbuser=0;
        String phoneNumber;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;


        ContentResolver contentResolver = ctx.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {



                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        //create the json and send it to the server
                        JSONObject json = new JSONObject();
                        try {
                            json.put("numero",phoneNumber);
                            json.put("name", name);
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                        liste_contacts.add(json.toString());//new Model(R.drawable.mon_compte,phoneNumber,name,false));
                    }

                    phoneCursor.close();
                    nbuser++;

                }


            }

        }
        //Log.d("kerawa_results",nbuser+"");
        cursor.close();
        return liste_contacts;

    }




    public static List<String> getShareApplication(){
        List<String> mList=new ArrayList<String>();
        mList.add("com.facebook.katana");
        mList.add("com.twitter.android");
        mList.add("com.facebook.orca");
       // mList.add("com.google.android.gm");
        mList.add("com.whatsapp");
        return mList;

    }



    public static ArrayList<PhoneContactInfo> getAllPhoneContacts(Context ctx, ArrayList<PhoneContactInfo> arrContacts,JSONObject obj) {

        if(arrContacts == null) arrContacts = new ArrayList<PhoneContactInfo>();

        //List<NameValuePair> nameValuePairs= new ArrayList<>();

        PhoneContactInfo phoneContactInfo = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.RawContacts.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Email._ID, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.RawContacts.CONTACT_ID}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        cursor.moveToFirst();

        JSONObject liste_contact = new JSONObject();
        JSONArray contacts = new JSONArray();
        while (!cursor.isAfterLast()) {
            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            int phoneContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            int ContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));


            String contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

            String type = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
            String whatsapp = "false";
            if (type.equalsIgnoreCase("com.whatsapp"))
            {
                whatsapp = "true";
            }

            phoneContactInfo = new PhoneContactInfo();

            phoneContactInfo.setPhoneContactID(phoneContactID);
            phoneContactInfo.setContactName(contactName);
            phoneContactInfo.setContactNumber(contactNumber);
            phoneContactInfo.setContactEmail(contactEmail);

            arrContacts.add(phoneContactInfo);


                JSONObject lecontact = new JSONObject();
                try {
                    lecontact.put("contact_name", contactName);
                    lecontact.put("contact_phone", contactNumber);
                    lecontact.put("contact_has_whatsapp", whatsapp);
                    lecontact.put("contact_id", ContactID);
                    //ajouter le contact id ici

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                contacts.put(lecontact);

            cursor.moveToNext();
        }
        TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String country = telephonyManager.getSimCountryIso();
        Locale locale = Locale.getDefault();
        String local_country = locale.getCountry();
        TimeZone tz = TimeZone.getDefault();
            try {

                liste_contact.put("contacts", contacts);
                if (obj!=null) {
                    obj.put("phones", contacts);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        Log.d("Lejson", liste_contact.toString());
        cursor.close();
        return arrContacts;
    }
  public static void getAllPhoneEmails(Context ctx,JSONObject obj) {



        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.RawContacts.CONTACT_ID,ContactsContract.CommonDataKinds.Email.DATA, ContactsContract.CommonDataKinds.Email.DISPLAY_NAME}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        cursor.moveToFirst();


        JSONArray contacts = new JSONArray();
        while (!cursor.isAfterLast()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME));
            int ContactID = cursor.getInt(cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
            String contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                JSONObject lecontact = new JSONObject();
                try {

                    lecontact.put("contact_email", contactEmail);
                    lecontact.put("contact_name", contactName);
                    lecontact.put("contact_id", ContactID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                contacts.put(lecontact);

            cursor.moveToNext();
        }
       try {
           if (obj!=null) {
                obj.put("emails", contacts);
              }
            } catch (JSONException e) {
                e.printStackTrace();
            }

      cursor.close();

    }


    public static class PhoneContactInfo {

        private int contactId;
        private String contactName;
        private String contactNumber;
        private String contactEmail;

        public void setPhoneContactID(int phoneContactID) {
            this.contactId=phoneContactID;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }


        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getContactName(){
            return this.contactName;
        }

        public String getContactNumber(){
            return this.contactNumber;
        }

        public String getContactEmail(){
            return this.contactEmail;
        }
    }

    public static boolean send_contact(String url, List<NameValuePair> nameValuePairs, Context ctx) {
        String response = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("X-Kerawa-Api-Consumer-Key",new Kerawa_Parameters(ctx).Hide_meta_data());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            Log.d("LOGTAG", "POST Response >>> " + response);
            editor.putString("envois", "non");
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    public static boolean can_send_contact(Context ctx){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final Long last = prefs.getLong("envois", 0);

        Long now = System.currentTimeMillis();

        return (now-last)/1000>=2592000;

    }

    public static ArrayList<Model> countries_code(Context ctx) {
        ArrayList<Model> liste=new ArrayList<>();
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.countrycode);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.v("Text Data", byteArrayOutputStream.toString());
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                String Country = jsonArray.getJSONObject(i).getString("COUNTRY"),code=jsonArray.getJSONObject(i).getString("COUNTRY_CODE"),iso=jsonArray.getJSONObject(i).getString("ISO_CODES").toLowerCase().split("-")[0];
                liste.add(new Model(-1,iso+"-"+code,code+" -- "+ Country,false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static ArrayList<Drugstore> drugstores(Context ctx, String region, String cityName) {
        ArrayList<Drugstore> liste=new ArrayList<>();
        if (region.trim().isEmpty()||cityName.trim().isEmpty()) return liste;
        JSONArray liste_phone;
        String drliste=load_drugstores(ctx);
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONObject jsonObject = new JSONObject(drliste);
            JSONArray arr=jsonObject.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString("region")!=null&&arr.getJSONObject(i).getString("region").equalsIgnoreCase(region)&&arr.getJSONObject(i).getString("ville")!=null&&arr.getJSONObject(i).getString("ville").equalsIgnoreCase(cityName)){
                    Drugstore drs=new Drugstore();

                if (arr.getJSONObject(i).getString("region")!=null){
                    drs.setRegion(arr.getJSONObject(i).getString("region"));
                }

                if (arr.getJSONObject(i).getString("ville")!=null){
                    drs.setCity(arr.getJSONObject(i).getString("ville"));
                }

                if (arr.getJSONObject(i).getString("nom")!=null){
                    drs.setStoreName(arr.getJSONObject(i).getString("nom"));
                }
                if (arr.getJSONObject(i).getString("localisation")!=null){
                    drs.setLocalisation(arr.getJSONObject(i).getString("localisation"));
                }

                if (arr.getJSONObject(i).getJSONArray("telephone")!=null){
                    ArrayList<String> phones=new ArrayList<>();
                    liste_phone=arr.getJSONObject(i).getJSONArray("telephone");
                    for (int k=0;k<liste_phone.length();k++){
                        phones.add(liste_phone.get(k).toString());
                    }
                    drs.setPhones(phones);
                }
               liste.add(drs);
                }
            }
           /* Log.d("drugstore","Drugstore downloaded");
            int id=jsonObject.getInt("id");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            prefs.edit().remove("id_fichier").apply();
            prefs.edit().putInt("id_fichier",id).apply();
            show_drugstore_Notification(ctx);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static String load_drugstores(Context ctx) {
        //InputStream inputStream = ctx.getResources().openRawResource(R.raw.pharmacy);
        File sdcard = Environment.getExternalStorageDirectory();
        File traceFile = new File(sdcard.getAbsoluteFile(), "pharmacy.json");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String pharmacy_list = prefs.getString("pharmacy_list", null);
        InputStream inputStream = null;
        if (pharmacy_list != null) {
            inputStream = new ByteArrayInputStream(pharmacy_list.getBytes(Charset.forName("UTF-8")));
        }
        ;
       /* FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(traceFile);
        } catch (FileNotFoundException e) {
            Log.d("drugstoreerror",e.toString());
        }*/
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            if (inputStream != null) {
                ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       return byteArrayOutputStream.toString();
    }

    public static ArrayList<Model> drugstore_regions(Context ctx,String region,String theregion){
        ArrayList<Model> regions=new ArrayList<>();
        String drlist=load_drugstores(ctx);
        String regionType=region;
        if (region.equals("")) regionType="region";
        String precedent="";
      try{
        JSONObject jo=new JSONObject(drlist);
        JSONArray arr=jo.getJSONArray("data");
        for (int i = 0; i < arr.length(); i++) {
                 Model row=new Model();
            if (regionType.equals("region")) {
                if (arr.getJSONObject(i).getString(regionType) != null&& !precedent.equals(arr.getJSONObject(i).getString(regionType))) {
                    row.setTag(arr.getJSONObject(i).getString(regionType));
                    row.setTitle(arr.getJSONObject(i).getString(regionType));
                    row.setIcon(-1);
                    regions.add(row);
                    precedent=arr.getJSONObject(i).getString(regionType);
                }
            }else{
                if (arr.getJSONObject(i).getString(regionType) != null&& arr.getJSONObject(i).getString("region").equals(theregion)&& !precedent.equals(arr.getJSONObject(i).getString(regionType))) {
                    row.setTag(arr.getJSONObject(i).getString(regionType));
                    row.setTitle(arr.getJSONObject(i).getString(regionType));
                    row.setIcon(-1);
                    regions.add(row);
                    precedent=arr.getJSONObject(i).getString(regionType);

                }
            }

        }


    } catch (Exception e) {
        e.printStackTrace();
    }
        return regions;
    }

    public static String code_from_text(String text){
        return text.split("-")[1];
    }

    public static void addShortcut(Context context)
    {
        Intent shortcutIntent = new Intent(context, splash.class);
        //shortcutIntent.setClassName(context, "splash");
        setAlarm(context);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Kerawa");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));
        addIntent.putExtra("duplicate", false);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }



    public static void setupWebViewClient(final WebView mWebView, final Activity ctx) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                new ShowHide(ctx.findViewById(R.id.loader)).Hide();
                ctx.setTitle(view.getTitle());
            }
        });
    }

    public static String join (ArrayList<String> s, String delimiter)
    {
        if (s == null || s.isEmpty()) return "";
        return TextUtils.join(delimiter,s);
    }

    public static void create_drugstore_list(Context ctx, ListView list,String Region,String cityName){
        ArrayList<Drugstore> drsList = drugstores(ctx,Region,cityName);
        //Log.d("drugstore",drsList.size()+"");
        list.setAdapter(new Drugstore_Adapter(ctx, R.layout.drugstore_row, drsList));
    }

    public static void show_drugstore_Notification(Context ctx) {
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, new Intent(ctx, Drug_store_list.class), 0);
        Resources r = ctx.getResources();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentIntent(pi).setTicker(r.getString(R.string.notification_text))
                .setSmallIcon(R.drawable.icone)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText(r.getString(R.string.notification_text))
                .setContentIntent(pi)
                //.setSound(Uri.parse(ctx.getResources().getResourceName(R.raw.bike_horn)))
                //.setSound(Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.bike_horn))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public static String millisecondTodate(long millis,String template){
        Date date=new Date(millis);
        DateFormat formatter = new SimpleDateFormat(template, Locale.FRENCH);
        return formatter.format(date);
    }

    public static void create_menu(ArrayList<Model> elements, ListView listView, Context ctx){
        Home_Adapter adapter = new Home_Adapter(ctx, elements);
        listView.setAdapter(adapter);
    }

    public static void cron_service_executor(int inter, Intent service, Context ctx, String savedIstant){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        Calendar today = Calendar.getInstance();
        int currentH = today.get(Calendar.HOUR_OF_DAY);
        Date before=new Date(prefs.getLong(savedIstant, 0));
        int pastH=Integer.valueOf(millisecondTodate(before.getTime(),"HH"));
        if (pastH>currentH) currentH=currentH+24;
        int duration=currentH-pastH;
        Log.d("CronTag","duree:"+duration+" "+savedIstant);
        boolean canrun=duration>=inter;
        if (canrun) startthisService(ctx,new Intent(service));
    }

    public static void _Openurl(String url, Context ctx) {
        Intent lien=new Intent(ctx, WebPageLoader.class);
        lien.putExtra("lien", url);
        lien.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(lien);
    }





    public  static int getDP(int size,Context ctx)
    {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int pixels = (int) (size* scale + 0.5f);
        return pixels ;
    }

    public static boolean can_delete_files(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final Long last = prefs.getLong("delete", 0);
        Long now = System.currentTimeMillis();
        return  (now-last)/1000>1296000;
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();

            for (int i=0; i<children.length; i++) {
                File file = new File(dir, children[i]);
                Date lastModDate = new Date(file.lastModified());

                long today = System.currentTimeMillis();
                if ((today-lastModDate.getTime())/1000>=3600/*1296000*/) {

                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }

            }
        }

        return dir.delete();
    }


    public static int firt_integer(String string){
        int i = 0;
        while (!Character.isDigit(string.charAt(i))) i++;
        int j = i;
        while (Character.isDigit(string.charAt(j))) j++;
        return Integer.parseInt(string.substring(i, j));
    }

    public static void make_volley_request(String url,Context ctx){

    }
    public static  String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time* 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.toString();
    }
public static String AdtoJson(Annonce ad)
{
    JSONObject result = new JSONObject();

    try {
        result.put("ad_id",ad.getAdd_id());
        result.put("ad_title", ad.getTitle());
        result.put("ad_price", ad.getPrice());
        result.put("ad_short_link", ad.getFriendly_url());
        result.put("ad_user_id", ad.getUser_id());
        result.put("ad_currency", ad.getCurrency());
        result.put("ad_city_name", ad.getCity_name());
        result.put("ad_phonenumber", ad.getPhone1());
        result.put("ad_description", ad.getDescription());
        result.put("ad_contactemail", ad.getEmail());
        result.put("ad_category_name", ad.getCategory_name());
        result.put("ad_url", ad.getAd_url());
        result.put("ad_date", ad.getDate());
        JSONArray jsonArrayImageUrl = new JSONArray();
        ArrayList<String> arrayList = ad.getImgList();
        for(int i = 0; i < arrayList.size(); i++){
            jsonArrayImageUrl.put(arrayList.get(i));
        }
        result.put("ad_images_urls", jsonArrayImageUrl);

        JSONArray jsonArrayMetas = new JSONArray();

        try {
            HashMap<String, String> map = ad.getMetas();
            Set<String> iterator = map.keySet();
            Iterator<String> it = iterator.iterator();
            while(it.hasNext()){
                JSONObject jsonObject = new JSONObject();
                String key = it.next();
                jsonObject.put("meta_description", key);
                jsonObject.put("meta_value", map.get(key));
                jsonArrayMetas.put(jsonObject);
            }
            result.put("ad_metas", jsonArrayMetas);
        }catch (NullPointerException e){
            Log.d("NullPointer", e.getLocalizedMessage());
        }



    } catch (Exception e) {
        Log.d("AdtoJson", e.getLocalizedMessage());
    }

    return result.toString();
}
}


