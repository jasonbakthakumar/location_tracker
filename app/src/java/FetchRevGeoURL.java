package com.example.sri.locationtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sri on 1/4/17.
 */

public class FetchRevGeoURL extends AsyncTask<String, Void, String> {



    Context context;
    GoogleMap googleMap;

    public FetchRevGeoURL(GoogleMap classMap, Context context) {
        googleMap =classMap;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
            Log.d("Background Task data", data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject json = (JSONObject) new JSONObject(result);
            //Get JSON Array called "results" and then get the 0th complete object as JSON
            JSONObject location = json.getJSONArray("results").getJSONObject(0);
            // Get the value of the attribute whose name is "formatted_string"
            String location_string = location.getString("formatted_address");
            Log.d("testZZZ", "formatted address:" + location_string);
            if(location_string.equals("1952, Vasantham Colony, Anna Nagar, Chennai, Tamil Nadu 600040, India")){
                MessageTask messageTask = new MessageTask(context,false);
                messageTask.execute(location_string);
            }
            else{
                MessageTask messageTask = new MessageTask(context,true);
                messageTask.execute(location_string);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ParserTask parserTask = new ParserTask(googleMap,context);
//
//        // Invokes the thread for parsing the JSON data
//        parserTask.execute(result);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



}
