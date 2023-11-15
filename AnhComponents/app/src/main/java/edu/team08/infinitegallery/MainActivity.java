package edu.team08.infinitegallery;

import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.team08.infinitegallery.albums.AlbumsFragment;
import edu.team08.infinitegallery.more.MoreFragment;
import edu.team08.infinitegallery.photos.PhotosFragment;
import edu.team08.infinitegallery.search.SearchFragment;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    private final int PERMISSION_REQUEST_READ  = 100;
    private final int PERMISSION_REQUEST_WRITE = 101;
    private final int PERMISSION_REQUEST_DELETE = 2296;
    private PhotosFragment photosFragment;
    private AlbumsFragment albumsFragment;
    private SearchFragment searchFragment;
    private MoreFragment moreFragment;
    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initApp();
    }

    private void requestPermissions() {
        String readImagePermission = (SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                android.Manifest.permission.READ_MEDIA_IMAGES :
                Manifest.permission.READ_EXTERNAL_STORAGE;
        String writeImagePermission = WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this, "Permission has been granted in the past!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {readImagePermission, writeImagePermission}, PERMISSION_REQUEST_READ);
        }

        // Manage files permission
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {return;}
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, PERMISSION_REQUEST_DELETE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_REQUEST_DELETE);
            }
        } else {
            //below android 11 - maybe WRITE_EXTERNAL_STORAGE

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied!"  + grantResults.length + ":" + grantResults[0] + ":" + PackageManager.PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_DELETE) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {
                    Toast.makeText(this, "The app must have access file permission!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void initApp(){

        photosFragment = PhotosFragment.newInstance(MainActivity.this);
        albumsFragment = AlbumsFragment.newInstance("", "");
        searchFragment = SearchFragment.newInstance("", "");
        moreFragment = MoreFragment.newInstance("", "");
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

    @Override
    public void onEmitMsgFromFragToMain(String sender, String request) {
        switch(sender) {


            default: break;
        }
    }
}