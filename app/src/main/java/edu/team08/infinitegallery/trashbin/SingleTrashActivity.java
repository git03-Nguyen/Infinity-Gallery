package edu.team08.infinitegallery.trashbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import edu.team08.infinitegallery.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.singlephoto.SinglePhotoFragment;

public class SingleTrashActivity extends AppCompatActivity implements MainCallbacks {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private String[] trashPaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trash);

        Intent intent = getIntent();
        if (intent.hasExtra("photoPaths")) {
            this.trashPaths = intent.getStringArrayExtra("photoPaths");
        }
        int currentPosition = 0;
        if (intent.hasExtra("currentPosition")) {
            currentPosition = intent.getIntExtra("currentPosition", 0);
        }

        singlePhotoFragment = new SinglePhotoFragment(this, this.trashPaths, currentPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delete) {
                permanentlyDeletePhoto();

            } else if (itemId == R.id.restore) {
                restorePhoto();
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
        AtomicReference<String> photoPath = new AtomicReference<>();

        // Create and show the progress dialog with the restoration logic

        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(this, "Restoring...", () -> {
                    // Restore the photo from the trash
                    try {
                        String path = new TrashBinManager(SingleTrashActivity.this).restorePhoto(new File(trashPaths[currentPosition]));
                        photoPath.set(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    Toast.makeText(SingleTrashActivity.this, "Photo restored at " + photoPath, Toast.LENGTH_SHORT).show();
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
                        File photoFile = new File(trashPaths[currentPosition]);
                        new TrashBinManager(SingleTrashActivity.this).permanentDelete(photoFile);
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


    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {

    }
}