package de.lab4inf.gui;

import de.lab4inf.gol.GameOfLifeModel;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class GameOfLifeView extends JComponent {
    private final GameOfLifeModel model;
    private final int cellSize = 30;

    public GameOfLifeView(GameOfLifeModel model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int rows = model.rows();
        int cols = model.columns();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellSize;
                int y = i * cellSize;

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, cellSize, cellSize);

                if (model.get(i, j)) {
                    g.setColor(Color.BLUE);
                    g.fillOval(x + 2, y + 2, cellSize - 4, cellSize - 4);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(model.columns() * cellSize, model.rows() * cellSize);
    }
}
