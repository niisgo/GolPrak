package de.lab4inf.gol;

import de.lab4inf.gui.GameOfLifeView;
import de.lab4inf.gui.SwingApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GoLApp extends SwingApp implements GameOfLifeListener {
    private GameOfLifeModel model;
    private GameOfLifeView view;
    private volatile boolean shouldRun = false;
    private JButton startButton;
    private JMenuItem startMenuItem;
    private JLabel sizeLabel;
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

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int cols = model.columns();
                int rows = model.rows();
                int cellWidth = view.getWidth() / cols;
                int cellHeight = view.getHeight() / rows;
                int col = e.getX() / cellWidth;
                int row = e.getY() / cellHeight;
                if (row >= 0 && row < rows && col >= 0 && col < cols) {
                    boolean current = model.get(row, col);
                    model.set(row, col, !current);
                    view.repaint();
                }
            }
        });

        return view;
    }
    @Override
    public void startUp() {
        getFrame().setSize(600, 600);
        model.addObserver(this);
        model.addObserver(view);
        super.startUp();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (shouldRun && model.isAlive()) {
                    model.nextGeneration();
                }
            }
        }).start();
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        startButton = new JButton("Start");
        startButton.addActionListener(evt -> {
            shouldRun = !shouldRun;
            String text = shouldRun ? "Stop" : "Start";
            startButton.setText(text);
            if (startMenuItem != null) {
                startMenuItem.setText(text);
            }
        });

        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(e -> {
            if (model.isAlive()) {
                model.nextGeneration();
            }
        });

        toolBar.add(startButton);
        toolBar.add(stepButton);

        return toolBar;
    }

    @Override
    protected JComponent createStatusBar(JTextField statusField) {
        JPanel statusBar = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));

        statusField.setEditable(false);
        statusField.setBorder(BorderFactory.createEtchedBorder());
        statusField.setForeground(Color.BLUE);
        statusField.setPreferredSize(new Dimension(250, 20));

        leftPanel.add(statusLabel);
        leftPanel.add(statusField);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel openLabel = new JLabel("Open");
        openLabel.setForeground(Color.BLUE);
        sizeLabel = new JLabel("10x10");

        rightPanel.add(openLabel);
        rightPanel.add(sizeLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        return statusBar;
    }

    @Override
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(evt -> {
            shouldRun = !shouldRun;
            String text = shouldRun ? "Stop" : "Start";
            startMenuItem.setText(text);
            if (startButton != null) {
                startButton.setText(text);
            }
        });
        fileMenu.add(startMenuItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(evt -> shutDown());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu settingsMenu = new JMenu("Settings");
        int[] sizes = {10, 20, 30, 40, 50};
        for (int sz : sizes) {
            JMenuItem item = new JMenuItem(sz + "x" + sz);
            item.setToolTipText("Set grid size to " + sz + " rows and columns");
            item.addActionListener(evt -> {
                model.setDimensions(sz, sz);
                model.notifyDimensionChanged();
            });
            settingsMenu.add(item);
        }
        menuBar.add(settingsMenu);

        JMenu patternsMenu = new JMenu("Patterns");
        for (String name : PatternFactory.getPatternNames()) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(evt -> {
                shouldRun = false;
                startMenuItem.setText("Start");
                if (startButton != null) {
                    startButton.setText("Start");
                }
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

    @Override
    public void generationChanged() {
        setStatusMsg("current generation: " + model.getGeneration());
    }

    @Override
    public void dimensionChanged() {
        sizeLabel.setText(view.model.rows() + "x" + view.model.rows());
    }
}
