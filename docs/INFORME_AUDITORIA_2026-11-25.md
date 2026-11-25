# Informe inicial para chat 00 — Auditoría 07

**Fecha:** 2026-11-25, madrugada (Costa Rica).  
**Tipo:** auditoría estática inicial, no regresión ejecutada.  
**Base:** `main@a914e80312e15bc1841086c8c5264be216d11114`; ramas de diseño `feature/motor@db233580` y `feature/variantes-horse@88b97f46`.  
**Fuente oficial:** `poker.pdf`, rúbrica pp. 7–8; guía maestra para decisiones de trabajo.

## Diagnóstico

La **infraestructura está creada**: `:core` Kotlin/JVM y `:app` Android/Compose; `app` declara dependencia en `core`. El único código funcional de core que se encontró es `CoreInfo.mensaje()`, cubierto por `CoreInfoTest.mensajeCorrecto()`; `MainActivity` presenta el mensaje. El repositorio documenta compilaciones satisfactorias y un dispositivo Samsung SM-S931B Android 16 utilizado en la integración inicial; **esta auditoría no repitió esos comandos**.

En las tres ramas inspeccionadas no se hallaron implementaciones Kotlin del dominio, evaluadores, reglas HORSE, motor, apuestas, botes ni bots. Los contenidos nuevos de las dos ramas de desarrollo son documentación conceptual; `docs/CONTRATOS.md` sigue indicando contratos pendientes. Tampoco existe evidencia de que el juego complete una mano o funcione en ambas orientaciones.

## Puntuación

**No hay calificación académica verificable todavía.** Si se sometiera hoy este snapshot como proyecto terminado, una **estimación conservadora orientativa** sería **0–10/100**, no una nota formal: únicamente hay pruebas parciales de arquitectura de módulos y visualización inicial; el resto de la rúbrica carece de implementación examinable. **Esto no es un pronóstico del resultado del 2 de diciembre.**

## Hallazgos para priorizar

1. **Bloqueante:** falta dominio y contratos aprobados (chat 02, coordinación 00).
2. **Bloqueante:** faltan cinco estrategias diferenciadas con sustitución (chat 03).
3. **Bloqueante:** falta motor con acciones legales, botes, fichas conservadas (chat 04).
4. **Bloqueante:** `:app` no tiene juego interactivo ni ambas orientaciones demostradas (chat 05).
5. **Alta:** pruebas de negocio inexistentes, aunque existe test de infraestructura (todos + chat 07).
6. **Alta:** reglas de apuestas, fichas impares, rotación, cantidad de jugadores y agotamiento de mazo sin decisión común (chat 00 con 02/03/04).

## Registro de evidencia y modificaciones

**Inspeccionado:** `README.md`, settings/build scripts, `CoreInfo.kt`, `CoreInfoTest.kt`, `MainActivity.kt`, tests Android de plantilla, `docs/{ARQUITECTURA,CONTRATOS,DECISIONES,ESTADO,REGISTRO_PRUEBAS,REGLAS,RUBRICA,PLAN}.md` y documentación de ramas 03/04.

**Pruebas ejecutadas por chat 07:** **NINGUNA**.  
**Pruebas reportadas por chat 01:** `:core:test` 1/1, compilación y APK iniciales, instalación física (ver `docs/REGISTRO_PRUEBAS.md`).  
**Código de producción modificado por chat 07:** ninguno.

**Documentación nueva de chat 07:** `docs/MATRIZ_AUDITORIA.md`, `docs/PLAN_MAESTRO_PRUEBAS.md`, este informe, exclusivamente en rama `feature/integration-tests`.

## Siguiente acción solicitada al chat 00

Definir/cerrar contratos transversales primero y evitar dos modelos incompatibles; exigir a 02/03/04 tests deterministas por incremento. Integrar un corte vertical mínimo `app -> motor -> reglas -> resultado` antes del acabado visual. Mantener rastreo de SHA, ejecución real y prueba de teléfono físico. Reauditar cada merge funcional y reservar el 1 de diciembre para regresión y defensa.

**Decisión de integración:** este informe propone evidencia; no aprueba contratos ni mergea ramas de producción.
