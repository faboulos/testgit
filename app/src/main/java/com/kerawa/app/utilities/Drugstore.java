package com.kerawa.app.utilities;

import java.util.ArrayList;

/**
 Created by martin on 12/18/15.
 */
public class Drugstore {
    String location;
    String Name;
    String Region;
    ArrayList<String> phones;
    String Country;
    String city_name;

    public Drugstore(){}


    public ArrayList<String> getPhones() {
        return this.phones;
    }

    public String getLocation() {
        return this.location;
    }

    public String getCity_name() {
        return this.city_name;
    }

    public String getName() {
        return this.Name;
    }

    public String getRegion() {
        return this.Region;
    }

    public String getCountry_name() {
        return this.Country;
    }

    public void setRegion(String region) {
        this.Region = region;
    }

    public void setCity(String city) {
        this.city_name = city;
    }

    public void setStoreName(String storeName) {
        this.Name = storeName;
    }

    public void setLocalisation(String localisation) {
        this.location = localisation;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }
}
