package com.kerawa.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kerawa.app.utilities.Ad_details;
import com.kerawa.app.utilities.Annonce;
import com.kerawa.app.utilities.Drugstore;
import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.Model;
import com.kerawa.app.utilities.Person;
import com.kerawa.app.utilities.offline_ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.kerawa.app.utilities.Krw_functions.Show_Toast;

/**
 Created by boris on 8/10/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "KerawaDB";
    private static final String TABLE_OFFLINE_ADS ="ads_offline" ;
    private String TABLE_COUNTRY = "country";
    private String TABLE_CATEGORY = "category";
    private String TABLE_SUB_CATEGORY = "sub_category";
    private String TABLE_ADS = "ads";
    private String TABLE_ADS_DETAILS = "ads_details";
    private String KEY_ID = "id" ;
    public static final String Regions_TABLE_NAME = "Regions";
    public static final String Regions_COLUMN_ID = "id";
    public static final String Regions_COLUMN_NAME = "CountryName";
    public static final String Region_COLUMN_ISO = "isoCode";
    public static final String Region_COLUMN_ICON = "RegionFlag";
    //Categories table
    public static final String Categories_TABLE_NAME = "Category";
    public static final String Categories_COLUMN_ID = "CatId";
    public static final String Categories_COLUMN_NAME = "CatName";
    public static final String Categories_COLUMN_parentID = "CatParentId";

    //table personne  created by Marcelin
    public  static  final String  person_id = "id";
    public static   final String    person_imei = "imei";
    public static   final String    person_name="name";
    public static   final String    person_email="email";
    public static   final String    person_userPhone="phone_mobile";
    public static   final String    person_cityID="cityID";
    public static   final String    person_cityName="cityName";
    public static   final String    person_countryID="countryID";
    public static   final String    person_countryName="country";
    public static   final String    person_password="password";
    public static   final String    person_deviceKey="deviceKey";
    public static   final String    person_userID="userID";
    public static   final String    person_website="website";
    public static   final String    person_authorizationKey="authorizationKey";
    public static   final String    person_secretCode="secretCode";




    private HashMap hp;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s integer primary key, %s text,%s text,%s text)", Regions_TABLE_NAME, Regions_COLUMN_ID, Regions_COLUMN_NAME, Region_COLUMN_ISO, Region_COLUMN_ICON));
        //---create city
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s integer primary key, %s text,%s text)","Cities", "ID", "Name", "Region"));
        //--------------------------------------------------------------
        String CREATE_CATEGORY_TABLE = "CREATE TABLE category ( " +
                "id INTEGER PRIMARY KEY, "+
                "category_name TEXT, "+
                "parentID TEXT, "+
                "category_lang TEXT )";


        // create category table
        db.execSQL(CREATE_CATEGORY_TABLE);


        //--------------------------------------------------------------
        String CREATE_SUBCATEGORY_TABLE = "CREATE TABLE sub_category ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subcatname TEXT, "+
                "subcatid TEXT, "+
                "catid TEXT )";

        // create subcategory table
        db.execSQL(CREATE_SUBCATEGORY_TABLE);

        //--------------------------------------------------------------
        String CREATE_META_TABLE = "CREATE TABLE meta ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "meta_id TEXT, "+
                "name TEXT, "+
                "meta_slug TEXT, "+
                "meta_required TEXT, "+
                "category_id TEXT )";

        // create subcategory table
        db.execSQL(CREATE_META_TABLE);

        //--------------------------------------------------------------
        String CREATE_ADS_TABLE = "CREATE TABLE ads ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "add_id TEXT, "+
                "price TEXT, "+
                "description TEXT, "+
                "date TEXT, "+
                "category TEXT, "+
                "city_name TEXT, "+
                "image_url TEXT ,"+
                "phone1 TEXT ,"+
                "phone2 TEXT ,"+
                "friendly_url TEXT ,"+
                "currency TEXT )";


        // create Ads table
        db.execSQL(CREATE_ADS_TABLE);

        //--------------------------------------------------------------
        //--------------------------------------------------------------


        String CREATE_ADS_OFFLINE_TABLE = "CREATE TABLE ads_offline ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ad_id TEXT, "+
                "title TEXT, "+
                "nbpic TEXT, "+
                "date TEXT, "+
                "description TEXT, "+
                "category TEXT, "+
                "subcategory TEXT, "+
                "price TEXT DEFAULT 0, "+
                "currency TEXT, "+
                "phone1 TEXT, "+
                "phone2 TEXT, "+
                "email TEXT, "+
                "countryID TEXT, "+
                "cityID TEXT, "+
                "status TEXT )";

        // create Ads details table
        db.execSQL(CREATE_ADS_OFFLINE_TABLE);

        //--------------------------------------------------------------
        String CREATE_ADS_FILE_TABLE = "CREATE TABLE ads_files ( " +
                "fid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adID TEXT, "+
                "url TEXT )";

        // create Ads details table
        db.execSQL(CREATE_ADS_FILE_TABLE);

         //--------------------------------------------------------------
        String CREATE_DRUGSTORE_TABLE = "CREATE TABLE drugstore ( " +
                "drstoreId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, "+
                "Country TEXT, "+
                "Region TEXT, "+
                "City TEXT, "+
                "Location TEXT, "+
                "Phones TEXT )";

        // create Ads details table
        db.execSQL(CREATE_DRUGSTORE_TABLE);



        //create person caracteristics
        String CREATE_PERSON ="CREATE TABLE person("+
                "Id INTEGER PRIMARY KEY AUTOINCREMENT  , "+
                "imei TEXT, "+"name TEXT, "+"email TEXT, "+"phone_mobile TEXT, "+
                "cityID TEXT, "+"cityName TEXT, "+"countryID TEXT, "+"country TEXT, "+
                "password TEXT, "+"deviceKey, "+ "userID, "+"website, "+"authorizationKey, "+
                "secretCode) ";

        db.execSQL(CREATE_PERSON);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS country");
        db.execSQL("DROP TABLE IF EXISTS Cities");
        db.execSQL("DROP TABLE IF EXISTS category");
        db.execSQL("DROP TABLE IF EXISTS sub_category");
        db.execSQL("DROP TABLE IF EXISTS ads");
        db.execSQL("DROP TABLE IF EXISTS ads_details");
        db.execSQL("DROP TABLE IF EXISTS ads_offline");
        db.execSQL("DROP TABLE IF EXISTS ads_files");
        db.execSQL("DROP TABLE IF EXISTS meta");
        db.execSQL("DROP TABLE IF EXISTS drugstore");
        db.execSQL("DROP TABLE IF EXISTS person");

        // create fresh books table
        this.onCreate(db);
    }
    //------------ Working with countries --------------------------------------


    public boolean insertPerson(Person person)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Id",person.getId());  // put this id to one because, one user is using mobile
        contentValues.put("imei", person.getImei());
        contentValues.put("email", person.getEmail());
        contentValues.put("name", person.getName());
        contentValues.put("phone_mobile",person.getUserPhones());
        contentValues.put("cityID",person.getCityId());
        contentValues.put("cityName",person.getCity());
        contentValues.put("countryID", String.valueOf(person.getCountry()));
        contentValues.put("country", person.getCountryName());
        contentValues.put("password",person.getPass());
        contentValues.put("website", person.getWebsite());
        contentValues.put("deviceKey",person.getDeviceKey());
        contentValues.put("userID", person.getuserID());
        contentValues.put("authorizationKey",person.getAutorisationKey());
        contentValues.put("secretCode", person.getSecretCode());

        db.insert("person", null, contentValues);
        db.close();
        return true;
    }

    public long countPerson(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery="SELECT * FROM person";

        Cursor cursor = db.rawQuery(selectQuery, null);

        db.close();
        return cursor.getCount();

    }

public Person  getPerson(){
    SQLiteDatabase db = this.getReadableDatabase();
    Person person = new Person();
    String selectQuery="SELECT * FROM person";

    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.getCount()== 0) {
        return null;
    }
    if (cursor != null){
        cursor.moveToFirst();


       person.setId(cursor.getInt(0));
        person.SetImei(cursor.getString(1));
        person.SetName(cursor.getString(2));
        person.SetEmail(cursor.getString(3));
        person.SetUserPhones(cursor.getString(4));
        person.SetCityId(Integer.parseInt(cursor.getString(5)));
        person.SetCity(cursor.getString(6));
        person.SetCountry(Integer.parseInt(cursor.getString(7)));
        person.setCountryName(cursor.getString(8));
        person.SetPass(cursor.getString(9));
        person.setDeviceKey(cursor.getString(10));
        person.SetUserID(Integer.parseInt(cursor.getString(11)));
        person.setWebsite(cursor.getString(12));
        person.SetAutorisationKey(cursor.getString(13));
        person.SetSecretCode(cursor.getString(14));
        return person;
    }
    cursor.close();


 return null;

}

//------------------------------------------------------------------------------------------------------------------

//------------ Working with countries --------------------------------------


    public boolean insertCountry(Integer id, String name, String isoCode)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String icon="R.drawable."+isoCode.toLowerCase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Regions_COLUMN_ID, id);
        contentValues.put(Regions_COLUMN_NAME, name);
        contentValues.put(Region_COLUMN_ISO, isoCode.toLowerCase());
        contentValues.put(Region_COLUMN_ICON, icon);
        db.insert(Regions_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }


//------------ Working with drugstore --------------------------------------


    public boolean insertDrugStore(Drugstore drstore)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Country",drstore.getCountry_name());
        contentValues.put("Region", drstore.getRegion());
        contentValues.put("City", drstore.getCity_name());
        contentValues.put("Location",drstore.getLocation());
        contentValues.put("Phones",Krw_functions.join(drstore.getPhones(), ","));
        db.insert("drugstore", null, contentValues);
        db.close();

        return true;
    }

   public boolean reset_drugstores(){
       SQLiteDatabase db = this.getWritableDatabase();
       db.execSQL("DELETE FROM drugstore");
       db.close();
       return true;
   }

    public boolean reset_countries(){
       SQLiteDatabase db = this.getWritableDatabase();
       db.execSQL("DELETE FROM Regions");
       db.execSQL("DELETE FROM Cities");
        db.close();
       return true;

    }


    public ArrayList<Drugstore> getAlldrugStore(int page,int lim)
    {
        ArrayList<Drugstore> array_list = new ArrayList<>();
        String limit="Limit "+page+", "+(page*lim);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM drugstore ORDER BY Region "+limit, null);
        if(res!=null&&res.moveToFirst()) {
            while (!res.isAfterLast()) {
                Drugstore drs=new Drugstore();
                drs.setRegion(res.getString(3));
                drs.setCity(res.getString(4));
                drs.setLocalisation(res.getString(5));
                drs.setPhones(new ArrayList<String>(Arrays.asList(res.getString(6).split(","))));
                array_list.add(drs);
                res.moveToNext();
            }
        }
        db.close();
/*        if (res != null) {
            res.close();
        }*/
        return array_list;
    }

