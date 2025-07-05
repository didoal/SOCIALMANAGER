package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.UsuarioAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment implements UsuarioAdapter.OnUsuarioDeleteListener {
    private EditText editTextNombreUsuario, editTextJugador, editTextPassword;
    private Button btnCrearUsuario, btnLogout;
    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private DataManager dataManager;
    private List<Usuario> usuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        cargarUsuarios();
        configurarListeners();
        return view;
    }

    private void inicializarVistas(View view) {
        editTextNombreUsuario = view.findViewById(R.id.editTextNombreUsuario);
        editTextJugador = view.findViewById(R.id.editTextJugador);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        btnCrearUsuario = view.findViewById(R.id.btnCrearUsuario);
        btnLogout = view.findViewById(R.id.btn_logout);
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        usuarios = new ArrayList<>();
        usuarioAdapter = new UsuarioAdapter(usuarios, this);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarUsuarios() {
        usuarios = dataManager.getUsuarios();
        usuarioAdapter.actualizarUsuarios(usuarios);
    }

    private void configurarListeners() {
        btnCrearUsuario.setOnClickListener(v -> crearUsuario());
        btnLogout.setOnClickListener(v -> {
            dataManager.cerrarSesion();
            requireActivity().finish();
        });
    }

    private void crearUsuario() {
        String nombre = editTextNombreUsuario.getText().toString().trim();
        String jugador = editTextJugador.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (nombre.isEmpty() || jugador.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validar duplicados (nombre + jugador)
        for (Usuario u : dataManager.getUsuarios()) {
            if (u.getNombre().equalsIgnoreCase(nombre) && u.getJugador().equalsIgnoreCase(jugador)) {
                Toast.makeText(requireContext(), "Ya existe un usuario con ese nombre y jugador", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Usuario nuevo = new Usuario(nombre, jugador, password, "padre");
        List<Usuario> lista = dataManager.getUsuarios();
        lista.add(nuevo);
        dataManager.guardarUsuarios(lista);
        cargarUsuarios();
        editTextNombreUsuario.setText("");
        editTextJugador.setText("");
        editTextPassword.setText("");
        Toast.makeText(requireContext(), "Usuario creado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUsuarioDelete(Usuario usuario) {
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
} 