package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class PerfilFragment extends Fragment implements UsuarioAdapter.OnUsuarioClickListener {
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
        btnCrearUsuario.setOnClickListener(v -> mostrarDialogoCrearUsuario());
        btnLogout.setOnClickListener(v -> {
            dataManager.cerrarSesion();
            requireActivity().finish();
        });
    }

    private void mostrarDialogoCrearUsuario() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_usuario, null);

        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);

        // Opciones de rol permitidas
        String[] roles = {"Madre", "Padre", "Tutor", "Jugador"};
        ArrayAdapter<String> adapterRol = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRol);

        // Opciones de equipo/categoría (puedes personalizar)
        String[] equipos = {"Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil"};
        ArrayAdapter<String> adapterEquipo = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, equipos);
        adapterEquipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(adapterEquipo);

        builder.setView(dialogView)
                .setTitle("Crear Usuario")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString().toLowerCase();
                    String equipo = spinnerEquipo.getSelectedItem().toString();

                    if (nombre.isEmpty() || password.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Validar duplicados (nombre + equipo)
                    for (Usuario u : dataManager.getUsuarios()) {
                        if (u.getNombre().equalsIgnoreCase(nombre) && u.getEquipo() != null && u.getEquipo().equalsIgnoreCase(equipo)) {
                            Toast.makeText(requireContext(), "Ya existe un usuario con ese nombre y equipo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Usuario nuevo = new Usuario(nombre, equipo, password, rol);
                    nuevo.setEmail(email);
                    nuevo.setEquipo(equipo);
                    List<Usuario> lista = dataManager.getUsuarios();
                    lista.add(nuevo);
                    dataManager.guardarUsuarios(lista);
                    cargarUsuarios();
                    Toast.makeText(requireContext(), "Usuario creado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEliminarClick(Usuario usuario) {
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