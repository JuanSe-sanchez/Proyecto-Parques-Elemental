package Service;

import org.example.enums.ColorJugador;
import org.example.model.Jugador;
import org.example.service.JugadorService;

public class JugadorServiceTest {

    @Test
    public void probarCrearJugador() {
        JugadorService servicio = new JugadorService();

        Jugador jugador = servicio.crearJugador(
                "Juan", ColorJugador.ROJO
        );

        assertNotNull(jugador);
        assertEquals("Juan", jugador.obtenerNombre());
    }

    @Test
    public void probarPuntajeInicial() {
        JugadorService servicio = new JugadorService();

        Jugador jugador = servicio.crearJugador(
                "Juan", ColorJugador.ROJO
        );

        assertEquals(0, servicio.obtenerPuntaje(jugador));
    }

    @Test
    public void probarNombreJugador() {
        JugadorService servicio = new JugadorService();

        Jugador jugador = servicio.crearJugador(
                "Juan", ColorJugador.ROJO
        );

        assertEquals("Juan", servicio.obtenerNombre(jugador));
    }
    
  @Test
    public void probarColorJugador() {
        JugadorService servicio = new JugadorService();
        Jugador jugador = servicio.crearJugador(
                "Juan", ColorJugador.ROJO
        );

        assertEquals(ColorJugador.ROJO, jugador.obtenerColor());
    }
    @Test
    public void probarCantidadJugadores() {
        JugadorService servicio = new JugadorService();
        
        assertEquals(4, servicio.obtenerCantidadJugadores());
    }
}
