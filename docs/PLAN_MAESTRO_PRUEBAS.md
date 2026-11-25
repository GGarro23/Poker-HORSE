# Plan maestro de pruebas — Chat 07

**Versión inicial:** 2026-11-25.  
**Estado:** PLANIFICADO; ninguna prueba nueva ejecutada por el chat 07.  
**Autoridad académica:** `poker.pdf`, pp. 1–8. **Alcance convencional propuesto:** `Poker_HORSE_Guia_Maestra.pdf`, `docs/REGLAS.md` y decisiones aprobadas por 00.  
**Base:** `main@a914e80312e15bc1841086c8c5264be216d11114`.

## 1. Reglas de operación

Cada caso debe tener: ID estable, objetivo, fixture determinista, estado inicial, acción/entrada, oráculo esperado, test automatizado o manual, commit, comando, entorno y resultado. Usar estados **PLANIFICADA**, **IMPLEMENTADA_NO_EJECUTADA**, **PASÓ**, **FALLÓ**, **BLOQUEADA**. Solo PASÓ/FALLÓ con evidencia de ejecución. No marcar como PASÓ por inspección ni por resultados de otro commit.

**P0:** fallos que impiden demostrar correctamente los conceptos de mayor peso, corrompen fichas/estado o exponen cartas ocultas. **P1:** reglas específicas y reproducibilidad. **P2:** UX/accesibilidad y casos amplios de robustez.

## 2. Catálogo de casos (todos PLANIFICADOS)

