package com.gestionclub.padres.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gestionclub.padres.R;

public class CalendarioFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabCrearEvento;
    private CalendarioPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        fabCrearEvento = view.findViewById(R.id.fabCrearEvento);

        pagerAdapter = new CalendarioPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Listado");
                    break;
                case 1:
                    tab.setText("Día");
                    break;
                case 2:
                    tab.setText("Semana");
                    break;
                case 3:
                    tab.setText("Mes");
                    break;
                case 4:
                    tab.setText("Año");
                    break;
            }
        }).attach();

        fabCrearEvento.setOnClickListener(v -> {
            // Aquí puedes abrir el diálogo para crear un evento
        });

        return view;
    }
} 