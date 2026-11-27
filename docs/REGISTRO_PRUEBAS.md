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

### Revalidacion final de infraestructura

Comando:
./gradlew :core:test :app:assembleDebug --rerun-tasks

Resultado: BUILD SUCCESSFUL.
40 tareas ejecutadas.
Duracion: 11 segundos.

Commit de infraestructura: fae8536.

## 2026-11-26 - Cartas, rangos y palos

Comando:
./gradlew :core:test --rerun-tasks

Resultados verificados mediante informes XML:

- CoreInfoTest: 1 prueba.
- CartaTest: 9 pruebas.
- Total: 10 pruebas.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.

Resultado: exitoso.

## 2026-11-26 - Mazo inmutable

Comando:
./gradlew :core:test --rerun-tasks

Primera ejecución:
- 19 pruebas.
- 1 fallo en extraerDeMazoVacio.
- Se corrigió la validación del estado del mazo
  antes de acceder a la primera carta.

Ejecución posterior verificada mediante XML:
- CoreInfoTest: 1 prueba.
- CartaTest: 9 pruebas.
- MazoTest: 9 pruebas.
- Total: 19.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.

Resultado: exitoso.