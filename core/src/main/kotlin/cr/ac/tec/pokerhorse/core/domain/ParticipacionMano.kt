package cr.ac.tec.pokerhorse.core.domain

class ParticipacionMano(
    val jugadorId: JugadorId,
    cartas: List<Carta> = emptyList(),
    val aporteCalle: Long = 0L,
    val aporteTotal: Long = 0L,
    val estado: EstadoJugador = EstadoJugador.ACTIVO
) {
    private val cartasInternas: List<Carta> = cartas.toList()

    init {
        require(aporteCalle >= 0) {
            "El aporte de la calle no puede ser negativo"
        }

        require(aporteTotal >= 0) {
            "El aporte total no puede ser negativo"
        }

        require(aporteCalle <= aporteTotal) {
            "El aporte de la calle no puede superar el aporte total"
        }

        require(cartasInternas.size == cartasInternas.toSet().size) {
            "La participación no puede contener cartas duplicadas"
        }
    }

    fun obtenerCartas(): List<Carta> {
        return cartasInternas.toList()
    }
    fun agregarCarta(carta: Carta): ParticipacionMano {
        return ParticipacionMano(
            jugadorId,
            cartasInternas + carta,
            aporteCalle,
            aporteTotal,
            estado
        )
    }
    private fun registrarAporte(cantidad: Long): ParticipacionMano {
        require(cantidad >= 0L) {
            "El aporte no puede ser negativo"
        }

        require(cantidad <= Long.MAX_VALUE - aporteTotal) {
            "El aporte supera el máximo permitido"
        }

        return ParticipacionMano(
            jugadorId,
            cartasInternas,
            aporteCalle + cantidad,
            aporteTotal + cantidad,
            estado
        )
    }
    fun reiniciarAporteCalle(): ParticipacionMano {
        return ParticipacionMano(jugadorId, cartasInternas, 0L, aporteTotal, estado)
    }
    fun cambiarEstado(nuevoEstado: EstadoJugador): ParticipacionMano {
        return ParticipacionMano(jugadorId, cartasInternas, aporteCalle, aporteTotal, nuevoEstado)
    }
    private fun registrarAnte(cantidad: Long): ParticipacionMano {
        require(cantidad >= 0L) {
            "El ante no puede ser negativo"
        }

        require(cantidad <= Long.MAX_VALUE - aporteTotal) {
            "El aporte supera el máximo permitido"
        }

        return ParticipacionMano(jugadorId, cartasInternas, aporteCalle, aporteTotal + cantidad, estado)
    }
    fun aportarFichas(jugador: Jugador, cantidad: Long, esAnte: Boolean = false): ResultadoAporte {
        require(jugador.id == jugadorId) {
            "El jugador no corresponde a esta participación"
        }

        require(estado == EstadoJugador.ACTIVO) {
            "El jugador no está activo"
        }

        val jugadorActualizado = jugador.retirarFichas(cantidad)

        val participacionActualizada = if (esAnte) {
            registrarAnte(cantidad)
        } else {
            registrarAporte(cantidad)
        }

        val participacionFinal = if (jugadorActualizado.fichas == 0L) {
            participacionActualizada.cambiarEstado(EstadoJugador.ALL_IN)
        } else {
            participacionActualizada
        }

        return ResultadoAporte(
            jugadorActualizado,
            participacionFinal
        )
    }
}