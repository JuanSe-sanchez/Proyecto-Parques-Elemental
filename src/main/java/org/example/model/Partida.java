package org.example.model;

import org.example.enums.EstadoPartida;

import java.time.LocalDateTime;
import java.util.List;

public class Partida {

    private Long id;
    private List<Jugador> jugadores;
    private Tablero tablero;
    private Dado dado;
    private Dado segundoDado;
    private EstadoPartida estado;
    private LocalDateTime fechaInicio;
    private int indiceTurnoActual;
    private int doblesConsecutivos;
    private int intentosSalidaCasa;
    private Jugador ganador;

    public Long obtenerId() {
        return id;
    }

    public void modificarId(Long id) {
        this.id = id;
    }

    public List<Jugador> obtenerJugadores() {
        return jugadores;
    }

    public void modificarJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Tablero obtenerTablero() {
        return tablero;
    }

    public void modificarTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public Dado obtenerDado() {
        return dado;
    }

    public void modificarDado(Dado dado) {
        this.dado = dado;
    }

    public Dado obtenerSegundoDado() {
        return segundoDado;
    }

    public void modificarSegundoDado(Dado segundoDado) {
        this.segundoDado = segundoDado;
    }

    public EstadoPartida obtenerEstado() {
        return estado;
    }

    public void modificarEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public LocalDateTime obtenerFechaInicio() {
        return fechaInicio;
    }

    public void modificarFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int obtenerIndiceTurnoActual() {
        return indiceTurnoActual;
    }

    public void modificarIndiceTurnoActual(int indiceTurnoActual) {
        this.indiceTurnoActual = indiceTurnoActual;
    }

    public int obtenerDoblesConsecutivos() {
        return doblesConsecutivos;
    }

    public void modificarDoblesConsecutivos(int doblesConsecutivos) {
        this.doblesConsecutivos = doblesConsecutivos;
    }

    public int obtenerIntentosSalidaCasa() {
        return intentosSalidaCasa;
    }

    public void modificarIntentosSalidaCasa(int intentosSalidaCasa) {
        this.intentosSalidaCasa = intentosSalidaCasa;
    }

    public Jugador obtenerGanador() {
        return ganador;
    }

    public void modificarGanador(Jugador ganador) {
        this.ganador = ganador;
    }
}
