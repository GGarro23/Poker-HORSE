package cr.ac.tec.pokerhorse.core.domain

sealed interface ResultadoTransicion<out T> {

    data class Exito<T>(
        val estado: T
    ) : ResultadoTransicion<T>

    data class Rechazo(
        val error: ErrorDominio
    ) : ResultadoTransicion<Nothing>
}