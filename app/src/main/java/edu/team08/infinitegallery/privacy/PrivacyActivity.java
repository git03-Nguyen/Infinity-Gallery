package edu.team08.infinitegallery.privacy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import edu.team08.infinitegallery.optionphotos.PhotosAdapter;
import edu.team08.infinitegallery.optionsettings.SettingsActivity;

public class PrivacyActivity extends AppCompatActivity {
    //adapter, manager usage list
    PhotosAdapter photosAdapter;
    PrivacyManager privacyManager;

    //Properties and attributes
    RecyclerView photosRecView;
    ViewSwitcher viewSwitcher;
    File[] privateFileList;

    private final int spanCount = 4;


    //on- methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacy));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //view binding
        photosRecView = findViewById(R.id.privacyRecView);
        viewSwitcher = findViewById(R.id.privacy_viewSwitcher);

        privacyManager = new PrivacyManager(this);
        privateFileList = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        privateFileList = privacyManager.getAllPrivacyFiles();

        //replace the viewSwitcher
        if (privateFileList.length > 0) {
            if (photosRecView.getId() == viewSwitcher.getNextView().getId()) {
                viewSwitcher.showNext();
            }
        } else if (R.id.emptyView == viewSwitcher.getNextView().getId()) {
            viewSwitcher.showNext();
        }

        photosAdapter = new PhotosAdapter(this, Arrays.asList(privateFileList), spanCount);
        photosRecView.setAdapter(photosAdapter);
        photosRecView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_privacy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else if (itemId == R.id.privacymenu_Reveal) {
            this.revealAllPhotos();
        } else if (itemId == R.id.privacymenu_Select) {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.privacymenu_ResetPassword) {
            Intent myIntent = new Intent(PrivacyActivity.this, PrivacySignupActivity.class);
            startActivity(myIntent, null);
        }
//        else if (itemId == R.id.privacymenu_ClearPassword) {
//            SharedPreferences mPref = getSharedPreferences("PASSWORD", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = mPref.edit();
//            editor.putString("PASS", "null");
//            editor.commit();
//
//        }
        else if (itemId == R.id.privacymenu_DeleteAll) {
            this.deleteAllPrivateFiles();
        } else if (itemId == R.id.privacymenu_Settings) {
            Intent myIntent = new Intent(PrivacyActivity.this, SettingsActivity.class);
            startActivity(myIntent, null);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //functional methods
    private void revealAllPhotos() {
        privateFileList = privacyManager.getAllPrivacyFiles();
        if (privateFileList.length == 0) {
            Toast.makeText(this, "No private file found to reveal", Toast.LENGTH_SHORT).show();
            return;
        }

        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm The Reveal",
                "Are you sure to reveal all the private photos?",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(PrivacyActivity.this, "Revealing ...", () -> {
                                    try {
                                        for (File privateFile: privateFileList) {
                                            privacyManager.revealPhoto(privateFile);
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                () -> {
                                    onResume();
                                });

                    }
                },
                null
        );
    }


    public void deleteAllPrivateFiles() {
        privateFileList = privacyManager.getAllPrivacyFiles();
        if (privateFileList.length == 0) {
            Toast.makeText(this, "No file found to be deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        ConfirmDialogBuilder.showConfirmDialog(
                this,
                "Confirm deleting all the private files",
                "Are you sure to delete the whole private files? This action cannot be undone.",
                new Runnable() {
                    @Override
                    public void run() {
                        Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(PrivacyActivity.this, "Deleting ...", () -> {
                                    for (File privateFile: privateFileList) {
                                        privacyManager.deletePermanent(privateFile);
                                    }
                                },
                                () -> {
                                    onResume();
                                });

                    }
                },
                null);
    }

    //getters - setters




}
