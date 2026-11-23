package org.example.dao;

import org.example.database.ConexionesBD;
import org.example.dto.PartidaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private final ConexionesBD conexionBD = new ConexionesBD();

    public void guardar(PartidaDTO partida) {

        String sql = "INSERT INTO partida (fecha_inicio, estado) VALUES (?, ?)";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setTimestamp(1, Timestamp.valueOf(partida.obtenerFecha()));
            consulta.setString(2, "EN_CURSO");

            consulta.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar partida: " + e.getMessage());
        }
    }

    public PartidaDTO consultar(long id) {

        String sql = "SELECT id, fecha_inicio FROM partida WHERE id = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {

            consulta.setLong(1, id);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {

                    Timestamp fecha = resultado.getTimestamp("fecha_inicio");

                    return new PartidaDTO(
                            resultado.getLong("id"),
                            fecha.toLocalDateTime(),
                            null
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar partida: " + e.getMessage());
        }

        return null;
    }

    public List<PartidaDTO> listar() {

        List<PartidaDTO> partidas = new ArrayList<>();

        String sql = "SELECT id, fecha_inicio FROM partida";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                Timestamp fecha = resultado.getTimestamp("fecha_inicio");

                PartidaDTO partida = new PartidaDTO(
                        resultado.getLong("id"),
                        fecha.toLocalDateTime(),
                        null
                );

                partidas.add(partida);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar partidas: " + e.getMessage());
        }

        return partidas;
    }
}
