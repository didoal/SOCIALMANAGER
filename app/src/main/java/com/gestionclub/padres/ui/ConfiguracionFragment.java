package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import java.util.Locale;

public class ConfiguracionFragment extends Fragment {
    
    private TextView textViewIdiomaActual;
    private TextView textViewTemaActual;
    private Button buttonCambiarIdioma;
    private Button buttonCambiarTema;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        
        sharedPreferences = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        
        inicializarVistas(view);
        configurarListeners();
        actualizarInformacion();
        
        return view;
    }

    private void inicializarVistas(View view) {
        textViewIdiomaActual = view.findViewById(R.id.textViewIdiomaActual);
        textViewTemaActual = view.findViewById(R.id.textViewTemaActual);
        buttonCambiarIdioma = view.findViewById(R.id.buttonCambiarIdioma);
        buttonCambiarTema = view.findViewById(R.id.buttonCambiarTema);
    }
    
    private void configurarListeners() {
        buttonCambiarIdioma.setOnClickListener(v -> mostrarDialogoIdioma());
        buttonCambiarTema.setOnClickListener(v -> mostrarDialogoTema());
    }
    
    private void actualizarInformacion() {
        // Actualizar información del idioma actual
        String idiomaActual = sharedPreferences.getString("idioma", "es");
        String textoIdioma = idiomaActual.equals("gl") ? "Idioma actual: Galego" : "Idioma actual: Español";
        textViewIdiomaActual.setText(textoIdioma);
        
        // Actualizar información del tema actual
        int temaActual = sharedPreferences.getInt("tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        String textoTema;
        switch (temaActual) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                textoTema = "Tema actual: Oscuro";
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                textoTema = "Tema actual: Claro";
                break;
            default:
                textoTema = "Tema actual: Por Defecto";
                break;
        }
        textViewTemaActual.setText(textoTema);
    }
    
    private void mostrarDialogoIdioma() {
        String[] idiomas = {"Español", "Galego"};
        String[] codigos = {"es", "gl"};
        
        String idiomaActual = sharedPreferences.getString("idioma", "es");
        int seleccionActual = idiomaActual.equals("gl") ? 1 : 0;
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar Idioma")
            .setSingleChoiceItems(idiomas, seleccionActual, (dialog, which) -> {
                String codigoIdioma = codigos[which];
                cambiarIdioma(codigoIdioma);
                dialog.dismiss();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void mostrarDialogoTema() {
        String[] temas = {"Tema Claro", "Tema Oscuro", "Tema por Defecto"};
        int[] codigos = {AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM};
        
        int temaActual = sharedPreferences.getInt("tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int seleccionActual = 2; // Por defecto
        for (int i = 0; i < codigos.length; i++) {
            if (codigos[i] == temaActual) {
                seleccionActual = i;
                break;
            }
        }
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar Tema")
            .setSingleChoiceItems(temas, seleccionActual, (dialog, which) -> {
                int codigoTema = codigos[which];
                cambiarTema(codigoTema);
                dialog.dismiss();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
    
    private void cambiarIdioma(String codigoIdioma) {
        // Guardar preferencia
        sharedPreferences.edit().putString("idioma", codigoIdioma).apply();
        
        // Aplicar idioma
        Locale locale = new Locale(codigoIdioma);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        
        // Recrear solo la actividad actual para aplicar cambios instantáneamente
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
    
    private void cambiarTema(int codigoTema) {
        // Guardar preferencia
        sharedPreferences.edit().putInt("tema", codigoTema).apply();
        
        // Aplicar tema
        AppCompatDelegate.setDefaultNightMode(codigoTema);
        // Recrear solo la actividad actual para aplicar cambios instantáneamente
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
    
    private void reiniciarAplicacion() {
        Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    
            @Override
    public void onResume() {
        super.onResume();
        actualizarInformacion();
    }
} 