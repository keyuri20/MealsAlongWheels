package com.example.android.demoapp;

public class Stops {
    public String id;
    public String title;
    public String latitude;
    public String longitude;

    public Stops(String id, String title, String latitude, String longitude ) {
        this.id = id;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
