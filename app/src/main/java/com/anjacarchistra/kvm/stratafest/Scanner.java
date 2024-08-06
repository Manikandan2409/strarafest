package com.anjacarchistra.kvm.stratafest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
//
//import com.anjacarchistra.kvm.stratafest.api.QrFoodHandler;
import com.anjacarchistra.kvm.stratafest.api.QrFoodHandler;
import com.anjacarchistra.kvm.stratafest.handler.QRCallback;
import com.anjacarchistra.kvm.stratafest.util.Helper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class Scanner extends AppCompatActivity implements QRCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Set orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkPermissionAndShowActivity(this);
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required!!", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            showCamera();
        } else {
            Toast.makeText(this, "Camera permission required!!", Toast.LENGTH_SHORT).show();
        }
    });

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            handleQrCodeResult(result.getContents());
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    });

    private void handleQrCodeResult(String resultContents) {
        String[] parts = Helper.decode(resultContents).split(",");
        int Lotid = Integer.parseInt(parts[0]);
        String name = parts[1];

        // Create an instance of QrFoodHandler
        QrFoodHandler qrFoodHandler = new QrFoodHandler(this, this, Lotid, name);
        qrFoodHandler.execute();
    }

    @Override
    public void onSuccess(String result) {
        // Handle success case
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, SuccessToken.class));
        finish();
    }

    @Override
    public void onError(String errorMessage) {
        // Handle error case
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
