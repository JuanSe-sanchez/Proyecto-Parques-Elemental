package org.example.service;

import org.example.enums.EstadoPartida;
import org.example.model.Dado;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.example.model.Tablero;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidaService {

    public Partida crearPartida() {
        Partida partida = new Partida();

        partida.modificarJugadores(new ArrayList<>());
        partida.modificarTablero(new Tablero());
        partida.modificarDado(new Dado());
        partida.modificarEstado(EstadoPartida.EN_CURSO);
        partida.modificarFechaInicio(LocalDateTime.now());

        return partida;
    }

    public void iniciarPartida(Partida partida) {
        partida.modificarEstado(EstadoPartida.EN_CURSO);
        partida.modificarFechaInicio(LocalDateTime.now());
    }

    public void finalizarPartida(Partida partida) {
        partida.modificarEstado(EstadoPartida.FINALIZADA);
    }

    public void agregarJugador(Partida partida, Jugador jugador) {
        partida.obtenerJugadores().add(jugador);
    }

    public List<Jugador> obtenerJugadores(Partida partida) {
        return partida.obtenerJugadores();
    }

    public EstadoPartida obtenerEstado(Partida partida) {
        return partida.obtenerEstado();
    }


}
