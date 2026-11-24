package org.example.service;

import org.example.dao.EstadisticaDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.enums.ColorJugador;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EstadisticaServiceTest {

    @Test
    public void probarRegistrarPartidaIncrementaJugadasYGanadas() {
        Map<Long, EstadisticaDTO> tablaEstadisticas = new HashMap<>();
        List<PartidaDTO> historial = new ArrayList<>();

        EstadisticaDAO estadisticaDAO = new EstadisticaDAO() {
            @Override
            public void guardar(long jugadorId, EstadisticaDTO estadistica) {
                tablaEstadisticas.put(jugadorId, estadistica);
            }

            @Override
            public EstadisticaDTO consultar(long jugadorId) {
                return tablaEstadisticas.get(jugadorId);
            }

            @Override
            public List<EstadisticaDTO> listar() {
                return new ArrayList<>(tablaEstadisticas.values());
            }
        };

        PartidaDAO partidaDAO = new PartidaDAO() {
            private long seq = 1;

            @Override
            public Long guardar(PartidaDTO partida) {
                long id = seq++;
                partida.modificarId(id);
                historial.add(partida);
                return id;
            }

            @Override
            public void actualizarFinalizacion(long partidaId, String ganador) {
                historial.stream()
                        .filter(p -> p.obtenerId() == partidaId)
                        .findFirst()
                        .ifPresent(p -> p.modificarGanador(ganador));
            }

            @Override
            public List<PartidaDTO> listar() {
                return historial;
            }
        };

        EstadisticaService servicio = new EstadisticaService(estadisticaDAO, partidaDAO);

        Jugador ganador = new Jugador();
        ganador.modificarId(1L);
        ganador.modificarNombre("Sofia");
        ganador.modificarColor(ColorJugador.ROJO);

        Jugador perdedor = new Jugador();
        perdedor.modificarId(2L);
        perdedor.modificarNombre("Carlos");
        perdedor.modificarColor(ColorJugador.AZUL);

        Partida partida = new Partida();
        partida.modificarFechaInicio(LocalDateTime.now());
        partida.modificarJugadores(List.of(ganador, perdedor));
        partida.modificarGanador(ganador);

        // Registrar 2 partidas ganadas por Sofia
        servicio.registrarPartida(partida);
        servicio.registrarPartida(partida);

        assertEquals(2, tablaEstadisticas.get(1L).obtenerPartidasJugadas());
        assertEquals(2, tablaEstadisticas.get(1L).obtenerPartidasGanadas());
        assertEquals(2, tablaEstadisticas.get(2L).obtenerPartidasJugadas());
        assertEquals(0, tablaEstadisticas.get(2L).obtenerPartidasGanadas());
        assertEquals(2, servicio.consultarEstadisticas().size());
        assertEquals(1, servicio.consultarHistorial().size());
        assertEquals("Sofia", servicio.consultarHistorial().get(0).obtenerGanador());
    }
}
