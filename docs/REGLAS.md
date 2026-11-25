# Reglas del juego

HORSE comprende cinco variantes:

1. Texas Hold'em.
2. Omaha Hi-Lo.
3. Razz.
4. Seven Card Stud.
5. Stud Eight-or-Better.

Direccion inicial: poker de limite fijo.

Pendiente de validacion y especificacion:

- Estructuras de apuestas.
- Reparto de cartas.
- Orden de accion.
- Evaluacion de manos altas y bajas.
- Empates y distribucion de botes.
- Rotacion entre variantes.
- Configuracion de jugadores.
- Casos limite.

No existen reglas implementadas en el hito inicial.

## Propuestas y preguntas del chat 04 (NO aprobadas)

Fecha: 25-11-2026. Ver `docs/MOTOR_FLUJO.md`. Estas notas **no
sustituyen** las decisiones de los chats 02, 03 ni 00.

- Confirmar blinds, antes, bring-in, tamaños de apuesta por calle, tope
  de subidas, reglas heads-up y excepción de all-in incompleto.
- Confirmar si una subida incompleta reabre o no la acción y si hay
  excepciones por variante/regla de mesa.
- Confirmar diferencias entre `Call`, `Bet`, `Raise` y `AllIn`;
  especialmente si monto significa aporte incremental o objetivo total.
- Confirmar cantidad de manos antes de rotar H-O-R-S-E, botón y asientos
  iniciales, eliminación, número de jugadores y fichas de inicio.
- Confirmar odd chips en empate y en split high/low; orden para asignar
  fichas remanentes y posibles casos de empate de low.
- Confirmar devolución de monto no igualado antes de crear botes,
  y tratamiento de apuestas forzosas cuando una pila es insuficiente.
- Confirmar qué hace el repartidor si no quedan cartas, especialmente
  en las variantes Stud con muchos jugadores.
- Confirmar reglas de mesa sobre auto-avance cuando todos los participantes
  restantes están all-in o solo queda uno que podría apostar.
- Mantener separación: bote principal/secundario por aportes; división
  high/low se calcula **dentro de cada bote** y puede no existir low.
