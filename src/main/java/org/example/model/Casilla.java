package org.example.model;

import org.example.enums.ColorJugador;
import org.example.enums.TipoCasilla;

public class Casilla {

    private int numero;
    private TipoCasilla tipo;
    private ColorJugador color;

    public int obtenerNumero() {
        return numero;
    }

    public void modificarNumero(int numero) {
        this.numero = numero;
    }

    public TipoCasilla obtenerTipo() {
        return tipo;
    }

    public void modificarTipo(TipoCasilla tipo) {
        this.tipo = tipo;
    }

    public ColorJugador obtenerColor() {
        return color;
    }

    public void modificarColor(ColorJugador color) {
        this.color = color;
    }

}