//-----------------------working with metas-----------------------------------------
public boolean insertMeta(int metaid ,String name ,String slug,int required, int categoryid)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("meta_id", metaid);
        contentValues.put("name",name);
        contentValues.put("meta_slug",slug);
        contentValues.put("meta_required",required+"");
        contentValues.put("category_id",categoryid);

        db.insert("meta", null, contentValues);
        db.close();
        return true;
    }


public ArrayList<Model> liste_meta(int catID, Context ctx){
    ArrayList<Model> lesMetas=new ArrayList<>();
    SQLiteDatabase db = this.getWritableDatabase();
   Cursor cur= db.rawQuery("SELECT * FROM meta WHERE category_id=" + catID + "", null);
   String toastText="";
    if(cur!=null&&cur.moveToFirst()) {

        while (!cur.isAfterLast()) {
            lesMetas.add(new Model(-1, cur.getString(3), cur.getString(2), false));
            toastText+=" | metaSlug:"+cur.getString(3)+" | meta:"+cur.getString(2)+" | meta_id:"+cur.getString(1)+" | id:"+cur.getString(0)+"\r\n";
            cur.moveToNext();
        }
        cur.close();
    }

    Show_Toast(ctx,toastText,true);
    db.close();
    return lesMetas;
}


//--------- Insert category in database ------------------------------------------------------------------
    public boolean insertCategory(Integer id, String name, String parentid, String lang)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id",id);
        contentValues.put("category_name", name);
        contentValues.put("parentID", parentid);
        contentValues.put("category_lang", lang);
        db.insert("category", null, contentValues);
        db.close();
        return true;
    }


 //----------- Insert City in database --------------------------------------------------------------------

    public boolean insertCity(Integer id, String name, String region)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID",id);
        contentValues.put("Name", name);
        contentValues.put("Region",region);
        db.insert("Cities", null, contentValues);
        Log.i("City", id + " => " + name + " => "+region);
        db.close();
        return true;
    }

