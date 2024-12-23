package com.example.konserkuy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {

    private JSONArray concerts;
    private Context context;
    private final Activity activity;

    public ConcertAdapter(Activity activity, JSONArray concerts) {
        this.activity = activity;
        this.concerts = concerts;
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_concert, parent, false);
        return new ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, int position) {
        try {
            JSONObject concert = concerts.getJSONObject(position);

            holder.concertName.setText(concert.getString("name"));

            String thumbnail = concert.getString("thumbnail"); // Thumbnail value from JSON
            String imageUrl = "http://10.0.2.2:4000/assets/" + thumbnail;

            ApiHelper apiHelper = new ApiHelper();
            apiHelper.makeApiCall(imageUrl, "get", null, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        activity.runOnUiThread(() -> {
                            try {
                                Picasso.get().load(imageUrl)
                                        .resize(800, 600).into(holder.concertImage);
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

            holder.detailButton.setOnClickListener(v -> {
                try {
                    String concertId = concert.getString("id");
                    Intent intent = new Intent(v.getContext(), DetailConcertActivity.class);

                    intent.putExtra("concertId", concertId);

                    v.getContext().startActivity(intent);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                };
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return concerts.length();
    }

    static class ConcertViewHolder extends RecyclerView.ViewHolder {
        ImageView concertImage;
        TextView concertName;
        Button detailButton;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            concertImage = itemView.findViewById(R.id.concertImage);
            concertName = itemView.findViewById(R.id.concertName);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
}
