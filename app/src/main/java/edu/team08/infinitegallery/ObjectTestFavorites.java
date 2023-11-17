package edu.team08.infinitegallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.List;

// TODO: Warning: this object is only used for testing the favorites album
public class ObjectTestFavorites {
    Context context;

    String[] paths;
    public ObjectTestFavorites(Context context, String[] paths) {
        this.context = context;
        this.paths = paths;
        testDatabaseOperations();
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

    private void insertFavoritePath(SQLiteDatabase db, String path) {
        ContentValues values = new ContentValues();
        values.put("PATH", path);
        db.insert("FAVORITE", null, values);
    }

    private void testDatabaseOperations() {

        if (databaseExists("albums.db")) {
            return;
        }

        // Create or open the database
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath("albums.db"), null);

        // Create the "FAVORITE" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS FAVORITE (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)");


        // Insert paths into the "FAVORITE" table
        for (String path: paths) {
            insertFavoritePath(db, path);
        }

        // Close the database
        db.close();
    }
}
