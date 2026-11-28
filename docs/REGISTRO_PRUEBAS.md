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

## 2026-11-26 - Jugadores, identidad y participación

Comando:
./gradlew :core:test --rerun-tasks

Resultados confirmados mediante informes XML:

- CoreInfoTest: 1 prueba.
- CartaTest: 9 pruebas.
- MazoTest: 9 pruebas.
- AsientoTest: 5 pruebas.
- JugadorIdTest: 5 pruebas.
- JugadorTest: 17 pruebas.
- ParticipacionManoTest: 23 pruebas.

Total: 69 pruebas.
Fallos: 0.
Errores: 0.
Omitidas: 0.

Resultado: exitoso.

Se verificaron las invariantes, la inmutabilidad,
la protección de colecciones, los límites numéricos
y la conservación local de fichas.

## 2026-11-27 - Acciones del dominio

Comando:
./gradlew :core:test --rerun-tasks

- Pruebas anteriores: 71.
- AccionTest: 12.
- Total: 83.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.

Resultado: BUILD SUCCESSFUL.
Duración: 7 segundos.
Evidencia: informes XML.

## 2026-11-27 - Cierre del modelo de dominio

Comando:
./gradlew :core:test --rerun-tasks

- CoreInfoTest: 1.
- CartaTest: 9.
- MazoTest: 9.
- AsientoTest: 5.
- JugadorIdTest: 5.
- JugadorTest: 17.
- ParticipacionManoTest: 23.
- VarianteCalleTest: 2.
- AccionTest: 12.
- ConfiguracionPartidaTest: 16.
- EstadoManoTest: 19.
- EstadoPartidaTest: 10.
- ResultadoTransicionTest: 6.

Total: 134 pruebas.
Fallos: 0.
Errores: 0.
Omitidas: 0.

Resultado: BUILD SUCCESSFUL.
Duración: 7 segundos.

Evidencia: informes XML de Gradle.
Fecha UTC: 2026-11-28T03:55:41Z.

## 2026-11-27 — Chat 03

- Evaluador alto: ejecución local completa exitosa (11 s), específica (15 s).
- Base de evaluadores y variantes c9cc1a6: ejecución local
  `./gradlew :core:test --rerun-tasks`, BUILD SUCCESSFUL (10 s),
  4 tareas ejecutadas. No se recibió el XML, por lo que no se afirma un total.
- Cierre: 13 métodos nuevos en OrdenAccionTest; pendientes de ejecución.
  No se cuenta un método escrito como prueba ejecutada.
- Verificar con `./gradlew :core:test --rerun-tasks` y sumar tests,
  failures, errors y skipped de core/build/test-results/test/TEST-*.xml.
