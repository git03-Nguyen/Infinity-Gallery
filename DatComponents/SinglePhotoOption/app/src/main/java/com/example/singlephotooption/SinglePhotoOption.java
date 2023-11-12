package com.example.singlephotooption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SinglePhotoOption extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    DisplaySinglePhotoFragment fragment;
    FragmentTransaction ft;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo_option);

        String readImagePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
            Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if(ContextCompat.checkSelfPermission(this, readImagePermission) == PackageManager.PERMISSION_GRANTED)
        {
            initApp();
            Toast.makeText(SinglePhotoOption.this, "Permission granted in the past!", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(SinglePhotoOption.this,
                    new String[]{readImagePermission}, 1);
        }
    }

    private void uncheckAllItemsBottomMenu(){
        Menu menu = bottomNavigationView.getMenu();
        menu.setGroupCheckable(0, true, false);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
        menu.setGroupCheckable(0, true, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initApp();
                Toast.makeText(SinglePhotoOption.this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SinglePhotoOption.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initApp() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        uncheckAllItemsBottomMenu();

        fragment = new DisplaySinglePhotoFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentHolder, fragment); ft.commit();
    }
}