package edu.team08.infinitegallery.optionprivacy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.team08.infinitegallery.R;

public class PrivacySignupActivity extends AppCompatActivity {

    //Properties and attributes
    private String _password;
    private String retype_password;
    private String _errorMsg;


    //view binding
    private EditText _passwordField;
    private Button _showHideButton;
    private EditText _retypePasswordField;
    private Button _showRetypeButton;
    private Button _signupButton;

    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_signup_form);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacySignup));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    //functional methods
    private void initComponents() {
        _passwordField = (EditText) findViewById(R.id.passwordField);
        _retypePasswordField = (EditText) findViewById(R.id.retypePasswordField);
        _showHideButton = (Button) findViewById(R.id.showTypeButton);
        _showRetypeButton = (Button) findViewById(R.id.showRetypeButton);
        _signupButton = (Button) findViewById(R.id.signup_Signup);

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

        _showRetypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_showRetypeButton.getText().toString().equals("Show")) {
                    _retypePasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    _showRetypeButton.setText("Hide");

                } else {
                    _retypePasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    _showRetypeButton.setText("Show");

                }

            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCorrespondingPassword()) {
                    savePreferencePassword();

                    Intent intent = new Intent(PrivacySignupActivity.this, PrivacyPasswordActivity.class);
//                    intent.putExtra("password", _password);
                    startActivity(intent);
                    Toast.makeText(PrivacySignupActivity.this, "Create password successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrivacySignupActivity.this, _errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checkCorrespondingPassword() {
        _password = _passwordField.getText().toString();
        retype_password = _retypePasswordField.getText().toString();

        if (null == _password || null == retype_password ||
                _password.equals("") || retype_password.equals("")) {
            _errorMsg = "Error: password or retype password must not be empty";
            return false;
        }

        if (!_password.equals(retype_password)) {
            _errorMsg = "Error: password and retype password must be the same";
            return false;
        }

        return true;
    }

    void savePreferencePassword() {
        SharedPreferences mPref = this.
                getSharedPreferences(PrivacyPasswordActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        try {
            editor.putString(PrivacyPasswordActivity.PREF_PASS_NAME, PrivacyEncodingPassword.SHA256(_password));
            editor.commit();
        } catch (Exception e) {
            Log.e("Error in hashing!", e.getMessage());
        }
    }
}
