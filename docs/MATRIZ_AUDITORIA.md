# Matriz de auditoría académica — Chat 07

**Fecha:** 2026-11-25 (Costa Rica)  
**Autoridad:** `poker.pdf`, pp. 1–8 (rúbrica oficial pp. 7–8).  
**Arquitectura y coordinación:** `Poker_HORSE_Guia_Maestra.pdf` y documentación versionada.  
**Repositorio:** `GGarro23/Poker-HORSE`. **Snapshot de main:** `a914e80312e15bc1841086c8c5264be216d11114`.  
**Ramas inspeccionadas:** `main`, `feature/motor`, `feature/variantes-horse`.

> Auditoría estática del repositorio remoto. No se descargó/compiló/ejecutó el proyecto en esta auditoría. `docs/REGISTRO_PRUEBAS.md` contiene resultados reportados por chat 01; no son una ejecución independiente de chat 07.

## Códigos de evidencia y estados

- **V (verificado estáticamente):** archivo/estructura y contenido inspeccionados en GitHub.
- **R (reportado):** ejecución descrita en un registro anterior; falta reproducirla aquí.
- **P (pendiente):** comportamiento sin implementación o sin evidencia.
- **F (fallido):** hay una prueba real ejecutada y con fallo documentado.
- **NA (no evaluable):** contrato todavía no cerrado o escenario no implementado.

No dar puntos por nomenclatura, `.gitkeep`, documentación conceptual ni tests sin aserciones de negocio.

## Matriz por rúbrica oficial

| Criterio | Peso | Evidencia a exigir | Situación a fecha de corte | Estado | Prioridad |
|---|---:|---|---|---|---|
| Abstracción y modelo de dominio | 12 | `Carta`, jugador, mano y contratos de responsabilidades reales; pruebas de comportamiento | En `core` solo existe `CoreInfo.kt`; `domain/` contiene `.gitkeep` | P | P0 |
| Encapsulamiento e invariantes | 13 | Constructores/operaciones válidas; pila no negativa, aportes consistentes, estados sin modificación externa | No hay dominio ni pruebas correspondientes en `main` | P | P0 |
| Polimorfismo HORSE | 20 | Cinco `ReglasVariante` concretas; motor las usa sin `when` por variante; sustitución con fake | `variants/` contiene `.gitkeep`; rama `feature/variantes-horse` documenta reglas sin Kotlin | P | P0 |
| Composición y responsabilidades | 15 | Motor delega evaluación/botes/mazo; app no computa apuestas ni ganadores | `implementation(project(":core"))` y consumo de `CoreInfo.mensaje()` verifican **solo acoplamiento de infraestructura**; sin colaboración de dominio | V parcial / P funcional | P0 |
| Diseño y acoplamiento | 10 | SOLID con evidencia de código, sustitución y pruebas; no jerarquías decorativas | Guías en docs, pero no implementaciones de negocio | P | P1 |
| Motor, apuestas y botes | 15 | Legalidad, turnos, side pots, all-in, fold, showdown, conservación y rechazos sin mutación | `engine/` y `betting/` vacíos; rama `feature/motor` tiene `docs/MOTOR_FLUJO.md` conceptual | P | P0 |
| Interfaz móvil y usabilidad | 8 | Partida ejecutándose en dispositivo; acciones legales; orientación vertical/horizontal | `MainActivity.kt` presenta splash de integración. Registro previo reporta APK, instalación y mensaje, no gameplay ni giro | R parcial / P funcional | P0 |
| Pruebas y defensa | 7 | Tests polimórficos e invariantes; defensores localizan y explican código específico | `CoreInfoTest.mensajeCorrecto` existe; registro declara 1/1; tests Android de plantilla no prueban HORSE | R parcial / P funcional | P0 |

**Puntaje académico exacto:** no asignado; todavía no hay evidencia suficiente ni defensa realizada.  
**Estimación conservadora si se entregara el snapshot como producto terminado:** 0–10/100, orientativa y no calificatoria, porque solo se puede justificar infraestructura y una pantalla provisional; **no proyecta la calificación de la entrega del 2 de diciembre**.

## Evidencia técnica inspeccionada

