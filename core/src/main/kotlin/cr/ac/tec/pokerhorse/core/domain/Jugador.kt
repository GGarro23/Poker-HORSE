package cr.ac.tec.pokerhorse.core.domain

data class Jugador(val id: JugadorId, val nombre: String, val asiento: Asiento, val fichas: Long){
    init {
        require(nombre.isNotBlank()){
            "El nombre del jugador no puede estar en blanco"
        }
        require(fichas >= 0){
            "La cantidad de fichas no puede ser negativa"
        }
    }

    fun retirarFichas(cantidad: Long):Jugador{
        require(cantidad>=0){
            "La cantidad a retirar no puede ser negativa"
        }
        require(fichas>=cantidad){
            "No hay suficientes fichas disponibles para realizar el retiro"
        }
        return copy(fichas = fichas-cantidad)
    }

    fun recibirFichas(cantidad: Long): Jugador{
        require(cantidad>=0){
            "La cantidad a recibir no puede ser negativa"
        }
        require(cantidad<=Long.MAX_VALUE-fichas){
            "La cantidad a recibir sobrepasa el maximo permitido"
        }
        return copy(fichas= fichas+cantidad)
    }
}