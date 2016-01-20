package com.belatrix.habilidadessociolaborales.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class PreferencesHelper {

    public static final String PREFS_NAME = PreferencesHelper.class.getPackage().getName();

    private static PreferencesHelper instance;
    private Context context;

    public static PreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesHelper(context);
        }
        return instance;
    }

    private PreferencesHelper(Context context) {
        this.context = context;
    }

    /*public String getPreference(String preferenceName) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(preferenceName, null);
    }*/

    public void savePreference(String preferenceName, Object value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (value instanceof String) {
            editor.putString(preferenceName, (String) value);
        }else if (value instanceof Date){
            editor.putLong(preferenceName, ((Date)value).getTime());
        }
        // Commit the edits!
        editor.apply();
    }

    public SharedPreferences getPreferences(){
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