| ID | Prio. | Nivel | Caso / entrada adversarial | Oráculo a verificar |
|---|---|---|---|---|
| DOM-01 | P0 | core | Construir jugador con pila -1 | Error de dominio o construcción rechazada |
| DOM-02 | P0 | core | Aporte calle mayor que total; aporte negativo | Estado inválido no representable |
| DOM-03 | P0 | core | Reutilizar una misma carta entre jugadores/mesa/mazo | Estado de mano inválido rechazado |
| DOM-04 | P0 | core | Mutar lista original usada por un estado, después de construirlo | Estado publicado no cambia |
| DOM-05 | P1 | core | Mazo canónico y extracción de 52 | 52 naipes únicos, sin reposición |
| DOM-06 | P1 | core | Repartos con semilla o mazo prefijado idénticos | Secuencias idénticas |
| DOM-07 | P0 | core | Estado terminal recibe nueva acción | Rechazo sin modificación |
| DOM-08 | P1 | core | Identidades y asientos repetidos | Unicidad/semántica del contrato preservada |
| EVAL-01 | P0 | core | Nueve categorías high | Clasificación y orden correctos |
| EVAL-02 | P0 | core | Misma categoría, diferencias en kicker | Ganador correcto por comparador completo |
| EVAL-03 | P0 | core | A-2-3-4-5 vs 2-3-4-5-6; 10-J-Q-K-A | Wheel vale 5; broadway vale 14 |
| EVAL-04 | P0 | core | Manos high idénticas con palos diferentes | Empate; palo no rompe igualdad de showdown |
| EVAL-05 | P0 | core | Mejor cinco entre siete; cartas distracción | Se selecciona combinación ganadora |
| LOW-01 | P0 | core | A-2-3-4-5 vs 7-5-4-2-A | Wheel vence; comparar high card del low al revés |
| LOW-02 | P0 | core | 7-5-4-2-A vs 7-6-3-2-A | 7-5... es mejor low |
| LOW-03 | P0 | core | Low de ocho con 5 rangos <=8 frente a mano con 9 | Primera califica; segunda no |
| LOW-04 | P0 | core | Colores/escaleras en ace-to-five | No invalidan low |
| LOW-05 | P1 | core | Razz con pareja obligatoria en las cinco | Resultado y desempate según reglas acordadas |
| OMA-01 | P0 | core | 1 propia + 4 mesa formarían nuts; 2+3 no | No permite selección 1+4 |
| OMA-02 | P0 | core | 3 propias + 2 mesa aparentan mejor mano | No permite selección 3+2 |
| OMA-03 | P0 | core | High y low ganan con elecciones diferentes 2+3 | Cada mitad evaluada legal e independientemente |
| OMA-04 | P0 | core | Low imposible con exactamente 2 propias | No crea low por usar 1 carta privada |
| VAR-01 | P0 | core | Cinco estrategias usadas por misma API | Cada variante implementa reparto/orden/evaluación propios |
| VAR-02 | P0 | core | Inyectar estrategia fake/sustituta | Motor trabaja sin cambiar ramas por variante |
| VAR-03 | P1 | core | Hold'em y Omaha: preflop vs postflop | Orden de acción correcto |
| VAR-04 | P1 | core | Razz Third: expuesta más alta; Stud Third: más baja | Bring-in correcto, incluido desempate autorizado |
| VAR-05 | P1 | core | Stud/Razz orden de Fourth–Seventh por visibles | Nuevo orden por calle |
| VAR-06 | P1 | core | Seven Card Stud Fourth con pareja expuesta | Apuesta opcional grande solo si reglas aprobadas lo permiten |
| VAR-07 | P1 | core | Stud Eight Fourth con pareja expuesta | No heredar automáticamente excepción de Stud High |
| VAR-08 | P0 | core | Rotación H–O–R–S–E y vuelta a H | Secuencia y frecuencia **según decisión registrada** |
| VAR-09 | P1 | core | Mazo no alcanza para Seventh Street de Stud | Resolución determinista según decisión registrada |
| ENG-01 | P0 | core | Jugador equivocado intenta actuar | Rechazo, estado original igual |
| ENG-02 | P0 | core | Check cuando existe deuda | Acción ilegal rechazada |
| ENG-03 | P0 | core | Call, bet, raise con monto negativo/cero/no permitido | Rechazo y conservación |
| ENG-04 | P0 | core | Raise supera pila o cap fixed-limit | Rechazo o ajuste expresamente contratado |
| ENG-05 | P0 | core | Check/check y bet/call; secuencia completa | Calle finaliza justo cuando corresponde |
| ENG-06 | P0 | core | Fold deja un solo contendiente | Premio por fold sin showdown |
| ENG-07 | P0 | core | All-in parcial menor que deuda | Pila cero, aportes correctos, side pot posible |
| ENG-08 | P0 | core | All-in de subida incompleta y acción previa | No reapertura, salvo regla aprobada |
| ENG-09 | P0 | core | Todos salvo uno all-in; autoavance | Sin turnos espurios; reparto restante correcto |
| ENG-10 | P1 | core | Límite de raises alcanzado y heads-up | Comportamiento conforme a configuración |
| POT-01 | P0 | core | A=100, B=200, C=500 contribuidos | Main 300; side 200; excedente 300 a C; suma 800 |
| POT-02 | P0 | core | Retirado aporta a los botes, tiene mejor mano | Aporte permanece, no puede ganar |
| POT-03 | P0 | core | Múltiples all-in y aportes escalonados | Elegibles y capas correctas por cada bote |
| POT-04 | P0 | core | Empate exacto high dentro de un bote | Divide sin duplicar/perder fichas |
| POT-05 | P0 | core | Hi-Lo sin mano low calificada | Todo el bote a high |
| POT-06 | P0 | core | Hi-Lo con high y low en distintos ganadores | División por bote, no global |
| POT-07 | P0 | core | Side pot con high/low diferente del main | Cada bote resuelto independientemente |
| POT-08 | P0 | core | Bote impar o varios ganadores empatados | Odd-chip asignado conforme política aprobada |
| POT-09 | P0 | core | Un jugador gana tanto high como low | Scoop correcto, sin premio duplicado |
| POT-10 | P0 | core | Cualquier transición válida de una mano | Suma(pilas)+suma(aportes pendientes) constante hasta liquidar |
| POT-11 | P0 | core | Finalización y devolución de excedentes | Suma(pilas finales) = suma(pilas iniciales) |
| BOT-01 | P0 | core | Bot observa estado que contiene cartas privadas ajenas | Vista pública nunca expone las privadas |
| BOT-02 | P0 | core | Bot devuelve acción ilegal | Motor la rechaza exactamente como acción humana |
| BOT-03 | P1 | core | Mismo estado y dependencias deterministas | Comportamiento reproducible |
| UI-01 | P0 | Android | Instalar APK y completar una mano de cada variante | Operación real, no presentación estática |
| UI-02 | P0 | Android | Acciones habilitadas/deshabilitadas por estado | No ofrece acciones que motor rechazaría |
| UI-03 | P0 | Android | Rotar dispositivo vertical↔horizontal durante mano | Estado y controles se conservan, no duplicación |
| UI-04 | P0 | Android | Antes y después de showdown, ver cartas bots | No hay filtración antes de revelar |
| UI-05 | P1 | Android | Repetir tap de apuesta / rotar durante acción | No duplica transiciones ni fichas |
| UI-06 | P1 | Android | Texto largo, pantalla chica y orientación distinta | Cartas y botones legibles, sin elementos inaccesibles |
| INT-01 | P0 | integración | ViewModel llama `accionesLegales`/motor | La UI no recalcula reglas en Compose |
| INT-02 | P0 | integración | Estado visible construido desde estado de juego | No se filtra mazo ni manos privadas |
| INT-03 | P0 | integración | Error del motor devuelve rechazo tipado | UI recupera y no corrompe partida |
| BUILD-01 | P0 | build | Checkout limpio, `:core:test`, `:app:assembleDebug` | Build reproducible y artefacto válido |
| BUILD-02 | P0 | físico | `adb devices`, instalación y apertura | APK ejecuta en dispositivo real |
| DEF-01 | P0 | manual | Cada integrante localiza y explica un archivo real | Puede justificar invariantes, colaboración y contrato |
| DEF-02 | P0 | manual | Demo de ~20 min con cinco variantes + caso adversarial | Recorrido sin diapositivas, evidencia en ejecución |

