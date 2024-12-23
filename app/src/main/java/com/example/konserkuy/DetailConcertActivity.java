package com.example.konserkuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailConcertActivity extends AppCompatActivity {

    private TextView concertNameText, concertDescText;
    private ImageView concertImg;
    private Button backBtn, bookBtn;
    private String concertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_concert);

        hideSystemUI();

        // Navigation
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ProfileActivity
                Intent intent = new Intent(DetailConcertActivity.this, ConcertActivity.class);
                startActivity(intent);
            }
        });

        // End Navigation

        Intent intent = getIntent();

        concertId = intent.getStringExtra("concertId");

        String url = "http://10.0.2.2:4000/concert/" + concertId;

        ApiHelper apiHelper = new ApiHelper();
        apiHelper.makeApiCall(url, "get", null, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseBody);
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            JSONObject object = dataArray.getJSONObject(0);

                            String concertNamerRes = object.getString("name");
                            String concertDescriptionRes = object.getString("description");
                            String thumbnail = object.getString("thumbnail");

                            concertImg = findViewById(R.id.concertImage);
                            concertNameText = findViewById(R.id.concertName);
                            concertDescText = findViewById(R.id.concertDescription);

                            concertNameText.setText(concertNamerRes);
                            concertDescText.setText(concertDescriptionRes);

                            // Get thumnail asset
                            String imageUrl = "http://10.0.2.2:4000/assets/" + thumbnail;

                            ApiHelper apiHelper = new ApiHelper();
                            apiHelper.makeApiCall(imageUrl, "get", null, new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        runOnUiThread(() -> {
                                            try {
                                                Picasso.get().load(imageUrl)
                                                        .resize(800, 600).into(concertImg);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    } else {
            //                        activity.runOnUiThread(() -> {
            //                            // Handle failure to fetch image
            //                            holder.concertImage.setImageResource(R.drawable.placeholder); // Set a placeholder image
            //                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
            //                    activity.runOnUiThread(() -> {
            //                        // Handle failure to make API call
            //                        holder.concertImage.setImageResource(R.drawable.placeholder); // Set a placeholder image
            //                    });
                                }
                            });

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
//                        activity.runOnUiThread(() -> {
//                            // Handle failure to fetch image
//                            holder.concertImage.setImageResource(R.drawable.placeholder); // Set a placeholder image
//                        });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
//                    activity.runOnUiThread(() -> {
//                        // Handle failure to make API call
//                        holder.concertImage.setImageResource(R.drawable.placeholder); // Set a placeholder image
//                    });
            }
        });


        bookBtn = findViewById(R.id.bookButton);
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), BookingActivity.class);

                intent.putExtra("concertId", concertId);

                v.getContext().startActivity(intent);
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