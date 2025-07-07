package com.gestionclub.padres.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.gestionclub.padres.R;
import java.util.ArrayList;
import java.util.List;

public class TutorialFragment extends Fragment {
    private ViewPager2 viewPager;
    private Button buttonSiguiente;
    private Button buttonOmitir;
    private TutorialPagerAdapter adapter;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        prefs = requireContext().getSharedPreferences("config", 0);
        viewPager = view.findViewById(R.id.viewPagerTutorial);
        buttonSiguiente = view.findViewById(R.id.buttonSiguiente);
        buttonOmitir = view.findViewById(R.id.buttonOmitir);
        adapter = new TutorialPagerAdapter(getTutorials());
        viewPager.setAdapter(adapter);

        buttonSiguiente.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finalizarTutorial();
            }
        });
        buttonOmitir.setOnClickListener(v -> finalizarTutorial());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == adapter.getItemCount() - 1) {
                    buttonSiguiente.setText("Finalizar");
                } else {
                    buttonSiguiente.setText("Siguiente");
                }
            }
        });

        return view;
    }

    private List<TutorialItem> getTutorials() {
        List<TutorialItem> list = new ArrayList<>();
        list.add(new TutorialItem("Bienvenido", "¡Bienvenido a la app del club! Usa el menú lateral para navegar por todas las secciones."));
        list.add(new TutorialItem("Perfil", "Gestiona tu información personal y preferencias desde la sección de Perfil."));
        list.add(new TutorialItem("Mensajes", "Comunícate con otros miembros del club en la sección de Mensajes y revisa el muro de destacados."));
        list.add(new TutorialItem("Notificaciones", "Recibe avisos importantes, recordatorios y novedades en la sección de Notificaciones."));
        list.add(new TutorialItem("Configuración", "Personaliza la app: idioma, tema, notificaciones y más desde Configuración."));
        return list;
    }

    private void finalizarTutorial() {
        prefs.edit().putBoolean("tutorial_visto", true).apply();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onBackPressed();
        }
    }

    // Clases internas para el tutorial
    public static class TutorialItem {
        public String titulo;
        public String descripcion;
        public TutorialItem(String t, String d) { titulo = t; descripcion = d; }
    }

    public class TutorialPagerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<TutorialPagerAdapter.TutorialViewHolder> {
        private List<TutorialItem> items;
        public TutorialPagerAdapter(List<TutorialItem> items) { this.items = items; }
        @NonNull
        @Override
        public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutorial, parent, false);
            return new TutorialViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
            holder.textTitulo.setText(items.get(position).titulo);
            holder.textDescripcion.setText(items.get(position).descripcion);
        }
        @Override
        public int getItemCount() { return items.size(); }
        class TutorialViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView textTitulo, textDescripcion;
            TutorialViewHolder(View v) {
                super(v);
                textTitulo = v.findViewById(R.id.textTituloTutorial);
                textDescripcion = v.findViewById(R.id.textDescripcionTutorial);
            }
        }
    }
} 