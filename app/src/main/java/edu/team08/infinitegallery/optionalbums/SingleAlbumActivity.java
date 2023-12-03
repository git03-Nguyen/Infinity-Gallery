package edu.team08.infinitegallery.optionalbums;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
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

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.settings.SettingsActivity;

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
            getAllPhotosOfFolder(folderPath);
            showAllPhotos();
            this.toolbar.setSubtitle(this.photoFiles.size() + " photos");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_single_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.settings) {
            Intent myIntent = new Intent(SingleAlbumActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else if (itemId == R.id.addPhotos) {

        } else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        return true;
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

    }

    private void showAllPhotos() {
        photosAdapter = new PhotosAdapter(this, photoFiles, spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {

    }

    List<String> imagePathList;
    String imagePath;

    @SuppressLint("Range")
    public void getImageFilePath(Uri uri) {

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            cursor.close();
        }
    }
}