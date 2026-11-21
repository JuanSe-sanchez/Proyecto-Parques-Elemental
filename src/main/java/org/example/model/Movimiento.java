package org.example.model;

import java.time.LocalDateTime;

public class Movimiento {

    private Ficha ficha;
    private int posicionInicial;
    private int posicionFinal;
    private LocalDateTime fecha;

    public Ficha obtenerFicha() {
        return ficha;
    }

    public void modificarFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public int obtenerPosicionInicial() {
        return posicionInicial;
    }

    public void modificarPosicionInicial(int posicionInicial) {
        this.posicionInicial = posicionInicial;
    }

    public int obtenerPosicionFinal() {
        return posicionFinal;
    }

    public void modificarPosicionFinal(int posicionFinal) {
        this.posicionFinal = posicionFinal;
    }

    public LocalDateTime obtenerFecha() {
        return fecha;
    }

    public void modificarFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }


}
