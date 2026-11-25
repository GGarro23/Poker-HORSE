# Diseño propuesto del flujo de una mano — Chat 04

**Estado:** propuesta de diseño; NO establece APIs ni reglas de variante definitivas.  
**Rama:** `feature/motor`.  
**Base revisada:** `main@a914e80312e15bc1841086c8c5264be216d11114`, 25-11-2026.  
**Autoridad:** `poker.pdf` (pp. 1–8), `Poker_HORSE_Guia_Maestra.pdf` (pp. 4–6), `docs/CONTRATOS.md` y `docs/REGLAS.md`.

## 1. Situación y límites

En la base revisada, los paquetes `domain/`, `evaluation/`, `variants/`,
`betting/`, `engine/` y `bots/` todavía contienen solo marcadores
`.gitkeep`. El único código de `:core` es la utilidad de infraestructura
`CoreInfo` y su test. No existen aún interfaces definitivas de los chats 02 y 03.

Por eso este documento describe **responsabilidades, datos necesarios y
secuencia**, no declaraciones Kotlin compilables. No agregar contratos
compartidos ni implementar transiciones hasta conciliar este diseño con el
dominio (02), las estrategias HORSE (03) y el centro de control (00).

## 2. Flujo completo de una mano

Las etiquetas de estado siguientes son **conceptuales**, no nombres de clases
o enumeraciones aprobadas:

```text
Partida preparada
  -> seleccionar variante y configuración para mano N
  -> preparar mazo reproducible y mano vacía
  -> participantes / botón / asientos iniciales
  -> obligaciones forzosas (ciegas o antes/bring-in)
  -> reparto inicial definido por la estrategia
  -> determinar orden y primer actor elegible
  -> [calle de apuestas]
       |-- consultar acciones legales (sin efectos secundarios)
       |-- validar actor, estado, monto y condiciones
       |-- aplicar acción a una copia lógica del estado
       |-- si solo queda un no-retirado: resolución por folds
       |-- si quedan actores pendientes: seleccionar siguiente turno
       '-- si finalizó la calle: cerrar apuestas
              |-- si quedan calles: repartir próxima calle y continuar
              '-- si termina mano: showdown
                  (también auto-avance cuando nadie puede apostar)
  -> normalizar aportes y devolver excedentes no igualados
  -> construir bote principal y secundarios
  -> determinar ganadores elegibles por bote (high/low por estrategia)
  -> distribuir fichas, empates y fichas impares según regla aprobada
  -> verificar conservación de fichas
  -> registrar resultado y finalizar mano
  -> decidir continuación / eliminación / fin de partida
  -> actualizar botón y rotación H-O-R-S-E según configuración aprobada
  -> preparar siguiente mano
```

**Precisión:** la finalización por folds liquida sin evaluar cartas; el
showdown solo involucra participantes no retirados elegibles. El orden de
posteo, el reparto, las calles y los actores vienen de las estrategias
de variante, nunca de `if/when` por H/O/R/S/E dentro del motor.

## 3. Colaboradores y propiedad de responsabilidades

| Colaborador conceptual | Sí hace | No hace |
|---|---|---|
| `MotorDeJuego` (`engine/`) | Orquesta una transición, comprueba turno, decide cierre o avance y devuelve estado/errores | No compara cartas ni calcula directamente premios |
| `ReglasVariante` (03) | Describe calles, reparto, obligaciones, estructura de apuestas, orden de acción y forma de evaluar | No manipula UI ni gestiona el ciclo Android |
| Gestor de apuestas (`betting/`) | Calcula legalidad monetaria, call, subidas, límite fijo, cierres y reapertura | No reparte cartas |
| `CalculadorDeBotes` (`betting/`) | Construye botes a partir de aportes, elegibilidad y excedentes; asigna montos a ganadores proporcionados por dominio | No clasifica manos |
| Evaluador/resultado de variante (03) | Compara resultados high y low y resuelve empates según contrato | No ajusta pilas de jugadores |
| Proveedor de mazo | Entrega orden determinista o mezcla inyectable, sin duplicación de cartas | No decide ganadores |
| `EstrategiaDeBot` (`bots/`) | Propone acción legal a partir de vista autorizada y opciones legales | No accede a cartas ocultas ajenas ni aplica acciones sin motor |
| `EstadoMano` / `EstadoPartida` (02) | Conservan solo los datos necesarios y sus invariantes | No tienen lógica de presentación Android |

