# Arquitectura

## Modulos

### :core

Biblioteca Kotlin/JVM independiente de Android.

Paquetes previstos:

- domain: entidades, valores e invariantes.
- evaluation: evaluadores de manos.
- variants: estrategias HORSE.
- betting: apuestas y botes.
- engine: transiciones y motor.
- bots: estrategias de jugadores automaticos.

### :app

Aplicacion Android con Jetpack Compose.

Paquetes previstos:

- ui/screens
- ui/components
- ui/theme
- viewmodel
- application

## Dependencias

app -> core

Core no puede importar componentes Android o Compose.

## Principios

- Responsabilidad unica.
- Composicion sobre herencia innecesaria.
- Polimorfismo mediante estrategias.
- Inmutabilidad y transiciones controladas.
- Separacion entre reglas y presentacion.

## Infraestructura actual

CoreInfo y CoreInfoTest verifican provisionalmente
la integracion entre los modulos.

No forman parte del modelo definitivo del juego.

## Primeros componentes del dominio

El paquete core.domain contiene:

- Palo: conjunto cerrado de palos.
- Rango: conjunto cerrado de rangos.
- Carta: objeto de valor inmutable.

Estos componentes no dependen de Android,
evaluadores, variantes ni motor.