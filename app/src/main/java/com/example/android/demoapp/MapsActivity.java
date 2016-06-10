package com.example.android.demoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnInfoWindowClickListener {
    public static final String LOG_TAG = "DATA";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private List<Bus> myBuses = new ArrayList<Bus>();
    ArrayAdapter<Bus> adapter;
    List<ModelClass> modelclassList;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ConnectivityManager cm=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile=cm.getActiveNetworkInfo();
        if(mobile==null)
        {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            MapsActivity.this.finish();
            System.exit(0);
        }
        else
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mainpage);
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }

    private void populateListView(final List<Bus> data) {
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //search();
                Location stopLoc = new Location("");
                FetchBustStopLocation stopLocation = new FetchBustStopLocation();
                String bno = data.get(position).getBusNumber();
                String agencyName = data.get(position).getAgency();
                String stopId = data.get(position).getBusStopId();
                stopLocation.execute(bno, agencyName, stopId);
                Bus newBus = myBuses.get(position);
                try
                {
                    stopLoc = stopLocation.get();
                    FetchYelpFood(stopLoc);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void FetchYelpFood(Location location) {
        try {
            modelclassList = new ArrayList<ModelClass>();
            YelpClass fetchFood = new YelpClass("pQl7HlElM5rLg8hP0JLNdQ", "HoPvHqXRXr_qqLRC8tC-qO51Eds", "HpfjYt5AEe8byYhDVVtcWWmcQI3jRQBi", "AnKSJuSxwger_GlEK4zfz5gvjTM");
            fetchFood.execute(location);
            try {
                String result = fetchFood.get();
                JSONObject business = new JSONObject(result);
                JSONArray businesses = business.getJSONArray("businesses");
                for (int i = 0; i < businesses.length(); i++) {
                    JSONObject restaurant = (JSONObject) businesses.get(i);
                    String name = restaurant.getString("name");
                    String bizid = restaurant.getString("id");
                    String url = "not available";
                    if(restaurant.has("url")){
                        url = restaurant.getString("url");
                    }

                    String category = "";
                    if(restaurant.has("categories")){
                        JSONArray JsonArrayCategory = restaurant.getJSONArray("categories");
                        for (int j = 0; j < JsonArrayCategory.length(); j++) {
                            JSONArray arr = JsonArrayCategory.getJSONArray(j);
                            if(j == JsonArrayCategory.length() - 1)
                                category += arr.getString(0);
                            else
                                category += arr.getString(0) + ", ";
                        }
                    }
                    else
                    {
                        category = "not available";
                    }
                    String rating = "not available";
                    if(restaurant.has("rating")){
                        rating = restaurant.getString("rating");
                    }
                    String ratingURL = restaurant.getString("rating_img_url");
                    String phone = "not available";
                    if(restaurant.has("phone")){
                        phone = restaurant.getString("phone");
                    }
                    JSONObject loc = restaurant.getJSONObject("location");
                    JSONObject coordinate = loc.getJSONObject("coordinate");
                    Double l1 = coordinate.getDouble("latitude");
                    Double l2 = coordinate.getDouble("longitude");
                    String mobileURL = restaurant.getString("mobile_url");
                    modelclassList.add((new ModelClass(name, url, bizid, rating,ratingURL,mobileURL, category, phone, l1, l2)));
                }

                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                for (ModelClass c : modelclassList) {
                    MarkOnMap(c.getLatitude(), c.getLongitude(), c.getName(), c.getId(), c.getCategory(), c.getRating());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Bitmap resizeMapIcons(String name, int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(name, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



    public void MarkOnMap(Double latitude,Double longitude, String name, String id, String Category, String Rating )
    {
         Marker marker = mMap.addMarker(new MarkerOptions()
                 .position(new LatLng(latitude, longitude))
                 .title(name)
                 .snippet("Rating: " + Rating)
                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.r3))
                 .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("r3", 60, 60))));
        marker.showInfoWindow();
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.getTitle();
        marker.getId();
        ModelClass restaurant = new ModelClass();

        for (ModelClass c : modelclassList) {
            if(marker.getTitle().equals(c.getName())){
                restaurant.setName(c.getName());
                restaurant.setId(c.getId());
                restaurant.setCategory(c.getCategory());
                restaurant.setPhone(c.getPhone());
                restaurant.setRating(c.getRating());
                restaurant.setRatingURL(c.getRatingURL());
                restaurant.setMobileURL(c.getMobileURL());
                restaurant.setLatitude(c.getLatitude());
                restaurant.setLongitude(c.getLongitude());
            }
        }

        startActivity(new Intent(getApplicationContext(), YelpBizDetail.class).putExtra("Detailclass", (Serializable) restaurant));
    }

    private class MyListAdapter extends ArrayAdapter<Bus> {
        public MyListAdapter() {
            super(MapsActivity.this, R.layout.row, myBuses);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.row, parent, false);
            }
            // Find the bus to work with.
            final Bus currentBus = myBuses.get(position);

            // Fill the ID
            TextView idText = (TextView) itemView.findViewById(R.id.busID);
            idText.setText(currentBus.getBusNumber());

            // Direction:
            TextView directionText = (TextView) itemView.findViewById(R.id.busDirection);
            directionText.setText(currentBus.getBusDirection());

            // Stop:
            TextView stopText = (TextView) itemView.findViewById(R.id.busStop);
            stopText.setText(currentBus.getBusStop());

            // Timing:
            TextView timingText = (TextView) itemView.findViewById(R.id.busTiming);
            timingText.setText(currentBus.getBusTiming());

            Button myButton=(Button)itemView.findViewById(R.id.button);
            //myButton.setGravity(Gravity.RIGHT);

            myButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(MapsActivity.this,StopsActivity.class);
                    myIntent.putExtra("BusId",currentBus.getBusNumber());
                    myIntent.putExtra("Agency",currentBus.getAgency());
                    startActivity(myIntent);
                }
            });
            return itemView;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location new_loc = null;

            Intent intent = this.getIntent();
            if(intent.hasExtra("Loc_Lat") && intent.hasExtra("Loc_Long"))
            {
                new_loc = new Location("");
                new_loc.setLatitude(Double.parseDouble(intent.getStringExtra("Loc_Lat")));
                new_loc.setLongitude(Double.parseDouble(intent.getStringExtra("Loc_Long")));
                if(mLastLocation != new_loc)
                {
                    mLastLocation = new_loc;
                }
            }

            if (mLastLocation != null) {
                //mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Me"));
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                LatLng myLoc = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

                FetchBusStops task = new FetchBusStops();
                task.execute(mLastLocation);

                if(new_loc != null)
                {
                    FetchYelpFood(mLastLocation);
                }

            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location new_loc = null;

            Intent intent = this.getIntent();
            if(intent.hasExtra("Loc_Lat") && intent.hasExtra("Loc_Long"))
            {
                new_loc = new Location("");
                new_loc.setLatitude(Double.parseDouble(intent.getStringExtra("Loc_Lat")));
                new_loc.setLongitude(Double.parseDouble(intent.getStringExtra("Loc_Long")));
                if(mLastLocation != new_loc)
                {
                    mLastLocation = new_loc;
                }
            }

            if (mLastLocation != null) {
                //mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Me"));
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                LatLng myLoc = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));

                FetchBusStops task = new FetchBusStops();
                task.execute(mLastLocation);

                if(new_loc != null)
                {
                    FetchYelpFood(mLastLocation);
                }


            }
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if(mLastLocation!=null) {
                            FetchBusStops task = new FetchBusStops();
                            task.execute(mLastLocation);
                            return false;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class FetchBusStops extends AsyncTask<Location, Void, List<Bus>> {


        protected List<Bus> doInBackground(Location... locations) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String busstopJsonStr = null;

            try {

                String baseUrl = "http://restbus.info/api/locations/" + locations[0].getLatitude() + "," + locations[0].getLongitude() + "/predictions";
                URL url = new URL(baseUrl);

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

                busstopJsonStr = buffer.toString();
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
                Log.v("Result : ","Data : "+getBusDataFromJson(busstopJsonStr));
                return getBusDataFromJson(busstopJsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Bus> data) {
            super.onPostExecute(data);

            myBuses.clear();
            myBuses = data;
            populateListView(data);
        }

    }

    private List<Bus> getBusDataFromJson(String forecastJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_VALUES = "values";
        final String OWM_ROUTE = "route";
        final String OWM_DIRECTION = "direction";
        final String OWM_TITLE = "title";
        final String OWM_MINUTES = "minutes";
        final String OWM_ID = "id";
        final String OWM_STOP = "stop";
        final String OWM_AGENCY = "agency";

        JSONArray busRouteJsonArray = new JSONArray(forecastJsonStr);


        List<Bus> resultBus = new ArrayList<Bus>();
        for (int i = 0; i < busRouteJsonArray.length(); i++) {

            String direction;
            String agency;
            String minutes;
            String Id;
            String busStop;
            String busStopId;

            JSONObject busRouteObject = busRouteJsonArray.getJSONObject(i);

            JSONObject routeValueObject = busRouteObject.getJSONArray(OWM_VALUES).getJSONObject(0);
            JSONObject routeIdObject = busRouteObject.getJSONObject(OWM_ROUTE);
            JSONObject agencyObject = busRouteObject.getJSONObject(OWM_AGENCY);
            JSONObject routeDirectionObject = routeValueObject.getJSONObject(OWM_DIRECTION);
            JSONObject routeStopObject = busRouteObject.getJSONObject(OWM_STOP);

            direction = routeDirectionObject.getString(OWM_TITLE);
            agency = agencyObject.getString(OWM_ID);
            minutes = routeValueObject.getString(OWM_MINUTES);
            Id = routeIdObject.getString(OWM_ID);
            busStop = routeStopObject.getString(OWM_TITLE);
            busStopId = routeStopObject.getString(OWM_ID);
            resultBus.add(new Bus(Id, direction, busStop, minutes,agency,busStopId));
        }
        return resultBus;
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}

