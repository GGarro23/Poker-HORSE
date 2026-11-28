package cr.ac.tec.pokerhorse.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class CartaTest{
    @Test
    fun cantidadDePalos() {
        val esperado = 4
        val obtenido = Palo.entries.size

        assertEquals(esperado, obtenido)
    }

    @Test
    fun cantidadDeRangos() {
        assertEquals(13, Rango.entries.size)
    }

    @Test
    fun valorAs(){
        assertEquals(14,Rango.AS.valor)
    }

    @Test
    fun valorRey(){
        assertEquals(13,Rango.REY.valor)
    }

    @Test
    fun cartasEquivalentes(){
        val carta1 = Carta(Palo.PICA, Rango.REY)
        val carta2 = Carta(Palo.PICA, Rango.REY)

        assertEquals(carta1, carta2)
        assertNotSame(carta1, carta2)
    }

    @Test
    fun cartasConDistintoPalo(){
        val carta1 = Carta(Palo.DIAMANTE, Rango.CUATRO)
        val carta2 = Carta(Palo.TREBOL, Rango.CUATRO)
        assertNotEquals(carta1, carta2)
    }

    @Test
    fun cartasConDistintoRango(){
        val carta1 = Carta(Palo.DIAMANTE, Rango.DOS)
        val carta2 = Carta(Palo.DIAMANTE, Rango.AS)

        assertNotEquals(carta1, carta2)
    }

    @Test
    fun copiaNoModificaOriginal(){
        val cartaOriginal = Carta(Palo.CORAZON, Rango.REINA)
        val cartaCopia = cartaOriginal.copy(rango =Rango.REY)

        assertNotSame(cartaCopia,cartaOriginal)

        assertEquals(Rango.REINA, cartaOriginal.rango)
        assertEquals(Rango.REY, cartaCopia.rango)

        assertEquals(Palo.CORAZON, cartaOriginal.palo)
        assertEquals(Palo.CORAZON, cartaCopia.palo)
    }

    @Test
    fun combinacionesUnicas(){
        val cartas = Palo.entries.flatMap{ palo ->
            Rango.entries.map{ rango ->
                Carta(palo,rango)
            }
        }

        assertEquals(52, cartas.size)
        assertEquals(52, cartas.toSet().size)
    }
}