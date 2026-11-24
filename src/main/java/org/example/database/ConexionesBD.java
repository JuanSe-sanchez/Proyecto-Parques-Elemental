package org.example.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConexionesBD {

    private static final String ENV_URL = "PARQUES_DB_URL";
    private static final String ENV_USER = "PARQUES_DB_USER";
    private static final String ENV_PASSWORD = "PARQUES_DB_PASSWORD";
    private static final String ARCHIVO_PROPIEDADES = "db.properties";

    private final String url;
    private final String user;
    private final String password;
    private Connection conexion;

    public ConexionesBD() {
        Properties props = cargarPropiedadesLocales();
        this.url = obtenerConfiguracion(ENV_URL, props.getProperty("db.url"));
        this.user = obtenerConfiguracion(ENV_USER, props.getProperty("db.user"));
        this.password = obtenerConfiguracion(ENV_PASSWORD, props.getProperty("db.password"));
    }

    public ConexionesBD(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public boolean estaConfigurada() {
        return url != null && !url.isBlank()
                && user != null && !user.isBlank()
                && password != null && !password.isBlank();
    }

    public Connection conectar() {
        if (!estaConfigurada()) {
            System.out.println("Base de datos no configurada. Define PARQUES_DB_URL/USER/PASSWORD o db.properties.");
            return null;
        }
        try {
            conexion = DriverManager.getConnection(url, user, password);
            return conexion;
        } catch (SQLException e) {
            System.out.println("ERROR AL CONECTAR CON BASE DE DATOS: " + e.getMessage());
            return null;
        }
    }

    public boolean inicializarEsquema() {
        try (Connection conn = conectar()) {
            if (conn == null) {
                return false;
            }
            String sqlScript = leerSchemaSql();
            if (sqlScript == null || sqlScript.isBlank()) {
                return false;
            }
            String[] sentencias = sqlScript.split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String sentencia : sentencias) {
                    String limpia = eliminarComentariosSql(sentencia);
                    if (!limpia.isEmpty()) {
                        stmt.execute(limpia);
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error al inicializar el esquema de BD: " + e.getMessage());
            return false;
        }
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }

    private Properties cargarPropiedadesLocales() {
        Properties props = new Properties();
        Path ruta = Path.of(ARCHIVO_PROPIEDADES);
        if (Files.exists(ruta)) {
            try (InputStream in = new FileInputStream(ruta.toFile())) {
                props.load(in);
            } catch (IOException e) {
                System.out.println("No se pudo leer " + ARCHIVO_PROPIEDADES + ": " + e.getMessage());
            }
        }
        return props;
    }

    private String obtenerConfiguracion(String variableEntorno, String valorPropiedad) {
        String env = System.getenv(variableEntorno);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return valorPropiedad != null ? valorPropiedad.trim() : null;
    }

    private String eliminarComentariosSql(String sentencia) {
        StringBuilder sb = new StringBuilder();
        for (String linea : sentencia.split("\\R")) {
            String trimmed = linea.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                sb.append(trimmed).append(' ');
            }
        }
        return sb.toString().trim();
    }

    private String leerSchemaSql() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                return null;
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No se pudo leer schema.sql: " + e.getMessage());
            return null;
        }
    }
}
