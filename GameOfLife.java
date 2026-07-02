import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The GameOfLife class serves as the GUI controller for the Conway's Game of Life simulation.
 * <p>
 * It handles all user interactions, including button controls, grid drawing, pattern loading,
 * and displaying simulation statistics such as the average age, alive cell count, and generation number.
 * </p>
 *
 * <p><strong>Extra Credit:</strong> This version includes a safe color gradient feature
 * that visually changes cell color based on age — older cells appear redder.</p>
 */
public class GameOfLife extends JFrame {

    /** The Simulation object that maintains all game logic and grid data. */
    private Simulation simulation;

    /** Swing timer used to control automatic evolution speed. */
    private Timer timer;

    /** The main panel that holds all GUI components. */
    private JPanel mainPanel;

    /** Control buttons for simulation actions. */
    private JButton startButton, pauseButton, resetButton, stepButton, loadRleButton;

    /** Buttons to choose grid sizes. */
    private JButton size25Button, size50Button, size100Button, size175Button;

    /** Slider to adjust simulation speed. */
    private JSlider speedSlider;

    /** Labels to display simulation statistics. */
    private JLabel averageAgeLabel, aliveCellsLabel, maxAgeLabel, generationLabel;

    /** Panels for displaying grid, statistics, and controls. */
    private JPanel gridPanel, statsPanel, controlPanel, combinedPanel;

    /** The current size of each cell (in pixels). */
    private int cellSize = 10;

