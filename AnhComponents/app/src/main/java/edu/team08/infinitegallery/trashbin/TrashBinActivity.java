package edu.team08.infinitegallery.trashbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.photos.PhotosAdapter;

public class TrashBinActivity extends AppCompatActivity {
    private int spanCount = 4;
    private TrashBinManager trashBinManager;
    PhotosAdapter photosAdapter;
    RecyclerView photosRecView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_bin);

        setSupportActionBar(findViewById(R.id.toolbarTrashBin));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trashBinManager = new TrashBinManager(this);
        photosRecView = findViewById(R.id.recViewTrash);
    }

    @Override
    public void onResume() {
        super.onResume();
        File[] trashFiles = trashBinManager.getTrashFiles();
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
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuTrashBinSelect) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuTrashBinRestore) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuTrashBinEmpty) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuTrashBinSettings) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}