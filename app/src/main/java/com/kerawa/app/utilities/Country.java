package com.kerawa.app.utilities;

/**
 * Created by boris on 8/10/2015.
 */
public class Country {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    private int id ;
    private String country_name ;
    private String country_id ;

    public Country(){}
    public Country(String c_name,String c_id){

        super();
        this.country_id = c_id;
        this.country_name = c_name ;
    }
}