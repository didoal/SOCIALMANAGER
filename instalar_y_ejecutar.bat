@echo off
chcp 65001 >nul
echo 🚀 INSTALADOR Y EJECUTOR - IMPORTADOR DE USUARIOS
echo ================================================

echo.
echo 📋 Verificando Python...

python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python no está instalado o no está en el PATH
    echo.
    echo 💡 Instalando Python...
    echo 📥 Descargando Python desde https://www.python.org/downloads/
    echo ⚠️  Por favor, instala Python manualmente desde:
    echo    https://www.python.org/downloads/
    echo.
    echo ✅ Después de instalar Python, ejecuta este script nuevamente
    pause
    exit /b 1
)

echo ✅ Python encontrado
python --version

echo.
echo 📦 Instalando dependencias...
pip install pandas openpyxl xlrd

if %errorlevel% neq 0 (
    echo ❌ Error al instalar dependencias
    pause
    exit /b 1
)

echo ✅ Dependencias instaladas

echo.
echo 🎯 ¿Qué quieres hacer?
echo 1. Crear archivo Excel de prueba
echo 2. Importar usuarios desde Excel existente
echo 3. Ejecutar prueba completa del sistema
echo 4. Salir

set /p opcion="Selecciona una opción (1-4): "

if "%opcion%"=="1" (
    echo.
    echo 📝 Creando archivo Excel de prueba...
    python test_importacion.py crear
    echo.
    echo ✅ Archivo de prueba creado: usuarios_prueba.xlsx
    echo 📊 Puedes editarlo y luego ejecutar la opción 2
)

if "%opcion%"=="2" (
    echo.
    echo 📁 Buscando archivos Excel...
    dir *.xlsx 2>nul
    if %errorlevel% neq 0 (
        echo ❌ No se encontraron archivos Excel
        echo 💡 Primero crea un archivo Excel o ejecuta la opción 1
    ) else (
        set /p archivo="Introduce el nombre del archivo Excel: "
        echo.
        echo 🚀 Importando usuarios desde %archivo%...
        python importar_usuarios_excel.py %archivo%
        echo.
        echo 🔗 Integrando con aplicación Android...
        python integrar_usuarios_android.py
    )
)

if "%opcion%"=="3" (
    echo.
    echo 🧪 Ejecutando prueba completa del sistema...
    python test_importacion.py probar
)

if "%opcion%"=="4" (
    echo 👋 ¡Hasta luego!
    exit /b 0
)

echo.
echo 🎉 Proceso completado
echo 📱 Los usuarios están listos para usar en la aplicación Android
pause 