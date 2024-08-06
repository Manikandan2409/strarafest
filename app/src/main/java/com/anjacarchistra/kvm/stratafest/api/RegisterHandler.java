package com.anjacarchistra.kvm.stratafest.api;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.anjacarchistra.kvm.stratafest.api.Constants;
import com.anjacarchistra.kvm.stratafest.handler.RegisterCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private RegisterCallback callback;
    private JSONObject json;
    private String endpoint = Constants.ENDPOINT + "team_register.php";

    public RegisterHandler(Context context, RegisterCallback callback, JSONObject json) {
        this.context = context;
        this.callback = callback;
        this.json = json;
        Log.d("RegisterHandler", "Constructor called with JSON: " + json.toString());
    }

    @Override
    protected String doInBackground(Void... params) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Write JSON data to output stream
            OutputStream os = conn.getOutputStream();
            os.write(this.json.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            // Read response
            int responseCode = conn.getResponseCode();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("RegisterHandler", "Response from server: " + response.toString());
            } else {
                Log.e("RegisterHandler", "Error: " + responseCode);
                response.insert(0, "Error: ");
            }
            conn.disconnect();
        } catch (Exception e) {
            Log.e("RegisterHandler", "Exception: " + e.getMessage());
            response.append("Exception: ").append(e.getMessage());
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("RegisterHandler", "Result: " + result);
        try {
            JSONObject obj = new JSONObject(result);
            String status = obj.getString("status");
            // Notify the callback with the result
            callback.onSuccess(status);
        } catch (JSONException e) {
            Log.e("RegisterHandler", "Failed to parse JSON: " + e.getMessage());
            callback.onError("Failed to parse JSON");
        }
    }
}
