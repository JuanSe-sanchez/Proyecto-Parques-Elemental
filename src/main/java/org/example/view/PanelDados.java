package org.example.view;

import org.example.enums.ColorJugador;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.IntConsumer;

public class PanelDados extends JPanel {

    private final DadoVisual dadoVisual1;
    private final DadoVisual dadoVisual2;
    private final BotonLanzar botonLanzar;
    private final JToggleButton botonDado1;
    private final JToggleButton botonDado2;
    private final JToggleButton botonSuma;
    private final ButtonGroup grupoOpciones;
    private final JLabel etiquetaMensaje;

    private IntConsumer alSeleccionarPasos;
    private int valorDado1;
    private int valorDado2;

    public PanelDados() {
        setLayout(new BorderLayout(12, 6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(205, 205, 205)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        setBackground(new Color(248, 249, 251));

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelIzquierdo.setOpaque(false);

        dadoVisual1 = new DadoVisual();
        dadoVisual2 = new DadoVisual();

        botonLanzar = new BotonLanzar("Lanzar Dados");
        botonLanzar.setFont(new Font("SansSerif", Font.BOLD, 14));
        botonLanzar.setPreferredSize(new Dimension(150, 42));

        panelIzquierdo.add(dadoVisual1);
        panelIzquierdo.add(dadoVisual2);
        panelIzquierdo.add(botonLanzar);

        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelOpciones.setOpaque(false);

        botonDado1 = new JToggleButton("Dado 1");
        botonDado2 = new JToggleButton("Dado 2");
        botonSuma = new JToggleButton("Suma");

        Font fontOpcion = new Font("SansSerif", Font.BOLD, 12);
        botonDado1.setFont(fontOpcion);
        botonDado2.setFont(fontOpcion);
        botonSuma.setFont(fontOpcion);

        grupoOpciones = new ButtonGroup();
        grupoOpciones.add(botonDado1);
        grupoOpciones.add(botonDado2);
        grupoOpciones.add(botonSuma);

        botonDado1.addActionListener(e -> notificarPaso(valorDado1));
        botonDado2.addActionListener(e -> notificarPaso(valorDado2));
        botonSuma.addActionListener(e -> notificarPaso(valorDado1 + valorDado2));

        panelOpciones.add(new JLabel("Usar:"));
        panelOpciones.add(botonDado1);
        panelOpciones.add(botonDado2);
        panelOpciones.add(botonSuma);

        etiquetaMensaje = new JLabel("Bienvenido a Parqués Master. Inicia una partida para jugar.", SwingConstants.LEFT);
        etiquetaMensaje.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiquetaMensaje.setForeground(new Color(45, 45, 45));

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelOpciones, BorderLayout.EAST);
        add(etiquetaMensaje, BorderLayout.SOUTH);

        actualizarControles(0, 0, List.of(), true, false, false, false, 0, null, etiquetaMensaje.getText());
    }

    public void asignarAccionLanzar(Runnable accion) {
        botonLanzar.addActionListener(e -> accion.run());
    }

    public void asignarAccionSeleccionPasos(IntConsumer listener) {
        this.alSeleccionarPasos = listener;
    }

    public void mostrarDados(int valor) {
        actualizarValor(valor);
    }

    public void actualizarValor(int valor) {
        this.valorDado1 = valor;
        dadoVisual1.modificarValor(valor);
    }

    public void actualizarControles(
            int d1,
            int d2,
            List<Integer> disponibles,
            boolean puedeLanzar,
            boolean puedeDado1,
            boolean puedeDado2,
            boolean puedeSuma,
            int pasosActivos,
            String mensaje
    ) {
        actualizarControles(d1, d2, disponibles, puedeLanzar, puedeDado1, puedeDado2, puedeSuma, pasosActivos, null, mensaje);
    }

    public void actualizarControles(
            int d1,
            int d2,
            List<Integer> disponibles,
            boolean puedeLanzar,
            boolean puedeDado1,
            boolean puedeDado2,
            boolean puedeSuma,
            int pasosActivos,
            ColorJugador colorJugadorActual,
            String mensaje
    ) {
        this.valorDado1 = d1;
        this.valorDado2 = d2;

        dadoVisual1.modificarValor(d1);
        dadoVisual2.modificarValor(d2);

        botonLanzar.setEnabled(puedeLanzar);
        botonLanzar.modificarColorJugador(colorJugadorActual);

        boolean d1Disponible = disponibles.contains(d1) && d1 > 0;
        boolean d2Disponible = (d1 == d2)
                ? disponibles.size() >= 2
                : (disponibles.contains(d2) && d2 > 0);

        botonDado1.setText(d1 > 0 ? "Dado 1 (" + d1 + ")" : "Dado 1");
        botonDado2.setText(d2 > 0 ? "Dado 2 (" + d2 + ")" : "Dado 2");
        botonSuma.setText((d1 > 0 && d2 > 0) ? "Suma (" + (d1 + d2) + ")" : "Suma");

        botonDado1.setEnabled(!puedeLanzar && d1Disponible && puedeDado1);
        botonDado2.setEnabled(!puedeLanzar && d2Disponible && puedeDado2);
        botonSuma.setEnabled(!puedeLanzar && disponibles.size() == 2 && puedeSuma);

        grupoOpciones.clearSelection();
        if (!puedeLanzar && pasosActivos > 0) {
            if (botonSuma.isEnabled() && pasosActivos == (d1 + d2)) {
                botonSuma.setSelected(true);
            } else if (botonDado1.isEnabled() && pasosActivos == d1) {
                botonDado1.setSelected(true);
            } else if (botonDado2.isEnabled() && pasosActivos == d2) {
                botonDado2.setSelected(true);
            }
        }

        etiquetaMensaje.setText(mensaje != null ? mensaje : "");
        repaint();
    }

    private void notificarPaso(int pasos) {
        if (alSeleccionarPasos != null && pasos > 0) {
            alSeleccionarPasos.accept(pasos);
        }
    }

    private static class BotonLanzar extends JButton {
        private Color colorTextoJugador = Color.WHITE;
        private Color colorBordeJugador = new Color(70, 78, 92);

        BotonLanzar(String texto) {
            super(texto);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void modificarColorJugador(ColorJugador color) {
            if (color == null) {
                this.colorTextoJugador = Color.WHITE;
                this.colorBordeJugador = new Color(90, 98, 112);
            } else {
                this.colorTextoJugador = switch (color) {
                    case ROJO -> new Color(255, 85, 85);
                    case AZUL -> new Color(90, 170, 255);
                    case VERDE -> new Color(75, 220, 120);
                    case AMARILLO -> new Color(255, 215, 55);
                };
                this.colorBordeJugador = this.colorTextoJugador;
            }
            setForeground(this.colorTextoJugador);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondo = isEnabled()
                    ? (getModel().isPressed() ? new Color(24, 28, 36) : new Color(38, 43, 54))
                    : new Color(215, 218, 224);

            g2.setColor(fondo);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

            g2.setColor(isEnabled() ? colorBordeJugador : new Color(180, 185, 192));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

            g2.setFont(getFont());
            g2.setColor(isEnabled() ? colorTextoJugador : new Color(130, 135, 145));
            String texto = getText();
            int tw = g2.getFontMetrics().stringWidth(texto);
            int th = g2.getFontMetrics().getAscent();
            g2.drawString(texto, (getWidth() - tw) / 2, (getHeight() + th) / 2 - 2);

            g2.dispose();
        }
    }

    private static class DadoVisual extends JPanel {
        private int valor;

        DadoVisual() {
            setPreferredSize(new Dimension(48, 48));
            setOpaque(false);
        }

        void modificarValor(int valor) {
            this.valor = valor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, size, size, 10, 10);
            g2.setColor(new Color(80, 80, 80));
            g2.drawRoundRect(x, y, size, size, 10, 10);

            if (valor >= 1 && valor <= 6) {
                g2.setColor(new Color(35, 35, 35));
                int r = 4;
                int c1 = x + size / 4;
                int c2 = x + size / 2;
                int c3 = x + (3 * size) / 4;
                int r1 = y + size / 4;
                int r2 = y + size / 2;
                int r3 = y + (3 * size) / 4;

                if (valor == 1 || valor == 3 || valor == 5) {
                    dibujarPunto(g2, c2, r2, r);
                }
                if (valor >= 2) {
                    dibujarPunto(g2, c1, r1, r);
                    dibujarPunto(g2, c3, r3, r);
                }
                if (valor >= 4) {
                    dibujarPunto(g2, c3, r1, r);
                    dibujarPunto(g2, c1, r3, r);
                }
                if (valor == 6) {
                    dibujarPunto(g2, c1, r2, r);
                    dibujarPunto(g2, c3, r2, r);
                }
            } else {
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("?", x + size / 2 - 4, y + size / 2 + 6);
            }

            g2.dispose();
        }

        private void dibujarPunto(Graphics2D g2, int cx, int cy, int r) {
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        }
    }
}
