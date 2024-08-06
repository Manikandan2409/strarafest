package com.anjacarchistra.kvm.stratafest;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.api.ResultHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Result;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.handler.ResultCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResultActivity extends AppCompatActivity implements EventCallback, ResultCallback {
    private TableLayout tableEvents;
    private TableLayout tableSelectedMembers;
    private SQLiteHelper databaseHelper;
    private List<Result> results = new ArrayList<>();
    private boolean isEventHandlerComplete = false;
    private boolean isResultHandlerComplete = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tableEvents = findViewById(R.id.tableEvents);
        tableSelectedMembers = findViewById(R.id.tableSelectedMembers);
        databaseHelper = SQLiteHelper.getInstance(this);

        if (databaseHelper.getAllEvents().isEmpty()) {
            new EventHandler(ResultActivity.this, ResultActivity.this).execute();
        }

        new ResultHandler(ResultActivity.this, ResultActivity.this).execute();
    }

    @Override
    public void onSuccess(List<Event> events) {
        for (Event e : events) {
            databaseHelper.addEvent(e);
        }
        isEventHandlerComplete = true;
        checkAndPopulateTables();
        runOnUiThread(() -> Toast.makeText(this, "Event added to DB", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onResultsSuccess(List<Result> results) {
        Log.d("RESULTS SUCESS",results.toString());
        for (Result r : results) {

            this.results.add(r);
        Log.d("RESULTS FROM SUCESS",r.toString());
        Log.d("FULL RESULTS CHANGED",this.results.toString());
        }

        isResultHandlerComplete = true;
        Log.d("CALLING UI","UI STARTS");
        checkAndPopulateTables();
        runOnUiThread(() -> Toast.makeText(this, "Results added", Toast.LENGTH_SHORT).show());
    }

    private void checkAndPopulateTables() {
        Log.d("PASSING","POPULATE TABLE");
            populateEventsTable(this.results);

    }

    private void populateEventsTable(List<Result> results) {
        tableEvents.removeAllViews();
        Log.d("POPULATE","CREATING HEADER");
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        headerRow.setPadding(5, 5, 5, 5);

        headerRow.addView(createTableHeader("Event ID"));
        headerRow.addView(createTableHeader("Event Name"));
        headerRow.addView(createTableHeader("Prelims"));
        headerRow.addView(createTableHeader("Finals"));
        tableEvents.addView(headerRow);

        Log.d("POPULATE","ROWS");
        Log.d("RESULTS",results.toString());
        for (Result result : this.results) {
            Log.d("USING RESULTS",result.toString());
            TableRow tableRow = new TableRow(this);

            TextView eventIdTextView = new TextView(this);
            eventIdTextView.setText(String.valueOf(result.getEventid()));
            eventIdTextView.setPadding(30, 32, 30, 31);
            eventIdTextView.setGravity(Gravity.CENTER);
            eventIdTextView.setBackgroundResource(R.drawable.border_table);
            eventIdTextView.setTextAppearance(this, R.style.TableCellEventName);

            TextView eventNameTextView = new TextView(this);
            eventNameTextView.setText(result.getEventname());
            eventNameTextView.setPadding(30, 32, 30, 31);
            eventNameTextView.setGravity(Gravity.CENTER);
            eventNameTextView.setBackgroundResource(R.drawable.border_table);
            eventNameTextView.setTextAppearance(this, R.style.TableCellEventName);

            ImageButton prelimsImageButton = createImageButton(result.getEventid(), "prelims", result.getPrelims(), R.drawable.baseline_visibility_off_24, R.drawable.baseline_view_list_24);
            ImageButton winnerImageButton = createImageButton(result.getEventid(), "finals", result.getFinals(), R.drawable.baseline_visibility_off_24, R.drawable.baseline_view_list_24);

            tableRow.addView(eventIdTextView);
            tableRow.addView(eventNameTextView);
            tableRow.addView(prelimsImageButton);
            tableRow.addView(winnerImageButton);
            tableEvents.addView(tableRow);
        }
    }

    private TextView createTableHeader(String text) {
        TextView headerTextView = new TextView(this);
        headerTextView.setText(text);
        headerTextView.setPadding(30, 32, 30, 31);
        headerTextView.setTextColor(Color.BLACK);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setTextAppearance(this, R.style.TableCell);
        return headerTextView;
    }

    private ImageButton createImageButton(int id, String creation, Boolean condition, int falseResource, int trueResource) {
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(condition ? trueResource : falseResource);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setPadding(30, 15, 30, 15);
        imageButton.setBackgroundResource(R.drawable.border_table);
        imageButton.setTag(id);
        imageButton.setEnabled(condition);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer eventId = (Integer) v.getTag();

                for (Result result : results) {
                    if (result.getEventid() == eventId) {
                        tableSelectedMembers.removeAllViews();

                        Set<String> val = creation.equals("prelims") ? result.getSelectedSet() : result.getWinners();
                        createMemberTable(val);
                        break;
                    }
                }
            }
        });
        return imageButton;
    }

    private void createMemberTable(Set<String> val) {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        headerRow.setPadding(5, 5, 5, 5);

        headerRow.addView(createTableHeader("LotID"));
        headerRow.addView(createTableHeader("Lots"));
        tableSelectedMembers.addView(headerRow);

        int i = 1;
        for (String lot : val) {
            TableRow tableRow = new TableRow(this);

            TextView lotIdTextView = new TextView(this);
            lotIdTextView.setText(String.valueOf(i++));
            lotIdTextView.setPadding(30, 32, 30, 31);
            lotIdTextView.setGravity(Gravity.CENTER);
            lotIdTextView.setBackgroundResource(R.drawable.border_table);
            lotIdTextView.setTextAppearance(this, R.style.TableCellEventName);

            TextView lotNameTextView = new TextView(this);
            lotNameTextView.setText(lot);
            lotNameTextView.setPadding(30, 32, 30, 31);
            lotNameTextView.setGravity(Gravity.CENTER);
            lotNameTextView.setBackgroundResource(R.drawable.border_table);
            lotNameTextView.setTextAppearance(this, R.style.TableCellEventName);

            tableRow.addView(lotIdTextView);
            tableRow.addView(lotNameTextView);
            tableSelectedMembers.addView(tableRow);
            Log.d("SELECT MEMBERS", lot);
        }

        tableSelectedMembers.setVisibility(View.VISIBLE);
    }
}
