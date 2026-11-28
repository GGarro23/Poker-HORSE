package cr.ac.tec.pokerhorse.core.domain

data class EstadoPartida(
    val configuracion: ConfiguracionPartida,
    val manoActual: EstadoMano,
    val manosCompletadas: Long = 0L
) {
    init {
        require(manosCompletadas >= 0L) {
            "La cantidad de manos completadas no puede ser negativa"
        }

        val jugadores = manoActual.obtenerJugadores()

        require(jugadores.size == configuracion.cantidadJugadores) {
            "La cantidad de jugadores no coincide con la configuración"
        }

        require(jugadores.all {
            it.asiento.posicion < configuracion.cantidadJugadores
        }) {
            "Existe un asiento fuera de los límites de la mesa"
        }

        require(manoActual.totalFichas == totalFichasIniciales) {
            "La cantidad total de fichas de la partida no se conserva"
        }
    }

    val totalFichasIniciales: Long
        get() = configuracion.fichasIniciales *
                configuracion.cantidadJugadores

    val varianteActual: Variante
        get() = manoActual.variante

    fun obtenerJugadores(): List<Jugador> {
        return manoActual.obtenerJugadores()
    }
}