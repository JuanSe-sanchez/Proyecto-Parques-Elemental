package org.example.service;

import org.example.dao.PartidaDAO;
import org.example.dto.PartidaDTO;
import org.example.enums.EstadoFicha;
import org.example.enums.EstadoPartida;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.model.Partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PartidaService {

    public static final int PUNTOS_CAPTURA = 20;
    public static final int PUNTOS_META = 50;

    private final PartidaDAO partidaDAO;
    private final JugadorService jugadorService;
    private final TableroService tableroService;
    private final DadoService dadoService;
    private final FichaService fichaService;
    private final ReglaService reglaService;
    private final MovimientoService movimientoService;
    private final EstadisticaService estadisticaService;

    public record ResultadoJugada(
            boolean exitosa,
            boolean salioDeCasa,
            boolean llegoAMeta,
            List<Ficha> fichasCapturadas,
            Jugador ganador,
            String mensaje
    ) {
    }

    public PartidaService() {
        this(
                new PartidaDAO(),
                new JugadorService(),
                new TableroService(),
                new DadoService(),
                new FichaService(),
                new ReglaService(),
                new MovimientoService(),
                new EstadisticaService()
        );
    }

    public PartidaService(
            PartidaDAO partidaDAO,
            JugadorService jugadorService,
            TableroService tableroService,
            DadoService dadoService,
            FichaService fichaService,
            ReglaService reglaService,
            MovimientoService movimientoService,
            EstadisticaService estadisticaService
    ) {
        this.partidaDAO = partidaDAO;
        this.jugadorService = jugadorService;
        this.tableroService = tableroService;
        this.dadoService = dadoService;
        this.fichaService = fichaService;
        this.reglaService = reglaService;
        this.movimientoService = movimientoService;
        this.estadisticaService = estadisticaService;
    }

    public Partida crearPartida() {
        Partida partida = new Partida();
        partida.modificarJugadores(new ArrayList<>());
        partida.modificarTablero(tableroService.crearTablero());
        partida.modificarDado(dadoService.crearDado());
        partida.modificarSegundoDado(dadoService.crearDado());
        partida.modificarEstado(EstadoPartida.EN_CURSO);
        partida.modificarFechaInicio(LocalDateTime.now());
        partida.modificarIndiceTurnoActual(0);
        partida.modificarDoblesConsecutivos(0);
        partida.modificarIntentosSalidaCasa(0);
        return partida;
    }

    public void iniciarPartida(Partida partida) {
        if (partida == null) {
            return;
        }
        partida.modificarEstado(EstadoPartida.EN_CURSO);
        partida.modificarFechaInicio(LocalDateTime.now());
        partida.modificarIndiceTurnoActual(0);
        partida.modificarDoblesConsecutivos(0);
        partida.modificarIntentosSalidaCasa(0);
        partida.modificarGanador(null);

        if (partida.obtenerTablero() == null || !tableroService.estaTableroListo(partida.obtenerTablero())) {
            partida.modificarTablero(tableroService.crearTablero());
        }

        if (partida.obtenerJugadores() != null && !partida.obtenerJugadores().isEmpty()) {
            PartidaDTO dto = new PartidaDTO(null, partida.obtenerFechaInicio(), null);
            Long id = partidaDAO.guardar(dto);
            partida.modificarId(id);
        }
    }

    public void finalizarPartida(Partida partida) {
        if (partida == null) {
            return;
        }
        partida.modificarEstado(EstadoPartida.FINALIZADA);
        Jugador ganador = obtenerGanador(partida);
        if (ganador != null) {
            partida.modificarGanador(ganador);
        }
        if (partida.obtenerJugadores() != null && !partida.obtenerJugadores().isEmpty()) {
            estadisticaService.registrarPartida(partida);
        }
    }

    public void agregarJugador(Partida partida, Jugador jugador) {
        if (partida.obtenerJugadores() == null) {
            partida.modificarJugadores(new ArrayList<>());
        }
        partida.obtenerJugadores().add(jugador);
    }

    public List<Jugador> obtenerJugadores(Partida partida) {
        return partida.obtenerJugadores();
    }

    public EstadoPartida obtenerEstado(Partida partida) {
        return partida.obtenerEstado();
    }

    public Jugador obtenerJugadorActual(Partida partida) {
        if (partida == null || partida.obtenerJugadores() == null || partida.obtenerJugadores().isEmpty()) {
            return null;
        }
        int indice = partida.obtenerIndiceTurnoActual() % partida.obtenerJugadores().size();
        return partida.obtenerJugadores().get(indice);
    }

    public void cambiarTurno(Partida partida) {
        if (partida == null || partida.obtenerJugadores() == null || partida.obtenerJugadores().isEmpty()) {
            return;
        }
        int siguiente = (partida.obtenerIndiceTurnoActual() + 1) % partida.obtenerJugadores().size();
        partida.modificarIndiceTurnoActual(siguiente);
        partida.modificarDoblesConsecutivos(0);
        partida.modificarIntentosSalidaCasa(0);
    }

    public Jugador obtenerGanador(Partida partida) {
        if (partida == null || partida.obtenerJugadores() == null) {
            return null;
        }
        for (Jugador jugador : partida.obtenerJugadores()) {
            if (jugadorService.tieneTodasEnMeta(jugador)) {
                partida.modificarGanador(jugador);
                return jugador;
            }
        }
        return partida.obtenerGanador();
    }

    public List<Ficha> obtenerFichasMovibles(Partida partida, Jugador jugador, int pasos, boolean puedeSalirDeCasa) {
        List<Ficha> movibles = new ArrayList<>();
        if (partida == null || jugador == null || jugador.obtenerFichas() == null) {
            return movibles;
        }
        for (Ficha ficha : jugador.obtenerFichas()) {
            if (ficha.obtenerEstado() == EstadoFicha.CASA && puedeSalirDeCasa) {
                movibles.add(ficha);
            } else if (ficha.obtenerEstado() == EstadoFicha.RECORRIDO && reglaService.validarAvance(ficha, pasos)) {
                movibles.add(ficha);
            }
        }
        return movibles;
    }

    public ResultadoJugada ejecutarJugada(Partida partida, Jugador jugador, Ficha ficha, int pasos, boolean puedeSalirDeCasa) {
        if (partida == null || jugador == null || ficha == null || partida.obtenerEstado() != EstadoPartida.EN_CURSO) {
            return new ResultadoJugada(false, false, false, List.of(), null, "La jugada no es valida.");
        }

        int posicionInicial = ficha.obtenerPosicion();
        boolean salioDeCasa = false;

        if (ficha.obtenerEstado() == EstadoFicha.CASA) {
            if (!puedeSalirDeCasa) {
                return new ResultadoJugada(false, false, false, List.of(), null, "Necesitas sacar pares para salir de casa.");
            }
            fichaService.sacarDeCasa(ficha);
            salioDeCasa = true;
        } else if (ficha.obtenerEstado() == EstadoFicha.RECORRIDO) {
            if (!reglaService.validarAvance(ficha, pasos)) {
                return new ResultadoJugada(false, false, false, List.of(), null,
                        "Esa ficha necesita el numero exacto para entrar a la meta.");
            }
            fichaService.avanzarFicha(ficha, pasos);
        } else {
            return new ResultadoJugada(false, false, false, List.of(), null, "Esta ficha ya llego a la meta.");
        }

        movimientoService.registrarMovimiento(ficha, posicionInicial, ficha.obtenerPosicion());

        List<Ficha> capturadas = aplicarCapturas(partida, jugador, ficha);
        if (!capturadas.isEmpty()) {
            jugadorService.actualizarPuntaje(jugador, capturadas.size() * PUNTOS_CAPTURA);
        }

        boolean llegoAMeta = ficha.obtenerEstado() == EstadoFicha.META;
        if (llegoAMeta) {
            jugadorService.actualizarPuntaje(jugador, PUNTOS_META);
        }

        Jugador ganador = obtenerGanador(partida);
        if (ganador != null) {
            finalizarPartida(partida);
        }

        String mensaje = construirMensajeJugada(jugador, ficha, salioDeCasa, llegoAMeta, capturadas, ganador);
        return new ResultadoJugada(true, salioDeCasa, llegoAMeta, capturadas, ganador, mensaje);
    }

    public boolean aplicarPenalizacionTresDobles(Partida partida, Jugador jugador, Ficha ultimaFichaMovida) {
        if (partida == null || jugador == null) {
            return false;
        }
        boolean devuelta = false;
        if (ultimaFichaMovida != null
                && ultimaFichaMovida.obtenerColor() == jugador.obtenerColor()
                && ultimaFichaMovida.obtenerEstado() == EstadoFicha.RECORRIDO
                && ultimaFichaMovida.obtenerPosicion() <= TableroService.TOTAL_CASILLAS_EXTERIOR) {
            Casilla casilla = tableroService.obtenerCasilla(partida.obtenerTablero(), ultimaFichaMovida.obtenerPosicion());
            if (!reglaService.validarCasillaSegura(casilla)) {
                fichaService.regresarSalida(ultimaFichaMovida);
                devuelta = true;
            }
        }
        cambiarTurno(partida);
        return devuelta;
    }

    public boolean estaPartidaActiva() {
        return true;
    }

    public boolean estaPartidaActiva(Partida partida) {
        return partida != null && partida.obtenerEstado() == EstadoPartida.EN_CURSO;
    }

    public MovimientoService obtenerMovimientoService() {
        return movimientoService;
    }

    private List<Ficha> aplicarCapturas(Partida partida, Jugador jugadorActivo, Ficha fichaActiva) {
        if (fichaActiva.obtenerEstado() != EstadoFicha.RECORRIDO
                || fichaActiva.obtenerPosicion() > TableroService.TOTAL_CASILLAS_EXTERIOR) {
            return List.of();
        }
        Casilla casillaDestino = tableroService.obtenerCasilla(partida.obtenerTablero(), fichaActiva.obtenerPosicion());
        List<Ficha> rivales = new ArrayList<>();
        for (Jugador otro : partida.obtenerJugadores()) {
            if (otro != jugadorActivo && otro.obtenerFichas() != null) {
                rivales.addAll(otro.obtenerFichas());
            }
        }
        List<Ficha> capturadas = fichaService.detectarCapturas(fichaActiva, rivales, casillaDestino);
        for (Ficha capturada : capturadas) {
            fichaService.regresarSalida(capturada);
        }
        return capturadas;
    }

    private String construirMensajeJugada(
            Jugador jugador,
            Ficha ficha,
            boolean salioDeCasa,
            boolean llegoAMeta,
            List<Ficha> capturadas,
            Jugador ganador
    ) {
        if (ganador != null) {
            return "¡" + ganador.obtenerNombre() + " ha ganado la partida!";
        }
        StringBuilder sb = new StringBuilder();
        if (salioDeCasa) {
            sb.append(jugador.obtenerNombre()).append(" saco la ficha #").append(ficha.obtenerId())
                    .append(" a la casilla ").append(ficha.obtenerPosicion()).append(".");
        } else if (llegoAMeta) {
            sb.append("¡").append(jugador.obtenerNombre()).append(" corono la ficha #")
                    .append(ficha.obtenerId()).append(" en la META (+").append(PUNTOS_META).append(" pts)!");
        } else {
            sb.append(jugador.obtenerNombre()).append(" movio la ficha #").append(ficha.obtenerId())
                    .append(" a la casilla ").append(ficha.obtenerPosicion()).append(".");
        }
        if (!capturadas.isEmpty()) {
            sb.append(" ¡Capturo ").append(capturadas.size()).append(" ficha(s) rival(es) (+")
                    .append(capturadas.size() * PUNTOS_CAPTURA).append(" pts)!");
        }
        return sb.toString();
    }
}
