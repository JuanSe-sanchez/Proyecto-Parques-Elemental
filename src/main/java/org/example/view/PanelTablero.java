package org.example.view;

import org.example.enums.ColorJugador;
import org.example.enums.EstadoFicha;
import org.example.enums.TipoCasilla;
import org.example.model.Casilla;
import org.example.model.Ficha;
import org.example.model.Jugador;
import org.example.model.Partida;
import org.example.service.TableroService;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class PanelTablero extends JPanel {

    private static final int GRID_SIZE = 19;

    private static final Color FONDO_TABLERO = new Color(245, 241, 232);
    private static final Color COLOR_CASILLA_NORMAL = new Color(255, 253, 247);
    private static final Color COLOR_CASILLA_SEGURO = new Color(224, 236, 248);
    private static final Color COLOR_BORDE_CASILLA = new Color(165, 165, 165);

    private Partida partida;
    private Set<Ficha> fichasMovibles = new HashSet<>();
    private Consumer<Ficha> alSeleccionarFicha;

    private final Map<Integer, Point> coordenadasCasillas = new HashMap<>();
    private final Map<ColorJugador, List<Point>> coordenadasPasillos = new HashMap<>();
    private final List<FichaDibujada> fichasEnPantalla = new ArrayList<>();

    private record FichaDibujada(Ficha ficha, Ellipse2D.Double hitbox) {
    }

    public PanelTablero() {
        setBackground(FONDO_TABLERO);
        setPreferredSize(new Dimension(660, 660));
        construirMapaCoordenadas();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manejarClick(e.getPoint());
            }
        });
    }

    public void asignarListenerSeleccionFicha(Consumer<Ficha> listener) {
        this.alSeleccionarFicha = listener;
    }

    public void actualizarEstado(Partida partida, List<Ficha> movibles) {
        this.partida = partida;
        this.fichasMovibles = movibles != null ? new HashSet<>(movibles) : new HashSet<>();
        actualizarFichas();
    }

    public void actualizarFichas() {
        refrescarTablero();
    }

    public void dibujarTablero() {
        refrescarTablero();
    }

    public void refrescarTablero() {
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int lado = Math.min(getWidth() - 24, getHeight() - 24);
        int tamCelda = Math.max(20, lado / GRID_SIZE);
        int tamTablero = tamCelda * GRID_SIZE;
        int offsetX = (getWidth() - tamTablero) / 2;
        int offsetY = (getHeight() - tamTablero) / 2;

        dibujarFondoCasas(g2, offsetX, offsetY, tamCelda);
        dibujarCasillasExteriores(g2, offsetX, offsetY, tamCelda);
        dibujarPasillosMeta(g2, offsetX, offsetY, tamCelda);
        dibujarCentroMeta(g2, offsetX, offsetY, tamCelda);
        dibujarFichas(g2, offsetX, offsetY, tamCelda);

        g2.dispose();
    }

    private void manejarClick(Point punto) {
        if (alSeleccionarFicha == null) {
            return;
        }
        // Priorizar fichas movibles si hay solapamiento
        for (int i = fichasEnPantalla.size() - 1; i >= 0; i--) {
            FichaDibujada fd = fichasEnPantalla.get(i);
            if (fichasMovibles.contains(fd.ficha()) && fd.hitbox().contains(punto)) {
                alSeleccionarFicha.accept(fd.ficha());
                return;
            }
        }
        for (int i = fichasEnPantalla.size() - 1; i >= 0; i--) {
            FichaDibujada fd = fichasEnPantalla.get(i);
            if (fd.hitbox().contains(punto)) {
                alSeleccionarFicha.accept(fd.ficha());
                return;
            }
        }
    }

    private void dibujarFondoCasas(Graphics2D g2, int ox, int oy, int c) {
        // Sentido antihorario: ROJO arriba-izquierda, AZUL abajo-izquierda, VERDE abajo-derecha, AMARILLO arriba-derecha
        dibujarZonaCasa(g2, ox, oy, 8 * c, ColorJugador.ROJO);
        dibujarZonaCasa(g2, ox, oy + 11 * c, 8 * c, ColorJugador.AZUL);
        dibujarZonaCasa(g2, ox + 11 * c, oy + 11 * c, 8 * c, ColorJugador.VERDE);
        dibujarZonaCasa(g2, ox + 11 * c, oy, 8 * c, ColorJugador.AMARILLO);
    }

    private void dibujarZonaCasa(Graphics2D g2, int x, int y, int size, ColorJugador color) {
        Color base = colorPrincipal(color);
        g2.setColor(colorSuave(color));
        g2.fillRoundRect(x + 2, y + 2, size - 4, size - 4, 16, 16);

        g2.setColor(base);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(x + 2, y + 2, size - 4, size - 4, 16, 16);

        int margen = size / 6;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x + margen, y + margen, size - 2 * margen, size - 2 * margen, 14, 14);
        g2.setColor(base);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x + margen, y + margen, size - 2 * margen, size - 2 * margen, 14, 14);

        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(11, size / 14)));
        FontMetrics fm = g2.getFontMetrics();
        String titulo = "CASA " + color.name();
        int tx = x + (size - fm.stringWidth(titulo)) / 2;
        int ty = y + margen - 6;
        g2.drawString(titulo, tx, ty);
    }

    private void dibujarCasillasExteriores(Graphics2D g2, int ox, int oy, int c) {
        Map<Integer, Casilla> mapaCasillas = new HashMap<>();
        if (partida != null && partida.obtenerTablero() != null && partida.obtenerTablero().obtenerCasillas() != null) {
            for (Casilla casilla : partida.obtenerTablero().obtenerCasillas()) {
                mapaCasillas.put(casilla.obtenerNumero(), casilla);
            }
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, Math.max(9, c / 3)));

        for (int num = 1; num <= TableroService.TOTAL_CASILLAS_EXTERIOR; num++) {
            Point grid = coordenadasCasillas.get(num);
            if (grid == null) {
                continue;
            }
            int x = ox + grid.x * c;
            int y = oy + grid.y * c;

            Casilla casilla = mapaCasillas.get(num);
            Color relleno = COLOR_CASILLA_NORMAL;
            String marca = String.valueOf(num);

            if (casilla != null) {
                if (casilla.obtenerTipo() == TipoCasilla.SALIDA && casilla.obtenerColor() != null) {
                    relleno = colorPrincipal(casilla.obtenerColor());
                } else if (casilla.obtenerTipo() == TipoCasilla.SEGURO) {
                    relleno = COLOR_CASILLA_SEGURO;
                }
            } else {
                if (num == TableroService.SALIDA_ROJO) relleno = colorPrincipal(ColorJugador.ROJO);
                else if (num == TableroService.SALIDA_AZUL) relleno = colorPrincipal(ColorJugador.AZUL);
                else if (num == TableroService.SALIDA_VERDE) relleno = colorPrincipal(ColorJugador.VERDE);
                else if (num == TableroService.SALIDA_AMARILLO) relleno = colorPrincipal(ColorJugador.AMARILLO);
                else if (num == 8 || num == 13 || num == 25 || num == 30 || num == 42 || num == 47 || num == 59 || num == 64) {
                    relleno = COLOR_CASILLA_SEGURO;
                }
            }

            g2.setColor(relleno);
            g2.fillRect(x, y, c, c);
            g2.setColor(COLOR_BORDE_CASILLA);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x, y, c, c);

            boolean esSalida = (num == TableroService.SALIDA_ROJO || num == TableroService.SALIDA_AZUL
                    || num == TableroService.SALIDA_VERDE || num == TableroService.SALIDA_AMARILLO);
            boolean esSeguro = (relleno.equals(COLOR_CASILLA_SEGURO));

            Color colorTexto = esSalida
                    ? (num == TableroService.SALIDA_AMARILLO ? Color.DARK_GRAY : Color.WHITE)
                    : (esSeguro ? new Color(35, 85, 145) : new Color(110, 110, 110));
            g2.setColor(colorTexto);
            FontMetrics fm = g2.getFontMetrics();
            String texto = esSeguro ? "★" : marca;
            g2.drawString(texto, x + (c - fm.stringWidth(texto)) / 2, y + (c + fm.getAscent()) / 2 - 2);
        }
    }

    private void dibujarPasillosMeta(Graphics2D g2, int ox, int oy, int c) {
        for (Map.Entry<ColorJugador, List<Point>> entry : coordenadasPasillos.entrySet()) {
            ColorJugador color = entry.getKey();
            Color relleno = colorSuave(color);
            Color borde = colorPrincipal(color);

            for (Point grid : entry.getValue()) {
                int x = ox + grid.x * c;
                int y = oy + grid.y * c;
                g2.setColor(relleno);
                g2.fillRect(x, y, c, c);
                g2.setColor(borde);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRect(x, y, c, c);
            }
        }
    }

    private void dibujarCentroMeta(Graphics2D g2, int ox, int oy, int c) {
        int x = ox + 8 * c;
        int y = oy + 8 * c;
        int w = 3 * c;
        int cx = x + w / 2;
        int cy = y + w / 2;

        // Triángulos alineados con cada pasillo (ROJO arriba, AZUL izquierda, VERDE abajo, AMARILLO derecha)
        g2.setColor(colorPrincipal(ColorJugador.ROJO));
        g2.fillPolygon(new int[]{x, x + w, cx}, new int[]{y, y, cy}, 3);

        g2.setColor(colorPrincipal(ColorJugador.AZUL));
        g2.fillPolygon(new int[]{x, x, cx}, new int[]{y + w, y, cy}, 3);

        g2.setColor(colorPrincipal(ColorJugador.VERDE));
        g2.fillPolygon(new int[]{x + w, x, cx}, new int[]{y + w, y + w, cy}, 3);

        g2.setColor(colorPrincipal(ColorJugador.AMARILLO));
        g2.fillPolygon(new int[]{x + w, x + w, cx}, new int[]{y, y + w, cy}, 3);

        g2.setColor(new Color(50, 50, 50));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(x, y, w, w);

        g2.setColor(Color.WHITE);
        g2.fillOval(cx - c / 2, cy - c / 2, c, c);
        g2.setColor(new Color(40, 40, 40));
        g2.drawOval(cx - c / 2, cy - c / 2, c, c);

        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, c / 3)));
        FontMetrics fm = g2.getFontMetrics();
        String label = "META";
        g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + fm.getAscent() / 2 - 2);
    }

    private void dibujarFichas(Graphics2D g2, int ox, int oy, int c) {
        fichasEnPantalla.clear();
        if (partida == null || partida.obtenerJugadores() == null) {
            return;
        }

        Map<String, List<Ficha>> gruposPorCelda = new HashMap<>();
        List<Ficha> fichasEnCasa = new ArrayList<>();

        for (Jugador jugador : partida.obtenerJugadores()) {
            if (jugador.obtenerFichas() == null) {
                continue;
            }
            for (Ficha ficha : jugador.obtenerFichas()) {
                if (ficha.obtenerEstado() == EstadoFicha.CASA) {
                    fichasEnCasa.add(ficha);
                } else {
                    String clave = clavePosicion(ficha);
                    gruposPorCelda.computeIfAbsent(clave, k -> new ArrayList<>()).add(ficha);
                }
            }
        }

        for (Ficha ficha : fichasEnCasa) {
            Point centro = obtenerCentroCasa(ficha, ox, oy, c);
            dibujarFichaIndividual(g2, ficha, centro.x, centro.y, (int) (c * 0.85));
        }

        for (List<Ficha> grupo : gruposPorCelda.values()) {
            int total = grupo.size();
            for (int i = 0; i < total; i++) {
                Ficha ficha = grupo.get(i);
                Point base = obtenerCentroPosicion(ficha, ox, oy, c);
                int diametro = total == 1 ? (int) (c * 0.78) : (int) (c * 0.56);
                int dx = 0;
                int dy = 0;
                if (total > 1) {
                    int offset = Math.max(4, c / 4);
                    dx = (i % 2 == 0) ? -offset : offset;
                    dy = (i < 2) ? -offset : offset;
                }
                dibujarFichaIndividual(g2, ficha, base.x + dx, base.y + dy, diametro);
            }
        }
    }

    private void dibujarFichaIndividual(Graphics2D g2, Ficha ficha, int cx, int cy, int diametro) {
        int r = diametro / 2;
        boolean movible = fichasMovibles.contains(ficha);

        if (movible) {
            g2.setColor(new Color(255, 215, 0, 210));
            g2.setStroke(new BasicStroke(3.2f));
            g2.drawOval(cx - r - 4, cy - r - 4, diametro + 8, diametro + 8);
        }

        Ellipse2D.Double circulo = new Ellipse2D.Double(cx - r, cy - r, diametro, diametro);
        g2.setColor(colorPrincipal(ficha.obtenerColor()));
        g2.fill(circulo);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(circulo);

        String idTexto = ficha.obtenerId() != null ? String.valueOf(ficha.obtenerId()) : "";
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, diametro / 2)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(ficha.obtenerColor() == ColorJugador.AMARILLO ? Color.DARK_GRAY : Color.WHITE);
        g2.drawString(idTexto, cx - fm.stringWidth(idTexto) / 2, cy + fm.getAscent() / 2 - 2);

        fichasEnPantalla.add(new FichaDibujada(ficha, new Ellipse2D.Double(cx - r - 4, cy - r - 4, diametro + 8, diametro + 8)));
    }

    private Point obtenerCentroCasa(Ficha ficha, int ox, int oy, int c) {
        int baseCol;
        int baseRow;
        switch (ficha.obtenerColor()) {
            case ROJO -> {
                baseCol = 0;
                baseRow = 0;
            }
            case AZUL -> {
                baseCol = 0;
                baseRow = 11;
            }
            case VERDE -> {
                baseCol = 11;
                baseRow = 11;
            }
            case AMARILLO -> {
                baseCol = 11;
                baseRow = 0;
            }
            default -> {
                baseCol = 0;
                baseRow = 0;
            }
        }
        int idx = ficha.obtenerId() != null ? (int) ((ficha.obtenerId() - 1) % 4) : 0;
        double colOffset = (idx % 2 == 0) ? 2.7 : 5.3;
        double rowOffset = (idx < 2) ? 2.8 : 5.2;
        int cx = ox + (int) ((baseCol + colOffset) * c);
        int cy = oy + (int) ((baseRow + rowOffset) * c);
        return new Point(cx, cy);
    }

    private Point obtenerCentroPosicion(Ficha ficha, int ox, int oy, int c) {
        int pos = ficha.obtenerPosicion();
        if (ficha.obtenerEstado() == EstadoFicha.META || pos == TableroService.POSICION_META) {
            return switch (ficha.obtenerColor()) {
                case ROJO -> new Point(ox + (int) (9.5 * c), oy + (int) (8.6 * c));
                case AZUL -> new Point(ox + (int) (8.6 * c), oy + (int) (9.5 * c));
                case VERDE -> new Point(ox + (int) (9.5 * c), oy + (int) (10.4 * c));
                case AMARILLO -> new Point(ox + (int) (10.4 * c), oy + (int) (9.5 * c));
            };
        }
        if (pos >= TableroService.POSICION_INICIO_PASILLO && pos < TableroService.POSICION_META) {
            int pasoIdx = pos - TableroService.POSICION_INICIO_PASILLO;
            List<Point> pasillo = coordenadasPasillos.get(ficha.obtenerColor());
            if (pasillo != null && pasoIdx >= 0 && pasoIdx < pasillo.size()) {
                Point p = pasillo.get(pasoIdx);
                return new Point(ox + p.x * c + c / 2, oy + p.y * c + c / 2);
            }
        }
        Point grid = coordenadasCasillas.get(pos);
        if (grid != null) {
            return new Point(ox + grid.x * c + c / 2, oy + grid.y * c + c / 2);
        }
        return new Point(ox + 9 * c + c / 2, oy + 9 * c + c / 2);
    }

    private String clavePosicion(Ficha ficha) {
        if (ficha.obtenerEstado() == EstadoFicha.META || ficha.obtenerPosicion() >= TableroService.POSICION_INICIO_PASILLO) {
            return ficha.obtenerColor().name() + "-" + ficha.obtenerPosicion();
        }
        return "EXT-" + ficha.obtenerPosicion();
    }

    private void construirMapaCoordenadas() {
        // Recorrido en sentido ANTIHORARIO (derecha a izquierda) comenzando en la casilla 1 = SALIDA ROJO

        // 1..4: bajando por col 8 (rows 4..7) desde SALIDA ROJO (1)
        for (int i = 0; i < 4; i++) {
            coordenadasCasillas.put(1 + i, new Point(8, 4 + i));
        }
        // 5..12: derecha a izquierda por row 8 (cols 7..0) -> SEGURO en 8
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(5 + i, new Point(7 - i, 8));
        }
        // 13: extremo izquierdo (SEGURO y entrada pasillo AZUL)
        coordenadasCasillas.put(13, new Point(0, 9));

        // 14..21: izquierda a derecha por row 10 (cols 0..7) -> SALIDA AZUL en 18
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(14 + i, new Point(i, 10));
        }
        // 22..29: bajando por col 8 (rows 11..18) -> SEGURO en 25
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(22 + i, new Point(8, 11 + i));
        }
        // 30: extremo inferior (SEGURO y entrada pasillo VERDE)
        coordenadasCasillas.put(30, new Point(9, 18));

        // 31..38: subiendo por col 10 (rows 18..11) -> SALIDA VERDE en 35
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(31 + i, new Point(10, 18 - i));
        }
        // 39..46: izquierda a derecha por row 10 (cols 11..18) -> SEGURO en 42
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(39 + i, new Point(11 + i, 10));
        }
        // 47: extremo derecho (SEGURO y entrada pasillo AMARILLO)
        coordenadasCasillas.put(47, new Point(18, 9));

        // 48..55: derecha a izquierda por row 8 (cols 18..11) -> SALIDA AMARILLO en 52
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(48 + i, new Point(18 - i, 8));
        }
        // 56..63: subiendo por col 10 (rows 7..0) -> SEGURO en 59
        for (int i = 0; i < 8; i++) {
            coordenadasCasillas.put(56 + i, new Point(10, 7 - i));
        }
        // 64: extremo superior (SEGURO y entrada pasillo ROJO)
        coordenadasCasillas.put(64, new Point(9, 0));

        // 65..68: bajando por col 8 (rows 0..3) conectando con casilla 1 (SALIDA ROJO)
        for (int i = 0; i < 4; i++) {
            coordenadasCasillas.put(65 + i, new Point(8, i));
        }

        // Pasillos de llegada (7 casillas hacia el centro para cada color)
        List<Point> pasilloRojo = new ArrayList<>();
        for (int r = 1; r <= 7; r++) pasilloRojo.add(new Point(9, r));
        coordenadasPasillos.put(ColorJugador.ROJO, pasilloRojo);

        List<Point> pasilloAzul = new ArrayList<>();
        for (int col = 1; col <= 7; col++) pasilloAzul.add(new Point(col, 9));
        coordenadasPasillos.put(ColorJugador.AZUL, pasilloAzul);

        List<Point> pasilloVerde = new ArrayList<>();
        for (int r = 17; r >= 11; r--) pasilloVerde.add(new Point(9, r));
        coordenadasPasillos.put(ColorJugador.VERDE, pasilloVerde);

        List<Point> pasilloAmarillo = new ArrayList<>();
        for (int col = 17; col >= 11; col--) pasilloAmarillo.add(new Point(col, 9));
        coordenadasPasillos.put(ColorJugador.AMARILLO, pasilloAmarillo);
    }

    public static Color colorPrincipal(ColorJugador color) {
        if (color == null) {
            return Color.GRAY;
        }
        return switch (color) {
            case ROJO -> new Color(214, 58, 58);
            case AZUL -> new Color(46, 114, 210);
            case VERDE -> new Color(46, 160, 82);
            case AMARILLO -> new Color(235, 185, 35);
        };
    }

    public static Color colorSuave(ColorJugador color) {
        if (color == null) {
            return Color.LIGHT_GRAY;
        }
        return switch (color) {
            case ROJO -> new Color(252, 218, 218);
            case AZUL -> new Color(214, 232, 255);
            case VERDE -> new Color(214, 245, 224);
            case AMARILLO -> new Color(255, 246, 204);
        };
    }
}
