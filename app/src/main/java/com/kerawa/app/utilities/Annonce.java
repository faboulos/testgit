package com.kerawa.app.utilities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 Created by Fifty on 2015-07-11.
 */
public class Annonce implements Serializable {


    private int countryID;
    private int cityID;
    private ArrayList<String> imgList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int adid ;
    private String add_id;
    private String User_id;

    public int getAdId() {
        return id;
    }

    public void setAdId(int id) {
        this.id = id;
    }
    private int nbpics;
    private int subCategory_id;
    private int id;
    private ArrayList<String> vipCount;

    public HashMap<String,String> getMetas() {
        return metas;
    }

    public void setMetas(HashMap<String,String> metas) {
        this.metas = metas;
    }

    private HashMap<String,String> metas ;
    private String title;
    private String price;
    private String ehanced;
    private String currency;
    private String date;
    private String country_name;
    private String city_name;
    private String category_name;
    private String subcategory_name;
    private String image_thumbnail_url;
    private String ad_url;
    private int Category_id;
    private String description;
    private String previous;
    private String next;
    private String phone1 = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status = "";
    private String phone2 = "";

    public String getFriendly_url() {
        return friendly_url;
    }

    public void setFriendly_url(String friendly_url) {
        this.friendly_url = friendly_url;
    }

    private String friendly_url = "";


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email = "";

    public Annonce() {

    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdd_id() {
        return add_id;
    }

    public void setAdd_id(String add_id) {
        this.add_id = add_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEhanced() {
        return ehanced;
    }

    public void setEhanced(String ehanced) {
        this.ehanced = ehanced;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getImage_thumbnail_url() {
        return image_thumbnail_url;
    }

    public void setImage_thumbnail_url(String image_thumbnail_url) {
        this.image_thumbnail_url = image_thumbnail_url;
    }

    public String getAd_url() {
        return ad_url;
    }

    public void setAd_url(String ad_url) {
        this.ad_url = ad_url;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public int getNbpics() {
        return this.nbpics;
    }

    public void setNbpics(int nbpics) {
         this.nbpics=nbpics;
    }

    public int getCategory_id() {
        return this.Category_id;
    }

    public void setCategory_id(int catid) {
         this.Category_id=catid;
    }

    public int getsubCategory_id() {
        return this.subCategory_id;
    }

    public void setsubCategory_id(int subid) {
       this.subCategory_id=subid;
    }

    public int getCountry_id() {
        return this.countryID;
    }

    public void setCountry_id(int CountryID) {
        this.countryID=CountryID;
    }

    public int getCity_ID() {
        return this.cityID;
    }

    public void setCity_ID(int cid) {
        this.cityID=cid;
    }


    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public ArrayList<String> getImgList() {
        return  this.imgList;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }


    public ArrayList<String> getVipCount() {
        return vipCount;
    }

    public void setVipCount(ArrayList<String> vipCount) {
        this.vipCount = vipCount;
    }
}
