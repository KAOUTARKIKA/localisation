package com.example.tp_localisation.classes;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Position {
    private static final String TAG = "Position";

    @SerializedName("id")
    private int id;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("imei")
    private String imei;

    @SerializedName("date")
    private String date;

    // Default constructor for Gson
    public Position() {
        Log.d(TAG, "Default constructor called");
    }

    public Position(double latitude, double longitude, String imei, String date) {
        Log.d(TAG, "Constructor called with: lat=" + latitude + ", lon=" + longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.imei = imei;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImei() {
        return imei;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", imei='" + imei + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}