//----- Getting a country from his ID -------------------------------------------------------------------------

    public Cursor getCountry(int CountryID){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "SELECT * FROM "+Regions_TABLE_NAME+" WHERE "+Regions_COLUMN_ID+"="+CountryID+"", null );
    }
    public Cursor getCity(int CityID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursi = db.rawQuery("SELECT * FROM " + "Cities" + " WHERE " + "ID" + "=" + CityID + "", null);
        db.close();
        return cursi;
    }

//----- Getting a country from iso -------------------------------------------------------------------------

    public Cursor getCountryFromISO(String CountryISO){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery(String.format("SELECT * FROM %s WHERE %s='%s'", Regions_TABLE_NAME, Region_COLUMN_ISO, CountryISO), null );
        if(cursor.moveToFirst()) {
            db.close();
            return cursor;
        }
        db.close();
        return null;
    }

//----- List inserted country ------------------------------------------------------------------------------

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int number_of_countries_in_database = (int) DatabaseUtils.queryNumEntries(db, Regions_TABLE_NAME);
        db.close();
        return number_of_countries_in_database;
    }

//------ Working with country "Updating"--------------------------------------------------------------------

    public boolean updateCountry (Integer CountryID, String CountryName, String CountryIso, String icon)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Regions_COLUMN_ID,CountryID);
        contentValues.put(Regions_COLUMN_NAME,CountryName);
        contentValues.put(Region_COLUMN_ISO,CountryIso);
        db.update(Regions_TABLE_NAME, contentValues, Regions_COLUMN_ID + " = ? ", new String[]{Integer.toString(CountryID)});
        return true;
    }

