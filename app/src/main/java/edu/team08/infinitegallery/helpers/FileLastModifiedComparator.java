package edu.team08.infinitegallery.helpers;

import java.io.File;
import java.util.Comparator;

public class FileLastModifiedComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        // Compare files based on last modified date in descending order
        return Long.compare(file2.lastModified(), file1.lastModified());
    }
}