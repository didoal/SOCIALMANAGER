package com.gestionclub.padres.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.car.app.notification.CarAppNotificationBuilder;

import com.gestionclub.padres.R;
import com.gestionclub.padres.ui.MainActivity;

public class AndroidAutoNotificationService {

    private static final String CHANNEL_ID = "social_manager_channel";
    private static final String CHANNEL_NAME = "Social Manager Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones de Gestión Club";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static Notification createEventNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        // Configuración específica para Android Auto
        CarAppNotificationBuilder carAppNotificationBuilder = new CarAppNotificationBuilder(builder);
        carAppNotificationBuilder.setTitle(title)
                .setText(message)
                .setIcon(R.drawable.ic_event);

        return builder.build();
    }

    public static Notification createAttendanceNotification(Context context, String eventTitle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check)
                .setContentTitle("Confirmar Asistencia")
                .setContentText("Evento: " + eventTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        // Configuración específica para Android Auto
        CarAppNotificationBuilder carAppNotificationBuilder = new CarAppNotificationBuilder(builder);
        carAppNotificationBuilder.setTitle("Confirmar Asistencia")
                .setText("Evento: " + eventTitle)
                .setIcon(R.drawable.ic_check);

        return builder.build();
    }

    public static Notification createTeamNotification(Context context, String teamName, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_team)
                .setContentTitle("Equipo: " + teamName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // Configuración específica para Android Auto
        CarAppNotificationBuilder carAppNotificationBuilder = new CarAppNotificationBuilder(builder);
        carAppNotificationBuilder.setTitle("Equipo: " + teamName)
                .setText(message)
                .setIcon(R.drawable.ic_team);

        return builder.build();
    }
} 