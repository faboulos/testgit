package com.kerawa.app.utilities;

import android.graphics.Bitmap;

/**
 Created by kwemart on 10/05/2015.
 */
public class ads_Model {

    private Bitmap icon;
    private String title;
    private boolean isGroupHeader = false;
    private String tag;


    public ads_Model(Bitmap icon, String tag, String title, boolean isGroupHeader) {
        super();
        this.icon = icon;
        this.title = title;
        this.isGroupHeader = isGroupHeader;
        this.tag=tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap code) {
        this.icon = code;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String code) {
        this.tag = code;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }
//gettters & setters...
}