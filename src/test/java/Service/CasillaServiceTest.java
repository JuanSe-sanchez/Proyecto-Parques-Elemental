package Service;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.service.CasillaService;

public class CasillaServiceTest {

    @Test
    public void probarCrearCasilla() {
        CasillaService servicio = new CasillaService();

        Casilla casilla = servicio.crearCasilla(
                5, TipoCasilla.NORMAL, ColorJugador.ROJO
        );

        assertNotNull(casilla);
        assertEquals(5, servicio.obtenerNumero(casilla));
    }

    @Test
    public void probarTipoCasilla() {
        CasillaService servicio = new CasillaService();

        Casilla casilla = servicio.crearCasilla(
                5, TipoCasilla.NORMAL, ColorJugador.ROJO
        );

        assertEquals(TipoCasilla.NORMAL,
                servicio.obtenerTipo(casilla));
    }
    
    @Test
    public void probarObtenerCantidadCasillas() {
        CasillaService servicio = new CasillaService();
        assertEquals(68, servicio.obtenerCantidadCasillas());
    }

}
