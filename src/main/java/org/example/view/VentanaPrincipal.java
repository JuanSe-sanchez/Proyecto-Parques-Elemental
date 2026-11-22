package org.example.view;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final PanelTablero panelTablero;
    private final PanelJugador panelJugador;
    private final PanelDados panelDados;

    public VentanaPrincipal() {

        setTitle("Parqués");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panelTablero = new PanelTablero();
        panelJugador = new PanelJugador();
        panelDados = new PanelDados();

        add(panelTablero, BorderLayout.CENTER);
        add(panelJugador, BorderLayout.EAST);
        add(panelDados, BorderLayout.SOUTH);

        setSize(1000, 700);
        setLocationRelativeTo(null);
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
}