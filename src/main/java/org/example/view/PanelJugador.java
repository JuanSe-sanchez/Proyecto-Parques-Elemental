package org.example.view;

import javax.swing.*;

public class PanelJugador extends JPanel {
    private JLabel etiquetaTurno;
    public PanelJugador() {
        etiquetaTurno = new JLabel(Jugadores)JLabel("Jugadores");
        add(etiquetaTurno);
    }

    public void actualizarTurno(String textoTurno) {
        etiquetaTurno.setText(textoTurno);
    }
    
}
