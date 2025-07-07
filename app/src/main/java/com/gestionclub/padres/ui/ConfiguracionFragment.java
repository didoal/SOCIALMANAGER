package com.gestionclub.padres.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;

public class ConfiguracionFragment extends Fragment {
    private Switch switchNotificaciones;
    private Switch switchSonido;
    private Spinner spinnerIdioma;
    private Spinner spinnerTema;
    private Switch switchPrivacidad;
    private Button buttonVerTutorial;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        prefs = requireContext().getSharedPreferences("config", 0);
        inicializarVistas(view);
        cargarPreferencias();
        configurarListeners();
        return view;
    }

    private void inicializarVistas(View view) {
        switchNotificaciones = view.findViewById(R.id.switchNotificaciones);
        switchSonido = view.findViewById(R.id.switchSonido);
        spinnerIdioma = view.findViewById(R.id.spinnerIdioma);
        spinnerTema = view.findViewById(R.id.spinnerTema);
        switchPrivacidad = view.findViewById(R.id.switchPrivacidad);
        buttonVerTutorial = view.findViewById(R.id.buttonVerTutorial);

        // Idiomas disponibles
        String[] idiomas = {"Español", "Inglés", "Gallego"};
        ArrayAdapter<String> adapterIdioma = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, idiomas);
        adapterIdioma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdioma.setAdapter(adapterIdioma);

        // Temas disponibles
        String[] temas = {"Claro", "Oscuro", "Azul Profesional"};
        ArrayAdapter<String> adapterTema = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, temas);
        adapterTema.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTema.setAdapter(adapterTema);
    }

    private void cargarPreferencias() {
        switchNotificaciones.setChecked(prefs.getBoolean("notificaciones", true));
        switchSonido.setChecked(prefs.getBoolean("sonido", true));
        switchPrivacidad.setChecked(prefs.getBoolean("privacidad", false));
        spinnerIdioma.setSelection(prefs.getInt("idioma", 0));
        spinnerTema.setSelection(prefs.getInt("tema", 2));
    }

    private void configurarListeners() {
        switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notificaciones", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
        });
        switchSonido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sonido", isChecked).apply();
        });
        switchPrivacidad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("privacidad", isChecked).apply();
        });
        spinnerIdioma.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("idioma", position).apply();
                // Aquí podrías recargar la app con el idioma seleccionado
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        spinnerTema.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt("tema", position).apply();
                // Aquí podrías aplicar el tema en tiempo real
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        buttonVerTutorial.setOnClickListener(v -> {
            // Mostrar el tutorial
            if (getActivity() instanceof MainActivity) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TutorialFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });
    }
} 