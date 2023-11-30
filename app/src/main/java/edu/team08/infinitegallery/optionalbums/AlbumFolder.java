package edu.team08.infinitegallery.optionalbums;

import java.io.File;

public class AlbumFolder {
    private File[] photos;
    private File folder;

    public AlbumFolder(File[] photos, File folder) {
        this.photos = photos;
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }

    public File[] getPhotos() {
        return photos;
    }

    public int getNumberOfPhotos() {
        return photos.length;
    }

}
