package com.example.sri.locationtracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    GoogleMap classMap;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    double sourceLatitude = 0.0;
    double sourceLongitude = 0.0;
    EditText fromEditText;
    EditText toEditText;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 5;
    ArrayList<LatLng> markerPoints;

    ImageView goButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        markerPoints = new ArrayList<LatLng>();
        goButton = (ImageView) findViewById(R.id.imageView);
        fromEditText = (EditText) findViewById(R.id.editText3);
        toEditText = (EditText) findViewById(R.id.editText4);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(Main2Activity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("CONTACTS",MODE_PRIVATE);
                if(preferences.getStringSet("nameSet",null) != null) {
                    if (sourceLatitude != 0.0) {
                        LatLng sourcePosition = new LatLng(sourceLatitude, sourceLongitude);
                        LatLng destPosition = new LatLng(13.0504, 80.1455);
                        if (classMap != null) {
                            String from = fromEditText.getText().toString().trim().replaceAll("\\s+", "+");
                            String to = toEditText.getText().toString().trim().replaceAll("\\s+", "+");
                            FetchURL FetchUrl = new FetchURL(classMap, Main2Activity.this);
                           String urlNew = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from + "&destination=" + to + "&key=<YOUR_API_KEY>";

                            FetchUrl.execute(urlNew);
                        }

                    } else {
                        Toast.makeText(Main2Activity.this, "Wait the map is being initialized", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Main2Activity.this, "Please specify a contact to notify", Toast.LENGTH_LONG).show();
                }


                //route(sourcePosition,destPosition,"driving");

            }
        });



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Code to be filled
        classMap = googleMap;
            getLocationAndShow(googleMap);


    }


    protected void route(LatLng sourcePosition, LatLng destPosition, String mode) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(10).color(
                            Color.RED);

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    if(classMap != null){
                        Toast.makeText(Main2Activity.this, "Here", Toast.LENGTH_SHORT).show();
                        classMap.addPolyline(rectLine);
                        md.getDurationText(doc);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };

        new GMapASyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();

    }



    public void getLocationAndShow(GoogleMap googleMap) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(this, "This is important", Toast.LENGTH_SHORT).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {

            try {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                // getting GPS status
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    Location location = null;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                sourceLatitude = location.getLatitude();
                                sourceLongitude = location.getLongitude();
                                LatLng sydney = new LatLng(sourceLatitude, sourceLongitude);
                                googleMap.setMyLocationEnabled(true);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
                                googleMap.addMarker(new MarkerOptions()
                                        .title("Chennai")
                                        .snippet("India is my country")
                                        .position(sydney));
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    sourceLatitude = location.getLatitude();
                                    sourceLongitude = location.getLongitude();
                                    Toast.makeText(this, "I'm here", Toast.LENGTH_SHORT).show();
                                    LatLng sydney = new LatLng(sourceLatitude, sourceLongitude);
                                    googleMap.setMyLocationEnabled(true);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
                                    googleMap.addMarker(new MarkerOptions()
                                            .title("Chennai")
                                            .snippet("India is my country")
                                            .position(sydney));
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted

                    Toast.makeText(this, "restart the app", Toast.LENGTH_SHORT).show();


                }
                else{
                    //not granted
                    Toast.makeText(this, "It wont work", Toast.LENGTH_SHORT).show();
                }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.expanded_menu:

                startActivity(new Intent(this,Main3Activity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("IMTHELOCATION", String.valueOf(location.getLatitude()) + " long = " + location.getLongitude());
        String reverseGeocoding = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(location.getLatitude()) + "," +String.valueOf(location.getLongitude()) + "&key=AIzaSyA8UaEorMPCVoXGHf89ubhpuDMFFnRmb2o";
        if(classMap != null) {
            FetchRevGeoURL fetchURL = new FetchRevGeoURL(classMap,Main2Activity.this);
            fetchURL.execute(reverseGeocoding);
        }



//        SharedPreferences prefs = getSharedPreferences("SETLOCATION",MODE_PRIVATE);
//        Set<String> latSet = prefs.getStringSet("latSet",null);
//        Set<String> longSet = prefs.getStringSet("longSet",null);
//        if(latSet != null && longSet != null) {
//            List<String> LATTlist = new ArrayList<String>(latSet);
//            List<String> LONGlist = new ArrayList<String>(longSet);
//            if(LATTlist.contains(String.valueOf(location.getLatitude()))){
//                if(LONGlist.contains(String.valueOf(location.getLongitude()))){
//                    showTheNotifiationRight();
//                }
//                else{
//                    showTheNotifiation();
//                }
//            }
//            else{
//                showTheNotifiation();
//            }
//            for (int i = 0; i < LATTlist.size(); i++) {
//                String latString = LATTlist.get(i);
//                String longString = LONGlist.get(i);
//                double latG = Double.parseDouble(latString);
//                double longG = Double.parseDouble(longString);
//                if (Double.compare(latG, location.getLatitude()) != 0 && Double.compare(longG, location.getLongitude()) != 0) {
//                    if(i == LATTlist.size() - 1) {
//                        showTheNotifiation();
//                        break;
//                    }
//                }
//            }
        }


    private void showTheNotifiationRight() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle("RIGHT ROUTE")
                .setContentText("LOCATION TRACKER")
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notification.build());
    }

    private void showTheNotifiation() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle("WRONG ROUTE")
                .setContentText("LOCATION TRACKER")
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0,notification.build());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
