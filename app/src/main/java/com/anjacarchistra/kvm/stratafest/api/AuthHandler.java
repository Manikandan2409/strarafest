package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.dto.Profile;
import com.anjacarchistra.kvm.stratafest.handler.AuthCallback;
import com.anjacarchistra.kvm.stratafest.util.Helper;

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

public class AuthHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private AuthCallback callback;
    private static  String endpoint = Constants.ENDPOINT + "/auth.php";
    private int lotid;
    private String name;
    private String password;

    public AuthHandler(Context context, AuthCallback callback, int lotid, String name, String password) {
        this.context = context;
        this.callback = callback;
        this.lotid = lotid;
        this.name = name;
        this.password = password;
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Create JSON data
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lotid", lotid);
            jsonObject.put("pname", name);
            jsonObject.put("password", password);

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
            JSONObject jsonObject = new JSONObject(result);

            // Extract the fields from the JSON object
            String lotid = jsonObject.getString("parid");
            String name = jsonObject.getString("name");
            String password = this.password; // If password is not part of the response, handle accordingly
            String collegename = jsonObject.getString("clgname");
            String deptname = jsonObject.getString("deptname");
            String eventid = jsonObject.getString("eventid");
            String time = jsonObject.getString("eventtime");
            String venue = jsonObject.getString("eventvenue");
            Profile profile = new Profile(lotid, name, password, eventid, time, venue, collegename, deptname);

            // Notify the callback with the result
            callback.onSuccess(profile);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError(result);
        }
    }
}
