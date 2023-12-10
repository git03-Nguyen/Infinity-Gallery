package edu.team08.infinitegallery.settings;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.R;

public class SettingsActivity extends AppCompatActivity {
    private SwitchMaterial nightModeSwitch, trashModeSwitch, languageSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbarSettings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nightModeSwitch = findViewById(R.id.sNightMode);
        trashModeSwitch = findViewById(R.id.sTrashMode);
        languageSwitch = findViewById(R.id.languageMode);

        nightModeSwitch.setChecked(AppConfig.getInstance(this).getNightMode());
        trashModeSwitch.setChecked(AppConfig.getInstance(this).getTrashMode());
        languageSwitch.setChecked(AppConfig.getInstance(this).getSelectedLanguage());

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppConfig.getInstance(SettingsActivity.this).setNightMode(isChecked);
                restartMainActivity();
            }
        });

        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLocale("vi");
                } else {
                    setLocale("en");
                }
                // Set selected language in AppConfig
                AppConfig.getInstance(SettingsActivity.this).setSelectedLanguage(isChecked);

                restartMainActivity();
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        getResources().getConfiguration().setLocale(locale);
       getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
    }

    private void restartMainActivity() {
        TaskStackBuilder.create(SettingsActivity.this)
                .addNextIntent(new Intent(SettingsActivity.this, MainActivity.class))
                .startActivities();

//        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        } else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
