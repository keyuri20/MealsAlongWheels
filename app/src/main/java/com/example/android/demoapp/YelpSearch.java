package com.example.android.demoapp;


import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class YelpSearch extends ListActivity implements Serializable {

    @SuppressWarnings("serial")
    class Business implements Serializable {
        final String name;
        final String url;
        final String id;
        final String rating;
        final String category;


        public Business(String name, String url, String id, String rating,String categories) {
            this.name = name;
            this.url = url;
            this.id = id;
            this.rating = rating;
            this.category = categories;
        }

        @Override
        public String toString() {
            return "NAME :"+name +"  "+"ID : "+id;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Finding Tacos...");
        setProgressBarIndeterminateVisibility(true);

        new AsyncTask<Void, Void, List<Business>>() {
            @Override
            protected List<Business> doInBackground(Void... params) {

                String businesses = Yelp.getYelp(YelpSearch.this).search("food", "91754");
                try {
                    Log.v("TAG","try");
                    return processJson(businesses);
                } catch (JSONException e) {
                    return Collections.<Business>emptyList();
                }
            }

            @Override
            protected void onPostExecute(List<Business> businesses) {
                Log.v("TAG","onPostExecute");
                Log.v("BusinessesList","Businesses "+ businesses);
                //setTitle("Tacos Found");
                setProgressBarIndeterminateVisibility(false);
                getListView().setAdapter(new ArrayAdapter<Business>(YelpSearch.this, android.R.layout.simple_list_item_1, businesses));

            }
        }.execute();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Business biz = (Business) listView.getItemAtPosition(position);
    }

    List<Business> processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray businesses = json.getJSONArray("businesses");

        ArrayList<Business> businessObjs = new ArrayList<Business>(businesses.length());

        for (int i = 0; i < businesses.length(); i++) {
            JSONObject business = businesses.getJSONObject(i);
            businessObjs.add(new Business(business.optString("name"), business.optString("mobile_url"),business.optString("id"),
                    business.optString("rating"),business.optString("categories")));
        }
        return businessObjs;
    }
}
