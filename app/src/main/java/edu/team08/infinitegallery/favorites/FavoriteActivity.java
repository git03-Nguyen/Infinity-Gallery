package edu.team08.infinitegallery.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.team08.infinitegallery.main.MainCallbacks;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.settings.SettingsActivity;

public class FavoriteActivity extends AppCompatActivity implements MainCallbacks {
    private static int spanCount = 0;
    File[] favoriteFiles;
    private FavoriteManager favoriteManager;
    PhotosAdapter photosAdapter;
    RecyclerView photosRecView;
    ViewSwitcher viewSwitcher;
    Toolbar toolbar;
    Toolbar toolbarPhotosSelection;
    MaterialButton btnTurnOffSelectionMode;
    TextView txtNumberOfSelected;
    MaterialCheckBox checkBoxAll;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        toolbar = findViewById(R.id.toolbarFavorite);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favoriteManager = new FavoriteManager(this);
        favoriteFiles = null;
        photosRecView = findViewById(R.id.recViewFavorites);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        if (spanCount == 0) {
            spanCount = 4;
        }

        this.toolbarPhotosSelection = findViewById(R.id.toolbarPhotosSelection);
        this.btnTurnOffSelectionMode = findViewById(R.id.btnTurnOffSelectionMode);
        this.btnTurnOffSelectionMode.setOnClickListener(v -> {
            toggleSelectionMode();
        });
        this.txtNumberOfSelected = findViewById(R.id.txtNumberOfSelected);
        this.checkBoxAll = findViewById(R.id.checkboxAll);
        this.bottomNavigationView = findViewById(R.id.selectionBottomBar);
        this.bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            File[] files = getSelectedFiles();

            if (itemId == R.id.multipleHide) {
                //hideMultiplePhotos(files);
            } else if (itemId == R.id.multipleMoveTrash) {
                //trashMultiplePhotos(files);
            } else if (itemId == R.id.multipleShare) {
                //shareMultiplePhotos(files);
            } else {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private File[] getSelectedFiles() {
        if (!photosAdapter.getSelectionMode()) {
            return new File[0];
        }

        if (photosAdapter.selectedAll) {
            return favoriteFiles;
        }

        List<File> list = new ArrayList<>();
        SparseBooleanArray selectedItemsId = photosAdapter.getSelectedIds();
        for (int i = 0; i < selectedItemsId.size(); i++) {
            if (selectedItemsId.valueAt(i)) list.add(favoriteFiles[selectedItemsId.keyAt(i)]);
        }
        return list.toArray(new File[0]);
    }

    private void toggleSelectionMode() {
        photosAdapter.toggleSelectionMode();
        if (photosAdapter.getSelectionMode()) {
            this.toolbar.setVisibility(View.GONE);
            this.toolbarPhotosSelection.setVisibility(View.VISIBLE);
            String formattedText=getResources().getString(R.string.selected_photos,0);
            this.txtNumberOfSelected.setText(formattedText);
            this.bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            this.toolbar.setVisibility(View.VISIBLE);
            this.toolbarPhotosSelection.setVisibility(View.GONE);
            this.bottomNavigationView.setVisibility(View.GONE);
        }
        this.checkBoxAll.setChecked(false);
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
        setSpanSize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        else if (itemId == R.id.select) {
            toggleSelectionMode();
        }
        else if (itemId == R.id.column_2) {
            spanCount = 2;
            setSpanSize();
        }
        else if (itemId == R.id.column_3) {
            spanCount = 3;
            setSpanSize();
        }
        else if (itemId == R.id.column_4) {
            spanCount = 4;
            setSpanSize();
        }
        else if (itemId == R.id.column_5) {
            spanCount = 5;
            setSpanSize();
        } else if (itemId == R.id.menuPhotosSettings) {
            Intent myIntent = new Intent(FavoriteActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setSpanSize() {
        photosAdapter = new PhotosAdapter(this, Arrays.asList(favoriteFiles), spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    private void setNumberOfSelectedFiles(int number) {
        String formattedText=getResources().getString(R.string.selected_photos, number);
        this.txtNumberOfSelected.setText(formattedText);
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch(sender) {
            case "NUMBER OF SELECTIONS":
                int selectionsCount = Integer.parseInt(request);
                this.setNumberOfSelectedFiles(selectionsCount);
                break;

            default: break;
        }
    }
}
