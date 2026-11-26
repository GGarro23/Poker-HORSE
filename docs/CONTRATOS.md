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