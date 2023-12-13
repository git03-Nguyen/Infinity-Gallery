package edu.team08.infinitegallery.trashbin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.settings.SettingsActivity;

public class TrashBinActivity extends AppCompatActivity {
    private int spanCount = 4;
    File[] trashFiles;
    private TrashBinManager trashBinManager;
    TrashAdapter trashAdapter;
    RecyclerView photosRecView;
    ViewSwitcher viewSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin);

        setSupportActionBar(findViewById(R.id.toolbarTrashBin));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trashBinManager = new TrashBinManager(this);
        trashFiles = null;
        photosRecView = findViewById(R.id.recViewTrash);
        viewSwitcher = findViewById(R.id.viewSwitcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        trashBinManager.checkAndCleanTrashBin();
        trashFiles = trashBinManager.getTrashFiles();
        if (trashFiles.length > 0) {
            if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
        }
        trashAdapter = new TrashAdapter(this, Arrays.asList(trashFiles), spanCount, trashBinManager);
        photosRecView.setAdapter(trashAdapter);
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
        trashFiles = trashBinManager.getTrashFiles();
        if (trashFiles.length == 0) {
            Toast.makeText(this, "Empty trash bin already", Toast.LENGTH_SHORT).show();
            return;
        }
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getResources().getString(R.string.confirm_empty_trash_bin),
                getResources().getString(R.string.confirm_empty_trash_bin_description),
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(TrashBinActivity.this, "Deleting ...", () -> {
                                    for (File trash: trashFiles) {
                                        trashBinManager.permanentDelete(trash);
                                    }
                                },
                                () -> {
                                    onResume();
                                });

                    }
                },
                null);
    }

    private void restoreAllPhotos() {
        trashFiles = trashBinManager.getTrashFiles();
        if (trashFiles.length == 0) {
            Toast.makeText(this, "No trash to restore", Toast.LENGTH_SHORT).show();
            return;
        }
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getResources().getString(R.string.confirm_restore),
                getResources().getString(R.string.confirm_restore_description),
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(TrashBinActivity.this, "Restoring ...", () -> {
                                    try {
                                        for (File trash: trashFiles) {
                                            trashBinManager.restorePhoto(trash);
                                        }
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
