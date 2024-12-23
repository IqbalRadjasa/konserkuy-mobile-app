package com.example.konserkuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "UserSession";
    private static final String KEY_USERID = "userId";

    private static final String KEY_USERNAME = "username";

    private EditText usernameInput, emailInput, addressInput, subdistrictInput, urban_villageInput;
    private Button updateBtn, historyBtn;
    private ImageView profileBtn, homeBtn, concertBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        hideSystemUI();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(KEY_USERID, null);

        fetchUserData(userId);

        // Logout
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        // End Logout

        // Navigation
        homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        profileBtn = findViewById(R.id.profileButton);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        concertBtn = findViewById(R.id.concertButton);
        concertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, ConcertActivity.class);
                startActivity(intent);
            }
        });

        historyBtn = findViewById(R.id.historyButton);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);
            }
        });
        // End Navigation

        updateBtn = findViewById(R.id.updateButton);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addressInput = findViewById(R.id.address);
                subdistrictInput = findViewById(R.id.subdistrict);
                urban_villageInput = findViewById(R.id.urban_village);

                String addressValue = addressInput.getText().toString().trim();
                String subdistrictValue = subdistrictInput.getText().toString().trim();
                String urbanVillageValue = urban_villageInput.getText().toString().trim();

                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("address", addressValue);
                    jsonBody.put("subdistrict", subdistrictValue);
                    jsonBody.put("urban_village", urbanVillageValue);
                    String jsonToString = jsonBody.toString();

                    String url = "http://10.0.2.2:4000/users/" + userId;

                    ApiHelper apiHelper = new ApiHelper();
                    apiHelper.makeApiCall(url, "patch", jsonToString, new Callback() {

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
    //                runOnUiThread(() -> textView.setText("Failed to fetch user data: " + e.getMessage()));
                        }
                    });
                }catch (Exception e){

                }
            }
        });

    }

    private void fetchUserData(String userId) {
        String url = "http://10.0.2.2:4000/users/" + userId;

        ApiHelper apiHelper = new ApiHelper();
        apiHelper.makeApiCall(url, "get", null, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            JSONObject userObject = dataArray.getJSONObject(0);

                            String username = userObject.getString("username");
                            String email = userObject.getString("email");
                            String address = userObject.getString("address");
                            String subdistrict = userObject.getString("subdistrict");
                            String urban_village = userObject.getString("urban_village");

                            usernameInput = findViewById(R.id.username);
                            emailInput = findViewById(R.id.email);
                            addressInput = findViewById(R.id.address);
                            subdistrictInput = findViewById(R.id.subdistrict);
                            urban_villageInput = findViewById(R.id.urban_village);

                            usernameInput.setText(username);
                            emailInput.setText(email);
                            addressInput.setText(address != null ? address : "");
                            subdistrictInput.setText(subdistrict != null ? subdistrict : "");
                            urban_villageInput.setText(urban_village != null ? urban_village : "");
                        } catch (Exception e) {
//                            textView.setText("Error parsing API response.");
                        }
                    });
                } else {
//                    runOnUiThread(() -> textView.setText("Failed to fetch user data. Code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() -> textView.setText("Failed to fetch user data: " + e.getMessage()));
            }
        });
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
    private void logout() {
        // Clear the session from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears all data in SharedPreferences
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish HomeActivity to prevent back navigation
    }
    private void showAlert(String title, String message, String isSuccess) {
        new android.app.AlertDialog.Builder(ProfileActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (isSuccess != "" && isSuccess == "true") {
                        // Redirect to LoginActivity after success
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}