package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.example.model.Tablero;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TableroService {

    public static final int TOTAL_CASILLAS_EXTERIOR = 68;
    public static final int PASOS_ENTRADA_PASILLO = 63;
    public static final int PASOS_META = 71;
    public static final int POSICION_INICIO_PASILLO = 101;
    public static final int POSICION_META = 108;

    public static final int SALIDA_ROJO = 1;
    public static final int SALIDA_AZUL = 18;
    public static final int SALIDA_VERDE = 35;
    public static final int SALIDA_AMARILLO = 52;

    private static final Set<Integer> CASILLAS_SEGURAS = Set.of(8, 13, 25, 30, 42, 47, 59, 64);

    private final CasillaService casillaService;

    public TableroService() {
        this(new CasillaService());
    }

    public TableroService(CasillaService casillaService) {
        this.casillaService = casillaService;
    }

    public static int obtenerPosicionSalida(ColorJugador color) {
        if (color == null) {
            throw new IllegalArgumentException("El color del jugador no puede ser nulo");
        }
        return switch (color) {
            case ROJO -> SALIDA_ROJO;
            case AZUL -> SALIDA_AZUL;
            case VERDE -> SALIDA_VERDE;
            case AMARILLO -> SALIDA_AMARILLO;
        };
    }

    public Tablero crearTablero() {
        Tablero tablero = new Tablero();
        tablero.modificarCasillas(construirCasillasBase());
        return tablero;
    }

    public void reiniciarTablero(Tablero tablero) {
        if (tablero == null) {
            return;
        }
        tablero.modificarCasillas(construirCasillasBase());
    }

    public Casilla obtenerCasilla(Tablero tablero, int posicion) {
        if (posicion == POSICION_META) {
            return casillaService.crearCasilla(POSICION_META, TipoCasilla.META, null);
        }
        if (posicion >= POSICION_INICIO_PASILLO && posicion < POSICION_META) {
            return casillaService.crearCasilla(posicion, TipoCasilla.SEGURO, null);
        }
        return casillaService.obtenerCasilla(tablero, posicion);
    }

    public boolean validarMovimiento(Tablero tablero, Ficha ficha, int posicionDestino) {
        if (tablero == null || ficha == null || ficha.obtenerEstado() == EstadoFicha.META) {
            return false;
        }
        if (posicionDestino >= 1 && posicionDestino <= TOTAL_CASILLAS_EXTERIOR) {
            return obtenerCasilla(tablero, posicionDestino) != null;
        }
        return posicionDestino >= POSICION_INICIO_PASILLO && posicionDestino <= POSICION_META;
    }

    public void agregarCasilla(Tablero tablero, Casilla casilla) {
        tablero.obtenerCasillas().add(casilla);
    }

    public List<Casilla> obtenerCasillas(Tablero tablero) {
        return tablero.obtenerCasillas();
    }

    public int contarCasillas(Tablero tablero) {
        return tablero.obtenerCasillas().size();
    }

    public boolean estaTableroListo() {
        return true;
    }

    public boolean estaTableroListo(Tablero tablero) {
        return tablero != null
                && tablero.obtenerCasillas() != null
                && tablero.obtenerCasillas().size() >= TOTAL_CASILLAS_EXTERIOR;
    }

    private List<Casilla> construirCasillasBase() {
        List<Casilla> casillas = new ArrayList<>(TOTAL_CASILLAS_EXTERIOR);
        for (int numero = 1; numero <= TOTAL_CASILLAS_EXTERIOR; numero++) {
            TipoCasilla tipo = TipoCasilla.NORMAL;
            ColorJugador color = null;

            if (numero == SALIDA_ROJO) {
                tipo = TipoCasilla.SALIDA;
                color = ColorJugador.ROJO;
            } else if (numero == SALIDA_AZUL) {
                tipo = TipoCasilla.SALIDA;
                color = ColorJugador.AZUL;
            } else if (numero == SALIDA_VERDE) {
                tipo = TipoCasilla.SALIDA;
                color = ColorJugador.VERDE;
            } else if (numero == SALIDA_AMARILLO) {
                tipo = TipoCasilla.SALIDA;
                color = ColorJugador.AMARILLO;
            } else if (CASILLAS_SEGURAS.contains(numero)) {
                tipo = TipoCasilla.SEGURO;
            }

            casillas.add(casillaService.crearCasilla(numero, tipo, color));
        }
        return casillas;
    }
}
