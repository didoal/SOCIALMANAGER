package com.gestionclub.padres.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * Servicio para manejar la memoria y evitar crashes
 */
public class MemoryManagementService extends Service {
    
    private static final String TAG = "MemoryService";
    private boolean isRunning = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Servicio de memoria creado");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            startMemoryMonitoring();
            Log.i(TAG, "Servicio de memoria iniciado");
        }
        return START_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.i(TAG, "Servicio de memoria destruido");
    }
    
    /**
     * Inicia el monitoreo de memoria
     */
    private void startMemoryMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        // Verificar memoria cada 30 segundos
                        Thread.sleep(30000);
                        checkMemoryStatus();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Error en monitoreo de memoria", e);
                        break;
                    } catch (Exception e) {
                        Log.e(TAG, "Error inesperado en monitoreo de memoria", e);
                    }
                }
            }
        }).start();
    }
    
    /**
     * Verifica el estado de la memoria
     */
    private void checkMemoryStatus() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            // Calcular porcentaje de memoria usada
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            Log.d(TAG, String.format("Estado de memoria: %.1f%% usada", memoryUsagePercent));
            
            // Si la memoria está muy alta, hacer garbage collection
            if (memoryUsagePercent > 80) {
                Log.w(TAG, "Memoria alta detectada (" + String.format("%.1f%%", memoryUsagePercent) + "), ejecutando garbage collection");
                System.gc();
                
                // Esperar un poco y verificar de nuevo
                Thread.sleep(1000);
                runtime = Runtime.getRuntime();
                freeMemory = runtime.freeMemory();
                totalMemory = runtime.totalMemory();
                usedMemory = totalMemory - freeMemory;
                memoryUsagePercent = (double) usedMemory / maxMemory * 100;
                
                Log.i(TAG, "Después de GC: " + String.format("%.1f%%", memoryUsagePercent) + " usada");
            }
            
            // Si la memoria sigue muy alta después del GC, notificar
            if (memoryUsagePercent > 90) {
                Log.e(TAG, "¡ADVERTENCIA! Memoria crítica: " + String.format("%.1f%%", memoryUsagePercent) + " usada");
                // Aquí podrías enviar una notificación al usuario o tomar otras medidas
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar estado de memoria", e);
        }
    }
    
    /**
     * Método estático para iniciar el servicio
     */
    public static void startService(android.content.Context context) {
        try {
            Intent intent = new Intent(context, MemoryManagementService.class);
            context.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar servicio de memoria", e);
        }
    }
    
    /**
     * Método estático para detener el servicio
     */
    public static void stopService(android.content.Context context) {
        try {
            Intent intent = new Intent(context, MemoryManagementService.class);
            context.stopService(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al detener servicio de memoria", e);
        }
    }
} 