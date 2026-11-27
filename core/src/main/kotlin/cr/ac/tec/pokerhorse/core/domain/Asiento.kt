package cr.ac.tec.pokerhorse.core.domain

@JvmInline
value class Asiento(val posicion: Int){
    init {
        require(posicion >= 0){
            "El asiento tiene que ser una posicion valida"
        }
    }
}