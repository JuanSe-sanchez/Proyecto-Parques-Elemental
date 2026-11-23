package org.example.dao;

import org.example.database.ConexionesBD;
import org.example.dto.JugadorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    private final ConexionesBD conexionBD = new ConexionesBD();

    public void guardar(JugadorDTO jugador) {

        String sql = "INSERT INTO jugador (nombre, color, puntaje) VALUES (?, ?, ?)";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setString(1, jugador.obtenerNombre());
            consulta.setString(2, jugador.obtenerColor());

            // El puntaje se debe obtener del Model o del Service.
            consulta.setInt(3, 0);

            consulta.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar jugador: " + e.getMessage());
        }
    }

    public JugadorDTO consultar(long id) {

        String sql = "SELECT id, nombre, color FROM jugador WHERE id = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setLong(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {
                    return new JugadorDTO(
                            resultado.getLong("id"),
                            resultado.getString("nombre"),
                            resultado.getString("color")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar jugador: " + e.getMessage());
        }

        return null;
    }

    public List<JugadorDTO> listar() {

        List<JugadorDTO> jugadores = new ArrayList<>();

        String sql = "SELECT id, nombre, color FROM jugador";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                JugadorDTO jugador = new JugadorDTO(
                        resultado.getLong("id"),
                        resultado.getString("nombre"),
                        resultado.getString("color")
                );

                jugadores.add(jugador);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar jugadores: " + e.getMessage());
        }

        return jugadores;
    }
}
