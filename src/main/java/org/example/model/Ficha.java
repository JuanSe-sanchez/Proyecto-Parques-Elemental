package org.example.model;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;

public class Ficha {

    private Long id;
    private int posicion;
    private int pasosRecorridos = -1;
    private EstadoFicha estado;
    private ColorJugador color;

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public int obtenerPosicion() {
        return posicion;
    }

    public void modificarPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int obtenerPasosRecorridos() {
        return pasosRecorridos;
    }

    public void modificarPasosRecorridos(int pasosRecorridos) {
        this.pasosRecorridos = pasosRecorridos;
    }

    public EstadoFicha obtenerEstado() {
        return estado;
    }

    public void modificarEstado(EstadoFicha estado) {
        this.estado = estado;
    }

    public ColorJugador obtenerColor() {
        return color;
    }

    public void modificarColor(ColorJugador color) {
        this.color = color;
    }
}
