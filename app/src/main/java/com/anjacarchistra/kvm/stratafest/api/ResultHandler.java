package com.anjacarchistra.kvm.stratafest.api;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Lot;
import com.anjacarchistra.kvm.stratafest.dto.Result;
import com.anjacarchistra.kvm.stratafest.handler.LotCallback;
import com.anjacarchistra.kvm.stratafest.handler.ResultCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultHandler extends AsyncTask<Void, Void, String> {
    private Context context;
    private ResultCallback callback;
    private String endpoint = Constants.ENDPOINT + "get_results.php";
    SQLiteHelper databaseHandler;

    public ResultHandler(Context context, ResultCallback callback) {
        this.context = context;
        this.callback = callback;
       // Toast.makeText(context, "FETCHING VALUES FOR RESULTS", Toast.LENGTH_SHORT).show();
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
    protected void onPostExecute(String resultString) {
        super.onPostExecute(resultString);
        try {

            databaseHandler = SQLiteHelper.getInstance(context);

           if (databaseHandler.getAllEvents().isEmpty()){callback.onError("No Event in local");}
            List<Result> results = new ArrayList<>();
           List<Event> events =  databaseHandler.getAllEvents();
                JSONObject jsonObject = new JSONObject(resultString);

            for (Event event:events) {

                JSONObject eventjson = jsonObject.getJSONObject(event.getEventName());

                Boolean prelims = eventjson.getInt("prelims")==1?true:false;
                List<String> prelimsresult= eventjson.getString("prelims_lots") == null?null:
                        Arrays.asList(eventjson.getString("prelims_lots")
                                .split(","));
                Boolean finals = eventjson.getInt("finals") ==1?true:false;

                List<String> finalresult = eventjson.getString("final_lots")== null?null:
                        Arrays.asList(eventjson.getString("final_lots").split(","));
                LinkedHashSet<String> selectedSet = prelimsresult.stream().collect(Collectors.toCollection(LinkedHashSet::new));
                LinkedHashSet<String> winnersSet = finalresult.stream().collect(Collectors.toCollection(LinkedHashSet::new));

                Result result = new Result(event.getEventId(), event.getEventName(), prelims, selectedSet, finals, winnersSet);
                results.add(result);
                Log.d("RESULTS",result.toString());
            }

            // Notify the callback with the result
            Log.d("PASSING TO SUCESS",results.toString());
            callback.onResultsSuccess(results);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Failed to parse JSON");
        }
    }
}

