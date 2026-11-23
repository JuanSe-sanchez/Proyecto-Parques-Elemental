package org.example.service;

import org.example.model.Casilla;
import org.example.model.Tablero;

import java.util.ArrayList;
import java.util.List;

public class TableroService {

    public Tablero crearTablero() {
        Tablero tablero = new Tablero();
        tablero.modificarCasillas(new ArrayList<>());
        return tablero;
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
}
