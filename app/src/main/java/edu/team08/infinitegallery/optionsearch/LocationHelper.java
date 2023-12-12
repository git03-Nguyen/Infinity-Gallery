package edu.team08.infinitegallery.optionsearch;

import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationHelper {
    private static final String OPEN_CAGE_BASE_URL = "https://api.opencagedata.com/geocode/v1/";

    private static final String API_KEY = "4f5f8453f0254a788542d60675fd7cbe";
    private static final HashMap<String,String> reverseGeocodingCache=new HashMap<>();
    public static float[] readCoordinatesFromExif(String photoPath) {
        try {
            ExifInterface exifInterface = new ExifInterface(photoPath);
            float[] latLong = new float[2];
            if (exifInterface.getLatLong(latLong)) {
                Log.d("readCoordinatesFromExif", Arrays.toString(latLong));
                return latLong;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public interface ReverseGeocodeCallback {
        void onSuccess(String formattedAddress,PhotoInfo photoInfo);

        void onFailure(Throwable t);
    }
    public static void reverseCoordinatesToAddress(float latitude, float longitude, ReverseGeocodeCallback callback,PhotoInfo photoInfo) {
        String latLng=latitude+", "+longitude;

        //Check if result is already cached
        if (reverseGeocodingCache.containsKey(latLng))
        {
            String cachedAddress=reverseGeocodingCache.get(latLng);
            callback.onSuccess(cachedAddress,photoInfo);
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OPEN_CAGE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenCageService service = retrofit.create(OpenCageService.class);
//        String latLng = latitude + "," + longitude;
        Call<OpenCageResponse> call = service.reverseGeocode(latLng, API_KEY);

        call.enqueue(new Callback<OpenCageResponse>() {
            @Override
            public void onResponse(Call<OpenCageResponse> call, Response<OpenCageResponse> response) {
                if (response.isSuccessful()) {
                    OpenCageResponse result = response.body();
                    if (result != null) {
                        List<OpenCageResponse.Result> results = result.getResults();
                        if (!results.isEmpty()) {
                            OpenCageResponse.Result firstResult = results.get(0);
                            String formattedAddress = firstResult.getFormattedAddress();

                            //Put result to cache:
                            reverseGeocodingCache.put(latLng,formattedAddress);
                            // Call the onSuccess method of the callback
                            callback.onSuccess(formattedAddress,photoInfo);
                            return;
                        }
                    }
                }
                // Call the onFailure method of the callback
                callback.onFailure(new Exception("Failed to get address"));
            }

            @Override
            public void onFailure(Call<OpenCageResponse> call, Throwable t) {
                t.printStackTrace();
                // Call the onFailure method of the callback
                callback.onFailure(t);
            }
        });
    }
}
