package com.example.android.demoapp;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YelpClass extends AsyncTask<Location,Void,String> {
    OAuthService service;
    Token accessToken;
    static String YELP_CONSUMER_KEY ="pQl7HlElM5rLg8hP0JLNdQ";
    static String YELP_CONSUMER_SECRET = "HoPvHqXRXr_qqLRC8tC-qO51Eds";
    static String YELP_TOKEN = "HpfjYt5AEe8byYhDVVtcWWmcQI3jRQBi";
    static String YELP_TOKEN_SECRET = "AnKSJuSxwger_GlEK4zfz5gvjTM";


    public static Yelp getYelp(Context context) {
        return new Yelp(YELP_CONSUMER_KEY, YELP_CONSUMER_SECRET,
                YELP_TOKEN, YELP_TOKEN_SECRET);
    }

    public YelpClass(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }
    @Override
    protected void onPostExecute(String strings) {
        //super.onPostExecute(strings);
    }

    @Override
    protected String doInBackground(Location... params) {
        Location location=params[0];
        String latitude=Double.toString(location.getLatitude());
        String longitude=Double.toString(location.getLongitude());
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", "food");
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("limit", "20");
        request.addQuerystringParameter("radius_filter", "1000");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody().toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
