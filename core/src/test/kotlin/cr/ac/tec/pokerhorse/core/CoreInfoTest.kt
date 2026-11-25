package cr.ac.tec.pokerhorse.core

import kotlin.test.Test
import kotlin.test.assertEquals

class CoreInfoTest {

    @Test
    fun mensajeCorrecto() {
        val esperado = "Poker HORSE Core funcionando"
        val obtenido = CoreInfo.mensaje()

        assertEquals(esperado, obtenido)
    }
}
