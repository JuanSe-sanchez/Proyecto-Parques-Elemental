package org.example.dto;

import java.time.LocalDateTime;

public class PartidaDTO {

    private Long id;
    private LocalDateTime fecha;
    private String ganador;

    public PartidaDTO() {
    }

    public PartidaDTO(Long id, LocalDateTime fecha, String ganador) {
        this.id = id;
        this.fecha = fecha;
        this.ganador = ganador;
    }

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public LocalDateTime obtenerFecha() {
        return fecha;
    }

    public void modificarFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String obtenerGanador() {
        return ganador;
    }

    public void modificarGanador(String ganador) {
        this.ganador = ganador;
    }

}