//------ Working with Cities  "Updating"--------------------------------------------------------------------


    public boolean updateCity (Integer id,String name,String country)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name",name);
        contentValues.put("Region",country);

        db.update("Cities", contentValues, "ID" + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

//------ Working with country "deleting"--------------------------------------------------------------------


    public Integer deleteCountry (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Regions_TABLE_NAME, Regions_COLUMN_ID+"= ? ", new String[] { id.toString() });
    }
    public Integer deleteCity (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Cities", "ID"+"= ? ", new String[] { id.toString() });
    }

//------ Working with country "listing"--------------------------------------------------------------------


    public HashMap<Integer,String> getAllCountries()
    {
        HashMap<Integer,String> array_list = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + Regions_TABLE_NAME + " ORDER BY " + Regions_COLUMN_NAME, null );
        if(res!=null&&res.moveToFirst()) {

            while (!res.isAfterLast()) {
                array_list.put(res.getInt(0), res.getString(1));
                res.moveToNext();
            }
        }
        if (res != null) {
            res.close();
        }
        db.close();
        return array_list;
    }



//get cities from current country

    public HashMap<Integer,String> getAllCities(Context ctx)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String nm = prefs.getString("Kerawa-count", null);
        final String nom = Krw_functions.countryName(nm, ctx);
        HashMap<Integer,String> array_list = new HashMap<Integer,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + "Cities" + " WHERE Region=\"" + nom + "\" ORDER BY " + "Name ASC", null );
        if(res!=null&&res.moveToFirst()) {

            while (!res.isAfterLast()) {
                array_list.put(res.getInt(0), res.getString(1));
                res.moveToNext();
            }
        }
        db.close();
        return array_list;
    }



    //get cities from Country ID

    public HashMap<Integer,String> getAllCities_from_countryID(int CountryID)
    {
        String nom=CountryID+"";
        HashMap<Integer,String> array_list = new HashMap<Integer,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM "+"Cities"+" WHERE Region=\""+nom+"\" ORDER BY "+"Name ASC", null );
        if(res!=null&&res.moveToFirst()) {

            while (!res.isAfterLast()) {
                array_list.put(res.getInt(0), res.getString(1));
                res.moveToNext();
            }
         res.close();
        }

        db.close();
        return array_list;
    }



 //-------- generating queue list -------------------------

    public int get_onQueue_Ads() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_OFFLINE_ADS);
    }

