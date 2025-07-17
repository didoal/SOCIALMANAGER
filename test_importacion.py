#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de prueba para el importador de usuarios
Autor: Asistente IA
Fecha: 2025
"""

import pandas as pd
import os
from datetime import datetime

def crear_archivo_prueba():
    """Crea un archivo Excel de prueba con usuarios de ejemplo"""
    
    # Datos de prueba
    datos_prueba = {
        'nombre': [
            'Juan Pérez',
            'María García', 
            'Luis Martínez',
            'Carmen López',
            'Diego Admin',
            'Ana Entrenadora',
            'Pedro Tutor',
            'Sofia Madre'
        ],
        'email': [
            'juan@club.com',
            'maria@club.com',
            'luis@club.com', 
            'carmen@club.com',
            'diego@club.com',
            'ana@club.com',
            'pedro@club.com',
            'sofia@club.com'
        ],
        'password': [
            'padre123',
            'padre123',
            'padre123',
            'padre123', 
            'admin123',
            'entrenador123',
            'padre123',
            'padre123'
        ],
        'rol': [
            'padre',
            'padre',
            'padre',
            'padre',
            'administrador',
            'entrenador',
            'tutor',
            'padre'
        ],
        'jugador': [
            'Carlos Pérez',
            'Ana García',
            'Pedro Martínez',
            'Miguel López',
            None,
            None,
            'Lucas Tutor',
            'Emma Madre'
        ],
        'equipo': [
            'Alevín A',
            'Infantil B',
            'Cadete A',
            'Alevín A',
            None,
            'Todos',
            'Infantil A',
            'Cadete B'
        ]
    }
    
    # Crear DataFrame
    df = pd.DataFrame(datos_prueba)
    
    # Guardar como Excel
    archivo_prueba = "usuarios_prueba.xlsx"
    
    with pd.ExcelWriter(archivo_prueba, engine='openpyxl') as writer:
        # Hoja principal con usuarios
        df.to_excel(writer, sheet_name='Usuarios', index=False)
        
        # Hoja de instrucciones
        instrucciones = pd.DataFrame({
            'Campo': ['nombre', 'email', 'password', 'rol', 'jugador', 'equipo'],
            'Descripción': [
                'Nombre completo del padre/tutor',
                'Email único del usuario',
                'Contraseña para el login',
                'Rol: padre, entrenador, administrador',
                'Nombre del jugador asociado (opcional)',
                'Equipo del jugador (opcional)'
            ],
            'Obligatorio': ['Sí', 'Sí', 'Sí', 'Sí', 'No', 'No'],
            'Ejemplo': [
                'Juan Pérez',
                'juan@club.com',
                'padre123',
                'padre',
                'Carlos Pérez',
                'Alevín A'
            ]
        })
        instrucciones.to_excel(writer, sheet_name='Instrucciones', index=False)
        
        # Hoja de estadísticas
        stats = pd.DataFrame({
            'Estadística': [
                'Total de usuarios',
                'Padres/Tutores',
                'Entrenadores',
                'Administradores',
                'Con jugador asociado',
                'Sin jugador asociado'
            ],
            'Cantidad': [
                len(df),
                len(df[df['rol'].isin(['padre', 'tutor'])]),
                len(df[df['rol'] == 'entrenador']),
                len(df[df['rol'] == 'administrador']),
                len(df[df['jugador'].notna()]),
                len(df[df['jugador'].isna()])
            ]
        })
        stats.to_excel(writer, sheet_name='Estadísticas', index=False)
    
    print(f"✅ Archivo de prueba creado: {archivo_prueba}")
    print(f"📊 Contiene {len(df)} usuarios de ejemplo")
    
    return archivo_prueba

def ejecutar_prueba_completa():
    """Ejecuta una prueba completa del sistema de importación"""
    
    print("🧪 INICIANDO PRUEBA COMPLETA DEL SISTEMA")
    print("="*50)
    
    # Paso 1: Crear archivo de prueba
    print("1️⃣ Creando archivo Excel de prueba...")
    archivo_prueba = crear_archivo_prueba()
    
    # Paso 2: Importar usuarios
    print("\n2️⃣ Importando usuarios desde Excel...")
    try:
        from importar_usuarios_excel import ImportadorUsuarios
        
        importador = ImportadorUsuarios(archivo_prueba)
        exito_importacion = importador.importar()
        
        if not exito_importacion:
            print("❌ La importación falló")
            return False
            
    except ImportError as e:
        print(f"❌ Error al importar: {e}")
        print("💡 Asegúrate de tener instaladas las dependencias:")
        print("   pip install pandas openpyxl")
        return False
    
    # Paso 3: Integrar con Android
    print("\n3️⃣ Integrando con aplicación Android...")
    try:
        from integrar_usuarios_android import IntegradorAndroid
        
        integrador = IntegradorAndroid()
        exito_integracion = integrador.integrar()
        
        if not exito_integracion:
            print("❌ La integración falló")
            return False
            
    except ImportError as e:
        print(f"❌ Error al integrar: {e}")
        return False
    
    # Paso 4: Verificar archivos generados
    print("\n4️⃣ Verificando archivos generados...")
    archivos_esperados = [
        "usuarios_importados.json",
        "usuarios_importados.db",
        "app/src/main/assets/usuarios.json",
        "app/src/main/assets/database.db",
        "app/src/main/assets/config.json",
        "instalar_usuarios.sh"
    ]
    
    archivos_encontrados = []
    for archivo in archivos_esperados:
        if os.path.exists(archivo):
            archivos_encontrados.append(archivo)
            print(f"   ✅ {archivo}")
        else:
            print(f"   ❌ {archivo} (no encontrado)")
    
    # Paso 5: Resumen final
    print("\n" + "="*50)
    print("🎉 PRUEBA COMPLETADA")
    print("="*50)
    
    print(f"✅ Archivos generados: {len(archivos_encontrados)}/{len(archivos_esperados)}")
    print(f"📁 Archivo de prueba: {archivo_prueba}")
    print(f"📱 Integración Android: {'✅ Completada' if exito_integracion else '❌ Falló'}")
    
    if len(archivos_encontrados) >= 4:
        print("\n🎯 ¡Sistema funcionando correctamente!")
        print("📱 Los usuarios están listos para usar en la aplicación Android")
        return True
    else:
        print("\n⚠️  Algunos archivos no se generaron correctamente")
        return False

def limpiar_archivos_prueba():
    """Limpia los archivos generados durante las pruebas"""
    
    archivos_a_limpiar = [
        "usuarios_prueba.xlsx",
        "usuarios_importados.json",
        "usuarios_importados.db",
        "plantilla_usuarios.xlsx"
    ]
    
    directorios_a_limpiar = [
        "app/src/main/assets"
    ]
    
    print("🧹 Limpiando archivos de prueba...")
    
    for archivo in archivos_a_limpiar:
        if os.path.exists(archivo):
            os.remove(archivo)
            print(f"   🗑️  Eliminado: {archivo}")
    
    for directorio in directorios_a_limpiar:
        if os.path.exists(directorio):
            shutil.rmtree(directorio)
            print(f"   🗑️  Eliminado directorio: {directorio}")
    
    print("✅ Limpieza completada")

def main():
    """Función principal"""
    print("🧪 SISTEMA DE PRUEBAS - IMPORTADOR DE USUARIOS")
    print("="*60)
    
    import sys
    
    if len(sys.argv) > 1:
        comando = sys.argv[1].lower()
        
        if comando == "crear":
            crear_archivo_prueba()
        elif comando == "probar":
            ejecutar_prueba_completa()
        elif comando == "limpiar":
            limpiar_archivos_prueba()
        else:
            print("❌ Comando no reconocido")
            print("Comandos disponibles: crear, probar, limpiar")
    else:
        # Ejecutar prueba completa por defecto
        print("🚀 Ejecutando prueba completa del sistema...")
        exito = ejecutar_prueba_completa()
        
        if exito:
            print("\n🎉 ¡Todo funcionando perfectamente!")
            print("📱 El sistema está listo para usar")
        else:
            print("\n❌ Algunos problemas detectados")
            print("🔧 Revisa los errores y vuelve a intentar")

if __name__ == "__main__":
    main() 