# Registro de pruebas

## 2026-11-25 - Infraestructura

### Compilacion Android inicial

Comando: ./gradlew :app:assembleDebug

Resultado: BUILD SUCCESSFUL.
36 tareas ejecutadas.
Duracion: 1m 54s.
APK: aproximadamente 12 MB.

### Compilacion multimodulo

Comando: ./gradlew :core:build :app:assembleDebug

Resultado: BUILD SUCCESSFUL.
37 tareas ejecutadas.
Duracion: 1m 26s.

### Prueba unitaria de core

Comando: ./gradlew :core:test

Prueba: CoreInfoTest.mensajeCorrecto

- Pruebas: 1.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.

Resultado confirmado mediante informe XML y HTML.

### Integracion fisica

Dispositivo: Samsung SM-S931B.
Android: 16.
API: 36.

La aplicacion presenta correctamente el mensaje
proporcionado por core.

Resultado: verificado visualmente en dispositivo fisico.

### Observaciones

Advertencia no bloqueante al retirar simbolos de
depuracion de libandroidx.graphics.path.so.

La base preliminar 00 no forma parte de estas pruebas.

Las pruebas del dominio, motor y orientacion horizontal
continuan pendientes.
