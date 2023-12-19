package edu.team08.infinitegallery.privacy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.team08.infinitegallery.R;

public class PrivacySignupActivity extends AppCompatActivity {
    //Secure questions
    String[] securityQuestions = {
            "What city were you born in?",
            "What is the name of your first pet?",
            "What is the name of your primary school?",
            "What is the name of your secondary school?",
            "What is the name of your high school?",
            "What is the name of your university?",
            "What is the name of your first pet?",
    };

    //Properties and attributes
    private String _password;
    private String retype_password;
    private String _errorMsg;
    private String selectedQuestion;
    private String enteredAnswer;

    //view binding
    private EditText _secureAnsField;
    private EditText _passwordField;
    private Button _showHideButton;
    private EditText _retypePasswordField;
    private Button _showRetypeButton;
    private Button _signupButton;
    private Spinner _secureQuesSpinner;
    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_privacy_modify_password);

        //set toolbar
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyModifyingPassword));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeActivity();
    }

    //functional methods
    private void initializeActivity() {
        _passwordField = (EditText) findViewById(R.id.passwordField);
        _retypePasswordField = (EditText) findViewById(R.id.retypePasswordField);
        _secureAnsField = (EditText) findViewById(R.id.secure_ans);
        _showHideButton = (Button) findViewById(R.id.showTypeButton);
        _showRetypeButton = (Button) findViewById(R.id.showRetypeButton);
        _signupButton = (Button) findViewById(R.id.signup_Signup);
        _secureQuesSpinner = findViewById(R.id.secure_ques_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, securityQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _secureQuesSpinner.setAdapter(adapter);


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
                    savePreferenceSecureQues_Ans();

                    Intent intent = new Intent(PrivacySignupActivity.this, PrivacyLoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(PrivacySignupActivity.this, "Create password successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PrivacySignupActivity.this, _errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void savePreferenceSecureQues_Ans() {
        SharedPreferences mPref = this.
                getSharedPreferences("SECURE_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        try {
            editor.putString("SECURE_QUES", PrivacyEncoder.SHA256_hashing(selectedQuestion));
            editor.putString("SECURE_ANS", PrivacyEncoder.SHA256_hashing(enteredAnswer));

            editor.commit();
        } catch (Exception e) {
            Log.e("Error in hashing!", e.getMessage());
        }
    }

    private boolean checkCorrespondingPassword() {
        _password = _passwordField.getText().toString();
        retype_password = _retypePasswordField.getText().toString();

        selectedQuestion = _secureQuesSpinner.getSelectedItem().toString();
        enteredAnswer = _secureAnsField.getText().toString().toLowerCase().trim();

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
                getSharedPreferences(PrivacyLoginActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        try {
            editor.putString(PrivacyLoginActivity.PREF_PASS_NAME, PrivacyEncoder.SHA256_hashing(_password));
            editor.commit();
        } catch (Exception e) {
            Log.e("Error in hashing!", e.getMessage());
        }
    }
}
