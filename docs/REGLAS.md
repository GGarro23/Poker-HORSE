# Reglas HORSE — referencia verificada

Estado: documentación de reglas convencionales para el chat 03; **sin implementación Kotlin todavía**.
Fecha de verificación: 25 de noviembre de 2026.
Responsable: 03 — Evaluadores y variantes HORSE.

## 1. Niveles de autoridad

1. **Requisito académico (Eddy):** `poker.pdf` exige demostrar cinco estrategias polimórficas, reparto, apuestas, orden de acción y evaluación diferenciada; sus ejemplos de interfaces son orientativos y no especifican exhaustivamente el reglamento.
2. **Referencia convencional externa:** reglas de HORSE y sus variantes publicadas por PokerStars [R1–R6] y escala ace-to-five de BARGE [R7]. Estas reglas orientan la implementación; no se atribuyen al profesor.
3. **Decisión propia del proyecto:** cualquier configuración simplificada o caso límite elegido debe aprobarse mediante el chat 00 y registrarse en `docs/DECISIONES.md`; los contratos Kotlin definitivos serán los de `docs/CONTRATOS.md` después del chat 02.

No utilizar esta documentación para dar por implementadas funciones o contratos.

## 2. Estructura general

- HORSE: **H** Fixed Limit Texas Hold'em; **O** Fixed Limit Omaha Hi-Lo (Eight-or-Better); **R** Razz; **S** Seven Card Stud High; **E** Seven Card Stud Hi-Lo (Eight-or-Better). En la referencia [R1] los límites fijos se mantienen durante la rotación.
- Hold'em y Omaha usan cartas privadas y comunitarias, ciegas y botón.
- Razz, Stud y Stud Hi-Lo usan ante, bring-in y cartas personales parcialmente visibles. En el reparto normal **no tienen cartas comunitarias** [R1].
- **Regla de desempate:** los palos no desempatan manos de showdown. El orden de palos puede servir para determinar bring-in de Stud/Razz [R3–R5]; no debe contaminar el comparador de manos.

## 3. Evaluación

### Alta tradicional

Categorías en orden descendente: escalera de color, póker, full house, color, escalera, trío, doble pareja, pareja, carta alta. La escalera A-2-3-4-5 tiene valor máximo de cinco; el as también puede cerrar 10-J-Q-K-A con valor máximo de catorce. Todos los empates se resuelven comparando primero la categoría y después los valores relevantes de la combinación (incluidos kickers).

### Baja ace-to-five

- El as vale uno; **escaleras y colores no perjudican** la mano baja.
- Cinco rangos distintos se comparan de mayor a menor, y gana el vector lexicográficamente más bajo. Ejemplo: 7-5-4-2-A supera 7-6-3-2-A.
- A-2-3-4-5 es el mejor low.
- **Eight-or-Better:** exige cinco valores distintos, todos de ocho o menos. Si no califica, no existe resultado low para ese jugador.
- **Razz:** no exige Eight-or-Better. Una mano sin parejas es mejor que cualquier mano emparejada. Si ninguna combinación de cinco cartas evita pareja, se comparan las manos emparejadas; la pareja menor es mejor (p. ej., A-A-7-6-5 supera 2-2-5-4-3) [R3, R6, R7].
- High y low pueden utilizar distintas combinaciones legales de cinco cartas.

## 4. Particularidades de las cinco estrategias

| Variante | Reparto normal | Selección de 5 cartas | Orden inicial y posterior |
| --- | --- | --- | --- |
| **H** Texas Hold'em | 2 privadas; flop 3, turn 1, river 1 comunitarias | Mejor alta entre las 7 disponibles | Preflop: siguiente a ciega grande; postflop: primer activo tras botón |
| **O** Omaha Hi-Lo | 4 privadas; flop 3, turn 1, river 1 comunitarias | **Exactamente 2 privadas + 3 comunitarias** por cada evaluación high y low, posiblemente selecciones distintas; low Eight-or-Better | Igual que H |
| **R** Razz | Third Street: 2 ocultas + 1 visible; Fourth, Fifth, Sixth: 1 visible c/u; Seventh: 1 oculta | Mejor low ace-to-five entre sus 7 cartas; sin calificador | Third: carta visible **más alta** pone bring-in; después: mejor (menor) mano visible |
| **S** Seven Card Stud | Mismo reparto stud | Mejor alta entre sus 7 cartas | Third: carta visible **más baja** pone bring-in; después: mejor (mayor) mano visible |
| **E** Stud Eight-or-Better | Mismo reparto stud | Mejor alta y mejor low Eight-or-Better entre sus 7 cartas; selecciones independientes | Como S (bring-in más bajo, después mano visible más alta) |

