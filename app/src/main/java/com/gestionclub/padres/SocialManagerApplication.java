package com.gestionclub.padres;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * Clase Application personalizada para manejar la memoria y evitar crashes
 */
public class SocialManagerApplication extends MultiDexApplication {
    
    private static final String TAG = "SocialManagerApp";
    private static SocialManagerApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        try {
            // Configurar manejo de excepciones no capturadas
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    Log.e(TAG, "Excepción no capturada en hilo: " + thread.getName(), throwable);
                    
                    // Aquí podrías enviar el error a un servicio de crash reporting
                    // Crashlytics.logException(throwable);
                    
                    // Terminar la aplicación de forma segura
                    System.exit(1);
                }
            });
            
            // Configurar manejo de memoria
            configureMemoryManagement();
            
            // Configuración adicional para la aplicación
            initializeApplication();
            
            Log.i(TAG, "Aplicación inicializada correctamente");
            
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar la aplicación", e);
        }
    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
    /**
     * Configura el manejo de memoria para evitar crashes
     */
    private void configureMemoryManagement() {
        try {
            // Configurar el recolector de basura
            System.gc();
            
            // Configurar el tamaño del heap
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            
            Log.i(TAG, String.format("Memoria - Máxima: %d MB, Total: %d MB, Libre: %d MB", 
                maxMemory / 1024 / 1024, 
                totalMemory / 1024 / 1024, 
                freeMemory / 1024 / 1024));
            
            // Si la memoria libre es muy baja, hacer garbage collection
            if (freeMemory < maxMemory * 0.1) { // Menos del 10% libre
                Log.w(TAG, "Memoria baja detectada, ejecutando garbage collection");
                System.gc();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar manejo de memoria", e);
        }
    }
    
    /**
     * Obtiene la instancia de la aplicación
     */
    public static SocialManagerApplication getInstance() {
        return instance;
    }
    
    /**
     * Método para limpiar memoria cuando sea necesario
     */
    public void clearMemory() {
        try {
            System.gc();
            Log.i(TAG, "Memoria limpiada manualmente");
        } catch (Exception e) {
            Log.e(TAG, "Error al limpiar memoria", e);
        }
    }
    
    /**
     * Método para verificar el estado de la memoria
     */
    public boolean isMemoryLow() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long freeMemory = runtime.freeMemory();
            
            return freeMemory < maxMemory * 0.15; // Menos del 15% libre
        } catch (Exception e) {
            Log.e(TAG, "Error al verificar memoria", e);
            return false;
        }
    }
    
    /**
     * Método para obtener información de memoria
     */
    public String getMemoryInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            return String.format("Memoria - Usada: %d MB, Libre: %d MB, Total: %d MB, Máxima: %d MB",
                usedMemory / 1024 / 1024,
                freeMemory / 1024 / 1024,
                totalMemory / 1024 / 1024,
                maxMemory / 1024 / 1024);
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener información de memoria", e);
            return "Error al obtener información de memoria";
        }
    }

    private void initializeApplication() {
        // Configuración específica para la aplicación
        // Esto asegura que la aplicación funcione correctamente
        try {
            // Configurar comportamientos específicos de la aplicación
        } catch (Exception e) {
            // Manejar excepciones si es necesario
        }
    }
} 