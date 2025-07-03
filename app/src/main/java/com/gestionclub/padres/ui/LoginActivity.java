package com.gestionclub.padres.ui;

import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsuario = findViewById(R.id.et_usuario);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String usuario = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // Login local de ejemplo: usuario = admin, password = admin
            if (usuario.equals("admin") && password.equals("admin")) {
                Usuario user = new Usuario("admin", "Administrador", "administrador", "");
                String userJson = new Gson().toJson(user);
                new SessionManager(this).saveUser(userJson);
                
                // Guardar también en DataManager
                DataManager dataManager = new DataManager(this);
                dataManager.guardarUsuarioActual(user);
                
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 