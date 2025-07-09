package com.gestionclub.padres.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.util.SessionManager;
import com.gestionclub.padres.data.DataManager;
import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword;
    private TextView tvError;
    private boolean passwordVisible = false;
    private DataManager dataManager;
    private ImageView ivClearUsuario, ivClearPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            dataManager = new DataManager(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            dataManager = null;
        }

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        inicializarVistas();
        configurarListeners();
        
        if (dataManager != null) {
            crearDatosEjemplo();
        }
    }

    private void inicializarVistas() {
        etUsuario = findViewById(R.id.et_usuario);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        tvError = findViewById(R.id.tv_error);
        ivClearUsuario = findViewById(R.id.iv_clear_usuario);
        ivClearPassword = findViewById(R.id.iv_clear_password);
        
        etUsuario.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) ocultarTeclado();
        });
        
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) ocultarTeclado();
        });
        
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
    }

    private void ocultarTeclado() {
        android.view.inputmethod.InputMethodManager imm = 
            (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void configurarListeners() {
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
            
            if (cursorPosition >= 0 && cursorPosition <= etPassword.getText().length()) {
                etPassword.setSelection(cursorPosition);
            }
            etPassword.requestFocus();
        });
        
        btnLogin.setOnClickListener(v -> realizarLogin());
        
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE || 
                actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                realizarLogin();
                return true;
            }
            return false;
        });
        
        etUsuario.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT) {
                etPassword.requestFocus();
                return true;
            }
            return false;
        });

        View appTitle = findViewById(R.id.app_title);
        if (appTitle != null) {
            appTitle.setOnClickListener(v -> forzarCreacionDatosEjemplo());
        }
    }

    private void forzarCreacionDatosEjemplo() {
        dataManager.cerrarSesion();
        
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
        
        List<Usuario> usuarios = new ArrayList<>();
        
        Usuario admin = new Usuario("Administrador", "admin@club.com", "admin123", "administrador");
        admin.setId("admin_001");
        usuarios.add(admin);
        
        Usuario adminAlt = new Usuario("Diego", "diego@club.com", "admin123", "administrador");
        adminAlt.setId("admin_002");
        usuarios.add(adminAlt);
        
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
        
        dataManager.guardarUsuarios(usuarios);
        
        dataManager.agregarJugadorAEquipo("equipo_alevin_a", "padre_001");
        dataManager.agregarJugadorAEquipo("equipo_alevin_a", "padre_004");
        dataManager.agregarJugadorAEquipo("equipo_infantil_b", "padre_002");
        dataManager.agregarJugadorAEquipo("equipo_cadete_a", "padre_003");
        
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
        
        etUsuario.setText("admin@club.com");
        etPassword.setText("admin123");
    }

    private void realizarLogin() {
        if (dataManager == null) {
            mostrarError("Error interno de la aplicación. Reinicia la app.");
            return;
        }
        
        String usuarioInput = etUsuario.getText().toString().trim();
        String passwordInput = etPassword.getText().toString().trim();
        tvError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(usuarioInput)) {
            mostrarError("Por favor, ingresa tu usuario o email");
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            mostrarError("Por favor, ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando credenciales...");
        btnLogin.setAlpha(0.7f);

        btnLogin.postDelayed(() -> {
            Usuario usuario = buscarUsuario(usuarioInput, passwordInput);
            if (usuario == null) {
                mostrarError("❌ Usuario o contraseña incorrectos\n\nVerifica tus credenciales o consulta con el administrador.");
                btnLogin.setEnabled(true);
                btnLogin.setText("INICIAR SESIÓN");
                btnLogin.setAlpha(1.0f);
                return;
            }
            loginExitoso(usuario);
        }, 1200);
    }

    private Usuario buscarUsuario(String input, String password) {
        List<Usuario> usuarios = dataManager.getUsuarios();
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(input) ||
                u.getNombre().equalsIgnoreCase(input) ||
                (u.getNombre() != null && u.getNombre().toLowerCase().contains(input.toLowerCase()))) {
                if (u.getPassword().equals(password)) {
                    return u;
                }
            }
        }
        return null;
    }

    private void loginExitoso(Usuario usuario) {
        String userJson = new Gson().toJson(usuario);
        new SessionManager(this).saveUser(userJson);
        dataManager.guardarUsuarioActual(usuario);
        
        String mensajeBienvenida = "¡Bienvenido, " + usuario.getNombre() + "!";
        
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
        
        Toast.makeText(this, mensajeBienvenida, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        tvError.setAlpha(0f);
        tvError.animate().alpha(1f).setDuration(300).start();
    }

    private void crearDatosEjemplo() {
        // Los datos se crean automáticamente en DataManager
    }
} 