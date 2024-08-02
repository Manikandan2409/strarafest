package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.Login;
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

public class LoginHandler extends AsyncTask<JSONObject, Void, String> {
    private  Context context ;
    private ApiCallback callback;
    private String email;
    private String password;

    private String endpoint = Constants.ENDPOINT + "/login.php";



    public LoginHandler(Context context, ApiCallback callback, String email, String password) {
        this.context = context;
        this.callback = callback;
        this.email = email;
        this.password = password;
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        JSONObject jsonObject = params[0];
        String response = "";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
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
            List<Profile> events = new ArrayList<>();
            if (jsonArray.length() == 0) {
                callback.onError("No matching record found");
                return;
            }
            System.out.println(jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int participantId = jsonObject.getInt("participantid");
                String name = jsonObject.getString("name");
                String collegeName = jsonObject.getString("collegename");
                String departmentName = jsonObject.getString("departmentname");
                String eventName = jsonObject.getString("evname");
                String email = jsonObject.getString("email");
                String password = jsonObject.getString("password");

                // Assuming Event class can hold participant details, modify if necessary
                Profile event = new Profile(participantId, name, collegeName, departmentName, eventName, email, password);
                events.add(event);
            }

            // Notify the callback with the result
           // callback.onSuccess(events);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to parse JSON");
        }
    }
}
