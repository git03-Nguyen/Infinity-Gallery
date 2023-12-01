package edu.team08.infinitegallery.optionalbums;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;

public class SingleAlbumActivity extends AppCompatActivity implements MainCallbacks {
    int spanCount = 4;
    List<File> photoFiles;
    RecyclerView photosRecView;
    PhotosAdapter photosAdapter;
    Toolbar toolbar;
    String albumName;
    String folderPath;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);

        this.firstTime = true;

        Intent intent = getIntent();
        if (intent.hasExtra("albumName")) {
            this.albumName = intent.getStringExtra("albumName");
        }
        if (intent.hasExtra("folderPath")) {
            this.folderPath = intent.getStringExtra("folderPath");
        }

        getAllPhotos();

        this.toolbar = findViewById(R.id.topToolbarAlbum);
        this.toolbar.setTitle(this.albumName);
        this.toolbar.setSubtitle(this.photoFiles.size() + " photos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photosRecView = findViewById(R.id.recViewPhotos);
        showAllPhotos();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstTime) {
            firstTime = false;
        } else {
            reGetAllPhotos();
            showAllPhotos();
        }
    }

    private void reGetAllPhotos() {
        if (albumName == "Favorites") {
            getAllFavoritePhotos();
        } else if (albumName == "Privacy") {

        } else {
            getAllPhotosOfFolder(folderPath);
        }
    }

    private void getAllPhotosOfFolder(String folderPath) {
        this.photoFiles = new ArrayList<File>();
        File folder = new File(folderPath);
        if (folder == null || !folder.isDirectory()) return;
        this.photoFiles = filterImageFiles(folder.listFiles());
    }

    private List<File> filterImageFiles(File[] files) {
        List<File> imageFiles = new ArrayList<>();
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".gif"};

        Arrays.stream(files)
                .filter(file -> file.isFile() && hasValidExtension(file, validExtensions))
                .forEach(imageFiles::add);

        return imageFiles;
    }

    private boolean hasValidExtension(File file, String[] validExtensions) {
        return Arrays.stream(validExtensions)
                .anyMatch(extension -> file.getName().toLowerCase().endsWith(extension));
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

    private void getAllPhotos() {
        Intent intent = getIntent();
        String[] photosPaths = null;
        if (intent.hasExtra("photosList")) {
            photosPaths = intent.getStringArrayExtra("photosList");
        }
        this.photoFiles = new ArrayList<>();
        if (photosPaths != null) {
            for (String path: photosPaths) {
                this.photoFiles.add(new File(path));
            }
        }
        if (albumName == "Favorites") {
            getAllFavoritePhotos();
        } else if (albumName == "Privacy") {

        } else {

        }
    }

    private void getAllFavoritePhotos() {
        this.photoFiles = new ArrayList<>();
        List<String> photoPaths = new ArrayList<>();

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                this.getDatabasePath("favorite.db"), null);

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

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {

    }
}