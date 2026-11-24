package org.example.service;

import org.example.dao.JugadorDAO;
import org.example.dto.JugadorDTO;
import org.example.enums.ColorJugador;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JugadorServiceTest {

    private static class JugadorDAOMemoria extends JugadorDAO {
        private long secuencia = 1;
        private final Map<Long, JugadorDTO> datos = new HashMap<>();
        private final Map<Long, Integer> puntajes = new HashMap<>();

        @Override
        public Long guardar(JugadorDTO jugador, int puntajeInicial) {
            long id = secuencia++;
            jugador.modificarId(id);
            datos.put(id, jugador);
            puntajes.put(id, puntajeInicial);
            return id;
        }

        @Override
        public JugadorDTO consultar(long id) {
            return datos.get(id);
        }

        @Override
        public JugadorDTO consultarPorNombre(String nombre) {
            return datos.values().stream()
                    .filter(j -> j.obtenerNombre().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void actualizarPuntaje(long id, int puntaje) {
            puntajes.put(id, puntaje);
        }
    }

    @Test
    public void probarCrearJugadorConCuatroFichas() {
        JugadorService servicio = new JugadorService(new JugadorDAOMemoria(), new FichaService());

        Jugador jugador = servicio.crearJugador("Juan", ColorJugador.ROJO);

        assertNotNull(jugador);
        assertEquals("Juan", servicio.obtenerNombre(jugador));
        assertEquals(ColorJugador.ROJO, jugador.obtenerColor());
        assertEquals(0, servicio.obtenerPuntaje(jugador));
        assertEquals(4, servicio.obtenerFichas(jugador).size());
        assertTrue(servicio.tieneTodasEnCasa(jugador));
        assertFalse(servicio.tieneTodasEnMeta(jugador));
    }

    @Test
    public void probarRegistrarJugadorYActualizarPuntaje() {
        JugadorDAOMemoria dao = new JugadorDAOMemoria();
        JugadorService servicio = new JugadorService(dao, new FichaService());

        Jugador jugador = servicio.crearJugador("Ana", ColorJugador.VERDE);
        servicio.registrarJugador(jugador);

        assertNotNull(jugador.obtenerId());
        servicio.actualizarPuntaje(jugador, 50);
        assertEquals(50, jugador.obtenerPuntaje());

        Jugador recuperado = servicio.obtenerJugador(jugador.obtenerId());
        assertNotNull(recuperado);
        assertEquals("Ana", recuperado.obtenerNombre());
    }

    @Test
    public void probarSeleccionarColorYMeta() {
        FichaService fichaService = new FichaService();
        JugadorService servicio = new JugadorService(new JugadorDAOMemoria(), fichaService);

        Jugador jugador = servicio.crearJugador("Luis", ColorJugador.AZUL);
        servicio.seleccionarColor(jugador, ColorJugador.AMARILLO);

        assertEquals(ColorJugador.AMARILLO, jugador.obtenerColor());
        for (Ficha f : jugador.obtenerFichas()) {
            assertEquals(ColorJugador.AMARILLO, f.obtenerColor());
            fichaService.enviarAFinal(f);
        }
        assertTrue(servicio.tieneTodasEnMeta(jugador));
    }

    @Test
    public void probarCantidadJugadores() {
        JugadorService servicio = new JugadorService(new JugadorDAOMemoria(), new FichaService());
        assertEquals(4, servicio.obtenerCantidadJugadores());
    }
}
