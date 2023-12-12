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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.team08.infinitegallery.R;

public class PrivacyForgetPassActivity extends AppCompatActivity {
    //Properties and attributes
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
    private Button _applyChangeButton;
    private Spinner _secureQuesSpinner;
    private TextView _description;

    //on- methods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.privacy_forget_password_form);

        setContentView(R.layout.privacy_modify_password_form);
        //set toolbar
//        setSupportActionBar(findViewById(R.id.toolbarForPrivacyForgetPassword));
        setSupportActionBar(findViewById(R.id.toolbarForPrivacyModifyingPassword));
        getSupportActionBar().setTitle("PRIVACY CHANGING PASSWORD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeActivity();
    }

    //functional methods
    private void initializeActivity() {
         _description = (TextView) findViewById(R.id.modifyingPrivacyPassword_description);
        _passwordField = (EditText) findViewById(R.id.passwordField);
        _retypePasswordField = (EditText) findViewById(R.id.retypePasswordField);
        _secureAnsField = (EditText) findViewById(R.id.secure_ans);
        _showHideButton = (Button) findViewById(R.id.showTypeButton);
        _showRetypeButton = (Button) findViewById(R.id.showRetypeButton);
        _applyChangeButton = (Button) findViewById(R.id.signup_Signup);
        _secureQuesSpinner = findViewById(R.id.secure_ques_spinner);

        _description.setText("Enter the saved secure question and answer to change the password");

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

        _applyChangeButton.setText("Apply change");
        _applyChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCorrespondingPassword()) {
                    SharedPreferences mPref = getSharedPreferences("PASSWORD", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putString("PASS", PrivacyEncoder.SHA256_hashing(_password));
                    editor.commit();

                    Intent intent = new Intent(PrivacyForgetPassActivity.this, PrivacyLoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(PrivacyForgetPassActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PrivacyForgetPassActivity.this, _errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean checkCorrespondingPassword() {
        SharedPreferences mPref = getSharedPreferences("SECURE_PREF", Context.MODE_PRIVATE);

        selectedQuestion = mPref.getString("SECURE_QUES", null);
        enteredAnswer = mPref.getString("SECURE_ANS", null);

        String cur_selectedQuestion = _secureQuesSpinner.getSelectedItem().toString();
        String cur_enteredAnswer = _secureAnsField.getText().toString().toLowerCase().trim();

        if(!PrivacyEncoder.SHA256_hashing(cur_selectedQuestion).equals(selectedQuestion)
        || (!PrivacyEncoder.SHA256_hashing(cur_enteredAnswer).equals(enteredAnswer))) {
            _errorMsg = "Error: the secure question or answer is not corresponding with the saved data.";
            return false;
        }


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
}
