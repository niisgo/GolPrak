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

        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellWidth;
                int y = i * cellHeight;

                // Gitter zeichnen
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, cellWidth, cellHeight);

                // Lebende Zelle: Rechteck mit leichtem Abstand
                if (model.get(i, j)) {
                    g.setColor(Color.BLUE);
                    g.fillOval(
                            x + 2, y + 2,
                            Math.max(0, cellWidth - 4),
                            Math.max(0, cellHeight - 4)
                    );
                }
            }
        }
    }
}
