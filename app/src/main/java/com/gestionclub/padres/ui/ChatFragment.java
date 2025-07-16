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
import com.gestionclub.padres.adapter.MensajeAdapter;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.AdapterView;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Button buttonEnviar;
    private Button buttonFiltroEquipo;
    private Button buttonDestacados;
    private TextView textViewChatInfo;
    private TextView textViewEquipoFiltro;
    private MensajeAdapter mensajeAdapter;
    private DataManager dataManager;
    private Usuario usuarioActual;
    private String equipoFiltroActual = "Todos";
    private boolean mostrandoDestacados = false;
    
    // Vistas de filtros avanzados
    private LinearLayout layoutHeaderFiltros;
    private LinearLayout layoutContenidoFiltros;
    private ImageView imageViewExpandirFiltros;
    private Spinner spinnerFiltroEquipo;
    private Spinner spinnerFiltroTipoMensaje;
    private Spinner spinnerFiltroUsuario;
    private Spinner spinnerFiltroEstado;
    private Button buttonAplicarFiltros;
    private Button buttonLimpiarFiltros;
    
    // Vistas de estadísticas
    private TextView textViewTotalMensajes;
    private TextView textViewMensajesDestacados;
    
    // Vistas de controles
    private Button buttonExportarChat;
    
    // Variables de filtros
    private String filtroEquipoAvanzado = "TODOS";
    private String filtroTipoMensaje = "TODOS";
    private String filtroUsuario = "TODOS";
    private String filtroEstado = "TODOS";
    private boolean filtrosExpandidos = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            inicializarVistas(view);
            configurarRecyclerView();
            configurarFiltroEquipo();
            cargarMensajes();
            configurarListeners();
        } catch (Exception e) {
            Log.e("Chat", "Error al cargar mensajes", e);
            mostrarErrorCarga(view);
        }
        return view;
    }
    
    private void mostrarErrorCarga(View view) {
        // Ocultar vistas principales
        if (recyclerViewMensajes != null) {
            recyclerViewMensajes.setVisibility(View.GONE);
        }
        
        // Mostrar solo el Toast de error
        Toast.makeText(requireContext(), R.string.error_cargar_mensajes, Toast.LENGTH_LONG).show();
    }

    private void inicializarVistas(View view) {
        recyclerViewMensajes = view.findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = view.findViewById(R.id.editTextMensaje);
        buttonEnviar = view.findViewById(R.id.buttonEnviar);
        buttonFiltroEquipo = view.findViewById(R.id.buttonFiltroEquipo);
        buttonDestacados = view.findViewById(R.id.buttonDestacados);
        textViewChatInfo = view.findViewById(R.id.textViewChatInfo);
        textViewEquipoFiltro = view.findViewById(R.id.textViewEquipoFiltro);
        
        // Inicializar vistas de filtros avanzados
        layoutHeaderFiltros = view.findViewById(R.id.layoutHeaderFiltros);
        layoutContenidoFiltros = view.findViewById(R.id.layoutContenidoFiltros);
        imageViewExpandirFiltros = view.findViewById(R.id.imageViewExpandirFiltros);
        spinnerFiltroEquipo = view.findViewById(R.id.spinnerFiltroEquipo);
        spinnerFiltroTipoMensaje = view.findViewById(R.id.spinnerFiltroTipoMensaje);
        spinnerFiltroUsuario = view.findViewById(R.id.spinnerFiltroUsuario);
        spinnerFiltroEstado = view.findViewById(R.id.spinnerFiltroEstado);
        buttonAplicarFiltros = view.findViewById(R.id.buttonAplicarFiltros);
        buttonLimpiarFiltros = view.findViewById(R.id.buttonLimpiarFiltros);
        
        // Inicializar vistas de estadísticas
        textViewTotalMensajes = view.findViewById(R.id.textViewTotalMensajes);
        textViewMensajesDestacados = view.findViewById(R.id.textViewMensajesDestacados);
        
        // Inicializar vistas de controles
        buttonExportarChat = view.findViewById(R.id.buttonExportarChat);
    }

    private void configurarRecyclerView() {
        mensajeAdapter = new MensajeAdapter(new ArrayList<>(), usuarioActual);
        mensajeAdapter.setOnMensajeClickListener(new MensajeAdapter.OnMensajeClickListener() {
            @Override
            public void onMensajeLongClick(Mensaje mensaje, View view) {
                mostrarDialogoDestacar(mensaje);
            }
        });
        recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewMensajes.setAdapter(mensajeAdapter);
    }

    private void configurarFiltroEquipo() {
        // Mostrar botones de filtro y destacados para administradores y entrenadores
        boolean puedeGestionar = usuarioActual != null && 
            (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()));
        
        if (puedeGestionar) {
            buttonFiltroEquipo.setVisibility(View.VISIBLE);
            buttonDestacados.setVisibility(View.VISIBLE);
        } else {
            buttonFiltroEquipo.setVisibility(View.GONE);
            buttonDestacados.setVisibility(View.GONE);
            // Para usuarios normales, filtrar automáticamente por su equipo
            if (usuarioActual != null && usuarioActual.getEquipo() != null) {
                equipoFiltroActual = usuarioActual.getEquipo();
                textViewEquipoFiltro.setText("Equipo: " + equipoFiltroActual);
            }
        }
    }
    
    private void configurarFiltrosAvanzados() {
        // Configurar expansión/colapso de filtros
        if (layoutHeaderFiltros != null) {
            layoutHeaderFiltros.setOnClickListener(v -> toggleFiltros());
        }
        
        // Configurar spinners
        configurarSpinnerEquipo();
        configurarSpinnerTipoMensaje();
        configurarSpinnerUsuario();
        configurarSpinnerEstado();
        
        // Configurar botones de acción
        if (buttonAplicarFiltros != null) {
            buttonAplicarFiltros.setOnClickListener(v -> aplicarFiltros());
        }
        
        if (buttonLimpiarFiltros != null) {
            buttonLimpiarFiltros.setOnClickListener(v -> limpiarFiltros());
        }
        
        if (buttonExportarChat != null) {
            buttonExportarChat.setOnClickListener(v -> exportarChatPDF());
        }
        
        // Configurar visibilidad según rol
        configurarVisibilidadFiltros();
    }
    
    private void configurarVisibilidadFiltros() {
        // Mostrar filtros para administradores y entrenadores
        boolean puedeGestionar = usuarioActual != null && 
            (usuarioActual.isEsAdmin() || "entrenador".equals(usuarioActual.getRol()));
        
        if (layoutHeaderFiltros != null) {
            layoutHeaderFiltros.setVisibility(puedeGestionar ? View.VISIBLE : View.GONE);
        }
    }
    
    private void toggleFiltros() {
        if (layoutContenidoFiltros == null || imageViewExpandirFiltros == null) return;
        
        filtrosExpandidos = !filtrosExpandidos;
        
        if (filtrosExpandidos) {
            layoutContenidoFiltros.setVisibility(View.VISIBLE);
            imageViewExpandirFiltros.setRotation(180f);
        } else {
            layoutContenidoFiltros.setVisibility(View.GONE);
            imageViewExpandirFiltros.setRotation(0f);
        }
    }
    
    private void configurarSpinnerEquipo() {
        if (spinnerFiltroEquipo == null) return;
        
        List<String> equipos = new ArrayList<>();
        equipos.add("TODOS");
        
        // Si es entrenador, solo mostrar su equipo
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos.add(usuarioActual.getEquipo());
        } else {
            // Si es admin, mostrar todos los equipos
            equipos.addAll(dataManager.getNombresEquipos());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEquipo.setAdapter(adapter);
        
        spinnerFiltroEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEquipoAvanzado = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEquipoAvanzado = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerTipoMensaje() {
        if (spinnerFiltroTipoMensaje == null) return;
        
        String[] tipos = {"TODOS", "GENERAL", "INFORMATIVO", "URGENTE", "ANUNCIO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipoMensaje.setAdapter(adapter);
        
        spinnerFiltroTipoMensaje.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroTipoMensaje = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroTipoMensaje = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerUsuario() {
        if (spinnerFiltroUsuario == null) return;
        
        List<String> usuarios = new ArrayList<>();
        usuarios.add("TODOS");
        
        // Obtener usuarios únicos de los mensajes
        List<Mensaje> mensajes = dataManager.getMensajes();
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getUsuario() != null && !mensaje.getUsuario().isEmpty() && 
                !usuarios.contains(mensaje.getUsuario())) {
                usuarios.add(mensaje.getUsuario());
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, usuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroUsuario.setAdapter(adapter);
        
        spinnerFiltroUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroUsuario = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroUsuario = "TODOS";
            }
        });
    }
    
    private void configurarSpinnerEstado() {
        if (spinnerFiltroEstado == null) return;
        
        String[] estados = {"TODOS", "NORMAL", "DESTACADO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroEstado.setAdapter(adapter);
        
        spinnerFiltroEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstado = parent.getItemAtPosition(position).toString();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroEstado = "TODOS";
            }
        });
    }
    
    private void aplicarFiltros() {
        // Aplicar filtros y recargar mensajes
        cargarMensajes();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Filtros aplicados correctamente", Toast.LENGTH_SHORT).show();
    }
    
    private void limpiarFiltros() {
        // Limpiar todos los filtros
        filtroEquipoAvanzado = "TODOS";
        filtroTipoMensaje = "TODOS";
        filtroUsuario = "TODOS";
        filtroEstado = "TODOS";
        
        // Resetear spinners
        if (spinnerFiltroEquipo != null) spinnerFiltroEquipo.setSelection(0);
        if (spinnerFiltroTipoMensaje != null) spinnerFiltroTipoMensaje.setSelection(0);
        if (spinnerFiltroUsuario != null) spinnerFiltroUsuario.setSelection(0);
        if (spinnerFiltroEstado != null) spinnerFiltroEstado.setSelection(0);
        
        // Recargar mensajes
        cargarMensajes();
        actualizarEstadisticas();
        Toast.makeText(requireContext(), "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }

    private void cargarMensajes() {
        List<Mensaje> mensajesFiltrados = new ArrayList<>();
        
        // Obtener mensajes según el modo (destacados o todos)
        List<Mensaje> todosMensajes;
        if (mostrandoDestacados) {
            todosMensajes = dataManager.getMensajesDestacados();
        } else {
            todosMensajes = dataManager.getMensajes();
        }
        
        // Aplicar filtros avanzados
        todosMensajes = aplicarFiltrosAvanzados(todosMensajes);
        
        // Aplicar filtro de equipo básico (para compatibilidad)
        for (Mensaje mensaje : todosMensajes) {
            if ("Todos".equals(equipoFiltroActual)) {
                mensajesFiltrados.add(mensaje);
            } else {
                if (equipoFiltroActual.equals(mensaje.getEquipo()) || 
                    (mensaje.getEquipo() == null && usuarioActual != null && equipoFiltroActual.equals(usuarioActual.getEquipo()))) {
                    mensajesFiltrados.add(mensaje);
                }
            }
        }
        
        mensajeAdapter.actualizarMensajes(mensajesFiltrados);
        if (!mensajesFiltrados.isEmpty()) {
            recyclerViewMensajes.smoothScrollToPosition(mensajesFiltrados.size() - 1);
        }
        
        // Actualizar estadísticas
        actualizarEstadisticas();
    }
    
    private List<Mensaje> aplicarFiltrosAvanzados(List<Mensaje> mensajes) {
        List<Mensaje> mensajesFiltrados = new ArrayList<>();
        
        for (Mensaje mensaje : mensajes) {
            boolean cumpleFiltros = true;
            
            // Filtro por equipo
            if (!filtroEquipoAvanzado.equals("TODOS")) {
                if (!filtroEquipoAvanzado.equals(mensaje.getEquipo())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por tipo de mensaje
            if (!filtroTipoMensaje.equals("TODOS")) {
                if (!filtroTipoMensaje.equals(mensaje.getTipo())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por usuario
            if (!filtroUsuario.equals("TODOS")) {
                if (!filtroUsuario.equals(mensaje.getUsuario())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por estado
            if (!filtroEstado.equals("TODOS")) {
                if (filtroEstado.equals("DESTACADO") && !mensaje.isDestacado()) {
                    cumpleFiltros = false;
                } else if (filtroEstado.equals("NORMAL") && mensaje.isDestacado()) {
                    cumpleFiltros = false;
                }
            }
            
            if (cumpleFiltros) {
                mensajesFiltrados.add(mensaje);
            }
        }
        
        return mensajesFiltrados;
    }

    private void configurarListeners() {
        buttonEnviar.setOnClickListener(v -> enviarMensaje());
        buttonFiltroEquipo.setOnClickListener(v -> mostrarDialogoFiltro());
        buttonDestacados.setOnClickListener(v -> toggleDestacados());
        
        editTextMensaje.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensaje();
            return true;
        });
    }

    private void toggleDestacados() {
        mostrandoDestacados = !mostrandoDestacados;
        if (mostrandoDestacados) {
            buttonDestacados.setText("Ver Todos");
            textViewChatInfo.setText("Muro de Mensajes Destacados");
        } else {
            buttonDestacados.setText("Ver Destacados");
            textViewChatInfo.setText("Chat General del Club");
        }
        cargarMensajes();
    }

    private void mostrarDialogoDestacar(Mensaje mensaje) {
        // Verificar permisos para destacar mensajes
        boolean puedeDestacar = false;
        
        if (usuarioActual != null) {
            if (usuarioActual.isEsAdmin()) {
                // Los administradores pueden destacar cualquier mensaje
                puedeDestacar = true;
            } else if ("entrenador".equals(usuarioActual.getRol())) {
                // Los entrenadores solo pueden destacar mensajes de su equipo
                puedeDestacar = usuarioActual.getEquipo() != null && 
                    (usuarioActual.getEquipo().equals(mensaje.getEquipo()) || 
                     mensaje.getEquipo() == null);
            }
        }
        
        if (!puedeDestacar) {
            Toast.makeText(requireContext(), "No tienes permisos para destacar este mensaje", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String accion = mensaje.isDestacado() ? "Quitar de destacados" : "Destacar mensaje";
        String mensajeDialogo = mensaje.isDestacado() ? 
            "¿Quieres quitar este mensaje de los destacados?" : 
            "¿Quieres destacar este mensaje para que aparezca en el muro?";

        new AlertDialog.Builder(requireContext())
            .setTitle(accion)
            .setMessage(mensajeDialogo)
            .setPositiveButton("Sí", (dialog, which) -> {
                dataManager.toggleDestacadoMensaje(mensaje.getId());
                cargarMensajes();
                String confirmacion = mensaje.isDestacado() ? 
                    "Mensaje quitado de destacados" : "Mensaje destacado";
                Toast.makeText(requireContext(), confirmacion, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void mostrarDialogoFiltro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Filtrar por Equipo");

        // Crear spinner con opciones
        Spinner spinner = new Spinner(requireContext());
        String[] equipos;
        
        // Si es entrenador, solo mostrar su equipo y "Todos"
        if (usuarioActual != null && "entrenador".equals(usuarioActual.getRol()) && 
            usuarioActual.getEquipo() != null) {
            equipos = new String[]{"Todos", usuarioActual.getEquipo()};
        } else {
            // Si es admin, mostrar todos los equipos
            equipos = new String[]{"Todos", "Biberones", "Prebenjamín A", "Prebenjamín B", "Benjamín A", "Benjamín B", 
                               "Alevín A", "Alevín B", "Infantil", "Cadete", "Juvenil"};
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, equipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Seleccionar el equipo actual
        for (int i = 0; i < equipos.length; i++) {
            if (equipos[i].equals(equipoFiltroActual)) {
                spinner.setSelection(i);
                break;
            }
        }

        builder.setView(spinner);
        builder.setPositiveButton("Aplicar", (dialog, which) -> {
            equipoFiltroActual = spinner.getSelectedItem().toString();
            textViewEquipoFiltro.setText("Equipo: " + equipoFiltroActual);
            cargarMensajes();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void enviarMensaje() {
        String contenido = editTextMensaje.getText().toString().trim();
        
        if (contenido.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Mensaje nuevoMensaje = new Mensaje(
            usuarioActual.getId(),
            usuarioActual.getNombre(),
            contenido,
            usuarioActual.isEsAdmin()
        );
        
        // Asignar equipo al mensaje
        nuevoMensaje.setEquipo(usuarioActual.getEquipo());

        dataManager.agregarMensaje(nuevoMensaje);
        cargarMensajes();
        editTextMensaje.setText("");

        // Crear notificación para nuevos mensajes (solo si es admin)
        if (usuarioActual.isEsAdmin()) {
            crearNotificacionMensaje(nuevoMensaje);
        }
    }

    private void crearNotificacionMensaje(Mensaje mensaje) {
        dataManager.crearNotificacionMensaje(mensaje);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajes();
    }
} 