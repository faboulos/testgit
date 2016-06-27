package com.kerawa.app.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

/**
 Created by kwemart on 02/09/2015.
 */
public class AndroideDevice {

    public Context ctx;
    public String imei;
    public String Operator;
    public String country;
    public String phone_number;
    public String indicatif;
    public TelephonyManager tm;
    private ArrayList<String> emailsList;

    public AndroideDevice(Context ctx){
        this.ctx=ctx;
        this.emailsList=Krw_functions.getemailsList(ctx);
        this.tm= (TelephonyManager) this.ctx.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getImei(){
        WifiManager manager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
//        Show_Toast(ctx,address,true);
        return (!(tm.getDeviceId() == null) && !tm.getDeviceId().equals("")) ? tm.getDeviceId() : address.replace(":","");
    }
    public void setImei(String imei){
        this.imei=imei;
    }
    public String getOperator(){
        return tm.getSimOperatorName();
    }
    public void setOperator(String operator){
        this.Operator=operator;
    }
    public void setCountry(String country){
        this.country=country;
    }
    public String getCountry(){
        return tm.getNetworkCountryIso();
    }
    public void setPhone_number(String str){
        this.phone_number=str;
    }
    public String getPhone_number(){
        return tm.getLine1Number();
    }


    public ArrayList<String> getEmailsList()
    {
        return this.emailsList;
    }
}
