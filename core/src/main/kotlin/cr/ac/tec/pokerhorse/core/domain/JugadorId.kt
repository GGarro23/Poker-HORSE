package cr.ac.tec.pokerhorse.core.domain

@JvmInline
value class JugadorId(val identificador: Int) {
    init {
        require(identificador >= 0){
            "El ID no puede ser negativo"
        }
    }
}