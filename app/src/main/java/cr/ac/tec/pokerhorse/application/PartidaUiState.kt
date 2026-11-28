package cr.ac.tec.pokerhorse.application

import cr.ac.tec.pokerhorse.core.engine.VistaVersionada

enum class Pantalla { MENU, CONFIGURACION, MESA, FIN }

data class PartidaUiState(
    val pantalla: Pantalla = Pantalla.MENU,
    val mesa: VistaVersionada? = null,
    val procesando: Boolean = false,
    val error: String? = null
)
