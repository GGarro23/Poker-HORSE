# Contratos

Estado: pendientes de definicion definitiva.

El chat 02 definira el modelo de dominio y sus contratos.

Areas pendientes:

- Cartas, rangos, palos y mazo.
- Jugadores, identidad y asientos.
- Estados de mano y partida.
- Acciones y resultados de transiciones.
- Evaluadores y estrategias de variantes.
- Representacion de fichas y apuestas.

Los cambios en contratos compartidos deben coordinarse
con el centro de control.

CoreInfo es una utilidad provisional de infraestructura,
no un contrato del dominio.

## Cartas, rangos y palos

Estado: implementado y probado.

Paquete: cr.ac.tec.pokerhorse.core.domain

- Palo: enum con PICA, DIAMANTE, TREBOL y CORAZON.
- Rango: enum con 13 rangos y propiedad
  inmutable valor: Int, del 2 al 14.
- Carta: data class inmutable con las propiedades
  palo: Palo y rango: Rango.

La igualdad de cartas es estructural.
El valor nominal del As es 14.

La interpretación del As en manos bajas y el orden
de los palos corresponden a las estrategias y
evaluadores, no a Carta.

## Mazo y extracción

Estado: implementado y probado.

Clase Mazo:
- Mazo.crear(): Mazo
- cantidad: Int
- estaVacio(): Boolean
- mezclar(random: Random = Random.Default): Mazo
- extraer(): ResultadoExtraccion

ResultadoExtraccion:
- carta: Carta
- mazoRestante: Mazo

Mazo.crear() genera 52 cartas únicas.

La mezcla y la extracción producen nuevas instancias
sin modificar el mazo original.

El constructor es privado y la colección interna
está encapsulada.

Extraer de un mazo vacío lanza IllegalStateException.

La mezcla admite generadores aleatorios con semilla
para permitir pruebas reproducibles.

## Jugadores, identidad y participación

Estado: implementado y probado, pendiente de ratificación.

- JugadorId: value class con identificador: Int no negativo.
- Asiento: value class con posicion: Int no negativa.
- Jugador: data class inmutable con id, nombre, asiento y fichas.
- Las cantidades de fichas se representan mediante Long.

Operaciones de Jugador:
- retirarFichas(cantidad: Long): Jugador
- recibirFichas(cantidad: Long): Jugador

EstadoJugador:
- ACTIVO
- RETIRADO
- ALL_IN

ParticipacionMano contiene:
- jugadorId
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

ResultadoAporte contiene el jugador y la participación actualizados.

Los antes incrementan exclusivamente el aporte total.
Las operaciones normales incrementan ambos aportes.
Agotar las fichas mediante un aporte produce ALL_IN.

La legalidad de las apuestas y transiciones de estado
corresponde al motor.

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