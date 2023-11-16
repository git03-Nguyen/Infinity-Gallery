package edu.team08.infinitegallery;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.team08.infinitegallery.albums.AlbumsFragment;
import edu.team08.infinitegallery.more.MoreFragment;
import edu.team08.infinitegallery.photos.PhotosFragment;
import edu.team08.infinitegallery.search.SearchFragment;
import edu.team08.infinitegallery.settings.AppConfig;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    private final int PERMISSIONS_REQUEST_CODE_1  = 100;
    private final int PERMISSIONS_REQUEST_CODE_2 = 2296;
    private static final int SETTINGS_REQUEST_CODE = 1;
    private PhotosFragment photosFragment;
    private AlbumsFragment albumsFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private BottomNavigationView bottomNavigationView;
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
            Toast.makeText(MainActivity.this, "Permissions have been granted in the past!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Permissions denied! Stopping app ...", Toast.LENGTH_SHORT).show();
                finish();
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
        MediaScannerConnection.scanFile(MainActivity.this, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, new String[] {"image/*"}, new MediaScannerConnection.OnScanCompletedListener()  {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        });

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
            return true;
        });


    }

    private void changeTheme(boolean isChecked) {
        if (isChecked) {
            setTheme(R.style.Theme_MyTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            AppConfig.getInstance(this).setNightMode(true);

        }
        else {
            setTheme(R.style.Theme_MyTheme);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            AppConfig.getInstance(this).setNightMode(false);
        }
    }

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch(sender) {


            default: break;
        }
    }
}