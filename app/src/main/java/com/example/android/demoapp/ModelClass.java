package com.example.android.demoapp;

import java.io.Serializable;

public class ModelClass implements Serializable {

    public String url;
    public String id;
    public String rating;
    public String ratingURL;
    public String mobileURL;
    public String category;
    public String name;
    String phone;
    public double latitude;
    public double longitude;
    public ModelClass(){}

    public ModelClass(String name, String url, String id, String rating,String ratingURL,String mobileURL,String categories,String phone, double latitude,double longitude)
    {
        this.name=name;
        this.url=url;
        this.id=id;
        this.rating=rating;
        this.ratingURL=ratingURL;
        this.mobileURL=mobileURL;
        this.category=categories;
        this.latitude=latitude;
        this.longitude=longitude;
        this.phone= phone;

    }
    public String getName(){return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getRatingURL() {
        return ratingURL;
    }

    public void setRatingURL(String ratingURL) {
        this.ratingURL = ratingURL;
    }

    public Double getLatitude(){return latitude;}
    public Double getLongitude(){return longitude;}

    public String getMobileURL() {
        return mobileURL;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMobileURL(String mobileURL) {
        this.mobileURL = mobileURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}