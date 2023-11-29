package edu.team08.infinitegallery.singlephoto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.team08.infinitegallery.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.trashbin.SingleTrashActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;
public class SinglePhotoActivity extends AppCompatActivity implements MainCallbacks {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar topToolbarPhoto;
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

        topToolbarPhoto = findViewById(R.id.topToolbarPhoto);
        setDateForToolbar(photoPaths[currentPosition]);

        setSupportActionBar(topToolbarPhoto);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setDateForToolbar(String filePath){
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(filePath));
            ExifSubIFDDirectory exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            FileSystemDirectory fileDir = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
            Date date = null;
            if(exifDir != null){
                date = exifDir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }else if(fileDir != null){
                date = fileDir.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE);
            }else{
                //TODO: how to handle in case the photo don't have Exif tag and File tag.
            }

            if(date != null && topToolbarPhoto != null){
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                topToolbarPhoto.setTitle(dateFormat.format(date));
                topToolbarPhoto.setSubtitle(timeFormat.format(date));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Deletion",
                "Are you sure to move this photo to the trash?",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(SinglePhotoActivity.this, "Deleting ...", () -> {
                                    try {
                                        new TrashBinManager(SinglePhotoActivity.this).moveToTrash(new File(photoPaths[currentPosition]));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    finish();
                                });

                    }
                },
                null);
    }

    // ...

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        // Do not care who sender?
        // Get information about current picture => current position.
        int currentPosition = Integer.parseInt(request);
        setDateForToolbar(this.photoPaths[currentPosition]);
    }
}