- `settings.gradle.kts`: incluye `:app` y `:core` (**V**).
- `app/build.gradle.kts`: depende de `:core` (**V**).
- `core/build.gradle.kts`: Kotlin/JVM, JUnit y `kotlin("test")` (**V**).
- `core/src/main/kotlin/cr/ac/tec/pokerhorse/core/CoreInfo.kt`: mensaje de infraestructura (**V**).
- `core/src/test/kotlin/cr/ac/tec/pokerhorse/core/CoreInfoTest.kt`: aserción de texto (**V**, resultado **R**).
- `app/src/main/java/cr/ac/tec/pokerhorse/MainActivity.kt`: interfaz provisional y lectura del mensaje (**V**).
- `app/src/test/.../ExampleUnitTest.kt`: `2 + 2 = 4`, plantilla (no evidencia de reglas) (**V**).
- `app/src/androidTest/.../ExampleInstrumentedTest.kt`: verifica packageName, plantilla (no evidencia de UX) (**V**).
- `docs/REGISTRO_PRUEBAS.md`: informa `:core:test` 1/1, compilaciones Gradle exitosas e instalación en Samsung SM-S931B con Android 16, API 36 (**R**).
- `feature/motor/docs/MOTOR_FLUJO.md`: flujo conceptual, expresamente sin APIs finales (**V**).
- `feature/variantes-horse/docs/REGLAS.md`: reglas convencionales y decisiones pendientes, expresamente sin código (**V**).
- `docs/CONTRATOS.md`: contratos definitivos aún pendientes (**V**).

## Hallazgos priorizados (no confundir ausencia futura con bug reproducido)

| ID | Gravedad | Hallazgo | Impacto | Acción y responsable |
|---|---|---|---|---|
| A-01 | Bloqueante para entrega | No existe dominio funcional en main | 12% + 13% y dependencias de todo el juego | Chat 02 + 00: cerrar contratos, integrar código y tests |
| A-02 | Bloqueante para entrega | Ninguna estrategia HORSE está implementada | 20% + motor | Chat 03: cinco implementaciones y evidencia de sustitución |
| A-03 | Bloqueante para entrega | Motor, apuestas y botes no existen | 15%, invariantes, jugabilidad | Chat 04: implementar por incrementos M0–M6 |
| A-04 | Bloqueante para entrega | Android solo demuestra CoreInfo, no una mano jugable | 8% + demostración presencial | Chat 05: corte vertical real y prueba física |
| A-05 | Alta | La prueba `CoreInfoTest` no prueba ninguna regla; tests Android son plantillas | 7% y confianza de regresión | Chats 02/03/04 + 07: tests de negocio ejecutables |
| A-06 | Alta | Reglas aún con decisiones abiertas: importes, subida, fichas impares, rotación, agotamiento, identidades | Riesgo transversal | Chat 00 coordina 02/03/04; documentar decisión y tests |
| A-07 | Media | No consta prueba horizontal ni pruebas de UI sobre acciones legales | 8% y calidad de demostración | Chat 05/07, dispositivo real |

Los `docs/` contienen propuestas, no prueba de ejecución.

## Condiciones de aceptación por criterio

1. **12%:** tipos reales, invariantes y responsabilidades respaldadas por tests; no clases decorativas.
2. **13%:** estados inválidos rechazados en constructor y transición, sin mutación compartida accidental.
3. **20%:** cinco estrategias conectadas por el mismo contrato; mock/fake intercambiable sin editar motor.
4. **15% composición:** colaboración real inyectada y ausencia de reglas en Compose.
5. **10%:** evidencia de SRP/OCP/LSP/ISP/DIP con archivo y método; explicación de elecciones.
6. **15% motor:** casos adversariales de side pots y conservación, incluidos folds y all-in.
7. **8%:** APK real, cinco modalidades, giros vertical/horizontal, control legal de botones y cartas privadas.
8. **7%:** evidencia de tests ejecutados con commit/entorno, prueba reproducible y defensa individual.

## Protocolo de reauditoría

- Registrar SHA exacto de cada rama/merge e inspeccionar producción y tests por nombre de archivo/método.
- Ejecutar `./gradlew :core:test :app:testDebugUnitTest :app:assembleDebug` donde exista SDK/configuración adecuados; después `./gradlew :app:connectedDebugAndroidTest` en dispositivo conectado.
- Guardar entorno, comandos, conteos, salidas, artefactos y fallos en `docs/REGISTRO_PRUEBAS.md`. Un comando propuesto no es evidencia.
- Si se detecta fallo, abrir incidencia por gravedad, comunicar a su chat y a 00, y reejecutar pruebas tras la corrección.
- No editar contratos de producción desde chat 07 sin autorización de 00.

**Referencia complementaria:** `docs/PLAN_MAESTRO_PRUEBAS.md` y `docs/INFORME_AUDITORIA_2026-11-25.md`.