//-----------------------------------------------------

  /*  public List<Annonce> getAllAds() {
        List<Annonce> ads = new LinkedList<Annonce>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ADS ;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Annonce ad = null;
        if (cursor.moveToFirst()) {
            do {
                ad = new Annonce();
                ad.setAdId(Integer.parseInt(cursor.getString(0)));
                ad.setAdd_id(cursor.getString(1));
                ad.setTitle(cursor.getString(2));
                ad.setAdd_id(cursor.getString(3));
                ad.setPrice(cursor.getString(4));
                ad.setDate(cursor.getString(5));
                ad.setCity_name(cursor.getString(6));
                ad.setImage_thumbnail_url(cursor.getString(7));

                // Add book to books
                ads.add(ad);
            } while (cursor.moveToNext());
        }

        Log.d("getAllAds()", ads.toString());

        // return books
        return ads;
    }*/

    public List<offline_ad> getAllOfflineAds() {
        ArrayList<offline_ad> ads = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM ads_offline where status = 'false' and ad_id=''" ;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        offline_ad ad = null;
        if (cursor.moveToFirst()) {
            do {
                ad = new offline_ad();
                ad.setId(cursor.getInt(0));
                ad.setAd_id(cursor.getString(1));
                ad.setTitle(cursor.getString(2));
                ad.setNbpic(cursor.getString(3));
                ad.setDate(cursor.getString(4));
                ad.setDescription(cursor.getString(5));
                ad.setCategory(cursor.getString(6));
                ad.setSubcategory(cursor.getString(7));
                ad.setPrice(cursor.getString(8));
                ad.setCurrency(cursor.getString(9));
                ad.setPhone1(cursor.getString(10));
                ad.setPhone2(cursor.getString(11));
                ad.setEmail(cursor.getString(12));
                ad.setCountryId(cursor.getString(13));
                ad.setCityId(cursor.getString(14));
                ad.setStatus(cursor.getString(15));
                ads.add(ad);
            } while (cursor.moveToNext());
        }

        Log.d("getAllofflineAds()", ads.toArray().toString());
        cursor.close();
        db.close();

        // return books
        return ads;
    }

    public List<Annonce> getAllAdsFromCategory(String category_name) {
        List<Annonce> ads = new LinkedList<Annonce>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ADS +" WHERE category = '" + category_name +"'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Annonce ad = null;
        if (cursor.moveToFirst()) {
            do {
                ad = new Annonce();
                ad.setAdId(Integer.parseInt(cursor.getString(0)));
                ad.setTitle(cursor.getString(1));
                ad.setAdd_id(cursor.getString(2));
                ad.setPrice(cursor.getString(3));
                ad.setDescription(cursor.getString(4));
                ad.setDate(cursor.getString(5));
                ad.setCategory_name(cursor.getString(6));
                ad.setCity_name(cursor.getString(7));
                ad.setImage_thumbnail_url(cursor.getString(8));
                ad.setPhone1(cursor.getString(9));
                ad.setPhone2(cursor.getString(10));
                ad.setFriendly_url(cursor.getString(11));

                // Add book to books
                ads.add(ad);
            } while (cursor.moveToNext());
        }

        Log.d("getAllAdsFromCategory()", ads.toString());
        db.close();
        cursor.close();
        // return books
        return ads;
    }

    // Updating single book
    public int updateAd(Annonce an) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", an.getTitle()); // get title
        values.put("add_id", an.getAdd_id()); // get title
        values.put("price", an.getPrice()); // get title
        values.put("date", an.getDate()); // get title
        values.put("city_name", an.getCity_name()); // get title
        values.put("image_thumbnail_url", an.getImage_thumbnail_url()); // get title
        // get author

        // 3. updating row
        int i = db.update(TABLE_ADS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(an.getAdd_id()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    public int updateAdDescription(String ad_id,String description) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("description", description); // get title

        // get author

        // 3. updating row

        int i = db.update(TABLE_ADS, //table
                values, // column/value
                "ad_id= ?", // selections
                new String[] {ad_id}); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void deleteAD(Annonce an) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ADS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(an.getAdd_id())});

        // 3. close
        db.close();

        Log.d("deleteAd", an.toString());

    }
    public void deleteAllADsFromCategory(String category) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete

        db.delete(TABLE_ADS,
                "category = ?",
                new String[]{category});

        // 3. close
        db.close();



    }
    public void deleteAllADs() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.execSQL("delete from " + TABLE_ADS);

        // 3. close
        db.close();
     }
    public void deleteAllMetas() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.execSQL("delete from meta");

        // 3. close
        db.close();
     }
    public String getMetaFromDB(String rawName) {

       String res = "";
        // 1. build the query
        String query = "SELECT  * FROM meta WHERE meta_slug = '" + rawName +"'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Annonce ad = null;
        if (cursor.moveToFirst()) {
                res = cursor.getString(0);
        }

        Log.d("getmetafromrawName", res);
        db.close();
        cursor.close();
        // return books

    return res;
    }
    //-------------------- Working with ads details ------------------------------


    public Ad_details getAd_details(int ad_id) {

        Ad_details adDetails = new Ad_details();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ADS + " WHERE ad_id = "+ad_id;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Ad_details ad = null;
        if (cursor!=null&&cursor.moveToFirst()) {
            do {
                ad = new Ad_details();
                ad.setId(cursor.getInt(0));
                ad.setAd_id(cursor.getString(1));
                ad.setTitle(cursor.getString(2));
                ad.setDate(cursor.getString(3));
                ad.setCategory(cursor.getString(4));
                ad.setPrice(cursor.getString(5));
                ad.setDetails(cursor.getString(6));
                ad.setPhone1(cursor.getString(7));
                ad.setPhone1(cursor.getString(8));
                ad.setPhone2(cursor.getString(9));
                ad.setEmail(cursor.getString(10));
                ad.setImag1(cursor.getString(11));
                ad.setImag2(cursor.getString(12));
                ad.setImag3(cursor.getString(13));
                ad.setImag4(cursor.getString(14));

                adDetails = ad ;

            } while (cursor.moveToNext());
        }
        db.close();
        Log.d("getAd_detail()", adDetails.toString());

        // return books
        return adDetails;
    }



    // Deleting single ad detail
    public void deleteBook(Ad_details adDetails) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_ADS_DETAILS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(adDetails.getId())});

        // 3. close
        db.close();

        Log.d("deletead detail", adDetails.toString());

    }

    //============== insertions dans la base de donnees ==============================
