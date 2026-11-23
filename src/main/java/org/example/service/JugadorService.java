package org.example.service;

import org.example.enums.ColorJugador;
import org.example.model.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorService {


    public Jugador crearJugador(String nombre, ColorJugador color) {
        Jugador jugador = new Jugador();

        jugador.modificarNombre(nombre);
        jugador.modificarColor(color);
        jugador.modificarFichas(new ArrayList<>());
        jugador.modificarPuntaje(0);

        return jugador;
    }

    public void agregarFicha(Jugador jugador, Ficha ficha) {
        jugador.obtenerFichas().add(ficha);
    }

    public void modificarPuntaje(Jugador jugador, int puntaje) {
        jugador.modificarPuntaje(puntaje);
    }

    public int obtenerPuntaje(Jugador jugador) {
        return jugador.obtenerPuntaje();
    }

    public String obtenerNombre(Jugador jugador) {
        return jugador.obtenerNombre();
    }

    public List<Ficha> obtenerFichas(Jugador jugador) {
        return jugador.obtenerFichas();
    }
    
    public int obtenerCantidadJugadores() {
        return 4;
    }
}
