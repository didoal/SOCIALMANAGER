package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.util.SessionManager;
import com.gestionclub.padres.data.DataManager;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreate: Iniciando LoginActivity");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            Log.d(TAG, "onCreate: Layout cargado correctamente");

            // Inicializar DataManager
            Log.d(TAG, "onCreate: Inicializando DataManager");
            dataManager = new DataManager(this);
            Log.d(TAG, "onCreate: DataManager inicializado");

            // Verificar si ya hay una sesión activa
            Log.d(TAG, "onCreate: Verificando sesión existente");
            SessionManager sessionManager = new SessionManager(this);
            if (sessionManager.getUser() != null) {
                Log.d(TAG, "onCreate: Sesión existente encontrada, redirigiendo a MainActivity");
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            inicializarVistas();
            configurarListeners();
            crearDatosEjemplo();
            Log.d(TAG, "onCreate: LoginActivity inicializada correctamente");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error crítico", e);
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void inicializarVistas() {
        try {
            Log.d(TAG, "inicializarVistas: Iniciando");
            etUsuario = findViewById(R.id.et_usuario);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            Log.d(TAG, "inicializarVistas: Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "inicializarVistas: Error", e);
            throw e;
        }
    }

    private void configurarListeners() {
        try {
            Log.d(TAG, "configurarListeners: Configurando listeners");
            btnLogin.setOnClickListener(v -> realizarLogin());
            
            // Permitir login con Enter
            etPassword.setOnEditorActionListener((v, actionId, event) -> {
                realizarLogin();
                return true;
            });
            Log.d(TAG, "configurarListeners: Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "configurarListeners: Error", e);
            throw e;
        }
    }

    private void realizarLogin() {
        try {
            Log.d(TAG, "realizarLogin: Iniciando proceso de login");
            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Log.d(TAG, "realizarLogin: Usuario: " + usuario + ", Password: " + (password.isEmpty() ? "vacío" : "contenido"));

            // Validaciones
            if (TextUtils.isEmpty(usuario)) {
                Log.d(TAG, "realizarLogin: Usuario vacío");
                etUsuario.setError("Ingresa tu usuario");
                etUsuario.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Log.d(TAG, "realizarLogin: Password vacío");
                etPassword.setError("Ingresa tu contraseña");
                etPassword.requestFocus();
                return;
            }

            // Mostrar loading
            btnLogin.setEnabled(false);
            btnLogin.setText("Iniciando...");

            // Simular delay de red
            btnLogin.postDelayed(() -> {
                // Login local de ejemplo
                if (usuario.equals("admin") && password.equals("admin")) {
                    Log.d(TAG, "realizarLogin: Credenciales correctas");
                    loginExitoso();
                } else {
                    Log.d(TAG, "realizarLogin: Credenciales incorrectas");
                    loginFallido();
                }
            }, 1000);
            
        } catch (Exception e) {
            Log.e(TAG, "realizarLogin: Error", e);
            Toast.makeText(this, "Error en login: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }

    private void loginExitoso() {
        try {
            Log.d(TAG, "loginExitoso: Iniciando");
            // Crear usuario administrador
            Usuario user = new Usuario("Administrador", "admin@club.com", "admin", "administrador");
            user.setEquipoNombre("Todos los Equipos");
            Log.d(TAG, "loginExitoso: Usuario creado");
            
            // Guardar en SessionManager
            String userJson = new Gson().toJson(user);
            new SessionManager(this).saveUser(userJson);
            Log.d(TAG, "loginExitoso: Usuario guardado en SessionManager");
            
            // Guardar en DataManager
            dataManager.guardarUsuarioActual(user);
            Log.d(TAG, "loginExitoso: Usuario guardado en DataManager");
            
            // Mostrar mensaje de éxito
            Toast.makeText(this, "¡Bienvenido, " + user.getNombre() + "!", Toast.LENGTH_SHORT).show();
            
            // Ir a MainActivity
            Log.d(TAG, "loginExitoso: Redirigiendo a MainActivity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "loginExitoso: Error", e);
            Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }

    private void loginFallido() {
        Log.d(TAG, "loginFallido: Mostrando error");
        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        etPassword.setText("");
        etPassword.requestFocus();
        btnLogin.setEnabled(true);
        btnLogin.setText("INICIAR SESIÓN");
    }

    private void crearDatosEjemplo() {
        try {
            Log.d(TAG, "crearDatosEjemplo: Los datos de ejemplo se crean automáticamente en el constructor de DataManager");
        } catch (Exception e) {
            Log.e(TAG, "crearDatosEjemplo: Error", e);
        }
    }
} 