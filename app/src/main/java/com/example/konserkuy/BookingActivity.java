package com.example.konserkuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookingActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "UserSession";
    private static final String KEY_USERID = "userId";

    private Button backBtn, bookBtn;
    private EditText fullNameInput, phoneNumberInput, idCardInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        hideSystemUI();

        // Navigation
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(BookingActivity.this, ConcertActivity .class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Intent intent = getIntent();

        String userId = sharedPreferences.getString(KEY_USERID, null);
        String concertId = intent.getStringExtra("concertId");
        fullNameInput = findViewById(R.id.fullname);
        phoneNumberInput = findViewById(R.id.phone_number);
        idCardInput = findViewById(R.id.idCard);


        bookBtn = findViewById(R.id.bookButton);
        bookBtn.setOnClickListener(v -> {
            String fullnameValue = fullNameInput.getText().toString().trim();
            String phonenumberValue = phoneNumberInput.getText().toString().trim();
            String idcardValue = idCardInput.getText().toString().trim();

            if (fullnameValue.equals("") || phonenumberValue.equals("") || idcardValue.equals("")) {
                runOnUiThread(() -> showAlert("Something wrong", "Please fill up the form.", "true"));
                return;
            }

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("concert_id", concertId);
                jsonBody.put("fullname", fullnameValue);
                jsonBody.put("phone_number", phonenumberValue);
                jsonBody.put("id_card", idcardValue);
                String jsonToString = jsonBody.toString();

                String url = "http://10.0.2.2:4000/booking/";

                ApiHelper apiHelper = new ApiHelper();
                apiHelper.makeApiCall(url, "post", jsonToString, new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();

                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                int status = jsonResponse.getInt("status");
                                String message = jsonResponse.getString("message");

                                runOnUiThread(() -> showAlert("Request Successful",
                                        "Message: " + message + "\n\n", "true"));
                            } catch (Exception e) {
                                Log.e("RegisterActivity", "Error parsing JSON response", e);
                                runOnUiThread(() -> showAlert("Error", "Failed to parse response", "false"));
                            }
                        } else {
                            Log.e("API_ERROR", "Request failed with code: " + response.code());
                            runOnUiThread(() -> showAlert("Request Failed", "Failed with code: " + response.code(), "false"));
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("API_ERROR", "Request failed", e);
//                runOnUiThread(() -> textView.setText("Request Failed: " + e.getMessage()));
                    }
                });
            } catch (Exception e) {
                Log.e("RegisterActivity", "Error creating JSON object", e);
            }
        });

    }

    private void showAlert(String title, String message, String isSuccess) {
        new android.app.AlertDialog.Builder(BookingActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (isSuccess != "" && isSuccess == "true") {
                        // Redirect to LoginActivity after success
                        Intent intent = new Intent(BookingActivity.this, ConcertActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}