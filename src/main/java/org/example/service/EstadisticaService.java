package org.example.service;

import org.example.dao.EstadisticaDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.model.Jugador;
import org.example.model.Partida;

import java.util.List;

public class EstadisticaService {
    private EstadisticaDAO estadisticaDAO;
    private PartidaDAO partidaDAO;

    public EstadisticaService() {
        estadisticaDAO = new EstadisticaDAO();
        partidaDAO = new PartidaDAO();
    }

    public void registrarPartida(Partida partida) {

        if (partida == null || partida.obtenerJugadores() == null) {
            return;
        }

        for (Jugador jugador : partida.obtenerJugadores()) {

            if (jugador.obtenerId() == null) {
                continue;
            }

            EstadisticaDTO estadistica =
                    estadisticaDAO.consultar(jugador.obtenerId());

            if (estadistica == null) {
                estadistica = new EstadisticaDTO(0, 0);
            }

            int partidasJugadas =
                    estadistica.obtenerPartidasJugadas() + 1;

            int partidasGanadas =
                    estadistica.obtenerPartidasGanadas();

            estadistica.modificarPartidasJugadas(partidasJugadas);

            estadisticaDAO.guardar(
                    jugador.obtenerId(),
                    estadistica
            );
        }
    }

    public List<EstadisticaDTO> consultarEstadisticas() {
        return estadisticaDAO.listar();
    }

    public List<PartidaDTO> consultarHistorial() {
        return partidaDAO.listar();
    }
}
