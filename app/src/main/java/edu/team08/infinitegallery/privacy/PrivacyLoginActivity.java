package edu.team08.infinitegallery.privacy;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import edu.team08.infinitegallery.R;

public class PrivacyLoginActivity extends AppCompatActivity {

    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    //Properties and attributes
    private String _password;
    private String input_password;

    public static final String PREF_NAME = "PASSWORD";
    public static final String PREF_PASS_NAME = "PASS";

    //view binding
    private EditText _passwordField;
    private Button _showHideButton;
    private Button _loginButton;
    private Button _forgotPasswordButton;
    private ImageButton _fingerPrintButton;
    private ImageButton _patternButton;

    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_login_form);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyPassword));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        initializeBiometricPrompt();

        initializeActivity();
    }

    private void initializeBiometricPrompt() {

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(PrivacyLoginActivity.this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PrivacyLoginActivity.this, PrivacyActivity.class);
                startActivity(intent);
                Toast.makeText(PrivacyLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login Privacy Folder using your Fingerprint !")
                .setSubtitle("Please put your finger into the sensor in order to take the verification")
                .setNegativeButtonText("Cancel")
                .build()
        ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            this.finish();
        } else {

            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //functional methods
    private void initializeActivity() {
        _passwordField = (EditText) findViewById(R.id.passwordField);
        _showHideButton = (Button) findViewById(R.id.showHideButton);
        _loginButton = (Button) findViewById(R.id.login_Login);
        _forgotPasswordButton = (Button) findViewById(R.id.login_Forgot);
        _fingerPrintButton = (ImageButton) findViewById(R.id.login_FingerprintLogin);
        _patternButton = (ImageButton) findViewById(R.id.login_PatternLogin);

        //Click Button to show/hide password field
        _showHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_showHideButton.getText().toString().equals("Show")) {
                    _passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    _showHideButton.setText("Hide");
                } else {
                    _passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    _showHideButton.setText("Show");
                }

            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_password = _passwordField.getText().toString();
                _passwordField.setText(null);
                //Empty password field
                if (input_password == null)
                {
                    input_password = "";
                }

                if(isAuthorized()) {
                    Intent intent = new Intent(PrivacyLoginActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                    Toast.makeText(PrivacyLoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PrivacyLoginActivity.this, "Login failed due to wrong password", Toast.LENGTH_SHORT).show();

                }

            }
        });

        _forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrivacyLoginActivity.this, PrivacyForgetPassActivity.class);
                startActivity(intent);
                finish();
            }
        });

        _fingerPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAvailableBiometric()) {
                    biometricPrompt.authenticate(promptInfo);
                }
            }
        });

        _patternButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHavingPattern()) {
                    Intent intent = new Intent(PrivacyLoginActivity.this, PrivacyLoginByPatternActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
}

    private boolean isAuthorized() {
        SharedPreferences mPref = this.
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if(mPref.contains("PASS") == false) {
            _password = null;
            return true;
        } else {
            _password = mPref.getString(PREF_PASS_NAME, null);
        }
        Log.d("PASSWORD_TAG", "Cleared password: " + mPref.getString("PASS", null));


        //always correct if there is no password yet
        if (_password == null)
        {
            Log.d("PASSWORD_TAG", "The password is null");
            return true;
        }

        try {
            if (PrivacyEncoder.SHA256_hashing(input_password).equals(_password))
            {
                return true;
            }
        } catch (Exception e)
        {
            Log.e("Error in hashing!", e.getMessage());
        }

        return false;
    }

    private boolean checkAvailableBiometric() {
        BiometricManager biometricManager = BiometricManager.from(PrivacyLoginActivity.this);
        boolean result = false;

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(PrivacyLoginActivity.this, "App can authenticate using biometrics.", Toast.LENGTH_SHORT).show();
                result = true;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(PrivacyLoginActivity.this, "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(PrivacyLoginActivity.this, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();

                // Biometric features are currently unavailable
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 202);
                break;

            default: break;
        }

        return result;
    }

    boolean isHavingPattern() {
        SharedPreferences mPref = this.getSharedPreferences("PATTERN_PASSWORD", Context.MODE_PRIVATE);

        String password = mPref.getString("PASS", null);
        boolean state = mPref.contains("PASS") || (password != null);

        Toast.makeText(PrivacyLoginActivity.this, "Pattern state: " + state, Toast.LENGTH_SHORT).show();
        Log.d("PATTERN_STATE", "The pattern password: " + mPref.getString("PASS", null));
        return state;
    }
}
