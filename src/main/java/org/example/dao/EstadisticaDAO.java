package org.example.dao;

import org.example.database.ConexionesBD;
import org.example.dto.EstadisticaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadisticaDAO {

    private final ConexionesBD conexionBD = new ConexionesBD();

    public void guardar(long jugadorId, EstadisticaDTO estadistica) {

        String sql = "INSERT INTO estadistica " +
                "(jugador_id, partidas_jugadas, partidas_ganadas) " +
                "VALUES (?, ?, ?)";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setLong(1, jugadorId);
            consulta.setInt(2, estadistica.obtenerPartidasJugadas());
            consulta.setInt(3, estadistica.obtenerPartidasGanadas());

            consulta.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar estadistica: " + e.getMessage());
        }
    }

    public EstadisticaDTO consultar(long jugadorId) {

        String sql = "SELECT partidas_jugadas, partidas_ganadas " +
                "FROM estadistica WHERE jugador_id = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setLong(1, jugadorId);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {
                    return new EstadisticaDTO(
                            resultado.getInt("partidas_jugadas"),
                            resultado.getInt("partidas_ganadas")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar estadistica: " + e.getMessage());
        }

        return null;
    }

    public List<EstadisticaDTO> listar() {

        List<EstadisticaDTO> estadisticas = new ArrayList<>();

        String sql = "SELECT partidas_jugadas, partidas_ganadas FROM estadistica";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                estadisticas.add(new EstadisticaDTO(
                        resultado.getInt("partidas_jugadas"),
                        resultado.getInt("partidas_ganadas")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar estadisticas: " + e.getMessage());
        }

        return estadisticas;
    }
}