**Dependencia:** `:app -> :core`. `engine` conoce abstracciones de
`domain`, `variants`, `betting` y evaluación, nunca Android.
El avance debe preservar estado original ante error.

## 4. Datos que el motor necesita del dominio (solicitud al chat 02)

- Identidad estable (id y/o asiento, **sin imponer uno**), pila, aporte en
  calle, aporte total, condición activo/retirado/all-in.
- Mano y partida, calle actual, posición del actor, secuencia de jugadores,
  cartas ocultas/visibles, comunitarias y mazo restante.
- Cantidad objetivo de la calle, última subida completa y quién conserva
  derecho de volver a subir; pendientes de actuar y estado terminal.
- Tipo de acción con identidad del actor o mecanismo equivalente para que
  `aplicar` pueda impedir el juego fuera de turno.
- Resultado tipado de transición; resultado de showdown y distribución.
- Representación exacta de importes (base local usa `Long`, ejemplo académico
  usa `Int`): queda bloqueada hasta contrato del chat 02.

**No confundir:** monto de apuesta de la calle, aporte total en toda la mano,
pila disponible y dinero del bote. Acordar cuál es dato fuente y cuál es
derivado, para evitar discrepancias o contar fichas dos veces.

## 5. Colaboración requerida de las variantes (chat 03)

- Secuencia de calles y señal de última calle.
- Plan de reparto (propias ocultas, visibles, comunitarias, burn, etc.) por
  calle; no inferir del nombre de la variante.
- Obligaciones forzosas: ciegas, antes, bring-in y excepciones de short stack.
- Orden inicial y posterior de acción: puede depender de cartas expuestas;
  resolver por estrategia en cada nueva calle.
- Estructura fixed-limit por calle, tamaño de apuesta/subida, tope y
  excepciones aplicables.
- Resultado comparable: high, low opcional/calificado y reglas de división.
- Comportamiento determinista ante insuficiencia de cartas del mazo.

Las condiciones de Hold'em y Omaha (comunitarias y ciegas), Stud/Razz
(antes, bring-in, cartas visibles) y low calificable de Omaha/Stud Eight
se documentan como dirección de reglas en la guía, no como APIs aprobadas.

## 6. Legalidad y transición

`accionesLegales(estado)` se comportará como consulta pura: misma entrada,
mismo resultado y sin mezclar, apostar, asignar turno ni modificar pilas.
No se determina todavía si su resultado será un conjunto de instancias
`Accion` o descriptores de acción con montos permitidos; los montos de
`Bet`/`Raise`/`AllIn` exigen contrato previo.

`aplicar(estado, actor, accion)` se presenta solo como secuencia conceptual
(no como firma final):

1. Validar que existe mano activa y que el actor coincide con el turno.
2. Rechazar retirados, all-in y acciones incompatibles con la calle actual.
3. Validar tipo, monto, fichas disponibles, obligación de igualar y límite
   fixed-limit con colaborador de apuestas.
4. Si no es válida, devolver error de dominio sin cambiar ninguna entrada.
5. Si es válida, construir copia con pila/aportes/estado actualizados.
6. Actualizar pendientes, última subida completa, derecho de reabrir y
   siguiente actor; **una subida incompleta all-in no reabre por sí sola**.
7. Evaluar si queda un contendiente o terminó la ronda. Si termina, avanzar
   con la estrategia; si es última calle, resolver showdown.
8. Comprobar las invariantes y devolver nuevo estado.

Para una ronda cerrada se requiere que toda persona que **puede actuar**
haya respondido a la última apuesta/subida completa aplicable y tenga
aporte igualado o esté all-in/retirada. Los all-in no reciben nuevos turnos.
Si ya no quedan dos jugadores capaces de apostar, las calles restantes
pueden repartirse automáticamente, conservando el plan de variante.

