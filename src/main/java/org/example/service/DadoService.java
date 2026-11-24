package org.example.service;

import org.example.model.Dado;

import java.util.Random;

public class DadoService {

    private final Random aleatorio;

    public DadoService() {
        this(new Random());
    }

    public DadoService(Random aleatorio) {
        this.aleatorio = aleatorio;
    }

    public Dado crearDado() {
        return new Dado();
    }

    public int lanzarDado() {
        return aleatorio.nextInt(6) + 1;
    }

    public int lanzarDado(Dado dado) {
        int valor = lanzarDado();
        if (dado != null) {
            dado.modificarValor(valor);
        }
        return valor;
    }

    public int[] lanzarDosDados(Dado dado1, Dado dado2) {
        int valor1 = lanzarDado(dado1);
        int valor2 = lanzarDado(dado2);
        return new int[]{valor1, valor2};
    }

    public int obtenerValor(Dado dado) {
        return dado != null ? dado.obtenerValor() : 0;
    }

    public boolean validarDobles(int valor1, int valor2) {
        return esValorValido(valor1) && esValorValido(valor2) && valor1 == valor2;
    }

    public boolean esValorValido(int valor) {
        return valor >= 1 && valor <= 6;
    }
}
