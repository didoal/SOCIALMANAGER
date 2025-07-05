package com.gestionclub.padres.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.util.SessionManager;
import com.gestionclub.padres.data.DataManager;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword;
    private TextView tvError;
    private boolean passwordVisible = false;
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
            ivTogglePassword = findViewById(R.id.iv_toggle_password);
            tvError = findViewById(R.id.tv_error);
            
            // Configurar focus change listeners para mejor manejo del teclado
            etUsuario.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    ocultarTeclado();
                }
            });
            
            etPassword.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    ocultarTeclado();
                }
            });
            
            Log.d(TAG, "inicializarVistas: Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "inicializarVistas: Error", e);
            throw e;
        }
    }

    private void ocultarTeclado() {
        try {
            android.view.inputmethod.InputMethodManager imm = 
                (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "ocultarTeclado: Error", e);
        }
    }

    private void configurarListeners() {
        try {
            Log.d(TAG, "configurarListeners: Configurando listeners");
            
            // Mostrar/ocultar contraseña con mejor manejo del teclado
            ivTogglePassword.setOnClickListener(v -> {
                passwordVisible = !passwordVisible;
                int cursorPosition = etPassword.getSelectionStart();
                
                if (passwordVisible) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_lock);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eye);
                }
                
                // Restaurar posición del cursor
                if (cursorPosition >= 0 && cursorPosition <= etPassword.getText().length()) {
                    etPassword.setSelection(cursorPosition);
                }
                
                // Forzar actualización del teclado
                etPassword.requestFocus();
            });
            
            btnLogin.setOnClickListener(v -> realizarLogin());
            
            // Permitir login con Enter en el campo de contraseña
            etPassword.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE || 
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                    realizarLogin();
                    return true;
                }
                return false;
            });
            
            // Permitir login con Enter en el campo de usuario
            etUsuario.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                    etPassword.requestFocus();
                    return true;
                }
                return false;
            });

            // Botón de debug temporal - doble tap en el título
            findViewById(R.id.app_title).setOnClickListener(v -> {
                // Forzar creación de datos de ejemplo
                forzarCreacionDatosEjemplo();
            });
            
            Log.d(TAG, "configurarListeners: Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "configurarListeners: Error", e);
            throw e;
        }
    }

    private void forzarCreacionDatosEjemplo() {
        try {
            Log.d(TAG, "forzarCreacionDatosEjemplo: Forzando creación de datos");
            
            // Limpiar datos existentes
            dataManager.cerrarSesion();
            
            // Crear usuarios de ejemplo manualmente
            List<Usuario> usuarios = new ArrayList<>();
            
            // Administrador con múltiples formas de acceso
            Usuario admin = new Usuario("admin", "admin@club.com", "admin", "administrador");
            usuarios.add(admin);
            
            // Administrador alternativo
            Usuario adminAlt = new Usuario("Administrador", "admin@club.com", "admin123", "administrador");
            usuarios.add(adminAlt);
            
            // Padres/Tutores
            Usuario padre1 = new Usuario("Juan Pérez", "juan@club.com", "padre123", "padre");
            usuarios.add(padre1);
            
            Usuario padre2 = new Usuario("María García", "maria@club.com", "padre123", "padre");
            usuarios.add(padre2);
            
            // Guardar usuarios
            dataManager.guardarUsuarios(usuarios);
            
            // Mostrar mensaje
            Toast.makeText(this, "Datos creados. Prueba: admin/admin o admin@club.com/admin123", Toast.LENGTH_LONG).show();
            
            // Auto-completar campos para testing
            etUsuario.setText("admin");
            etPassword.setText("admin");
            
            Log.d(TAG, "forzarCreacionDatosEjemplo: Datos creados exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "forzarCreacionDatosEjemplo: Error", e);
            Toast.makeText(this, "Error creando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void realizarLogin() {
        try {
            Log.d(TAG, "realizarLogin: Iniciando proceso de login");
            String usuarioInput = etUsuario.getText().toString().trim();
            String passwordInput = etPassword.getText().toString().trim();
            tvError.setVisibility(View.GONE);

            Log.d(TAG, "realizarLogin: Usuario: " + usuarioInput + ", Password: " + (passwordInput.isEmpty() ? "vacío" : "contenido"));

            // Validaciones
            if (TextUtils.isEmpty(usuarioInput)) {
                Log.d(TAG, "realizarLogin: Usuario vacío");
                mostrarError("Por favor, ingresa tu usuario o email");
                etUsuario.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(passwordInput)) {
                Log.d(TAG, "realizarLogin: Password vacío");
                mostrarError("Por favor, ingresa tu contraseña");
                etPassword.requestFocus();
                return;
            }

            // Mostrar loading
            btnLogin.setEnabled(false);
            btnLogin.setText("Iniciando...");

            // Simular delay de red
            btnLogin.postDelayed(() -> {
                Usuario usuario = buscarUsuario(usuarioInput, passwordInput);
                if (usuario == null) {
                    Log.d(TAG, "realizarLogin: Usuario no encontrado o contraseña incorrecta");
                    mostrarError("Usuario o contraseña incorrectos. Intenta de nuevo o consulta con el administrador.");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("INICIAR SESIÓN");
                    return;
                }
                // Login exitoso
                loginExitoso(usuario);
            }, 800);
            
        } catch (Exception e) {
            Log.e(TAG, "realizarLogin: Error", e);
            Toast.makeText(this, "Error en login: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
        }
    }

    private Usuario buscarUsuario(String input, String password) {
        try {
            List<Usuario> usuarios = dataManager.getUsuarios();
            Log.d(TAG, "buscarUsuario: Buscando usuario con input: " + input);
            Log.d(TAG, "buscarUsuario: Total de usuarios: " + usuarios.size());
            for (Usuario u : usuarios) {
                if (u.getEmail().equalsIgnoreCase(input) ||
                    u.getNombre().equalsIgnoreCase(input) ||
                    (u.getNombre() != null && u.getNombre().toLowerCase().contains(input.toLowerCase()))) {
                    // Si coincide el usuario, nombre o email, ahora verifica la contraseña
                    if (u.getPassword().equals(password)) {
                        Log.d(TAG, "buscarUsuario: Usuario encontrado: " + u.getNombre());
                        return u;
                    }
                }
            }
            Log.d(TAG, "buscarUsuario: Usuario no encontrado o contraseña incorrecta");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "buscarUsuario: Error", e);
            return null;
        }
    }

    private void loginExitoso(Usuario usuario) {
        try {
            Log.d(TAG, "loginExitoso: Iniciando");
            // Guardar en SessionManager
            String userJson = new Gson().toJson(usuario);
            new SessionManager(this).saveUser(userJson);
            Log.d(TAG, "loginExitoso: Usuario guardado en SessionManager");
            
            // Guardar en DataManager
            dataManager.guardarUsuarioActual(usuario);
            Log.d(TAG, "loginExitoso: Usuario guardado en DataManager");
            
            // Mostrar mensaje de éxito
            Toast.makeText(this, "¡Bienvenido, " + usuario.getNombre() + "!", Toast.LENGTH_SHORT).show();
            
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

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        tvError.setAlpha(0f);
        tvError.animate().alpha(1f).setDuration(300).start();
    }

    private void crearDatosEjemplo() {
        try {
            Log.d(TAG, "crearDatosEjemplo: Los datos de ejemplo se crean automáticamente en el constructor de DataManager");
            
            // Debug: Verificar usuarios disponibles
            List<Usuario> usuarios = dataManager.getUsuarios();
            Log.d(TAG, "crearDatosEjemplo: Número de usuarios disponibles: " + usuarios.size());
            for (Usuario u : usuarios) {
                Log.d(TAG, "crearDatosEjemplo: Usuario - Nombre: " + u.getNombre() + 
                          ", Email: " + u.getEmail() + 
                          ", Rol: " + u.getRol() + 
                          ", Password: " + u.getPassword());
            }
        } catch (Exception e) {
            Log.e(TAG, "crearDatosEjemplo: Error", e);
        }
    }
} 