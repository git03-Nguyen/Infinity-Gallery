package edu.team08.infinitegallery.privacy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;
import edu.team08.infinitegallery.main.MainActivity;

public class PrivacyChangePatternActivity extends AppCompatActivity {
    //Properties and attributes
    TextView pattern_description;
    PatternLockView patternLockView;
    Button resetPattern;

    String passwordForPattern;
    String inputPasswordInPattern;

    //on- methods
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_login_pattern);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyPattern));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Privacy Pattern Change");

        pattern_description = (TextView) findViewById(R.id.privacyPattern_text);
        pattern_description.setText("Draw new pattern you want to make the verification");

        patternLockView = findViewById(R.id.login_patternView);
        resetPattern = (Button) findViewById(R.id.resetPattern);
        resetPattern.setVisibility(View.VISIBLE);

        resetPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mPref = getSharedPreferences("PATTERN_PASSWORD", Context.MODE_PRIVATE);
                if(mPref.contains("PASS")) {
                    ConfirmDialogBuilder.showConfirmDialog(
                            PrivacyChangePatternActivity.this,
                            "Confirm The clear",
                            "Are you sure to clear the pattern verification ?",
                            new Runnable() {
                                @Override
                                public void run() {
                                    Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(
                                            PrivacyChangePatternActivity.this,
                                            "Clearing ...",
                                            () -> {
                                                SharedPreferences.Editor editor = mPref.edit();
                                                editor.remove("PASS");
                                                editor.apply();

                                                finish();
                                            },
                                            () -> {
                                                onResume();
                                            }
                                    );
                                }
                            },
                            null
                    );
                } else {
                    Toast.makeText(PrivacyChangePatternActivity.this, "The pattern is cleared or not created yet.", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
                Log.d("PATTERN_CHANGE_TAG", "Pattern complete: " +
                        PatternLockUtils.patternToString(patternLockView, pattern));

                SharedPreferences myPref = getSharedPreferences("PATTERN_PASSWORD", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();
                inputPasswordInPattern = PatternLockUtils.patternToString(patternLockView, pattern);

                passwordForPattern = myPref.getString("PASS", null);

                if (inputPasswordInPattern.length() < 3) {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                    Toast.makeText(PrivacyChangePatternActivity.this,
                            "The pattern length must be greater than or equal to 3", Toast.LENGTH_LONG).show()
                    ;
                } else if((!myPref.contains("PASS")) || (passwordForPattern == null)
                || !PrivacyEncoder.SHA256_hashing(inputPasswordInPattern).equalsIgnoreCase(passwordForPattern)) {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);

                    ConfirmDialogBuilder.showConfirmDialog(
                            PrivacyChangePatternActivity.this,
                            "Confirm new pattern",
                            "Are you sure to change the pattern like that ?" +
                                    " You can just change the pattern once you access to the privacy folder" +
                                    " by other methods in case you've forgot the pattern.",
                            new Runnable() {
                                @Override
                                public void run() {
                                    Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(
                                            PrivacyChangePatternActivity.this,
                                            "Changing ...",
                                            () -> {
                                                editor.putString("PASS", PrivacyEncoder.SHA256_hashing(inputPasswordInPattern));
                                                editor.apply();

                                                finish();
                                            },
                                            () -> {
                                                onResume();
                                            }
                                    );
                                }
                            },
                            null
                    );
                }
                else {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                    Toast.makeText(PrivacyChangePatternActivity.this,
                            "The new pattern cannot be the same with the old one", Toast.LENGTH_LONG).show()
                    ;
                }
            }

            @Override
            public void onCleared() {
                Log.d(getClass().getName(), "Pattern has been cleared");
            }
        });
    }

}
