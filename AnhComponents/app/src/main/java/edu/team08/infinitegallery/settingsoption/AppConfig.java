package edu.team08.infinitegallery.settingsoption;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private SharedPreferences sharedPreferences;
    private static AppConfig instance;
    private static final String NIGHT_MODE = "night_mode";
    private static final String TRASH_MODE = "trash_mode";
    private static final String TIME_LAPSE = "time_lapse";

    public static AppConfig getInstance(Context context) {
        if (instance == null)
            instance = new AppConfig(context);
        return instance;
    }

    private AppConfig(Context context) {
        sharedPreferences = context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
        initConfig();
    }

   private void initConfig() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NIGHT_MODE, getNightMode());
        editor.putBoolean(TRASH_MODE, getTrashMode());
        editor.apply();
    }

    public boolean getNightMode() {
        return sharedPreferences.getBoolean(NIGHT_MODE, false);
    }

    public boolean getTrashMode() {
        return sharedPreferences.getBoolean(TRASH_MODE, true);
    }

    public String getTimeLapse() { return sharedPreferences.getString(TIME_LAPSE, "1 seconds");}

    public void setNightMode(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getNightMode() == value) return;
        editor.remove(NIGHT_MODE);
        editor.putBoolean(NIGHT_MODE, value);
        editor.apply();
    }

    public void setTrashMode(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getTrashMode() == value) return;
        editor.remove(TRASH_MODE);
        editor.putBoolean(TRASH_MODE, value);
        editor.apply();
    }

    public void setTimeLapse(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getTimeLapse().equalsIgnoreCase(value)) return;
        editor.remove(TIME_LAPSE);
        editor.putString(TIME_LAPSE, value);
        editor.apply();
    }
}
