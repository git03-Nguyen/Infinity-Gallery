package edu.team08.infinitegallery.singlephoto.edit.base;

import android.R;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog = null;
    private String mPermission = null;
    private ActivityResultLauncher permissionLauncher = null;

    public BaseActivity(){
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            isPermissionGranted(isGranted , mPermission);
        });
    }

    public Boolean requestPermission(String permission){
        Boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if(!isGranted){
            mPermission = permission;
            permissionLauncher.launch(permission);
        }

        return isGranted;
    }

    public void isPermissionGranted(Boolean isGranted, String permission){

    }

    public void makeFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    protected void showLoading(String message){
        mProgressDialog = new ProgressDialog(this);
        if(mProgressDialog != null){
            mProgressDialog.setMessage(message);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

    }

    protected void hideLoading(){
        if(mProgressDialog != null) mProgressDialog.dismiss();
    }

    protected void showSnackbar(String message){
        View view = findViewById(R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}