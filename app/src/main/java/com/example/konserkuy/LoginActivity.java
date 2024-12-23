package com.example.konserkuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERID = "userId";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Redirect to HomeActivity if already logged in
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        EditText usernameInput, passwordInput;
        TextView loginBtn, registerBtn;

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);

        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(v -> {
            String usernameValue = usernameInput.getText().toString().trim();
            String passwordValue = passwordInput.getText().toString().trim();

//            if (!passwordValue.equals(confirmPasswordValue)) {
//                runOnUiThread(() -> showAlert("Password Mismatch", "The passwords do not match. Please try again.", ""));
//                return;
//            }
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", usernameValue);
                jsonBody.put("password", passwordValue);
                String jsonToString = jsonBody.toString();

                String url = "http://10.0.2.2:4000/users/Auth";

                ApiHelper apiHelper = new ApiHelper();
                apiHelper.makeApiCall(url, "post", jsonToString, new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();

                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                JSONArray dataArray = jsonResponse.getJSONArray("data");
                                JSONObject userObject = dataArray.getJSONObject(0);

                                int userId = userObject.getInt("id");
                                String username = userObject.getString("username");

                                saveSession(userId, username);
                                runOnUiThread(() -> showAlert("Login Successful",
                                        null, "true"));
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

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAlert(String title, String message, String isSuccess) {
        new android.app.AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (isSuccess != "" && isSuccess == "true") {
                        // Redirect to LoginActivity after success
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void saveSession(int userId, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USERID, String.valueOf(userId));
        editor.apply();
    }

}