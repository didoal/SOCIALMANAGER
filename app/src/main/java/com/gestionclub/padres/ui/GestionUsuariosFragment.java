package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.model.Equipo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosFragment extends Fragment {
    private static final String TAG = "GestionUsuariosFragment";
    
    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private DataManager dataManager;
    private TextView textViewEstadisticas;
    private FloatingActionButton fabAgregarUsuario;
    private Spinner spinnerEquipos;
    private TextView textViewSeleccionEquipo;
    private List<Equipo> listaEquipos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creando vista de gestión de usuarios");
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        
        dataManager = new DataManager(requireContext());
        inicializarVistas(view);
        configurarRecyclerView();
        cargarUsuarios();
        actualizarEstadisticas();
        
        return view;
    }

    private void inicializarVistas(View view) {
        Log.d(TAG, "inicializarVistas: Inicializando vistas");
        recyclerViewUsuarios = view.findViewById(R.id.recyclerViewUsuarios);
        textViewEstadisticas = view.findViewById(R.id.textViewEstadisticas);
        fabAgregarUsuario = view.findViewById(R.id.fabAgregarUsuario);
        spinnerEquipos = view.findViewById(R.id.spinnerEquipos);
        textViewSeleccionEquipo = view.findViewById(R.id.textViewSeleccionEquipo);
        
        fabAgregarUsuario.setOnClickListener(v -> mostrarDialogoCrearUsuario());
    }

    private void configurarRecyclerView() {
        Log.d(TAG, "configurarRecyclerView: Configurando RecyclerView");
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        usuarioAdapter = new UsuarioAdapter(new ArrayList<>(), 
            this::mostrarDialogoEliminarUsuario,
            this::mostrarDialogoEditarUsuario);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarUsuarios() {
        Log.d(TAG, "cargarUsuarios: Cargando lista de usuarios");
        Usuario usuarioActual = dataManager.getUsuarioActual();
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            // Mostrar Spinner y cargar equipos
            spinnerEquipos.setVisibility(View.VISIBLE);
            textViewSeleccionEquipo.setVisibility(View.VISIBLE);
            listaEquipos = dataManager.getEquipos();
            List<String> nombresEquipos = new ArrayList<>();
            for (Equipo equipo : listaEquipos) {
                nombresEquipos.add(equipo.getNombre());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresEquipos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEquipos.setAdapter(adapter);
            spinnerEquipos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    Equipo equipoSeleccionado = listaEquipos.get(position);
                    List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(equipoSeleccionado.getId());
                    usuarioAdapter.actualizarUsuarios(jugadores);
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
            // Mostrar jugadores del primer equipo por defecto
            if (!listaEquipos.isEmpty()) {
                List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(listaEquipos.get(0).getId());
                usuarioAdapter.actualizarUsuarios(jugadores);
            }
        } else if (usuarioActual != null && usuarioActual.getEquipoId() != null) {
            // Ocultar Spinner
            spinnerEquipos.setVisibility(View.GONE);
            textViewSeleccionEquipo.setVisibility(View.GONE);
            List<Usuario> jugadores = dataManager.getJugadoresPorEquipo(usuarioActual.getEquipoId());
            usuarioAdapter.actualizarUsuarios(jugadores);
        } else {
            spinnerEquipos.setVisibility(View.GONE);
            textViewSeleccionEquipo.setVisibility(View.GONE);
            usuarioAdapter.actualizarUsuarios(new ArrayList<>());
        }
    }

    private void actualizarEstadisticas() {
        Log.d(TAG, "actualizarEstadisticas: Actualizando estadísticas");
        List<Usuario> usuarios = dataManager.getUsuarios();
        int totalUsuarios = usuarios.size();
        int admins = 0, padres = 0, madres = 0, tutores = 0, jugadores = 0, entrenadores = 0;
        
        for (Usuario usuario : usuarios) {
            if (usuario.isEsAdmin()) admins++;
            else if ("padre".equalsIgnoreCase(usuario.getRol())) padres++;
            else if ("madre".equalsIgnoreCase(usuario.getRol())) madres++;
            else if ("tutor".equalsIgnoreCase(usuario.getRol())) tutores++;
            else if ("jugador".equalsIgnoreCase(usuario.getRol())) jugadores++;
            else if ("entrenador".equalsIgnoreCase(usuario.getRol())) entrenadores++;
        }
        
        String estadisticas = String.format("Total: %d | Admins: %d | Padres: %d | Madres: %d | Tutores: %d | Jugadores: %d | Entrenadores: %d",
                totalUsuarios, admins, padres, madres, tutores, jugadores, entrenadores);
        textViewEstadisticas.setText(estadisticas);
    }

    private void mostrarDialogoCrearUsuario() {
        Log.d(TAG, "mostrarDialogoCrearUsuario: Mostrando diálogo");
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_usuario, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = dialogView.findViewById(R.id.editTextConfirmPassword);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Configurar spinner de roles
        String[] roles = {"Padre", "Madre", "Tutor", "Jugador", "Entrenador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Listener para actualizar el hint del campo jugador según el rol
        spinnerRol.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = roles[position].toLowerCase();
                if ("entrenador".equals(rolSeleccionado)) {
                    editTextJugador.setHint("Jugador que representa (opcional para entrenadores)");
                } else {
                    editTextJugador.setHint("Jugador que representa *");
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // Configurar spinner de equipos con equipos reales del sistema
        List<String> equipos = new ArrayList<>();
        equipos.add("Sin equipo asignado");
        List<Equipo> equiposReales = dataManager.getEquipos();
        for (Equipo equipo : equiposReales) {
            equipos.add(equipo.getNombre());
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        builder.setView(dialogView)
                .setTitle("Crear Nuevo Usuario")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                    String jugador = editTextJugador.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString().toLowerCase();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    
                    if (validarDatosCreacion(nombre, password, confirmPassword, rol, jugador)) {
                        crearUsuario(nombre, password, jugador, rol, equipo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosCreacion(String nombre, String password, String confirmPassword, String rol, String jugador) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "La contraseña es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Solo requerir jugador para roles que representan a un jugador específico
        if (!"entrenador".equalsIgnoreCase(rol) && !"administrador".equalsIgnoreCase(rol) && jugador.isEmpty()) {
            Toast.makeText(requireContext(), "Debe especificar el jugador al que representa", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void crearUsuario(String nombre, String password, String jugador, String rol, String equipo) {
        Log.d(TAG, "crearUsuario: Creando usuario " + nombre);
        
        // Generar email único basado en el nombre
        String email = generarEmailUnico(nombre);
        
        Usuario nuevoUsuario = new Usuario(nombre, jugador, password, rol);
        nuevoUsuario.setEmail(email);
        
        // Asignar equipo solo si no es "Sin equipo asignado"
        if (!"Sin equipo asignado".equals(equipo)) {
            nuevoUsuario.setEquipo(equipo);
            
            // Buscar el equipo real y asignar el equipoId
            List<Equipo> equipos = dataManager.getEquipos();
            for (Equipo equipoReal : equipos) {
                if (equipoReal.getNombre().equals(equipo)) {
                    nuevoUsuario.setEquipoId(equipoReal.getId());
                    break;
                }
            }
        }
        
        // Si es jugador, usar el nombre como jugador
        if ("jugador".equals(rol)) {
            nuevoUsuario.setJugador(nombre);
        }
        
        dataManager.agregarUsuario(nuevoUsuario);
        cargarUsuarios();
        actualizarEstadisticas();
        
        String mensaje = "Usuario creado exitosamente";
        if ("entrenador".equals(rol)) {
        if (!jugador.isEmpty()) {
                mensaje += " - Entrenador asignado a: " + jugador;
            } else {
                mensaje += " - Entrenador sin jugador específico asignado";
            }
        } else if (!jugador.isEmpty()) {
            mensaje += " - Representa a: " + jugador;
        }
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
        Log.d(TAG, "crearUsuario: Usuario " + nombre + " creado correctamente");
    }

    private String generarEmailUnico(String nombre) {
        String baseEmail = nombre.toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "")
                .replaceAll("\\s+", "") + "@club.com";
        
        // Verificar si ya existe y agregar número si es necesario
        String emailFinal = baseEmail;
        int contador = 1;
        
        while (dataManager.existeUsuario(emailFinal)) {
            emailFinal = baseEmail.replace("@club.com", contador + "@club.com");
            contador++;
        }
        
        return emailFinal;
    }

    private void mostrarDialogoEditarUsuario(Usuario usuario) {
        Log.d(TAG, "mostrarDialogoEditarUsuario: Mostrando diálogo para " + usuario.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_usuario, null);
        
        EditText editTextNombre = dialogView.findViewById(R.id.editTextNombre);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextJugador = dialogView.findViewById(R.id.editTextJugador);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Llenar datos existentes
        editTextNombre.setText(usuario.getNombre());
        editTextEmail.setText(usuario.getEmail());
        editTextJugador.setText(usuario.getJugador());
        
        // Configurar spinner de roles
        String[] roles = {"Padre", "Madre", "Tutor", "Jugador", "Entrenador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Listener para actualizar el hint del campo jugador según el rol
        spinnerRol.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = roles[position].toLowerCase();
                if ("entrenador".equals(rolSeleccionado)) {
                    editTextJugador.setHint("Jugador que representa (opcional para entrenadores)");
                } else {
                    editTextJugador.setHint("Jugador que representa *");
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // Seleccionar rol actual
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].toLowerCase().equals(usuario.getRol())) {
                spinnerRol.setSelection(i);
                break;
            }
        }
        
        // Configurar spinner de equipos
        List<String> equipos = new ArrayList<>();
        equipos.add("Sin equipo asignado");
        List<Equipo> equiposReales = dataManager.getEquipos();
        for (Equipo equipo : equiposReales) {
            equipos.add(equipo.getNombre());
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        // Seleccionar equipo actual
        String equipoActual = usuario.getEquipo();
        if (equipoActual != null && !equipoActual.isEmpty()) {
            for (int i = 0; i < equipos.size(); i++) {
                if (equipos.get(i).equals(equipoActual)) {
                    spinnerEquipo.setSelection(i);
                    break;
                }
            }
        } else {
            spinnerEquipo.setSelection(0); // "Sin equipo asignado"
        }
        
        builder.setView(dialogView)
                .setTitle("Editar Usuario")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();
                    String jugador = editTextJugador.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString().toLowerCase();
                    String equipo = spinnerEquipo.getSelectedItem().toString();
                    
                    if (validarDatosEdicion(nombre, jugador, rol)) {
                        editarUsuario(usuario, nombre, jugador, rol, equipo);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean validarDatosEdicion(String nombre, String jugador, String rol) {
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Solo requerir jugador para roles que representan a un jugador específico
        if (!"entrenador".equalsIgnoreCase(rol) && !"administrador".equalsIgnoreCase(rol) && jugador.isEmpty()) {
            Toast.makeText(requireContext(), "Debe especificar el jugador al que representa", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void editarUsuario(Usuario usuarioOriginal, String nombre, String jugador, String rol, String equipo) {
        Log.d(TAG, "editarUsuario: Editando usuario " + usuarioOriginal.getNombre());
        
        // No permitir cambiar rol de administrador
        if (usuarioOriginal.isEsAdmin() && !rol.equalsIgnoreCase("administrador")) {
            Toast.makeText(requireContext(), "No se puede cambiar el rol de un administrador", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Actualizar datos del usuario
        usuarioOriginal.setNombre(nombre);
        usuarioOriginal.setJugador(jugador);
        usuarioOriginal.setRol(rol);
        
        if (!"Sin equipo asignado".equals(equipo)) {
            usuarioOriginal.setEquipo(equipo);
            
            // Buscar el equipo real y asignar el equipoId
            List<Equipo> equipos = dataManager.getEquipos();
            for (Equipo equipoReal : equipos) {
                if (equipoReal.getNombre().equals(equipo)) {
                    usuarioOriginal.setEquipoId(equipoReal.getId());
                    break;
                }
            }
        } else {
            usuarioOriginal.setEquipo(null);
            usuarioOriginal.setEquipoId(null);
        }
        
        // Si es jugador, usar el nombre como jugador
        if ("jugador".equals(rol)) {
            usuarioOriginal.setJugador(nombre);
        }
        
        // Guardar cambios
        dataManager.actualizarUsuario(usuarioOriginal);
        cargarUsuarios();
        actualizarEstadisticas();
        
        String mensaje = "Usuario actualizado exitosamente";
        if ("entrenador".equals(rol)) {
        if (!jugador.isEmpty()) {
                mensaje += " - Entrenador asignado a: " + jugador;
            } else {
                mensaje += " - Entrenador sin jugador específico asignado";
            }
        } else if (!jugador.isEmpty()) {
            mensaje += " - Representa a: " + jugador;
        }
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
        Log.d(TAG, "editarUsuario: Usuario " + nombre + " actualizado correctamente");
    }

    private void mostrarDialogoEliminarUsuario(Usuario usuario) {
        Log.d(TAG, "mostrarDialogoEliminarUsuario: Mostrando diálogo para " + usuario.getNombre());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que quieres eliminar al usuario '" + usuario.getNombre() + "'?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarUsuario(usuario);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(Usuario usuario) {
        Log.d(TAG, "eliminarUsuario: Eliminando usuario " + usuario.getNombre());
        
        // No permitir eliminar administradores
        if (usuario.isEsAdmin()) {
            Toast.makeText(requireContext(), "No se puede eliminar un administrador", Toast.LENGTH_SHORT).show();
            return;
        }
        
        dataManager.eliminarUsuario(usuario.getEmail());
        cargarUsuarios();
        actualizarEstadisticas();
        
        Toast.makeText(requireContext(), "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "eliminarUsuario: Usuario " + usuario.getNombre() + " eliminado correctamente");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
        cargarUsuarios();
        actualizarEstadisticas();
    }
} 