package edu.team08.infinitegallery.optionalbums;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.IOException;
import java.io.SerializablePermission;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.privacy.PrivacyManager;
import edu.team08.infinitegallery.settings.SettingsActivity;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class AddPhotoActivity extends AppCompatActivity implements MainCallbacks {
    private String folderPath;
    private String albumName;
    private List<File> allPhotos;
    private Toolbar toolbar;
    private TextView txtNumberOfSelected;
    private int numberOfSelected;
    private ViewSwitcher viewSwitcher;
    private RecyclerView recyclerView;
    private PhotosAdapter photosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Intent intent = getIntent();
        if (intent.hasExtra("folderPath")) {
            folderPath = intent.getStringExtra("folderPath");
        }
        albumName = new File(folderPath).getName();

        toolbar = findViewById(R.id.toolbarAddPhotos);

        String formatText=getResources().getString(R.string.add_to_album,albumName);
//        toolbar.setTitle("Add to ${AlbumName}".replace("${AlbumName}", albumName));
        toolbar.setTitle(formatText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numberOfSelected = 0;
        txtNumberOfSelected = findViewById(R.id.txtNumberOfSelected);
        String formattedText=getResources().getString(R.string.selected_photos,0);
//        txtNumberOfSelected.setText("Selected ${num} photos.".replace("${num}", "0"));
        txtNumberOfSelected.setText(formattedText);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        recyclerView = findViewById(R.id.recView);

        readAllImages();
        showAllPictures();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_add_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.addPhotos) {
            addPhotosToAlbum();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    void showAllPictures() {
        if (allPhotos.size() > 0) {
            if (recyclerView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
            photosAdapter = new PhotosAdapter(this, allPhotos, 4);
            photosAdapter.toggleSelectionMode();
            recyclerView.setAdapter(photosAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        }
    }


    private void readAllImages() {
        allPhotos = new ArrayList<>();
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            }

            cursor = this.getContentResolver().query(uri, projection, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                String photoPath = cursor.getString(columnIndex);
                allPhotos.add(new File(photoPath));
            }
        } finally {
            // Close the cursor to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }

        }

    }

    private boolean addPhotosToAlbum() {
        if (allPhotos == null || allPhotos.isEmpty()) return false;
        if (numberOfSelected == 0) return false;

        List<File> list = new ArrayList<>();
        SparseBooleanArray selectedItemsId = photosAdapter.getSelectedIds();
        for (int i = 0; i < selectedItemsId.size(); i++) {
            if (selectedItemsId.valueAt(i)) list.add(allPhotos.get(selectedItemsId.keyAt(i)));
        }

        String title = getResources().getString(R.string.move_photos);
//        String message = "Are you sure to move ${num} photos to ${albumName}?"
//                .replace("${num}", String.valueOf(numberOfSelected))
//                .replace("${albumName}", new File(folderPath).getName());
        String message=getResources().getString(R.string.confirm_move_photos,numberOfSelected,new File(folderPath).getName());
        ConfirmDialogBuilder.showConfirmDialog(this, title, message
                , () -> {
                    Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(AddPhotoActivity.this, "Hiding ...", () -> {
                                moveToAlbum(list);
                            },
                            () -> {
                                finish();
                            });
                }
                , () -> {

                });

        return true;
    }

    private void moveToAlbum(List<File> files) {
        TrashBinManager trashBinManager = new TrashBinManager(this);
        File folder = new File(folderPath);

        for (File file : files) {
            // Check if the folder already contains the file, and skip if it does
            File newFile = new File(folder, file.getName());
            if (!newFile.exists()) {
                // Move the file to /folderPath/fileName
                try {
                    trashBinManager.moveFile(file, newFile);
                } catch (IOException e) {
                    Toast.makeText(this, "There are some errors!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch (sender) {
            case "NUMBER OF SELECTIONS":
                numberOfSelected = Integer.parseInt(request);
                String formattedText=getResources().getString(R.string.selected_photos,numberOfSelected);
                txtNumberOfSelected.setText(formattedText);
//                txtNumberOfSelected.setText("Selected ${num} photos.".replace("${num}", request));
                break;

            default: break;
        }
    }
}