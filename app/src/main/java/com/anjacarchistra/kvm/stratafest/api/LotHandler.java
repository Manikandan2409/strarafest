package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.handler.LotCallback;
import com.anjacarchistra.kvm.stratafest.dto.Lot;

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

public class LotHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private LotCallback callback;
    private String endpoint = Constants.ENDPOINT + "login_lots.php";

    public LotHandler(Context context, LotCallback callback) {
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
            String jsonArrayString = "[" + result.replace("}{", "},{") + "]";
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            List<Lot> lots = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String lotId = jsonObject.getString("lotid");
                String lotName = jsonObject.getString("lotname");
                lots.add(new Lot(lotId, lotName));
            }
            // Notify the callback with the result
            callback.onSucceed(lots);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onProblem("Failed to parse JSON");
        }
    }
}