## 3. Fixtures prioritarios

### F1 — Three-way all-in 100 / 200 / 500

A aporta 100, B 200, C 500. El algoritmo devuelve a C sus 300 no igualados. **Bote principal = 300 (A/B/C)** y **bote secundario = 200 (B/C)**. Antes de distribuir: 300 + 200 + 300 devueltos = 800. Construir también la versión donde A se retira tras aportar y no figura entre elegibles.

### F2 — Omaha ilegal por atajo

Definir 4 cartas privadas y 5 comunitarias de forma que la mejor mano global surja usando 1 privada o 3 privadas, mientras la mejor de exactamente **2 privadas + 3 comunitarias** sea inferior. Validar tanto high como low.

### F3 — Split con side pot

Múltiples niveles de apuesta, un retirado y dos ganadores distintos para high/low en el principal, otros ganadores en el secundario. Garantizar que **cada** bote tenga elegibilidad y split independientes.

### F4 — Trazas deterministas

Mazo prefijado, secuencia de actores y acciones; guardar estado final, orden de reparto, premios y hash/representación serializada estable (si el contrato lo permite). Repetir sin azar oculto.

### F5 — Rechazo inmune a mutación

Capturar snapshot profundo del estado y ejecutar acciones fuera de turno, negativas, overbet y estado terminal. Comparar antes y después, incluso listas compartidas.

## 4. Propiedades generales

- **Conservación:** durante la mano, fichas en pilas + aportes todavía retenidos = capital inicial; después del reparto, pilas finales = capital inicial si no hay recompras/rake.
- **Legalidad:** solo puede actuar el participante en turno y en estado elegible.
- **Pureza de consulta:** `accionesLegales` no modifica estado ni orden del mazo.
- **Rechazo atómico:** entrada inválida nunca deja estado parcial.
- **Unicidad:** ninguna carta puede aparecer dos veces en manos/mesa/mazo activo.
- **Privacidad:** vistas de UI/bot nunca incluyen cartas ocultas de adversarios ni orden del mazo.
- **Sustitución:** añadir otra `ReglasVariante` no exige modificar las ramas del motor.

