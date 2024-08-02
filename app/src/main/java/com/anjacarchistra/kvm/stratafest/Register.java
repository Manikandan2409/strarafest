package com.anjacarchistra.kvm.stratafest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Participant;
import com.anjacarchistra.kvm.stratafest.util.EventParticipantsWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    private static final String  TAG="REGISTER";
    private List<EditText> participantNameEditTexts = new ArrayList<>();
    private List<EditText> participantEmailEditTexts = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private HashMap<String, List<Participant>> eventParticipantsMap = new HashMap<>();
    private HashMap<Switch, Integer> switchEventMap = new HashMap<>();
    private HashMap<Integer, List<EditText>> eventParticipantFieldsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Populate the list of events
        events.add(new Event(1, "Event 1", 3, 2));
        events.add(new Event(2, "Event 2", 2, 1));
        events.add(new Event(3, "Event 3", 4, 3));

        // Get the container layout
        LinearLayout dynamicContainer = findViewById(R.id.dynamicContainer);

        // Create dynamic layouts for each event
        for (Event event : events) {
            createEventLayout(dynamicContainer, event);
        }

        // Get the submit button and set its click listener
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void createEventLayout(LinearLayout dynamicContainer, Event event) {
        // Create a RelativeLayout for the event name and switch
        RelativeLayout eventLayout = new RelativeLayout(this);
        eventLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a TextView for the event name
        TextView eventTextView = new TextView(this);
        eventTextView.setText(event.getEventName());
        eventTextView.setTextSize(18);
        eventTextView.setPadding(0, 0, 0, 16);
        eventTextView.setId(event.getEventId());  // Set the ID to the event ID
        eventLayout.addView(eventTextView);

        // Create a Switch for the event
        Switch eventSwitch = new Switch(this);
        eventSwitch.setChecked(true); // Default is ON
        eventSwitch.setTag(event.getEventName());  // Set the tag to the event name

        RelativeLayout.LayoutParams switchParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        switchParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        eventLayout.addView(eventSwitch, switchParams);

        dynamicContainer.addView(eventLayout);

        switchEventMap.put(eventSwitch, event.getEventId());

        // Create participant fields dynamically
        List<EditText> participantFields = new ArrayList<>();
        for (int i = 0; i < event.getMaxParticipant(); i++) {
            // Create a TextView for the participant label
            TextView participantLabel = new TextView(this);
            participantLabel.setText("Participant " + (i + 1));
            participantLabel.setTextSize(16);
            dynamicContainer.addView(participantLabel);

            // Create an EditText for the participant name
            EditText nameEditText = new EditText(this);
            nameEditText.setHint("Enter Name");
            nameEditText.setId(event.getEventId() * 1000 + i * 2);  // Unique ID
            nameEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            addTextWatcher(nameEditText);
            dynamicContainer.addView(nameEditText);
            participantNameEditTexts.add(nameEditText);
            participantFields.add(nameEditText);

            // Create an EditText for the participant email
            EditText emailEditText = new EditText(this);
            emailEditText.setHint("Enter Email");
            emailEditText.setId(event.getEventId() * 1000 + i * 2 + 1);  // Unique ID
            emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            emailEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            addTextWatcher(emailEditText);
            dynamicContainer.addView(emailEditText);
            participantEmailEditTexts.add(emailEditText);
            participantFields.add(emailEditText);
        }

        eventParticipantFieldsMap.put(event.getEventId(), participantFields);

        // Set a listener on the switch to enable/disable EditTexts
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 0; i < event.getMaxParticipant(); i++) {
                participantFields.get(i * 2).setEnabled(isChecked);
                participantFields.get(i * 2 + 1).setEnabled(isChecked);
            }

            // If switch is ON, ensure minimum participant count is met
            if (isChecked) {
                for (int i = 0; i < event.getMinParticipant(); i++) {
                    participantFields.get(i * 2).setEnabled(true);
                    participantFields.get(i * 2 + 1).setEnabled(true);
                }
            }
        });

        // Ensure minimum participant count is enabled initially
        for (int i = 0; i < event.getMinParticipant(); i++) {
            participantFields.get(i * 2).setEnabled(true);
            participantFields.get(i * 2 + 1).setEnabled(true);
        }
    }

    private void addTextWatcher(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = ((EditText) v).getText().toString();
                if (text.isEmpty()) {
                    ((EditText) v).setError("Field cannot be empty");
                } else if (((EditText) v).getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS && !isValidEmail(text)) {
                    ((EditText) v).setError("Invalid email address");
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void handleSubmit() {
        eventParticipantsMap.clear(); // Clear previous data
        for (Event event : events) {
            List<Participant> participants = new ArrayList<>();
            List<EditText> participantFields = eventParticipantFieldsMap.get(event.getEventId());

            // Check if switch is disabled and skip the event if it is
            if (switchEventMap.keySet().stream().anyMatch(sw -> sw.getTag().equals(event.getEventName()) && !sw.isChecked())) {
                continue;
            }

            if (participantFields != null) {
                for (int i = 0; i < event.getMaxParticipant(); i++) {
                    EditText nameEditText = participantFields.get(i * 2);
                    EditText emailEditText = participantFields.get(i * 2 + 1);

                    if (nameEditText.isEnabled()) {
                        String name = nameEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        if (!name.isEmpty() && isValidEmail(email)) {
                            participants.add(new Participant(name, email));
                        }
                    }
                }
            }
            eventParticipantsMap.put(event.getEventName(), participants);
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, List<EditText>> entry : eventParticipantFieldsMap.entrySet()) {
            Integer eventName = entry.getKey();
            List<EditText> participants = entry.getValue();

            sb.append("Event: ").append(eventName).append("\n");
            for (EditText participant : participants) {
                sb.append(" - ").append(participant.toString()).append("\n");
            }
        }Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        // Pass the data to another activity
        Intent intent = new Intent(this, ConfirmParticipantsActivity.class);
        EventParticipantsWrapper wrapper = new EventParticipantsWrapper(eventParticipantsMap);
        intent.putExtra("eventParticipantsWrapper", wrapper);
        startActivity(intent);

    }

}
