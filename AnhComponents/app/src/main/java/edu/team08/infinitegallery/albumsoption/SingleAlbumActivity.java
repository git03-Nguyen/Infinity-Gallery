package edu.team08.infinitegallery.albumsoption;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.photosoption.PhotosAdapter;
import edu.team08.infinitegallery.settingsoption.SettingsActivity;

public class SingleAlbumActivity extends AppCompatActivity {
    int spanCount = 4;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;
    Toolbar toolbar;
    String albumType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);

        Intent intent = getIntent();
        if (intent.hasExtra("albumType")) {
            this.albumType = intent.getStringExtra("albumType");
        }
        if (albumType == "userDefinedType") {
            // TODO: this album is user-defined => get the table name and query the photos
        }

        this.toolbar = findViewById(R.id.topToolbarAlbum);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photosRecView = findViewById(R.id.recViewPhotos);
        getAllPhotosInAlbum();
        showAllPhotos();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.menuTrashBinSettings) {
            Intent myIntent = new Intent(SingleAlbumActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void getAllPhotosInAlbum() {
        this.photoFiles = new ArrayList<>();
        switch (this.albumType) {
            case "favorite":
                getAllFavoritePhotos();
                break;
            default:
                break;
        }
    }

    private void getAllFavoritePhotos() {
        List<String> photoPaths = new ArrayList<>();

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                this.getDatabasePath("albums.db"), null);

        // Query for favorite photos
        Cursor cursor = db.query(
                "FAVORITE",
                new String[]{"PATH"},
                null,
                null,
                null,
                null,
                null
        );

        // Extract paths from the cursor
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex("PATH"));
                photoPaths.add(path);
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        for (String path : photoPaths) {
            File photo = new File(path);
            if (photo.exists()) photoFiles.add(photo);
        }
    }

    private void showAllPhotos() {
        photosAdapter = new PhotosAdapter(this, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

}