package org.example.controller;

import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.enums.ColorJugador;
import org.example.model.Jugador;
import org.example.service.EstadisticaService;
import org.example.service.JugadorService;

import java.util.List;

public class JugadorController {

    private final JugadorService jugadorService;
    private final EstadisticaService estadisticaService;

    public JugadorController() {
        this(new JugadorService(), new EstadisticaService());
    }

    public JugadorController(JugadorService jugadorService, EstadisticaService estadisticaService) {
        this.jugadorService = jugadorService;
        this.estadisticaService = estadisticaService;
    }

    public void registrarJugador() {
        // Sobrecarga de compatibilidad; usar registrarJugador(Jugador) o registrarJugador(String, ColorJugador)
    }

    public Jugador registrarJugador(String nombre, ColorJugador color) {
        Jugador jugador = jugadorService.crearJugador(nombre, color);
        registrarJugador(jugador);
        return jugador;
    }

    public void registrarJugador(Jugador jugador) {
        jugadorService.registrarJugador(jugador);
    }

    public void seleccionarColor() {
        // Sobrecarga de compatibilidad; usar seleccionarColor(Jugador, ColorJugador)
    }

    public void seleccionarColor(Jugador jugador, ColorJugador color) {
        jugadorService.seleccionarColor(jugador, color);
    }

    public List<EstadisticaDTO> consultarEstadisticas() {
        return estadisticaService.consultarEstadisticas();
    }

    public List<PartidaDTO> consultarHistorial() {
        return estadisticaService.consultarHistorial();
    }
}
