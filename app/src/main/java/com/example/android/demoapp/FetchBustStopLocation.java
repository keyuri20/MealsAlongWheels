package com.example.android.demoapp;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchBustStopLocation extends AsyncTask<String,Void,Location> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Location location) {
        super.onPostExecute(location);
    }

    @Override
    protected Location doInBackground(String... params) {
        String busNumber=params[0];
        String agency=params[1];
        String busStopId=params[2];
        try
        {
            String newUrl = "http://restbus.info/api/agencies/"+agency+"/routes/"+busNumber;
            URL url = new URL(newUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            String stopJsonStr = buffer.toString();
            JSONObject stopsObj=new JSONObject(stopJsonStr);
            JSONArray stopsArray=stopsObj.getJSONArray("stops");
            for(int j=0;j<stopsArray.length();j++)
            {
                JSONObject stop=(JSONObject)stopsArray.get(j);
                String newStopId=stop.get("id").toString();
                if(newStopId.equals(busStopId))
                {
                    Log.v("stopnew",newStopId);
                    Log.v("stopold",busStopId);
                    String latitude=Double.toString(stop.getDouble("lat"));
                    String longitude=Double.toString(stop.getDouble("lon"));
                    Location location=new Location("");
                    location.setLatitude(Double.parseDouble(latitude));
                    location.setLongitude(Double.parseDouble(longitude));
                    return location;
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
