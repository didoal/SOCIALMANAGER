package com.gestionclub.padres.service;

import androidx.car.app.CarAppService;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.model.CarIcon;
import androidx.car.app.model.GridTemplate;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.car.app.model.Action;
import androidx.car.app.model.Header;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.CarLocation;
import androidx.car.app.model.Distance;
import androidx.car.app.model.DistanceUnit;
import androidx.car.app.model.Place;
import androidx.car.app.model.PlaceListMetadata;
import androidx.car.app.model.RoutePreviewNavigationTemplate;
import androidx.car.app.model.Trip;
import androidx.car.app.navigation.model.NavigationTemplate;
import androidx.car.app.navigation.model.PlaceListNavigationTemplate;
import androidx.core.graphics.drawable.IconCompat;

import com.gestionclub.padres.R;
import com.gestionclub.padres.model.Evento;
import com.gestionclub.padres.model.Equipo;
import com.gestionclub.padres.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class SocialManagerCarService extends CarAppService {

    @Override
    public Session onCreateSession() {
        return new SocialManagerSession();
    }

    private static class SocialManagerSession extends Session {

        @Override
        public Template onCreateTemplate(Header header) {
            return new GridTemplate.Builder()
                    .setHeader(header)
                    .setTitle("Gestión Club")
                    .setSingleList(createMainMenu())
                    .build();
        }

        private ItemList createMainMenu() {
            ItemList.Builder builder = new ItemList.Builder();

            // Eventos próximos
            builder.addItem(new Row.Builder()
                    .setTitle("📅 Eventos Próximos")
                    .setSubtitle("Ver próximos eventos del club")
                    .setOnClickListener(() -> {
                        setResult(new EventosScreen(getCarContext()));
                    })
                    .build());

            // Equipos
            builder.addItem(new Row.Builder()
                    .setTitle("⚽ Equipos")
                    .setSubtitle("Gestionar equipos del club")
                    .setOnClickListener(() -> {
                        setResult(new EquiposScreen(getCarContext()));
                    })
                    .build());

            // Notificaciones
            builder.addItem(new Row.Builder()
                    .setTitle("🔔 Notificaciones")
                    .setSubtitle("Ver notificaciones recientes")
                    .setOnClickListener(() -> {
                        setResult(new NotificacionesScreen(getCarContext()));
                    })
                    .build());

            // Asistencias
            builder.addItem(new Row.Builder()
                    .setTitle("✅ Asistencias")
                    .setSubtitle("Confirmar asistencia a eventos")
                    .setOnClickListener(() -> {
                        setResult(new AsistenciasScreen(getCarContext()));
                    })
                    .build());

            return builder.build();
        }
    }

    // Pantalla de Eventos
    private static class EventosScreen extends Screen {
        public EventosScreen(CarContext carContext) {
            super(carContext);
        }

        @Override
        public Template onGetTemplate() {
            ItemList.Builder builder = new ItemList.Builder();

            // Simular eventos (en una implementación real, estos vendrían de la base de datos)
            List<Evento> eventos = getEventosEjemplo();

            for (Evento evento : eventos) {
                builder.addItem(new Row.Builder()
                        .setTitle(evento.getTitulo())
                        .setSubtitle(evento.getFecha() + " - " + evento.getHora())
                        .addText(evento.getDescripcion())
                        .setOnClickListener(() -> {
                            // Mostrar detalles del evento
                            getCarContext().showToast("Evento: " + evento.getTitulo());
                        })
                        .build());
            }

            return new ListTemplate.Builder()
                    .setHeader(new Header.Builder()
                            .setTitle("Eventos Próximos")
                            .setStartHeaderAction(Action.BACK)
                            .build())
                    .setSingleList(builder.build())
                    .build();
        }

        private List<Evento> getEventosEjemplo() {
            List<Evento> eventos = new ArrayList<>();
            
            Evento evento1 = new Evento();
            evento1.setTitulo("Entrenamiento Sub-12");
            evento1.setFecha("15/01/2025");
            evento1.setHora("18:00");
            evento1.setDescripcion("Entrenamiento técnico en el campo principal");
            eventos.add(evento1);

            Evento evento2 = new Evento();
            evento2.setTitulo("Partido vs. Racing Vilariño");
            evento2.setFecha("18/01/2025");
            evento2.setHora("16:30");
            evento2.setDescripcion("Partido de liga en casa");
            eventos.add(evento2);

            return eventos;
        }
    }

    // Pantalla de Equipos
    private static class EquiposScreen extends Screen {
        public EquiposScreen(CarContext carContext) {
            super(carContext);
        }

        @Override
        public Template onGetTemplate() {
            ItemList.Builder builder = new ItemList.Builder();

            // Simular equipos
            List<Equipo> equipos = getEquiposEjemplo();

            for (Equipo equipo : equipos) {
                builder.addItem(new Row.Builder()
                        .setTitle(equipo.getNombre())
                        .setSubtitle("Categoría: " + equipo.getCategoria())
                        .addText("Entrenador: " + equipo.getEntrenador())
                        .setOnClickListener(() -> {
                            getCarContext().showToast("Equipo: " + equipo.getNombre());
                        })
                        .build());
            }

            return new ListTemplate.Builder()
                    .setHeader(new Header.Builder()
                            .setTitle("Equipos del Club")
                            .setStartHeaderAction(Action.BACK)
                            .build())
                    .setSingleList(builder.build())
                    .build();
        }

        private List<Equipo> getEquiposEjemplo() {
            List<Equipo> equipos = new ArrayList<>();
            
            Equipo equipo1 = new Equipo();
            equipo1.setNombre("Sub-12");
            equipo1.setCategoria("Infantil");
            equipo1.setEntrenador("José Antonio Suárez");
            equipos.add(equipo1);

            Equipo equipo2 = new Equipo();
            equipo2.setNombre("Sub-16");
            equipo2.setCategoria("Cadete");
            equipo2.setEntrenador("Carlos Rodríguez");
            equipos.add(equipo2);

            return equipos;
        }
    }

    // Pantalla de Notificaciones
    private static class NotificacionesScreen extends Screen {
        public NotificacionesScreen(CarContext carContext) {
            super(carContext);
        }

        @Override
        public Template onGetTemplate() {
            ItemList.Builder builder = new ItemList.Builder();

            // Simular notificaciones
            builder.addItem(new Row.Builder()
                    .setTitle("Nuevo evento creado")
                    .setSubtitle("Entrenamiento Sub-12 - 15/01/2025")
                    .addText("Se ha creado un nuevo entrenamiento para el equipo Sub-12")
                    .setOnClickListener(() -> {
                        getCarContext().showToast("Notificación leída");
                    })
                    .build());

            builder.addItem(new Row.Builder()
                    .setTitle("Recordatorio de partido")
                    .setSubtitle("Partido vs. Racing Vilariño - 18/01/2025")
                    .addText("No olvides confirmar tu asistencia al partido")
                    .setOnClickListener(() -> {
                        getCarContext().showToast("Notificación leída");
                    })
                    .build());

            return new ListTemplate.Builder()
                    .setHeader(new Header.Builder()
                            .setTitle("Notificaciones")
                            .setStartHeaderAction(Action.BACK)
                            .build())
                    .setSingleList(builder.build())
                    .build();
        }
    }

    // Pantalla de Asistencias
    private static class AsistenciasScreen extends Screen {
        public AsistenciasScreen(CarContext carContext) {
            super(carContext);
        }

        @Override
        public Template onGetTemplate() {
            ItemList.Builder builder = new ItemList.Builder();

            // Simular eventos pendientes de confirmación
            builder.addItem(new Row.Builder()
                    .setTitle("Entrenamiento Sub-12")
                    .setSubtitle("15/01/2025 - 18:00")
                    .addText("Estado: Pendiente de confirmación")
                    .setOnClickListener(() -> {
                        getCarContext().showToast("Confirmar asistencia al entrenamiento");
                    })
                    .build());

            builder.addItem(new Row.Builder()
                    .setTitle("Partido vs. Racing Vilariño")
                    .setSubtitle("18/01/2025 - 16:30")
                    .addText("Estado: Pendiente de confirmación")
                    .setOnClickListener(() -> {
                        getCarContext().showToast("Confirmar asistencia al partido");
                    })
                    .build());

            return new ListTemplate.Builder()
                    .setHeader(new Header.Builder()
                            .setTitle("Asistencias Pendientes")
                            .setStartHeaderAction(Action.BACK)
                            .build())
                    .setSingleList(builder.build())
                    .build();
        }
    }
} 