package com.example.tp_localisation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.viewmodels.PositionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1;
    private static final int REQUEST_BACKGROUND_PERMISSION = 2;
    private static final String TAG = "MainActivity";

    private Button btnSend, btnShowMap;
    private TextView tvStatus;

    private LocationManager locationManager;
    private TelephonyManager telephonyManager;
    private PositionViewModel viewModel;

    // Flag to prevent multiple location updates
    private boolean locationUpdateInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = findViewById(R.id.btn_send);
        btnShowMap = findViewById(R.id.btn_show_map);
        tvStatus = findViewById(R.id.tv_status);

        // Initialize LocationManager and TelephonyManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PositionViewModel.class);
        viewModel.init(this);

        // Observe the result from ViewModel
        viewModel.getResponseLiveData().observe(this, result -> {
            tvStatus.setText(result);
            locationUpdateInProgress = false;
        });

        // Button click listener to check permissions and fetch location
        btnSend.setOnClickListener(v -> {
            if (checkPermissions()) {
                sendCurrentPosition();
            } else {
                requestPermissions();
            }
        });

        // Set onClickListener to navigate to the MapActivity
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        // Pour Android 10+ (API 29+), vérifiez ACCESS_BACKGROUND_LOCATION si nécessaire
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    private void requestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, REQUEST_PERMISSIONS);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
            }, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (checkPermissions()) {
                sendCurrentPosition();
            } else {
                tvStatus.setText("Permissions refusées");
            }
        } else if (requestCode == REQUEST_BACKGROUND_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                sendCurrentPosition();
            } else {
                tvStatus.setText("Permission de localisation en arrière-plan refusée");
            }
        }
    }

    private void sendCurrentPosition() {
        try {
            if (locationUpdateInProgress) {
                tvStatus.setText("Demande de localisation déjà en cours...");
                return;
            }

            locationUpdateInProgress = true;
            tvStatus.setText("Recherche de la position...");

            if (checkPermissions()) {
                // Try to use network provider for faster initial position
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    Log.d(TAG, "Requested location updates from NETWORK_PROVIDER");
                }

                // Also request GPS updates for accuracy
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Log.d(TAG, "Requested location updates from GPS_PROVIDER");
                } else {
                    tvStatus.setText("GPS est désactivé. Veuillez activer le GPS dans les paramètres.");
                    locationUpdateInProgress = false;
                    return;
                }

                // Get last known location as a fallback
                Location lastKnownLocation = null;
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d(TAG, "Getting last known location from NETWORK_PROVIDER");
                }
                if (lastKnownLocation == null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d(TAG, "Getting last known location from GPS_PROVIDER");
                }

                if (lastKnownLocation != null) {
                    Log.d(TAG, "Using last known location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
                    updatePosition(lastKnownLocation);
                }

                // Set a timeout handler
                new android.os.Handler().postDelayed(() -> {
                    if (tvStatus.getText().toString().equals("Recherche de la position...")) {
                        tvStatus.setText("Délai d'attente de localisation dépassé. Réessayez ou vérifiez les paramètres GPS.");
                        try {
                            locationManager.removeUpdates(locationListener);
                        } catch (SecurityException e) {
                            Log.e(TAG, "Error removing location updates: " + e.getMessage());
                        }
                        locationUpdateInProgress = false;
                    }
                }, 15000); // 15 seconds timeout

            } else {
                tvStatus.setText("Permissions non accordées");
                locationUpdateInProgress = false;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while requesting location updates", e);
            tvStatus.setText("Erreur de sécurité: " + e.getMessage());
            locationUpdateInProgress = false;
        } catch (Exception e) {
            Log.e(TAG, "Exception while requesting location updates", e);
            tvStatus.setText("Erreur: " + e.getMessage());
            locationUpdateInProgress = false;
        }
    }

    private final android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
            updatePosition(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "Provider status changed: " + provider + ", status: " + status);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.d(TAG, "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.d(TAG, "Provider disabled: " + provider);
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatus.setText("GPS est désactivé. Veuillez activer le GPS dans les paramètres.");
            }
        }
    };

    private void updatePosition(Location location) {
        try {
            // Arrêter les écouteurs de localisation une fois qu'une position est obtenue
            locationManager.removeUpdates(locationListener);

            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String imei = getImei();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Log.d(TAG, "Sending position: Lat=" + lat + ", Lon=" + lon + ", IMEI=" + imei + ", Date=" + date);
            tvStatus.setText("Envoi de la position...");

            Position position = new Position(lat, lon, imei, date);
            viewModel.sendPosition(position);

        } catch (SecurityException e) {
            Log.e(TAG, "Error removing location updates", e);
            tvStatus.setText("Erreur de sécurité: " + e.getMessage());
            locationUpdateInProgress = false;
        } catch (Exception e) {
            Log.e(TAG, "Error processing location", e);
            tvStatus.setText("Erreur: " + e.getMessage());
            locationUpdateInProgress = false;
        }
    }

    // Get IMEI, or fallback to another identifier if not allowed
    private String getImei() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Pour Android 10+, on utilise un identifiant unique de l'application
            return android.provider.Settings.Secure.getString(
                    getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    return telephonyManager.getImei();
                }
            } catch (SecurityException e) {
                Log.e(TAG, "IMEI access error: " + e.getMessage());
            }
        }
        // Fallback pour les anciennes versions ou si l'accès n'est pas autorisé
        return android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }
}