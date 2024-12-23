package com.example.konserkuy;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiHelper {

    private final OkHttpClient client = new OkHttpClient();

    public void makeApiCall(String url, String method, String jsonBody, Callback callback) {
        Request request = null;

        if ("post".equalsIgnoreCase(method)){
            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else if ("get".equalsIgnoreCase(method)) {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        } else if("patch".equalsIgnoreCase(method)){
            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            request = new Request.Builder()
                    .url(url)
                    .patch(body)
                    .build();
        }else if ("delete".equalsIgnoreCase(method)) {
            request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
        }

        client.newCall(request).enqueue(callback);
    }
}
