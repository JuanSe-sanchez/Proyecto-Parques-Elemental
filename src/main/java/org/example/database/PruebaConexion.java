package org.example.database;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {
        ConexionesBD conexionBD = new ConexionesBD();
        Connection conexion = conexionBD.conectar();

        if (conexion != null) {
            System.out.println("Conexion exitosa con Neon!");
            conexionBD.cerrar();
            boolean esquemaOk = conexionBD.inicializarEsquema();
            System.out.println("Esquema inicializado/actualizado: " + esquemaOk);
        } else {
            System.out.println("Error al conectar con Neon.");
        }
    }
}
