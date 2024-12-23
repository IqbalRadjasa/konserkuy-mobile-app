package com.example.konserkuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
        Button registerBtn, backBtn;

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        usernameInput = findViewById(R.id.username);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirmPassword);

        registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(v -> {
            String usernameValue = usernameInput.getText().toString().trim();
            String emailValue = emailInput.getText().toString().trim();
            String passwordValue = passwordInput.getText().toString().trim();
            String confirmPasswordValue = confirmPasswordInput.getText().toString().trim();

            if (!passwordValue.equals(confirmPasswordValue)) {
                runOnUiThread(() -> showAlert("Password Mismatch", "The passwords do not match. Please try again.", ""));
                return;
            }
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", usernameValue);
                jsonBody.put("email", emailValue);
                jsonBody.put("password", passwordValue);
                String jsonToString = jsonBody.toString();

                String url = "http://10.0.2.2:4000/users/";

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
        new android.app.AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (isSuccess != "" && isSuccess == "true") {
                        // Redirect to LoginActivity after success
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

}