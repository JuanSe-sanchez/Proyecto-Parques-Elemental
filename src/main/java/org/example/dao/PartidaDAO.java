package org.example.dao;

import org.example.database.ConexionesBD;
import org.example.dto.PartidaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {

    private final ConexionesBD conexionBD;

    public PartidaDAO() {
        this(new ConexionesBD());
    }

    public PartidaDAO(ConexionesBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public Long guardar(PartidaDTO partida) {
        if (partida == null) {
            return null;
        }
        String sql = "INSERT INTO partida (fecha_inicio, estado, ganador) VALUES (?, ?, ?)";
        LocalDateTime fecha = partida.obtenerFecha() != null ? partida.obtenerFecha() : LocalDateTime.now();
        String estado = partida.obtenerGanador() != null ? "FINALIZADA" : "EN_CURSO";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                consulta.setTimestamp(1, Timestamp.valueOf(fecha));
                consulta.setObject(2, estado, Types.OTHER);
                consulta.setString(3, partida.obtenerGanador());
                consulta.executeUpdate();

                try (ResultSet keys = consulta.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long idGenerado = keys.getLong(1);
                        partida.modificarId(idGenerado);
                        return idGenerado;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar partida: " + e.getMessage());
        }
        return null;
    }

    public void actualizarFinalizacion(long partidaId, String ganador) {
        actualizarFinalizacion(partidaId, null, ganador);
    }

    public void actualizarFinalizacion(long partidaId, Long ganadorId, String ganador) {
        String sql = "UPDATE partida SET estado = ?, ganador = ?, ganador_id = COALESCE(?, ganador_id) WHERE id = ?";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setObject(1, "FINALIZADA", Types.OTHER);
                consulta.setString(2, ganador);
                if (ganadorId != null) {
                    consulta.setLong(3, ganadorId);
                } else {
                    consulta.setNull(3, Types.BIGINT);
                }
                consulta.setLong(4, partidaId);
                consulta.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al finalizar partida en BD: " + e.getMessage());
        }
    }

    public PartidaDTO consultar(long id) {
        String sql = "SELECT id, fecha_inicio, ganador FROM partida WHERE id = ?";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setLong(1, id);
                try (ResultSet resultado = consulta.executeQuery()) {
                    if (resultado.next()) {
                        Timestamp fecha = resultado.getTimestamp("fecha_inicio");
                        return new PartidaDTO(
                                resultado.getLong("id"),
                                fecha != null ? fecha.toLocalDateTime() : null,
                                resultado.getString("ganador")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar partida: " + e.getMessage());
        }
        return null;
    }

    public List<PartidaDTO> listar() {
        List<PartidaDTO> partidas = new ArrayList<>();
        String sql = "SELECT id, fecha_inicio, ganador FROM partida ORDER BY id DESC";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return partidas;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql);
                 ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    Timestamp fecha = resultado.getTimestamp("fecha_inicio");
                    partidas.add(new PartidaDTO(
                            resultado.getLong("id"),
                            fecha != null ? fecha.toLocalDateTime() : null,
                            resultado.getString("ganador")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar partidas: " + e.getMessage());
        }
        return partidas;
    }

    public int contarPartidas() {
        return listar().size();
    }
}
