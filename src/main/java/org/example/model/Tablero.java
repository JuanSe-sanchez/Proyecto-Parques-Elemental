package org.example.model;

import java.util.List;

public class Tablero {


    private List<Casilla> casillas;

    public List<Casilla> obtenerCasillas() {
        return casillas;
    }

    public void modificarCasillas(List<Casilla> casillas) {
        this.casillas = casillas;
    }
}

