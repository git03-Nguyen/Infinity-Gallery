package edu.team08.infinitegallery.optionsearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenCageService {
    @GET("json")
    Call<OpenCageResponse> geocode(
            @Query("q") String query,
            @Query("key") String apiKey
    );

    @GET("json")
    Call<OpenCageResponse> reverseGeocode(
            @Query("q") String latLng, // Provide the coordinates in the format "latitude,longitude"
            @Query("key") String apiKey
    );
}