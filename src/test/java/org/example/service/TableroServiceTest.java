package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.example.model.Tablero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableroServiceTest {

    @Test
    public void probarCrearTableroCon68Casillas() {
        TableroService servicio = new TableroService();

        Tablero tablero = servicio.crearTablero();

        assertNotNull(tablero);
        assertEquals(68, servicio.contarCasillas(tablero));
        assertTrue(servicio.estaTableroListo(tablero));
    }

    @Test
    public void probarSalidasYSegurosConfigurados() {
        TableroService servicio = new TableroService();
        Tablero tablero = servicio.crearTablero();

        assertEquals(TipoCasilla.SALIDA, servicio.obtenerCasilla(tablero, TableroService.SALIDA_ROJO).obtenerTipo());
        assertEquals(ColorJugador.ROJO, servicio.obtenerCasilla(tablero, TableroService.SALIDA_ROJO).obtenerColor());
        assertEquals(TipoCasilla.SALIDA, servicio.obtenerCasilla(tablero, TableroService.SALIDA_AZUL).obtenerTipo());
        assertEquals(TipoCasilla.SALIDA, servicio.obtenerCasilla(tablero, TableroService.SALIDA_VERDE).obtenerTipo());
        assertEquals(TipoCasilla.SALIDA, servicio.obtenerCasilla(tablero, TableroService.SALIDA_AMARILLO).obtenerTipo());

        assertEquals(TipoCasilla.SEGURO, servicio.obtenerCasilla(tablero, 8).obtenerTipo());
        assertEquals(TipoCasilla.SEGURO, servicio.obtenerCasilla(tablero, 64).obtenerTipo());
        assertEquals(TipoCasilla.META, servicio.obtenerCasilla(tablero, TableroService.POSICION_META).obtenerTipo());
    }

    @Test
    public void probarAgregarYReiniciarTablero() {
        TableroService servicio = new TableroService();
        CasillaService servicioCasilla = new CasillaService();

        Tablero tablero = servicio.crearTablero();
        Casilla extra = servicioCasilla.crearCasilla(69, TipoCasilla.NORMAL, null);
        servicio.agregarCasilla(tablero, extra);

        assertEquals(69, servicio.contarCasillas(tablero));

        servicio.reiniciarTablero(tablero);
        assertEquals(68, servicio.contarCasillas(tablero));
    }

    @Test
    public void probarValidarMovimiento() {
        TableroService servicio = new TableroService();
        FichaService fichaService = new FichaService();
        Tablero tablero = servicio.crearTablero();
        Ficha ficha = fichaService.crearFicha(ColorJugador.ROJO);

        assertTrue(servicio.validarMovimiento(tablero, ficha, 5));
        assertTrue(servicio.validarMovimiento(tablero, ficha, TableroService.POSICION_META));
        assertFalse(servicio.validarMovimiento(tablero, ficha, 999));

        fichaService.enviarAFinal(ficha);
        assertFalse(servicio.validarMovimiento(tablero, ficha, 5));
    }

    @Test
    public void probarTableroInicializado() {
        TableroService servicio = new TableroService();
        assertTrue(servicio.estaTableroListo());
    }
}
