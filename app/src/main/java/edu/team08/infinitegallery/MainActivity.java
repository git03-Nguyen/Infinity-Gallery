package edu.team08.infinitegallery;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.team08.infinitegallery.favorite.FavoriteManager;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.optionalbums.AlbumsFragment;
import edu.team08.infinitegallery.optionmore.MoreFragment;
import edu.team08.infinitegallery.optionphotos.PhotosFragment;

import edu.team08.infinitegallery.optionprivacy.PrivacyManager;
import edu.team08.infinitegallery.optionprivacy.PrivacyPasswordActivity;

import edu.team08.infinitegallery.optionsearch.SearchFragment;
import edu.team08.infinitegallery.optionsettings.AppConfig;
import edu.team08.infinitegallery.singlephoto.SinglePhotoActivity;
import edu.team08.infinitegallery.trashbin.TrashAdapter;
import edu.team08.infinitegallery.trashbin.TrashBinActivity;
import edu.team08.infinitegallery.trashbin.TrashBinManager;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    private final int PERMISSIONS_REQUEST_CODE_1  = 100;
    private final int PERMISSIONS_REQUEST_CODE_2 = 2296;
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
        requestPermissions();
        initApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanMediaOnStorage(null);
    }



    private void requestPermissions() {
        String readPermission = (SDK_INT >= VERSION_CODES.TIRAMISU) ? READ_MEDIA_IMAGES : READ_EXTERNAL_STORAGE;
        String writePermission = WRITE_EXTERNAL_STORAGE;
        String internetPermission = INTERNET;
        String networkPermission = ACCESS_NETWORK_STATE;

        Boolean isReadImagesAllowed = ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED;
        Boolean isWriteImagesAllowed = ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED;
        Boolean isInternetAllowed = ContextCompat.checkSelfPermission(this, internetPermission) == PackageManager.PERMISSION_GRANTED;
        Boolean isNetworkStateAllowed = ContextCompat.checkSelfPermission(this, networkPermission) == PackageManager.PERMISSION_GRANTED;
        Boolean successful = isReadImagesAllowed && isInternetAllowed && isNetworkStateAllowed;
        if (SDK_INT < 34) {
            successful = successful && isWriteImagesAllowed;
        }

        if (successful){
            Toast.makeText(MainActivity.this, "Permissions granted!", Toast.LENGTH_SHORT).show();
        } else {
            String[] permissions = new String[] {readPermission, writePermission, internetPermission, networkPermission};
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSIONS_REQUEST_CODE_1);
        }

        // Permission to copy, move, delete, edit files on external storage - (!) new for Android 11+
        if (SDK_INT >= VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {return;}
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, PERMISSIONS_REQUEST_CODE_2);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSIONS_REQUEST_CODE_2);
            }
        } else {
            //below android 11 - maybe WRITE_EXTERNAL_STORAGE

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_1) {
            Boolean successful = grantResults.length > 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    successful = false;
                    break;
                }
            }
            if (successful) {
                Toast.makeText(MainActivity.this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_CODE_2) {
            if (SDK_INT >= VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission to access files has been denied! Stopping app...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
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
                "Are you sure to move this photo to the trash?",
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
                "Are you sure to move this photo to the privacy list ?",
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