package com.gestionclub.padres.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gestionclub.padres.R;
import com.gestionclub.padres.adapter.MensajeMuroAdapter;
import com.gestionclub.padres.adapter.ResultadoPartidoAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.ResultadoPartido;
import com.gestionclub.padres.model.Usuario;
import java.util.List;
import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    private DataManager dataManager;
    private Usuario usuarioActual;
    
    // Vistas del layout
    private TextView textViewVerTodoMuro;
    private TextView textViewVerTodoPartidos;
    private RecyclerView recyclerViewMuro;
    private RecyclerView recyclerViewUltimosPartidos;
    private MensajeMuroAdapter mensajeMuroAdapter;
    private ResultadoPartidoAdapter resultadoPartidoAdapter;
    private FloatingActionButton fabAgregarMensaje;
    
    // Separadores de sección
    private TextView textSectionTitleMuro, textSectionSubtitleMuro;
    private TextView textSectionTitlePartidos, textSectionSubtitlePartidos;
    


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
                    inicializarVistas(view);
        cargarDatosUsuario();
        cargarMuro();
        cargarUltimosPartidos();
        } catch (Exception e) {
            Log.e("DashboardFragment", "Error al cargar dashboard", e);
            Toast.makeText(requireContext(), "Error al cargar el dashboard", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        // Configurar separadores de sección
        configurarSeparadoresSeccion(view);
        
        textViewVerTodoMuro = view.findViewById(R.id.textViewVerTodoMuro);
        recyclerViewMuro = view.findViewById(R.id.containerMuroDashboard);
        
        // Configurar RecyclerView del muro
        recyclerViewMuro.setLayoutManager(new LinearLayoutManager(requireContext()));
        mensajeMuroAdapter = new MensajeMuroAdapter(dataManager.getMensajesDestacados(), usuarioActual);
        recyclerViewMuro.setAdapter(mensajeMuroAdapter);
        
        // Configurar listener del adaptador
        mensajeMuroAdapter.setOnMensajeMuroClickListener(new MensajeMuroAdapter.OnMensajeMuroClickListener() {
            @Override
            public void onMensajeClick(Mensaje mensaje) {
                // Mostrar detalles del mensaje
                mostrarDetallesMensaje(mensaje);
            }

            @Override
            public void onMensajeLongClick(Mensaje mensaje, View view) {
                // Mostrar opciones de editar/borrar para admins
                if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                    mostrarOpcionesMensaje(mensaje, view);
                }
            }
        });
        
        // Configurar click en "Ver todo" del muro
        textViewVerTodoMuro.setOnClickListener(v -> {
            navegarAMuroCompleto();
        });
        
        // Configurar RecyclerView de últimos partidos
        recyclerViewUltimosPartidos = view.findViewById(R.id.recyclerViewUltimosPartidos);
        recyclerViewUltimosPartidos.setLayoutManager(new LinearLayoutManager(requireContext()));
        resultadoPartidoAdapter = new ResultadoPartidoAdapter(dataManager.getUltimosResultados(3));
        recyclerViewUltimosPartidos.setAdapter(resultadoPartidoAdapter);
        
        // Configurar listener del adaptador de partidos
        resultadoPartidoAdapter.setOnResultadoPartidoClickListener(resultado -> {
            mostrarDetallesResultado(resultado);
        });
        
        // Configurar click en "Ver todo" de partidos
        textViewVerTodoPartidos = view.findViewById(R.id.textViewVerTodoPartidos);
        textViewVerTodoPartidos.setOnClickListener(v -> {
            navegarAResultadosCompletos();
        });
        
        // Configurar botón flotante
        fabAgregarMensaje = view.findViewById(R.id.fabAgregarMensaje);
        if (usuarioActual != null && usuarioActual.isEsAdmin()) {
            fabAgregarMensaje.setVisibility(View.VISIBLE);
            fabAgregarMensaje.setOnClickListener(v -> {
                mostrarDialogoCrearMensaje();
            });
        } else {
            fabAgregarMensaje.setVisibility(View.GONE);
        }
    }

    private void configurarSeparadoresSeccion(View view) {
        // Configurar separador del muro
        View separatorMuro = view.findViewById(R.id.item_section_separator);
        if (separatorMuro != null) {
            textSectionTitleMuro = separatorMuro.findViewById(R.id.textSectionTitle);
            textSectionSubtitleMuro = separatorMuro.findViewById(R.id.textSectionSubtitle);
            
            if (textSectionTitleMuro != null) {
                textSectionTitleMuro.setText("Comunicación");
            }
            if (textSectionSubtitleMuro != null) {
                textSectionSubtitleMuro.setText("Mensajes del equipo");
            }
        }
        
        // Configurar separador de partidos
        View separatorPartidos = view.findViewById(R.id.item_section_separator);
        if (separatorPartidos != null) {
            textSectionTitlePartidos = separatorPartidos.findViewById(R.id.textSectionTitle);
            textSectionSubtitlePartidos = separatorPartidos.findViewById(R.id.textSectionSubtitle);
            
            if (textSectionTitlePartidos != null) {
                textSectionTitlePartidos.setText("Resultados");
            }
            if (textSectionSubtitlePartidos != null) {
                textSectionSubtitlePartidos.setText("Últimos partidos");
            }
        }
    }

    private void cargarDatosUsuario() {
        // El dashboard no muestra el nombre del usuario directamente
        // Se puede usar para otras configuraciones si es necesario
    }

    private void cargarMuro() {
        try {
            List<Mensaje> mensajesDestacados = dataManager.getMensajesDestacados();
            
            // Si no hay mensajes destacados, crear algunos de ejemplo
            if (mensajesDestacados.isEmpty()) {
                dataManager.crearMensajesDestacadosEjemplo();
                mensajesDestacados = dataManager.getMensajesDestacados();
            }
            
            mensajeMuroAdapter.actualizarMensajes(mensajesDestacados);
            
            Log.d("DashboardFragment", "Cargados " + mensajesDestacados.size() + " mensajes del muro");
            
        } catch (Exception e) {
            Log.e("DashboardFragment", "Error al cargar muro", e);
        }
    }

    private void cargarUltimosPartidos() {
        try {
            List<ResultadoPartido> resultados;
            
            // Si no hay resultados, crear algunos de ejemplo
            if (dataManager.getResultadosPartidos().isEmpty()) {
                dataManager.crearResultadosPartidosEjemplo();
            }
            
            // Obtener resultados según el rol del usuario
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                // Administrador ve todos los resultados
                resultados = dataManager.getUltimosResultados(3);
            } else {
                // Usuario normal ve solo los resultados de su equipo
                String equipoId = usuarioActual != null ? usuarioActual.getEquipoId() : null;
                if (equipoId != null) {
                    resultados = dataManager.getResultadosPorEquipo(equipoId);
                    // Limitar a 3 resultados
                    if (resultados.size() > 3) {
                        resultados = resultados.subList(0, 3);
                    }
                } else {
                    resultados = new ArrayList<>();
                }
            }
            
            resultadoPartidoAdapter.actualizarResultados(resultados);
            
            Log.d("DashboardFragment", "Cargados " + resultados.size() + " resultados de partidos");
            
        } catch (Exception e) {
            Log.e("DashboardFragment", "Error al cargar últimos partidos", e);
        }
    }

    private void mostrarDetallesMensaje(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Detalles del Mensaje")
               .setMessage("Autor: " + mensaje.getAutorNombre() + "\n\n" + mensaje.getContenido())
               .setPositiveButton("Cerrar", null)
               .show();
    }

    private void mostrarOpcionesMensaje(Mensaje mensaje, View view) {
        String[] opciones = {"Editar", "Borrar"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Opciones del mensaje")
               .setItems(opciones, (dialog, which) -> {
                   switch (which) {
                       case 0: // Editar
                           mostrarDialogoEditarMensaje(mensaje);
                           break;
                       case 1: // Borrar
                           confirmarBorrarMensaje(mensaje);
                           break;
                   }
               })
               .show();
    }

    private void mostrarDialogoEditarMensaje(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_mensaje, null);
        
        EditText editTextContenido = dialogView.findViewById(R.id.editTextContenido);
        editTextContenido.setText(mensaje.getContenido());
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // Configurar botones
        dialogView.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String nuevoContenido = editTextContenido.getText().toString().trim();
            if (!nuevoContenido.isEmpty()) {
                mensaje.setContenido(nuevoContenido);
                dataManager.actualizarMensaje(mensaje);
                cargarMuro();
                dialog.dismiss();
                Toast.makeText(requireContext(), "Mensaje actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    private void confirmarBorrarMensaje(Mensaje mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmar borrado")
               .setMessage("¿Estás seguro de que quieres borrar este mensaje?")
               .setPositiveButton("Borrar", (dialog, which) -> {
                   dataManager.eliminarMensaje(mensaje.getId());
                   cargarMuro();
                   Toast.makeText(requireContext(), "Mensaje borrado", Toast.LENGTH_SHORT).show();
               })
               .setNegativeButton("Cancelar", null)
               .show();
    }

    private void mostrarDetallesResultado(ResultadoPartido resultado) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Detalles del Partido")
               .setMessage("Equipo Local: " + resultado.getEquipoLocal() + "\n" +
                          "Equipo Visitante: " + resultado.getEquipoVisitante() + "\n" +
                          "Resultado Final: " + resultado.getResultadoCompleto() + "\n" +
                          "Primera Parte: " + resultado.getResultadoPrimera() + "\n" +
                          "Estado: " + resultado.getEstado() + "\n" +
                          "Ubicación: " + resultado.getUbicacion())
               .setPositiveButton("Cerrar", null)
               .show();
    }

    private void navegarAMuroCompleto() {
        navegarAFragmento(new MuroDestacadosFragment());
    }

    private void navegarAResultadosCompletos() {
        navegarAFragmento(new ResultadosPartidosFragment());
    }

    private void navegarAFragmento(Fragment fragment) {
        try {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e("DashboardFragment", "Error al navegar", e);
            Toast.makeText(requireContext(), "Error al navegar", Toast.LENGTH_SHORT).show();
        }
    }



    private void mostrarDialogoCrearMensaje() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_crear_mensaje, null);
        
        EditText editTextContenido = dialogView.findViewById(R.id.editTextNuevoContenido);
        CheckBox checkBoxDestacado = dialogView.findViewById(R.id.checkBoxDestacado);
        
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // Configurar botones
        dialogView.findViewById(R.id.btnCancelarCrear).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnPublicar).setOnClickListener(v -> {
            String contenido = editTextContenido.getText().toString().trim();
            if (!contenido.isEmpty()) {
                Mensaje nuevoMensaje = new Mensaje(
                    usuarioActual.getId(),
                    usuarioActual.getNombre(),
                    contenido,
                    usuarioActual.isEsAdmin()
                );
                
                if (checkBoxDestacado.isChecked()) {
                    nuevoMensaje.setDestacado(true);
                    dataManager.agregarMensajeDestacado(nuevoMensaje);
                } else {
                    dataManager.agregarMensaje(nuevoMensaje);
                }
                
                cargarMuro();
                dialog.dismiss();
                Toast.makeText(requireContext(), "Mensaje publicado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    private void cerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cerrar Sesión")
               .setMessage("¿Estás seguro de que quieres cerrar sesión?")
               .setPositiveButton("Sí", (dialog, which) -> {
                   dataManager.cerrarSesion();
                   // Navegar a la pantalla de login
                   requireActivity().finish();
               })
               .setNegativeButton("No", null)
               .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosUsuario();
        cargarMuro();
        cargarUltimosPartidos();
    }
} 