package de.lab4inf.gol;

import de.lab4inf.gui.GameOfLifeView;
import de.lab4inf.gui.SwingApp;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class GoLApp extends SwingApp {
    private GameOfLifeModel model;
    private GameOfLifeView view;
    private final int maxGeneration = 81;
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;
        SwingApp app = new GoLApp();
        app.startUp();
    }

    @Override
    protected JComponent createContent() {
        int nRows = 9, mCols = 9, xPos = 3, yPos = 3;
        model = new GameOfLifeModel(nRows, mCols);
        boolean[][] pattern = Main.patternFromCommandLine(arguments);
        model.setPattern(xPos, yPos, pattern);
        view = new GameOfLifeView(model);
        return view;
    }

    @Override
    public void startUp() {
        getFrame().pack();
        getFrame().setResizable(false);
        super.startUp();
        new Thread(() -> {
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                model.nextGeneration();
                view.repaint();
            } while (model.isAlive() && model.getGeneration() < maxGeneration);
        }).start();
    }

    @Override
    protected JToolBar createToolBar() {
        return new JToolBar(); // optional leere Toolbar
    }

    @Override
    protected JComponent createStatusBar(JTextField statusField) {
        return new JPanel(); // optional leere Statusbar
    }

    @Override
    protected JMenuBar createMenuBar() {
        return new JMenuBar(); // optional leere Menüleiste
    }
}