/*
    public  void insertCountry(country country)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("country_name", country.getCountry_name());
        values.put("country_id",country.getCountry_id());
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_COUNTRY,
                null,
                values);
    }
    public  void insertCategory(category category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name",category.getCategory_name());
        values.put("category_id",category.getCategory_id());
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_CATEGORY,
                null,
                values);
    }
    public  void insertsubCategory(subCategory subCategory)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("subcatname", subCategory.getSubcategory_name());
        values.put("subcatid",subCategory.getSubcategory_id());
        values.put("catid",subCategory.getCategory_id());
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_SUB_CATEGORY,
                null,
                values);
    }

    public  void insertAdBlock(ArrayList<Annonce> ads)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int len = ads.size();
        for (int i=0;i<=len-1;i++) {

            Annonce ad = ads.get(i) ;
            values.put("title", ad.getTitle());
            values.put("add_id", ad.getAdd_id());
            values.put("price", ad.getPrice());
            values.put("date", ad.getDate());
            values.put("city_name", ad.getCity_name());
            values.put("image_url", ad.getImage_thumbnail_url());
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    TABLE_ADS,
                    null,
                    values);
        }
    }
    public  void insertAD_Details(Ad_details adDetails)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ad_id", adDetails.getAd_id());
        values.put("title", adDetails.getTitle());
        values.put("date",adDetails.getDate());
        values.put("category",adDetails.getCategory());
        values.put("price", adDetails.getPrice());
        values.put("details", adDetails.getDetails());
        values.put("phone1", adDetails.getPhone1());
        values.put("phone2", adDetails.getPhone2());
        values.put("email", adDetails.getEmail());
        values.put("img1", adDetails.getImag1());
        values.put("img2", adDetails.getImag2());
        values.put("img3", adDetails.getImag3());
        values.put("img4", adDetails.getImag4());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_ADS_DETAILS,
                null,
                values);
    }
*/
    public  void insertAd(Annonce ad)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", ad.getTitle());
        values.put("add_id", ad.getAdd_id());
        values.put("price", ad.getPrice());
        values.put("description", ad.getDescription());
        values.put("category", ad.getCategory_name());
        values.put("date", ad.getDate());
        values.put("city_name", ad.getCity_name());
        values.put("image_url", ad.getImage_thumbnail_url());
        values.put("phone1", ad.getPhone1());
        values.put("phone2", ad.getPhone2());
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                TABLE_ADS,
                null,
                values);
        db.close();
    }
    public HashMap<Integer, String> getAllCategories(String CatID) {
        HashMap<Integer,String> array_list = new HashMap<Integer,String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM "+"category"+" WHERE parentID=\""+CatID+"\" ORDER BY "+"category_name", null );
        if(res!=null) {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                array_list.put(res.getInt(0), res.getString(1));
                res.moveToNext();
            }
        }
        db.close();
        return array_list;
    }

    public boolean updateofflineAdStatus(int id, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status",status);
        db.update("ads_offline", contentValues, "id" + " = ? ", new String[]{String.valueOf(id)});
        db.close();
        return true;

    }

