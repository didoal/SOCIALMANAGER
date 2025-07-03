package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.util.SessionManager;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        Button btnPerfil = findViewById(R.id.btn_perfil);
        Button btnCalendario = findViewById(R.id.btn_calendario);
        Button btnEstadisticas = findViewById(R.id.btn_estadisticas);
        Button btnAsistencia = findViewById(R.id.btn_asistencia);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Mostrar u ocultar botón de estadísticas según el rol
        String userJson = sessionManager.getUser();
        Usuario usuario = new Gson().fromJson(userJson, Usuario.class);
        if (usuario == null) {
            btnEstadisticas.setVisibility(View.GONE);
            btnAsistencia.setVisibility(View.GONE);
        } else if ("administrador".equalsIgnoreCase(usuario.getRol())) {
            btnAsistencia.setVisibility(View.GONE);
        } else {
            btnEstadisticas.setVisibility(View.GONE);
        }

        btnPerfil.setOnClickListener(v -> mostrarFragmento(new PerfilFragment()));
        btnCalendario.setOnClickListener(v -> mostrarFragmento(new CalendarioFragment()));
        btnEstadisticas.setOnClickListener(v -> mostrarFragmento(new com.gestionclub.admin.ui.EstadisticasFragment()));
        btnAsistencia.setOnClickListener(v -> mostrarFragmento(new AsistenciaFragment()));
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Mostrar fragmento de perfil por defecto
        if (savedInstanceState == null) {
            mostrarFragmento(new PerfilFragment());
        }
    }

    private void mostrarFragmento(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
} 