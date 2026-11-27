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

## D-007 - Representacion de cartas

Estado: implementada.

- Utilizar enumeraciones para Palo y Rango.
- Utilizar una data class inmutable para Carta.
- Representar cartas mediante composicion.
- Utilizar igualdad estructural.
- Delegar las comparaciones dependientes de las
  reglas a evaluadores y estrategias.

Justificacion:
Los tipos enumerados impiden representar rangos
y palos invalidos. No se requiere una jerarquia
de cartas ni validaciones redundantes.

## D-008 - Mazo inmutable

Estado: implementada.

- Constructor privado y método de fábrica.
- Colección interna privada con copia defensiva.
- Validación de cantidad máxima y ausencia de duplicados.
- Mezcla y extracción sin modificar el objeto original.
- Inyección del generador Random.
- ResultadoExtraccion como tipo explícito de dominio.
- Uso de IllegalStateException al extraer de un mazo vacío.

Justificación:
Preservar los estados anteriores del juego, proteger
las invariantes y facilitar pruebas reproducibles.