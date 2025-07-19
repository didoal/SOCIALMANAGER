package com.gestionclub.padres.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.gestionclub.padres.data.DataManager;
import com.gestionclub.padres.model.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExcelImporter {
    
    private static final String TAG = "ExcelImporter";
    
    public static class ImportResult {
        public int totalRows;
        public int successCount;
        public int errorCount;
        public List<String> errors;
        
        public ImportResult() {
            this.errors = new ArrayList<>();
        }
    }
    
    public static ImportResult importUsersFromExcel(Context context, Uri fileUri) {
        ImportResult result = new ImportResult();
        
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                result.errors.add("No se pudo abrir el archivo");
                return result;
            }
            
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Primera hoja
            
            DataManager dataManager = new DataManager(context);
            
            // Procesar cada fila (empezando desde la fila 1 para saltar el encabezado)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                result.totalRows++;
                
                try {
                    Usuario usuario = parseUserFromRow(row, dataManager);
                    if (usuario != null) {
                        dataManager.agregarUsuario(usuario);
                        result.successCount++;
                    } else {
                        result.errorCount++;
                        result.errors.add("Fila " + (i + 1) + ": Datos incompletos o inválidos");
                    }
                } catch (Exception e) {
                    result.errorCount++;
                    result.errors.add("Fila " + (i + 1) + ": " + e.getMessage());
                    Log.e(TAG, "Error procesando fila " + (i + 1), e);
                }
            }
            
            workbook.close();
            inputStream.close();
            
        } catch (Exception e) {
            Log.e(TAG, "Error importando archivo Excel", e);
            result.errors.add("Error al procesar el archivo: " + e.getMessage());
        }
        
        return result;
    }
    
    private static Usuario parseUserFromRow(Row row, DataManager dataManager) throws Exception {
        try {
            // Obtener valores de las celdas
            String username = getCellValueAsString(row.getCell(0)); // Columna A: Usuario
            String password = getCellValueAsString(row.getCell(1)); // Columna B: Contraseña
            String rol = getCellValueAsString(row.getCell(2));      // Columna C: Rol
            String nombreJugador = getCellValueAsString(row.getCell(3)); // Columna D: Nombre Jugador
            
            // Validaciones básicas
            if (username == null || username.trim().isEmpty()) {
                throw new Exception("Nombre de usuario es obligatorio");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new Exception("Contraseña es obligatoria");
            }
            if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
                throw new Exception("Nombre del jugador es obligatorio");
            }
            
            // Verificar si el usuario ya existe
            if (dataManager.existeUsuario(username)) {
                throw new Exception("El usuario '" + username + "' ya existe");
            }
            
            // Crear el usuario
            Usuario usuario = new Usuario(nombreJugador, null, password, rol);
            usuario.setId(UUID.randomUUID().toString());
            usuario.setEmail(username + "@club.com"); // Email temporal basado en username
            
            return usuario;
            
        } catch (Exception e) {
            Log.e(TAG, "Error parseando fila", e);
            throw e;
        }
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    // Método para crear un archivo de ejemplo
    public static String getExampleFormat() {
        return "Formato del archivo Excel:\n\n" +
               "Columna A: Nombre de Usuario (obligatorio)\n" +
               "Columna B: Contraseña (obligatorio)\n" +
               "Columna C: Rol (Padre/Tutor o Administrador)\n" +
               "Columna D: Nombre del Jugador (obligatorio)\n\n" +
               "Ejemplo:\n" +
               "usuario1 | password123 | Padre/Tutor | Juan Pérez\n" +
               "admin1   | admin123    | Administrador | Administrador";
    }
} 