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

## D-009 - Identidad y participación de jugadores

Estado: Aceptada

- Separar identidad y posición mediante value classes.
- Utilizar Long para representar cantidades de fichas.
- Proteger las operaciones contra desbordamientos.
- Representar Jugador mediante una data class inmutable.
- Separar los datos persistentes de los datos de cada mano.
- Proteger las colecciones mediante copias defensivas.
- Actualizar conjuntamente fichas y aportes mediante ResultadoAporte.

Justificación:

Evitar confusiones entre identificadores y posiciones,
preservar estados anteriores y garantizar la conservación
local de fichas sin duplicar información.

La unicidad de identidades y asientos, la capacidad de
la mesa y la coherencia global corresponden a los
estados agregados de la partida y al motor.

## D-010 - Variantes y calles

Estado: implementada y probada, pendiente de ratificación.

Representar variantes y calles mediante enumeraciones.

Delegar las secuencias y reglas a las estrategias.
No utilizar ordinal para avanzar entre calles.

## D-011 - Representación polimórfica de acciones

Estado: implementada y probada, pendiente de ratificación.

Utilizar una sealed interface para representar acciones
con y sin parámetros.

Utilizar data object para Fold, Check, Call y AllIn.
Utilizar data class para Bet y Raise.

Bet representa fichas nuevas y Raise un aporte objetivo
de la calle.

Justificación:

Evitar estados inválidos, garantizar seguridad de tipos
y permitir expresiones when exhaustivas.

## D-012 - Configuración inmutable

Estado: implementada y probada, pendiente de ratificación.

Separar los parámetros iniciales de los estados
dinámicos de la partida.

Utilizar Long para las cantidades de fichas y
prevenir desbordamientos desde la construcción.

## D-013 - Estados agregados

Estado: implementada y probada, pendiente de ratificación.

EstadoMano reúne jugadores, participaciones, mazo,
cartas comunitarias y datos de las apuestas.

EstadoPartida contiene EstadoMano mediante composición.

No duplicar el almacenamiento de jugadores ni del pozo.

Calcular el pozo y la apuesta vigente a partir
de los aportes.

Proteger las colecciones mediante copias defensivas.

Comprobar la conservación global de fichas respecto
de la configuración inicial.

## D-014 - Resultados tipados

Estado: implementada y probada, pendiente de ratificación.

Utilizar ResultadoTransicion<out T> para representar
éxito o rechazo de una operación.

Representar los errores esperables mediante ErrorDominio.

Reservar las excepciones para construcciones inválidas
o incumplimientos de las precondiciones internas.

## PROP-03-002 — Integración de reglas fixed-limit

Estado: contrato general ratificado provisionalmente; detalles pendientes
de coordinación con 04 y cierre por 00.

- Límite propuesto: tres aumentos completos tras apertura, incluso heads-up.
- Stud alto cuarta: incremento grande opcional con pareja visible; el motor
  conserva el incremento grande cuando se ha utilizado.
- Agotamiento Stud: carta comunitaria excepcional en séptima; no reducir
  el máximo de ocho jugadores del dominio.
- LAN listen server: el anfitrión es la autoridad; no ranked obligatorio.
- La estrategia no muta el dominio, ni ejecuta apuestas, ni serializa.

## PROP-04-002 — Integración del motor y sesión LAN

Estado: implementada en feature/motor-de-juego; pendiente de pruebas y
ratificación por 00. No modifica las firmas del dominio ni de variantes.

- EstadoMotor compone EstadoPartida y una RondaApuestas interna; conserva
  pendientes, derechos de subida, objetivo nominal e incrementos disponibles.
  No duplica las fichas ni los aportes del dominio.
- MotorDeJuego orquesta Repartidor, InicioMano, GestorApuestas y
  CalculadorDeBotes. Las estrategias se resuelven por su contrato.
- SesionAutoritativa serializa comandos en el anfitrión, exige revisión
  esperada y devuelve vistas filtradas. La identidad debe provenir de
  la asociación conexión-jugador en la capa de transporte.
- La red no debe serializar EstadoMotor ni EstadoMano. Los bots consumen
  VistaJugador y todas sus propuestas pasan por MotorDeJuego.aplicar.
- Políticas de all-in corto, fichas impares y rotación: ver REGLAS.md.
