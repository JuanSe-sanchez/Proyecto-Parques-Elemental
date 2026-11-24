package org.example.service;

import org.example.model.Ficha;
import org.example.model.Movimiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovimientoService {

    private final ReglaService reglaService;
    private final List<Movimiento> historial = new ArrayList<>();

    public MovimientoService() {
        this(new ReglaService());
    }

    public MovimientoService(ReglaService reglaService) {
        this.reglaService = reglaService;
    }

    public Movimiento registrarMovimiento(Ficha ficha, int posicionInicial, int posicionFinal) {
        Movimiento movimiento = new Movimiento();
        movimiento.modificarFicha(ficha);
        movimiento.modificarPosicionInicial(posicionInicial);
        movimiento.modificarPosicionFinal(posicionFinal);
        movimiento.modificarFecha(LocalDateTime.now());
        historial.add(movimiento);
        return movimiento;
    }

    public boolean validarMovimiento(Movimiento movimiento) {
        if (movimiento == null || movimiento.obtenerFicha() == null) {
            return false;
        }
        if (reglaService.validarLlegadaMeta(movimiento.obtenerFicha())
                && movimiento.obtenerPosicionInicial() == TableroService.POSICION_META) {
            return false;
        }
        return movimiento.obtenerPosicionFinal() >= 0
                && movimiento.obtenerPosicionFinal() != movimiento.obtenerPosicionInicial();
    }

    public void ejecutarMovimiento(Movimiento movimiento) {
        if (!validarMovimiento(movimiento)) {
            throw new IllegalArgumentException("Movimiento invalido");
        }
        movimiento.obtenerFicha().modificarPosicion(movimiento.obtenerPosicionFinal());
        if (movimiento.obtenerFecha() == null) {
            movimiento.modificarFecha(LocalDateTime.now());
        }
        if (!historial.contains(movimiento)) {
            historial.add(movimiento);
        }
    }

    public int obtenerPosicionInicial(Movimiento movimiento) {
        return movimiento.obtenerPosicionInicial();
    }

    public int obtenerPosicionFinal(Movimiento movimiento) {
        return movimiento.obtenerPosicionFinal();
    }

    public LocalDateTime obtenerFecha(Movimiento movimiento) {
        return movimiento.obtenerFecha();
    }

    public int contarMovimientos() {
        return historial.size();
    }

    public List<Movimiento> obtenerHistorial() {
        return Collections.unmodifiableList(historial);
    }

    public void limpiarHistorial() {
        historial.clear();
    }
}
