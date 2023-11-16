package edu.team08.infinitegallery.albums;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.settings.SettingsActivity;

public class AlbumsFragment extends Fragment {
    Context context;
    Button btnTestDB; // TODO: this is only for testing database
    public AlbumsFragment(Context context) {
        this.context = context;
    }

    public static AlbumsFragment newInstance(Context context) {
        return new AlbumsFragment(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbarAlbums);

        toolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            int itemId = item.getItemId();
            if (itemId == R.id.menuAlbumsSettings) {
                Intent myIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(myIntent, null);
            }
            return true;
        });

        // TODO: this is for testing, must be removed later
        btnTestDB = rootView.findViewById(R.id.btnTestDB);
        btnTestDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDatabaseOperations();
            }
        });

        return rootView;
    }

    // TODO: these methods only for testing, must be deleted later
    private void testDatabaseOperations() {

        if (databaseExists("albums.db")) {
            Toast.makeText(context, "Database 'albums.db' exists", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "Creating 'albums.db' ...", Toast.LENGTH_SHORT).show();

        // Create or open the database
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath("albums.db"), null);

        // Create the "FAVORITE" table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS FAVORITE (ID INTEGER PRIMARY KEY AUTOINCREMENT, PATH TEXT)");


        // Insert paths into the "FAVORITE" table
        insertFavoritePath(db, "/storage/emulated/0/Pictures/AAA.jpg");
        insertFavoritePath(db, "/storage/emulated/0/Pictures/doraemon-and-nobita.jpg");

        // Close the database
        db.close();
    }

    // TODO: this is a testing method
    private void insertFavoritePath(SQLiteDatabase db, String path) {
        ContentValues values = new ContentValues();
        values.put("PATH", path);
        db.insert("FAVORITE", null, values);
    }

    // TODO: this is a testing method
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