## 5. Ejecución y evidencia

Primero confirmar contratos y presencia del código. Luego crear clases `*Test.kt` en `core/src/test/kotlin/` o `app/src/test/` y pruebas instrumentadas en `app/src/androidTest/`. Ejecutar en un checkout del commit auditado.

Comandos objetivo (no ejecutados por chat 07):

```bash
git rev-parse HEAD
java -version
./gradlew :core:test :app:testDebugUnitTest :app:assembleDebug --rerun-tasks
adb devices
./gradlew :app:connectedDebugAndroidTest
```

Registrar en `docs/REGISTRO_PRUEBAS.md` **por sesión**: fecha/hora, autor, SHA exacto, rama, Java/Gradle/Android SDK, dispositivo, comando literal, número de tests, PASÓ/FALLÓ/omitidas, logs y ruta de reportes XML/HTML, defectos y fecha de reejecución. Si un comando no pudo ejecutarse, poner **NO EJECUTADO/BLOQUEADA** con motivo; no inventar datos.

## 6. Secuencia de ejecución

| Fecha objetivo | Puerta de calidad |
|---|---|
| 25–26 nov. | Aprobación de contratos; DOM, EVAL, LOW; construir fixtures |
| 27 nov. | H/O, OMA, VAR básicos, integración de acción inicial |
| 28 nov. | R/S, apuestas fijas, POT (side pots), múltiples all-in |
| 29 nov. | Stud E, low, rotación, bots, privacidad |
| 30 nov. | Regresión de cinco variantes y pruebas de sustitución |
| 1 dic. | Checkout limpio, APK, pruebas físicas en ambas orientaciones, ensayo de defensa |
| 2 dic. | Smoke de entrega, SHA/tag y dispositivos listos antes de la cita |

La progresión depende de integración de otros chats; no adelantar estado de PASÓ sin ejecución.

## 7. Preparación del quiz y defensa

Una pregunta de defensa solo se considera dominada cuando la persona: (1) abre el archivo real, (2) identifica método/contrato, (3) explica responsabilidad y aquello que NO hace, (4) sigue un ejemplo concreto, y (5) describe una prueba que detectaría un bug.

Banco de preguntas **a responder con código real**, no memorizar definiciones:
- ¿Dónde se valida que una acción fuera de turno sea atómica?
- ¿Cómo se impide mutar la pila por fuera del motor? ¿Alcanza `val` si hay listas mutables?
- ¿Qué ocurre si inyecto un `ReglasVariante` fake?
- ¿Por qué `sealed interface Accion` no demuestra por sí mismo el polimorfismo de variantes?
- ¿Por qué Omaha no puede usar una carta propia y cuatro comunitarias?
- ¿Cómo se calcula POT-01 y cuándo se devuelven 300 fichas?
- ¿Qué separa la liquidación high/low de la creación de side pots?
- ¿Por qué el ViewModel no debería importar un evaluador para decidir ganadores?
- ¿Cómo se reproduce la misma mano dos veces?
- ¿Qué archivos cambiarían si añadiéramos una sexta variante?

**Guion de demo propuesto (~20 min):** 0–2 arquitectura y APK; 2–9 cinco variantes; 9–13 acciones ilegales y apuestas/all-in; 13–16 side pots, split y conservación; 16–18 tests reproducibles; 18–20 preguntas individuales. Adaptar al comportamiento realmente implementado.

## 8. Comunicación y correcciones

Fallo en contrato/modelo -> 02; evaluación/variante -> 03; apuestas/transición/bots -> 04; Android -> 05; arte/legibilidad -> 06. Decisiones compartidas, integración y prioridades -> 00. Chat 07 puede agregar tests y documentación en `feature/integration-tests`, pero no modificar contratos de producción sin 00.
