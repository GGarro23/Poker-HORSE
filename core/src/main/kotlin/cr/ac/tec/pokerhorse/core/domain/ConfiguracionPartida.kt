package cr.ac.tec.pokerhorse.core.domain

data class ConfiguracionPartida(
    val cantidadJugadores: Int = 6,
    val fichasIniciales: Long = 1000L,
    val apuestaPequena: Long = 20L,
    val apuestaGrande: Long = 40L,
    val ante: Long = 2L,
    val bringIn: Long = 5L,
    val manosPorVariante: Int = 6
) {
    init {
        require(cantidadJugadores in 2..8) {
            "La cantidad de jugadores debe estar entre 2 y 8"
        }

        require(fichasIniciales > 0L) {
            "Las fichas iniciales deben ser positivas"
        }

        require(fichasIniciales <= Long.MAX_VALUE / cantidadJugadores) {
            "La cantidad total de fichas supera el máximo permitido"
        }

        require(apuestaPequena > 0L) {
            "La apuesta pequeña debe ser positiva"
        }

        require(apuestaGrande >= apuestaPequena) {
            "La apuesta grande no puede ser menor que la pequeña"
        }

        require(ante >= 0L) {
            "El ante no puede ser negativo"
        }

        require(bringIn > 0L && bringIn <= apuestaPequena) {
            "El bring-in debe ser positivo y no superar la apuesta pequeña"
        }

        require(manosPorVariante > 0) {
            "Debe jugarse al menos una mano por variante"
        }
    }
}