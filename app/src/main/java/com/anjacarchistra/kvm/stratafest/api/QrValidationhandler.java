package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.anjacarchistra.kvm.stratafest.handler.QRCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class QrValidationhandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private QRCallback callback;
    private int teamid;
    private  static  String ENDPOINT = Constants.ENDPOINT+"qr_check.php";

    public QrValidationhandler(Context context, QRCallback callback, int teamid) {
        this.context = context;
        this.callback = callback;
        this.teamid = teamid;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response ="";
        try{
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("teamid",teamid);

            OutputStream os = conn.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

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


        } catch (MalformedURLException e) {
            response = "Error: Cannot connect to server";
        } catch (ProtocolException e) {
           response ="Error: In Appropriate  Protocol ";
        } catch (IOException e) {
            response="Error: Typo error";
        } catch (JSONException e) {
            response="Cannot store value as json";
        }
        return  response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try{
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            int d = Log.d(String.format("JSON: value %s", s),s);
            JSONObject jsonObject = new JSONObject(s);
          String encrypt=  jsonObject.getString("encrypt");
callback.onSuccess(encrypt);
        }catch (JSONException e){
            e.printStackTrace();
            callback.onError("Failed to parse JSON");
        }

    }
}
