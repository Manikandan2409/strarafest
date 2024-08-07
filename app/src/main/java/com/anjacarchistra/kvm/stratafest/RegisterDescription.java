package com.anjacarchistra.kvm.stratafest;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;
import com.anjacarchistra.kvm.stratafest.util.Helper;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RegisterDescription extends AppCompatActivity implements EventCallback {

    private TableLayout tableLayout;
    private LinearLayout multiPointLayout;
    private SQLiteHelper databaseHelper;
    private CountDownLatch latch;
    TextView collegeName, departmentName;
    private Button nextButton;
    String[] value;
    List<String> points;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_description);

        databaseHelper = SQLiteHelper.getInstance(this);

        Intent i = getIntent();
        String encodedvalue = i.getStringExtra("encodevalue");
        value = Helper.decode(encodedvalue).split(",");
        tableLayout = findViewById(R.id.desctableLayout);
        collegeName = findViewById(R.id.collegeName);
        departmentName = findViewById(R.id.departmentName);
        multiPointLayout = findViewById(R.id.multiPointLayout);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle next button click
                // Navigate to next activity or perform desired action
            }
        });



        collegeName.setText(value[1]);
        try {
            collegeName.setTextColor(getResources().getColor(R.color.secnd_light_blue, null));
        } catch (Resources.NotFoundException e) {
            Log.e("RegisterDescription", "Color resource not found", e);
        }
        departmentName.setText(value[2]);

        // Initialize the CountDownLatch with 1 as we are waiting for one thread to finish.
        latch = new CountDownLatch(1);

        // Check if the events table has records
        if (databaseHelper.getAllEvents().isEmpty()) {
            // If empty, fetch events from the API
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("DESCRIPTION", "CALLING EVENT THREAD");
                    new EventHandler(RegisterDescription.this, RegisterDescription.this).execute();
                    try {
                        // Wait for the events to be stored
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Populate the table after adding events to the database
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateTable();
                            populatePoints();
                        }
                    });
                }
            }).start();
        } else {
            // If not empty, populate the table directly
            populateTable();
            populatePoints();
        }
    }

    @Override
    public void onSuccess(List<Event> events) {
        for (Event event : events) {
            Log.d("INSERTION", event.toString());
            databaseHelper.addEvent(event);
        }
        // Countdown the latch to indicate that the events have been stored
        latch.countDown();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Cannot fetch events from API", Toast.LENGTH_SHORT).show();
        // Countdown the latch to avoid waiting indefinitely
        latch.countDown();
    }

    private void populateTable() {
        tableLayout.removeAllViews();

        // Create header row
        TableRow headingRow = new TableRow(this);

        TextView headingEventName = new TextView(this);
        headingEventName.setText("Event Name");
        headingEventName.setPadding(25, 25, 25, 25);
        headingEventName.setGravity(Gravity.CENTER);
        headingEventName.setTextColor(Color.BLACK);
        headingEventName.setBackgroundResource(R.drawable.border_table);
        headingEventName.setTextAppearance(this, R.style.TableCell);

        TextView headingVenue = new TextView(this);
        headingVenue.setText("Max Participants");
        headingVenue.setPadding(25, 25, 25, 25);
        headingVenue.setGravity(Gravity.CENTER);
        headingVenue.setTextColor(Color.BLACK);
        headingVenue.setBackgroundResource(R.drawable.border_table);
        headingVenue.setTextAppearance(this, R.style.TableCell);

        TextView headingTime = new TextView(this);
        headingTime.setText("Time");
        headingTime.setPadding(25, 25, 25, 25);
        headingTime.setGravity(Gravity.CENTER);
        headingTime.setTextColor(Color.BLACK);
        headingTime.setBackgroundResource(R.drawable.border_table);
        headingTime.setTextAppearance(this, R.style.TableCell);

        headingRow.addView(headingEventName);
        headingRow.addView(headingVenue);
        headingRow.addView(headingTime);
        tableLayout.addView(headingRow);

        // Fetch and display event data
        List<Event> events = databaseHelper.getAllEvents();
        Log.d("EVENTS",events.toString());
        Toast.makeText(this, events.toString(), Toast.LENGTH_SHORT).show();
        if (events.isEmpty()) {
            Toast.makeText(this, "Event table empty", Toast.LENGTH_SHORT).show();
        } else {
            for (Event event : events) {
                TableRow tableRow = new TableRow(this);

                TextView eventNameTextView = new TextView(this);
                eventNameTextView.setText(event.getEventName());
                eventNameTextView.setPadding(25, 25, 25, 25);
                eventNameTextView.setGravity(Gravity.CENTER);
                eventNameTextView.setBackgroundResource(R.drawable.border_table);
                eventNameTextView.setTextAppearance(this, R.style.TableCellEventName);

                TextView venueTextView = new TextView(this);
                venueTextView.setText(String.valueOf(event.getMaxParticipant())); // Replace with real venue data if available
                venueTextView.setPadding(25, 25, 25, 25);
                venueTextView.setGravity(Gravity.CENTER);
                venueTextView.setBackgroundResource(R.drawable.border_table);
                venueTextView.setTextAppearance(this, R.style.TableCellVenue);

                TextView timeTextView = new TextView(this);
                timeTextView.setText(event.getTime()); // Replace with real time data if available
                timeTextView.setPadding(25, 25, 25, 25);
                timeTextView.setGravity(Gravity.CENTER);
                timeTextView.setBackgroundResource(R.drawable.border_table);
                timeTextView.setTextAppearance(this, R.style.TableCellTime);

                tableRow.addView(eventNameTextView);
                tableRow.addView(venueTextView);
                tableRow.addView(timeTextView);

                tableLayout.addView(tableRow);
            }
        }
        tableLayout.setVisibility(View.VISIBLE);
    }
    private void populatePoints() {
        // Example points, replace with your dynamic points
        points = new LinkedList<>();
        points.add("Maximum number of participants per team is 15.");
        points.add("It is the responsibility of the participants to avoid clashes between the events they are participating.");
        points.add("Registration fee is 200/- per participant.");
        points.add("Participant names are not changeable in app.");
        points.add("Certificate will be issued by the name you register.");
        for (String point : points) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(point);
            textView.setPadding(0, 8, 0, 8);
            multiPointLayout.addView(textView);

        }
    }
}
