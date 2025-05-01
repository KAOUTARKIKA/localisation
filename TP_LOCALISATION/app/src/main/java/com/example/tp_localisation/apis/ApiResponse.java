package com.example.tp_localisation.apis;

public class ApiResponse {
    private String message;
    private String error;

    public String getMessage() {
        return message != null ? message : "";
    }

    public String getError() {
        return error != null ? error : "";
    }

    public boolean isSuccess() {
        return error == null || error.isEmpty();
    }
}