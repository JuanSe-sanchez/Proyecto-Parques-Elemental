package Service;

import org.example.enums.ColorJugador;
import org.example.model.Ficha;
import org.example.model.Movimiento;
import org.example.service.FichaService;
import org.example.service.MovimientoService;

public class MovimientoServiceTest {

    @Test
    public void probarRegistrarMovimiento() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();

        Ficha ficha = servicioFicha.crearFicha(ColorJugador.ROJO);

        Movimiento movimiento = servicio.registrarMovimiento(
                ficha, 0, 5
        );

        assertNotNull(movimiento);
    }

    @Test
    public void probarPosicionInicial() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();

        Ficha ficha = servicioFicha.crearFicha(ColorJugador.ROJO);

        Movimiento movimiento = servicio.registrarMovimiento(
                ficha, 0, 5
        );

        assertEquals(0,
                servicio.obtenerPosicionInicial(movimiento));
    }

    @Test
    public void probarPosicionFinal() {
        FichaService servicioFicha = new FichaService();
        MovimientoService servicio = new MovimientoService();

        Ficha ficha = servicioFicha.crearFicha(ColorJugador.ROJO);

        Movimiento movimiento = servicio.registrarMovimiento(
                ficha, 0, 5
        );

        assertEquals(5,
                servicio.obtenerPosicionFinal(movimiento));
    }
    
    @Test
    public void probarContarMovimientos() {
        MovimientoService servicio = new MovimientoService();
        assertEquals(0, servicio.contarMovimientos());
    }
}
