package edu.team08.infinitegallery;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import edu.team08.infinitegallery.main.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_CODE_1  = 1001;
    private final int PERMISSIONS_REQUEST_CODE_2 = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestAppPermissions();
    }

    private void requestAppPermissions() {
        if (checkAppPermission()) {
            Toast.makeText(this, R.string.permissions_granted, Toast.LENGTH_SHORT).show();
            startApplication();
            return;
        }
        
        String readPermission = (SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? READ_MEDIA_IMAGES : READ_EXTERNAL_STORAGE;
        String writePermission = WRITE_EXTERNAL_STORAGE;
        String internetPermission = INTERNET;
        String networkPermission = ACCESS_NETWORK_STATE;

        String[] permissions = new String[] {readPermission, writePermission, internetPermission, networkPermission};
        ActivityCompat.requestPermissions(SplashScreenActivity.this, permissions, PERMISSIONS_REQUEST_CODE_1);

    }

    private boolean checkAppPermission() {
        String readPermission = (SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? READ_MEDIA_IMAGES : READ_EXTERNAL_STORAGE;
        String writePermission = WRITE_EXTERNAL_STORAGE;
        String internetPermission = INTERNET;
        String networkPermission = ACCESS_NETWORK_STATE;

        boolean result = false;
        boolean isReadImagesAllowed = ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED;
        boolean isWriteImagesAllowed = ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED;
        boolean isInternetAllowed = ContextCompat.checkSelfPermission(this, internetPermission) == PackageManager.PERMISSION_GRANTED;
        boolean isNetworkStateAllowed = ContextCompat.checkSelfPermission(this, networkPermission) == PackageManager.PERMISSION_GRANTED;
        result = isReadImagesAllowed && isInternetAllowed && isNetworkStateAllowed;
        
        if (SDK_INT < 29) {
            result = result && isWriteImagesAllowed;
        }

        if (SDK_INT >= Build.VERSION_CODES.R) {
            result = result && Environment.isExternalStorageManager();
        }

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_1) {
            boolean successful = grantResults.length > 0;
            if (successful) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(SplashScreenActivity.this, R.string.permissions_denied, Toast.LENGTH_SHORT).show();
                    finish();
                }
                successful = successful && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }

            if (successful) {
                // Permission to copy, move, delete, edit files on external storage - (!) new for Android 11+
                if (SDK_INT >= Build.VERSION_CODES.R) {
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
                    startApplication();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_CODE_2) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Toast.makeText(this, R.string.permission_to_access_files_has_been_denied_stopping_app, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.permissions_granted, Toast.LENGTH_SHORT).show();
                    startApplication();
                }
            }
        }
    }

    private void startApplication() {
        MediaScannerConnection.scanFile(SplashScreenActivity.this, new String[] { Environment.getExternalStorageDirectory().getAbsolutePath() }, new String[] {"image/*"}, new MediaScannerConnection.OnScanCompletedListener()  {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}