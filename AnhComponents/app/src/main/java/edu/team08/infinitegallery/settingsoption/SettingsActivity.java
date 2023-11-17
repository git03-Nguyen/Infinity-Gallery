package edu.team08.infinitegallery.settingsoption;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import edu.team08.infinitegallery.MainActivity;
import edu.team08.infinitegallery.R;

public class SettingsActivity extends AppCompatActivity {
    private SwitchMaterial nightModeSwitch, trashModeSwitch;
    private TextView timeSlideShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbarSettings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nightModeSwitch = findViewById(R.id.sNightMode);
        trashModeSwitch = findViewById(R.id.sTrashMode);

        String[] list = getResources().getStringArray(R.array.choice_items);

        nightModeSwitch.setChecked(AppConfig.getInstance(this).getNightMode());
        trashModeSwitch.setChecked(AppConfig.getInstance(this).getTrashMode());
//        timeSlideShow.setText("After " + AppConfig.getInstance(this).getTimeLapse());

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppConfig.getInstance(SettingsActivity.this).setNightMode(isChecked);
                TaskStackBuilder.create(SettingsActivity.this)
                        .addNextIntent(new Intent(SettingsActivity.this, MainActivity.class))
                        .startActivities();
            }
        });
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