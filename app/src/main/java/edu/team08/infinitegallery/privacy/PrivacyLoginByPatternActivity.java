package edu.team08.infinitegallery.privacy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import edu.team08.infinitegallery.R;

public class PrivacyLoginByPatternActivity extends AppCompatActivity {
    //Properties and attributes

    PatternLockView patternLockView;
    String passwordForPattern;

    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_privacy_login);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyPattern));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        patternLockView = findViewById(R.id.login_patternView);


        //TODO: Delete this
        //this.passwordForPattern = "0123";


        SharedPreferences myPref = this.
                getSharedPreferences("PATTERN_PASSWORD", Context.MODE_PRIVATE);

        if(myPref.contains("PASS") == false) {
            this.passwordForPattern = null;
        } else {
            this.passwordForPattern = myPref.getString("PASS", null);
        }

        implementPatternLockLogic();
    }

    private void implementPatternLockLogic() {
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                Log.d(getClass().getName(), "Pattern drawing started");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
                Log.d(getClass().getName(), "Pattern progress: " +
                        PatternLockUtils.patternToString(patternLockView, progressPattern));
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                Log.d("PATTERN_COMPLETE_TAG", "Pattern complete: " +
                        PatternLockUtils.patternToString(patternLockView, pattern));

                String inputPatternPassword = PatternLockUtils.patternToString(patternLockView, pattern);

                if(PrivacyEncoder.SHA256_hashing(inputPatternPassword).equalsIgnoreCase(passwordForPattern)){
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);

                    Intent intent = new Intent(PrivacyLoginByPatternActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                    Toast.makeText(PrivacyLoginByPatternActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    Toast.makeText(PrivacyLoginByPatternActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCleared() {
                Log.d(getClass().getName(), "Pattern has been cleared");
            }
        });
    }
}
