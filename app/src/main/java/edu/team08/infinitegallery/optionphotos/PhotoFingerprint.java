package edu.team08.infinitegallery.optionphotos;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhotoFingerprint implements Comparable<PhotoFingerprint> {
    private String path;
    private long finger;

    public PhotoFingerprint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFinger() {
        return finger;
    }

    public void setFinger(long finger) {
        this.finger = finger;
    }

    public void setFinger(Bitmap bitmap) {
        float scale_width = 8.0f / bitmap.getWidth();
        float scale_height = 8.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_width, scale_height);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        setFinger(getFingerprint(scaledBitmap));

        bitmap.recycle();
        scaledBitmap.recycle();
    }

    /*
     * Calculates the fingerprint by setting characters to 0 or 1 depending
     * on if the corresponding pixel in the fingerprint is less than the average
     * pixel gray value or not. Then calculates the finger
     *
     * @param pixels The array of pixels in the fingerprint.
     * @param avg    The average gray value.
     * @return The finger.
     */
    private static long getFingerprint(Bitmap bitmap) {
        double[][] pixels = getGrayPixels(bitmap);
        double avg = getGrayAvg(pixels);
        int width = pixels[0].length;
        int height = pixels.length;
        String binaryStr = "";
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    binaryStr += '1';
                } else {
                    binaryStr += '0';
                }
            }
        }
        long fingerprint = Long.parseUnsignedLong(binaryStr, 2);
        return fingerprint;
    }

    private static double getGrayAvg(double[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count += pixels[i][j];
            }
        }
        return (double) count / (double) (width * height);
    }

    private static double[][] getGrayPixels(Bitmap bitmap) {
        int width = 8;
        int height = 8;
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = computeGrayValue(bitmap.getPixel(i, j));
            }
        }
        return pixels;
    }

    /*
     * Computes the gray value of the given RGB pixel using 0.3 red, 0.59 green,
     * and 0.11 blue.
     *
     * @param pixel The RGB pixel.
     * @return The gray value.
     */
    private static double computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return 0.3 * red + 0.59 * green + 0.11 * blue;
    }

    /*
     * Calculates the Hamming distance between the two given fingerprints.
     * The Hamming distance is the number of positions at which the
     * corresponding symbols are different.
     *
     * @param finger1 The first fingerprint.
     * @param finger2 The second fingerprint.
     * @return The hamming distance.
     */
    public int hammingDist(long finger2) {
        int dist = 0;
        long finger1 = this.getFinger();
        long result = finger1 ^ finger2;
        while (result != 0) {
            ++dist;
            result &= result - 1;
        }
        return dist;
    }

    public boolean isNewer(PhotoFingerprint x) {
        long last = (new File(this.path)).lastModified();
        long last_x = (new File(x.path)).lastModified();
        return last > last_x;
    }

    @Override
    public int compareTo(PhotoFingerprint other) {
        return Long.compare(this.finger, other.finger);
    }
}
