package edu.team08.infinitegallery.trashbin;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.singlephoto.SinglePhotoFragment;

public class SingleTrashActivity extends AppCompatActivity {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private String[] photoPaths;
    private int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trash);

        Intent intent = getIntent();
        if (intent.hasExtra("photoPaths")) {
            this.photoPaths = intent.getStringArrayExtra("photoPaths");
        }
        if (intent.hasExtra("currentPosition")) {
            this.currentPosition = intent.getIntExtra("currentPosition", 0);
        }

        singlePhotoFragment = new SinglePhotoFragment(this, this.photoPaths, this.currentPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

        // TODO: implementations for bottom nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            if (itemId == R.id.delete) {
                // TODO: permanently delete

            } else if (itemId == R.id.restore) {
                // TODO: restore image
                try {
                    new TrashBinManager(this).restorePhoto(new File(photoPaths[currentPosition]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // File restored successfully, update UI or perform other actions
                Toast.makeText(this, "Image restored to Pictures", Toast.LENGTH_SHORT).show();
                finish();
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


}