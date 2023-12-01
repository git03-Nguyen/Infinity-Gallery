package edu.team08.infinitegallery.favorite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteManager {
    int spanCount = 4;
    private Context context;
    private static String FAVORITE_PATH;
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
        File internalStorage = context.getFilesDir();
        File favoriteDir = new File(internalStorage, "favorite");
        if (!favoriteDir.exists()) favoriteDir.mkdir();
        File nomediaFile = new File(favoriteDir, ".nomedia");
        if (!nomediaFile.exists()) nomediaFile.createNewFile();
        this.FAVORITE_PATH = favoriteDir.getAbsolutePath();

        testDatabaseOperations();
    }

    private void cloneFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    public void addToFavorite(File photo) throws IOException {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(FAVORITE_DB_NAME), null);
        ContentValues values = new ContentValues();
        values.put("PATH", photo.getAbsolutePath());
        values.put("NAME", photo.getName());
        //values.put("DATE", System.currentTimeMillis());
        db.insert(FAVORITE_TABLE_NAME, null, values);

        cloneFile(photo, new File(FAVORITE_PATH, photo.getName()));
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
                favorFiles.add(favorFile);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();

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

    private void testDatabaseOperations() {

        if (databaseExists(FAVORITE_DB_NAME)) {
            return;
        }

        // Create the database
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath(FAVORITE_DB_NAME), null);

        // Create the "favor" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITE_TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT, NAME TEXT)");

        // Close the database
        db.close();
    }
}
