package com.example.android.demoapp;

import android.content.Context;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


public class Yelp {

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

    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public String search(String term, String location) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public String search(String term, String lat, String lon) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", lat+","+lon);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }


}