package com.example.sri.locationtracker;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by sri on 1/4/17.
 */

public class GMapASyncTask extends AsyncTask<String, Void, Document> {

    private final static String TAG = GMapASyncTask.class.getSimpleName();
    private Handler handler;
    private LatLng start, end;
    private String mode;

    public GMapASyncTask(Handler handler, LatLng sourcePosition, LatLng destPosition, String modeDriving) {
        this.start = sourcePosition;
        this.end = destPosition;
        this.mode = modeDriving;
        this.handler = handler;
    }

    @Override
    protected Document doInBackground(String... params) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=" + mode;
        Log.d("url", url);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            Log.i("FSDS",doc.toString());
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Document result) {
        if (result != null) {
            Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
            Message message = new Message();
            message.obj = result;
            handler.dispatchMessage(message);
        } else {
            Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
        }
    }

    @Override
    protected void onPreExecute() {
    }

}
