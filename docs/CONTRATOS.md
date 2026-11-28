# Contratos

Estado: modelo de dominio base implementado y probado.
Pendiente de ratificación final por el centro de control.

Paquete: cr.ac.tec.pokerhorse.core.domain

Los cambios en contratos compartidos deben coordinarse
con el centro de control.

CoreInfo es una utilidad provisional de infraestructura,
no un contrato del dominio.

## Cartas, rangos y palos

Estado: implementado y probado.

- Palo: enum con PICA, DIAMANTE, TREBOL y CORAZON.
- Rango: enum con 13 rangos y propiedad valor: Int,
  del 2 al 14.
- Carta: data class inmutable con palo: Palo
  y rango: Rango.

La igualdad de cartas es estructural.
El valor nominal del As es 14.

La interpretación del As en manos bajas y el orden
de los palos corresponden a las estrategias
y evaluadores.

## Mazo y extracción

Estado: ratificado por el centro de control.
La nueva operación obtenerCartas queda pendiente
de ratificación.

Clase Mazo:

- Mazo.crear(): Mazo
- cantidad: Int
- estaVacio(): Boolean
- mezclar(random: Random = Random.Default): Mazo
- extraer(): ResultadoExtraccion
- obtenerCartas(): List<Carta>

ResultadoExtraccion:

- carta: Carta
- mazoRestante: Mazo

Mazo.crear() genera 52 cartas únicas.

La mezcla y la extracción producen nuevas instancias
sin modificar el mazo original.

El constructor es privado y la colección interna
está encapsulada.

Obtener las cartas devuelve una copia defensiva.

Extraer de un mazo vacío lanza IllegalStateException.

La mezcla admite generadores aleatorios con semilla
para permitir pruebas reproducibles.

## Jugadores, identidad y participación

Estado: aceptado por el centro de control.
Decisión D-009.

- JugadorId: value class con identificador: Int
  no negativo.
- Asiento: value class con posicion: Int no negativa.
- Jugador: data class inmutable con id, nombre,
  asiento y fichas.
- Las cantidades de fichas se representan mediante Long.

Operaciones de Jugador:

- retirarFichas(cantidad: Long): Jugador
- recibirFichas(cantidad: Long): Jugador

EstadoJugador:

- ACTIVO
- RETIRADO
- ALL_IN

ParticipacionMano contiene:

- jugadorId: JugadorId
- Cartas individuales protegidas mediante copias defensivas.
- aporteCalle: Long
- aporteTotal: Long
- estado: EstadoJugador

Operaciones públicas:

- obtenerCartas(): List<Carta>
- agregarCarta(carta: Carta): ParticipacionMano
- reiniciarAporteCalle(): ParticipacionMano
- cambiarEstado(nuevoEstado: EstadoJugador): ParticipacionMano
- aportarFichas(jugador: Jugador, cantidad: Long,
  esAnte: Boolean = false): ResultadoAporte

ResultadoAporte contiene el jugador y la participación
actualizados.

Los antes incrementan exclusivamente el aporte total.
Los aportes normales incrementan ambos aportes.

Agotar las fichas mediante un aporte produce ALL_IN.

Las operaciones protegen las cantidades contra valores
negativos y desbordamientos.

La legalidad contextual de las apuestas y transiciones
de estado corresponde al motor.

## Variantes y calles

Estado: implementado y probado, pendiente de ratificación.

Variante:

- HOLD_EM
- OMAHA_HI_LO
- RAZZ
- STUD
- STUD_HI_LO

Calle:

- PREFLOP
- FLOP
- TURN
- RIVER
- TERCERA
- CUARTA
- QUINTA
- SEXTA
- SEPTIMA

Las estrategias determinan las calles válidas,
su secuencia y las reglas particulares.

No se utiliza ordinal para determinar transiciones.

Showdown no constituye una calle de apuestas.

## Acciones

Estado: implementado y probado, pendiente de ratificación.

Accion es una sealed interface con seis implementaciones:

- Fold
- Check
- Call
- AllIn
- Bet(cantidad: Long)
- Raise(hasta: Long)

Bet representa las fichas nuevas de una apuesta inicial.

Raise representa el aporte objetivo de la calle,
no el incremento ni el aporte acumulado de la mano.

Bet y Raise requieren montos estrictamente positivos.

Call y AllIn no reciben montos: el motor los calcula
a partir del estado vigente.

La legalidad de las acciones corresponde al motor.

## Configuración de partida

Estado: implementado y probado, pendiente de ratificación.

ConfiguracionPartida es una data class inmutable.

Propiedades y valores predeterminados:

