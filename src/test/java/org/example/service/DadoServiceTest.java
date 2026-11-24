package org.example.service;

import org.example.model.Dado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DadoServiceTest {

    @Test
    public void probarCrearDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        assertNotNull(dado);
    }

    @Test
    public void probarLanzarDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        int resultado = servicio.lanzarDado(dado);

        assertTrue(resultado >= 1 && resultado <= 6);
    }

    @Test
    public void probarLanzarDosDados() {
        DadoService servicio = new DadoService();
        Dado d1 = servicio.crearDado();
        Dado d2 = servicio.crearDado();

        int[] resultados = servicio.lanzarDosDados(d1, d2);

        assertEquals(2, resultados.length);
        assertTrue(servicio.esValorValido(resultados[0]));
        assertTrue(servicio.esValorValido(resultados[1]));
        assertEquals(resultados[0], servicio.obtenerValor(d1));
        assertEquals(resultados[1], servicio.obtenerValor(d2));
    }

    @Test
    public void probarValorDelDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        servicio.lanzarDado(dado);

        assertEquals(dado.obtenerValor(), servicio.obtenerValor(dado));
        assertEquals(0, servicio.obtenerValor(null));
    }

    @Test
    public void probarRangoDadoYDobles() {
        DadoService servicio = new DadoService();

        assertTrue(servicio.esValorValido(1));
        assertTrue(servicio.esValorValido(4));
        assertTrue(servicio.esValorValido(6));
        assertFalse(servicio.esValorValido(0));
        assertFalse(servicio.esValorValido(7));

        assertTrue(servicio.validarDobles(3, 3));
        assertFalse(servicio.validarDobles(3, 5));
        assertFalse(servicio.validarDobles(0, 0));
    }
}
