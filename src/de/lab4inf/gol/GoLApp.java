package de.lab4inf.gol;

import de.lab4inf.gui.GameOfLifeView;
import de.lab4inf.gui.SwingApp;
import javax.swing.*;

public class GoLApp extends SwingApp {
    private GameOfLifeModel model;
    private GameOfLifeView view;
    private volatile boolean shouldRun = false;
    private JMenuItem startMenuItem;
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;
        SwingApp app = new GoLApp();
        app.startUp();
    }

    @Override
    protected JComponent createContent() {
        int nRows = 10, mCols = 10, xPos = 3, yPos = 3;
        model = new GameOfLifeModel(nRows, mCols);
        boolean[][] pattern = Main.patternFromCommandLine(arguments);
        model.setPattern(xPos, yPos, pattern);
        view = new GameOfLifeView(model);
        return view;
    }

    @Override
    public void startUp() {
        getFrame().setSize(600, 600);
        super.startUp();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (shouldRun) {
                    model.nextGeneration();
                    view.repaint();
                }
            }
        }).start();
    }

    @Override
    protected JToolBar createToolBar() {
        return new JToolBar();
    }

    @Override
    protected JComponent createStatusBar(JTextField statusField) {
        return new JPanel();
    }

    @Override
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(evt -> {
            shouldRun = !shouldRun;
            startMenuItem.setText(shouldRun ? "Stop" : "Start");
        });
        fileMenu.add(startMenuItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(evt -> shutDown());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        int[] sizes = {10, 20, 30, 40};
        for (int sz : sizes) {
            JMenuItem item = new JMenuItem(sz + "x" + sz);
            item.setToolTipText("Set grid size to " + sz + " rows and columns");
            item.addActionListener(evt -> {
                model.setDimensions(sz, sz);
                view.repaint();
            });
            settingsMenu.add(item);
        }
        menuBar.add(settingsMenu);

        // Patterns menu
        JMenu patternsMenu = new JMenu("Patterns");
        for (String name : PatternFactory.getPatternNames()) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(evt -> {
                // clear the field
                model.setDimensions(model.rows(), model.columns());
                shouldRun = false;
                startMenuItem.setText("Start");
                // set selected pattern centered
                boolean[][] pat = PatternFactory.getPattern(name);
                int r = (model.rows() - pat.length) / 2;
                int c = (model.columns() - pat[0].length) / 2;
                model.setPattern(r, c, pat);
                view.repaint();
            });
            patternsMenu.add(item);
        }
        menuBar.add(patternsMenu);

        return menuBar;
    }
}