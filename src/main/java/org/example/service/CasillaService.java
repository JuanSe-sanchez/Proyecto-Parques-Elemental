package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Tablero;

public class CasillaService {

    public static final int TOTAL_CASILLAS = 68;

    public Casilla crearCasilla(int numero, TipoCasilla tipo, ColorJugador color) {
        Casilla casilla = new Casilla();
        casilla.modificarNumero(numero);
        casilla.modificarTipo(tipo);
        casilla.modificarColor(color);
        return casilla;
    }

    public int obtenerNumero(Casilla casilla) {
        return casilla.obtenerNumero();
    }

    public TipoCasilla obtenerTipo(Casilla casilla) {
        return casilla.obtenerTipo();
    }

    public ColorJugador obtenerColor(Casilla casilla) {
        return casilla.obtenerColor();
    }

    public boolean validarCasillaSegura(Casilla casilla) {
        if (casilla == null || casilla.obtenerTipo() == null) {
            return false;
        }
        TipoCasilla tipo = casilla.obtenerTipo();
        return tipo == TipoCasilla.SEGURO || tipo == TipoCasilla.SALIDA || tipo == TipoCasilla.META;
    }

    public TipoCasilla validarTipoCasilla(Casilla casilla) {
        return casilla != null ? casilla.obtenerTipo() : null;
    }

    public Casilla obtenerCasilla(Tablero tablero, int posicion) {
        if (tablero == null || tablero.obtenerCasillas() == null) {
            return null;
        }
        for (Casilla casilla : tablero.obtenerCasillas()) {
            if (casilla.obtenerNumero() == posicion) {
                return casilla;
            }
        }
        return null;
    }

    public int obtenerCantidadCasillas() {
        return TOTAL_CASILLAS;
    }
}
