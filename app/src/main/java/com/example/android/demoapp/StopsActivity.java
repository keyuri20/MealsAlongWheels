package com.example.android.demoapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }
    }


    public static class DetailActivityFragment extends android.support.v4.app.Fragment {

        public int busId;
        public String agency;
        public ArrayAdapter<String> adapter;
        public List<String> stops=new ArrayList<String>();
        public List<StopData> stopdata = new ArrayList<StopData>();

        public Map<String,Location> myMap = new HashMap<String,Location>();


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            List<String> arr=new ArrayList<String>();
            adapter = new ArrayAdapter<String>(getActivity(),R.layout.busstopnameview,R.id.stop_name, arr);

            View rootView = inflater.inflate(R.layout.fragment_stops, container, false);

            ListView listView = (ListView)rootView.findViewById(R.id.listBusStops);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String title = parent.getItemAtPosition(position).toString();
                    if(myMap.containsKey(title))
                    {
                        Location loc = myMap.get(title);
                        Intent myIntent = new Intent(getActivity(),MapsActivity.class);
                        myIntent.putExtra("Loc_Lat", String.valueOf(loc.getLatitude()));
                        myIntent.putExtra("Loc_Long", String.valueOf(loc.getLongitude()));
                        startActivity(myIntent);
                    }
                }
            });

            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            Intent intent = getActivity().getIntent();
            busId= Integer.parseInt(intent.getExtras().getString("BusId"));
            agency=intent.getExtras().getString("Agency");
            getAllStops stops=new getAllStops();
            stops.execute(Integer.toString(busId),agency);

            return rootView;
        }

        class getAllStops extends AsyncTask<String, Void,List<String>>{

            @Override
            protected void onPostExecute(List<String> s) {
                adapter.clear();
                adapter.addAll(s);
            }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<String> doInBackground(String... params) {
                String bus=params[0];
                String agency=params[1];
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String busStopsJson = null;

                try {

                    String stopUrl = "http://restbus.info/api/agencies/"+agency+"/routes/"+bus;
                    URL url = new URL(stopUrl);

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();


                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

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

                    busStopsJson = buffer.toString();
                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getBusListFromJson(busStopsJson);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            public List<String> getBusListFromJson(String JsonString)
            {
                try
                {
                    JSONObject obj=(JSONObject)new JSONObject(JsonString);
                    JSONArray st=obj.getJSONArray("stops");
                    for(int i=0;i<st.length();i++)
                    {
                        JSONObject row= (JSONObject) st.get(i);
                        String id=row.getString("id");
                        String title=row.getString("title");
                        Double latitude= row.getDouble("lat");
                        Double longitude=row.getDouble("lon");
                        stops.add(title);
                        Location loc = new Location("");
                        loc.setLatitude(latitude);
                        loc.setLongitude(longitude);
                        stopdata.add(new StopData(title, loc));
                        myMap.put(title, loc);
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }
                return stops;
            }
        }

        public class StopData {
            String stop_name;
            Location location;

            public StopData(String stop_name, Location location) {
                this.stop_name = stop_name;
                this.location = location;
            }

            @Override
            public String toString() {
                return stop_name;
            }
        }
    }
}

