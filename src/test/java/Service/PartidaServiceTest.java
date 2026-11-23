package Service;

import org.example.enums.EstadoPartida;
import org.example.model.Partida;
import org.example.service.PartidaService;

public class PartidaServiceTest {

    @Test
    public void probarCrearPartida() {
        PartidaService servicio = new PartidaService();

        Partida partida = servicio.crearPartida();

        assertNotNull(partida);
    }

    @Test
    public void probarIniciarPartida() {
        PartidaService servicio = new PartidaService();
        Partida partida = servicio.crearPartida();

        servicio.iniciarPartida(partida);

        assertEquals(EstadoPartida.EN_CURSO,
                servicio.obtenerEstado(partida));
    }

    @Test
    public void probarFinalizarPartida() {
        PartidaService servicio = new PartidaService();
        Partida partida = servicio.crearPartida();

        servicio.finalizarPartida(partida);

        assertEquals(EstadoPartida.FINALIZADA,
                servicio.obtenerEstado(partida));
    }
    @Test
    public void probarCicloCompletoPartida() {
        PartidaService servicio = new PartidaService();
        Partida partida = servicio.crearPartida();
        assertNotNull(partida);

        servicio.iniciarPartida(partida);
        assertEquals(EstadoPartida.EN_CURSO, servicio.obtenerEstado(partida));

        servicio.finalizarPartida(partida);
        assertEquals(EstadoPartida.FINALIZADA, servicio.obtenerEstado(partida));
    }

}
