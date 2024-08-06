package com.anjacarchistra.kvm.stratafest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;
import com.anjacarchistra.kvm.stratafest.util.FoodDetails;

import java.util.List;

public class Viewer extends AppCompatActivity {

    private TableLayout tableLayout;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = SQLiteHelper.getInstance(this);

        populateTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateTable();
    }
    private void populateTable() {
        tableLayout.removeAllViews(); // Clear existing rows

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Optional: background color for header
        headerRow.setPadding(5, 5, 5, 5); // Padding for header

        // Add headers
        TextView numberHeader = new TextView(this);
        numberHeader.setText("S.No.");
        numberHeader.setPadding(15, 15, 15, 15);
        numberHeader.setTextColor(Color.BLACK); // Optional: text color for header
        numberHeader.setGravity(Gravity.CENTER);
        headerRow.addView(numberHeader);

        TextView lotIdHeader = new TextView(this);
        lotIdHeader.setText("Lot ID");
        lotIdHeader.setPadding(15, 15, 15, 15);
        lotIdHeader.setTextColor(Color.BLACK); // Optional: text color for header
        lotIdHeader.setGravity(Gravity.CENTER);
        headerRow.addView(lotIdHeader);

        TextView nameHeader = new TextView(this);
        nameHeader.setText("Name");
        nameHeader.setPadding(15, 15, 15, 15);
        nameHeader.setTextColor(Color.BLACK); // Optional: text color for header
        nameHeader.setGravity(Gravity.CENTER);
        headerRow.addView(nameHeader);

        tableLayout.addView(headerRow);

        List<FoodDetails> foodDetailsList = dbHelper.getAllFoodDetails();
        int count = 1; // Initialize serial number counter
        for (FoodDetails foodDetail : foodDetailsList) {
            TableRow row = new TableRow(this);

            // Add serial number column
            TextView numberView = new TextView(this);
            numberView.setText(String.valueOf(count++)); // Increment serial number
            numberView.setPadding(15, 15, 15, 15);
            numberView.setGravity(Gravity.CENTER);
            row.addView(numberView);

            // Add lot ID column
            TextView lotIdView = new TextView(this);
            lotIdView.setText(String.valueOf(foodDetail.getLotid()));
            lotIdView.setPadding(15, 15, 15, 15);
            lotIdView.setGravity(Gravity.CENTER);
            row.addView(lotIdView);

            // Add name column
            TextView nameView = new TextView(this);
            nameView.setText(foodDetail.getName());
            nameView.setPadding(15, 15, 15, 15);
            nameView.setGravity(Gravity.CENTER);
            row.addView(nameView);

            tableLayout.addView(row);
        }
    }

}
