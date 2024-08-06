package com.anjacarchistra.kvm.stratafest;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    private  Button button1,button2,button3,button4,button5,button6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


         button1 = findViewById(R.id.button);
         button2 = findViewById(R.id.button2);
         button3 = findViewById(R.id.button3);
         button4 = findViewById(R.id.button4);
         button5 = findViewById(R.id.button5);
         button6 = findViewById(R.id.button6);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Dashboard.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, EventTableActivity.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Dashboard.this, "Food Token Button Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Dashboard.this,FoodToken.class));
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,CertificateActivity.class));
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(Dashboard.this, "Logout Button Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Dashboard.this,ResultActivity.class));
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent =new Intent(Dashboard.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}