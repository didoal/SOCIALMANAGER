package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.util.ExcelImporter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GestionUsuariosFragment extends Fragment {
    
    private static final int PICK_EXCEL_FILE = 1;
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private TextView textViewTotalUsuarios;
    private TextView textViewTotalAdmins;
    private LinearLayout containerUsuarios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_usuarios, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarBotones(view);
            cargarUsuarios();
            actualizarEstadisticas();
        } catch (Exception e) {
            Log.e("GestionUsuariosFragment", "Error al cargar gestión de usuarios", e);
            Toast.makeText(requireContext(), "Error al cargar la gestión de usuarios", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        textViewTotalUsuarios = view.findViewById(R.id.textViewTotalUsuarios);
        textViewTotalAdmins = view.findViewById(R.id.textViewTotalAdmins);
        containerUsuarios = view.findViewById(R.id.containerUsuarios);
    }

    private void configurarBotones(View view) {


        // Configurar botón de agregar usuario
        View btnAddUser = view.findViewById(R.id.btnAddUser);
        if (btnAddUser != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddUser.setVisibility(View.VISIBLE);
                btnAddUser.setOnClickListener(v -> {
                    mostrarOpcionesCrearUsuario();
                });
            } else {
                btnAddUser.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarOpcionesCrearUsuario() {
        String[] opciones = {"Crear Usuario Manualmente", "Importar desde Excel"};
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Crear Usuario")
            .setItems(opciones, (dialog, which) -> {
                switch (which) {
                    case 0:
                        mostrarDialogoCrearUsuario();
                        break;
                    case 1:
                        importarDesdeExcel();
                        break;
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void importarDesdeExcel() {
        // Mostrar información sobre el formato
        new AlertDialog.Builder(requireContext())
            .setTitle("Importar desde Excel")
            .setMessage(ExcelImporter.getExampleFormat())
            .setPositiveButton("Seleccionar Archivo", (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_EXCEL_FILE);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_EXCEL_FILE && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                procesarArchivoExcel(fileUri);
            }
        }
    }

    private void procesarArchivoExcel(Uri fileUri) {
        try {
            ExcelImporter.ImportResult result = ExcelImporter.importUsersFromExcel(requireContext(), fileUri);
            
            StringBuilder message = new StringBuilder();
            message.append("Importación completada:\n");
            message.append("• Total de filas procesadas: ").append(result.totalRows).append("\n");
            message.append("• Usuarios creados exitosamente: ").append(result.successCount).append("\n");
            message.append("• Errores: ").append(result.errorCount).append("\n");
            
            if (!result.errors.isEmpty()) {
                message.append("\nErrores encontrados:\n");
                for (String error : result.errors) {
                    message.append("• ").append(error).append("\n");
                }
            }
            
            new AlertDialog.Builder(requireContext())
                .setTitle("Resultado de Importación")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> {
                    cargarUsuarios();
                    actualizarEstadisticas();
                })
                .show();
                
        } catch (Exception e) {
            Log.e("GestionUsuariosFragment", "Error procesando archivo Excel", e);
            Toast.makeText(requireContext(), "Error al procesar el archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarDialogoCrearUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_crear_usuario, null);
        
        // Obtener referencias a los campos
        EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText editTextNombreCompleto = dialogView.findViewById(R.id.editTextNombreCompleto);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        Spinner spinnerRol = dialogView.findViewById(R.id.spinnerRol);
        Spinner spinnerEquipo = dialogView.findViewById(R.id.spinnerEquipo);
        
        // Configurar spinner de roles
        String[] roles = {"Padre/Tutor", "Administrador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);
        
        // Configurar spinner de equipos
        List<Equipo> equipos = dataManager.getEquipos();
        List<String> nombresEquipos = new ArrayList<>();
        nombresEquipos.add("Sin equipo asignado");
        for (Equipo equipo : equipos) {
            nombresEquipos.add(equipo.getNombre());
        }
        ArrayAdapter<String> equipoAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, nombresEquipos);
        equipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(equipoAdapter);
        
        builder.setView(dialogView)
               .setPositiveButton("Crear Usuario", null)
               .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
        
        AlertDialog dialog = builder.create();
        
        // Configurar el botón positivo para validar antes de cerrar
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String nombreCompleto = editTextNombreCompleto.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String rol = spinnerRol.getSelectedItem().toString();
                String equipoSeleccionado = spinnerEquipo.getSelectedItem().toString();
                
                // Validaciones
                if (username.isEmpty()) {
                    editTextUsername.setError("El nombre de usuario es obligatorio");
                    return;
                }
                if (password.isEmpty()) {
                    editTextPassword.setError("La contraseña es obligatoria");
                    return;
                }
                if (nombreCompleto.isEmpty()) {
                    editTextNombreCompleto.setError("El nombre completo es obligatorio");
                    return;
                }
                if (dataManager.existeUsuario(username)) {
                    editTextUsername.setError("El nombre de usuario ya existe");
                    return;
                }
                
                // Crear usuario
                try {
                    Usuario nuevoUsuario = new Usuario(nombreCompleto, null, password, rol);
                    nuevoUsuario.setId(UUID.randomUUID().toString());
                    nuevoUsuario.setEmail(email.isEmpty() ? null : email);
                    
                    // Asignar equipo si se seleccionó uno
                    if (!equipoSeleccionado.equals("Sin equipo asignado")) {
                        for (Equipo equipo : equipos) {
                            if (equipo.getNombre().equals(equipoSeleccionado)) {
                                nuevoUsuario.setEquipoId(equipo.getId());
                                break;
                            }
                        }
                    }
                    
                    dataManager.agregarUsuario(nuevoUsuario);
                    
                    Toast.makeText(requireContext(), "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    
                    // Recargar la lista
                    cargarUsuarios();
                    actualizarEstadisticas();
                    
                } catch (Exception e) {
                    Log.e("GestionUsuariosFragment", "Error al crear usuario", e);
                    Toast.makeText(requireContext(), "Error al crear usuario", Toast.LENGTH_SHORT).show();
                }
            });
        });
        
        dialog.show();
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = dataManager.getUsuarios();
            
            // Limpiar contenedor si existe
            if (containerUsuarios != null) {
                containerUsuarios.removeAllViews();
            }
            
            // Crear vistas para cada usuario
            for (Usuario usuario : usuarios) {
                View usuarioView = crearVistaUsuario(usuario);
                if (usuarioView != null && containerUsuarios != null) {
                    containerUsuarios.addView(usuarioView);
                }
            }
            
            Log.d("GestionUsuariosFragment", "Cargando " + usuarios.size() + " usuarios");
        } catch (Exception e) {
            Log.e("GestionUsuariosFragment", "Error al cargar usuarios", e);
        }
    }

    private View crearVistaUsuario(Usuario usuario) {
        try {
            // Crear el layout del usuario
            CardView cardView = new CardView(requireContext());
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 32);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(48);
            cardView.setCardElevation(16);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

            // Contenido del card
            LinearLayout contentLayout = new LinearLayout(requireContext());
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setPadding(64, 64, 64, 64);

            // Header del usuario
            LinearLayout headerLayout = new LinearLayout(requireContext());
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            headerParams.setMargins(0, 0, 0, 48);
            headerLayout.setLayoutParams(headerParams);

            // Avatar del usuario
            TextView avatarView = new TextView(requireContext());
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(160, 160);
            avatarParams.setMargins(0, 0, 48, 0);
            avatarView.setLayoutParams(avatarParams);
            avatarView.setBackgroundResource(R.drawable.circle_button_blue);
            avatarView.setText(usuario.getNombre().substring(0, 1).toUpperCase());
            avatarView.setTextColor(getResources().getColor(R.color.white));
            avatarView.setTextSize(16);
            avatarView.setGravity(android.view.Gravity.CENTER);

            // Información del usuario
            LinearLayout userLayout = new LinearLayout(requireContext());
            userLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams userParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            userLayout.setLayoutParams(userParams);

            TextView userName = new TextView(requireContext());
            userName.setText(usuario.getNombre());
            userName.setTextColor(getResources().getColor(R.color.text_primary));
            userName.setTextSize(16);
            userName.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView userEmail = new TextView(requireContext());
            userEmail.setText(usuario.getEmail() != null ? usuario.getEmail() : "Sin email");
            userEmail.setTextColor(getResources().getColor(R.color.text_secondary));
            userEmail.setTextSize(14);

            userLayout.addView(userName);
            userLayout.addView(userEmail);

            // Badge de rol
            TextView rolBadge = new TextView(requireContext());
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            badgeParams.setMargins(32, 0, 0, 0);
            rolBadge.setLayoutParams(badgeParams);
            rolBadge.setText(usuario.isEsAdmin() ? "ADMIN" : "PADRE");
            rolBadge.setTextColor(usuario.isEsAdmin() ? 
                getResources().getColor(R.color.green) : getResources().getColor(R.color.blue_profesional));
            rolBadge.setTextSize(12);
            rolBadge.setTypeface(null, android.graphics.Typeface.BOLD);
            rolBadge.setBackgroundResource(usuario.isEsAdmin() ? 
                R.drawable.rol_admin_background : R.drawable.rol_background);
            rolBadge.setPadding(16, 16, 16, 16);

            headerLayout.addView(avatarView);
            headerLayout.addView(userLayout);
            headerLayout.addView(rolBadge);

            // Información del equipo
            LinearLayout equipoLayout = new LinearLayout(requireContext());
            equipoLayout.setOrientation(LinearLayout.HORIZONTAL);
            equipoLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams equipoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            equipoParams.setMargins(0, 0, 0, 48);
            equipoLayout.setLayoutParams(equipoParams);

            TextView equipoText = new TextView(requireContext());
            String equipoNombre = "Sin equipo asignado";
            if (usuario.getEquipoId() != null && !usuario.getEquipoId().isEmpty()) {
                Equipo equipo = dataManager.getEquipoPorId(usuario.getEquipoId());
                if (equipo != null) {
                    equipoNombre = equipo.getNombre();
                }
            }
            equipoText.setText("Equipo: " + equipoNombre);
            equipoText.setTextColor(getResources().getColor(R.color.text_secondary));
            equipoText.setTextSize(14);

            equipoLayout.addView(equipoText);

            // Botones de acción
            LinearLayout actionLayout = new LinearLayout(requireContext());
            actionLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionLayout.setGravity(android.view.Gravity.END);
            LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            actionLayout.setLayoutParams(actionParams);

            Button editButton = new Button(requireContext());
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            editParams.setMargins(0, 0, 32, 0);
            editButton.setLayoutParams(editParams);
            editButton.setText("Editar");
            editButton.setTextColor(getResources().getColor(R.color.blue_profesional));
            editButton.setTextSize(12);
            editButton.setBackgroundResource(R.drawable.button_outline_background);
            editButton.setOnClickListener(v -> mostrarDialogoEditarUsuario(usuario));

            Button deleteButton = new Button(requireContext());
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            deleteButton.setLayoutParams(deleteParams);
            deleteButton.setText("Eliminar");
            deleteButton.setTextColor(getResources().getColor(R.color.red));
            deleteButton.setTextSize(12);
            deleteButton.setBackgroundResource(R.drawable.button_outline_background);
            deleteButton.setOnClickListener(v -> confirmarEliminarUsuario(usuario));

            actionLayout.addView(editButton);
            actionLayout.addView(deleteButton);

            // Agregar elementos al layout
            contentLayout.addView(headerLayout);
            contentLayout.addView(equipoLayout);
            contentLayout.addView(actionLayout);

            // Agregar contenido al card
            cardView.addView(contentLayout);

            return cardView;

        } catch (Exception e) {
            Log.e("GestionUsuariosFragment", "Error al crear vista de usuario", e);
            return null;
        }
    }

    private void mostrarDialogoEditarUsuario(Usuario usuario) {
        // Implementar diálogo de edición
        Toast.makeText(requireContext(), "Función de editar usuario próximamente", Toast.LENGTH_SHORT).show();
    }

    private void confirmarEliminarUsuario(Usuario usuario) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro de que desea eliminar al usuario " + usuario.getNombre() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> {
                try {
                    dataManager.eliminarUsuario(usuario.getEmail());
                    Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                    actualizarEstadisticas();
                } catch (Exception e) {
                    Log.e("GestionUsuariosFragment", "Error al eliminar usuario", e);
                    Toast.makeText(requireContext(), "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void actualizarEstadisticas() {
        try {
            List<Usuario> usuarios = dataManager.getUsuarios();
            
            // Actualizar total de usuarios
            if (textViewTotalUsuarios != null) {
                textViewTotalUsuarios.setText(String.valueOf(usuarios.size()));
            }
            
            // Calcular total de administradores
            int totalAdmins = 0;
            for (Usuario usuario : usuarios) {
                if (usuario.isEsAdmin()) {
                    totalAdmins++;
                }
            }
            
            if (textViewTotalAdmins != null) {
                textViewTotalAdmins.setText(String.valueOf(totalAdmins));
            }
            
        } catch (Exception e) {
            Log.e("GestionUsuariosFragment", "Error al actualizar estadísticas", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarUsuarios();
        actualizarEstadisticas();
    }
} 