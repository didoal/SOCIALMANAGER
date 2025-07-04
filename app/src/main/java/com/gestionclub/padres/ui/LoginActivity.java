package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar DataManager
        dataManager = new DataManager(this);

        // Verificar si ya hay una sesión activa
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        inicializarVistas();
        configurarListeners();
        crearDatosEjemplo();
    }

    private void inicializarVistas() {
        etUsuario = findViewById(R.id.et_usuario);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void configurarListeners() {
        btnLogin.setOnClickListener(v -> realizarLogin());
        
        // Permitir login con Enter
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            realizarLogin();
            return true;
        });
    }

    private void realizarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("Ingresa tu usuario");
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
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
                loginExitoso();
            } else {
                loginFallido();
            }
        }, 1000);
    }

    private void loginExitoso() {
        try {
            // Crear usuario administrador
            Usuario user = new Usuario("Administrador", "admin@club.com", "admin", "administrador");
            user.setEquipoNombre("Todos los Equipos");
            
            // Guardar en SessionManager
            String userJson = new Gson().toJson(user);
            new SessionManager(this).saveUser(userJson);
            
            // Guardar en DataManager
            dataManager.guardarUsuarioActual(user);
            
            // Mostrar mensaje de éxito
            Toast.makeText(this, "¡Bienvenido, " + user.getNombre() + "!", Toast.LENGTH_SHORT).show();
            
            // Ir a MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }

    private void loginFallido() {
        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        etPassword.setText("");
        etPassword.requestFocus();
        btnLogin.setEnabled(true);
        btnLogin.setText("INICIAR SESIÓN");
    }

    private void crearDatosEjemplo() {
        // Los datos de ejemplo se crean automáticamente en el constructor de DataManager
        // No necesitamos hacer nada aquí
    }
} 