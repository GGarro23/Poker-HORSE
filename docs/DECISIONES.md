# Decisiones tecnicas

## D-001 - Arquitectura multimodulo

Estado: aceptada.

Separar Android y el nucleo Kotlin/JVM.
La dependencia es exclusivamente app -> core.

## D-002 - Sistema de compilacion

Estado: implementado.

- Gradle Wrapper 9.6.0.
- AGP 9.5.2.
- Kotlin 2.4.21.
- Gradle Kotlin DSL.
- Catalogo centralizado de versiones.
- JDK 21 para core.
- JDK 25 para el daemon.
- Bytecode objetivo Java 11.

La compilacion local y la prueba de core fueron exitosas.

## D-003 - Plataforma Android

Estado: implementado.

- minSdk: 26.
- compileSdk: 37.
- targetSdk: 37.
- Jetpack Compose.
- Pruebas preferentemente en dispositivo fisico.

## D-004 - Control de versiones

Estado: aceptada.

- Repositorio: Poker-HORSE.
- Rama principal: main.
- Ramas cortas feature/ y fix/.
- Conventional Commits.
- Repositorio publico por decision del propietario.
- No publicar documentos privados ni credenciales.

La visibilidad publica modifica la decision inicial
de la guia de trabajo.

## D-005 - Organizacion de fuentes

Estado: implementada.

Core utiliza src/main/kotlin.
App conserva src/main/java, generado por Android Studio.

Los archivos de app siguen escritos en Kotlin.

## D-006 - Base preliminar 00

Estado: aceptada por el centro de control.

La existencia de la base preliminar fue confirmada
por el centro de control.

No se integrara automaticamente.

El chat 02 construira el dominio definitivo utilizando
la infraestructura actual. La base preliminar podra
consultarse como referencia opcional.

No se incorporan sus contratos ni pruebas al repositorio
en este hito.

## PROP-04-001 - Flujo del motor de juego

**Estado: PROPUESTA PENDIENTE, no adoptada.** Fecha: 25-11-2026.
**Documento:** `docs/MOTOR_FLUJO.md` en `feature/motor`.

- Orquestar desde `MotorDeJuego` transiciones puras, validacion de
  actor, avances de calle, cierre y liquidacion.
- Mantener calculo de apuestas y botes en colaboradores independientes.
- Obtener reparto, obligaciones, orden y evaluacion de `ReglasVariante`.
- Permitir un proveedor de mazo determinista y un bot con vista filtrada.
- Usar botes por niveles de aportes y elegir ganadores por cada bote,
  sin permitir ganar a jugadores retirados.
- Antes de codificar, pedir al chat 02 tipos definitivos de identidad,
  importes, acciones y resultados de transicion; al 03, contratos de
  variante, apuesta y evaluacion; al 00, arbitraje compartido.
- No modificar interfaces comunes ni mezclar `feature/motor` a
  `main` sin revisar esos puntos.

Esta propuesta describe una arquitectura interna del chat 04;
**no fija firmas ni cierra reglas**.
