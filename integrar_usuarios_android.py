#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para integrar usuarios importados con la aplicación Android SocialManager
Autor: Asistente IA
Fecha: 2025
"""

import json
import os
import shutil
import sqlite3
from pathlib import Path

class IntegradorAndroid:
    def __init__(self, archivo_json="usuarios_importados.json"):
        """
        Inicializa el integrador para Android
        
        Args:
            archivo_json (str): Ruta al archivo JSON con usuarios importados
        """
        self.archivo_json = archivo_json
        self.ruta_app = "app/src/main/assets"
        self.usuarios = []
        
    def cargar_usuarios(self):
        """Carga los usuarios desde el archivo JSON"""
        try:
            if not os.path.exists(self.archivo_json):
                print(f"❌ No se encontró el archivo: {self.archivo_json}")
                print("💡 Ejecuta primero el script de importación: python importar_usuarios_excel.py")
                return False
                
            with open(self.archivo_json, 'r', encoding='utf-8') as f:
                self.usuarios = json.load(f)
                
            print(f"✅ Cargados {len(self.usuarios)} usuarios desde {self.archivo_json}")
            return True
            
        except Exception as e:
            print(f"❌ Error al cargar usuarios: {str(e)}")
            return False
    
    def crear_directorio_assets(self):
        """Crea el directorio de assets si no existe"""
        try:
            os.makedirs(self.ruta_app, exist_ok=True)
            print(f"✅ Directorio creado: {self.ruta_app}")
            return True
        except Exception as e:
            print(f"❌ Error al crear directorio: {str(e)}")
            return False
    
    def generar_archivo_usuarios_json(self):
        """Genera un archivo JSON con los usuarios para la app Android"""
        try:
            archivo_usuarios = os.path.join(self.ruta_app, "usuarios.json")
            
            # Convertir a formato compatible con la app
            usuarios_app = []
            for usuario in self.usuarios:
                usuario_app = {
                    'id': usuario['id'],
                    'nombre': usuario['nombre'],
                    'email': usuario['email'],
                    'password': usuario['password'],
                    'rol': usuario['rol'],
                    'jugador': usuario.get('jugador'),
                    'fechaRegistro': usuario['fechaRegistro'],
                    'activo': usuario['activo'],
                    'equipo': usuario.get('equipo'),
                    'equipoId': usuario.get('equipoId')
                }
                usuarios_app.append(usuario_app)
            
            with open(archivo_usuarios, 'w', encoding='utf-8') as f:
                json.dump(usuarios_app, f, ensure_ascii=False, indent=2)
            
            print(f"✅ Archivo de usuarios generado: {archivo_usuarios}")
            return archivo_usuarios
            
        except Exception as e:
            print(f"❌ Error al generar archivo de usuarios: {str(e)}")
            return None
    
    def generar_base_datos_sqlite(self):
        """Genera una base de datos SQLite para la app Android"""
        try:
            archivo_db = os.path.join(self.ruta_app, "database.db")
            
            conn = sqlite3.connect(archivo_db)
            cursor = conn.cursor()
            
            # Crear tabla de usuarios
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS usuarios (
                    id TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    jugador TEXT,
                    fecha_registro TEXT,
                    activo INTEGER DEFAULT 1,
                    equipo TEXT,
                    equipo_id TEXT
                )
            ''')
            
            # Crear tabla de equipos (si no existe)
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS equipos (
                    id TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    categoria TEXT,
                    entrenador TEXT
                )
            ''')
            
            # Insertar usuarios
            for usuario in self.usuarios:
                cursor.execute('''
                    INSERT OR REPLACE INTO usuarios 
                    (id, nombre, email, password, rol, jugador, fecha_registro, activo, equipo, equipo_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ''', (
                    usuario['id'],
                    usuario['nombre'],
                    usuario['email'],
                    usuario['password'],
                    usuario['rol'],
                    usuario.get('jugador'),
                    usuario['fechaRegistro'],
                    1 if usuario['activo'] else 0,
                    usuario.get('equipo'),
                    usuario.get('equipoId')
                ))
            
            conn.commit()
            conn.close()
            
            print(f"✅ Base de datos SQLite generada: {archivo_db}")
            return archivo_db
            
        except Exception as e:
            print(f"❌ Error al generar base de datos: {str(e)}")
            return None
    
    def generar_archivo_configuracion(self):
        """Genera un archivo de configuración para la app"""
        try:
            archivo_config = os.path.join(self.ruta_app, "config.json")
            
            config = {
                'version': '1.0',
                'fecha_importacion': self.usuarios[0]['fechaRegistro'] if self.usuarios else '',
                'total_usuarios': len(self.usuarios),
                'usuarios_por_rol': self._contar_usuarios_por_rol(),
                'configuracion': {
                    'importacion_automatica': True,
                    'validacion_emails': True,
                    'detectar_duplicados': True
                }
            }
            
            with open(archivo_config, 'w', encoding='utf-8') as f:
                json.dump(config, f, ensure_ascii=False, indent=2)
            
            print(f"✅ Archivo de configuración generado: {archivo_config}")
            return archivo_config
            
        except Exception as e:
            print(f"❌ Error al generar configuración: {str(e)}")
            return None
    
    def _contar_usuarios_por_rol(self):
        """Cuenta usuarios por rol"""
        conteo = {}
        for usuario in self.usuarios:
            rol = usuario['rol']
            conteo[rol] = conteo.get(rol, 0) + 1
        return conteo
    
    def generar_script_instalacion(self):
        """Genera un script para instalar los usuarios en la app"""
        try:
            archivo_script = "instalar_usuarios.sh"
            
            script_content = f"""#!/bin/bash
