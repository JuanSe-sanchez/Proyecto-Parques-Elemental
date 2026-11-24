package org.example.controller;

import org.example.dao.EstadisticaDAO;
import org.example.dao.JugadorDAO;
import org.example.dao.PartidaDAO;
import org.example.dto.EstadisticaDTO;
import org.example.dto.JugadorDTO;
import org.example.dto.PartidaDTO;
import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.service.DadoService;
import org.example.service.EstadisticaService;
import org.example.service.FichaService;
import org.example.service.JugadorService;
import org.example.service.MovimientoService;
import org.example.service.PartidaService;
import org.example.service.ReglaService;
import org.example.service.TableroService;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JuegoControllerTest {

    private static class DadoDeterminista extends Random {
        private final Queue<Integer> valores = new ArrayDeque<>();

        DadoDeterminista(int... lanzamientos) {
            for (int v : lanzamientos) {
                valores.add(v - 1); // nextInt(6) + 1
            }
        }

        @Override
        public int nextInt(int bound) {
            return valores.isEmpty() ? 0 : valores.poll();
        }
    }

    private JuegoController crearControllerConDados(int... lanzamientos) {
        PartidaDAO partidaDAO = new PartidaDAO() {
            @Override
            public Long guardar(PartidaDTO partida) { return 1L; }
            @Override
            public void actualizarFinalizacion(long partidaId, String ganador) {}
        };
        JugadorDAO jugadorDAO = new JugadorDAO() {
            @Override
            public Long guardar(JugadorDTO jugador, int puntajeInicial) { return 1L; }
            @Override
            public JugadorDTO consultarPorNombre(String nombre) { return null; }
            @Override
            public void actualizarPuntaje(long id, int puntaje) {}
        };
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO() {
            @Override
            public void guardar(long jugadorId, EstadisticaDTO estadistica) {}
            @Override
            public EstadisticaDTO consultar(long jugadorId) { return null; }
        };

        DadoService dadoService = new DadoService(new DadoDeterminista(lanzamientos));
        FichaService fichaService = new FichaService();
        ReglaService reglaService = new ReglaService();
        JugadorService jugadorService = new JugadorService(jugadorDAO, fichaService);
        PartidaService partidaService = new PartidaService(
                partidaDAO,
                jugadorService,
                new TableroService(),
                dadoService,
                fichaService,
                reglaService,
                new MovimientoService(reglaService),
                new EstadisticaService(estadisticaDAO, partidaDAO)
        );
        return new JuegoController(partidaService, dadoService, fichaService, jugadorService, reglaService);
    }

    @Test
    public void probarTresIntentosSinParesYCambioDeTurno() {
        // 3 lanzamientos sin pares para Jugador 1: (1,2), (3,4), (5,6)
        JuegoController controller = crearControllerConDados(1, 2, 3, 4, 5, 6);
        JugadorService js = new JugadorService(new JugadorDAO() {
            @Override public Long guardar(JugadorDTO j, int p) { return 1L; }
            @Override public JugadorDTO consultarPorNombre(String n) { return null; }
        }, new FichaService());

        Jugador j1 = js.crearJugador("Ana", ColorJugador.ROJO);
        Jugador j2 = js.crearJugador("Beto", ColorJugador.AZUL);
        controller.iniciarPartidaConJugadores(List.of(j1, j2));

        controller.lanzarDosDados();
        assertEquals(j1, controller.obtenerJugadorActual());
        assertTrue(controller.estaEsperandoLanzamiento());

        controller.lanzarDosDados();
        assertEquals(j1, controller.obtenerJugadorActual());

        controller.lanzarDosDados();
        assertEquals(j2, controller.obtenerJugadorActual());
    }

    @Test
    public void probarSalidaConParesYTurnoExtra() {
        // Par 4-4 para salir y luego mover la misma ficha 4 pasos
        JuegoController controller = crearControllerConDados(4, 4, 2, 3);
        JugadorService js = new JugadorService(new JugadorDAO() {
            @Override public Long guardar(JugadorDTO j, int p) { return 1L; }
            @Override public JugadorDTO consultarPorNombre(String n) { return null; }
        }, new FichaService());

        Jugador j1 = js.crearJugador("Ana", ColorJugador.ROJO);
        Jugador j2 = js.crearJugador("Beto", ColorJugador.AZUL);
        controller.iniciarPartidaConJugadores(List.of(j1, j2));

        int[] dados = controller.lanzarDosDados();
        assertEquals(4, dados[0]);
        assertEquals(4, dados[1]);
        assertTrue(controller.esLanzamientoPar());
        assertFalse(controller.estaEsperandoLanzamiento());

        Ficha f1 = j1.obtenerFichas().get(0);
        // Saca ficha 1 de casa (consume primer 4)
        assertTrue(controller.moverFichaConPasos(f1, 4).exitosa());
        assertEquals(EstadoFicha.RECORRIDO, f1.obtenerEstado());
        assertEquals(TableroService.SALIDA_ROJO, f1.obtenerPosicion());

        // Mueve ficha 1 con el segundo 4 -> casilla 5 (1 + 4)
        assertTrue(controller.moverFichaConPasos(f1, 4).exitosa());
        assertEquals(5, f1.obtenerPosicion());

        // Por haber sacado par, sigue siendo turno de Ana y debe volver a lanzar
        assertEquals(j1, controller.obtenerJugadorActual());
        assertTrue(controller.estaEsperandoLanzamiento());
    }

    @Test
    public void probarTresIntentosConTresEnCasaYUnaEnMeta() {
        // 3 lanzamientos sin pares: (1,3), (2,5), (4,6)
        JuegoController controller = crearControllerConDados(1, 3, 2, 5, 4, 6);
        FichaService fichaService = new FichaService();
        JugadorService js = new JugadorService(new JugadorDAO() {
            @Override public Long guardar(JugadorDTO j, int p) { return 1L; }
            @Override public JugadorDTO consultarPorNombre(String n) { return null; }
        }, fichaService);

        Jugador j1 = js.crearJugador("Ana", ColorJugador.ROJO);
        Jugador j2 = js.crearJugador("Beto", ColorJugador.AZUL);
        // 1 ficha ya en META y 3 en CASA (0 activas en recorrido)
        fichaService.enviarAFinal(j1.obtenerFichas().get(0));
        controller.iniciarPartidaConJugadores(List.of(j1, j2));

        // Intento 1 fallido -> sigue Ana
        controller.lanzarDosDados();
        assertEquals(j1, controller.obtenerJugadorActual());
        assertTrue(controller.estaEsperandoLanzamiento());

        // Intento 2 fallido -> sigue Ana
        controller.lanzarDosDados();
        assertEquals(j1, controller.obtenerJugadorActual());
        assertTrue(controller.estaEsperandoLanzamiento());

        // Intento 3 fallido -> cambia turno a Beto
        controller.lanzarDosDados();
        assertEquals(j2, controller.obtenerJugadorActual());
    }
}
