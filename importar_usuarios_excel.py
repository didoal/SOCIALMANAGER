#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para importar usuarios desde Excel a la aplicación SocialManager
Autor: Asistente IA
Fecha: 2025
"""

import pandas as pd
import json
import os
import hashlib
import uuid
from datetime import datetime
import sqlite3
from pathlib import Path

class ImportadorUsuarios:
    def __init__(self, archivo_excel, ruta_db=None):
        """
        Inicializa el importador de usuarios
        
        Args:
            archivo_excel (str): Ruta al archivo Excel
            ruta_db (str): Ruta a la base de datos SQLite (opcional)
        """
        self.archivo_excel = archivo_excel
        self.ruta_db = ruta_db or self._encontrar_base_datos()
        self.usuarios_importados = []
        self.errores = []
        self.duplicados = []
        
    def _encontrar_base_datos(self):
        """Busca automáticamente la base de datos de la app"""
        # Rutas posibles donde puede estar la base de datos
        rutas_posibles = [
            "app/src/main/assets/database.db",
            "database.db",
            "SocialManager.db",
            "usuarios.db"
        ]
        
        for ruta in rutas_posibles:
            if os.path.exists(ruta):
                return ruta
                
        # Si no encuentra, crea una nueva
        return "usuarios_importados.db"
    
    def leer_excel(self):
        """Lee el archivo Excel y valida los datos"""
        try:
            print(f"📖 Leyendo archivo Excel: {self.archivo_excel}")
            
            # Leer el archivo Excel
            df = pd.read_excel(self.archivo_excel)
            
            # Validar columnas requeridas
            columnas_requeridas = ['nombre', 'email', 'password', 'rol']
            columnas_faltantes = [col for col in columnas_requeridas if col not in df.columns]
            
            if columnas_faltantes:
                raise ValueError(f"Columnas faltantes en el Excel: {columnas_faltantes}")
            
            print(f"✅ Excel leído correctamente. {len(df)} filas encontradas.")
            return df
            
        except Exception as e:
            print(f"❌ Error al leer el archivo Excel: {str(e)}")
            return None
    
    def validar_datos(self, df):
        """Valida los datos del DataFrame"""
        print("🔍 Validando datos...")
        
        errores = []
        datos_validos = []
        
        for index, row in df.iterrows():
            errores_fila = []
            
            # Validar nombre
            if pd.isna(row['nombre']) or str(row['nombre']).strip() == '':
                errores_fila.append("Nombre vacío")
            
            # Validar email
            email = str(row['email']).strip()
            if pd.isna(row['email']) or email == '':
                errores_fila.append("Email vacío")
            elif '@' not in email:
                errores_fila.append("Email inválido")
            
            # Validar password
            if pd.isna(row['password']) or str(row['password']).strip() == '':
                errores_fila.append("Contraseña vacía")
            
            # Validar rol
            rol = str(row['rol']).strip().lower()
            roles_validos = ['administrador', 'admin', 'padre', 'tutor', 'entrenador']
            if pd.isna(row['rol']) or rol == '':
                errores_fila.append("Rol vacío")
            elif rol not in roles_validos:
                errores_fila.append(f"Rol inválido: {rol}. Roles válidos: {roles_validos}")
            
            # Validar jugador (opcional)
            jugador = None
            if 'jugador' in row and not pd.isna(row['jugador']):
                jugador = str(row['jugador']).strip()
            
            # Validar equipo (opcional)
            equipo = None
            if 'equipo' in row and not pd.isna(row['equipo']):
                equipo = str(row['equipo']).strip()
            
            if errores_fila:
                errores.append({
                    'fila': index + 2,  # +2 porque Excel empieza en 1 y el header está en 1
                    'email': email,
                    'errores': errores_fila
                })
            else:
                datos_validos.append({
                    'nombre': str(row['nombre']).strip(),
                    'email': email,
                    'password': str(row['password']).strip(),
                    'rol': rol,
                    'jugador': jugador,
                    'equipo': equipo
                })
        
        self.errores = errores
        return datos_validos
    
    def verificar_duplicados(self, datos_validos):
        """Verifica si hay usuarios duplicados"""
        print("🔍 Verificando duplicados...")
        
        emails_vistos = set()
        duplicados = []
        datos_sin_duplicados = []
        
        for dato in datos_validos:
            if dato['email'] in emails_vistos:
                duplicados.append(dato)
            else:
                emails_vistos.add(dato['email'])
                datos_sin_duplicados.append(dato)
        
        self.duplicados = duplicados
        return datos_sin_duplicados
    
    def crear_usuario_json(self, dato):
        """Crea un objeto usuario en formato JSON compatible con la app"""
        usuario_id = str(uuid.uuid4())
        fecha_registro = datetime.now().isoformat()
        
        # Normalizar rol
        rol = dato['rol']
        if rol in ['admin', 'administrador']:
            rol = 'administrador'
        elif rol in ['padre', 'tutor']:
            rol = 'padre'
        
        usuario = {
            'id': usuario_id,
            'nombre': dato['nombre'],
            'email': dato['email'],
            'password': dato['password'],  # En producción, esto debería estar hasheado
            'rol': rol,
            'jugador': dato.get('jugador'),
            'fechaRegistro': fecha_registro,
            'activo': True,
            'equipo': dato.get('equipo'),
            'equipoId': None  # Se asignará después si es necesario
        }
        
        return usuario
    
    def guardar_en_json(self, usuarios):
        """Guarda los usuarios en un archivo JSON"""
        archivo_json = "usuarios_importados.json"
        
        try:
            with open(archivo_json, 'w', encoding='utf-8') as f:
                json.dump(usuarios, f, ensure_ascii=False, indent=2)
            
            print(f"✅ Usuarios guardados en: {archivo_json}")
            return archivo_json
            
        except Exception as e:
            print(f"❌ Error al guardar JSON: {str(e)}")
            return None
    
    def guardar_en_sqlite(self, usuarios):
        """Guarda los usuarios en una base de datos SQLite"""
        try:
            conn = sqlite3.connect(self.ruta_db)
            cursor = conn.cursor()
            
            # Crear tabla si no existe
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
            
            # Insertar usuarios
            for usuario in usuarios:
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
            
            print(f"✅ Usuarios guardados en base de datos: {self.ruta_db}")
            return True
            
        except Exception as e:
            print(f"❌ Error al guardar en SQLite: {str(e)}")
            return False
    
    def generar_resumen(self):
        """Genera un resumen de la importación"""
        print("\n" + "="*50)
        print("📊 RESUMEN DE IMPORTACIÓN")
        print("="*50)
        
        print(f"✅ Usuarios importados exitosamente: {len(self.usuarios_importados)}")
        print(f"❌ Errores encontrados: {len(self.errores)}")
        print(f"⚠️  Duplicados encontrados: {len(self.duplicados)}")
        
        if self.errores:
            print("\n📋 DETALLE DE ERRORES:")
            for error in self.errores:
                print(f"   Fila {error['fila']} ({error['email']}): {', '.join(error['errores'])}")
        
        if self.duplicados:
            print("\n📋 USUARIOS DUPLICADOS:")
            for dup in self.duplicados:
                print(f"   - {dup['email']} ({dup['nombre']})")
        
        if self.usuarios_importados:
            print("\n✅ USUARIOS IMPORTADOS:")
            for usuario in self.usuarios_importados:
                print(f"   - {usuario['email']} ({usuario['nombre']}) - Rol: {usuario['rol']}")
    
    def importar(self):
        """Ejecuta todo el proceso de importación"""
        print("🚀 Iniciando importación de usuarios desde Excel...")
        print(f"📁 Archivo Excel: {self.archivo_excel}")
        print(f"🗄️  Base de datos: {self.ruta_db}")
        
        # Paso 1: Leer Excel
        df = self.leer_excel()
        if df is None:
            return False
        
        # Paso 2: Validar datos
        datos_validos = self.validar_datos(df)
        if not datos_validos:
            print("❌ No hay datos válidos para importar")
            return False
        
        # Paso 3: Verificar duplicados
        datos_sin_duplicados = self.verificar_duplicados(datos_validos)
        
        # Paso 4: Crear objetos usuario
        print("👥 Creando objetos de usuario...")
        for dato in datos_sin_duplicados:
            usuario = self.crear_usuario_json(dato)
            self.usuarios_importados.append(usuario)
        
        # Paso 5: Guardar datos
        print("💾 Guardando datos...")
        
        # Guardar en JSON
        archivo_json = self.guardar_en_json(self.usuarios_importados)
        
        # Guardar en SQLite
        self.guardar_en_sqlite(self.usuarios_importados)
        
        # Paso 6: Generar resumen
        self.generar_resumen()
        
        return True

def crear_plantilla_excel():
    """Crea una plantilla de Excel para importar usuarios"""
    plantilla_data = {
        'nombre': ['Juan Pérez', 'María García', 'Luis Martínez', 'Carmen López'],
        'email': ['juan@club.com', 'maria@club.com', 'luis@club.com', 'carmen@club.com'],
        'password': ['padre123', 'padre123', 'padre123', 'padre123'],
        'rol': ['padre', 'padre', 'padre', 'padre'],
        'jugador': ['Carlos Pérez', 'Ana García', 'Pedro Martínez', 'Miguel López'],
        'equipo': ['Alevín A', 'Infantil B', 'Cadete A', 'Alevín A']
    }
    
    df = pd.DataFrame(plantilla_data)
    archivo_plantilla = "plantilla_usuarios.xlsx"
    
    with pd.ExcelWriter(archivo_plantilla, engine='openpyxl') as writer:
        df.to_excel(writer, sheet_name='Usuarios', index=False)
        
        # Agregar hoja de instrucciones
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
    
    print(f"✅ Plantilla creada: {archivo_plantilla}")
    return archivo_plantilla

def main():
    """Función principal"""
    print("🎯 IMPORTADOR DE USUARIOS - SOCIAL MANAGER")
    print("="*50)
    
    # Verificar si se proporciona un archivo Excel
    import sys
    if len(sys.argv) > 1:
        archivo_excel = sys.argv[1]
    else:
        # Buscar archivos Excel en el directorio actual
        archivos_excel = list(Path('.').glob('*.xlsx'))
        
        if not archivos_excel:
            print("❌ No se encontraron archivos Excel en el directorio actual.")
            print("¿Quieres crear una plantilla? (s/n): ", end='')
            respuesta = input().lower()
            
            if respuesta == 's':
                crear_plantilla_excel()
                print("\n📝 Se ha creado una plantilla. Complétala y ejecuta el script nuevamente.")
                return
            else:
                print("❌ No se puede continuar sin un archivo Excel.")
                return
        
        if len(archivos_excel) == 1:
            archivo_excel = str(archivos_excel[0])
        else:
            print("📁 Archivos Excel encontrados:")
            for i, archivo in enumerate(archivos_excel, 1):
                print(f"   {i}. {archivo}")
            
            print("\nSelecciona el archivo a importar (número): ", end='')
            try:
                seleccion = int(input()) - 1
                archivo_excel = str(archivos_excel[seleccion])
            except (ValueError, IndexError):
                print("❌ Selección inválida.")
                return
    
    # Verificar que el archivo existe
    if not os.path.exists(archivo_excel):
        print(f"❌ El archivo {archivo_excel} no existe.")
        return
    
    # Crear importador y ejecutar
    importador = ImportadorUsuarios(archivo_excel)
    exito = importador.importar()
    
    if exito:
        print("\n🎉 ¡Importación completada exitosamente!")
        print("📱 Los usuarios ya están listos para usar en la aplicación.")
    else:
        print("\n❌ La importación falló. Revisa los errores arriba.")

if __name__ == "__main__":
    main() 