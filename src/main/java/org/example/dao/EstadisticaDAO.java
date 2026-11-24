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

    private final ConexionesBD conexionBD;

    public EstadisticaDAO() {
        this(new ConexionesBD());
    }

    public EstadisticaDAO(ConexionesBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public void guardar(long jugadorId, EstadisticaDTO estadistica) {
        if (estadistica == null) {
            return;
        }
        String sql = "INSERT INTO estadistica (jugador_id, partidas_jugadas, partidas_ganadas) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (jugador_id) DO UPDATE " +
                "SET partidas_jugadas = EXCLUDED.partidas_jugadas, " +
                "    partidas_ganadas = EXCLUDED.partidas_ganadas";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setLong(1, jugadorId);
                consulta.setInt(2, estadistica.obtenerPartidasJugadas());
                consulta.setInt(3, estadistica.obtenerPartidasGanadas());
                consulta.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar estadistica: " + e.getMessage());
        }
    }

    public EstadisticaDTO consultar(long jugadorId) {
        String sql = "SELECT e.jugador_id, j.nombre, e.partidas_jugadas, e.partidas_ganadas " +
                "FROM estadistica e " +
                "LEFT JOIN jugador j ON j.id = e.jugador_id " +
                "WHERE e.jugador_id = ?";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setLong(1, jugadorId);
                try (ResultSet resultado = consulta.executeQuery()) {
                    if (resultado.next()) {
                        return new EstadisticaDTO(
                                resultado.getLong("jugador_id"),
                                resultado.getString("nombre"),
                                resultado.getInt("partidas_jugadas"),
                                resultado.getInt("partidas_ganadas")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar estadistica: " + e.getMessage());
        }
        return null;
    }

    public List<EstadisticaDTO> listar() {
        List<EstadisticaDTO> estadisticas = new ArrayList<>();
        String sql = "SELECT e.jugador_id, j.nombre, e.partidas_jugadas, e.partidas_ganadas " +
                "FROM estadistica e " +
                "LEFT JOIN jugador j ON j.id = e.jugador_id " +
                "ORDER BY e.partidas_ganadas DESC, e.partidas_jugadas DESC";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return estadisticas;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql);
                 ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    estadisticas.add(new EstadisticaDTO(
                            resultado.getLong("jugador_id"),
                            resultado.getString("nombre"),
                            resultado.getInt("partidas_jugadas"),
                            resultado.getInt("partidas_ganadas")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar estadisticas: " + e.getMessage());
        }
        return estadisticas;
    }
}
