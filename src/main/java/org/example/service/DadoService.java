package org.example.service;

import org.example.model.Dado;

import java.util.Random;

public class DadoService {

    private Random aleatorio = new Random();

    public Dado crearDado() {
        return new Dado();
    }

    public int lanzarDado(Dado dado) {
        int valor = aleatorio.nextInt(6) + 1;
        dado.modificarValor(valor);
        return valor;
    }

    public int obtenerValor(Dado dado) {
        return dado.obtenerValor();
    }
    
    public boolean esValorValido(int valor) {
        return valor >= 1 && valor <= 6;
    }
}
