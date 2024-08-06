package com.anjacarchistra.kvm.stratafest;

import static com.anjacarchistra.kvm.stratafest.api.Constants.NAME_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ProfileActivity extends AppCompatActivity  implements  EventCallback{

    private TextView tvStudentName, tvCollegeName, tvCollegeId, tvDeptId, tvPassword;

    private SQLiteHelper databaseHelper;
    private CountDownLatch latch;
    private TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize TextViews
        tvStudentName = findViewById(R.id.tvStudentName);
        tvCollegeName = findViewById(R.id.tvCollegeName);
       // tvCollegeId = findViewById(R.id.tvCollegeId);
        tvDeptId = findViewById(R.id.tvDeptId);
        tvPassword = findViewById(R.id.tvPassword);


        tableLayout = findViewById(R.id.tableLayout);

        databaseHelper = SQLiteHelper.getInstance(this);

        latch = new CountDownLatch(1);

        // Check if the events table has records
        if (databaseHelper.getAllEvents().isEmpty()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new EventHandler(ProfileActivity.this,  ProfileActivity.this).execute();
                    try {
                        // Wait for the events to be stored
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchDataFromDatabase();
                        }
                    });

                }
            }).start();
        }else {
            fetchDataFromDatabase();
        }



    }
    @Override
    public void onSuccess(List<Event> events) {
        for (Event event : events) {
            Log.d("INSERTION",event.toString());
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

    private void fetchDataFromDatabase() {
        // Fetch values from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String studentName = sharedPreferences.getString(NAME_KEY, "");
        String collegeName = sharedPreferences.getString("collegename", "");
        String collegeId = String.valueOf(sharedPreferences.getInt("collegeid", -1)); // Use -1 as default
        String deptId = String.valueOf(sharedPreferences.getInt("deptid", -1)); // Use -1 as default
        String deptname = sharedPreferences.getString("deptname","");
        String password = sharedPreferences.getString("password", "");

// Retrieve StringSet values
        Set<String> eventIdSet = sharedPreferences.getStringSet("eventid", new LinkedHashSet<>());
        Set<String> timeSet = sharedPreferences.getStringSet("time", new LinkedHashSet<>());
        Set<String> venueSet = sharedPreferences.getStringSet("venue", new LinkedHashSet<>());
        // Get a Set of event names based on the event IDs
        Log.d("EVENTID",eventIdSet.toString());
        Set<String> setEventNames = eventIdSet.stream()
                .map(e -> databaseHelper.getEventNameById(Integer.parseInt(e)))
                .collect(Collectors.toSet());
        Log.d("EVENTNAME",setEventNames.toString());
// Convert Sets to comma-separated strings
        String eventNames = String.join(", ", setEventNames);
        String timeString = String.join(", ", timeSet);
        String venueString = String.join(", ", venueSet);

// Set values to TextViews
        tvStudentName.setText(studentName);
        tvCollegeName.setText(collegeName);
        tvDeptId.setText(deptname);
        tvPassword.setText(password);

        populateTable(eventIdSet);
    }

    private void populateTable(Set<String> eventid) {
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
                if (eventid.contains(String.valueOf(event.getEventId()))){
                TableRow tableRow = new TableRow(this);

                TextView eventNameTextView = new TextView(this);
                eventNameTextView.setText(event.getEventName());
                eventNameTextView.setPadding(25, 25, 25, 25);
                eventNameTextView.setGravity(Gravity.CENTER);
                eventNameTextView.setBackgroundResource(R.drawable.border_table);
                eventNameTextView.setTextAppearance(this, R.style.TableCellEventName);

                TextView venueTextView = new TextView(this);
                venueTextView.setText(event.getVenue());
                venueTextView.setPadding(25, 25, 25, 25);
                venueTextView.setGravity(Gravity.CENTER);
                venueTextView.setBackgroundResource(R.drawable.border_table);
                venueTextView.setTextAppearance(this, R.style.TableCellVenue);

                TextView timeTextView = new TextView(this);
                timeTextView.setText(event.getTime());
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
}
