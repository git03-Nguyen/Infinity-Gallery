package edu.team08.infinitegallery.helpers;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        // Compare files based on file names in ascending order
        return file1.getName().compareToIgnoreCase(file2.getName());
    }
}