- cantidadJugadores: Int = 6
- fichasIniciales: Long = 1000L
- apuestaPequena: Long = 20L
- apuestaGrande: Long = 40L
- ante: Long = 2L
- bringIn: Long = 5L
- manosPorVariante: Int = 6

Invariantes:

- Entre 2 y 8 jugadores.
- Fichas iniciales positivas.
- Total inicial representable mediante Long.
- Apuesta pequeña positiva.
- Apuesta grande mayor o igual a la pequeña.
- Ante no negativo.
- Bring-in positivo y menor o igual a la apuesta pequeña.
- Al menos una mano por variante.

Los valores predeterminados y límites específicos
quedan sujetos a la ratificación de las reglas HORSE.

## EstadoMano

Estado: implementado y probado, pendiente de ratificación.

Agregado inmutable que representa una mano.

Contiene:

- variante: Variante
- calle: Calle
- mazo: Mazo
- Jugadores.
- Participaciones.
- Cartas comunitarias.
- turno: JugadorId?
- boton: Asiento
- aumentosCalle: Int

Operaciones públicas:

- obtenerJugadores(): List<Jugador>
- obtenerParticipaciones(): List<ParticipacionMano>
- obtenerComunitarias(): List<Carta>
- buscarJugador(id: JugadorId): Jugador?
- buscarParticipacion(id: JugadorId): ParticipacionMano?

Propiedades calculadas:

- pozo: Long
- apuestaVigente: Long
- totalFichas: Long

El pozo es la suma de los aportes totales.

La apuesta vigente es el mayor aporte de la calle.

El total de fichas es la suma de las fichas disponibles
y el pozo.

Invariantes:

- Identidades y asientos únicos.
- Participaciones únicas asociadas a jugadores existentes.
- Ausencia de cartas duplicadas entre mazo,
  comunitarias y participaciones.
- Botón asociado a un asiento existente.
- Turno nulo o correspondiente a un jugador activo
  con fichas disponibles.
- Cantidad de aumentos no negativa.
- Total de fichas representable mediante Long.

Las colecciones se protegen mediante copias defensivas.

## EstadoPartida

Estado: implementado y probado, pendiente de ratificación.

Data class inmutable que contiene:

- configuracion: ConfiguracionPartida
- manoActual: EstadoMano
- manosCompletadas: Long

Propiedades calculadas:

- totalFichasIniciales: Long
- varianteActual: Variante

Operaciones públicas:

- obtenerJugadores(): List<Jugador>

Invariantes:

- Cantidad de manos completadas no negativa.
- Cantidad de jugadores consistente con la configuración.
- Asientos dentro de los límites de la mesa.
- Conservación global de fichas respecto al total inicial.

Los jugadores eliminados permanecen registrados
con cero fichas.

No se duplica el almacenamiento de jugadores entre
EstadoPartida y EstadoMano.

## Resultados y errores

Estado: implementado y probado, pendiente de ratificación.

ResultadoTransicion<out T> es una sealed interface
con dos implementaciones:

- Exito<T>(estado: T)
- Rechazo(error: ErrorDominio)

ErrorDominio es una enumeración:

- JUGADOR_INEXISTENTE
- JUGADOR_NO_ACTIVO
- FUERA_DE_TURNO
- ACCION_NO_PERMITIDA
- MONTO_INVALIDO
- FICHAS_INSUFICIENTES
- MANO_FINALIZADA
- PARTIDA_FINALIZADA

Los rechazos esperables de acciones del usuario
se representan mediante ResultadoTransicion.Rechazo.

Las construcciones inválidas y los incumplimientos
de precondiciones internas utilizan require o check.

El motor debe preservar el estado original
ante una acción rechazada.

## Integración pendiente

Los siguientes asuntos deberán resolverse de manera
coordinada con los chats de evaluadores, variantes,
motor e interfaz:

- Contratos de EvaluadorDeManos y ReglasVariante.
- Visibilidad de cartas individuales en Stud y Razz.
- Jugadores pendientes de actuar.
- Reapertura y límites de aumentos.
- Coherencia entre fichas y estados de participación.
- Transiciones legales de EstadoJugador.
- Devolución de apuestas no igualadas.
- Fases de resolución y liquidación de pozos.
- Agotamiento del mazo.
- Rotación HORSE.
- Proyección de información permitida para bots y UI.

Core permanece independiente de Android y Compose.

## Verificación

Modelo de dominio base probado mediante:

./gradlew :core:test --rerun-tasks

- Pruebas: 134.
- Fallos: 0.
- Errores: 0.
- Omitidas: 0.

Resultado: BUILD SUCCESSFUL.

Evidencia: informes XML de la ejecución local
del 27 de noviembre de 2026, hora de Costa Rica.