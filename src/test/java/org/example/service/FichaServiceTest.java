package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FichaServiceTest {

    @Test
    public void probarCrearFicha() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        assertNotNull(ficha);
        assertEquals(EstadoFicha.CASA, servicio.obtenerEstado(ficha));
        assertEquals(0, servicio.obtenerPosicion(ficha));
    }

    @Test
    public void probarMoverFicha() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        servicio.moverFicha(ficha, 10);

        assertEquals(10, servicio.obtenerPosicion(ficha));
    }

    @Test
    public void probarRecorridoCircularYEntradaPasilloMeta() {
        FichaService servicio = new FichaService();
        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        assertTrue(servicio.sacarDeCasa(ficha));
        assertEquals(TableroService.SALIDA_AZUL, ficha.obtenerPosicion());
        assertEquals(0, ficha.obtenerPasosRecorridos());

        // Avanzar 50 pasos desde 18 -> (18 - 1 + 50) % 68 + 1 = 68
        assertTrue(servicio.avanzarFicha(ficha, 50));
        assertEquals(68, ficha.obtenerPosicion());

        // Avanzar 1 paso cruza 68 -> 1
        assertTrue(servicio.avanzarFicha(ficha, 1));
        assertEquals(1, ficha.obtenerPosicion());

        // Avanzar hasta entrada del pasillo (pasos 63 -> casilla 13)
        assertTrue(servicio.avanzarFicha(ficha, 12));
        assertEquals(13, ficha.obtenerPosicion());
        assertEquals(TableroService.PASOS_ENTRADA_PASILLO, ficha.obtenerPasosRecorridos());

        // Entrar al pasillo (casilla 101)
        assertTrue(servicio.avanzarFicha(ficha, 1));
        assertEquals(TableroService.POSICION_INICIO_PASILLO, ficha.obtenerPosicion());

        // No puede pasarse de META (faltan 7 pasos)
        assertFalse(servicio.avanzarFicha(ficha, 8));
        assertEquals(EstadoFicha.RECORRIDO, ficha.obtenerEstado());

        // Llegada exacta a META
        assertTrue(servicio.avanzarFicha(ficha, 7));
        assertEquals(EstadoFicha.META, ficha.obtenerEstado());
        assertEquals(TableroService.POSICION_META, ficha.obtenerPosicion());
        assertTrue(servicio.validarLlegadaMeta(ficha));
    }

    @Test
    public void probarRegresarACasaYCapturas() {
        FichaService servicio = new FichaService();
        CasillaService casillaService = new CasillaService();

        Ficha rojo = servicio.crearFicha(ColorJugador.ROJO);
        Ficha verde = servicio.crearFicha(ColorJugador.VERDE);
        servicio.sacarDeCasa(rojo);
        servicio.avanzarFicha(rojo, 5); // casilla 6 (NORMAL)
        servicio.sacarDeCasa(verde);
        verde.modificarPosicion(6);

        Casilla normal = casillaService.crearCasilla(6, TipoCasilla.NORMAL, null);
        List<Ficha> capturadas = servicio.detectarCapturas(rojo, List.of(verde), normal);
        assertEquals(1, capturadas.size());

        servicio.regresarSalida(verde);
        assertEquals(EstadoFicha.CASA, verde.obtenerEstado());
        assertEquals(0, verde.obtenerPosicion());
    }

    @Test
    public void probarEnviarFichaAMeta() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        servicio.enviarAFinal(ficha);

        assertEquals(EstadoFicha.META, servicio.obtenerEstado(ficha));
    }

    @Test
    public void probarMoverFichaSinPasosInvalidos() {
        FichaService servicio = new FichaService();
        assertThrows(IllegalArgumentException.class, () -> servicio.validarPasos(0));
    }
}
