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
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        // Create a custom dialog with ProgressBar
        Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_progress_bar);
        progressDialog.setTitle("Restoring...");
        progressDialog.setCancelable(false);

        ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
        TextView textViewMessage = progressDialog.findViewById(R.id.textViewMessage);

        // Show the dialog
        progressDialog.show();

        // Simulate the restoration process
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Record the start time
                    long startTime = System.currentTimeMillis();

                    // Restore the photo from the trash
                    new TrashBinManager(SingleTrashActivity.this).restorePhoto(new File(photoPaths[currentPosition]));

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
                    // For example, you can display a message about the restoration completion
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                } catch (IOException e) {
                    Log.e("restorePhoto", "Cannot restore photo!");
                    progressDialog.dismiss(); // Dismiss the progress dialog in case of an error
                }
            }
        }).start();
    }

    private void permanentlyDeletePhoto() {
        // Get the current position
        int currentPosition = singlePhotoFragment.getCurrentPosition();

        // Build a confirmation dialog for permanent deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Permanent Deletion");
        builder.setMessage("Are you sure to permanently delete this photo? This action cannot be undone.");

        // Add positive button for confirmation
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, proceed with permanent deletion
                try {
                    // Permanent deletion logic here
                    // For example, you can use File.delete() to delete the photo
                    File photoFile = new File(photoPaths[currentPosition]);
                    if (photoFile.delete()) {
                        // File deleted successfully
                        // Optionally, you can update UI or perform additional actions
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    } else {
                        // Failed to delete the file
                        Log.e("permanentlyDeletePhoto", "Failed to permanently delete photo");
                    }
                } catch (Exception e) {
                    Log.e("permanentlyDeletePhoto", "Error during permanent deletion", e);
                }
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