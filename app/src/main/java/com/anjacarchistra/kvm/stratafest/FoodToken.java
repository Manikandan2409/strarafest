package com.anjacarchistra.kvm.stratafest;

import static com.anjacarchistra.kvm.stratafest.api.Constants.EMAIL_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PASSWORD_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.anjacarchistra.kvm.stratafest.util.Helper;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class FoodToken extends AppCompatActivity {
    private ImageView qrcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_token);

        qrcode = findViewById(R.id.generatedQR);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, null);
        String password = sharedPreferences.getString(PASSWORD_KEY, null);
        Bitmap qrbitmap=qrGenerator(Helper.encode(email+","+password));
        qrcode.setImageBitmap(qrbitmap);
    }
    private Bitmap qrGenerator(String value){
        try {
            BarcodeEncoder be = new BarcodeEncoder();
            return  be.encodeBitmap(String.valueOf(Uri.parse(value)), BarcodeFormat.QR_CODE, 400, 400);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}