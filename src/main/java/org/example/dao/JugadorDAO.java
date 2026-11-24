package org.example.dao;

import org.example.database.ConexionesBD;
import org.example.dto.JugadorDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    private final ConexionesBD conexionBD;

    public JugadorDAO() {
        this(new ConexionesBD());
    }

    public JugadorDAO(ConexionesBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public Long guardar(JugadorDTO jugador) {
        return guardar(jugador, 0);
    }

    public Long guardar(JugadorDTO jugador, int puntajeInicial) {
        if (jugador == null) {
            return null;
        }
        String sql = "INSERT INTO jugador (nombre, color, puntaje) VALUES (?, ?, ?)";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                consulta.setString(1, jugador.obtenerNombre());
                consulta.setObject(2, jugador.obtenerColor(), Types.OTHER);
                consulta.setInt(3, puntajeInicial);
                consulta.executeUpdate();

                try (ResultSet keys = consulta.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long idGenerado = keys.getLong(1);
                        jugador.modificarId(idGenerado);
                        return idGenerado;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al guardar jugador: " + e.getMessage());
        }
        return null;
    }

    public void actualizarPuntaje(long id, int puntaje) {
        String sql = "UPDATE jugador SET puntaje = ? WHERE id = ?";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setInt(1, puntaje);
                consulta.setLong(2, id);
                consulta.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar puntaje: " + e.getMessage());
        }
    }

    public JugadorDTO consultar(long id) {
        String sql = "SELECT id, nombre, color FROM jugador WHERE id = ?";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
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
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar jugador: " + e.getMessage());
        }
        return null;
    }

    public JugadorDTO consultarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        String sql = "SELECT id, nombre, color FROM jugador WHERE LOWER(nombre) = LOWER(?) LIMIT 1";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql)) {
                consulta.setString(1, nombre.trim());
                try (ResultSet resultado = consulta.executeQuery()) {
                    if (resultado.next()) {
                        return new JugadorDTO(
                                resultado.getLong("id"),
                                resultado.getString("nombre"),
                                resultado.getString("color")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar jugador por nombre: " + e.getMessage());
        }
        return null;
    }

    public List<JugadorDTO> listar() {
        List<JugadorDTO> jugadores = new ArrayList<>();
        String sql = "SELECT id, nombre, color FROM jugador ORDER BY id";

        try (Connection conexion = conexionBD.conectar()) {
            if (conexion == null) {
                return jugadores;
            }
            try (PreparedStatement consulta = conexion.prepareStatement(sql);
                 ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    jugadores.add(new JugadorDTO(
                            resultado.getLong("id"),
                            resultado.getString("nombre"),
                            resultado.getString("color")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar jugadores: " + e.getMessage());
        }
        return jugadores;
    }
}
