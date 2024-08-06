package com.anjacarchistra.kvm.stratafest;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.anjacarchistra.kvm.stratafest.ConfirmParticipantsActivity;
import com.anjacarchistra.kvm.stratafest.R;
import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Participant;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;
import com.anjacarchistra.kvm.stratafest.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Register extends AppCompatActivity implements EventCallback {
    private static final String TAG = "REGISTER";

    private List<EditText> participantNameEditTexts = new ArrayList<>();
    // list of events
    private List<Event> events = new ArrayList<>();
    // evnt name, Participants name
    private HashMap<String, List<String>> eventParticipantsMap = new HashMap<>();
    // Swiches and eventname


    // event card
    private ArrayList<CardView> cardViewList = new ArrayList<>();

    // event name ref
    private ArrayList<TextView> eventNameTextViews = new ArrayList<>();
    //switch eventid
    private HashMap< Integer,Switch> switchEventMap = new HashMap<>();

    // event name , event Text fields
    private HashMap<String, List<EditText>> eventParticipantFieldsMap = new HashMap<>();

    private Typeface customTypeface;
    private  String[] intentvalue = new String[3];
    String[] value = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Load custom font
        //customTypeface = Typeface.createFromAsset(getAssets(), "fonts/your_custom_font.ttf");

        // Get the container layout
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // Fetch events from the server
        new EventHandler(this, this).execute();

        // Get the submit button and set its click listener
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> handleSubmit());

        // Retrieve and decode values from Intent
        Intent intent = getIntent();
        String encodedvalue = intent.getStringExtra("encodevalue");
         value = Helper.decode(encodedvalue).split(",");

        TextView collegeNameTextView = findViewById(R.id.collegeNameTextView);
        TextView deptNameTextView = findViewById(R.id.deptNameTextView);

