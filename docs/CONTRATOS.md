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