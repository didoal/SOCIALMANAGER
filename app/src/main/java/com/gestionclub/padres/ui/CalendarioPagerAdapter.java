package com.gestionclub.padres.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CalendarioPagerAdapter extends FragmentStateAdapter {

    public CalendarioPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
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
                return new CalendarioListadoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
} 