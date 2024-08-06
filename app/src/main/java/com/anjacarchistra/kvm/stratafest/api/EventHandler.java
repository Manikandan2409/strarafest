package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anjacarchistra.kvm.stratafest.api.Constants;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private EventCallback callback;
    private String endpoint = Constants.ENDPOINT + "/get_events.php";

    public EventHandler(Context context, EventCallback callback) {
        Log.d("EVENT HANDLER","GET EVENTS FROM DB");
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                response = sb.toString();
                reader.close();
            } else {
                response = "Error: " + responseCode;
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            response = "Exception: " + e.getMessage();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            List<Event> events = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int eventId = jsonObject.getInt("eventid");
                String eventName = jsonObject.getString("evname");
                int maxParticipant = jsonObject.getInt("maxparticipant");
                int minParticipant = jsonObject.getInt("minparticipant");
                String time = jsonObject.getString("evtime");
                String venue = jsonObject.getString("venuename");

                Event event = new Event(eventId, eventName, maxParticipant, minParticipant,time,venue);
                Log.d("EVENT",event.toString());

                events.add(event);
            }

            // Notify the callback with the result
            callback.onSuccess(events);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to parse JSON");
        }
    }
}
