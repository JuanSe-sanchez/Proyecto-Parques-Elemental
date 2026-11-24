package org.example.service;

import org.example.dao.EstadisticaDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.model.Jugador;
import org.example.model.Partida;

import java.time.LocalDateTime;
import java.util.List;

public class EstadisticaService {

    private final EstadisticaDAO estadisticaDAO;
    private final PartidaDAO partidaDAO;

    public EstadisticaService() {
        this(new EstadisticaDAO(), new PartidaDAO());
    }

    public EstadisticaService(EstadisticaDAO estadisticaDAO, PartidaDAO partidaDAO) {
        this.estadisticaDAO = estadisticaDAO;
        this.partidaDAO = partidaDAO;
    }

    public void registrarPartida(Partida partida) {
        if (partida == null) {
            return;
        }

        Jugador ganador = partida.obtenerGanador();
        String nombreGanador = ganador != null ? ganador.obtenerNombre() : null;

        if (partida.obtenerId() != null) {
            if (ganador != null && ganador.obtenerId() != null) {
                partidaDAO.actualizarFinalizacion(partida.obtenerId(), ganador.obtenerId(), nombreGanador);
            } else {
                partidaDAO.actualizarFinalizacion(partida.obtenerId(), nombreGanador);
            }
        } else if (nombreGanador != null) {
            PartidaDTO dto = new PartidaDTO(
                    null,
                    partida.obtenerFechaInicio() != null ? partida.obtenerFechaInicio() : LocalDateTime.now(),
                    nombreGanador
            );
            Long idGenerado = partidaDAO.guardar(dto);
            partida.modificarId(idGenerado);
        }

        if (partida.obtenerJugadores() == null) {
            return;
        }

        for (Jugador jugador : partida.obtenerJugadores()) {
            if (jugador == null || jugador.obtenerId() == null) {
                continue;
            }

            EstadisticaDTO estadistica = estadisticaDAO.consultar(jugador.obtenerId());
            if (estadistica == null) {
                estadistica = new EstadisticaDTO(jugador.obtenerId(), jugador.obtenerNombre(), 0, 0);
            }

            int partidasJugadas = estadistica.obtenerPartidasJugadas() + 1;
            int partidasGanadas = estadistica.obtenerPartidasGanadas();

            if (ganador != null && esMismoJugador(jugador, ganador)) {
                partidasGanadas++;
            }

            estadistica.modificarPartidasJugadas(partidasJugadas);
            estadistica.modificarPartidasGanadas(partidasGanadas);

            estadisticaDAO.guardar(jugador.obtenerId(), estadistica);
        }
    }

    public List<EstadisticaDTO> consultarEstadisticas() {
        return estadisticaDAO.listar();
    }

    public List<PartidaDTO> consultarHistorial() {
        return partidaDAO.listar();
    }

    private boolean esMismoJugador(Jugador a, Jugador b) {
        if (a == b) {
            return true;
        }
        if (a.obtenerId() != null && b.obtenerId() != null) {
            return a.obtenerId().equals(b.obtenerId());
        }
        return a.obtenerNombre() != null && a.obtenerNombre().equalsIgnoreCase(b.obtenerNombre());
    }
}
