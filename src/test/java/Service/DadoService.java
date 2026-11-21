package Service;

import org.example.model.Dado;


public class DadoService {

    @Test
    public void probarCrearDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        assertNotNull(dado);
    }

    @Test
    public void probarLanzarDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        int resultado = servicio.lanzarDado(dado);

        assertTrue(resultado >= 1 && resultado <= 6);
    }


    @Test
    public void probarValorDelDado() {
        DadoService servicio = new DadoService();

        Dado dado = servicio.crearDado();

        servicio.lanzarDado(dado);

        assertEquals(dado.obtenerValor(),
                servicio.obtenerValor(dado));
    }

}
