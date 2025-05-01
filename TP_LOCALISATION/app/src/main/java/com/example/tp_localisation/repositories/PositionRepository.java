package com.example.tp_localisation.repositories;

import android.content.Context;
import android.util.Log;

import com.example.tp_localisation.apis.ApiResponse;
import com.example.tp_localisation.apis.PositionApi;
import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.apis.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PositionRepository {
    private final Context context;
    private static final String TAG = "PositionRepository";

    public PositionRepository(Context context) {
        this.context = context;
    }

    public void sendPosition(Position position, final Callback<String> callback) {
        Log.d(TAG, "Preparing to send position: " + position.getLatitude() + ", " + position.getLongitude());

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PositionApi positionApi = retrofit.create(PositionApi.class);

        Log.d(TAG, "API call prepared with data: Lat=" + position.getLatitude() +
                ", Lon=" + position.getLongitude() + ", Date=" + position.getDate() +
                ", IMEI=" + position.getImei());

        // Use @Field annotations for POST parameters
        Call<ApiResponse> call = positionApi.sendPosition(
                position.getLatitude(),
                position.getLongitude(),
                position.getDate(),
                position.getImei()
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response successful: " + response.body().getMessage());
                    callback.onResponse(null, Response.success(response.body().getMessage()));
                } else {
                    String errorMsg = "Server error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Cannot parse error body", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onResponse(null, Response.success("Erreur: " + errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                callback.onFailure(null, t);
            }
        });
    }
}