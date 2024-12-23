package com.example.konserkuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaymentHistoryActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "UserSession";
    private static final String KEY_USERID = "userId";

    private RecyclerView historyRecyclerView;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        hideSystemUI();

        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(PaymentHistoryActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String userId = sharedPreferences.getString(KEY_USERID, null);

        String url = "http://10.0.2.2:4000/booking/history/" + userId;
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

                            // Set adapter
                            PaymentHistoryAdapter adapter = new PaymentHistoryAdapter(PaymentHistoryActivity.this, dataArray);
                            historyRecyclerView.setAdapter(adapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        // Handle failure
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    // Handle failure
                });
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
}