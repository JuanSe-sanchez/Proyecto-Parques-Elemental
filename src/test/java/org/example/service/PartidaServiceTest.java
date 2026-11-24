package org.example.service;

import org.example.dao.EstadisticaDAO;
import org.example.dao.JugadorDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.JugadorDTO;
import org.example.dto.PartidaDTO;
import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.enums.EstadoPartida;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PartidaServiceTest {

    private PartidaService crearServicioEnMemoria() {
        PartidaDAO partidaDAO = new PartidaDAO() {
            private long seq = 1;
            @Override
            public Long guardar(PartidaDTO partida) {
                long id = seq++;
                partida.modificarId(id);
                return id;
            }
            @Override
            public void actualizarFinalizacion(long partidaId, String ganador) {
            }
        };
        JugadorDAO jugadorDAO = new JugadorDAO() {
            private long seq = 1;
            @Override
            public Long guardar(JugadorDTO jugador, int puntajeInicial) {
                long id = seq++;
                jugador.modificarId(id);
                return id;
            }
            @Override
            public JugadorDTO consultarPorNombre(String nombre) {
                return null;
            }
            @Override
            public void actualizarPuntaje(long id, int puntaje) {
            }
        };
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO() {
            private final Map<Long, EstadisticaDTO> store = new HashMap<>();
            @Override
            public void guardar(long jugadorId, EstadisticaDTO estadistica) {
                store.put(jugadorId, estadistica);
            }
            @Override
            public EstadisticaDTO consultar(long jugadorId) {
                return store.get(jugadorId);
            }
        };
        FichaService fichaService = new FichaService();
        JugadorService jugadorService = new JugadorService(jugadorDAO, fichaService);
        EstadisticaService estadisticaService = new EstadisticaService(estadisticaDAO, partidaDAO);
        return new PartidaService(
                partidaDAO,
                jugadorService,
                new TableroService(),
                new DadoService(),
                fichaService,
                new ReglaService(),
                new MovimientoService(),
                estadisticaService
        );
    }

    @Test
    public void probarCrearIniciarYFinalizarPartida() {
        PartidaService servicio = crearServicioEnMemoria();

        Partida partida = servicio.crearPartida();
        assertNotNull(partida);
        assertEquals(68, partida.obtenerTablero().obtenerCasillas().size());

        servicio.iniciarPartida(partida);
        assertEquals(EstadoPartida.EN_CURSO, servicio.obtenerEstado(partida));
        assertTrue(servicio.estaPartidaActiva(partida));

        servicio.finalizarPartida(partida);
        assertEquals(EstadoPartida.FINALIZADA, servicio.obtenerEstado(partida));
    }

    @Test
    public void probarTurnosSalidaCapturaYVictoria() {
        PartidaService servicio = crearServicioEnMemoria();
        FichaService fichaService = new FichaService();
        JugadorService jugadorService = new JugadorService(new JugadorDAO() {
            @Override
            public Long guardar(JugadorDTO jugador, int puntajeInicial) { return 1L; }
            @Override
            public JugadorDTO consultarPorNombre(String nombre) { return null; }
            @Override
            public void actualizarPuntaje(long id, int puntaje) {}
        }, fichaService);

        Partida partida = servicio.crearPartida();
        Jugador rojo = jugadorService.crearJugador("Rojo", ColorJugador.ROJO);
        Jugador azul = jugadorService.crearJugador("Azul", ColorJugador.AZUL);
        servicio.agregarJugador(partida, rojo);
        servicio.agregarJugador(partida, azul);
        servicio.iniciarPartida(partida);

        assertEquals(rojo, servicio.obtenerJugadorActual(partida));
        servicio.cambiarTurno(partida);
        assertEquals(azul, servicio.obtenerJugadorActual(partida));
        servicio.cambiarTurno(partida);
        assertEquals(rojo, servicio.obtenerJugadorActual(partida));

        // Salida de casa de rojo con pares
        Ficha fichaRoja = rojo.obtenerFichas().get(0);
        PartidaService.ResultadoJugada salida = servicio.ejecutarJugada(partida, rojo, fichaRoja, 6, true);
        assertTrue(salida.exitosa());
        assertTrue(salida.salioDeCasa());
        assertEquals(TableroService.SALIDA_ROJO, fichaRoja.obtenerPosicion());

        // Colocar una ficha azul en casilla 6 (NORMAL) y capturarla con rojo (+5 desde casilla 1)
        Ficha fichaAzul = azul.obtenerFichas().get(0);
        fichaService.sacarDeCasa(fichaAzul);
        fichaAzul.modificarPosicion(6);

        PartidaService.ResultadoJugada captura = servicio.ejecutarJugada(partida, rojo, fichaRoja, 5, false);
        assertTrue(captura.exitosa());
        assertEquals(1, captura.fichasCapturadas().size());
        assertEquals(EstadoFicha.CASA, fichaAzul.obtenerEstado());
        assertEquals(PartidaService.PUNTOS_CAPTURA, rojo.obtenerPuntaje());

        // Llevar las otras 3 fichas de rojo a meta y coronar la cuarta
        for (int i = 1; i < 4; i++) {
            fichaService.enviarAFinal(rojo.obtenerFichas().get(i));
        }
        fichaRoja.modificarPasosRecorridos(TableroService.PASOS_META - 3);
        PartidaService.ResultadoJugada victoria = servicio.ejecutarJugada(partida, rojo, fichaRoja, 3, false);

        assertTrue(victoria.exitosa());
        assertTrue(victoria.llegoAMeta());
        assertEquals(rojo, victoria.ganador());
        assertEquals(EstadoPartida.FINALIZADA, partida.obtenerEstado());
    }

    @Test
    public void probarPartidaActiva() {
        PartidaService servicio = crearServicioEnMemoria();
        assertTrue(servicio.estaPartidaActiva());
    }
}
