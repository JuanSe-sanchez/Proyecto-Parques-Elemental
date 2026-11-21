package org.example.model;

import org.example.enums.EstadoPartida;

import java.util.List;
import java.time.LocalDateTime;

public class Partida {

    private Long id;
    private List<Jugador> jugadores;
    private Tablero tablero;
    private Dado dado;
    private EstadoPartida estado;
    private LocalDateTime fechaInicio;

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public List<Jugador> obtenerJugadores() {
        return jugadores;
    }

    public void modificarJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Tablero obtenerTablero() {
        return tablero;
    }

    public void modificarTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public Dado obtenerDado() {
        return dado;
    }

    public void modificarDado(Dado dado) {
        this.dado = dado;
    }

    public EstadoPartida obtenerEstado() {
        return estado;
    }

    public void modificarEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public LocalDateTime obtenerFechaInicio() {
        return fechaInicio;
    }

    public void modificarFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }


}