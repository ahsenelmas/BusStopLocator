package com.example.busstoplocater.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.busstoplocater.R;
import com.example.busstoplocater.model.BusStop;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText inputSearch;
    Button btnUseMyLocation, btnEnterStop;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputSearch = findViewById(R.id.inputSearch);
        btnUseMyLocation = findViewById(R.id.btnUseMyLocation);
        btnEnterStop = findViewById(R.id.btnEnterStop);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnEnterStop.setOnClickListener(v -> {
            inputSearch.setVisibility(View.VISIBLE);
            inputSearch.requestFocus();
        });

        inputSearch.setOnEditorActionListener((textView, actionId, event) -> {
            String stopName = inputSearch.getText().toString().trim();
            if (!stopName.isEmpty()) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("stop_name", stopName);
                startActivity(intent);
            } else {
                Toast.makeText(SearchActivity.this, "Please enter a stop name", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        btnUseMyLocation.setOnClickListener(v -> getCurrentLocation());
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    Toast.makeText(SearchActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                Location location = locationResult.getLastLocation();
                double myLat = location.getLatitude();
                double myLon = location.getLongitude();
                Log.d("REAL_GPS", "Got fresh location: " + myLat + ", " + myLon);

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("lat", myLat);
                intent.putExtra("lon", myLon);
                startActivity(intent);
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
