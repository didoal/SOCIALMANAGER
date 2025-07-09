package com.gestionclub.padres.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class LocaleHelper {
    
    private static final String PREF_NAME = "config";
    private static final String KEY_LANGUAGE = "idioma";
    private static final String KEY_THEME = "tema";
    
    public static void setLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String language = "es"; // valor por defecto
        
        try {
            // Intentar leer como String primero
            language = prefs.getString(KEY_LANGUAGE, "es");
        } catch (ClassCastException e) {
            // Si falla, intentar leer como Integer y convertir
            try {
                int languageCode = prefs.getInt(KEY_LANGUAGE, 0);
                language = languageCode == 1 ? "gl" : "es"; // 0=español, 1=gallego
                // Guardar como String para futuras lecturas
                prefs.edit().putString(KEY_LANGUAGE, language).apply();
            } catch (Exception ex) {
                // Si todo falla, usar español por defecto
                language = "es";
                prefs.edit().putString(KEY_LANGUAGE, language).apply();
            }
        }
        
        updateResources(context, language);
    }
    
    public static void setTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int theme = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }
    
    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String language = "es"; // valor por defecto
        
        try {
            language = prefs.getString(KEY_LANGUAGE, "es");
        } catch (ClassCastException e) {
            // Si falla, intentar leer como Integer y convertir
            try {
                int languageCode = prefs.getInt(KEY_LANGUAGE, 0);
                language = languageCode == 1 ? "gl" : "es";
                // Guardar como String para futuras lecturas
                prefs.edit().putString(KEY_LANGUAGE, language).apply();
            } catch (Exception ex) {
                language = "es";
                prefs.edit().putString(KEY_LANGUAGE, language).apply();
            }
        }
        
        return language;
    }
    
    public static int getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
} 