//        collegeNameTextView.setText(value[1]);
//        deptNameTextView.setText(value[2]);
    }

    @Override
    public void onSuccess(List<Event> fetchedEvents) {
        // Populate the list of events
        events.clear();
        events.addAll(fetchedEvents);
        SQLiteHelper db = SQLiteHelper.getInstance(this);
        for (Event event : events) {
            db.addEvent(event);
        }

        // Create dynamic event views for each event
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);
        for (Event event : events) {
            createEventLayout(dynamicContainer, event);
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Failed to load events: " + error, Toast.LENGTH_LONG).show();
    }


    @SuppressLint("ResourceAsColor")
    private void createEventLayout(LinearLayout dynamicContainer, Event event) {
        // Create a CardView for the entire event layout
        CardView eventCardView = new CardView(this);
        eventCardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        eventCardView.setCardElevation(16);
        eventCardView.setContentPadding(16, 16, 16, 16);
        eventCardView.setUseCompatPadding(true);
        eventCardView.setPreventCornerOverlap(true);
        eventCardView.setRadius(60);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 16, 0, 16);
        eventCardView.setLayoutParams(cardParams);

        // Create a LinearLayout to wrap event name and switch
        LinearLayout eventHeaderLayout = new LinearLayout(this);
        eventHeaderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        eventHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
        eventHeaderLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
        eventHeaderLayout.setPadding(24, 24, 24, 24);

        // Create a TextView for the event name
        TextView eventTextView = new TextView(this);
        eventTextView.setText(event.getEventName());
        eventTextView.setTextSize(24);
        eventTextView.setAllCaps(false);
        eventTextView.setTextColor(Color.WHITE);
        eventTextView.setTypeface(null, Typeface.BOLD);
        eventTextView.setPadding(20, 20, 20, 20);
        eventTextView.setId(event.getEventId());
        eventHeaderLayout.addView(eventTextView);

        // Create a Switch for the event
        Switch eventSwitch = new Switch(this);
        eventSwitch.setChecked(true);
        eventSwitch.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightgreen)));
        eventSwitch.setTag(event.getEventName());

        LinearLayout.LayoutParams switchParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        switchParams.gravity = Gravity.END;
        eventSwitch.setPadding(20, 40, 0, 10);

        eventHeaderLayout.addView(eventSwitch, switchParams);

        // Create a LinearLayout for participant fields
        LinearLayout participantFieldsLayout = new LinearLayout(this);
        participantFieldsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        participantFieldsLayout.setOrientation(LinearLayout.VERTICAL);
        participantFieldsLayout.setPadding(16, 16, 16, 16);

        // Create participant fields dynamically
        List<EditText> participantFields = new ArrayList<>();
        for (int i = 0; i < event.getMaxParticipant(); i++) {
            TextView participantLabel = new TextView(this);
            participantLabel.setText("Participant " + (i + 1));
            participantLabel.setTextSize(16);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams.setMargins(16, 16, 16, 8);
            participantLabel.setLayoutParams(labelParams);
            participantFieldsLayout.addView(participantLabel);

            EditText nameEditText = new EditText(this);
            nameEditText.setHint("Enter Name");
            nameEditText.setId(event.getEventId() * 1000 + i * 2);
            nameEditText.setBackgroundResource(R.drawable.border);
            nameEditText.setPadding(
                    getResources().getDimensionPixelSize(R.dimen.edit_text_padding_left),
                    getResources().getDimensionPixelSize(R.dimen.edit_text_padding_top),
                    getResources().getDimensionPixelSize(R.dimen.edit_text_padding_right),
                    getResources().getDimensionPixelSize(R.dimen.edit_text_padding_bottom)
            );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 16, 16, 16);
            nameEditText.setLayoutParams(params);
            addTextWatcher(nameEditText);
            participantFieldsLayout.addView(nameEditText);
            participantNameEditTexts.add(nameEditText);
            participantFields.add(nameEditText);
        }

        // Add the eventHeaderLayout and participantFieldsLayout to the CardView
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(eventHeaderLayout);
        cardContentLayout.addView(participantFieldsLayout);

        eventCardView.addView(cardContentLayout);
        dynamicContainer.addView(eventCardView);

        // Store CardView in ArrayList
        cardViewList.add(eventCardView);

        // Update the maps
        switchEventMap.put(event.getEventId(), eventSwitch);
        eventParticipantFieldsMap.put(event.getEventName(), participantFields);

        // Disable fields if the switch is off
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<EditText> fields = eventParticipantFieldsMap.get(event.getEventName());
            if (fields != null) {
                for (EditText field : fields) {
                    field.setEnabled(isChecked);
                }
            }

            // Change the button and CardView colors based on the switch state
            if (isChecked) {
                eventSwitch.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightgreen)));
                eventCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
            } else {
                eventSwitch.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
                eventCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.disablecard));
            }
        });
    }

    private int getEventIdBySwitch(Switch eventSwitch) {
        for (Map.Entry<Integer, Switch> entry : switchEventMap.entrySet()) {
            if (entry.getValue().equals(eventSwitch)) {
                return entry.getKey();
            }
        }
        return -1; // Return -1 or handle appropriately if not found
    }

    private void addTextWatcher(EditText editText) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // No action needed after text changes
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String name = editText.getText().toString();
                if (!name.matches("[a-zA-Z\\. ]+")) {
                    Toast.makeText(Register.this, "Invalid name format"+name, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void handleSubmit() {
        // Populate eventParticipantsMap with data
        HashSet<String> uniqueNames = new HashSet<>();
        for (Event event : events) {
            Switch currentSwitch = switchEventMap.get(event.getEventId());
            if (currentSwitch != null && currentSwitch.isChecked()) {
                List<EditText> currentEventParticipants = eventParticipantFieldsMap.get(event.getEventName());
                List<String> participants = new LinkedList<>();
                if (currentEventParticipants != null) {
                    for (EditText editText : currentEventParticipants) {
                        String participant = editText.getText().toString().trim();
                        if (!participant.matches("[a-zA-Z\\. ]+")) {
                            Toast.makeText(Register.this, "Invalid Name "+participant, Toast.LENGTH_SHORT).show();
                        return;
                        }
                        uniqueNames.add(participant);
                        if (!participant.isEmpty()) {
                            participants.add(participant);
                        }

                    }
                }
                if (uniqueNames.size()>14){
                    Toast.makeText(this, "Total participants must be <=15", Toast.LENGTH_SHORT).show();
                }

                // Check if participants list is empty
                if (participants.isEmpty()) {
                    Log.d(TAG, "Event " + event.getEventName() + " has no participants.");
                    Toast.makeText(this, event.getEventName()+" have  no participant", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    eventParticipantsMap.put(event.getEventName(), participants);
                }

                Log.d(TAG,eventParticipantsMap.toString());
            }
        }// Check for duplicates in the lists
        boolean hasDuplicates = false;
        for (Map.Entry<String,List<String>> participants : eventParticipantsMap.entrySet()) {
            if (checkForDuplicates(participants.getValue())) {
                hasDuplicates = true;
                Toast.makeText(this, participants.getKey()+" have duplicate participant", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (hasDuplicates) {
            // Handle the case where there are duplicates
            Log.d(TAG, "The list contains duplicates.");
        } else {
            // Convert the map to a JSON string
            String jsonString = convertEventParticipantsMapToJson();
            Log.d(TAG, "JSON String: " + jsonString);

            // Example of splitting a comma-separated string back into an array
            JSONObject json;
            try {
                json = new JSONObject();
                JSONObject eventdetailjson = new JSONObject(jsonString);
                for (Iterator<String> it = eventdetailjson.keys(); it.hasNext(); ) {
                    String eventName = it.next();
                    String participantsString = eventdetailjson.getString(eventName);
                    String[] participantsArray = participantsString.split(",");


                    // Print the resulting array
                    for (String participant : participantsArray) {
                        System.out.println(participant);
                    }

                    json.put("teamid",value[0]);
                    json.put("eventdetails",eventdetailjson);
                    json.put("count",uniqueNames.size());
                    //Log.d(TAG, String.valueOf(json));
                    Intent i = new Intent(Register.this,ConfirmParticipantsActivity.class);
                    i.putExtra("registeredvalue",json.toString());
                    new AlertDialog.Builder(this)
                            .setTitle("Message")
                            .setMessage("Confirm Participants ??")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private boolean checkForDuplicates(List<String> list) {
        Set<String> set = new HashSet<>();
        for (String item : list) {
            if (!set.add(item)) {
                // If add returns false, the item is already in the set
                return true;
            }
        }
        return false;
    }

    private String convertEventParticipantsMapToJson() {
        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, List<String>> entry : eventParticipantsMap.entrySet()) {
            String eventName = entry.getKey();
            String participantsString = String.join(",", entry.getValue());

            try {
                jsonObject.put(eventName, participantsString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject.toString();
    }

}


