package org.example;

import org.example.controller.JuegoController;
import org.example.controller.JugadorController;
import org.example.database.ConexionesBD;
import org.example.view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        ConexionesBD conexionesBD = new ConexionesBD();
        if (conexionesBD.estaConfigurada()) {
            conexionesBD.inicializarEsquema();
        }

        JuegoController juegoController = new JuegoController();
        JugadorController jugadorController = new JugadorController();

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(juegoController, jugadorController);
            ventana.iniciar();
        });
    }
}