public boolean updateofflineAdId(int id, String adID) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ad_id",adID);
        db.update("ads_offline", contentValues, "id" + " = ? ", new String[]{String.valueOf(id)});
        db.close();
    return true;

    }

    public boolean insertOfflineAd(offline_ad ad) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ad_id",ad.getAd_id());
        contentValues.put("title", ad.getTitle());
        contentValues.put("nbpic", ad.getNbpic());
        contentValues.put("date", ad.getDate());
        contentValues.put("description",ad.getDescription());
        contentValues.put("category",ad.getCategory());
        contentValues.put("subcategory",ad.getSubcategory());
        contentValues.put("price", ad.getPrice());
        contentValues.put("currency",ad.getCurrency());
        contentValues.put("phone1",ad.getPhone1());
        contentValues.put("phone2",ad.getPhone2());
        contentValues.put("email", ad.getEmail());
        contentValues.put("countryID",ad.getCountryId());
        contentValues.put("cityID",ad.getCityId());
        contentValues.put("status",ad.getStatus());

        db.insert("ads_offline", null, contentValues);
        db.close();
        return true;

    }

    public int db_save_ads(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        int response= (int) db.insert("ads_offline", null, contentValues);
        return response;
    }

    public List<offline_ad> getIncompletePublishedAds() {

        ArrayList<offline_ad> ads = new ArrayList<>();

        // 1. build the query
        String query = "SELECT  * FROM ads_offline where status = 'false' and ad_id !=''" ;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        offline_ad ad = null;
        if (cursor.moveToFirst()) {
            do {
                ad = new offline_ad();
                ad.setId(cursor.getInt(0));
                ad.setAd_id(cursor.getString(1));
                ad.setTitle(cursor.getString(2));
                ad.setNbpic(cursor.getString(3));
                ad.setDate(cursor.getString(4));
                ad.setDescription(cursor.getString(5));
                ad.setCategory(cursor.getString(6));
                ad.setSubcategory(cursor.getString(7));
                ad.setPrice(cursor.getString(8));
                ad.setCurrency(cursor.getString(9));
                ad.setPhone1(cursor.getString(10));
                ad.setPhone2(cursor.getString(11));
                ad.setEmail(cursor.getString(12));
                ad.setCountryId(cursor.getString(13));
                ad.setCityId(cursor.getString(14));
                ad.setStatus(cursor.getString(15));
                ads.add(ad);
            } while (cursor.moveToNext());
        }

        Log.d("getAllofflineAds()", ads.toArray().toString());
        cursor.close();
        db.close();

        // return books
        return ads;
    }

    //this method return subcategory depending on the given category

    public HashMap<Integer,String> getSubCategoryfromCategory(String categorie) {


        HashMap<Integer,String> array_list = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM category  WHERE parentID="+categorie, null );
        if(res != null && res.moveToFirst()) {

            while (!res.isAfterLast()) {
                array_list.put(res.getInt(0), res.getString(1));
                Log.d("ID SubCatMethod", res.getInt(0)+" " + res.getString(1));
                res.moveToNext();
              //  Log.d("ID SubCatMethod", res.getInt(0)+" " + res.getString(1));
            }
            res.close();
        }

        db.close();
        return array_list;



    }




}