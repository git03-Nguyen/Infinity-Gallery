package edu.team08.infinitegallery.optionsearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenCageResponse {
    @SerializedName("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        @SerializedName("formatted")
        private String formattedAddress;

        public String getFormattedAddress() {
            return formattedAddress;
        }
    }
}
