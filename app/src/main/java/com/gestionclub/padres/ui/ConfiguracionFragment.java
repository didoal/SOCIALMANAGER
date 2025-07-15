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
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import android.widget.EditText;
import android.widget.Toast;

public class ConfiguracionFragment extends Fragment {
    
    private TextView textViewIdiomaActual;
    private TextView textViewTemaActual;
    private Button buttonCambiarIdioma;
    private Button buttonCambiarTema;
    private SharedPreferences sharedPreferences;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewNombre;
    private TextView textViewEmail;
    private TextView textViewEquipo;
    private TextView textViewJugador;
    private Button buttonEditarPerfil;
    private Button buttonCambiarPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        
        sharedPreferences = requireContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarListeners();
        actualizarInformacion();
        cargarInformacionUsuario();
        
        return view;
    }

    private void inicializarVistas(View view) {
        textViewIdiomaActual = view.findViewById(R.id.textViewIdiomaActual);
        textViewTemaActual = view.findViewById(R.id.textViewTemaActual);
        buttonCambiarIdioma = view.findViewById(R.id.buttonCambiarIdioma);
        buttonCambiarTema = view.findViewById(R.id.buttonCambiarTema);
        // Usuario
        textViewNombre = view.findViewById(R.id.textViewNombreUsuario);
        textViewEmail = view.findViewById(R.id.textViewEmailUsuario);
        textViewEquipo = view.findViewById(R.id.textViewEquipoUsuario);
        textViewJugador = view.findViewById(R.id.textViewJugadorUsuario);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfilUsuario);
        buttonCambiarPassword = view.findViewById(R.id.buttonCambiarPasswordUsuario);
    }
    
    private void configurarListeners() {
        buttonCambiarIdioma.setOnClickListener(v -> mostrarDialogoIdioma());
        buttonCambiarTema.setOnClickListener(v -> mostrarDialogoTema());
        buttonEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());
        buttonCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());
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

    private void cargarInformacionUsuario() {
        if (usuarioActual != null) {
            textViewNombre.setText(usuarioActual.getNombre());
            textViewEmail.setText(usuarioActual.getEmail());
            String equipo = usuarioActual.getEquipo();
            textViewEquipo.setText(equipo != null && !equipo.isEmpty() ? equipo : "No asignado");
            String jugador = usuarioActual.getJugador();
            textViewJugador.setText(jugador != null && !jugador.isEmpty() ? jugador : "No asignado");
        }
    }

    private void mostrarDialogoEditarPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null);
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        // Pre-llenar con datos actuales
        editTextNombre.setText(usuarioActual.getNombre());
        editTextEmail.setText(usuarioActual.getEmail());
        editTextJugador.setText(usuarioActual.getJugador());
        builder.setView(dialogView)
                .setTitle("Editar Perfil")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = editTextNombre.getText().toString().trim();
                    String nuevoEmail = editTextEmail.getText().toString().trim();
                    String nuevoJugador = editTextJugador.getText().toString().trim();
                    if (nuevoNombre.isEmpty()) {
                        Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    usuarioActual.setNombre(nuevoNombre);
                    usuarioActual.setEmail(nuevoEmail);
                    usuarioActual.setJugador(nuevoJugador);
                    dataManager.actualizarUsuario(usuarioActual);
                    dataManager.guardarUsuarioActual(usuarioActual);
                    cargarInformacionUsuario();
                    Toast.makeText(requireContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoCambiarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cambiar_password, null);
        EditText editTextPasswordActual = dialogView.findViewById(R.id.editTextPasswordActual);
        EditText editTextPasswordNueva = dialogView.findViewById(R.id.editTextPasswordNueva);
        EditText editTextPasswordConfirmar = dialogView.findViewById(R.id.editTextPasswordConfirmar);
        builder.setView(dialogView)
                .setTitle("Cambiar Contraseña")
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String passwordActual = editTextPasswordActual.getText().toString();
                    String passwordNueva = editTextPasswordNueva.getText().toString();
                    String passwordConfirmar = editTextPasswordConfirmar.getText().toString();
                    if (passwordActual.isEmpty() || passwordNueva.isEmpty() || passwordConfirmar.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!passwordActual.equals(usuarioActual.getPassword())) {
                        Toast.makeText(requireContext(), "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!passwordNueva.equals(passwordConfirmar)) {
                        Toast.makeText(requireContext(), "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (passwordNueva.length() < 6) {
                        Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    usuarioActual.setPassword(passwordNueva);
                    dataManager.actualizarUsuario(usuarioActual);
                    dataManager.guardarUsuarioActual(usuarioActual);
                    Toast.makeText(requireContext(), "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
} 