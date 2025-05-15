package de.lab4inf.gui;

import de.lab4inf.gol.GameOfLifeModel;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;

public class GameOfLifeView extends JComponent {
    private final GameOfLifeModel model;

    public GameOfLifeView(GameOfLifeModel model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int rows = model.rows();
        int cols = model.columns();

        int totalW = getWidth();
        int totalH = getHeight();

        for (int i = 0; i < rows; i++) {
            int y  = i * totalH / rows;
            int h  = (i+1) * totalH / rows - y;

            for (int j = 0; j < cols; j++) {
                int x = j * totalW / cols;
                int w = (j+1) * totalW / cols - x;

                // Gitter
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, w, h);

                // Lebende Zelle: leicht eingezogenes Oval
                if (model.get(i, j)) {
                    g.setColor(Color.BLUE);
                    g.fillOval(
                            x + 2, y + 2,
                            Math.max(0, w - 4),
                            Math.max(0, h - 4)
                    );
                }
            }
        }
    }
}
