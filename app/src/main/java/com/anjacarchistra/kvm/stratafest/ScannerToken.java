package com.anjacarchistra.kvm.stratafest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ScannerToken extends AppCompatActivity {
    Button scanner;
    Button viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_token);
        scanner=findViewById(R.id.ScannerButton);
        viewer=findViewById(R.id.ViewButton);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(ScannerToken.this,Scanner.class));
            }
        }
        );
        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerToken.this,Viewer.class));
            }
        });
    }
}