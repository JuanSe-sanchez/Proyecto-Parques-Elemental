package org.example.view;

import org.example.dto.JugadorDTO;
import org.example.enums.EstadoFicha;
import org.example.model.Ficha;
import org.example.model.Jugador;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class PanelJugador extends JPanel {

    private final JLabel etiquetaTurno;
    private final JPanel panelListaJugadores;
    private final JButton botonNuevaPartida;
    private final JButton botonEstadisticas;
    private final JButton botonHistorial;

    public PanelJugador() {
        setLayout(new BorderLayout(8, 10));
        setPreferredSize(new Dimension(285, 660));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(205, 205, 205)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        setBackground(new Color(250, 250, 252));

        etiquetaTurno = new JLabel("Jugadores", SwingConstants.CENTER);
        etiquetaTurno.setFont(new Font("SansSerif", Font.BOLD, 16));
        etiquetaTurno.setOpaque(true);
        etiquetaTurno.setBackground(new Color(235, 240, 248));
        etiquetaTurno.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        panelListaJugadores = new JPanel();
        panelListaJugadores.setLayout(new BoxLayout(panelListaJugadores, BoxLayout.Y_AXIS));
        panelListaJugadores.setOpaque(false);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 6, 8));
        panelBotones.setOpaque(false);

        botonNuevaPartida = new JButton("Nueva Partida");
        botonEstadisticas = new JButton("Ver Estadisticas");
        botonHistorial = new JButton("Historial de Partidas");

        Font fontBtn = new Font("SansSerif", Font.BOLD, 13);
        botonNuevaPartida.setFont(fontBtn);
        botonEstadisticas.setFont(fontBtn);
        botonHistorial.setFont(fontBtn);

        panelBotones.add(botonNuevaPartida);
        panelBotones.add(botonEstadisticas);
        panelBotones.add(botonHistorial);

        add(etiquetaTurno, BorderLayout.NORTH);
        add(panelListaJugadores, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void asignarAccionNuevaPartida(Runnable accion) {
        botonNuevaPartida.addActionListener(e -> accion.run());
    }

    public void asignarAccionEstadisticas(Runnable accion) {
        botonEstadisticas.addActionListener(e -> accion.run());
    }

    public void asignarAccionHistorial(Runnable accion) {
        botonHistorial.addActionListener(e -> accion.run());
    }

    public void actualizarTurno(String textoTurno) {
        etiquetaTurno.setText(textoTurno);
    }

    public void actualizarJugador(JugadorDTO jugador) {
        if (jugador != null) {
            etiquetaTurno.setText("Turno: " + jugador.obtenerNombre() + " (" + jugador.obtenerColor() + ")");
        }
    }

    public void mostrarPuntaje() {
        revalidate();
        repaint();
    }

    public void actualizarJugadores(List<Jugador> jugadores, Jugador jugadorEnTurno) {
        panelListaJugadores.removeAll();

        if (jugadorEnTurno != null) {
            etiquetaTurno.setText("Turno: " + jugadorEnTurno.obtenerNombre());
            etiquetaTurno.setBackground(PanelTablero.colorSuave(jugadorEnTurno.obtenerColor()));
            etiquetaTurno.setForeground(new Color(30, 30, 30));
        } else {
            etiquetaTurno.setText("Sin partida activa");
            etiquetaTurno.setBackground(new Color(235, 240, 248));
        }

        if (jugadores != null) {
            for (Jugador jugador : jugadores) {
                boolean esTurno = jugador == jugadorEnTurno;
                panelListaJugadores.add(crearTarjetaJugador(jugador, esTurno));
                panelListaJugadores.add(Box.createVerticalStrut(10));
            }
        }

        panelListaJugadores.revalidate();
        panelListaJugadores.repaint();
    }

    private JPanel crearTarjetaJugador(Jugador jugador, boolean esTurno) {
        JPanel tarjeta = new JPanel(new BorderLayout(6, 4));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color colorJugador = PanelTablero.colorPrincipal(jugador.obtenerColor());
        tarjeta.setBackground(esTurno ? PanelTablero.colorSuave(jugador.obtenerColor()) : Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(esTurno ? colorJugador : new Color(215, 215, 215), esTurno ? 2 : 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JLabel titulo = new JLabel((esTurno ? "▶ " : "") + jugador.obtenerNombre() + " (" + jugador.obtenerColor() + ")");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        titulo.setForeground(new Color(35, 35, 35));

        int enCasa = 0;
        int enRecorrido = 0;
        int enMeta = 0;
        if (jugador.obtenerFichas() != null) {
            for (Ficha f : jugador.obtenerFichas()) {
                if (f.obtenerEstado() == EstadoFicha.CASA) enCasa++;
                else if (f.obtenerEstado() == EstadoFicha.RECORRIDO) enRecorrido++;
                else if (f.obtenerEstado() == EstadoFicha.META) enMeta++;
            }
        }

        JLabel detalleFichas = new JLabel(
                "Casa: " + enCasa + "  |  Juego: " + enRecorrido + "  |  Meta: " + enMeta + "/4"
        );
        detalleFichas.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel puntaje = new JLabel("Puntaje: " + jugador.obtenerPuntaje() + " pts");
        puntaje.setFont(new Font("SansSerif", Font.BOLD, 12));
        puntaje.setForeground(new Color(65, 65, 65));

        tarjeta.add(titulo, BorderLayout.NORTH);
        tarjeta.add(detalleFichas, BorderLayout.CENTER);
        tarjeta.add(puntaje, BorderLayout.SOUTH);

        return tarjeta;
    }
}
