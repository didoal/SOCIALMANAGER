package com.gestionclub.admin.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.*;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Confirmacion;
import com.gestionclub.padres.model.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.AxisBase;

public class EstadisticasFragment extends Fragment {
    private LinearLayout contenedorEstadisticas;
    private List<Evento> eventos;
    private List<Confirmacion> confirmaciones;
    private List<Usuario> jugadores;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        contenedorEstadisticas = v.findViewById(R.id.contenedorEstadisticas);

        cargarDatos();
        mostrarEstadisticas();

        return v;
    }

    private void cargarDatos() {
        eventos = cargarLista("eventos", new TypeToken<ArrayList<Evento>>(){}.getType());
        confirmaciones = cargarLista("confirmaciones", new TypeToken<ArrayList<Confirmacion>>(){}.getType());
        jugadores = cargarLista("usuarios", new TypeToken<ArrayList<Usuario>>(){}.getType());
    }

    private <T> List<T> cargarLista(String key, Type type) {
        String json = requireContext().getSharedPreferences(key, 0).getString(key, "[]");
        return new Gson().fromJson(json, type);
    }

    private void mostrarEstadisticas() {
        Map<String, List<Usuario>> jugadoresPorCategoria = new HashMap<>();
        for (Usuario jugador : jugadores) {
            if (!jugador.getRol().equalsIgnoreCase("jugador")) continue;
            String cat = jugador.getCategoria() == null ? "Sin categoría" : jugador.getCategoria();
            if (!jugadoresPorCategoria.containsKey(cat)) jugadoresPorCategoria.put(cat, new ArrayList<>());
            jugadoresPorCategoria.get(cat).add(jugador);
        }

        for (String categoria : jugadoresPorCategoria.keySet()) {
            TextView tvCat = new TextView(getContext());
            tvCat.setText("Categoría: " + categoria);
            tvCat.setTextColor(getResources().getColor(R.color.gold));
            tvCat.setTextSize(20);
            tvCat.setPadding(0, 24, 0, 8);
            contenedorEstadisticas.addView(tvCat);

            List<Usuario> jugadoresCat = jugadoresPorCategoria.get(categoria);

            for (String tipo : Arrays.asList("amistoso", "partido", "entrenamiento")) {
                TextView tvTipo = new TextView(getContext());
                tvTipo.setText("Tipo de evento: " + tipo.substring(0,1).toUpperCase() + tipo.substring(1));
                tvTipo.setTextColor(getResources().getColor(R.color.white));
                tvTipo.setTextSize(16);
                contenedorEstadisticas.addView(tvTipo);

                // Gráfico de barras (porcentaje de asistencia por jugador)
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> nombres = new ArrayList<>();
                int idx = 0;
                for (Usuario jugador : jugadoresCat) {
                    int total = 0, presentes = 0;
                    for (Evento evento : eventos) {
                        if (!evento.getTipo().equalsIgnoreCase(tipo)) continue;
                        total++;
                        Confirmacion conf = buscarConfirmacion(jugador.getUsername(), evento.getId());
                        if (conf != null && conf.isPresente()) presentes++;
                    }
                    float porcentaje = (total > 0) ? (presentes * 100f / total) : 0f;
                    entries.add(new BarEntry(idx, porcentaje));
                    nombres.add(jugador.getNombreReal());
                    idx++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Asistencia %");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData = new BarData(dataSet);
                barData.setValueTextColor(android.graphics.Color.BLACK);
                barData.setValueTextSize(10f);

                BarChart chart = new BarChart(getContext());
                chart.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 400));
                chart.setData(barData);

                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        int i = (int) value;
                        return i >= 0 && i < nombres.size() ? nombres.get(i) : "";
                    }
                });
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);

                chart.getAxisRight().setEnabled(false);
                chart.getDescription().setEnabled(false);
                chart.getLegend().setEnabled(false);
                chart.setFitBars(true);
                chart.invalidate();

                contenedorEstadisticas.addView(chart);

                // PieChart global asistencia/ausencia para la categoría y tipo
                int totalEventos = 0, totalPresentes = 0;
                for (Usuario jugador : jugadoresCat) {
                    for (Evento evento : eventos) {
                        if (!evento.getTipo().equalsIgnoreCase(tipo)) continue;
                        totalEventos++;
                        Confirmacion conf = buscarConfirmacion(jugador.getUsername(), evento.getId());
                        if (conf != null && conf.isPresente()) totalPresentes++;
                    }
                }
                int totalAusentes = totalEventos - totalPresentes;

                PieChart pieChart = new PieChart(getContext());
                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                pieEntries.add(new PieEntry(totalPresentes, "Asistencias"));
                pieEntries.add(new PieEntry(totalAusentes, "Ausencias"));

                PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData(pieDataSet);
                pieData.setValueFormatter(new PercentFormatter(pieChart));
                pieData.setValueTextSize(13f);
                pieChart.setData(pieData);
                pieChart.setUsePercentValues(true);
                pieChart.getDescription().setEnabled(false);
                pieChart.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 350));
                pieChart.setDrawEntryLabels(true);
                pieChart.getLegend().setEnabled(true);
                pieChart.invalidate();

                contenedorEstadisticas.addView(pieChart);
            }
        }
    }

    private Confirmacion buscarConfirmacion(String usuarioId, String eventoId) {
        for (Confirmacion c : confirmaciones) {
            if (c.getUsuarioId().equals(usuarioId) && c.getEventoId().equals(eventoId)) {
                return c;
            }
        }
        return null;
    }
}