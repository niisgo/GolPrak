package de.lab4inf.gol;

import de.lab4inf.gui.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        int nRows = 10;
        int mCols = 10;
        int xPos = 3;
        int yPos = 3;

        model = new GameOfLifeModel(nRows, mCols);
        boolean[][] pattern = Main.patternFromCommandLine(arguments);
        model.setPattern(xPos, yPos, pattern);

        view = new GameOfLifeView(model);
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleGridClick(e);
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

        setupHotkey();
        startGameLoop();
    }

    private void handleGridClick(MouseEvent e) {
        int cols = model.columns();
        int rows = model.rows();
        int totalW = view.getWidth();
        int totalH = view.getHeight();

        // Berechne Spalte/Zelle proportional zur aktuellen Komponentengröße
        int col = e.getX() * cols / totalW;
        int row = e.getY() * rows / totalH;

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            boolean current = model.get(row, col);
            model.set(row, col, !current);
            view.repaint();
        }
    }

    private void setupHotkey() {
        JRootPane rp = getFrame().getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();

        im.put(KeyStroke.getKeyStroke("F12"), "easter");
        am.put("easter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MinorUpdate.trigger();
            }
        });
    }

    private void startGameLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
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
        startButton.addActionListener(evt -> toggleSimulation());

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

    private void toggleSimulation() {
        shouldRun = !shouldRun;
        String text = shouldRun ? "Stop" : "Start";
        startButton.setText(text);
        if (startMenuItem != null) {
            startMenuItem.setText(text);
        }
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

        menuBar.add(createFileMenu());
        menuBar.add(createSettingsMenu());
        menuBar.add(createPatternsMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(evt -> toggleSimulation());
        fileMenu.add(startMenuItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(evt -> shutDown());
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createSettingsMenu() {
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

        // Freifeld für eigene dimensions
        JMenuItem customItem = new JMenuItem("Custom...");
        customItem.setToolTipText("Enter custom rows and columns");
        customItem.addActionListener(evt -> {
            JTextField rowsField = new JTextField();
            JTextField colsField = new JTextField();
            Object[] message = {
                    "Rows:", rowsField,
                    "Columns:", colsField
            };
            int option = JOptionPane.showConfirmDialog(getFrame(), message,
                    "Custom Grid Size", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int customRows = Integer.parseInt(rowsField.getText().trim());
                    int customCols = Integer.parseInt(colsField.getText().trim());
                    model.setDimensions(customRows, customCols);
                    model.notifyDimensionChanged();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(getFrame(),
                            "Bitte gültige positive ganze Zahlen eingeben.",
                            "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        settingsMenu.addSeparator();
        settingsMenu.add(customItem);

        return settingsMenu;
    }

    private JMenu createPatternsMenu() {
        JMenu patternsMenu = new JMenu("Patterns");
        for (String name : PatternFactory.getPatternNames()) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(evt -> applyPattern(name));
            patternsMenu.add(item);
        }
        return patternsMenu;
    }

    private void applyPattern(String name) {
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
    }

    @Override
    public void generationChanged() {
        setStatusMsg("current generation: " + model.getGeneration());
    }

    @Override
    public void dimensionChanged() {
        sizeLabel.setText(model.rows() + "x" + model.columns());
    }
}
