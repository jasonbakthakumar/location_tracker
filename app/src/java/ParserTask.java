package com.example.sri.locationtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sri on 1/4/17.
 */

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    GoogleMap mMap;
    Context context;
    public ParserTask(GoogleMap googleMap, Context context) {
        mMap = googleMap;
        this.context = context;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {

            jObject = new JSONObject(jsonData[0]);
            Log.d("ParserTaskds",jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("ParserTask", parser.toString());
            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("ParserTask","Executing routes");
            Log.d("ParserTask",routes.toString());


        } catch (Exception e) {
            Log.d("ParserTask",e.toString());
            Log.d("log_tag", "Error parsing data "+e.toString());
            Log.d("log_tag", "Failed data was:\n" + jsonData[0].toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            Set<String> latSet =new HashSet<String>();
            Set<String> longSet =new HashSet<String>();

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                latSet.add(String.valueOf(lat));
                longSet.add(String.valueOf(lng));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }

            SharedPreferences preferences = context.getSharedPreferences("SETLOCATION",Context.MODE_PRIVATE);
            SharedPreferences.Editor preferenceEdit = preferences.edit();
            preferenceEdit.putStringSet("latSet",latSet);
            preferenceEdit.putStringSet("longSet",longSet);
            preferenceEdit.apply();
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);

            Log.d("onPostExecute","onPostExecute lineoptions decoded");

        }

        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions != null) {
            mMap.addPolyline(lineOptions);
        }
        else {
            Log.d("onPostExecute","without Polylines drawn");
        }
    }
}
