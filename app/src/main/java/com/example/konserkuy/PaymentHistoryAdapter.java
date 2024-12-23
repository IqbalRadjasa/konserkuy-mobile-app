package com.example.konserkuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {

    private final JSONArray concerts;
    private final Activity activity;

    public PaymentHistoryAdapter(Activity activity, JSONArray concerts) {
        this.activity = activity;
        this.concerts = concerts;
    }

    @NonNull
    @Override
    public PaymentHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list, parent, false);
        return new PaymentHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryViewHolder holder, int position) {
        try {
            JSONObject concert = concerts.getJSONObject(position);

            // Set concert name
            holder.concertName.setText(concert.getString("name"));

            // Set click listener for details button
            holder.deleteButton.setOnClickListener(v -> {
                try {
                    String url = "http://10.0.2.2:4000/booking/" + concert.getString("id");

                    ApiHelper apiHelper = new ApiHelper();
                    apiHelper.makeApiCall(url, "delete", null, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                activity.runOnUiThread(() -> {
                                    new AlertDialog.Builder(activity)
                                            .setTitle("Success")
                                            .setMessage("History has been successfully deleted.")
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                // Reload the activity
                                                Intent intent = activity.getIntent();
                                                activity.finish();
                                                activity.startActivity(intent);
                                            })
                                            .setCancelable(false)
                                            .show();
                                });
                            } else {
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
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return concerts.length();
    }

    static class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView concertName;
        Button deleteButton;

        public PaymentHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            concertName = itemView.findViewById(R.id.concertName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
