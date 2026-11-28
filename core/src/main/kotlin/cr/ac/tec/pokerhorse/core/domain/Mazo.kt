package cr.ac.tec.pokerhorse.core.domain

import kotlin.random.Random

class Mazo private constructor(cartas: List<Carta>) {
    private val cartas: List<Carta> = cartas.toList()
    init {
        require(this.cartas.size <= 52) {
            "El mazo no puede contener más de 52 cartas"
        }
        require(this.cartas.size == this.cartas.toSet().size) {
            "El mazo no puede contener cartas duplicadas"
        }
    }
    companion object {
        fun crear(): Mazo {
            val cartas = Palo.entries.flatMap { palo ->
                Rango.entries.map { rango ->
                    Carta(palo, rango)
                }
            }

            return Mazo(cartas)
        }
    }
    val cantidad: Int
        get() = cartas.size

    fun estaVacio(): Boolean {
        return cartas.isEmpty()
    }

    fun mezclar(random: Random = Random.Default): Mazo {
        return Mazo(cartas.shuffled(random))
    }

    fun extraer(): ResultadoExtraccion {
        check(!estaVacio()) {
            "No se puede extraer una carta de un mazo vacío"
        }
        val carta = cartas.first()
        val mazoRestante = Mazo(cartas.drop(1))
        return ResultadoExtraccion(carta, mazoRestante)
    }

    fun obtenerCartas(): List<Carta> {
        return cartas.toList()
    }
}
