package com.example.sri.locationtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sri on 10/4/17.
 */

public class MessageTask extends AsyncTask<String,Void,Void> {

    Context context;
    boolean fuck;
    public MessageTask(Context context1, boolean b) {
        context = context1;
        fuck = b;
    }

    @Override
    protected Void doInBackground(String... params) {
        String result = sendTheMessage(params[0]);
        return null;
    }

    private String sendTheMessage(String param) {
        try{
        SharedPreferences preferences = context.getSharedPreferences("CONTACTS", MODE_PRIVATE);
            SharedPreferences prefs = context.getSharedPreferences("REG", MODE_PRIVATE);
        Set<String> setOfString = preferences.getStringSet("phoneSet",null);
        if(setOfString != null){
            List<String> destStrings = new ArrayList<String>(setOfString);
            String destination = "";
            for(int i=0; i<destStrings.size();i++){
                String temp = destStrings.get(i).trim().replaceAll("\\s+","");
                destination = temp.concat(destination);
                if(i != destStrings.size()-1){
                    destination = temp.concat("<");
                }
            }
            String authId = "MAYJIZYTDLZWNLZWUXYZ";
            String authToken = "NTIwOWQyZmQxNWUxMzhmM2E0ZmRkN2ZhZDc4ZTk0";
            RestAPI api = new RestAPI(authId, authToken, "v1");
            LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
            parameters.put("src", "+919710757370"); // Sender's phone number with country code
            parameters.put("dst", destination); // Receiver's phone number with country code
            if(fuck){
                parameters.put("text", prefs.getString("name","I") + " is going on correct path\n He is near  : " + param); // Your SMS text messeage
            }
            else{
                parameters.put("text",prefs.getString("name","I") + " is going on the WRONG PATH\n. He is near  : " + param); // Your SMS text messeage
            }
             // Send Unicode text
            //parameters.put("text", "こんにちは、元気ですか？"); // Your SMS text message - Japanese
            //parameters.put("text", "Ce est texte généré aléatoirement"); // Your SMS text message - French
            parameters.put("method", "GET"); // The method used to call the url

            MessageResponse msgResponse = api.sendMessage(parameters);
            // Print the response
            Log.d("STUFF",msgResponse.toString());

            if (msgResponse.serverCode == 202) {
                // Print the Message UUID
                Log.d("STUFF","Message UUID : " + msgResponse.messageUuids.get(0).toString());
            } else {
                Log.d("STUFF","Message UUID : " + msgResponse.error);
            }}}
             catch (PlivoException e) {
                e.printStackTrace();
            }
            return null;
    }
}
