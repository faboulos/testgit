package com.kerawa.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Thing;
import com.kerawa.app.helper.DatabaseHelper;
import com.kerawa.app.helper.GetImages;
import com.kerawa.app.helper.ImageStorage;
import com.kerawa.app.services.PostAdsService;
import com.kerawa.app.utilities.Ads_Form_Grid_adapter;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.Spinner_adapter;
import com.kerawa.app.utilities.ads_Model;
import com.newrelic.agent.android.NewRelic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.kerawa.app.utilities.Krw_functions.Open_Connection_or_register;
import static com.kerawa.app.utilities.Krw_functions.Show_Toast;
import static com.kerawa.app.utilities.Krw_functions.countryName;
import static com.kerawa.app.utilities.Krw_functions.generateCategoriesList;
import static com.kerawa.app.utilities.Krw_functions.generateCityList_from_CountryID;
import static com.kerawa.app.utilities.Krw_functions.generateCountryList;
import static com.kerawa.app.utilities.Krw_functions.generateCurrencies;
import static com.kerawa.app.utilities.Krw_functions.generate_spinner;
import static com.kerawa.app.utilities.Krw_functions.isMyServiceRunning;
import static java.lang.StrictMath.abs;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class createAdsActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private Uri mImageCaptureUri;
    int adsID;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;


    String imageTempName;

    Spinner cat,subcat,Cities,monn,pays;
    EditText titre,description,prix,phone1,phone2;
    //GridView image_grid;
    GridView image_grid;
    ArrayList<ads_Model> objets;
    ArrayList<Model> villes,values,lespays,listemonn;
    Ads_Form_Grid_adapter myadapter;
    DisplayMetrics display;
    int DisplayHeight,DisplayWidth;
    DatabaseHelper mydb;
    private int SubCategoryID;
    private ArrayList<String> listeUrl;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // expecting errors monitoring app
        NewRelic.withApplicationToken(

                "AA596b53bae74528bfb423356bd9d0d07d86142d4e"
        ).start(this.getApplication());


        Krw_functions.pushOpenScreenEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/Create_Ads_Activity");
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //mUrl = "http://examplepetstore.com/dogs/standard-poodle";
        mTitle = "Standard Poodle";
        mDescription = "The Standard Poodle stands at least 18 inches at the withers";

        setContentView(R.layout.activity_create_ads);
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs2.getString("username", null);
        if(username==null) {
            Intent i = new Intent(getApplicationContext(),  LoginActivity.class);
            Bundle extras = new Bundle();
            extras.putString("MenuId", R.id.action_add+"");
            i.putExtras(extras);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            // finishThisActivity(ctx);
        }

        objets=new ArrayList<ads_Model>();
        myadapter=new Ads_Form_Grid_adapter(getApplicationContext(), R.layout.photos_item,objets);
        adsID= abs((int) System.currentTimeMillis());
        image_grid= (GridView) findViewById(R.id.image_taker);
        image_grid.setAdapter(myadapter);

        final DatabaseHelper mydb=new DatabaseHelper(getApplicationContext());

         cat= (Spinner) findViewById(R.id.categories);
         subcat= (Spinner) findViewById(R.id.subcat);
         Cities= (Spinner) findViewById(R.id.liste_villes);
         monn= (Spinner) findViewById(R.id.price_selector);
         pays= (Spinner) findViewById(R.id.liste_pays);
         titre= (EditText) findViewById(R.id.titre_annonce);
         description= (EditText) findViewById(R.id.description);
         prix= (EditText) findViewById(R.id.price);
         phone1= (EditText) findViewById(R.id.phone1);
         phone2= (EditText) findViewById(R.id.phone2);

         phone1.setText(Krw_functions.Variables_Session(getApplicationContext()).getUserPhones());
         phone2.setHint("Telephone 2");

         lespays=generateCountryList(getApplicationContext());
         values=generateCategoriesList();
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String nom=prefs.getString("Kerawa-count",null);
         villes=generateCityList_from_CountryID(getApplicationContext(),countryName(nom,getApplicationContext()));
         String currencies = prefs.getString("currentcies", null);
         listemonn=generateCurrencies(currencies);

       //---Generate all spinners
           generate_spinner(values, getApplicationContext(),cat);
           generate_spinner(villes, getApplicationContext(),Cities);
           generate_spinner(listemonn, getApplicationContext(),monn);
           generate_spinner(lespays, getApplicationContext(), pays);

        pays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Model model = (Model) pays.getSelectedItem();
                String nom = countryName(model.getTag(), getApplicationContext());
                villes = generateCityList_from_CountryID(getApplicationContext(),nom);
                generate_spinner(villes, getApplicationContext(), Cities);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               Model model = (Model) cat.getSelectedItem();
               String nom = model.getTag();
               ArrayList<Model> models = new ArrayList<Model>();
               HashMap<Integer,String> liste_sub=mydb.getAllCategories(nom);
               for (HashMap.Entry<Integer, String> entry : liste_sub.entrySet()) {

                   int key = entry.getKey();
                   String value = entry.getValue();
                   models.add(new Model(R.drawable.ic_categories,key+"",value,false));
               }
               generate_spinner(models, getApplicationContext(), subcat);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

        int i = 0;
        do {
            Model elt = (Model) pays.getItemAtPosition(i);
            if(elt.getTag().equals(nom)) pays.setSelection(i);
            i++;
        }while (i<pays.getCount());

        i=0;

        do {
            Model elt= (Model) monn.getItemAtPosition(i);
            if(elt.getTag().equalsIgnoreCase("xaf")) monn.setSelection(i);
            i++;
        }while (i<monn.getCount());

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
           // getSupportActionBar().setIcon(R.drawable.icone);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listeUrl= new ArrayList<>();
        outState.putStringArrayList("photolist", listeUrl);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        listeUrl =   savedState.getStringArrayList("photolist");
       if (listeUrl != null) {
            for (int i = 0; i < listeUrl.size(); i++) {
                int tag = image_grid.getChildCount() + 1;
                ads_Model carre = new ads_Model(BitmapFactory.decodeFile(listeUrl.get(i)), tag + "", "photo " + tag, false);
                objets.add(carre);
                image_grid.setVisibility(View.VISIBLE);
                gridViewSetting(image_grid);
                myadapter.notifyDataSetChanged();
                listeUrl.remove(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_ads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //noinspection SimplifiableIfStatement
            case android.R.id.home:
                onBackPressed();
            return true;
            case R.id.action_settings:
            return true;
            case R.id.action_add:
             selectImage();
             Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/select_image_button");
             return true;
            case R.id.action_validate:
            postAds();
            Krw_functions.pushButtonEvent(getApplicationContext(), Krw_functions.Current_country(getApplicationContext()) + "/ad_create_button");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void postAds() {
        DatabaseHelper mydb=new DatabaseHelper(getApplicationContext());
        Annonce ad=new Annonce();
        Model lepays = (Model) pays.getSelectedItem(),
              laville= (Model) Cities.getSelectedItem(),
              lacategorie= (Model) cat.getSelectedItem(),
              lasouscategorie= (Model) subcat.getSelectedItem(),
              lamonnaie= (Model) monn.getSelectedItem();
        String title=titre.getText().toString(),body=description.getText().toString();
        String nom = countryName(lepays.getTag(), getApplicationContext());
        int CountryID=Integer.valueOf(nom);
        int CityId= !laville.getTag().equals("") ? Integer.valueOf(laville.getTag()):0;
        int CategoryID=Integer.valueOf(lacategorie.getTag());
        SubCategoryID= !lasouscategorie.getTag().equals("") ?Integer.valueOf(lasouscategorie.getTag()):0;
        String price=prix.getText().toString();
        int nbimg= objets.size();
        String Monnaie=lamonnaie.getTag();

        ad.setTitle(title);
        ad.setDescription(body);
        ad.setCountry_id(CountryID);
        ad.setCity_ID(CityId);
        ad.setCategory_id(CategoryID);
        ad.setsubCategory_id(SubCategoryID);
        ad.setPrice(price);
        ad.setCurrency(Monnaie);
        ad.setNbpics(nbimg);
        ad.setDate(adsID+"");
        ad.setPhone1(phone1.getText().toString());//Variables_Session(getApplicationContext()).getUserPhones());
     //Show_Toast(getApplicationContext(),"cet annonce compte "+nbimg+" photos"+(nbimg>0?objets.get(nbimg-1).getTag():""),true);
       if(Krw_functions.add_ads_to_queue(ad,createAdsActivity.this)) {
           if (!isMyServiceRunning(PostAdsService.class,getApplication()))
           {
             startService(new Intent(getApplicationContext(), PostAdsService.class));
           }
           startActivity(new Intent(this,AdsListActivity.class));
           finish();
       }

    }

    private void selectImage() {
        final ArrayList<Model> items  = new ArrayList<>();
        items.add(new Model(R.drawable.camera, "0", "Depuis la Camera", false));
        items.add(new Model(R.drawable.gallery, "1", "Depuis la Gallerie", false));
        final AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle("Ajouter la photo");
        builder.setIcon(R.drawable.ic_dropdown);

        builder.setAdapter(new Spinner_adapter(this, R.layout.single_spinner,items), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    captureImage(".kerawa/tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Choisir la photo dans"), PICK_FROM_FILE);
                }
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Show_Toast(getApplicationContext(), RESULT_OK + "", false);
        if(resultCode == RESULT_OK) {
            if (requestCode == 1) {
                onCaptureImageResult(data,"camera");
            } else if (requestCode == 2) {
                onCaptureImageResult(data,"file");
            }
        }

    }

    private void onCaptureImageResult(Intent data,String source) {
        int tag;
        String imagename_;
        ads_Model carre;
        switch (source) {
            case "camera":
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), imageBitmap, imageTempName);
                picturePath = getRealPathFromURI(tempUri);
                listeUrl.add(picturePath);
                //Show_Toast(getApplicationContext(),picturePath,true);
                imagename_ = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                if (!ImageStorage.checkifImageExists(imagename_, adsID + "")) {
                    GetImages imagegeter = new GetImages("File://" + picturePath, imagename_, adsID + "", false);
                    imagegeter.ctx = getApplicationContext();
                    imagegeter.execute();
                }
                tag = image_grid.getChildCount() + 1;
                carre = new ads_Model(imageBitmap, tag + "", "photo " + tag, false);
                objets.add(carre);
                image_grid.setVisibility(View.VISIBLE);
                gridViewSetting(image_grid);
                myadapter.notifyDataSetChanged();
                break;
            case "file":
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
                listeUrl.add(picturePath);
                imagename_ = picturePath.substring(picturePath.lastIndexOf("/") + 1);
                if (!ImageStorage.checkifImageExists(imagename_, adsID + "")) {
                    GetImages imagegeter = new GetImages("File://" + picturePath, imagename_, adsID + "", false);
                    imagegeter.ctx = getApplicationContext();
                    imagegeter.execute();
                }
                Bitmap thumbnail = BitmapFactory.decodeFile(picturePath);
                //Show_Toast(getApplicationContext(),picturePath, true);
                tag = image_grid.getChildCount() + 1;
                carre = new ads_Model(thumbnail, tag + "", "photo " + tag, false);
                objets.add(carre);
                image_grid.setVisibility(View.VISIBLE);
                gridViewSetting(image_grid);
                myadapter.notifyDataSetChanged();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choisir la photo dans"), PICK_FROM_FILE);
                //viewImage.setImageBitmap(thumbnail);
                break;
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public void captureImage(String imageName) {
        imageTempName = imageName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                ".kerawa/tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri.fromFile(file);
        startActivityForResult(intent, PICK_FROM_CAMERA);

    }
    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse("https://www.preprod.kerawa.com/item/new"))
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

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void gridViewSetting(GridView gridview) {

        int size=objets.size();
        // Calculated single Item Layout Width for each grid element ....
        int width = 100;

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int totalWidth = (int) (width * size * density);
        int singleItemWidth = (int) (width * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        gridview.setLayoutParams(params);
        gridview.setColumnWidth(singleItemWidth);
        gridview.setHorizontalSpacing(2);
        gridview.setStretchMode(GridView.STRETCH_SPACING);
        gridview.setNumColumns(size);
    }

}
