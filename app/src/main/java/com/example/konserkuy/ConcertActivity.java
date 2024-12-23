package com.example.konserkuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConcertActivity extends AppCompatActivity {

    private RecyclerView concertRecyclerView;
    private ImageView profileBtn, homeBtn, concertBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert);

        hideSystemUI();


        profileBtn = findViewById(R.id.profileButton);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ConcertActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ConcertActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        concertBtn = findViewById(R.id.concertButton);
        concertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(ConcertActivity.this, ConcertActivity.class);
                startActivity(intent);
            }
        });

        concertRecyclerView = findViewById(R.id.concertRecyclerView);
        concertRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String url = "http://10.0.2.2:4000/concert";
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
                            ConcertAdapter adapter = new ConcertAdapter(ConcertActivity.this, dataArray);
                            concertRecyclerView.setAdapter(adapter);

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
