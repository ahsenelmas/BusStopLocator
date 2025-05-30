package com.example.busstoplocater.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.busstoplocater.model.BusStop;
import com.example.busstoplocater.model.StopResponse;
import com.example.busstoplocater.view.SearchActivity;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchBusController implements FetchBusInterface {
    private final Context context;
    private final MapView mapView;
    private final double userLat, userLon;
    private final String datasetId, apiKey, searchedStopName;
    private final ProgressBar progressBar;

    public FetchBusController(Context context, MapView mapView,
                              double userLat, double userLon,
                              String datasetId, String apiKey,
                              String searchedStopName,
                              ProgressBar progressBar) {
        this.context = context;
        this.mapView = mapView;
        this.userLat = userLat;
        this.userLon = userLon;
        this.datasetId = datasetId;
        this.apiKey = apiKey;
        this.searchedStopName = searchedStopName;
        this.progressBar = progressBar;
    }

    @Override
    public void fetchBusStops() {
        progressBar.setVisibility(View.VISIBLE);

        WarsawApiService apiService = ApiClient.getClient().create(WarsawApiService.class);
        apiService.getStops(datasetId, apiKey).enqueue(new Callback<StopResponse>() {
            @Override
            public void onResponse(Call<StopResponse> call, Response<StopResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<BusStop> stops = parseStops(response.body());
                    boolean anyMatch = false;

                    for (BusStop stop : stops) {
                        String fullStopName = stop.getName() + " " + stop.getNumber();

                        if (searchedStopName != null && !searchedStopName.isEmpty()) {
                            if (!fullStopName.toLowerCase().contains(searchedStopName.trim().toLowerCase())) continue;
                            anyMatch = true; // if users search
                        } else {
                            double distance = distanceBetween(userLat, userLon, stop.getLat(), stop.getLon());
                            if (distance > 1.0) continue; // if users use GPS
                        }

                        Marker marker = new Marker(mapView);
                        marker.setPosition(new GeoPoint(stop.getLat(), stop.getLon()));
                        marker.setTitle(fullStopName);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        mapView.getOverlays().add(marker);
                    }

                    if (searchedStopName != null && !anyMatch) {
                        Toast.makeText(context, "Stop not found. Returning to home screen.", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(() -> {
                            context.startActivity(new Intent(context, SearchActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }, 2000);
                        return;
                    }

                    List<GeoPoint> geoPoints = new ArrayList<>();
                    for (Overlay overlay : mapView.getOverlays()) {
                        if (overlay instanceof Marker) {
                            Marker marker = (Marker) overlay;
                            geoPoints.add(marker.getPosition());
                        }
                    }

                    if (geoPoints.size() == 1) {
                        mapView.getController().setCenter(geoPoints.get(0));
                        mapView.getController().setZoom(17.0);
                    } else if (!geoPoints.isEmpty()) {
                        BoundingBox box = BoundingBox.fromGeoPoints(geoPoints);
                        mapView.zoomToBoundingBox(box, true);
                    }

                    mapView.invalidate();
                } else {
                    Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StopResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public List<BusStop> parseStops(StopResponse response) {
        List<BusStop> stops = new ArrayList<>();
        for (StopResponse.Result result : response.result) {
            String name = "", number = "";
            double lat = 0, lon = 0;
            for (StopResponse.Values v : result.values) {
                if (v.key.equals("nazwa_zespolu")) name = v.value;
                if (v.key.equals("slupek")) number = v.value;
                if (v.key.equals("szer_geo")) lat = Double.parseDouble(v.value.replace(",", "."));
                if (v.key.equals("dlug_geo")) lon = Double.parseDouble(v.value.replace(",", "."));
            }
            if (!name.isEmpty() && lat != 0 && lon != 0) {
                stops.add(new BusStop(name, number, lat, lon));
            }
        }
        return stops;
    }

    @Override
    public double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}