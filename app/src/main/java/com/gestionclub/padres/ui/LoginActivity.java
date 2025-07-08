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
import com.gestionclub.padres.model.Equipo;
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
    private ImageView ivClearUsuario, ivClearPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreate: Iniciando LoginActivity");
            super.onCreate(savedInstanceState);
            
            // Verificar que el layout existe antes de cargarlo
            Log.d(TAG, "onCreate: Cargando layout activity_login");
            setContentView(R.layout.activity_login);
            Log.d(TAG, "onCreate: Layout cargado correctamente");

            // Inicializar DataManager con manejo de errores
            Log.d(TAG, "onCreate: Inicializando DataManager");
            try {
                dataManager = new DataManager(this);
                Log.d(TAG, "onCreate: DataManager inicializado correctamente");
            } catch (Exception e) {
                Log.e(TAG, "onCreate: Error al inicializar DataManager", e);
                Toast.makeText(this, "Error al inicializar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                // Continuar sin DataManager para evitar crash
                dataManager = null;
            }

            // Verificar si ya hay una sesión activa
            Log.d(TAG, "onCreate: Verificando sesión existente");
            try {
                SessionManager sessionManager = new SessionManager(this);
                if (sessionManager.getUser() != null) {
                    Log.d(TAG, "onCreate: Sesión existente encontrada, redirigiendo a MainActivity");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "onCreate: Error al verificar sesión", e);
                // Continuar sin verificar sesión
            }

            Log.d(TAG, "onCreate: Inicializando vistas");
            inicializarVistas();
            
            Log.d(TAG, "onCreate: Configurando listeners");
            configurarListeners();
            
            Log.d(TAG, "onCreate: Creando datos de ejemplo");
            if (dataManager != null) {
                crearDatosEjemplo();
            }
            
            Log.d(TAG, "onCreate: LoginActivity inicializada correctamente");
            
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error crítico en LoginActivity", e);
            // Mostrar un mensaje de error más amigable
            try {
                Toast.makeText(this, "Error al iniciar la aplicación. Reinicia la app.", Toast.LENGTH_LONG).show();
            } catch (Exception toastError) {
                Log.e(TAG, "onCreate: Error al mostrar Toast", toastError);
            }
        }
    }

    private void inicializarVistas() {
        try {
            Log.d(TAG, "inicializarVistas: Iniciando");
            
            // Buscar cada vista individualmente con validación
            etUsuario = findViewById(R.id.et_usuario);
            if (etUsuario == null) {
                throw new RuntimeException("No se pudo encontrar et_usuario en el layout");
            }
            
            etPassword = findViewById(R.id.et_password);
            if (etPassword == null) {
                throw new RuntimeException("No se pudo encontrar et_password en el layout");
            }
            
            btnLogin = findViewById(R.id.btn_login);
            if (btnLogin == null) {
                throw new RuntimeException("No se pudo encontrar btn_login en el layout");
            }
            
            ivTogglePassword = findViewById(R.id.iv_toggle_password);
            if (ivTogglePassword == null) {
                throw new RuntimeException("No se pudo encontrar iv_toggle_password en el layout");
            }
            
            tvError = findViewById(R.id.tv_error);
            if (tvError == null) {
                throw new RuntimeException("No se pudo encontrar tv_error en el layout");
            }
            
            // Iconos de limpiar
            ivClearUsuario = findViewById(R.id.iv_clear_usuario);
            ivClearPassword = findViewById(R.id.iv_clear_password);
            
            Log.d(TAG, "inicializarVistas: Todas las vistas encontradas correctamente");
            
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
            
            // Mostrar/ocultar icono de limpiar usuario
            etUsuario.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ivClearUsuario.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                }
                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
            ivClearUsuario.setOnClickListener(v -> etUsuario.setText(""));
            // Mostrar/ocultar icono de limpiar password
            etPassword.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ivClearPassword.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                }
                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
            ivClearPassword.setOnClickListener(v -> etPassword.setText(""));
            
            Log.d(TAG, "inicializarVistas: Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "inicializarVistas: Error al inicializar vistas", e);
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
            
            // Verificar que las vistas estén inicializadas
            if (ivTogglePassword == null || etPassword == null || btnLogin == null) {
                throw new RuntimeException("Vistas no inicializadas correctamente");
            }
            
            // Mostrar/ocultar contraseña con mejor manejo del teclado
            ivTogglePassword.setOnClickListener(v -> {
                try {
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
                } catch (Exception e) {
                    Log.e(TAG, "configurarListeners: Error en toggle password", e);
                }
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
            View appTitle = findViewById(R.id.app_title);
            if (appTitle != null) {
                appTitle.setOnClickListener(v -> {
                    // Forzar creación de datos de ejemplo
                    forzarCreacionDatosEjemplo();
                });
            }
            
            Log.d(TAG, "configurarListeners: Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "configurarListeners: Error al configurar listeners", e);
            throw e;
        }
    }

    private void forzarCreacionDatosEjemplo() {
        try {
            Log.d(TAG, "forzarCreacionDatosEjemplo: Forzando creación de datos");
            
            // Limpiar datos existentes
            dataManager.cerrarSesion();
            
            // Crear equipos primero
            List<Equipo> equipos = new ArrayList<>();
            
            Equipo alevinA = new Equipo("Alevín A", "Alevín", "Carlos López");
            alevinA.setId("equipo_alevin_a");
            equipos.add(alevinA);
            
            Equipo infantilB = new Equipo("Infantil B", "Infantil", "María Rodríguez");
            infantilB.setId("equipo_infantil_b");
            equipos.add(infantilB);
            
            Equipo cadeteA = new Equipo("Cadete A", "Cadete", "Juan García");
            cadeteA.setId("equipo_cadete_a");
            equipos.add(cadeteA);
            
            dataManager.guardarEquipos(equipos);
            
            // Crear usuarios de ejemplo organizados
            List<Usuario> usuarios = new ArrayList<>();
            
            // Administrador principal
            Usuario admin = new Usuario("Administrador", "admin@club.com", "admin123", "administrador");
            admin.setId("admin_001");
            usuarios.add(admin);
            
            // Administrador alternativo
            Usuario adminAlt = new Usuario("Diego", "diego@club.com", "admin123", "administrador");
            adminAlt.setId("admin_002");
            usuarios.add(adminAlt);
            
            // Padres/Tutores organizados por equipos
            Usuario padre1 = new Usuario("Juan Pérez", "juan@club.com", "padre123", "padre");
            padre1.setId("padre_001");
            padre1.setJugador("Carlos Pérez");
            padre1.setEquipoId("equipo_alevin_a");
            usuarios.add(padre1);
            
            Usuario padre2 = new Usuario("María García", "maria@club.com", "padre123", "padre");
            padre2.setId("padre_002");
            padre2.setJugador("Ana García");
            padre2.setEquipoId("equipo_infantil_b");
            usuarios.add(padre2);
            
            Usuario padre3 = new Usuario("Luis Martínez", "luis@club.com", "padre123", "padre");
            padre3.setId("padre_003");
            padre3.setJugador("Pedro Martínez");
            padre3.setEquipoId("equipo_cadete_a");
            usuarios.add(padre3);
            
            Usuario padre4 = new Usuario("Carmen López", "carmen@club.com", "padre123", "padre");
            padre4.setId("padre_004");
            padre4.setJugador("Miguel López");
            padre4.setEquipoId("equipo_alevin_a");
            usuarios.add(padre4);
            
            // Guardar usuarios
            dataManager.guardarUsuarios(usuarios);
            
            // Asignar jugadores a equipos
            dataManager.agregarJugadorAEquipo("equipo_alevin_a", "padre_001");
            dataManager.agregarJugadorAEquipo("equipo_alevin_a", "padre_004");
            dataManager.agregarJugadorAEquipo("equipo_infantil_b", "padre_002");
            dataManager.agregarJugadorAEquipo("equipo_cadete_a", "padre_003");
            
            // Mostrar mensaje con credenciales organizadas
            String mensaje = "✅ Datos creados exitosamente\n\n" +
                           "👨‍💼 Administradores:\n" +
                           "• admin@club.com / admin123\n" +
                           "• diego@club.com / admin123\n\n" +
                           "👨‍👩‍👧‍👦 Padres (todos con contraseña: padre123):\n" +
                           "• juan@club.com (Equipo: Alevín A)\n" +
                           "• maria@club.com (Equipo: Infantil B)\n" +
                           "• luis@club.com (Equipo: Cadete A)\n" +
                           "• carmen@club.com (Equipo: Alevín A)";
            
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            
            // Auto-completar campos para testing
            etUsuario.setText("admin@club.com");
            etPassword.setText("admin123");
            
            Log.d(TAG, "forzarCreacionDatosEjemplo: Datos creados exitosamente con organización por equipos");
        } catch (Exception e) {
            Log.e(TAG, "forzarCreacionDatosEjemplo: Error", e);
            Toast.makeText(this, "Error creando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void realizarLogin() {
        try {
            Log.d(TAG, "realizarLogin: Iniciando proceso de login");
            
            // Verificar que el DataManager esté disponible
            if (dataManager == null) {
                Log.e(TAG, "realizarLogin: DataManager no disponible");
                mostrarError("Error interno de la aplicación. Reinicia la app.");
                return;
            }
            
            String usuarioInput = etUsuario.getText().toString().trim();
            String passwordInput = etPassword.getText().toString().trim();
            tvError.setVisibility(View.GONE);

            Log.d(TAG, "realizarLogin: Usuario: " + usuarioInput + ", Password: " + (passwordInput.isEmpty() ? "vacío" : "contenido"));

            // Validaciones mejoradas
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

            // Mostrar loading profesional
            btnLogin.setEnabled(false);
            btnLogin.setText("Verificando credenciales...");
            btnLogin.setAlpha(0.7f);

            // Simular delay de red con animación
            btnLogin.postDelayed(() -> {
                try {
                    Usuario usuario = buscarUsuario(usuarioInput, passwordInput);
                    if (usuario == null) {
                        Log.d(TAG, "realizarLogin: Usuario no encontrado o contraseña incorrecta");
                        mostrarError("❌ Usuario o contraseña incorrectos\n\nVerifica tus credenciales o consulta con el administrador.");
                        btnLogin.setEnabled(true);
                        btnLogin.setText("INICIAR SESIÓN");
                        btnLogin.setAlpha(1.0f);
                        return;
                    }
                    // Login exitoso
                    loginExitoso(usuario);
                } catch (Exception e) {
                    Log.e(TAG, "realizarLogin: Error en búsqueda de usuario", e);
                    mostrarError("⚠️ Error interno. Intenta de nuevo.");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("INICIAR SESIÓN");
                    btnLogin.setAlpha(1.0f);
                }
            }, 1200);
            
        } catch (Exception e) {
            Log.e(TAG, "realizarLogin: Error", e);
            Toast.makeText(this, "Error en login: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
            btnLogin.setAlpha(1.0f);
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
            
            // Preparar mensaje de bienvenida personalizado
            String mensajeBienvenida = "¡Bienvenido, " + usuario.getNombre() + "!";
            
            // Si es padre, mostrar información del equipo
            if ("padre".equals(usuario.getRol()) && usuario.getEquipoId() != null) {
                Equipo equipo = dataManager.getEquipoPorId(usuario.getEquipoId());
                if (equipo != null) {
                    mensajeBienvenida += "\n\n🏆 Equipo: " + equipo.getNombre();
                    mensajeBienvenida += "\n👨‍🏫 Entrenador: " + equipo.getEntrenador();
                    if (usuario.getJugador() != null) {
                        mensajeBienvenida += "\n⚽ Jugador: " + usuario.getJugador();
                    }
                }
            } else if ("administrador".equals(usuario.getRol())) {
                mensajeBienvenida += "\n\n👨‍💼 Panel de Administración";
                mensajeBienvenida += "\nGestiona equipos, usuarios y eventos";
            }
            
            // Mostrar mensaje de éxito con información del equipo
            Toast.makeText(this, mensajeBienvenida, Toast.LENGTH_LONG).show();
            
            // Ir a MainActivity
            Log.d(TAG, "loginExitoso: Redirigiendo a MainActivity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            
        } catch (Exception e) {
            Log.e(TAG, "loginExitoso: Error", e);
            Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnLogin.setEnabled(true);
            btnLogin.setText("INICIAR SESIÓN");
            btnLogin.setAlpha(1.0f);
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