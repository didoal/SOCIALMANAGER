# 🚀 INSTRUCCIONES RÁPIDAS - IMPORTADOR DE USUARIOS

## ⚡ Uso Rápido (Windows)

1. **Ejecutar el instalador automático:**
   ```
   instalar_y_ejecutar.bat
   ```

2. **O usar Python directamente:**
   ```bash
   pip install pandas openpyxl xlrd
   python importar_usuarios_excel.py
   ```

## 📊 Formato del archivo Excel

Tu archivo Excel debe tener estas columnas:

| Columna | Ejemplo | Obligatorio |
|---------|---------|-------------|
| `nombre` | Juan Pérez | ✅ |
| `email` | juan@club.com | ✅ |
| `password` | padre123 | ✅ |
| `rol` | padre | ✅ |
| `jugador` | Carlos Pérez | ❌ |
| `equipo` | Alevín A | ❌ |

## 🎯 Roles válidos:
- `padre` o `tutor` - Para padres/tutores
- `entrenador` - Para entrenadores  
- `administrador` o `admin` - Para administradores

## 📁 Archivos generados

Después de la importación tendrás:
- `usuarios_importados.json` - Usuarios en formato JSON
- `usuarios_importados.db` - Base de datos SQLite
- `app/src/main/assets/usuarios.json` - Para la app Android
- `app/src/main/assets/database.db` - Base de datos para Android

## 🔑 Credenciales de ejemplo

```
Email: juan@club.com
Contraseña: padre123
Rol: padre
```

## 🚨 Si tienes problemas

1. **Python no encontrado:** Instala Python desde https://www.python.org/downloads/
2. **Dependencias faltantes:** `pip install pandas openpyxl xlrd`
3. **Archivo Excel incorrecto:** Usa el formato de columnas especificado arriba
4. **Errores de validación:** Revisa el resumen de errores generado

## 📞 Soporte

Si necesitas ayuda:
1. Revisa `README_IMPORTACION.md` para instrucciones detalladas
2. Verifica que tu archivo Excel tenga el formato correcto
3. Ejecuta `python test_importacion.py` para probar el sistema

---

**¡Listo! Los usuarios estarán disponibles en la app Android** 📱 