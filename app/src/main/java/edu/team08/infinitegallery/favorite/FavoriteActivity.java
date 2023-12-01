package edu.team08.infinitegallery.favorite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import edu.team08.infinitegallery.optionmore.MoreFragment;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;
import edu.team08.infinitegallery.favorite.FavoriteActivity;
import edu.team08.infinitegallery.favorite.FavoriteManager;

public class FavoriteActivity extends AppCompatActivity {
    private int spanCount = 4;
    File[] favoriteFiles;
    private FavoriteManager favoriteManager;
    PhotosAdapter photosAdapter;
    RecyclerView photosRecView;
    ViewSwitcher viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        setSupportActionBar(findViewById(R.id.toolbarFavorite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favoriteManager = new FavoriteManager(this);
        favoriteFiles = null;
        photosRecView = findViewById(R.id.recViewFavorites);
        viewSwitcher = findViewById(R.id.viewSwitcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteFiles = favoriteManager.getFavoriteFiles();
        if (favoriteFiles.length > 0) {
            if (R.id.recViewFavorites == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
            viewSwitcher.showNext();
        }
        photosAdapter = new PhotosAdapter(this, Arrays.asList(favoriteFiles), spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.menuPhotosSelect) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menuPhotosColumns) {

        } else if (itemId == R.id.menuPhotosSettings) {
            Intent myIntent = new Intent(FavoriteActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
