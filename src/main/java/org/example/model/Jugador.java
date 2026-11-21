package org.example.model;

import org.example.enums.ColorJugador;

import java.util.List;

public class Jugador {

    private Long id;
    private String nombre;
    private ColorJugador color;
    private List<Ficha> fichas;
    private int puntaje;

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void modificarNombre(String nombre) {
        this.nombre = nombre;
    }

    public ColorJugador obtenerColor() {
        return color;
    }

    public void modificarColor(ColorJugador color) {
        this.color = color;
    }

    public List<Ficha> obtenerFichas() {
        return fichas;
    }

    public void modificarFichas(List<Ficha> fichas) {
        this.fichas = fichas;
    }

    public int obtenerPuntaje() {
        return puntaje;
    }

    public void modificarPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

}
