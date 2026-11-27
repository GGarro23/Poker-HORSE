package cr.ac.tec.pokerhorse.core.domain

data class ResultadoAporte(
    val jugador: Jugador,
    val participacion: ParticipacionMano
)

sealed interface Accion {

    data object Fold : Accion

    data object Check : Accion

    data object Call : Accion

    data object AllIn : Accion

    data class Bet(val cantidad: Long) : Accion {
        init {
            require(cantidad > 0L) {
                "La apuesta debe ser positiva"
            }
        }
    }

    data class Raise(val hasta: Long) : Accion {
        init {
            require(hasta > 0L) {
                "El monto objetivo debe ser positivo"
            }
        }
    }
}