# Script para instalar usuarios en la aplicación Android
# Generado automáticamente

echo "🚀 Instalando usuarios en la aplicación Android..."

# Verificar que existe el archivo de usuarios
if [ ! -f "{self.ruta_app}/usuarios.json" ]; then
    echo "❌ No se encontró el archivo de usuarios"
    exit 1
fi

# Copiar archivos a la aplicación
echo "📁 Copiando archivos..."

# Crear directorio de assets si no existe
mkdir -p {self.ruta_app}

# Copiar archivos
cp usuarios_importados.json {self.ruta_app}/usuarios.json
cp {self.ruta_app}/database.db {self.ruta_app}/database.db 2>/dev/null || echo "⚠️  Base de datos no encontrada"

echo "✅ Usuarios instalados correctamente"
echo "📱 Ahora puedes compilar y ejecutar la aplicación Android"
echo "🔑 Los usuarios podrán iniciar sesión con sus credenciales del Excel"
"""
            
            with open(archivo_script, 'w', encoding='utf-8') as f:
                f.write(script_content)
            
            # Hacer el script ejecutable (en sistemas Unix)
            try:
                os.chmod(archivo_script, 0o755)
            except:
                pass  # En Windows no es necesario
            
            print(f"✅ Script de instalación generado: {archivo_script}")
            return archivo_script
            
        except Exception as e:
            print(f"❌ Error al generar script: {str(e)}")
            return None
    
    def generar_resumen_integracion(self):
        """Genera un resumen de la integración"""
        print("\n" + "="*60)
        print("📱 RESUMEN DE INTEGRACIÓN CON ANDROID")
        print("="*60)
        
        print(f"✅ Usuarios cargados: {len(self.usuarios)}")
        
        # Estadísticas por rol
        conteo_roles = self._contar_usuarios_por_rol()
        print("\n📊 Distribución por roles:")
        for rol, cantidad in conteo_roles.items():
            print(f"   - {rol}: {cantidad} usuarios")
        
        # Archivos generados
        print(f"\n📁 Archivos generados:")
        print(f"   - {self.ruta_app}/usuarios.json")
        print(f"   - {self.ruta_app}/database.db")
        print(f"   - {self.ruta_app}/config.json")
        print(f"   - instalar_usuarios.sh")
        
        print(f"\n🎯 Próximos pasos:")
        print(f"   1. Compilar la aplicación Android")
        print(f"   2. Instalar en el dispositivo")
        print(f"   3. Los usuarios pueden iniciar sesión con sus credenciales")
        
        print(f"\n🔑 Credenciales de ejemplo:")
        if self.usuarios:
            usuario_ejemplo = self.usuarios[0]
            print(f"   Email: {usuario_ejemplo['email']}")
            print(f"   Contraseña: {usuario_ejemplo['password']}")
            print(f"   Rol: {usuario_ejemplo['rol']}")
    
    def integrar(self):
        """Ejecuta todo el proceso de integración"""
        print("🚀 Iniciando integración con aplicación Android...")
        
        # Paso 1: Cargar usuarios
        if not self.cargar_usuarios():
            return False
        
        # Paso 2: Crear directorio de assets
        if not self.crear_directorio_assets():
            return False
        
        # Paso 3: Generar archivos
        print("\n📁 Generando archivos para Android...")
        
        archivo_usuarios = self.generar_archivo_usuarios_json()
        archivo_db = self.generar_base_datos_sqlite()
        archivo_config = self.generar_archivo_configuracion()
        archivo_script = self.generar_script_instalacion()
        
        if not archivo_usuarios:
            print("❌ Error al generar archivos")
            return False
        
        # Paso 4: Generar resumen
        self.generar_resumen_integracion()
        
        print("\n🎉 ¡Integración completada exitosamente!")
        print("📱 Los usuarios están listos para usar en la aplicación Android")
        
        return True

def main():
    """Función principal"""
    print("📱 INTEGRADOR ANDROID - SOCIAL MANAGER")
    print("="*50)
    
    # Verificar si existe el archivo de usuarios importados
    archivo_json = "usuarios_importados.json"
    
    if not os.path.exists(archivo_json):
        print(f"❌ No se encontró el archivo: {archivo_json}")
        print("💡 Primero debes ejecutar el script de importación:")
        print("   python importar_usuarios_excel.py")
        return
    
    # Crear integrador y ejecutar
    integrador = IntegradorAndroid(archivo_json)
    exito = integrador.integrar()
    
    if exito:
        print("\n🎉 ¡Integración completada!")
        print("📱 Ahora puedes compilar y ejecutar la aplicación Android")
        print("🔑 Los usuarios podrán iniciar sesión con sus credenciales del Excel")
    else:
        print("\n❌ La integración falló. Revisa los errores arriba.")

if __name__ == "__main__":
    main() 