package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReglaServiceTest {

    @Test
    public void probarValidarDoblesYTresSeguidos() {
        ReglaService regla = new ReglaService();

        assertTrue(regla.validarDobles(6, 6));
        assertFalse(regla.validarDobles(4, 5));
        assertFalse(regla.validarDobles(0, 0));
        assertFalse(regla.validarDobles());

        assertFalse(regla.esTresDoblesSeguidos(2));
        assertTrue(regla.esTresDoblesSeguidos(3));
    }

    @Test
    public void probarValidarSalidaDeCasa() {
        ReglaService regla = new ReglaService();
        FichaService fichaService = new FichaService();
        Ficha ficha = fichaService.crearFicha(ColorJugador.ROJO);

        assertTrue(regla.validarSalida(ficha));
        assertTrue(regla.validarSalida(ficha, 5, 5));
        assertFalse(regla.validarSalida(ficha, 5, 2));

        fichaService.sacarDeCasa(ficha);
        assertFalse(regla.validarSalida(ficha, 5, 5));
    }

    @Test
    public void probarValidarCapturaYCasillaSegura() {
        ReglaService regla = new ReglaService();
        FichaService fichaService = new FichaService();
        CasillaService casillaService = new CasillaService();

        Ficha rojo = fichaService.crearFicha(ColorJugador.ROJO);
        Ficha azul = fichaService.crearFicha(ColorJugador.AZUL);
        fichaService.sacarDeCasa(rojo);
        fichaService.avanzarFicha(rojo, 3); // casilla 4 (NORMAL)
        fichaService.sacarDeCasa(azul);
        azul.modificarPosicion(4);

        Casilla normal = casillaService.crearCasilla(4, TipoCasilla.NORMAL, null);
        Casilla seguro = casillaService.crearCasilla(8, TipoCasilla.SEGURO, null);

        assertTrue(regla.validarCaptura(rojo, azul, normal));
        assertFalse(regla.validarCaptura(rojo, azul, seguro));
        assertFalse(regla.validarCaptura(rojo, rojo, normal));
    }

    @Test
    public void probarValidarAvanceYLlegadaMeta() {
        ReglaService regla = new ReglaService();
        FichaService fichaService = new FichaService();
        Ficha ficha = fichaService.crearFicha(ColorJugador.VERDE);

        assertFalse(regla.validarAvance(ficha, 4));
        fichaService.sacarDeCasa(ficha);
        assertTrue(regla.validarAvance(ficha, 6));

        ficha.modificarPasosRecorridos(TableroService.PASOS_META - 2);
        assertTrue(regla.validarAvance(ficha, 2));
        assertFalse(regla.validarAvance(ficha, 3));

        fichaService.avanzarFicha(ficha, 2);
        assertTrue(regla.validarLlegadaMeta(ficha));
    }
}
