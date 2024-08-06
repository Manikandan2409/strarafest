package com.anjacarchistra.kvm.stratafest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class EventTableActivity extends AppCompatActivity implements EventCallback {
    private TableLayout tableLayout;
    private SQLiteHelper databaseHelper;
    private CountDownLatch latch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_table);
        tableLayout = findViewById(R.id.tableLayout);

        databaseHelper = SQLiteHelper.getInstance(this);

        // Initialize the CountDownLatch with 1 as we are waiting for one thread to finish.
        latch = new CountDownLatch(1);

        // Check if the events table has records
        if (databaseHelper.getAllEvents().isEmpty()) {
            // If empty, fetch events from the API
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new EventHandler(EventTableActivity.this, EventTableActivity.this).execute();
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
                        }
                    });
                }
            }).start();
        } else {
            // If not empty, populate the table directly
            populateTable();
        }
    }

    @Override
    public void onSuccess(List<Event> events) {

        for (Event event : events) {
          //  Log.d("INSERTION",event.toString());
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
        headingVenue.setText("Venue");
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
                venueTextView.setText(event.getVenue()); // Replace with real venue data if available
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
    }
}
