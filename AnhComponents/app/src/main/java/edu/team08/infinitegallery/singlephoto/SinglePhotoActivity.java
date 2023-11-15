package edu.team08.infinitegallery.singlephoto;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

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

        singlePhotoFragment = new SinglePhotoFragment(this, this.photoPaths, this.currentPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, singlePhotoFragment)
                .commit();

        // TODO: implementations for bottom nav bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delete) {
                // get the photo and delete it
                // TODO: confirm to delete
                finish();
                try {
                    new TrashBinManager(this).moveToTrash(new File(photoPaths[currentPosition]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this, "Deleted: " + photoPaths[currentPosition], Toast.LENGTH_SHORT).show();
                List<String> newPhotoPaths = new ArrayList<>();
                for (String path : photoPaths) {
                    if (!path.equalsIgnoreCase(photoPaths[currentPosition])) {
                        newPhotoPaths.add(path);
                    }
                }
                finish();
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

}