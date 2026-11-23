package org.example.model;

public class Estadistica {

    private int partidasJugadas;
    private int partidasGanadas;

    public int obtenerPartidasJugadas() {
        return partidasJugadas;
    }

    public void modificarPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int obtenerPartidasGanadas() {
        return partidasGanadas;
    }

    public void modificarPartidasGanadas(int partidasGanadas) {
        this.partidasGanadas = partidasGanadas;
    }

}
