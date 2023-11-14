package edu.team08.infinitegallery.photos;

import android.net.Uri;

public class Photo {
    private String name;
    private Uri uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public Photo(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public Photo() {
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

}