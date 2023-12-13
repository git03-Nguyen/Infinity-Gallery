package edu.team08.infinitegallery.main;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.favorites.FavoriteManager;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.optionalbums.AlbumsFragment;
import edu.team08.infinitegallery.optionmore.MoreFragment;
import edu.team08.infinitegallery.optionphotos.PhotosFragment;
import edu.team08.infinitegallery.privacy.PrivacyManager;
import edu.team08.infinitegallery.optionsearch.SearchFragment;
import edu.team08.infinitegallery.settings.AppConfig;
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

        if (AppConfig.getInstance(MainActivity.this).getSelectedLanguage())
        {
          Locale locale=new Locale("vi");
          Locale.setDefault(locale);
          getResources().getConfiguration().setLocale(locale);
            getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void changeStatusBar() {
        runOnUiThread(() -> {
            if (currentFragment instanceof PhotosFragment) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
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
        if (files.length == 0) return;
        // TODO: implement sharing multiple files feature
        Toast.makeText(this, "Sharing", Toast.LENGTH_SHORT).show();
    }

    private void trashMultiplePhotos(File[] files) {
        if (files.length == 0) return;
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getString(R.string.confirm_deletion_title),
                getString(R.string.confirm_deletion_message,files.length),
                new Runnable() {

                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(MainActivity.this, "Deleting ...",
                                () -> {
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
                                    photosFragment.onResume();
                                });

                    }
                },
                null);
    }

    private void hideMultiplePhotos(File[] files) {
        if (files.length == 0) return;
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                getString(R.string.confirm_hiding_title),
                getString(R.string.confirm_hiding_list_photos_message,files.length),
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
                                    photosFragment.onResume();
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