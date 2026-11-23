package org.example.service;

import org.example.model.Casilla;
import org.example.model.Ficha;

public class ReglaService {


    public boolean validarSalida(Ficha ficha) {
        return false;
    }

    public boolean validarDobles() {
        return false;
    }

    public boolean validarCaptura(Ficha ficha) {
        return false;
    }

    public boolean validarCasillaSegura(Casilla casilla) {
        return false;
    }

    public boolean validarLlegadaMeta(Ficha ficha) {
        return false;
    }
    public boolean validarDobles(int dado1, int dado2) {
        return dado1 == dado2;
    }
}
