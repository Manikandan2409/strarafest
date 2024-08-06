package com.anjacarchistra.kvm.stratafest;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.anjacarchistra.kvm.stratafest.api.RegisterHandler;
import com.anjacarchistra.kvm.stratafest.api.RegistrationLotHandler;
import com.anjacarchistra.kvm.stratafest.dto.Lot;
import com.anjacarchistra.kvm.stratafest.handler.LotCallback;
import com.anjacarchistra.kvm.stratafest.handler.RegisterCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfirmParticipantsActivity extends AppCompatActivity implements LotCallback, RegisterCallback {
    private static final String TAG = "CONFIRMPARTICIPANTS";
    private TableLayout tableLayout;
    private EditText teamEmail, teamPassword, teamPhoneNumber;
    private Spinner lotSpinner;
    private ImageView teamImage;
    private Button submitButton;
    private List<Lot> lots;
    private JSONObject jsonObject;
    private Lot selectedLot;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_participants);

        tableLayout = findViewById(R.id.tableLayout);
        teamEmail = findViewById(R.id.teamEmail);
        teamPassword = findViewById(R.id.teamPassword);
        teamPhoneNumber = findViewById(R.id.teamPhoneNumber);
        lotSpinner = findViewById(R.id.lotSpinner);
        teamImage = findViewById(R.id.teamImage);
        submitButton = findViewById(R.id.submitButton);
        teamEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = teamEmail.getText().toString();
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                    Toast.makeText(ConfirmParticipantsActivity.this, "Mobile Number must have 10 numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
        teamPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String pass = teamPhoneNumber.getText().toString();
                if (!pass.matches("^[0-9]{10}$")) {
                    Toast.makeText(ConfirmParticipantsActivity.this, "Mobile Number must have 10 numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView upiTextView = findViewById(R.id.upiTextView);
        upiTextView.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("UPI ID", "bmanikandan24092004@oksbi");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "UPI ID copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        TextView joinWhatsAppGroup = findViewById(R.id.joinWhatsAppGroup);
        TextView sendPaymentScreenshot = findViewById(R.id.sendPaymentScreenshot);

        joinWhatsAppGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the URL with your WhatsApp group link
                String url = "https://chat.whatsapp.com/HTikqezQ8au0cSz8WJCthY";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        sendPaymentScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitButton.setVisibility(View.VISIBLE);
                // Replace the phone number with the WhatsApp number to send the payment screenshot
                String url = "https://api.whatsapp.com/send?phone=8148645640";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });




        String jsonData = getIntent().getStringExtra("registeredvalue");
        if (jsonData != null) {
            try {
                jsonObject = new JSONObject(jsonData);
                loadParticipantsFromJson(jsonObject);
                new RegistrationLotHandler(this, this).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                JSONObject jsonObject = createJsonObject();
                if (jsonObject != null) {
                    Log.d("SubmitButton", "Creating RegisterHandler with JSON: " + jsonObject.toString());
                    new RegisterHandler(this, this, jsonObject).execute();
                } else {
                    Log.d("SubmitButton", "createJsonObject returned null");
                    Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("SubmitButton", "validateInputs returned false");
                Toast.makeText(this, "Invalid inputs", Toast.LENGTH_SHORT).show();
            }
        });

        lotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLotName = (String) parent.getItemAtPosition(position);
                selectedLot = lots.stream()
                        .filter(lot -> lot.getLotName().equals(selectedLotName))
                        .findFirst()
                        .orElse(null);

                if (selectedLot != null) {
                    Log.d("Selected Lot", "ID: " + selectedLot.getLotId() + ", Name: " + selectedLot.getLotName());
                    Toast.makeText(ConfirmParticipantsActivity.this, selectedLot.getLotId(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Selected Lot", "No matching Lot found");
                    Toast.makeText(ConfirmParticipantsActivity.this, "No matching Lot found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected (optional)
            }
        });

    }

    @Override
    public void onSuccess(String msg) {
        showAlert(msg);
    }

    @Override
    public void onError(String error) {
        showAlert(error);
    }
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Message")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ConfirmParticipantsActivity.this,MainActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Set the selected image preview
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadParticipantsFromJson(JSONObject jsonObject) throws JSONException {
        JSONObject eventDetails = jsonObject.getJSONObject("eventdetails");

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Clear existing rows except for the header
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        for (Iterator<String> it = eventDetails.keys(); it.hasNext(); ) {
            String eventName = it.next();
            String participantNames = eventDetails.getString(eventName);

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            TextView eventNameTextView = new TextView(this);
            eventNameTextView.setText(eventName);
            eventNameTextView.setPadding(20, 20, 20, 20);
            eventNameTextView.setGravity(Gravity.CENTER); // Center the text
            eventNameTextView.setTextColor(Color.BLACK);
            eventNameTextView.setTextSize(16);
            eventNameTextView.setBackgroundResource(R.drawable.cell_border); // Set cell border
            row.addView(eventNameTextView);

            TextView participantNameTextView = new TextView(this);
            participantNameTextView.setText(participantNames);
            participantNameTextView.setPadding(20, 20, 20, 20);
            participantNameTextView.setTextColor(Color.BLACK);
            participantNameTextView.setTextSize(16);
            participantNameTextView.setGravity(Gravity.CENTER); // Center the text
            participantNameTextView.setBackgroundResource(R.drawable.cell_border); // Set cell border
            row.addView(participantNameTextView);

            tableLayout.addView(row);
        }
    }



    @Override
    public void onSucceed(List<Lot> lots) {
        this.lots = lots;
        Log.d(TAG, lots.toString());
        populateSpinner(lots);
    }

    @Override
    public void onProblem(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void populateSpinner(List<Lot> lots) {
        SQLiteHelper dbHelper = SQLiteHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<String> lotNames = new ArrayList<>();
        for (Lot lot : lots) {
            lotNames.add(lot.getLotName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lotNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lotSpinner.setAdapter(adapter);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(teamEmail.getText())) {
            teamEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(teamPassword.getText())) {
            teamPassword.setError("Password is required");
            return false;
        }

        if (TextUtils.isEmpty(teamPhoneNumber.getText())) {
            teamPhoneNumber.setError("Phone number is required");
            return false;
        }

        if (lotSpinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a lot", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private JSONObject createJsonObject() {
        try {
            String teamId = jsonObject.getString("teamid"); // Assuming teamId is in the original JSON
            Toast.makeText(this, teamId, Toast.LENGTH_SHORT).show();
            String phone=teamPhoneNumber.getText().toString().trim();

                String email = teamEmail.getText().toString();
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                    Toast.makeText(ConfirmParticipantsActivity.this, "Mobile Number must have 10 numbers", Toast.LENGTH_SHORT).show();
                }

            if (!phone.matches("^[0-9]{10}$")) {
                Toast.makeText(ConfirmParticipantsActivity.this, "Mobile number must have 10 number", Toast.LENGTH_SHORT).show();
                return null;
            }
            jsonObject.put("teamemail", teamEmail.getText().toString());
            jsonObject.put("password", teamPassword.getText().toString());
            jsonObject.put("phoneno", teamPhoneNumber.getText().toString());

            if (selectedLot != null) {
                jsonObject.put("lotid", selectedLot.getLotId());
            } else {
                Toast.makeText(this, "Must choose onr lot", Toast.LENGTH_SHORT).show();
                return null;
                //jsonObject.put("lotid", "");

            }

            Log.d(TAG,jsonObject.toString());

            Toast.makeText(this, "create method"+jsonObject.toString(), Toast.LENGTH_SHORT).show();
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



}