package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionesBD {

    private final String URL = "jdbc:postgresql://ep-bold-shape-bd8ktomc-pooler.c-8.us-east-2.aws.neon.tech:5432/neondb?sslmode=require";    private final String USER = "neondb_owner";
    private final String PASSWORD = "npg_4EwkQSogfvz8";
    private Connection conexion;

    public Connection conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexion exitosa con Neon");

            return conexion;

        } catch (SQLException e) {
            System.out.println("ERROR AL CONECTAR CON NEON");
            System.out.println("Mensaje: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Codigo: " + e.getErrorCode());

            e.printStackTrace();

            return null;
        }
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion cerrada correctamente");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}