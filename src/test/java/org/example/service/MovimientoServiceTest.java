package org.example.service;

import org.example.enums.ColorJugador;
import org.example.model.Ficha;
import org.example.model.Movimiento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovimientoServiceTest {

    @Test
    public void probarRegistrarMovimiento() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();

        Ficha ficha = servicioFicha.crearFicha(ColorJugador.ROJO);

        Movimiento movimiento = servicio.registrarMovimiento(ficha, 0, 5);

        assertNotNull(movimiento);
        assertNotNull(servicio.obtenerFecha(movimiento));
        assertEquals(1, servicio.contarMovimientos());
    }

    @Test
    public void probarPosicionInicialYFinal() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();

        Ficha ficha = servicioFicha.crearFicha(ColorJugador.ROJO);

        Movimiento movimiento = servicio.registrarMovimiento(ficha, 0, 5);

        assertEquals(0, servicio.obtenerPosicionInicial(movimiento));
        assertEquals(5, servicio.obtenerPosicionFinal(movimiento));
    }

    @Test
    public void probarValidarYEjecutarMovimiento() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();
        Ficha ficha = servicioFicha.crearFicha(ColorJugador.AZUL);

        Movimiento invalido = new Movimiento();
        invalido.modificarFicha(ficha);
        invalido.modificarPosicionInicial(5);
        invalido.modificarPosicionFinal(5);
        assertFalse(servicio.validarMovimiento(invalido));

        Movimiento valido = new Movimiento();
        valido.modificarFicha(ficha);
        valido.modificarPosicionInicial(0);
        valido.modificarPosicionFinal(22);
        assertTrue(servicio.validarMovimiento(valido));

        servicio.ejecutarMovimiento(valido);
        assertEquals(22, ficha.obtenerPosicion());
        assertEquals(1, servicio.obtenerHistorial().size());
    }

    @Test
    public void probarContarMovimientos() {
        MovimientoService servicio = new MovimientoService();
        assertEquals(0, servicio.contarMovimientos());
    }
}
