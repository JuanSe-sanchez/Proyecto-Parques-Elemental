package Service;

import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Tablero;
import org.example.service.CasillaService;
import org.example.service.TableroService;

public class TableroServiceTest {

    @Test
    public void probarCrearTablero() {
        TableroService servicio = new TableroService();

        Tablero tablero = servicio.crearTablero();

        assertNotNull(tablero);
    }

    @Test
    public void probarAgregarCasilla() {
        TableroService servicio = new TableroService();
        CasillaService servicioCasilla = new CasillaService();

        Tablero tablero = servicio.crearTablero();

        Casilla casilla = servicioCasilla.crearCasilla(
                1, TipoCasilla.NORMAL, null
        );

        servicio.agregarCasilla(tablero, casilla);

        assertEquals(1, servicio.contarCasillas(tablero));
    }
    
    @Test
    public void probarTableroInicializado() {
        TableroService servicio = new TableroService();
        
        assertTrue(servicio.estaTableroListo());
    }
}