**Apuestas a revisar:** check si no hay monto pendiente; call por el mínimo
entre deuda y pila; fold en turno; bet solo si la calle lo permite; raise
con tamaño de fixed-limit o excepción all-in menor; all-in no es un permiso
para ignorar la estructura de apuestas. El número máximo de subidas y la
semántica exacta de `Raise.monto` están pendientes de aprobación.

## 7. Botes y conservación

Propuesta de algoritmo puro:

1. Partir de aportes **totales de mano** no negativos, incluidos retirados.
2. Ordenar los niveles de aporte positivos, sin repetir.
3. Crear capas: para cada nivel `L`, cantidad
   `(L - nivelAnterior) * participantesConAporteAlMenos(L)`.
4. Una capa respaldada por un único aportante es **exceso no igualado**:
   devolverla antes de liquidar; no convertirla en premio disputado.
5. Cada bote disputado identifica elegibles que alcanzaron su nivel y
   **no** estén retirados. El aporte del retirado permanece en el importe.
6. Por bote se compara únicamente a elegibles; si no existe low calificado
   todo corresponde a high; si existe, se hace split según regla de variante.
7. Resolver empates y fichas impares por política explícita aprobada.
8. Verificar que `suma(pilas finales) = totalDeFichasIniciales` si no hay
   compras, comisiones ni nuevas fichas. Durante una mano,
   `suma(pilas) + suma(aportesNoLiquidados)` debe permanecer constante.
   Devoluciones y premios no se cuentan dos veces.

**Nota importante:** no confundir botes secundarios con distintos botes
high/low. Primero se crean los botes disputables por aportes y elegibilidad;
**cada uno** puede distribuirse high/low de forma independiente.

## 8. Bots y privacidad

El motor debe ofrecer una **vista del actor** que incluya sus cartas,
cartas públicas/visibles, aportes, pilas y acciones legales, pero no cartas
privadas de otros participantes ni orden futuro del mazo. El bot selecciona
una propuesta simple (por ejemplo, check > call permitido > fold; con
aleatoriedad inyectada solo si se necesita variación). El motor valida
la propuesta exactamente como cualquier acción humana.

## 9. Pruebas mínimas por incremento (con mazos deterministas)

| Hito | Casos verificables |
|---|---|
| M0: rechazo y turno | Fuera de turno, estado terminal, acción ilegal; anterior intacto |
| M1: ronda mínima | Check/check; bet/call; fold; alternancia y cierre de calle |
| M2: apuestas fixed-limit | Call parcial; bet/raise correctos; cap; subir sin fichas; all-in incompleto y no reapertura |
| M3: botes | 100/200/500, retirado aportante, dos side pots, exceso devuelto, empate y odd chip |
| M4: cierre | Ganador por folds, showdown high, low calificado y no calificado, split por bote |
| M5: variantes | Cinco estrategias bajo mismo motor, orden Stud/Razz, ciclo H-O-R-S-E |
| M6: bots/reproducibilidad | Misma semilla/mazo produce mismo resultado; bot no observa cartas ocultas; acción revalidada |
| Transversal | Sin pila negativa, sin carta duplicada, ni fichas perdidas/creadas, sin mutar estado anterior |

No declarar ninguna prueba **ejecutada** por el mero hecho de enumerarla.

## 10. Dependencias y desbloqueo para implementar

1. **Chat 02:** estructura de `EstadoMano`, `EstadoPartida`, identidad,
   dinero, acción, error, mutabilidad/control de colecciones.
2. **Chat 03:** contrato de `ReglasVariante`, calles, reparto, apuesta fija,
   orden y salida de evaluación high/low.
3. **Chat 00:** resolución de los acuerdos compartidos aún ambiguos y
   autorización para ajustar interfaces comunes.
4. **Chat 04:** empezar después con `M0`, una transición mínima y pruebas.
   Continuar por `M1 -> M2 -> M3 -> M4 -> M5 -> M6` en ramas cortas.

Las reglas y decisiones sin cerrar se registran también en
`docs/REGLAS.md` y `docs/DECISIONES.md`. Esta rama no se debe integrar
como aprobación automática de dichas propuestas.
