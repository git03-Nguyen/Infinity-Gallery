package edu.team08.infinitegallery.singlephoto;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.R;

public class SinglePhotoActivity extends AppCompatActivity {

    SinglePhotoFragment singlePhotoFragment;
    private BottomNavigationView bottomNavigationView;
    private String[] photoPaths;
    private int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo);

        Intent intent = getIntent();
        if (intent.hasExtra("photoPaths")) {
            this.photoPaths = intent.getStringArrayExtra("photoPaths");
        }
        if (intent.hasExtra("currentPosition")) {
            this.currentPosition = intent.getIntExtra("currentPosition", 0);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        setSupportActionBar(findViewById(R.id.topToolbarPhoto));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        singlePhotoFragment = new SinglePhotoFragment(this, this.photoPaths, this.currentPosition); // TODO: receive the photo and send to fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

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