    /**
     * Constructs the Game of Life window and initializes all GUI components.
     */
    public GameOfLife() {
        setTitle("GMU CS 310 - Project 1 - Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 7));

        initializeTitle();
        initializeControls();
        initializeStatsPanel();
        initializeGridPanel();
        combinePanels();
        initializeTimer();

        this.simulation = new Simulation(50, 50);
        updateStatistics();

        add(mainPanel);
        pack();
        setVisible(true);
        gridPanel.repaint();
    }

    /**
     * Initializes the window title at the top of the GUI.
     */
    private void initializeTitle() {
        JLabel titleLabel = new JLabel("GMU CS 310 - Project 1 - Game of Life", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
    }

    /**
     * Initializes control buttons, slider, and size options.
     */
    private void initializeControls() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");
        stepButton = new JButton("Step");
        loadRleButton = new JButton("Load RLE");

        size25Button = new JButton("25x25");
        size50Button = new JButton("50x50");
        size100Button = new JButton("100x100");
        size175Button = new JButton("175x175");

        startButton.addActionListener(e -> timer.start());
        pauseButton.addActionListener(e -> timer.stop());
        resetButton.addActionListener(e -> resetGrid());
        stepButton.addActionListener(e -> stepGeneration());
        loadRleButton.addActionListener(e -> loadRleFile());
        size25Button.addActionListener(e -> setGridAndCellSize(25, 20));
        size50Button.addActionListener(e -> setGridAndCellSize(50, 10));
        size100Button.addActionListener(e -> setGridAndCellSize(100, 5));
        size175Button.addActionListener(e -> setGridAndCellSize(175, 3));

        speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 600, 100);
        speedSlider.setPreferredSize(new Dimension(50, 40));
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setToolTipText("Adjust Simulation Speed");
        speedSlider.addChangeListener(e -> timer.setDelay(speedSlider.getValue()));

        controlPanel.add(startButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(pauseButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(resetButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(stepButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(loadRleButton);
        controlPanel.add(Box.createVerticalStrut(10));

        controlPanel.add(new JLabel("Choose a Grid Size:"));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(size25Button);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(size50Button);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(size100Button);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(size175Button);
        controlPanel.add(Box.createVerticalStrut(10));

        controlPanel.add(new JLabel("Speed (Fast <-> Slow):"));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(speedSlider);
    }

    /**
     * Initializes the statistics display panel that shows
     * average age, alive cells, maximum age, and generation number.
     */
    private void initializeStatsPanel() {
        statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        averageAgeLabel = new JLabel("Average Age: 0", SwingConstants.CENTER);
        aliveCellsLabel = new JLabel("Alive Cells: 0", SwingConstants.CENTER);
        maxAgeLabel = new JLabel("Max Age: 0", SwingConstants.CENTER);
        generationLabel = new JLabel("Generations: 0", SwingConstants.CENTER);

        statsPanel.add(averageAgeLabel);
        statsPanel.add(aliveCellsLabel);
        statsPanel.add(maxAgeLabel);
        statsPanel.add(generationLabel);
    }

    /**
     * Combines control and stats panels into the right-side container.
     */
    private void combinePanels() {
        combinedPanel = new JPanel();
        combinedPanel.setLayout(new BoxLayout(combinedPanel, BoxLayout.Y_AXIS));
        combinedPanel.add(controlPanel);
        combinedPanel.add(Box.createVerticalStrut(10));
        combinedPanel.add(statsPanel);
        mainPanel.add(combinedPanel, BorderLayout.EAST);
    }

    /**
     * Initializes the central grid panel where cells are drawn and interacted with.
     */
    private void initializeGridPanel() {
        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        gridPanel.setPreferredSize(new Dimension(550, 550));

        gridPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                int col = evt.getX() / cellSize;
                int row = evt.getY() / cellSize;
                simulation.toggleCell(row, col);
                gridPanel.repaint();
            }
        });

        gridPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                int col = evt.getX() / cellSize;
                int row = evt.getY() / cellSize;
                simulation.toggleCell(row, col);
                gridPanel.repaint();
            }
        });

        mainPanel.add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * Initializes the Swing timer used for automatic simulation progression.
     */
    private void initializeTimer() {
        timer = new Timer(100, e -> {
            simulation.evolve();
            updateStatistics();
            gridPanel.repaint();
        });
    }

    /**
     * Draws the grid and alive/dead cells with a color gradient based on cell age.
     *
     * @param g the Graphics object used for rendering
     */
    private void drawGrid(Graphics g) {
        if (simulation == null) {
            return;
        }
        DynamicArray<DynamicArray<Cell>> grid = simulation.getGrid();
        int maxAge = Math.max(1, simulation.getMaxAge());

        for (int row = 0; row < simulation.getRows(); row++) {
            for (int col = 0; col < simulation.getCols(); col++) {
                Cell cell = grid.get(row).get(col);
                if (cell.isAlive()) {
                    int age = cell.getAge();
                    float ratio = (float) (age - 1) / (float) (maxAge - 1);
                    ratio = Math.max(0, Math.min(ratio, 1));
                    int r = (int) (ratio * 255);
                    int gcol = 255 - r;
                    Color color = new Color(r, gcol, 0);
                    g.setColor(color);
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }

    /**
     * Resets the simulation grid to all dead cells.
     */
    private void resetGrid() {
        timer.stop();
        simulation.reset();
        updateStatistics();
        gridPanel.repaint();
    }

    /**
     * Advances the simulation by one generation.
     */
    private void stepGeneration() {
        simulation.evolve();
        updateStatistics();
        gridPanel.repaint();
    }

    /**
     * Updates the statistics labels with the latest simulation data.
     */
    private void updateStatistics() {
        averageAgeLabel.setText(String.format("Average Age: %.2f", simulation.getAverageAge()));
        aliveCellsLabel.setText("Alive Cells: " + simulation.getAliveCells());
        maxAgeLabel.setText("Max Age: " + simulation.getMaxAge());
        generationLabel.setText("Generations: " + simulation.getGenerations());
    }

    /**
     * Adjusts grid dimensions and resets simulation when a new size is chosen.
     *
     * @param newSize     the new number of rows and columns
     * @param newCellSize the new pixel size for each cell
     */
    private void setGridAndCellSize(int newSize, int newCellSize) {
        timer.stop();
        simulation = new Simulation(newSize, newSize);
        this.cellSize = newCellSize;
        updateStatistics();
        gridPanel.repaint();
    }

    /**
     * Opens a file chooser to load a Run-Length Encoded (.rle) pattern file.
     */
    private void loadRleFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                DynamicArray<String> lines = new DynamicArray<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("#")) {
                            lines.add(line);
                        }
                    }
                    simulation.parseRle(lines);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load RLE file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        gridPanel.repaint();
    }

    /**
     * The main entry point for running the Game of Life application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameOfLife::new);
    }
}
