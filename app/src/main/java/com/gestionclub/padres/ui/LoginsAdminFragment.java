package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.UsuarioAdminAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class LoginsAdminFragment extends Fragment implements UsuarioAdminAdapter.OnUsuarioAdminListener {
    private EditText editTextNombre, editTextEmail, editTextPassword, editTextJugador, editTextRol;
    private Button btnCrearUsuario, btnLimpiar;
    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdminAdapter usuarioAdapter;
    private DataManager dataManager;
    private List<Usuario> usuarios;
    private LinearLayout contenedorFormulario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logins_admin, container, false);
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        cargarUsuarios();
        configurarListeners();
        return view;
    }

    private void inicializarVistas(View view) {
        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextJugador = view.findViewById(R.id.editTextJugador);
        editTextRol = view.findViewById(R.id.editTextRol);
        btnCrearUsuario = view.findViewById(R.id.btnCrearUsuario);
        btnLimpiar = view.findViewById(R.id.btnLimpiar);
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        contenedorFormulario = view.findViewById(R.id.contenedorFormulario);

        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        usuarios = new ArrayList<>();
        usuarioAdapter = new UsuarioAdminAdapter(usuarios, this);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarUsuarios() {
        usuarios = dataManager.getUsuarios();
        usuarioAdapter.actualizarUsuarios(usuarios);
    }

    private void configurarListeners() {
        btnCrearUsuario.setOnClickListener(v -> crearUsuario());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
    }

    private void crearUsuario() {
        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String jugador = editTextJugador.getText().toString().trim();
        String rol = editTextRol.getText().toString().trim();

        if (nombre.isEmpty() || password.isEmpty() || rol.isEmpty()) {
            Toast.makeText(requireContext(), "Nombre, contraseña y rol son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar rol
        if (!rol.equalsIgnoreCase("administrador") && !rol.equalsIgnoreCase("padre") && !rol.equalsIgnoreCase("tutor")) {
            Toast.makeText(requireContext(), "Rol debe ser: administrador, padre o tutor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar duplicados
        for (Usuario u : dataManager.getUsuarios()) {
            if (u.getNombre().equalsIgnoreCase(nombre)) {
                Toast.makeText(requireContext(), "Ya existe un usuario con ese nombre", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Usuario nuevo = new Usuario(nombre, jugador, password, rol);
        if (!email.isEmpty()) {
            nuevo.setEmail(email);
        }

        List<Usuario> lista = dataManager.getUsuarios();
        lista.add(nuevo);
        dataManager.guardarUsuarios(lista);
        cargarUsuarios();
        limpiarFormulario();
        Toast.makeText(requireContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void limpiarFormulario() {
        editTextNombre.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextJugador.setText("");
        editTextRol.setText("");
        editTextNombre.requestFocus();
    }

    @Override
    public void onUsuarioDelete(Usuario usuario) {
        // No permitir eliminar el último administrador
        if (usuario.isEsAdmin()) {
            int adminCount = 0;
            for (Usuario u : dataManager.getUsuarios()) {
                if (u.isEsAdmin()) adminCount++;
            }
            if (adminCount <= 1) {
                Toast.makeText(requireContext(), "No se puede eliminar el último administrador", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<Usuario> lista = dataManager.getUsuarios();
        List<Usuario> nuevaLista = new ArrayList<>();
        for (Usuario u : lista) {
            if (!u.getId().equals(usuario.getId())) {
                nuevaLista.add(u);
            }
        }
        dataManager.guardarUsuarios(nuevaLista);
        cargarUsuarios();
        Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUsuarioEdit(Usuario usuario) {
        // Implementar edición si es necesario
        Toast.makeText(requireContext(), "Edición de usuarios próximamente", Toast.LENGTH_SHORT).show();
    }
} 