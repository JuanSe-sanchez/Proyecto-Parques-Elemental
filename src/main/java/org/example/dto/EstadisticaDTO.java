package org.example.dto;

public class EstadisticaDTO {

    private Long jugadorId;
    private String nombreJugador;
    private int partidasJugadas;
    private int partidasGanadas;

    public EstadisticaDTO() {
    }

    public EstadisticaDTO(int partidasJugadas, int partidasGanadas) {
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
    }

    public EstadisticaDTO(Long jugadorId, String nombreJugador, int partidasJugadas, int partidasGanadas) {
        this.jugadorId = jugadorId;
        this.nombreJugador = nombreJugador;
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
    }

    public Long obtenerJugadorId() {
        return jugadorId;
    }

    public void modificarJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String obtenerNombreJugador() {
        return nombreJugador;
    }

    public void modificarNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
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
