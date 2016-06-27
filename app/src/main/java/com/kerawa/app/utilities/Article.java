package com.kerawa.app.utilities;

/**
  Created by Fifty on 2015-07-11.
 */
public class Article {

    private int artID;
    private String title;
    private String date;
    private String category_name;
    private String Author_name;
    private String image_thumbnail_url;
    private String art_URL;
    private String previous;
    private String next;
    private String Art_description;
    private int CategoryId;
    private String art_plain_description;
    private String image;

    public Article() {

    }

    public int getArtID() {
        return artID;
    }

    public void setArtID(int artID) {
        this.artID = artID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getAuthor_name() {
        return Author_name;
    }

    public void setAuthor_name(String author_name) {
        this.Author_name = author_name;
    }

    public String getImage_thumbnail_url() {
        return image_thumbnail_url;
    }

    public void setImage_thumbnail_url(String image_thumbnail_url) {
        this.image_thumbnail_url = image_thumbnail_url;
    }
    public String getArt_url() {
        return art_URL;
    }
    public void setArt_url(String str) {
        this.art_URL = str;
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

    public Integer getCategoryId(){
        return this.CategoryId;
    }

    public void setCategoryId(int cat) {
        this.CategoryId=cat;
    }

    public String getArt_description() {
        return this.Art_description;
    }

    public void setArt_description(String s) {
        this.Art_description =s;
    }

    public void setArt_plain_description(String str) {
        this.art_plain_description = str.replaceAll("<(.|\n)*?>", "");
    }
    public String getArt_plain_description() {
       return  this.art_plain_description ;
    }

    public void setLargeImage(String image) {
        this.image = image;
    }
    public String getLargeImage() {
      return  this.image;
    }
}
