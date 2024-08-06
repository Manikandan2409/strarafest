package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.dto.Profile;
import com.anjacarchistra.kvm.stratafest.handler.CertificateCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CertificateHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private CertificateCallBack callback;
    private static  String endpoint = Constants.ENDPOINT + "/get_certificate.php";
    private int teamid;
    private int eventid;
    private String password;

    public CertificateHandler(Context context, CertificateCallBack callback, int teamid, int eventid) {
        this.context = context;
        this.callback = callback;
        this.teamid = teamid;
        this.eventid = eventid;
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
            jsonObject.put("teamid", teamid);
            jsonObject.put("eventid",eventid);

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
             result = String.valueOf(jsonObject.getInt("result"));
            // Notify the callback with the result
            callback.onCertificateSuccess(result);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onCertificateError(result);
        }
    }
}
