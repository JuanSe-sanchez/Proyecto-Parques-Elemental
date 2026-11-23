package org.example.enums;

public enum ColorJugador {

    ROJO,AZUL,VERDE,AMARILLO
    public static ColorJugador desdeTexto (String texto){
    if (texto== null || texto.isBlank()) {
            return null;
        }
        try {
            return ColorJugador.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
