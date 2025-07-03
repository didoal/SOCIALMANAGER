package com.gestionclub.padres.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER = "user";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(String userJson) {
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    public String getUser() {
        return prefs.getString(KEY_USER, null);
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
} 