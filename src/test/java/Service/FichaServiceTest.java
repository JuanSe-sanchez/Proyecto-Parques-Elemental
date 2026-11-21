package Service;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.model.Ficha;
import org.example.service.FichaService;

public class FichaServiceTest {

    @Test
    public void probarCrearFicha() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        assertNotNull(ficha);
        assertEquals(EstadoFicha.CASA,
                servicio.obtenerEstado(ficha));
    }

    @Test
    public void probarMoverFicha() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        servicio.moverFicha(ficha, 10);

        assertEquals(10, servicio.obtenerPosicion(ficha));
    }

    @Test
    public void probarEnviarFichaAMeta() {
        FichaService servicio = new FichaService();

        Ficha ficha = servicio.crearFicha(ColorJugador.AZUL);

        servicio.enviarAFinal(ficha);

        assertEquals(EstadoFicha.META,
                servicio.obtenerEstado(ficha));
    }


}
