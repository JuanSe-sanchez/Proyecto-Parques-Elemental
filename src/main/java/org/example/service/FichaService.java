package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.model.Ficha;

public class FichaService {

    public Ficha crearFicha(ColorJugador color) {
        Ficha ficha = new Ficha();

        ficha.modificarPosicion(0);
        ficha.modificarEstado(EstadoFicha.CASA);
        ficha.modificarColor(color);

        return ficha;
    }

    public void moverFicha(Ficha ficha, int nuevaPosicion) {
        ficha.modificarPosicion(nuevaPosicion);
        ficha.modificarEstado(EstadoFicha.RECORRIDO);
    }

    public void enviarAFinal(Ficha ficha) {
        ficha.modificarEstado(EstadoFicha.META);
    }

    public int obtenerPosicion(Ficha ficha) {
        return ficha.obtenerPosicion();
    }

    public EstadoFicha obtenerEstado(Ficha ficha) {
        return ficha.obtenerEstado();
    }

}
