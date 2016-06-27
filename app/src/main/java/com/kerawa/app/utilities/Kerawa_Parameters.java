package com.kerawa.app.utilities;

import android.content.Context;

import com.kerawa.app.R;

/**
 Created by kwemart on 26/08/2015.
 */
public class Kerawa_Parameters {

    //public static String PreProdURL="https://api.kerawa.com/v3/";

    //public static String PreProdURL="http://vps241072.ovh.net/v3.2.1/";
    //public static String PreProdURL="https://api.kerawa.com/v3/";

    // configuration de l'URL pour tous les tests
    public static String PreProdURL="http://vps241072.ovh.net/v3.2.1/";


    public static String VERSION="2.0.0";
    public static String Kerawa_API_KEY="7f60cf4cd8ed85PVCDMU8OmkRC1BYm0efbc2d5";
    public static String PreProdaccess23="b8538b3ff57f60cf4cd8ed0efbc2d589c8b72c52";
    public static String BlogBaseUrl="http://kerawa.com/blog/api/";
    public static String packageName = "com.kerawa.app";
    public Context ctx;

    public static String[] DownloadHours=new String[12],DrugstoreHours={"08:30","09:30","10:30"},ContactSendHours={"00:45","01:45","02:45","03:45","06:45","07:45","09:45"};

    public Kerawa_Parameters(Context _ctx){
        this.ctx=_ctx;
    }

    public String Hide_meta_data(){
        String YES = null;
     try{
         YES =ctx.getString(R.string.yes);
     }catch (Exception ignored){}
        return YES;
    }


}
