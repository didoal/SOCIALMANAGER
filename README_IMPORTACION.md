# 📊 Importador de Usuarios desde Excel - SocialManager

Este script permite importar usuarios desde un archivo Excel directamente a la aplicación SocialManager, creando logins rápidos y eficientes.

## 🚀 Características

- ✅ **Importación automática** desde archivos Excel (.xlsx)
- ✅ **Validación completa** de datos (emails, roles, contraseñas)
- ✅ **Detección de duplicados** automática
- ✅ **Múltiples formatos de salida** (JSON, SQLite)
- ✅ **Resumen detallado** de la importación
- ✅ **Plantilla Excel** incluida

## 📋 Requisitos

### Software necesario:
- Python 3.7 o superior
- pip (gestor de paquetes de Python)

### Instalación de dependencias:
```bash
pip install -r requirements.txt
```

## 📁 Estructura del archivo Excel

El archivo Excel debe tener las siguientes columnas:

| Columna | Obligatorio | Descripción | Ejemplo |
|---------|-------------|-------------|---------|
| `nombre` | ✅ | Nombre completo del padre/tutor | Juan Pérez |
| `email` | ✅ | Email único del usuario | juan@club.com |
| `password` | ✅ | Contraseña para el login | padre123 |
| `rol` | ✅ | Rol del usuario | padre, entrenador, administrador |
| `jugador` | ❌ | Nombre del jugador asociado | Carlos Pérez |
| `equipo` | ❌ | Equipo del jugador | Alevín A |

### Roles válidos:
- `padre` o `tutor` - Para padres/tutores
- `entrenador` - Para entrenadores
- `administrador` o `admin` - Para administradores

## 🎯 Uso del script

### Opción 1: Ejecutar con archivo específico
```bash
python importar_usuarios_excel.py mi_archivo.xlsx
```

### Opción 2: Ejecutar y seleccionar archivo
```bash
python importar_usuarios_excel.py
```
El script buscará automáticamente archivos Excel en el directorio actual.

### Opción 3: Crear plantilla
Si no tienes un archivo Excel, el script puede crear una plantilla:
```bash
python importar_usuarios_excel.py
# Selecciona 's' cuando te pregunte si quieres crear una plantilla
```

## 📊 Ejemplo de archivo Excel

| nombre | email | password | rol | jugador | equipo |
|--------|-------|----------|-----|---------|--------|
| Juan Pérez | juan@club.com | padre123 | padre | Carlos Pérez | Alevín A |
| María García | maria@club.com | padre123 | padre | Ana García | Infantil B |
| Luis Martínez | luis@club.com | padre123 | padre | Pedro Martínez | Cadete A |
| Diego Admin | diego@club.com | admin123 | administrador | - | - |

## 🔧 Configuración avanzada

### Personalizar la base de datos
```python
importador = ImportadorUsuarios("mi_archivo.xlsx", "ruta/personalizada/database.db")
```

### Validar sin importar
```python
importador = ImportadorUsuarios("mi_archivo.xlsx")
df = importador.leer_excel()
datos_validos = importador.validar_datos(df)
print(f"Usuarios válidos: {len(datos_validos)}")
```

## 📤 Archivos de salida

### 1. Archivo JSON (`usuarios_importados.json`)
Contiene todos los usuarios en formato JSON compatible con la app:
```json
[
  {
    "id": "uuid-generado",
    "nombre": "Juan Pérez",
    "email": "juan@club.com",
    "password": "padre123",
    "rol": "padre",
    "jugador": "Carlos Pérez",
    "fechaRegistro": "2025-01-16T10:30:00",
    "activo": true,
    "equipo": "Alevín A",
    "equipoId": null
  }
]
```

### 2. Base de datos SQLite (`usuarios_importados.db`)
Base de datos SQLite con tabla `usuarios` lista para usar.

## 🔍 Validaciones realizadas

### Validaciones obligatorias:
- ✅ Nombre no vacío
- ✅ Email válido y único
- ✅ Contraseña no vacía
- ✅ Rol válido

### Validaciones opcionales:
- ✅ Formato de email
- ✅ Roles permitidos
- ✅ Detección de duplicados

## 📊 Resumen de importación

El script genera un resumen completo que incluye:

```
==================================================
📊 RESUMEN DE IMPORTACIÓN
==================================================
✅ Usuarios importados exitosamente: 15
❌ Errores encontrados: 2
⚠️  Duplicados encontrados: 1

📋 DETALLE DE ERRORES:
   Fila 5 (usuario@invalido): Email inválido
   Fila 8 (): Nombre vacío

📋 USUARIOS DUPLICADOS:
   - juan@club.com (Juan Pérez)

✅ USUARIOS IMPORTADOS:
   - maria@club.com (María García) - Rol: padre
   - luis@club.com (Luis Martínez) - Rol: padre
```

## 🚨 Solución de problemas

### Error: "No module named 'pandas'"
```bash
pip install pandas openpyxl
```

### Error: "Columnas faltantes en el Excel"
Verifica que tu archivo Excel tenga las columnas: `nombre`, `email`, `password`, `rol`

### Error: "Email inválido"
Asegúrate de que los emails contengan el símbolo `@` y un dominio válido.

### Error: "Rol inválido"
Usa solo estos roles: `padre`, `tutor`, `entrenador`, `administrador`, `admin`

## 🔐 Seguridad

⚠️ **Importante**: Las contraseñas se almacenan en texto plano en este script de desarrollo. Para producción, considera:

1. **Hashear contraseñas** antes de almacenarlas
2. **Usar HTTPS** para transmisión de datos
3. **Implementar autenticación de dos factores**
4. **Auditar regularmente** los accesos

## 📞 Soporte

Si tienes problemas con el script:

1. Verifica que Python 3.7+ esté instalado
2. Instala las dependencias: `pip install -r requirements.txt`
3. Verifica el formato de tu archivo Excel
4. Revisa el resumen de errores generado

## 🎉 ¡Listo!

Una vez completada la importación, los usuarios estarán disponibles en la aplicación SocialManager y podrán iniciar sesión con sus credenciales del Excel.

---

**Desarrollado para CD Santiaguiño de Guizán** ⚽ 