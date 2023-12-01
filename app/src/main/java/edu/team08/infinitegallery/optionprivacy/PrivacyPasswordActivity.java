package edu.team08.infinitegallery.optionprivacy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.helpers.ConfirmDialogBuilder;
import edu.team08.infinitegallery.helpers.ProgressDialogBuilder;

public class PrivacyPasswordActivity extends AppCompatActivity {

    //Properties and attributes
    private String _password;
    private String input_password;

    public static final String PREF_NAME = "PASSWORD";
    public static final String PREF_PASS_NAME = "PASS";

    //view binding
    private EditText _passwordField;
    private Button _showHideButton;
    private Button _loginButton;
    private Button _resetButton;


    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_login_form);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyPassword));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
//        if (intent.hasExtra("password")) {
//            this._password = intent.getStringExtra("password");
//        }

        initComponents();
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
    private void initComponents() {
        _passwordField = (EditText) findViewById(R.id.passwordField);
        _showHideButton = (Button) findViewById(R.id.showHideButton);
        _loginButton = (Button) findViewById(R.id.login_Login);
        _resetButton = (Button) findViewById(R.id.login_Reset);

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

                //TODO: check the password and redirect properly
                if(isAuthorized()) {
                    Intent intent = new Intent(PrivacyPasswordActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                }

            }
        });

        _resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDialogBuilder.showConfirmDialog(
                        PrivacyPasswordActivity.this,
                        "Confirm reset password",
                        "By resetting password, you can access \\\"Privacy\\\" \" +\n" +
                                "                        \"without typing password, but every pictures in \\\"Privacy\\\" will be deleted\" +\n" +
                                "                        \"\\nDo you want to reset password?",
                        new Runnable() {
                            @Override
                            public void run() {
                                Dialog progressDialog = ProgressDialogBuilder.buildProgressDialog(PrivacyPasswordActivity.this, "Reseting ...",
                                        () -> {
                                            Intent intent = new Intent(PrivacyPasswordActivity.this, PrivacySignupActivity.class);
                                            startActivity(intent);
                                        },
                                        () -> {
                                            //DO NOTHING
                                        });

                            }
                        },
                        null
                );
            }
        });

}

    private boolean isAuthorized() {
        SharedPreferences mPref = this.
                getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String password = mPref.getString(PREF_PASS_NAME, null);
        _password = password;

        //always correct if there is no password yet
        if (input_password == null)
        {
            return true;
        }

        try {
            if (PrivacyEncodingPassword.SHA256(input_password).equals(_password))
            {
                return true;
            }
        } catch (Exception e)
        {
            Log.e("Error in hashing!", e.getMessage());
        }

        return false;
    }
}
