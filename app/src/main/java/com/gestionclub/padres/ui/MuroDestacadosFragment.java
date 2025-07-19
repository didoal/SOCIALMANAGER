package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.gestionclub.padres.R;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Mensaje;
import com.gestionclub.padres.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MuroDestacadosFragment extends Fragment {
    
    private DataManager dataManager;
    private Usuario usuarioActual;
    private LinearLayout containerMensajes;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_muro_destacados, container, false);
        try {
            dataManager = new DataManager(requireContext());
            usuarioActual = dataManager.getUsuarioActual();
            dateFormat = new SimpleDateFormat("EEEE, h:mm a", new Locale("es", "ES"));
            
            inicializarVistas(view);
            configurarBotones(view);
            cargarMensajesDestacados();
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al cargar muro", e);
            Toast.makeText(requireContext(), "Error al cargar el muro", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inicializarVistas(View view) {
        containerMensajes = view.findViewById(R.id.containerMensajes);
    }

    private void configurarBotones(View view) {
        // Configurar botón de retroceso
        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // Configurar botón de agregar post (solo para admins)
        View btnAddPost = view.findViewById(R.id.btnAddPost);
        if (btnAddPost != null) {
            if (usuarioActual != null && usuarioActual.isEsAdmin()) {
                btnAddPost.setVisibility(View.VISIBLE);
                btnAddPost.setOnClickListener(v -> {
                    mostrarDialogoCrearPost();
                });
            } else {
                btnAddPost.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarDialogoCrearPost() {
        // Aquí se mostraría un diálogo para crear un nuevo post destacado
        Toast.makeText(requireContext(), "Función de crear post destacado próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cargarMensajesDestacados() {
        try {
            // Limpiar contenedor
            if (containerMensajes != null) {
                containerMensajes.removeAllViews();
            }

            // Obtener mensajes destacados
            List<Mensaje> mensajesDestacados = dataManager.getMensajesDestacados();
            
            // Si no hay mensajes destacados, crear algunos de ejemplo
            if (mensajesDestacados.isEmpty()) {
                dataManager.crearMensajesDestacadosEjemplo();
                mensajesDestacados = dataManager.getMensajesDestacados();
            }

            if (mensajesDestacados.isEmpty()) {
                mostrarMensajeVacio();
                return;
            }

            // Crear vistas para cada mensaje destacado
            for (Mensaje mensaje : mensajesDestacados) {
                View mensajeView = crearVistaMensaje(mensaje);
                if (mensajeView != null) {
                    containerMensajes.addView(mensajeView);
                }
            }

            Log.d("MuroDestacados", "Cargados " + mensajesDestacados.size() + " mensajes destacados");
            
        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al cargar mensajes destacados", e);
            mostrarMensajeVacio();
        }
    }

    private void mostrarMensajeVacio() {
        if (containerMensajes != null) {
            TextView textViewVacio = new TextView(requireContext());
            textViewVacio.setText("No hay mensajes destacados");
            textViewVacio.setTextColor(getResources().getColor(R.color.text_secondary));
            textViewVacio.setTextSize(16);
            textViewVacio.setGravity(android.view.Gravity.CENTER);
            textViewVacio.setPadding(32, 64, 32, 64);
            
            containerMensajes.addView(textViewVacio);
        }
    }

    private View crearVistaMensaje(Mensaje mensaje) {
        try {
            // Crear el layout del mensaje
            CardView cardView = new CardView(requireContext());
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 0, 0, 32);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(48); // 12dp en píxeles
            cardView.setCardElevation(16); // 4dp en píxeles
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

            // Contenido del card
            LinearLayout contentLayout = new LinearLayout(requireContext());
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setPadding(64, 64, 64, 64); // 16dp en píxeles

            // Header del mensaje
            LinearLayout headerLayout = new LinearLayout(requireContext());
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            headerParams.setMargins(0, 0, 0, 48); // 12dp en píxeles
            headerLayout.setLayoutParams(headerParams);

            // Avatar del administrador
            TextView avatarView = new TextView(requireContext());
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(160, 160); // 40dp en píxeles
            avatarParams.setMargins(0, 0, 48, 0); // 12dp en píxeles
            avatarView.setLayoutParams(avatarParams);
            avatarView.setBackgroundResource(R.drawable.circle_button_blue);
            avatarView.setText("A");
            avatarView.setTextColor(getResources().getColor(R.color.white));
            avatarView.setTextSize(16);
            avatarView.setGravity(android.view.Gravity.CENTER);

            // Información del autor
            LinearLayout authorLayout = new LinearLayout(requireContext());
            authorLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
            );
            authorLayout.setLayoutParams(authorParams);

            TextView authorName = new TextView(requireContext());
            authorName.setText(mensaje.getAutorNombre());
            authorName.setTextColor(getResources().getColor(R.color.text_primary));
            authorName.setTextSize(16);
            authorName.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView dateText = new TextView(requireContext());
            if (mensaje.getFechaCreacion() != null) {
                dateText.setText(dateFormat.format(mensaje.getFechaCreacion()));
            } else {
                dateText.setText("Fecha no disponible");
            }
            dateText.setTextColor(getResources().getColor(R.color.text_secondary));
            dateText.setTextSize(12);

            authorLayout.addView(authorName);
            authorLayout.addView(dateText);

            // Badge de administrador
            TextView adminBadge = new TextView(requireContext());
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            badgeParams.setMargins(32, 0, 0, 0); // 8dp en píxeles
            adminBadge.setLayoutParams(badgeParams);
            adminBadge.setText("ADMIN");
            adminBadge.setTextColor(getResources().getColor(R.color.green));
            adminBadge.setTextSize(12);
            adminBadge.setTypeface(null, android.graphics.Typeface.BOLD);
            adminBadge.setBackgroundResource(R.drawable.rol_admin_background);
            adminBadge.setPadding(16, 16, 16, 16); // 4dp en píxeles

            headerLayout.addView(avatarView);
            headerLayout.addView(authorLayout);
            headerLayout.addView(adminBadge);

            // Contenido del mensaje
            TextView contentText = new TextView(requireContext());
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            contentParams.setMargins(0, 0, 0, 48); // 12dp en píxeles
            contentText.setLayoutParams(contentParams);
            contentText.setText(mensaje.getContenido());
            contentText.setTextColor(getResources().getColor(R.color.text_primary));
            contentText.setTextSize(16);
            contentText.setTypeface(null, android.graphics.Typeface.BOLD);

            // Agregar elementos al layout
            contentLayout.addView(headerLayout);
            contentLayout.addView(contentText);

            // Agregar contenido al card
            cardView.addView(contentLayout);

            return cardView;

        } catch (Exception e) {
            Log.e("MuroDestacados", "Error al crear vista de mensaje", e);
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMensajesDestacados();
    }
} 