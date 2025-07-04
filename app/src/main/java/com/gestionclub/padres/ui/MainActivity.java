package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreate: Iniciando MainActivity");
            super.onCreate(savedInstanceState);
            
            Log.d(TAG, "onCreate: Verificando sesión");
            SessionManager sessionManager = new SessionManager(this);
            if (sessionManager.getUser() == null) {
                Log.d(TAG, "onCreate: No hay sesión, redirigiendo a LoginActivity");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            
            Log.d(TAG, "onCreate: Cargando layout");
            setContentView(R.layout.activity_main);

            Log.d(TAG, "onCreate: Inicializando DataManager");
            dataManager = new DataManager(this);
            
            Log.d(TAG, "onCreate: Obteniendo usuario actual");
            usuarioActual = dataManager.getUsuarioActual();
            if (usuarioActual == null) {
                Log.d(TAG, "onCreate: Usuario actual es null, intentando recuperar de SessionManager");
                String userJson = sessionManager.getUser();
                if (userJson != null) {
                    usuarioActual = new Gson().fromJson(userJson, Usuario.class);
                    if (usuarioActual != null) {
                        dataManager.guardarUsuarioActual(usuarioActual);
                        Log.d(TAG, "onCreate: Usuario recuperado y guardado");
                    }
                }
            }

            Log.d(TAG, "onCreate: Inicializando vistas");
            inicializarVistas();
            configurarMenuLateral();
            actualizarHeaderUsuario();
            configurarVisibilidadMenu();
            actualizarContadorNotificaciones();

            // Crear datos de ejemplo si no existen
            Log.d(TAG, "onCreate: Verificando datos de ejemplo");
            if (dataManager.getNotificaciones().isEmpty()) {
                dataManager.crearDatosEjemploNotificaciones();
            }

            // Mostrar fragmento de perfil por defecto
            Log.d(TAG, "onCreate: Mostrando fragmento inicial");
            if (savedInstanceState == null) {
                mostrarFragmento(new PerfilFragment());
            }
            
            Log.d(TAG, "onCreate: MainActivity inicializada correctamente");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error crítico", e);
            // Mostrar error al usuario
            android.widget.Toast.makeText(this, "Error al cargar la aplicación: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private void inicializarVistas() {
        try {
            Log.d(TAG, "inicializarVistas: Iniciando");
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Log.d(TAG, "inicializarVistas: Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "inicializarVistas: Error", e);
            throw e;
        }
    }

    private void configurarMenuLateral() {
        try {
            Log.d(TAG, "configurarMenuLateral: Configurando");
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, (Toolbar) findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);
            Log.d(TAG, "configurarMenuLateral: Configurado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "configurarMenuLateral: Error", e);
            throw e;
        }
    }

    private void actualizarHeaderUsuario() {
        try {
            Log.d(TAG, "actualizarHeaderUsuario: Actualizando");
            View headerView = navigationView.getHeaderView(0);
            TextView textViewUserName = headerView.findViewById(R.id.textViewUserName);
            TextView textViewUserRole = headerView.findViewById(R.id.textViewUserRole);

            if (usuarioActual != null) {
                textViewUserName.setText(usuarioActual.getNombre());
                textViewUserRole.setText(usuarioActual.getRol());
                Log.d(TAG, "actualizarHeaderUsuario: Header actualizado para " + usuarioActual.getNombre());
            }
        } catch (Exception e) {
            Log.e(TAG, "actualizarHeaderUsuario: Error", e);
        }
    }

    private void configurarVisibilidadMenu() {
        try {
            Log.d(TAG, "configurarVisibilidadMenu: Configurando");
            if (usuarioActual != null) {
                if (usuarioActual.isEsAdmin()) {
                    navigationView.getMenu().findItem(R.id.nav_asistencia).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_seleccion_equipo).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_gestion_equipos).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_gestion_usuarios).setVisible(true);
                    Log.d(TAG, "configurarVisibilidadMenu: Configurado para administrador");
                } else {
                    navigationView.getMenu().findItem(R.id.nav_estadisticas).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_seleccion_equipo).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_gestion_equipos).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_gestion_usuarios).setVisible(false);
                    Log.d(TAG, "configurarVisibilidadMenu: Configurado para usuario normal");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "configurarVisibilidadMenu: Error", e);
        }
    }

    private void actualizarContadorNotificaciones() {
        try {
            Log.d(TAG, "actualizarContadorNotificaciones: Actualizando");
            int noLeidas = dataManager.getNotificacionesNoLeidas();
            MenuItem menuNotificaciones = navigationView.getMenu().findItem(R.id.nav_notificaciones);
            
            if (noLeidas > 0) {
                menuNotificaciones.setTitle("Notificaciones (" + noLeidas + ")");
            } else {
                menuNotificaciones.setTitle("Notificaciones");
            }
            Log.d(TAG, "actualizarContadorNotificaciones: " + noLeidas + " notificaciones no leídas");
        } catch (Exception e) {
            Log.e(TAG, "actualizarContadorNotificaciones: Error", e);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            Log.d(TAG, "onNavigationItemSelected: Item seleccionado: " + item.getTitle());
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
                    fragment = new SeleccionEquipoFragment();
                    titulo = "Seleccionar Equipo";
                } else {
                    fragment = new com.gestionclub.admin.ui.EstadisticasFragment();
                    titulo = "Estadísticas";
                }
            } else if (id == R.id.nav_seleccion_equipo) {
                fragment = new SeleccionEquipoFragment();
                titulo = "Seleccionar Equipo";
            } else if (id == R.id.nav_gestion_equipos) {
                fragment = new GestionEquiposFragment();
                titulo = "Gestión de Equipos";
            } else if (id == R.id.nav_gestion_usuarios) {
                fragment = new GestionUsuariosFragment();
                titulo = "Gestión de Usuarios";
            } else if (id == R.id.nav_logout) {
                Log.d(TAG, "onNavigationItemSelected: Cerrando sesión");
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
        } catch (Exception e) {
            Log.e(TAG, "onNavigationItemSelected: Error", e);
            return false;
        }
    }

    public void mostrarFragmento(Fragment fragment) {
        try {
            Log.d(TAG, "mostrarFragmento: Mostrando fragmento " + fragment.getClass().getSimpleName());
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        } catch (Exception e) {
            Log.e(TAG, "mostrarFragmento: Error", e);
        }
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
        Log.d(TAG, "onResume: MainActivity resumida");
        actualizarContadorNotificaciones();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: MainActivity pausada");
    }
} 