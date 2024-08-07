package com.anjacarchistra.kvm.stratafest;

import static com.anjacarchistra.kvm.stratafest.api.Constants.EMAIL_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.NAME_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PASSWORD_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anjacarchistra.kvm.stratafest.api.QrValidationhandler;
import com.anjacarchistra.kvm.stratafest.handler.QRCallback;
import com.anjacarchistra.kvm.stratafest.util.Helper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity implements QRCallback {

    private Button register;
    private  Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString(NAME_KEY, null);
        String password = sharedPreferences.getString(PASSWORD_KEY, null);
        if (email!=null&& password !=null){
            startActivity(new Intent(MainActivity.this,Dashboard.class));
        }
        register = findViewById(R.id.registerButton);
        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        Login.class));
            }
        });
        register.setOnClickListener(view -> checkPermissionAndShowActivity(this));
//    register.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            startActivity(new Intent(MainActivity.this,Register.class));
//        }
//    });



    }
// permission
    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required!!", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            showCamera();
        } else {
            Toast.makeText(this, "Camera permission required!!", Toast.LENGTH_SHORT).show();
            // Handle the case where the user denied the permission
        }
    });

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Qr code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
            else {

            String teamid = Helper.decode(result.getContents()).split(",")[0];
            Toast.makeText(this, teamid, Toast.LENGTH_SHORT).show();

            // testing purpose
            Intent i = new Intent(MainActivity.this,RegisterDescription.class);
            i.putExtra("encodevalue",result.getContents());
            startActivity(i);
            //      new QrValidationhandler(this,this,Integer.parseInt(teamid)).execute();
            }
    });

    @Override
    public void onSuccess(String message) {
       Intent i = new Intent(MainActivity.this,Register.class);
       i.putExtra("encodevalue",message);
       startActivity(i);

    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}