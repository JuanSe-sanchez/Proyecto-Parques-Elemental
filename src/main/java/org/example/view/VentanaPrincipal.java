package org.example.view;

import javax.swing.*;
import java.awt.*;

import static com.sun.org.apache.xerces.internal.util.DOMUtil.setVisible;
import static java.awt.AWTEventMulticaster.add;

public class VentanaPrincipal {

    private PanelTablero panelTablero;
    private PanelJugador panelJugador;
    private PanelDados panelDados;

    public VentanaPrincipal() {

    }

    private void setLocationRelativeTo(Object o) {
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
