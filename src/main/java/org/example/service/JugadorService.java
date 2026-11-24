package org.example.service;

import org.example.dao.JugadorDAO;
import org.example.dto.JugadorDTO;
import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.model.Ficha;
import org.example.model.Jugador;

import java.util.ArrayList;
import java.util.List;

public class JugadorService {

    public static final int FICHAS_POR_JUGADOR = 4;
    public static final int MAX_JUGADORES = 4;

    private final JugadorDAO jugadorDAO;
    private final FichaService fichaService;

    public JugadorService() {
        this(new JugadorDAO(), new FichaService());
    }

    public JugadorService(JugadorDAO jugadorDAO, FichaService fichaService) {
        this.jugadorDAO = jugadorDAO;
        this.fichaService = fichaService;
    }

    public Jugador crearJugador(String nombre, ColorJugador color) {
        Jugador jugador = new Jugador();
        jugador.modificarNombre(nombre);
        jugador.modificarColor(color);
        jugador.modificarPuntaje(0);
        jugador.modificarFichas(crearFichasIniciales(color));
        return jugador;
    }

    public void registrarJugador(Jugador jugador) {
        if (jugador == null || jugador.obtenerNombre() == null || jugador.obtenerNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio");
        }
        if (jugador.obtenerColor() == null) {
            throw new IllegalArgumentException("El color del jugador es obligatorio");
        }
        if (jugador.obtenerFichas() == null || jugador.obtenerFichas().isEmpty()) {
            jugador.modificarFichas(crearFichasIniciales(jugador.obtenerColor()));
        }

        JugadorDTO existente = jugadorDAO.consultarPorNombre(jugador.obtenerNombre());
        if (existente != null) {
            jugador.modificarId(existente.obtenerId());
        } else {
            JugadorDTO dto = new JugadorDTO(
                    null,
                    jugador.obtenerNombre().trim(),
                    jugador.obtenerColor().name()
            );
            Long idGenerado = jugadorDAO.guardar(dto, jugador.obtenerPuntaje());
            jugador.modificarId(idGenerado);
        }
    }

    public void seleccionarColor(Jugador jugador, ColorJugador color) {
        if (jugador == null || color == null) {
            return;
        }
        jugador.modificarColor(color);
        if (jugador.obtenerFichas() != null) {
            for (Ficha ficha : jugador.obtenerFichas()) {
                ficha.modificarColor(color);
            }
        }
    }

    public Jugador obtenerJugador(Long id) {
        if (id == null) {
            return null;
        }
        JugadorDTO dto = jugadorDAO.consultar(id);
        if (dto == null) {
            return null;
        }
        ColorJugador color = ColorJugador.desdeTexto(dto.obtenerColor());
        Jugador jugador = crearJugador(dto.obtenerNombre(), color != null ? color : ColorJugador.ROJO);
        jugador.modificarId(dto.obtenerId());
        return jugador;
    }

    public void actualizarPuntaje(Jugador jugador, int puntos) {
        if (jugador == null) {
            return;
        }
        int nuevoPuntaje = jugador.obtenerPuntaje() + puntos;
        jugador.modificarPuntaje(nuevoPuntaje);
        if (jugador.obtenerId() != null) {
            jugadorDAO.actualizarPuntaje(jugador.obtenerId(), nuevoPuntaje);
        }
    }

    public void agregarFicha(Jugador jugador, Ficha ficha) {
        if (jugador.obtenerFichas() == null) {
            jugador.modificarFichas(new ArrayList<>());
        }
        jugador.obtenerFichas().add(ficha);
    }

    public void modificarPuntaje(Jugador jugador, int puntaje) {
        jugador.modificarPuntaje(puntaje);
    }

    public int obtenerPuntaje(Jugador jugador) {
        return jugador.obtenerPuntaje();
    }

    public String obtenerNombre(Jugador jugador) {
        return jugador.obtenerNombre();
    }

    public List<Ficha> obtenerFichas(Jugador jugador) {
        return jugador.obtenerFichas();
    }

    public boolean tieneTodasEnCasa(Jugador jugador) {
        if (jugador == null || jugador.obtenerFichas() == null || jugador.obtenerFichas().isEmpty()) {
            return false;
        }
        for (Ficha ficha : jugador.obtenerFichas()) {
            if (ficha.obtenerEstado() != EstadoFicha.CASA) {
                return false;
            }
        }
        return true;
    }

    public boolean sinFichasActivasEnRecorridoYTieneEnCasa(Jugador jugador) {
        if (jugador == null || jugador.obtenerFichas() == null || jugador.obtenerFichas().isEmpty()) {
            return false;
        }
        boolean tieneEnCasa = false;
        for (Ficha ficha : jugador.obtenerFichas()) {
            if (ficha.obtenerEstado() == EstadoFicha.RECORRIDO) {
                return false;
            }
            if (ficha.obtenerEstado() == EstadoFicha.CASA) {
                tieneEnCasa = true;
            }
        }
        return tieneEnCasa;
    }

    public boolean tieneTodasEnMeta(Jugador jugador) {
        if (jugador == null || jugador.obtenerFichas() == null || jugador.obtenerFichas().isEmpty()) {
            return false;
        }
        for (Ficha ficha : jugador.obtenerFichas()) {
            if (ficha.obtenerEstado() != EstadoFicha.META) {
                return false;
            }
        }
        return true;
    }

    public int obtenerCantidadJugadores() {
        return MAX_JUGADORES;
    }

    private List<Ficha> crearFichasIniciales(ColorJugador color) {
        List<Ficha> fichas = new ArrayList<>(FICHAS_POR_JUGADOR);
        for (long i = 1; i <= FICHAS_POR_JUGADOR; i++) {
            Ficha ficha = fichaService.crearFicha(color);
            ficha.modificarId(i);
            fichas.add(ficha);
        }
        return fichas;
    }
}
