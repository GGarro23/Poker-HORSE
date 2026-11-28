# Android — Hito 4 (chat 05)

Base: b4df191cec62a464717af8e60f374d3862cedf47.
Rama: feature/android-compose.

## Implementación

- MainActivity configura Compose y respeta insets.
- PokerApp navega entre menú, configuración, mesa y fin.
- PartidaViewModel mantiene StateFlow, serializa eventos de usuario y conserva
  la sesión durante recreaciones de Activity. No persiste partidas tras muerte
  del proceso. Los bots avanzan fuera del hilo principal.
- SesionPartida define el puerto de cliente, SesionLocal contiene la autoridad
  y publica únicamente VistaVersionada del humano. Los bots reciben su propia
  VistaJugador y usan comandos versionados. LAN y ranked no implementados.
- MesaScreen presenta jugadores, cartas, pozo, aportes, turno, acciones y premios.
  Disposición por ancho disponible, con panel lateral en horizontal.
- Siguiente mano y fin de partida los decide el motor; rotación automática HORSE.
- Extensión aditiva de VistaJugador: montosAcciones y cantidadCartasOcultas en
  VistaParticipante. El importe se obtiene de GestorApuestas.monto, compartido
  con ejecución de acciones. No hay reglas nuevas ni exposición de cartas
  privadas. El resumen presenta premios autorizados; no revela cartas ajenas
  ni inventa categorías de mano. Coordinar revelación de showdown con 00 si
  se requiere ampliar el contrato.

## Dirección artística (chat 06)

Recursos provisionales, sin motor gráfico externo. La paleta está en
ui/theme/Color.kt, la tipografía en Type.kt y el tema en Theme.kt.
Reemplazar CartaVisual y JugadorVisual por sprites sin modificar el motor;
mantener semántica de accesibilidad. Los assets Android exportados deben ir
en res/drawable-nodpi cuando corresponda. MesaScreen controla layout.
No se incorporan sprites ni fuentes definitivas.

## Verificación

- Se revisó la base y los contratos existentes. 196 pruebas previas registradas
  por el propietario (no reejecutadas aquí).
- Se ampliaron assertions de VistaJugadorTest para metadatos privados/legales.
- Se añadieron dos pruebas de SesionLocal: rotación completa y revisión
  obsoleta. Pendientes de ejecución.
- Intento de ./gradlew :core:test :app:assembleDebug bloqueado antes de
  compilar: descarga Gradle 9.6.0, Network is unreachable. El entorno tampoco
  tiene Android SDK ni dispositivo conectado.
- git diff --check sin errores.

En el equipo del propietario:
```sh
git fetch origin
git switch feature/android-compose
git pull --ff-only
./gradlew :core:test :app:testDebugUnitTest :app:assembleDebug
adb devices
./gradlew :app:installDebug
```

Probar Galaxy S25: inicio, 2–8 jugadores, comandos, rotación de pantalla
durante turno de bot, manos HORSE (manosPorVariante=1), premios, salida,
fin de partida y privacidad. No considerar cerrado el hito antes de
confirmar compilación, instalación y pruebas.
