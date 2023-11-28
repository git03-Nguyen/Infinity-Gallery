package edu.team08.infinitegallery.trashbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;
import edu.team08.infinitegallery.singlephoto.SinglePhotoActivity;

public class TrashBinActivity extends AppCompatActivity {
    private int spanCount = 4;
    private TrashBinManager trashBinManager;
    PhotosAdapter photosAdapter;
    RecyclerView photosRecView;
    ViewSwitcher viewSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin);

        setSupportActionBar(findViewById(R.id.toolbarTrashBin));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trashBinManager = new TrashBinManager(this);
        photosRecView = findViewById(R.id.recViewTrash);
        viewSwitcher = findViewById(R.id.viewSwitcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        File[] trashFiles = trashBinManager.getTrashFiles();
        if (trashFiles.length > 0) {
            if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
        }
        photosAdapter = new PhotosAdapter(this, Arrays.asList(trashFiles), spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_trash_bin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.menuTrashBinRestoreAll) {
            this.restoreAllPhotos();
        } else if (itemId == R.id.menuTrashBinSelect) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuTrashBinEmpty) {
            this.emptyTrashBin();
        } else if (itemId == R.id.menuTrashBinSettings) {
            Intent myIntent = new Intent(TrashBinActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void emptyTrashBin() {
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm empty trash bin",
                "Are you sure to empty the trash bin? This action cannot be undone.",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(TrashBinActivity.this, "Deleting ...", () -> {
                                    trashBinManager.emptyTrashBin();
                                },
                                () -> {
                                    onResume();
                                });

                    }
                },
                null);
    }

    private void restoreAllPhotos() {
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Restore",
                "Are you sure to restore all photos?",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(TrashBinActivity.this, "Restoring ...", () -> {
                                    try {
                                        trashBinManager.restoreAllPhotos();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    onResume();
                                });

                    }
                },
                null);

    }

}
