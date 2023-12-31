package edu.team08.infinitegallery.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private SharedPreferences sharedPreferences;
    private static AppConfig instance;
    private static final String NIGHT_MODE = "night_mode";
    private static final String TRASH_MODE = "trash_mode";
    private static final String TIME_LAPSE = "time_lapse";

    private static final String VIETNAMESE_LANGUAGE = "vietnamese_language";
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
        editor.putBoolean(VIETNAMESE_LANGUAGE,getSelectedLanguage());
        editor.putInt(TIME_LAPSE, getTimeLapse());
        editor.apply();
    }

    public boolean getNightMode() {
        return sharedPreferences.getBoolean(NIGHT_MODE, false);
    }

    public boolean getTrashMode() {
        return sharedPreferences.getBoolean(TRASH_MODE, true);
    }

    public int getTimeLapse() { return sharedPreferences.getInt(TIME_LAPSE, 1);}

    public boolean getSelectedLanguage() {
        return sharedPreferences.getBoolean(VIETNAMESE_LANGUAGE, false); // Default false
    }
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

    public void setTimeLapse(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getTimeLapse() == value) return;
        editor.remove(TIME_LAPSE);
        editor.putInt(TIME_LAPSE, value);
        editor.apply();
    }
    public void setSelectedLanguage(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (getSelectedLanguage()==value) return;
        editor.remove(VIETNAMESE_LANGUAGE);
        editor.putBoolean(VIETNAMESE_LANGUAGE, value);
        editor.apply();
    }
}
