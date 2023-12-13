package edu.team08.infinitegallery.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

import edu.team08.infinitegallery.main.MainActivity;
import edu.team08.infinitegallery.R;
import edu.team08.infinitegallery.optionalbums.SingleAlbumActivity;
import edu.team08.infinitegallery.slideshow.SlideShowActivity;

public class SettingsActivity extends AppCompatActivity {
    private SwitchMaterial nightModeSwitch, trashModeSwitch, languageSwitch;
    private CardView slideshowCard;
    private SeekBar durationBar;
    private static int delayedValue = 1;
    private TextView durationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbarSettings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nightModeSwitch = findViewById(R.id.sNightMode);
        trashModeSwitch = findViewById(R.id.sTrashMode);
        languageSwitch = findViewById(R.id.languageMode);

        slideshowCard = findViewById(R.id.slideshow);
        durationText = findViewById(R.id.durationText);
        durationBar = findViewById(R.id.durationBar);
        String[] list = getResources().getStringArray(R.array.choice_items);

        nightModeSwitch.setChecked(AppConfig.getInstance(this).getNightMode());
        trashModeSwitch.setChecked(AppConfig.getInstance(this).getTrashMode());
        languageSwitch.setChecked(AppConfig.getInstance(this).getSelectedLanguage());
        durationText.setText(AppConfig.getInstance(this).getTimeLapse() + "s");
        durationBar.setProgress(AppConfig.getInstance(this).getTimeLapse());

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
                AppConfig.getInstance(SettingsActivity.this).setSelectedLanguage(isChecked);
                if (isChecked) {
                    setLocale("vi");
                } else {
                    setLocale("en");
                }
                // Set selected language in AppConfig

                restartMainActivity();
            }
        });

        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AppConfig.getInstance(SettingsActivity.this).setTimeLapse(progress);
                durationText.setText(String.valueOf(progress) + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //delayedValue = progress;
                AppConfig.getInstance(SettingsActivity.this).setTimeLapse(progress);
                durationText.setText(String.valueOf(progress) + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /*slideshowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                View view = LayoutInflater.from(v.getRootView().getContext())
                        .inflate(R.layout.slideshow_dialog, null, false);
                RadioGroup durationGroup = view.findViewById(R.id.durRadioGroup);
                // Set default check
                //String radioId = "duration_" + durationText.getText();
                // On check changed
                durationGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if(checkedId == R.id.duration_1s){
                        delayedValue = 1;
                    } else if(checkedId == R.id.duration_2s){
                        delayedValue = 2;
                    } else if(checkedId == R.id.duration_3s){
                        delayedValue = 3;
                    } else if(checkedId == R.id.duration_4s){
                        delayedValue = 4;
                    } else if(checkedId == R.id.duration_5s) {
                        delayedValue = 5;
                    } else {
                        delayedValue = 10;
                    }
                    durationText.setText(String.valueOf(delayedValue) + "s");
                    Toast.makeText(v.getRootView().getContext(), "Duration" + String.valueOf(delayedValue) + "s", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(SettingsActivity.this, SlideShowActivity.class);
                    myIntent.putExtra("delayedValue", delayedValue);
                });
                builder.setView(view);
                builder.show();
            }

            private void showDurationDialog() {

            }
        });*/
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
