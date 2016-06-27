package com.kerawa.app.utilities;

public class Person {



    String imei;
    String name;
    String email;
    String UserPhone;
    int CityID;
    Integer CountryID;


// add by Marcelin to use it into database
    int id;
    String countryName;
    String deviceKey;
//============================================

    String password;


    String username;
    String CityName;
    Integer userID;



    String Website;
    String userType;
    private String AuthorisationKey;
    private String SecretCode;


    public  Person(){}

    /*public Person( int id, String name, String email,String imei, String userPhone,int cityID, String cityName,
                   Integer countryID, String countryName,String deviceKey,String password,Integer userID,
                   String website,String authorisationKey, String secretCode) {
        this.imei = imei;
        this.name = name;
        this.email = email;
        UserPhone = userPhone;
        CityID = cityID;
        CountryID = countryID;
        this.id = id;
        this.countryName = countryName;
        this.deviceKey = deviceKey;
        this.password = password;
        CityName = cityName;
        this.userID = userID;
        Website = website;
        AuthorisationKey = authorisationKey;
        SecretCode = secretCode;
    }
*/
    public String getName(){return this.name;}
    public void SetName(String str){this.name=str;}
    public String getEmail(){return this.email;}
    public void SetEmail(String str){this.email=str;}
    public String getImei(){return this.imei;}
    public void SetImei(String str){this.imei=str;}
    public String getPass(){return this.password;}
    public void SetPass(String str){this.password=str;}
    public Integer getCountry(){return this.CountryID;}
    public void SetCountry(Integer str){this.CountryID=str;}
    public String getCity(){return this.CityName;}
    public void SetCity(String str){this.CityName=str;}
    public String getUsername(){return this.username;}
    public void SetUsername(String str){this.username=str;}
    public String getUserPhones(){return this.UserPhone;}
    public void SetUserPhones(String str){this.UserPhone=str;}
    public Integer getuserID(){return this.userID;}
    public void SetUserID(Integer str){this.userID=str;}
    public void SetAutorisationKey(String authorisationKey) { this.AuthorisationKey =authorisationKey; }
    public String getAutorisationKey() { return this.AuthorisationKey; }
    public void SetCityId(int CityId) {this.CityID=CityId;}
    public int getCityId() {return this.CityID;}
    public void SetSecretCode(String str){this.SecretCode=str;}
    public String getSecretCode() { return this.SecretCode;    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

}
