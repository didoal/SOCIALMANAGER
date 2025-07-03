package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.UsuarioAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;
import java.util.List;

public class GestionUsuariosFragment extends Fragment implements UsuarioAdapter.OnUsuarioClickListener {
    private RecyclerView recyclerViewUsuarios;
    private TextView textViewTitulo;
    private Button buttonAgregarUsuario;
    private UsuarioAdapter usuarioAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        
        dataManager = new DataManager(requireContext());
        usuarioActual = dataManager.getUsuarioActual();
        
        inicializarVistas(view);
        configurarRecyclerView();
        cargarUsuarios();
        
        return view;
    }

    private void inicializarVistas(View view) {
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);
        buttonAgregarUsuario = view.findViewById(R.id.buttonAgregarUsuario);
        
        buttonAgregarUsuario.setOnClickListener(v -> mostrarDialogoUsuario(null));
    }

    private void configurarRecyclerView() {
        usuarioAdapter = new UsuarioAdapter(dataManager.getUsuarios(), this);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = dataManager.getUsuarios();
        usuarioAdapter.actualizarUsuarios(usuarios);
        textViewTitulo.setText("Gestión de Usuarios (" + usuarios.size() + ")");
    }

    private void mostrarDialogoUsuario(Usuario usuarioExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_usuario, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Configurar spinner de roles
        String[] roles = {"jugador", "entrenador", "tutor", "administrador"};
        android.widget.ArrayAdapter<String> rolAdapter = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Configurar spinner de equipos
        List<Equipo> equipos = dataManager.getEquipos();
        String[] nombresEquipos = new String[equipos.size() + 1];
        nombresEquipos[0] = "Sin equipo";
        for (int i = 0; i < equipos.size(); i++) {
            nombresEquipos[i + 1] = equipos.get(i).getNombre();
        }
        
        android.widget.ArrayAdapter<String> equipoAdapter = new android.widget.ArrayAdapter<>(
            requireContext(), android.R.layout.simple_spinner_item, nombresEquipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Llenar datos si es edición
        if (usuarioExistente != null) {
            editTextNombre.setText(usuarioExistente.getNombre());
            editTextEmail.setText(usuarioExistente.getEmail());
            editTextPassword.setText(usuarioExistente.getPassword());
            
            // Seleccionar rol
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equals(usuarioExistente.getRol())) {
                    spinnerRol.setSelection(i);
                    break;
                }
            }
            
            // Seleccionar equipo
            if (usuarioExistente.getEquipoId() != null) {
                for (int i = 0; i < equipos.size(); i++) {
                    if (equipos.get(i).getId().equals(usuarioExistente.getEquipoId())) {
                        spinnerEquipo.setSelection(i + 1);
                        break;
                    }
                }
            } else {
                spinnerEquipo.setSelection(0);
            }
        }
        
        String tituloDialogo = usuarioExistente != null ? "Editar Usuario" : "Nuevo Usuario";
        
        builder.setView(dialogView)
                .setTitle(tituloDialogo)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString();
                    
                    if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    Equipo equipoSeleccionado = null;
                    int equipoIndex = spinnerEquipo.getSelectedItemPosition();
                    if (equipoIndex > 0) {
                        equipoSeleccionado = equipos.get(equipoIndex - 1);
                    }
                    
                    if (usuarioExistente != null) {
                        actualizarUsuario(usuarioExistente, nombre, email, password, rol, equipoSeleccionado);
                    } else {
                        agregarUsuario(nombre, email, password, rol, equipoSeleccionado);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarUsuario(String nombre, String email, String password, String rol, Equipo equipo) {
        Usuario nuevoUsuario = new Usuario(nombre, email, password, rol);
        if (equipo != null) {
            nuevoUsuario.setEquipoId(equipo.getId());
            nuevoUsuario.setEquipoNombre(equipo.getNombre());
        }
        
        List<Usuario> usuarios = dataManager.getUsuarios();
        usuarios.add(nuevoUsuario);
        dataManager.guardarUsuarios(usuarios);
        cargarUsuarios();
        Toast.makeText(requireContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void actualizarUsuario(Usuario usuario, String nombre, String email, String password, String rol, Equipo equipo) {
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRol(rol);
        
        if (equipo != null) {
            usuario.setEquipoId(equipo.getId());
            usuario.setEquipoNombre(equipo.getNombre());
        } else {
            usuario.setEquipoId(null);
            usuario.setEquipoNombre(null);
        }
        
        List<Usuario> usuarios = dataManager.getUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                break;
            }
        }
        dataManager.guardarUsuarios(usuarios);
        cargarUsuarios();
        Toast.makeText(requireContext(), "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUsuarioClick(Usuario usuario) {
        // Mostrar opciones: editar o eliminar
        String[] opciones = {"Editar", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Opciones del usuario")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        // Editar
                        mostrarDialogoUsuario(usuario);
                    } else if (which == 1) {
                        // Eliminar
                        confirmarEliminarUsuario(usuario);
                    }
                })
                .show();
    }

    private void confirmarEliminarUsuario(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que quieres eliminar al usuario '" + usuario.getNombre() + "'? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    List<Usuario> usuarios = dataManager.getUsuarios();
                    List<Usuario> usuariosFiltrados = new java.util.ArrayList<>();
                    for (Usuario u : usuarios) {
                        if (!u.getId().equals(usuario.getId())) {
                            usuariosFiltrados.add(u);
                        }
                    }
                    dataManager.guardarUsuarios(usuariosFiltrados);
                    cargarUsuarios();
                    Toast.makeText(requireContext(), "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarUsuarios();
    }
} 