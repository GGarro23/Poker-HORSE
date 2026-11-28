# Estado del proyecto

## Infraestructura

- [x] Android Studio y SDK configurados.
- [x] Gradle Wrapper funcional.
- [x] Modulos core y app creados.
- [x] Dependencia app -> core.
- [x] Primera prueba unitaria exitosa.
- [x] Aplicacion ejecutada en dispositivo fisico.
- [x] Repositorio local inicializado.
- [x] Repositorio remoto configurado.
- [x] Primer commit publicado.

## Dominio

Hito 1 implementado y probado.
Pendiente de ratificación final e integración.

- [x] Carta, Rango y Palo.
- [x] Mazo y ResultadoExtraccion.
- [x] JugadorId y Asiento.
- [x] Jugador y EstadoJugador.
- [x] ParticipacionMano y ResultadoAporte.
- [x] Variante y Calle.
- [x] Accion.
- [x] ConfiguracionPartida.
- [x] EstadoMano.
- [x] EstadoPartida.
- [x] ResultadoTransicion y ErrorDominio.
- [x] Pruebas unitarias del modelo base.

Pruebas acumuladas: 134 exitosas.

El dominio se construyó desde cero, sin utilizar
la base preliminar 00.

## Motor

Pendiente.

## Interfaz

Pantalla provisional de integracion disponible.
Diseno pixel art definitivo pendiente.

## Siguiente paso

Ratificar los contratos del modelo de dominio.

Continuar con evaluadores, estrategias HORSE
y motor de juego.

Resolver los ajustes de integración de forma coordinada.

## Cierre del Hito 0

Commit de infraestructura:
fae8536b13cd706c736b4676d14a2fc5ca37ad50

Infraestructura publicada y sincronizada con GitHub.

Siguiente responsable: chat 02 - Modelo de dominio.



## Chat 03 — Evaluadores y variantes (27-11-2026)

Evaluadores alto y bajo y cinco estrategias implementados en
feature/implementacion-variantes. Base validada localmente por Gabriel:
c9cc1a6, BUILD SUCCESSFUL (10 segundos). Contrato general ratificado
provisionalmente por 00. Cierre agrega pruebas de orden, bring-in,
excepción de cuarta calle y mazo insuficiente; ejecución nueva pendiente.
Integración a main condicionada a pruebas y coordinación con 04.
