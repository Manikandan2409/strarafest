package com.anjacarchistra.kvm.stratafest;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.anjacarchistra.kvm.stratafest.dto.Participant;
import com.anjacarchistra.kvm.stratafest.util.EventParticipantsWrapper;

import java.util.HashMap;
import java.util.List;

public class ConfirmParticipantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_participants);

        // Get the container layout
        LinearLayout container = findViewById(R.id.container);

        // Get the passed data
        EventParticipantsWrapper wrapper = getIntent().getParcelableExtra("eventParticipantsWrapper");
        HashMap<String, List<Participant>> eventParticipantsMap = wrapper != null ? wrapper.getEventParticipantsMap() : null;

        // Display the event participants
        if (eventParticipantsMap != null) {
            for (String eventName : eventParticipantsMap.keySet()) {
                TextView eventTextView = new TextView(this);
                eventTextView.setText("Event: " + eventName);
                eventTextView.setTextSize(18);
                container.addView(eventTextView);

                List<Participant> participants = eventParticipantsMap.get(eventName);
                for (Participant participant : participants) {
                    TextView participantTextView = new TextView(this);
                    participantTextView.setText(participant.toString());
                    container.addView(participantTextView);
                }
            }
        }
    }
}