Reglas de H y O: [R2, R6]. Reglas de R: [R3]. Reglas de S: [R4]. Reglas de E: [R5].

### Apuestas de límite fijo

- H/O: apuesta pequeña en preflop y flop; apuesta grande en turn y river; en las reglas de PokerStars se permiten normalmente hasta cuatro apuestas por ronda (apuesta inicial y tres subidas) [R2, R6].
- R/S/E: apuesta pequeña en Third y Fourth Street; apuesta grande desde Fifth hasta Seventh [R3–R5].
- **Excepción S:** si en Fourth Street alguien muestra una pareja, se permite abrir/subir con apuesta grande; si se usa, esa calle sigue con importe grande [R4].
- **Excepción NO aplicable E:** Stud Eight-or-Better **no** permite duplicar la apuesta en Fourth Street por pareja visible [R5]. En Razz se conserva la apuesta pequeña de Fourth Street [R3].
- El importe concreto de ciegas, ante, bring-in, apuesta pequeña/grande y el número máximo de subidas debe definirse en `ConfiguracionPartida`, no quedar hardcodeado en los evaluadores.

### Bring-in, empates y agotamiento del mazo

- Para el bring-in del Stud alto y Stud E, si dos cartas visibles tienen el mismo rango se escoge el palo **menor**; para Razz se escoge el palo **mayor**. Convención de orden creciente: tréboles, diamantes, corazones, picas [R3–R5].
- El orden del bring-in no equivale al orden para determinar el ganador al showdown: **en el showdown no existe jerarquía de palos**.
- En Stud con muchos participantes puede faltar mazo para repartir individualmente Seventh Street. La regla de referencia puede colocar una única carta comunitaria excepcional compartida por quienes continúan [R3–R5]. Este caso NO está resuelto aún en la implementación y requiere decisión de arquitectura/configuración antes de codificarlo.

## 5. Decisiones propias pendientes de aprobación en el chat 00

1. Número mínimo y máximo de jugadores, con especial atención al mazo de 52 cartas y el reparto Stud.
2. Si la rotación HORSE ocurre tras una cantidad fija de manos u órbitas; la referencia [R1] describe rondas de cada juego y congela botón al pasar de Omaha a Razz.
3. Cantidades de ante, bring-in, ciegas y apuestas, y política de incrementos/cap de subidas en heads-up o all-in.
4. Tratamiento de cartas quemadas, desempates de manos visibles para determinar actor, agotamiento del mazo y Seventh Street comunitaria excepcional.
5. Liquidación de mitades, botes secundarios, fichas impares, empates, y devolución de excedentes: **responsabilidad del chat 04**. El chat 03 entrega resultados comparables, no distribuye fichas.
6. Interfaz concreta de `ReglasVariante`, `ResultadoEvaluacion`, `Calle`, `PlanDeReparto` y `EstructuraApuestas`: depende de la aprobación del chat 02.

## 6. Pruebas de aceptación previstas para el chat 03

- Nueve categorías altas, desempates de todas ellas, escalera con as bajo, coincidencia de valores con diferentes palos.
- Low ace-to-five sin parejas, comparaciones de la segunda/tercera carta, manos con parejas para Razz, Eight-or-Better calificado/no calificado.
- Selección de mejor combinación de cinco entre siete; restricción exacta Omaha 2+3 para high y low independientes.
- Planes de reparto, visibilidad y calles de las cinco variantes.
- Orden inicial y posterior de acción de flop games/Stud/Razz; desempate de bring-in por palo.
- Apuestas fijas y diferencias de Fourth Street S vs E.
- Sustitución de las cinco estrategias a través de un mismo contrato `ReglasVariante`.
- Tests deterministas sin interfaz ni imports de Android.

## 7. Referencias externas verificadas

- [R1] PokerStars, HORSE: https://www.pokerstars.com/poker/games/horse/
- [R2] PokerStars, Omaha Hi/Lo: https://www.pokerstars.com/poker/games/omaha/high-low/
- [R3] PokerStars, Razz: https://www.pokerstars.com/poker/games/razz/
- [R4] PokerStars, Seven Card Stud: https://www.pokerstars.com/poker/games/stud/
- [R5] PokerStars, Seven Card Stud Hi/Lo: https://www.pokerstars.com/poker/games/stud/high-low/
- [R6] PokerStars, Texas Hold'em: https://www.pokerstars.com/poker/games/texas-holdem/
- [R7] BARGE Rulebook, Appendix A, Ace-to-Five Lowball: https://www.barge.org/rulebook/lowball-scales.html

> Alcance: reglas verificadas y casos pendientes; **no implica tests ejecutados ni estrategias implementadas**.
