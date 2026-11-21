package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;

public class CasillaService {

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

}
