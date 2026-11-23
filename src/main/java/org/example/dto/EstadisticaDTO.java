package org.example.dto;

public class EstadisticaDTO {

    private int partidasJugadas;
    private int partidasGanadas;

    public EstadisticaDTO() {
    }

    public EstadisticaDTO(int partidasJugadas, int partidasGanadas) {
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
    }

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
