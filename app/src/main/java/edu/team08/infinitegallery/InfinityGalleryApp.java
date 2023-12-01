package edu.team08.infinitegallery;

import android.app.Application;

public class InfinityGalleryApp extends Application {

    private static InfinityGalleryApp infinityGalleryApp;
    private static final String TAG = InfinityGalleryApp.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        infinityGalleryApp = this;
    }
    public static InfinityGalleryApp getApp() {
        return infinityGalleryApp;
    }
}