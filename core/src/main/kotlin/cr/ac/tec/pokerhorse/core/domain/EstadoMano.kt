package cr.ac.tec.pokerhorse.core.domain

class EstadoMano(
    val variante: Variante,
    val calle: Calle,
    val mazo: Mazo,
    jugadores: List<Jugador>,
    participaciones: List<ParticipacionMano>,
    comunitarias: List<Carta> = emptyList(),
    val turno: JugadorId? = null,
    val boton: Asiento,
    val aumentosCalle: Int = 0
) {
    private val jugadoresInternos = jugadores.toList()
    private val participacionesInternas = participaciones.toList()
    private val comunitariasInternas = comunitarias.toList()

    init {
        require(jugadoresInternos.size in 2..8) {
            "La mesa debe contener entre 2 y 8 jugadores"
        }

        require(jugadoresInternos.map { it.id }.toSet().size ==
                jugadoresInternos.size) {
            "No puede haber identidades duplicadas"
        }

        require(jugadoresInternos.map { it.asiento }.toSet().size ==
                jugadoresInternos.size) {
            "No puede haber asientos duplicados"
        }

        require(participacionesInternas.map { it.jugadorId }.toSet().size ==
                participacionesInternas.size) {
            "No puede haber participaciones duplicadas"
        }

        val jugadoresPorId = jugadoresInternos.associateBy { it.id }

        require(participacionesInternas.all {
            it.jugadorId in jugadoresPorId
        }) {
            "Toda participación debe corresponder a un jugador"
        }

        require(jugadoresInternos.any { it.asiento == boton }) {
            "El botón debe corresponder a un asiento de la mesa"
        }

        require(aumentosCalle >= 0) {
            "La cantidad de aumentos no puede ser negativa"
        }

        require(comunitariasInternas.size <= 5) {
            "No puede haber más de cinco cartas comunitarias"
        }

        require(turno == null || participacionesInternas.any {
            it.jugadorId == turno &&
                    it.estado == EstadoJugador.ACTIVO &&
                    jugadoresPorId.getValue(it.jugadorId).fichas > 0L
        }) {
            "El turno debe corresponder a un jugador activo con fichas"
        }

        val todasLasCartas =
            mazo.obtenerCartas() +
                    comunitariasInternas +
                    participacionesInternas.flatMap { it.obtenerCartas() }

        require(todasLasCartas.size == todasLasCartas.toSet().size) {
            "Existen cartas duplicadas en la mano"
        }

        var total = 0L

        for (jugador in jugadoresInternos) {
            require(jugador.fichas <= Long.MAX_VALUE - total) {
                "Desbordamiento en el total de fichas"
            }
            total += jugador.fichas
        }

        for (participacion in participacionesInternas) {
            require(participacion.aporteTotal <= Long.MAX_VALUE - total) {
                "Desbordamiento en el total de fichas"
            }
            total += participacion.aporteTotal
        }
    }

    fun obtenerJugadores(): List<Jugador> {
        return jugadoresInternos.toList()
    }

    fun obtenerParticipaciones(): List<ParticipacionMano> {
        return participacionesInternas.toList()
    }

    fun obtenerComunitarias(): List<Carta> {
        return comunitariasInternas.toList()
    }

    fun buscarJugador(id: JugadorId): Jugador? {
        return jugadoresInternos.find { it.id == id }
    }

    fun buscarParticipacion(id: JugadorId): ParticipacionMano? {
        return participacionesInternas.find { it.jugadorId == id }
    }

    val pozo: Long
        get() = participacionesInternas.sumOf { it.aporteTotal }

    val apuestaVigente: Long
        get() = participacionesInternas
            .maxOfOrNull { it.aporteCalle } ?: 0L

    val totalFichas: Long
        get() = jugadoresInternos.sumOf { it.fichas } + pozo
}