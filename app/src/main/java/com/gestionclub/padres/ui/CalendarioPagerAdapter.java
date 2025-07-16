package com.gestionclub.padres.ui;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CalendarioPagerAdapter extends FragmentStateAdapter {
    private static final String TAG = "CalendarioPagerAdapter";

    public CalendarioPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            switch (position) {
                case 0:
                    return new CalendarioListadoFragment();
                case 1:
                    return new CalendarioDiaFragment();
                case 2:
                    return new CalendarioSemanaFragment();
                case 3:
                    return new CalendarioMesFragment();
                case 4:
                    return new CalendarioAnoFragment();
                default:
                    Log.w(TAG, "Posición no válida: " + position + ", retornando CalendarioListadoFragment");
                    return new CalendarioListadoFragment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al crear fragment para posición: " + position, e);
            // En caso de error, retornar un fragment simple
            return new CalendarioListadoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
} 