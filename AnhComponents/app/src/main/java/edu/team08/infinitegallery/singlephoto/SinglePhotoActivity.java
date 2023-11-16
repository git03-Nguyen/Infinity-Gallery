package edu.team08.infinitegallery.singlephoto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class SinglePhotoActivity extends AppCompatActivity {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private String[] photoPaths;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

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
            if (itemId == R.id.moveTrash) {
                moveToTrash();
            } else {
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

    private void moveToTrash() {
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        // Build a confirmation dialog with a progress bar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure to move this photo to the trash?");

        // Add positive button for confirmation
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, proceed with deletion
                // Create a custom dialog with ProgressBar
                Dialog progressDialog = new Dialog(SinglePhotoActivity.this);
                progressDialog.setContentView(R.layout.dialog_progress_bar);
                progressDialog.setTitle("Deleting...");
                progressDialog.setCancelable(false);

                ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
                TextView textViewMessage = progressDialog.findViewById(R.id.textViewMessage);

                // Show the dialog
                progressDialog.show();

                // Simulate the deletion process
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Record the start time
                            long startTime = System.currentTimeMillis();

                            // Move the photo to the trash
                            new TrashBinManager(SinglePhotoActivity.this).moveToTrash(new File(photoPaths[currentPosition]));

                            // Record the end time
                            long endTime = System.currentTimeMillis();

                            // Calculate the actual time taken
                            final long timeTaken = endTime - startTime;

                            // Dismiss the progress dialog
                            progressDialog.dismiss();

                            // Finish the activity or handle further actions
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });

                            // Optionally, update UI or perform additional actions based on the actual time taken
                            // For example, you can display a message about the deletion completion
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });

                        } catch (IOException e) {
                            Log.e("moveToTrash", "Cannot move photo to trash bin!");
                            progressDialog.dismiss(); // Dismiss the progress dialog in case of an error
                        }
                    }
                }).start();
            }
        });

        // Add negative button for cancel
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled, do nothing
            }
        });

        // Show the confirmation dialog
        builder.show();
    }




}