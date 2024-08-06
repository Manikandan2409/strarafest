package com.anjacarchistra.kvm.stratafest.api;

import android.content.Context;
import android.os.AsyncTask;

import com.anjacarchistra.kvm.stratafest.handler.QRCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;
import com.anjacarchistra.kvm.stratafest.util.FoodDetails;

public class QrFoodHandler extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private QRCallback callback;

    private int Lotid;
    private String name;
    public QrFoodHandler(Context context, QRCallback callback, int Lotid, String name) {
        this.context = context;
        this.callback = callback;
        this.Lotid = Lotid;
        this.name = name;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SQLiteHelper dbHelper = SQLiteHelper.getInstance(context);
            FoodDetails foodDetail = new FoodDetails(Lotid, name);
            dbHelper.addFoodDetails(foodDetail); // Store data in local database
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            callback.onSuccess("Data saved successfully");
        } else {
            callback.onError("Failed to save data");
        }
    }
}
