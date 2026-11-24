package org.example.controller;

import org.example.enums.EstadoFicha;
import org.example.enums.EstadoPartida;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.example.service.DadoService;
import org.example.service.FichaService;
import org.example.service.JugadorService;
import org.example.service.PartidaService;
import org.example.service.ReglaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JuegoController {

    private final PartidaService partidaService;
    private final DadoService dadoService;
    private final FichaService fichaService;
    private final JugadorService jugadorService;
    private final ReglaService reglaService;

    private Partida partidaActual;
    private int dado1;
    private int dado2;
    private final List<Integer> dadosDisponibles = new ArrayList<>();
    private boolean lanzamientoFuePar;
    private boolean esperandoLanzamiento = true;
    private boolean turnoExtraPendiente;
    private Ficha fichaSeleccionada;
    private Ficha ultimaFichaMovidaEnTurno;
    private int pasosActivos;
    private String ultimoMensaje = "Configura los jugadores para comenzar una partida.";

    public JuegoController() {
        this(
                new PartidaService(),
                new DadoService(),
                new FichaService(),
                new JugadorService(),
                new ReglaService()
        );
    }

    public JuegoController(
            PartidaService partidaService,
            DadoService dadoService,
            FichaService fichaService,
            JugadorService jugadorService,
            ReglaService reglaService
    ) {
        this.partidaService = partidaService;
        this.dadoService = dadoService;
        this.fichaService = fichaService;
        this.jugadorService = jugadorService;
        this.reglaService = reglaService;
    }

    public void iniciarPartida() {
        if (partidaActual == null) {
            partidaActual = partidaService.crearPartida();
        }
        iniciarPartidaConJugadores(partidaActual.obtenerJugadores());
    }

    public void iniciarPartidaConJugadores(List<Jugador> jugadores) {
        partidaActual = partidaService.crearPartida();
        if (jugadores != null) {
            for (Jugador jugador : jugadores) {
                partidaService.agregarJugador(partidaActual, jugador);
            }
        }
        partidaService.iniciarPartida(partidaActual);
        dado1 = 0;
        dado2 = 0;
        dadosDisponibles.clear();
        lanzamientoFuePar = false;
        esperandoLanzamiento = true;
        turnoExtraPendiente = false;
        fichaSeleccionada = null;
        ultimaFichaMovidaEnTurno = null;
        pasosActivos = 0;

        Jugador primero = obtenerJugadorActual();
        if (primero != null) {
            ultimoMensaje = "Partida iniciada. Turno de " + primero.obtenerNombre()
                    + " (" + primero.obtenerColor() + "). ¡Lanza los dados!";
        } else {
            ultimoMensaje = "Partida creada sin jugadores.";
        }
    }

    public int lanzarDado() {
        int[] valores = lanzarDosDados();
        return valores[0] + valores[1];
    }

    public int[] lanzarDosDados() {
        if (partidaActual == null || partidaActual.obtenerEstado() != EstadoPartida.EN_CURSO) {
            ultimoMensaje = "No hay una partida en curso.";
            return new int[]{0, 0};
        }
        if (!esperandoLanzamiento) {
            ultimoMensaje = "Ya lanzaste los dados. Selecciona una ficha para mover.";
            return new int[]{dado1, dado2};
        }

        Jugador jugadorActual = obtenerJugadorActual();
        int[] valores = dadoService.lanzarDosDados(
                partidaActual.obtenerDado(),
                partidaActual.obtenerSegundoDado()
        );
        dado1 = valores[0];
        dado2 = valores[1];
        lanzamientoFuePar = reglaService.validarDobles(dado1, dado2);

        if (lanzamientoFuePar) {
            int dobles = partidaActual.obtenerDoblesConsecutivos() + 1;
            partidaActual.modificarDoblesConsecutivos(dobles);
            if (reglaService.esTresDoblesSeguidos(dobles)) {
                boolean devuelta = partidaService.aplicarPenalizacionTresDobles(
                        partidaActual, jugadorActual, ultimaFichaMovidaEnTurno
                );
                dadosDisponibles.clear();
                esperandoLanzamiento = true;
                turnoExtraPendiente = false;
                fichaSeleccionada = null;
                ultimaFichaMovidaEnTurno = null;
                pasosActivos = 0;
                Jugador siguiente = obtenerJugadorActual();
                ultimoMensaje = "¡3 pares seguidos (" + dado1 + "-" + dado2 + ") de " + jugadorActual.obtenerNombre() + "! "
                        + (devuelta ? "Su ultima ficha regreso a casa. " : "")
                        + "Turno de " + (siguiente != null ? siguiente.obtenerNombre() : "-") + ".";
                return valores;
            }
        } else {
            partidaActual.modificarDoblesConsecutivos(0);
        }

        dadosDisponibles.clear();
        dadosDisponibles.add(dado1);
        dadosDisponibles.add(dado2);
        turnoExtraPendiente = lanzamientoFuePar;
        esperandoLanzamiento = false;
        fichaSeleccionada = null;

        if (!tieneAlgunMovimientoPosible(jugadorActual)) {
            resolverSinMovimientos(jugadorActual);
        } else {
            seleccionarPasoPorDefecto(jugadorActual);
            if (lanzamientoFuePar) {
                ultimoMensaje = "¡Pares " + dado1 + "-" + dado2 + " para " + jugadorActual.obtenerNombre()
                        + "! Saca una ficha de casa o mueve una ficha resaltada.";
            } else {
                ultimoMensaje = jugadorActual.obtenerNombre() + " saco " + dado1 + " y " + dado2
                        + ". Elige el dado y haz clic en una ficha resaltada.";
            }
        }
        return valores;
    }

    public void seleccionarFicha(Ficha ficha) {
        this.fichaSeleccionada = ficha;
    }

    public void moverFicha(Ficha ficha) {
        if (pasosActivos <= 0 && !dadosDisponibles.isEmpty()) {
            pasosActivos = dadosDisponibles.get(0);
        }
        moverFichaConPasos(ficha, pasosActivos);
    }

    public PartidaService.ResultadoJugada moverFichaConPasos(Ficha ficha, int pasos) {
        if (partidaActual == null || partidaActual.obtenerEstado() != EstadoPartida.EN_CURSO) {
            ultimoMensaje = "La partida ya finalizo o no ha iniciado.";
            return new PartidaService.ResultadoJugada(false, false, false, List.of(), null, ultimoMensaje);
        }
        if (esperandoLanzamiento || dadosDisponibles.isEmpty()) {
            ultimoMensaje = "Primero debes lanzar los dados.";
            return new PartidaService.ResultadoJugada(false, false, false, List.of(), null, ultimoMensaje);
        }

        Jugador jugadorActual = obtenerJugadorActual();
        if (ficha == null || jugadorActual == null || ficha.obtenerColor() != jugadorActual.obtenerColor()) {
            ultimoMensaje = "Esa ficha no pertenece al jugador en turno.";
            return new PartidaService.ResultadoJugada(false, false, false, List.of(), null, ultimoMensaje);
        }

        boolean esSuma = dadosDisponibles.size() == 2
                && pasos == (dadosDisponibles.get(0) + dadosDisponibles.get(1));
        boolean esDadoIndividual = dadosDisponibles.contains(pasos);

        if (ficha.obtenerEstado() == EstadoFicha.CASA) {
            if (!lanzamientoFuePar) {
                ultimoMensaje = "Solo puedes sacar fichas de casa cuando sacas pares.";
                return new PartidaService.ResultadoJugada(false, false, false, List.of(), null, ultimoMensaje);
            }
            PartidaService.ResultadoJugada res = partidaService.ejecutarJugada(
                    partidaActual, jugadorActual, ficha, dadosDisponibles.get(0), true
            );
            if (!res.exitosa()) {
                ultimoMensaje = res.mensaje();
                return res;
            }
            dadosDisponibles.remove(0);
            procesarPostJugada(jugadorActual, ficha, res);
            return res;
        }

        if (!esDadoIndividual && !esSuma) {
            ultimoMensaje = "Selecciona un valor de dado disponible para mover.";
            return new PartidaService.ResultadoJugada(false, false, false, List.of(), null, ultimoMensaje);
        }

        PartidaService.ResultadoJugada res = partidaService.ejecutarJugada(
                partidaActual, jugadorActual, ficha, pasos, false
        );
        if (!res.exitosa()) {
            ultimoMensaje = res.mensaje();
            return res;
        }

        if (esSuma) {
            dadosDisponibles.clear();
        } else {
            dadosDisponibles.remove(Integer.valueOf(pasos));
        }

        procesarPostJugada(jugadorActual, ficha, res);
        return res;
    }

    public void finalizarPartida() {
        if (partidaActual != null) {
            partidaService.finalizarPartida(partidaActual);
            dadosDisponibles.clear();
            esperandoLanzamiento = false;
            ultimoMensaje = "Partida finalizada.";
        }
    }

    public Partida obtenerPartidaActual() {
        return partidaActual;
    }

    public Jugador obtenerJugadorActual() {
        return partidaService.obtenerJugadorActual(partidaActual);
    }

    public int obtenerDado1() {
        return dado1;
    }

    public int obtenerDado2() {
        return dado2;
    }

    public List<Integer> obtenerDadosDisponibles() {
        return Collections.unmodifiableList(dadosDisponibles);
    }

    public boolean estaEsperandoLanzamiento() {
        return esperandoLanzamiento;
    }

    public boolean esLanzamientoPar() {
        return lanzamientoFuePar;
    }

    public int obtenerPasosActivos() {
        return pasosActivos;
    }

    public void seleccionarPasosActivos(int pasos) {
        if (dadosDisponibles.contains(pasos)
                || (dadosDisponibles.size() == 2 && pasos == dadosDisponibles.get(0) + dadosDisponibles.get(1))) {
            this.pasosActivos = pasos;
        }
    }

    public Ficha obtenerFichaSeleccionada() {
        return fichaSeleccionada;
    }

    public String obtenerUltimoMensaje() {
        return ultimoMensaje;
    }

    public List<Ficha> obtenerFichasMoviblesActuales() {
        if (esperandoLanzamiento || dadosDisponibles.isEmpty() || partidaActual == null) {
            return List.of();
        }
        Jugador jugadorActual = obtenerJugadorActual();
        if (jugadorActual == null) {
            return List.of();
        }
        if (pasosActivos > 0) {
            boolean esSuma = dadosDisponibles.size() == 2
                    && pasosActivos == dadosDisponibles.get(0) + dadosDisponibles.get(1);
            boolean puedeSalir = lanzamientoFuePar && !esSuma;
            return partidaService.obtenerFichasMovibles(partidaActual, jugadorActual, pasosActivos, puedeSalir);
        }
        return List.of();
    }

    public boolean puedeUsarSuma() {
        if (esperandoLanzamiento || dadosDisponibles.size() != 2 || partidaActual == null) {
            return false;
        }
        Jugador jugadorActual = obtenerJugadorActual();
        int suma = dadosDisponibles.get(0) + dadosDisponibles.get(1);
        return !partidaService.obtenerFichasMovibles(partidaActual, jugadorActual, suma, false).isEmpty();
    }

    public boolean puedeUsarDado(int valor) {
        if (esperandoLanzamiento || !dadosDisponibles.contains(valor) || partidaActual == null) {
            return false;
        }
        Jugador jugadorActual = obtenerJugadorActual();
        return !partidaService.obtenerFichasMovibles(partidaActual, jugadorActual, valor, lanzamientoFuePar).isEmpty();
    }

    public FichaService obtenerFichaService() {
        return fichaService;
    }

    private void procesarPostJugada(Jugador jugadorActual, Ficha ficha, PartidaService.ResultadoJugada res) {
        ultimaFichaMovidaEnTurno = ficha;
        fichaSeleccionada = null;

        if (res.ganador() != null) {
            dadosDisponibles.clear();
            esperandoLanzamiento = false;
            turnoExtraPendiente = false;
            pasosActivos = 0;
            ultimoMensaje = res.mensaje();
            return;
        }

        StringBuilder msg = new StringBuilder(res.mensaje());

        if (dadosDisponibles.isEmpty() || !tieneAlgunMovimientoPosible(jugadorActual)) {
            dadosDisponibles.clear();
            esperandoLanzamiento = true;
            pasosActivos = 0;
            if (turnoExtraPendiente) {
                turnoExtraPendiente = false;
                msg.append(" ¡Repites turno por pares!");
            } else {
                ultimaFichaMovidaEnTurno = null;
                partidaService.cambiarTurno(partidaActual);
                Jugador siguiente = obtenerJugadorActual();
                msg.append(" Turno de ").append(siguiente != null ? siguiente.obtenerNombre() : "-").append(".");
            }
        } else {
            seleccionarPasoPorDefecto(jugadorActual);
            msg.append(" Te queda un dado de ").append(dadosDisponibles.get(0)).append(".");
        }

        ultimoMensaje = msg.toString();
    }

    private void resolverSinMovimientos(Jugador jugadorActual) {
        dadosDisponibles.clear();
        esperandoLanzamiento = true;
        pasosActivos = 0;

        if (jugadorService.sinFichasActivasEnRecorridoYTieneEnCasa(jugadorActual) && !lanzamientoFuePar) {
            int intentos = partidaActual.obtenerIntentosSalidaCasa() + 1;
            partidaActual.modificarIntentosSalidaCasa(intentos);
            if (intentos < ReglaService.MAX_INTENTOS_SALIDA_CASA) {
                ultimoMensaje = jugadorActual.obtenerNombre() + " saco " + dado1 + "-" + dado2
                        + " (sin pares). Intento " + intentos + "/" + ReglaService.MAX_INTENTOS_SALIDA_CASA
                        + " para salir de casa: ¡lanza de nuevo!";
                return;
            }
            partidaService.cambiarTurno(partidaActual);
            Jugador siguiente = obtenerJugadorActual();
            ultimoMensaje = jugadorActual.obtenerNombre() + " agoto sus 3 intentos sin pares. Turno de "
                    + (siguiente != null ? siguiente.obtenerNombre() : "-") + ".";
            return;
        }

        if (turnoExtraPendiente) {
            turnoExtraPendiente = false;
            ultimoMensaje = jugadorActual.obtenerNombre() + " no tiene jugadas validas, pero saco pares: ¡lanza de nuevo!";
        } else {
            ultimaFichaMovidaEnTurno = null;
            partidaService.cambiarTurno(partidaActual);
            Jugador siguiente = obtenerJugadorActual();
            ultimoMensaje = jugadorActual.obtenerNombre() + " saco " + dado1 + "-" + dado2
                    + " sin movimientos posibles. Turno de "
                    + (siguiente != null ? siguiente.obtenerNombre() : "-") + ".";
        }
    }

    private boolean tieneAlgunMovimientoPosible(Jugador jugador) {
        Set<Integer> opciones = new LinkedHashSet<>(dadosDisponibles);
        for (int valor : opciones) {
            if (!partidaService.obtenerFichasMovibles(partidaActual, jugador, valor, lanzamientoFuePar).isEmpty()) {
                return true;
            }
        }
        if (dadosDisponibles.size() == 2) {
            int suma = dadosDisponibles.get(0) + dadosDisponibles.get(1);
            return !partidaService.obtenerFichasMovibles(partidaActual, jugador, suma, false).isEmpty();
        }
        return false;
    }

    private void seleccionarPasoPorDefecto(Jugador jugador) {
        for (int valor : dadosDisponibles) {
            if (!partidaService.obtenerFichasMovibles(partidaActual, jugador, valor, lanzamientoFuePar).isEmpty()) {
                pasosActivos = valor;
                return;
            }
        }
        if (dadosDisponibles.size() == 2) {
            int suma = dadosDisponibles.get(0) + dadosDisponibles.get(1);
            if (!partidaService.obtenerFichasMovibles(partidaActual, jugador, suma, false).isEmpty()) {
                pasosActivos = suma;
                return;
            }
        }
        pasosActivos = 0;
    }
}
