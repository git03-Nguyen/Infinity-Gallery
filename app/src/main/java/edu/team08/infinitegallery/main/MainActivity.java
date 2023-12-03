package edu.team08.infinitegallery.main;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteManager;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.optionalbums.AlbumsFragment;
import edu.team08.infinitegallery.optionmore.MoreFragment;
import edu.team08.infinitegallery.optionphotos.PhotosFragment;
import edu.team08.infinitegallery.privacy.PrivacyManager;
import edu.team08.infinitegallery.optionsearch.SearchFragment;
import edu.team08.infinitegallery.optionsettings.AppConfig;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    private static final int SETTINGS_REQUEST_CODE = 1;
    private PhotosFragment photosFragment;
    private AlbumsFragment albumsFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView bottomSelectionFeatures;
    private Fragment currentFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppConfig.getInstance(MainActivity.this).getNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(() -> {
            scanMediaOnStorage(null);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        runOnUiThread(() -> {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (AppConfig.getInstance(MainActivity.this).getNightMode()) {
                getWindow().getDecorView().setSystemUiVisibility(0);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        });
    }

    private void initApp() {
//        scanMediaOnStorage();

        bottomSelectionFeatures = findViewById(R.id.selectionBottomBar);
        bottomSelectionFeatures.setOnItemSelectedListener(item -> {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });
        bottomSelectionFeatures.getMenu().setGroupCheckable(0, false, true);
        setSelectionFeaturesForAllPhotos();

        photosFragment = PhotosFragment.newInstance(MainActivity.this);
        albumsFragment = AlbumsFragment.newInstance(MainActivity.this);
        searchFragment = SearchFragment.newInstance(MainActivity.this);
        moreFragment = MoreFragment.newInstance(MainActivity.this);
        currentFragment = photosFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHolder, currentFragment)
                .commit();

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            scanMediaOnStorage(null);
            int itemId = item.getItemId();
            if (itemId == R.id.nav_photos) {
                currentFragment = photosFragment;
            } else if (itemId == R.id.nav_albums) {
                currentFragment = albumsFragment;
            } else if (itemId == R.id.nav_search) {
                currentFragment = searchFragment;
            } else if (itemId == R.id.nav_more) {
                currentFragment = moreFragment;
            }

            if (currentFragment != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder, currentFragment)
                        .commit();

            // Check to clean the trash bin
            new TrashBinManager(this).checkAndCleanTrashBin();
            new FavoriteManager(this);

            return true;
        });

    }

    public void changeStatusBar() {
        runOnUiThread(() -> {
            if (currentFragment instanceof PhotosFragment) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().getDecorView().setSystemUiVisibility(0);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                if (AppConfig.getInstance(MainActivity.this).getNightMode()) {
                    getWindow().getDecorView().setSystemUiVisibility(0);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        });
    }

    private void setSelectionFeaturesForAllPhotos() {
        bottomSelectionFeatures.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (photosFragment == null) return false;
            File[] files = photosFragment.getSelectedFiles();

            if (itemId == R.id.multipleHide) {
                hideMultiplePhotos(files);
            } else if (itemId == R.id.multipleMoveTrash) {
                trashMultiplePhotos(files);
            } else if (itemId == R.id.multipleShare) {
                shareMultiplePhotos(files);
            } else {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void shareMultiplePhotos(File[] files) {
        // TODO: implement sharing multiple files feature
        Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();
    }

    private void trashMultiplePhotos(File[] files) {
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Deletion",
                "Are you sure to move ${n} photos to the trash?".replace("${n}", String.valueOf(files.length)),
                new Runnable() {

                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(MainActivity.this, "Deleting ...", () -> {
                                    try {
                                        TrashBinManager trashBinManager = new TrashBinManager(MainActivity.this);
                                        for (File file: files) {
                                            trashBinManager.moveToTrash(file);
                                        }
                                        scanMediaOnStorage(null);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    photosFragment.toggleSelectionMode();
                                });

                    }
                },
                null);
    }

    private void hideMultiplePhotos(File[] files) {
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm Hiding",
                "Are you sure to move ${n} photos to the privacy list?".replace("${n}", String.valueOf(files.length)),
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(MainActivity.this, "Hiding ...", () -> {
                                    try {
                                        PrivacyManager privacyManager = new PrivacyManager(MainActivity.this);
                                        for (File file: files) {
                                            privacyManager.hideToPrivacy(file);
                                        }
                                        scanMediaOnStorage(null);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    photosFragment.toggleSelectionMode();
                                });

                    }
                },
                null);
    }

    public void scanMediaOnStorage(@Nullable Runnable runnable) {
        MediaScannerConnection.scanFile(MainActivity.this, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, new String[] {"image/*"}, new MediaScannerConnection.OnScanCompletedListener()  {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
                if (runnable != null) runnable.run();
            }
        });
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch(sender) {
            case "SELECTION MODE":
                if (request == "0") {
                    bottomSelectionFeatures.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    bottomSelectionFeatures.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.GONE);
                }
                break;

            case "NUMBER OF SELECTIONS":
                int selectionsCount = Integer.parseInt(request);
                photosFragment.setNumberOfSelectedFiles(selectionsCount);
                break;

            default: break;
        }
    }


}