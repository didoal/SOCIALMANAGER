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

        // Crear datos de ejemplo si no existen
        if (dataManager.getNotificaciones().isEmpty()) {
            dataManager.crearDatosEjemploNotificaciones();
        }

        // Mostrar fragmento de perfil por defecto
        if (savedInstanceState == null) {
            mostrarFragmento(new PerfilFragment());
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
        TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
        TextView textViewUserRole = headerView.findViewById(R.id.textViewUserRole);

        if (usuarioActual != null) {
            textViewUserName.setText(usuarioActual.getNombre());
            textViewUserRole.setText(usuarioActual.getRol());
        }
    }

    private void configurarVisibilidadMenu() {
        if (usuarioActual != null) {
            if (usuarioActual.isEsAdmin()) {
                navigationView.getMenu().findItem(R.id.nav_asistencia).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_seleccion_equipo).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.nav_estadisticas).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_seleccion_equipo).setVisible(false);
            }
        }
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
        if (id == R.id.nav_perfil) {
            fragment = new PerfilFragment();
            titulo = "Perfil";
        } else if (id == R.id.nav_calendario) {
            fragment = new CalendarioFragment();
            titulo = "Calendario";
        } else if (id == R.id.nav_mensajes) {
            fragment = new MensajesFragment();
            titulo = "Mensajes";
        } else if (id == R.id.nav_objetos_perdidos) {
            fragment = new ObjetosPerdidosFragment();
            titulo = "Objetos Perdidos";
        } else if (id == R.id.nav_notificaciones) {
            fragment = new NotificacionesFragment();
            titulo = "Notificaciones";
        } else if (id == R.id.nav_asistencia) {
            fragment = new AsistenciaFragment();
            titulo = "Asistencia";
        } else if (id == R.id.nav_estadisticas) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                // Si es admin, mostrar selección de equipo primero
                fragment = new SeleccionEquipoFragment();
                titulo = "Seleccionar Equipo";
            } else {
                fragment = new com.gestionclub.admin.ui.EstadisticasFragment();
                titulo = "Estadísticas";
            }
        } else if (id == R.id.nav_seleccion_equipo) {
            fragment = new SeleccionEquipoFragment();
            titulo = "Seleccionar Equipo";
        } else if (id == R.id.nav_logout) {
            dataManager.cerrarSesion();
            new SessionManager(this).clearSession();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (fragment != null) {
            mostrarFragmento(fragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titulo);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void mostrarFragmento(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
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
    public void onResume() {
        super.onResume();
        actualizarContadorNotificaciones();
    }
} 