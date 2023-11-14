package edu.team08.infinitegallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.team08.infinitegallery.albums.AlbumsFragment;
import edu.team08.infinitegallery.more.MoreFragment;
import edu.team08.infinitegallery.photos.PhotosFragment;
import edu.team08.infinitegallery.search.SearchFragment;

public class MainActivity extends AppCompatActivity implements MainCallbacks {
    private final int PERMISSION_REQUEST_CODE  = 100;
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

        String readImagePermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                android.Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        String writeImagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED){
            initApp();
            Toast.makeText(MainActivity.this, "Permission has been granted in the past!", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[] {readImagePermission, writeImagePermission},
                    PERMISSION_REQUEST_CODE
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initApp();
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied!"  + grantResults.length + ":" + grantResults[0] + ":" + PackageManager.PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
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