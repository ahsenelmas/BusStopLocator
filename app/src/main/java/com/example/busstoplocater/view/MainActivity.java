package com.example.busstoplocater.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.busstoplocater.R;
import com.example.busstoplocater.controller.FetchBusController;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class
MainActivity extends AppCompatActivity {
    private MapView mapView;
    private final String datasetId = "ab75c33d-3a26-4342-b36a-6e5fef0a3ac3";
    private final String apiKey = "1442ede5-7232-4489-9d4f-643f4dae8162";
    private double userLat = 0, userLon = 0;
    private String searchedStopName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        userLat = getIntent().getDoubleExtra("lat", 0);
        userLon = getIntent().getDoubleExtra("lon", 0);
        searchedStopName = getIntent().getStringExtra("stop_name");

        Log.d("GPS_RECEIVED", "Lat: " + userLat + ", Lon: " + userLon);

        if (userLat == 0 && userLon == 0) {
            userLat = 52.23;
            userLon = 21.01;
        }

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(13.0);
        mapView.getController().setCenter(new GeoPoint(userLat, userLon));

        new FetchBusController(
                this,
                mapView,
                userLat,
                userLon,
                datasetId,
                apiKey,
                searchedStopName,
                progressBar
        ).fetchBusStops();

        ImageView homeLogo = findViewById(R.id.btnHomeLogo);
        homeLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
