package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.handler.ApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventHandler extends AsyncTask<JSONObject, Void, String> {
    private Context context;
    private ApiCallback callback;
    private String endpoint = Constants.ENDPOINT + "/get-events.php";

    public EventHandler(Context context, ApiCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        JSONObject jsonObject = params[0];
        String response = "";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Write JSON data to output stream
            OutputStream os = conn.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

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
        System.out.println(result);
        try {
            JSONArray jsonArray = new JSONArray(result);
            List<Event> events = new ArrayList<>();
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int eventId = jsonObject.getInt("eventid");
                String eventName = jsonObject.getString("evname");
                int maxParticipant = jsonObject.getInt("maxparticipant");
                int minParticipant = jsonObject.getInt("minparticipant");

                Event event = new Event(eventId, eventName, maxParticipant, minParticipant);
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
