# Motor de juego — Chat 04

Base: main 011424def916f3a7bca60231c760d1f4b7884516.
Rama: feature/motor-de-juego.

## Componentes

- Repartidor: consume PlanReparto y un mazo inmutable.
- InicioMano: prepara participantes, reparto, obligaciones y orden.
- EstadoMotor: agrega metadatos de ronda a EstadoPartida por composición.
- GestorApuestas: consulta acciones legales y aplica aportes.
- CalculadorDeBotes: devuelve excedentes, construye capas y liquida por modalidad.
- MotorDeJuego: valida actor, avanza calles, liquida manos y rota variantes.
- VistaJugador: proyección que excluye mazo y cartas privadas ajenas.
- EstrategiaDeBot y BotBasico: eligen una acción de la vista.
- SesionAutoritativa: acceso sincronizado, revisión incremental, comandos
  con identity proveniente del transporte y vistas filtradas.

## Uso desde aplicación

```kotlin
val motor = MotorDeJuego()
val estado = motor.iniciar(ConfiguracionPartida(cantidadJugadores = 2),
    listOf("Anfitrión", "Invitado"))
val sesion = SesionAutoritativa(motor, estado, JugadorId(0))
val vista = sesion.vista(JugadorId(0))
val respuesta = sesion.aplicar(JugadorId(0), vista.revision, Accion.Call)
```

El transporte debe asociar cada conexión a un JugadorId y no confiar
en identificadores enviados por el cliente. Enviar una vista distinta
a cada jugador. No enviar EstadoMotor. Los comandos de bot se procesan
por la misma sesión. No se implementan sockets en core.

## Pruebas

174 pruebas anteriores registradas por 03. Las nuevas pruebas cubren
reparto, obligaciones, apuestas, turnos, botes, split, empate, rotación,
privacidad y revisión autoritativa. No ejecutadas en este entorno:
no se dispone de Gradle ni compilador Kotlin. Ejecutar en el repositorio
local:

```bash
./gradlew :core:test --rerun-tasks
```

No considerar este bloque aprobado hasta confirmar la compilación y
los resultados del XML. Políticas pendientes: REGLAS.md y DECISIONES.md.
No se ha integrado a main.
