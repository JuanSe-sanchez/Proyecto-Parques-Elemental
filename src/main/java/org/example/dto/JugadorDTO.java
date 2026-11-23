package org.example.dto;

public class JugadorDTO {

    private Long id;
    private String nombre;
    private String color;

    public JugadorDTO() {
    }

    public JugadorDTO(Long id, String nombre, String color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
    }

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

    public String obtenerColor() {
        return color;
    }

    public void modificarColor(String color) {
        this.color = color;
    }

}
