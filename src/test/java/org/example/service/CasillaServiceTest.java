package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Tablero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CasillaServiceTest {

    @Test
    public void probarCrearCasilla() {
        CasillaService servicio = new CasillaService();

        Casilla casilla = servicio.crearCasilla(
                5, TipoCasilla.NORMAL, ColorJugador.ROJO
        );

        assertNotNull(casilla);
        assertEquals(5, servicio.obtenerNumero(casilla));
        assertEquals(ColorJugador.ROJO, servicio.obtenerColor(casilla));
    }

    @Test
    public void probarTipoCasilla() {
        CasillaService servicio = new CasillaService();

        Casilla casilla = servicio.crearCasilla(
                5, TipoCasilla.NORMAL, ColorJugador.ROJO
        );

        assertEquals(TipoCasilla.NORMAL, servicio.obtenerTipo(casilla));
        assertEquals(TipoCasilla.NORMAL, servicio.validarTipoCasilla(casilla));
        assertNull(servicio.validarTipoCasilla(null));
    }

    @Test
    public void probarValidarCasillaSegura() {
        CasillaService servicio = new CasillaService();

        assertTrue(servicio.validarCasillaSegura(servicio.crearCasilla(12, TipoCasilla.SEGURO, null)));
        assertTrue(servicio.validarCasillaSegura(servicio.crearCasilla(5, TipoCasilla.SALIDA, ColorJugador.ROJO)));
        assertTrue(servicio.validarCasillaSegura(servicio.crearCasilla(108, TipoCasilla.META, null)));
        assertFalse(servicio.validarCasillaSegura(servicio.crearCasilla(6, TipoCasilla.NORMAL, null)));
        assertFalse(servicio.validarCasillaSegura(null));
    }

    @Test
    public void probarObtenerCasillaEnTablero() {
        CasillaService servicio = new CasillaService();
        TableroService tableroService = new TableroService(servicio);
        Tablero tablero = tableroService.crearTablero();

        Casilla salidaRojo = servicio.obtenerCasilla(tablero, TableroService.SALIDA_ROJO);
        assertNotNull(salidaRojo);
        assertEquals(TipoCasilla.SALIDA, salidaRojo.obtenerTipo());
        assertEquals(ColorJugador.ROJO, salidaRojo.obtenerColor());
        assertNull(servicio.obtenerCasilla(tablero, 999));
    }

    @Test
    public void probarObtenerCantidadCasillas() {
        CasillaService servicio = new CasillaService();
        assertEquals(68, servicio.obtenerCantidadCasillas());
    }
}
