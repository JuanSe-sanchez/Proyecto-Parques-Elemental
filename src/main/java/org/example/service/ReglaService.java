package org.example.service;

import org.example.enums.EstadoFicha;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;

public class ReglaService {

    public static final int MAX_DOBLES_CONSECUTIVOS = 3;
    public static final int MAX_INTENTOS_SALIDA_CASA = 3;

    public boolean validarSalida(Ficha ficha) {
        return ficha != null && ficha.obtenerEstado() == EstadoFicha.CASA;
    }

    public boolean validarSalida(Ficha ficha, int dado1, int dado2) {
        return validarSalida(ficha) && validarDobles(dado1, dado2);
    }

    public boolean validarDobles() {
        return false;
    }

    public boolean validarDobles(int dado1, int dado2) {
        return dado1 >= 1 && dado1 <= 6 && dado2 >= 1 && dado2 <= 6 && dado1 == dado2;
    }

    public boolean esTresDoblesSeguidos(int doblesConsecutivos) {
        return doblesConsecutivos >= MAX_DOBLES_CONSECUTIVOS;
    }

    public boolean validarCasillaSegura(Casilla casilla) {
        if (casilla == null || casilla.obtenerTipo() == null) {
            return false;
        }
        TipoCasilla tipo = casilla.obtenerTipo();
        return tipo == TipoCasilla.SEGURO || tipo == TipoCasilla.SALIDA || tipo == TipoCasilla.META;
    }

    public boolean validarCaptura(Ficha ficha) {
        return ficha != null
                && ficha.obtenerEstado() == EstadoFicha.RECORRIDO
                && ficha.obtenerPosicion() >= 1
                && ficha.obtenerPosicion() <= TableroService.TOTAL_CASILLAS_EXTERIOR;
    }

    public boolean validarCaptura(Ficha atacante, Ficha defensora, Casilla casillaDestino) {
        if (!validarCaptura(atacante) || !validarCaptura(defensora)) {
            return false;
        }
        if (atacante == defensora || atacante.obtenerColor() == defensora.obtenerColor()) {
            return false;
        }
        if (validarCasillaSegura(casillaDestino)) {
            return false;
        }
        return atacante.obtenerPosicion() == defensora.obtenerPosicion();
    }

    public boolean validarLlegadaMeta(Ficha ficha) {
        return ficha != null
                && (ficha.obtenerEstado() == EstadoFicha.META
                || ficha.obtenerPasosRecorridos() == TableroService.PASOS_META);
    }

    public boolean validarAvance(Ficha ficha, int pasos) {
        if (ficha == null || pasos <= 0) {
            return false;
        }
        if (ficha.obtenerEstado() != EstadoFicha.RECORRIDO) {
            return false;
        }
        int pasosActuales = Math.max(0, ficha.obtenerPasosRecorridos());
        return pasosActuales + pasos <= TableroService.PASOS_META;
    }
}
