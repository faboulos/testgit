package com.kerawa.app.utilities;

import java.util.Comparator;

/**
 * Created by kwemart on 10/05/2015.
 */
public class Model {

    private int icon;
    private String title;
    public boolean isGroupHeader = false;
    private String tag;

    public Model() {
        super();
        this.isGroupHeader=false;
    }
    public Model(int icon, String tag, String title, boolean isGroupHeader) {
        super();
        this.icon = icon;
        this.title = title;
        this.isGroupHeader = isGroupHeader;
        this.tag=tag;
    }



    public static Comparator<Model> TitleComparator = new Comparator<Model>() {


        public int compare(Model s1, Model s2) {
            String StudentName1 = s1.getTitle().toUpperCase();
            String StudentName2 = s2.getTitle().toUpperCase();

            //ascending order
            return StudentName1.compareTo(StudentName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int code) {
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