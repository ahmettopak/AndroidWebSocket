package com.ahmet.androidwebsocket.tinydb;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/26/2024
 */

import android.content.Context;
import android.content.SharedPreferences;

public class TinyDB {

    private SharedPreferences preferences;

    public TinyDB(Context context) {
        preferences = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
}
