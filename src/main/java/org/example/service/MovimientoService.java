package org.example.service;

import org.example.model.Ficha;
import org.example.model.Movimiento;

import java.time.LocalDateTime;

public class MovimientoService {

    public Movimiento registrarMovimiento(Ficha ficha, int posicionInicial, int posicionFinal) {
        Movimiento movimiento = new Movimiento();

        movimiento.modificarFicha(ficha);
        movimiento.modificarPosicionInicial(posicionInicial);
        movimiento.modificarPosicionFinal(posicionFinal);
        movimiento.modificarFecha(LocalDateTime.now());

        return movimiento;
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

}
