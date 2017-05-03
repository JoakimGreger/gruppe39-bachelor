package com.example.joakim.ceapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import android.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class LocationMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager lm;
    private Location mLastLocation;
    private String provider;
    private ArrayList<Position> locations;
    private ArrayList<String> activeM;
    private ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_maps);
        markers = new ArrayList<Marker>();
        activeM = new ArrayList<String>();



        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        provider = lm.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            lm.requestLocationUpdates(provider, 500, 1, this);
            mLastLocation = lm.getLastKnownLocation(provider);
            Log.e("Exception", "Location: " + mLastLocation.getLatitude());
        }

        /*
        Log.d("I", "onCreate");
        System.out.println("ONCREATE CALLED");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mLastLocation = lm.getLastKnownLocation(provider);

        }
        */


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        locations = new ArrayList<Position>();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 20 || hourOfDay <=4){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_night_json));
        } if (hourOfDay >= 5 || hourOfDay < 20) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.style_day_json));
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        mMap.setMinZoomPreference(17);
        mMap.setMaxZoomPreference(17);
        mMap.getUiSettings().setScrollGesturesEnabled(false);


        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));


        new LocationsTask().execute("http://webapp.bimorstad.tech/usertest/read");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

        }

        // new LocationsTask().execute("http://webapp.bimorstad.tech/usertest/read");
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean curr = false;

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));


        for(Position pos: locations) {
            float[] dist = new float[1];
            location.distanceBetween(location.getLatitude(), location.getLongitude(), pos.lat, pos.lng, dist);
            if (dist[0] < 50) {
                activeM.add(pos.id);
                curr = true;
                pos.active = true;
                pos.m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if(pos.active) {
                pos.m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                pos.active = false;
            }
        }



    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        for (Position pos: locations) {
            if (pos.lat == marker.getPosition().latitude && pos.lng == marker.getPosition().longitude && pos.active) {
                Intent intent = new Intent(this, DragActivity.class);
                intent.putExtra("id", pos.id);
                startActivity(intent);
            }
        }


        return false;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public class LocationsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = null;
            HttpURLConnection connection = null;

            try {
                String adress = params[0];
                URL url = new URL(adress);
                connection = (HttpURLConnection) url.openConnection();


                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=reader.readLine())!=null)
                {
                    builder.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                // ArrayList<String> list = db.getList();
                markers.clear();
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject obj = json.getJSONObject(i);

                    boolean hasUserTest = false;
                    // Checks if user has pokemon
                    /*
                    for(String id: list) {
                        if (id.equals(obj.getString("_id"))) {
                            hasUserTest = true;
                        }
                    }
                    */
                    LatLng marker = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
                    float[] dist = new float[1];
                    mLastLocation.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), marker.latitude, marker.longitude, dist);
                    if (dist[0] < 50) {
                        Marker m = mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(obj.getString("title")));
                        locations.add(new Position(marker.latitude, marker.longitude, obj.getString("Id"),  m, true));
                    } else {
                        Marker m = mMap.addMarker(new MarkerOptions().position(marker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(obj.getString("title")));
                        locations.add(new Position(marker.latitude, marker.longitude, obj.getString("Id"),  m, false));
                    }


                        /*
                    if (hasUserTest) {

                    } else {
                        mMap.addMarker(new MarkerOptions().position(marker).title(obj.getString("name")));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class Position {
        public double lat;
        public double lng;
        public String id;
        public Marker m;
        public Boolean active;

        public Position(double lat, double lng, String id, Marker m, Boolean active) {
            this.lat = lat;
            this.lng = lng;
            this.id = id;
            this.m = m;
            this.active = active;
        }
    }

}
