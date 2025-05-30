package com.example.busstoplocater.controller;

import com.example.busstoplocater.model.StopResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WarsawApiService {
    @GET("api/action/dbstore_get")
    Call<StopResponse> getStops(
            @Query("id") String datasetId,
            @Query("apikey") String apiKey
    );
}
