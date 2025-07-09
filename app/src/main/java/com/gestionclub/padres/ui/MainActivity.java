package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.util.SessionManager;
import com.gestionclub.padres.data.DataManager;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DataManager dataManager;
    private Usuario usuarioActual;

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
        dataManager = new DataManager(this);
        
        usuarioActual = dataManager.getUsuarioActual();
        if (usuarioActual == null) {
            String userJson = sessionManager.getUser();
            if (userJson != null) {
                usuarioActual = new Gson().fromJson(userJson, Usuario.class);
                if (usuarioActual != null) {
                    dataManager.guardarUsuarioActual(usuarioActual);
                }
            }
        }

        inicializarVistas();
        configurarMenuLateral();
        actualizarHeaderUsuario();
        configurarVisibilidadMenu();
        actualizarContadorNotificaciones();

        if (dataManager.getNotificaciones().isEmpty()) {
            dataManager.crearDatosEjemploNotificaciones();
        }

        boolean tutorialVisto = getSharedPreferences("config", 0).getBoolean("tutorial_visto", false);
        if (!tutorialVisto) {
            mostrarFragmento(new TutorialFragment());
            return;
        }

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_inicio);
            mostrarFragmento(new DashboardFragment());
        }
    }

    private void inicializarVistas() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configurarMenuLateral() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, (Toolbar) findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void actualizarHeaderUsuario() {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUsuarioHeader = headerView.findViewById(R.id.textViewUsuarioHeader);

        if (usuarioActual != null && textViewUsuarioHeader != null) {
            String infoUsuario = usuarioActual.getNombre();
            if (usuarioActual.getEquipo() != null) {
                infoUsuario += " - " + usuarioActual.getEquipo();
            }
            textViewUsuarioHeader.setText(infoUsuario);
        }
    }

    private void configurarVisibilidadMenu() {
        boolean esAdmin = usuarioActual != null && usuarioActual.isEsAdmin();
        navigationView.getMenu().setGroupVisible(R.id.nav_admin, esAdmin);
    }

    private void actualizarContadorNotificaciones() {
        int noLeidas = dataManager.getNotificacionesNoLeidas();
        MenuItem menuNotificaciones = navigationView.getMenu().findItem(R.id.nav_notificaciones);
        
        if (noLeidas > 0) {
            menuNotificaciones.setTitle("Notificaciones (" + noLeidas + ")");
        } else {
            menuNotificaciones.setTitle("Notificaciones");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String titulo = "";

        int id = item.getItemId();
        if (id == R.id.nav_inicio) {
            fragment = new DashboardFragment();
            titulo = "Inicio";
        } else if (id == R.id.nav_mi_equipo) {
            fragment = new PerfilFragment();
            titulo = "Mi Equipo";
        } else if (id == R.id.nav_calendario) {
            fragment = new CalendarioFragment();
            titulo = "Calendario";
        } else if (id == R.id.nav_chat) {
            fragment = new ChatFragment();
            titulo = "Chat";
        } else if (id == R.id.nav_asistencia) {
            fragment = new AsistenciaFragment();
            titulo = "Asistencia";
        } else if (id == R.id.nav_objetos_perdidos) {
            fragment = new ChatFragment();
            titulo = "Mensajes";
        } else if (id == R.id.nav_notificaciones) {
            fragment = new NotificacionesFragment();
            titulo = "Notificaciones";
        } else if (id == R.id.nav_estadisticas) {
            fragment = new EstadisticasFragment();
            titulo = "Estadísticas";
        } else if (id == R.id.nav_muro_destacados) {
            fragment = new MuroDestacadosFragment();
            titulo = "Muro de Destacados";
        } else if (id == R.id.nav_gestion_equipos) {
            fragment = new GestionEquiposFragment();
            titulo = "Gestión de Equipos";
        } else if (id == R.id.nav_gestion_eventos) {
            fragment = new GestionEventosFragment();
            titulo = "Gestión de Eventos";
        } else if (id == R.id.nav_gestion_usuarios) {
            fragment = new GestionUsuariosFragment();
            titulo = "Gestión de Usuarios";
        } else if (id == R.id.nav_configuracion) {
            fragment = new ConfiguracionFragment();
            titulo = "Configuración";
        } else if (id == R.id.nav_logout) {
            cerrarSesion();
            return true;
        }

        if (fragment != null) {
            mostrarFragmento(fragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titulo);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void cerrarSesion() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.clearSession();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void mostrarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarContadorNotificaciones();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
} 