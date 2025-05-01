package com.example.tp_localisation.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.repositories.PositionRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PositionViewModel extends ViewModel {
    private static final String TAG = "PositionViewModel";
    private PositionRepository repository;
    private final MutableLiveData<String> responseLiveData = new MutableLiveData<>();

    public void init(Context context) {
        repository = new PositionRepository(context);
        responseLiveData.setValue("Prêt à envoyer la position");
    }

    public LiveData<String> getResponseLiveData() {
        return responseLiveData;
    }

    public void sendPosition(Position position) {
        if (repository == null) {
            Log.e(TAG, "Repository not initialized");
            responseLiveData.postValue("Erreur: Repository non initialisé");
            return;
        }

        repository.sendPosition(position, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body();
                    Log.d(TAG, "Position sent successfully: " + result);
                    responseLiveData.postValue("Position envoyée avec succès: " + result);
                } else {
                    String errorMsg = "Erreur serveur: " + response.code();
                    Log.e(TAG, errorMsg);
                    responseLiveData.postValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                String errorMsg = "Échec de l'envoi: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                responseLiveData.postValue(errorMsg);
            }
        });
    }
}