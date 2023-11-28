package edu.team08.infinitegallery.trashbin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.singlephoto.SinglePhotoFragment;

public class SingleTrashActivity extends AppCompatActivity {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private String[] photoPaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trash);

        Intent intent = getIntent();
        if (intent.hasExtra("photoPaths")) {
            this.photoPaths = intent.getStringArrayExtra("photoPaths");
        }
        int currentPosition = 0;
        if (intent.hasExtra("currentPosition")) {
            currentPosition = intent.getIntExtra("currentPosition", 0);
        }

        singlePhotoFragment = new SinglePhotoFragment(this, this.photoPaths, currentPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

        // TODO: implementations for bottom nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delete) {
                // TODO: permanently delete, confirm alert before moving on
                permanentlyDeletePhoto();

            } else if (itemId == R.id.restore) {
                restorePhoto();
            }

            else {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        setSupportActionBar(findViewById(R.id.topToolbarPhoto));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }

        return true;
    }

    private void restorePhoto() {
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        // Create and show the progress dialog with the restoration logic

        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(this, "Restoring...", () -> {
                    // Restore the photo from the trash
                    try {
                        new TrashBinManager(SingleTrashActivity.this).restorePhoto(new File(photoPaths[currentPosition]));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    Toast.makeText(SingleTrashActivity.this, "Photo restored at " + photoPaths[currentPosition], Toast.LENGTH_SHORT).show();
                    finish();
                });

        }

    private void permanentlyDeletePhoto() {
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Permanent Deletion",
                "Are you sure to permanently delete this photo? This action cannot be undone.",
                new Runnable() {
                    @Override
                    public void run() {
                        File photoFile = new File(photoPaths[currentPosition]);
                        photoFile.delete();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                },
                null
        );
    }


}