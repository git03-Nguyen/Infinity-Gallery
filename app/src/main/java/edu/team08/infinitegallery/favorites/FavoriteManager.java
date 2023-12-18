package edu.team08.infinitegallery.favorites;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoriteManager {
    int spanCount = 4;
    private Context context;
    private static final String FAVORITE_DB_NAME = "favorite.db";
    private static final String FAVORITE_TABLE_NAME = "FAVORITE";

    public FavoriteManager(Context context) {
        this.context = context;
        try {
            initFavorite();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initFavorite() throws IOException {

        // Create the database
        SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath(FAVORITE_DB_NAME), null).close();

        if (databaseExists(FAVORITE_DB_NAME)) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    context.getDatabasePath(FAVORITE_DB_NAME), null);

            // Create the "favor" table if it doesn't exist
            db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITE_TABLE_NAME
                    + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)");

            // Query all paths from the "FAVORITE" table
            Cursor cursor = db.query("FAVORITE", new String[]{"PATH"}, null, null, null, null, null);

            if (cursor != null) {
                // Iterate through the cursor to check and delete rows
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex("PATH"));
                    File file = new File(path);

                    // Check if the file doesn't exist
                    if (!file.exists()) {
                        // Delete the row from the "FAVORITE" table
                        db.delete("FAVORITE", "PATH=?", new String[]{path});
                    }
                }

                cursor.close();
            }
        }
    }

    public void addToFavorite(String photoPath) {
        if (!new File(photoPath).exists()) return;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(FAVORITE_DB_NAME), null);
        ContentValues values = new ContentValues();
        values.put("PATH", photoPath);
        db.insert(FAVORITE_TABLE_NAME, null, values);
    }

    public void removeFromFavorite(String photoPath) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("favorite.db"), null);

        // Delete the row with the specified photoPath from the "FAVORITE" table
        int deletedRows = db.delete("FAVORITE", "PATH=?", new String[]{photoPath});

        db.close();
    }


    public boolean isFavorite(String photoPath) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("favorite.db"), null);

        // Query to check if the photoPath exists in the "FAVORITE" table
        Cursor cursor = db.query("FAVORITE", null, "PATH=?", new String[]{photoPath}, null, null, null);

        boolean isFavorite = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return isFavorite;
    }

    public File[] getFavoriteFiles() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(FAVORITE_DB_NAME), null);
        // Query the database to get all favorite file names
        Cursor cursor = db.query(FAVORITE_TABLE_NAME,
                new String[]{"PATH"}, null, null, null, null, null);

        List<File> favorFiles = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String favorPath = cursor.getString(cursor.getColumnIndex("PATH"));
                File favorFile = new File(favorPath);
                if (favorFile.exists()) favorFiles.add(favorFile);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

        Collections.sort(favorFiles, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                // Compare based on the last modified date in descending order (newest first)
                return Long.compare(file2.lastModified(), file1.lastModified());
            }
        });

        return favorFiles.toArray(new File[0]);
    }

    private boolean databaseExists(String dbName) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(
                    context.getDatabasePath(dbName).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (Exception e) {
            // Database does not exist
        }
        return checkDB != null;
    }

}
