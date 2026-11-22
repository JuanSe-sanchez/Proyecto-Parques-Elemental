package org.example.service;

import org.example.dao.EstadisticaDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.model.Partida;

import java.util.List;

public class EstadisticaService {

    private EstadisticaDAO estadisticaDAO;
    private PartidaDAO partidaDAO;

    public void registrarPartida(Partida partida) {

    }

    public List<EstadisticaDTO> consultarEstadisticas() {
        return null;
    }

    public List<PartidaDTO> consultarHistorial() {
        return null;
    }

}
