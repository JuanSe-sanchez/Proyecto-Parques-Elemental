package org.example.view;

import org.example.controller.JuegoController;
import org.example.controller.JugadorController;
import org.example.dto.EstadisticaDTO;
import org.example.dto.PartidaDTO;
import org.example.enums.ColorJugador;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.example.service.PartidaService;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JuegoController juegoController;
    private final JugadorController jugadorController;

    private final PanelTablero panelTablero;
    private final PanelJugador panelJugador;
    private final PanelDados panelDados;

    public VentanaPrincipal() {
        this(new JuegoController(), new JugadorController());
    }

    public VentanaPrincipal(JuegoController juegoController, JugadorController jugadorController) {
        this.juegoController = juegoController;
        this.jugadorController = jugadorController;

        setTitle("Parqués Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelTablero = new PanelTablero();
        panelJugador = new PanelJugador();
        panelDados = new PanelDados();

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
        add(panelDados, BorderLayout.SOUTH);

        configurarEventos();

        setSize(1080, 780);
        setMinimumSize(new Dimension(940, 700));
        setLocationRelativeTo(null);
    }

    public void iniciar() {
        mostrarVentana();
        mostrarDialogoNuevaPartida();
    }

    public void actualizarVista() {
        Partida partida = juegoController.obtenerPartidaActual();
        Jugador actual = juegoController.obtenerJugadorActual();
        List<Ficha> movibles = juegoController.obtenerFichasMoviblesActuales();

        panelTablero.actualizarEstado(partida, movibles);
        panelJugador.actualizarJugadores(partida != null ? partida.obtenerJugadores() : List.of(), actual);

        int d1 = juegoController.obtenerDado1();
        int d2 = juegoController.obtenerDado2();
        boolean puedeLanzar = partida != null
                && partida.obtenerGanador() == null
                && juegoController.estaEsperandoLanzamiento();

        panelDados.actualizarControles(
                d1,
                d2,
                juegoController.obtenerDadosDisponibles(),
                puedeLanzar,
                juegoController.puedeUsarDado(d1),
                juegoController.puedeUsarDado(d2),
                juegoController.puedeUsarSuma(),
                juegoController.obtenerPasosActivos(),
                actual != null ? actual.obtenerColor() : null,
                juegoController.obtenerUltimoMensaje()
        );

        refrescarVista();
    }

    public void mostrarGanador(Jugador ganador) {
        if (ganador == null) {
            return;
        }
        JOptionPane.showMessageDialog(
                this,
                "¡Felicitaciones " + ganador.obtenerNombre() + " (" + ganador.obtenerColor() + ")!\n"
                        + "Has llevado tus 4 fichas a la META con " + ganador.obtenerPuntaje() + " puntos.",
                "¡Tenemos Ganador!",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public PanelTablero obtenerPanelTablero() {
        return panelTablero;
    }

    public PanelJugador obtenerPanelJugador() {
        return panelJugador;
    }

    public PanelDados obtenerPanelDados() {
        return panelDados;
    }

    public void mostrarVentana() {
        setVisible(true);
    }

    public void refrescarVista() {
        revalidate();
        repaint();
    }

    private void configurarEventos() {
        panelDados.asignarAccionLanzar(() -> {
            juegoController.lanzarDosDados();
            actualizarVista();
        });

        panelDados.asignarAccionSeleccionPasos(pasos -> {
            juegoController.seleccionarPasosActivos(pasos);
            actualizarVista();
        });

        panelTablero.asignarListenerSeleccionFicha(ficha -> {
            juegoController.seleccionarFicha(ficha);
            PartidaService.ResultadoJugada resultado = juegoController.moverFichaConPasos(
                    ficha,
                    juegoController.obtenerPasosActivos()
            );
            actualizarVista();
            if (resultado.ganador() != null) {
                mostrarGanador(resultado.ganador());
            }
        });

        panelJugador.asignarAccionNuevaPartida(this::mostrarDialogoNuevaPartida);
        panelJugador.asignarAccionEstadisticas(this::mostrarDialogoEstadisticas);
        panelJugador.asignarAccionHistorial(this::mostrarDialogoHistorial);
    }

    private void mostrarDialogoNuevaPartida() {
        Integer[] opcionesCantidad = {2, 3, 4};
        Integer cantidad = (Integer) JOptionPane.showInputDialog(
                this,
                "Selecciona la cantidad de jugadores:",
                "Nueva Partida de Parqués",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesCantidad,
                2
        );

        if (cantidad == null) {
            return;
        }

        ColorJugador[] colores = ColorJugador.values();
        JTextField[] camposNombre = new JTextField[cantidad];
        @SuppressWarnings("unchecked")
        JComboBox<ColorJugador>[] combosColor = new JComboBox[cantidad];

        JPanel panelForm = new JPanel(new GridLayout(cantidad + 1, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panelForm.add(new JLabel("Nombre del Jugador"));
        panelForm.add(new JLabel("Color"));

        for (int i = 0; i < cantidad; i++) {
            camposNombre[i] = new JTextField("Jugador " + (i + 1), 14);
            combosColor[i] = new JComboBox<>(colores);
            combosColor[i].setSelectedIndex(i);
            panelForm.add(camposNombre[i]);
            panelForm.add(combosColor[i]);
        }

        while (true) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    panelForm,
                    "Registrar Jugadores",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            Set<ColorJugador> coloresUsados = new HashSet<>();
            List<Jugador> jugadores = new ArrayList<>();
            String error = null;

            for (int i = 0; i < cantidad; i++) {
                String nombre = camposNombre[i].getText().trim();
                ColorJugador color = (ColorJugador) combosColor[i].getSelectedItem();

                if (nombre.isEmpty()) {
                    error = "Todos los jugadores deben tener un nombre.";
                    break;
                }
                if (!coloresUsados.add(color)) {
                    error = "Cada jugador debe elegir un color diferente.";
                    break;
                }
                jugadores.add(jugadorController.registrarJugador(nombre, color));
            }

            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Datos invalidos", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            juegoController.iniciarPartidaConJugadores(jugadores);
            actualizarVista();
            return;
        }
    }

    private void mostrarDialogoEstadisticas() {
        List<EstadisticaDTO> estadisticas = jugadorController.consultarEstadisticas();
        if (estadisticas.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aun no hay estadisticas registradas (o no hay conexion con la base de datos).",
                    "Estadisticas",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] columnas = {"Jugador", "Partidas Jugadas", "Partidas Ganadas", "% Victoria"};
        Object[][] filas = new Object[estadisticas.size()][4];
        for (int i = 0; i < estadisticas.size(); i++) {
            EstadisticaDTO dto = estadisticas.get(i);
            int jugadas = dto.obtenerPartidasJugadas();
            int ganadas = dto.obtenerPartidasGanadas();
            double pct = jugadas > 0 ? (ganadas * 100.0) / jugadas : 0.0;
            filas[i][0] = dto.obtenerNombreJugador() != null ? dto.obtenerNombreJugador() : "ID " + dto.obtenerJugadorId();
            filas[i][1] = jugadas;
            filas[i][2] = ganadas;
            filas[i][3] = String.format("%.1f%%", pct);
        }

        JTable tabla = new JTable(filas, columnas);
        tabla.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, scroll, "Estadisticas de Jugadores", JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarDialogoHistorial() {
        List<PartidaDTO> historial = jugadorController.consultarHistorial();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aun no hay partidas registradas en el historial.",
                    "Historial de Partidas",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] columnas = {"ID", "Fecha", "Ganador"};
        Object[][] filas = new Object[historial.size()][3];
        for (int i = 0; i < historial.size(); i++) {
            PartidaDTO dto = historial.get(i);
            filas[i][0] = dto.obtenerId();
            filas[i][1] = dto.obtenerFecha() != null ? dto.obtenerFecha().format(FORMATO_FECHA) : "-";
            filas[i][2] = dto.obtenerGanador() != null ? dto.obtenerGanador() : "En curso";
        }

        JTable tabla = new JTable(filas, columnas);
        tabla.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(460, 260));

        JOptionPane.showMessageDialog(this, scroll, "Historial de Partidas", JOptionPane.PLAIN_MESSAGE);
    }
}
