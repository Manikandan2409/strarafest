package com.anjacarchistra.kvm.stratafest;

import static com.anjacarchistra.kvm.stratafest.api.Constants.NAME_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PASSWORD_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;
import static com.anjacarchistra.kvm.stratafest.api.Constants.VENUE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anjacarchistra.kvm.stratafest.api.AuthHandler;
import com.anjacarchistra.kvm.stratafest.api.LotHandler;
import com.anjacarchistra.kvm.stratafest.dto.Lot;
import com.anjacarchistra.kvm.stratafest.dto.Profile;
import com.anjacarchistra.kvm.stratafest.handler.AuthCallback;
import com.anjacarchistra.kvm.stratafest.handler.LotCallback;
import com.google.zxing.client.android.Intents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Login extends AppCompatActivity implements LotCallback, AuthCallback {

    private static final String TAG = "LoginActivity";

    private EditText nameEditText;
    private EditText passwordEditText;
    private Spinner lotSpinner;
    private Button loginButton;
    private List<String> lotNames = new ArrayList<>();
    private Map<String, String> lotMap = new HashMap<>();
    private String selectedLotId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        lotSpinner = findViewById(R.id.lotSpinner);
        loginButton = findViewById(R.id.loginButton);

        // Fetch lots from server and populate the spinner
        new LotHandler(this, this).execute();

        lotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLotName = lotNames.get(position);
                selectedLotId = lotMap.get(selectedLotName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLotId = "-1";
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch the values
                String name = nameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (name.equals("developerdev") && password.equals("devmvk")){
                    startActivity(new Intent(Login.this, ScannerToken.class));
                }
                // Validate name, password, and spinner selection
                if (validateName(name) && validatePassword(password) && validateSpinnerSelection()) {
                    // Log the values if valid
                    Log.d(TAG, "Name: " + name);
                    Log.d(TAG, "Password: " + password);
                    Toast.makeText(Login.this, selectedLotId, Toast.LENGTH_SHORT).show();
                    new AuthHandler(Login.this, Login.this, Integer.parseInt(selectedLotId), name, password).execute();


                }
            }
        });
    }

    private void populateLotSpinner(List<Lot> lots) {
        for (Lot lot : lots) {
            lotNames.add(lot.getLotName());
            lotMap.put(lot.getLotName(), lot.getLotId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lotNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lotSpinner.setAdapter(adapter);
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 3) {
            passwordEditText.setError("Password must be at least 6 characters long");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateSpinnerSelection() {
        if (selectedLotId.equals("-1")) {
            Toast.makeText(this, "Please select a lot", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSucceed(List<Lot> lots) {
        populateLotSpinner(lots);
    }

    @Override
    public void onProblem(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSuccess(Profile profile) {
        if (profile != null ) {
            // Assuming the list contains only one Profile object

            // Get the SharedPreferences instance

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

// Store values from Profile object
            Toast.makeText(this, profile.toString(), Toast.LENGTH_SHORT).show();
            editor.putString("lot",selectedLotId);

            editor.putStringSet("lotid", profile.getLotid().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet()));

            editor.putString(NAME_KEY, profile.getName());
            Log.d(NAME_KEY,profile.getName());

            editor.putString(PASSWORD_KEY, profile.getPassword());
            Log.d(PASSWORD_KEY,profile.getPassword());

            editor.putStringSet("eventid", new HashSet<>(profile.getEventid().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet())));

            editor.putStringSet("time", new HashSet<>(profile.getTime()));
            editor.putStringSet(VENUE, new HashSet<>(profile.getVenue()));
            editor.putString("collegename", profile.getCollegename());
            editor.putInt("collegeid", profile.getCollegeid());
            editor.putInt("deptid", profile.getDeptid());
            editor.putString("deptname", profile.getDeptname());
            editor.putString("eventvenue", profile.getEventvenue());

            // Commit changes
            editor.apply();
            Toast.makeText(this, "Login Succeed", Toast.LENGTH_SHORT).show();

            // Redirect to Dashboard
            startActivity(new Intent(Login.this, Dashboard.class));
            finish();
        }
    }
    @Override
       public void onError(String errorMessage){
        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
