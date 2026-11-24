package org.example.service;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.model.Casilla;
import org.example.model.Ficha;

import java.util.ArrayList;
import java.util.List;

public class FichaService {

    private final ReglaService reglaService;

    public FichaService() {
        this(new ReglaService());
    }

    public FichaService(ReglaService reglaService) {
        this.reglaService = reglaService;
    }

    public Ficha crearFicha(ColorJugador color) {
        Ficha ficha = new Ficha();
        ficha.modificarPosicion(0);
        ficha.modificarPasosRecorridos(-1);
        ficha.modificarEstado(EstadoFicha.CASA);
        ficha.modificarColor(color);
        return ficha;
    }

    public boolean sacarDeCasa(Ficha ficha) {
        if (ficha == null || ficha.obtenerEstado() != EstadoFicha.CASA || ficha.obtenerColor() == null) {
            return false;
        }
        int salida = TableroService.obtenerPosicionSalida(ficha.obtenerColor());
        ficha.modificarPosicion(salida);
        ficha.modificarPasosRecorridos(0);
        ficha.modificarEstado(EstadoFicha.RECORRIDO);
        return true;
    }

    public int calcularNuevaPosicion(Ficha ficha, int pasos) {
        if (!reglaService.validarAvance(ficha, pasos) || ficha.obtenerColor() == null) {
            return -1;
        }
        int pasosActuales = Math.max(0, ficha.obtenerPasosRecorridos());
        int nuevosPasos = pasosActuales + pasos;

        if (nuevosPasos == TableroService.PASOS_META) {
            return TableroService.POSICION_META;
        }
        if (nuevosPasos > TableroService.PASOS_ENTRADA_PASILLO) {
            int pasoEnPasillo = nuevosPasos - TableroService.PASOS_ENTRADA_PASILLO;
            return TableroService.POSICION_INICIO_PASILLO + pasoEnPasillo - 1;
        }
        int salida = TableroService.obtenerPosicionSalida(ficha.obtenerColor());
        return ((salida - 1 + nuevosPasos) % TableroService.TOTAL_CASILLAS_EXTERIOR) + 1;
    }

    public boolean avanzarFicha(Ficha ficha, int pasos) {
        validarPasos(pasos);
        int nuevaPosicion = calcularNuevaPosicion(ficha, pasos);
        if (nuevaPosicion < 0) {
            return false;
        }
        int nuevosPasos = Math.max(0, ficha.obtenerPasosRecorridos()) + pasos;
        ficha.modificarPasosRecorridos(nuevosPasos);
        ficha.modificarPosicion(nuevaPosicion);
        if (nuevosPasos == TableroService.PASOS_META) {
            ficha.modificarEstado(EstadoFicha.META);
        } else {
            ficha.modificarEstado(EstadoFicha.RECORRIDO);
        }
        return true;
    }

    public void moverFicha(Ficha ficha, int nuevaPosicionOPasos) {
        validarPasos(nuevaPosicionOPasos);
        if (ficha == null) {
            return;
        }
        if (ficha.obtenerEstado() == EstadoFicha.CASA) {
            ficha.modificarPosicion(nuevaPosicionOPasos);
            ficha.modificarPasosRecorridos(0);
            ficha.modificarEstado(EstadoFicha.RECORRIDO);
        } else {
            avanzarFicha(ficha, nuevaPosicionOPasos);
        }
    }

    public void regresarSalida(Ficha ficha) {
        if (ficha == null) {
            return;
        }
        ficha.modificarPosicion(0);
        ficha.modificarPasosRecorridos(-1);
        ficha.modificarEstado(EstadoFicha.CASA);
    }

    public void actualizarPosicion(Ficha ficha, int posicion) {
        if (ficha != null) {
            ficha.modificarPosicion(posicion);
        }
    }

    public void enviarAFinal(Ficha ficha) {
        if (ficha == null) {
            return;
        }
        ficha.modificarPasosRecorridos(TableroService.PASOS_META);
        ficha.modificarPosicion(TableroService.POSICION_META);
        ficha.modificarEstado(EstadoFicha.META);
    }

    public boolean validarLlegadaMeta(Ficha ficha) {
        return reglaService.validarLlegadaMeta(ficha);
    }

    public boolean validarCaptura(Ficha ficha) {
        return reglaService.validarCaptura(ficha);
    }

    public List<Ficha> detectarCapturas(Ficha fichaActiva, List<Ficha> fichasRivales, Casilla casillaDestino) {
        List<Ficha> capturadas = new ArrayList<>();
        if (fichaActiva == null || fichasRivales == null) {
            return capturadas;
        }
        for (Ficha rival : fichasRivales) {
            if (reglaService.validarCaptura(fichaActiva, rival, casillaDestino)) {
                capturadas.add(rival);
            }
        }
        return capturadas;
    }

    public int obtenerPosicion(Ficha ficha) {
        return ficha.obtenerPosicion();
    }

    public EstadoFicha obtenerEstado(Ficha ficha) {
        return ficha.obtenerEstado();
    }

    public void validarPasos(int pasos) {
        if (pasos <= 0) {
            throw new IllegalArgumentException("La cantidad de pasos debe ser mayor que cero");
        }
    }
}
