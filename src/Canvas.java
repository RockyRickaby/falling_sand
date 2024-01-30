import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The Canvas class serves mostly a single purpose: to let the user
 * paint some pixels on the screen. Other of its purposes is to serve
 * as a way to render the Falling Sand Simulator.
 */
public class Canvas extends JFrame {
    private static final int SCALE = 5,
                             DELAY = 18;

    private static Canvas instance = null;
    private static int cursorX = 0, cursorY = 0,
                       brushSize = 1;

    private SandSimul subCanvas;
    private Timer timer;
    private boolean isRunning;

    /**
     * Creates a new empty Canvas.
     */
    private Canvas() {
        isRunning = false;
        subCanvas = new SandSimul();
        timer = new Timer(DELAY, e -> update());

        this.setBackground(Color.black);
        this.setTitle("Falling Sand");
        this.add(generateCanvas());
        this.setJMenuBar(generateMenuBar());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        timer.start();
    }

    /**
     * Returns the current instance of Canvas.
     * @return
     */
    public static Canvas getInstance() {
        if (instance == null) {
            instance = new Canvas();
        }
        return instance;
    }

    /**
     * Clears this Canvas and the Subcanvas.
     */
    private void reset() {
        isRunning = false;
        subCanvas.reset();
        repaint();
    }

    /**
     * Updates the state of this Canvas and the Subcanvas.
     */
    private void update() {
        if (isRunning) {
            subCanvas.tick();
            repaint();
        }
        if (subCanvas.isCurrentlyStatic()) {
            isRunning = false;
        }
    }

    /**
     * Shows a popup asking for a new brush size.
     * <p>
     * Min.: 1, Max.: 10.
     */
    private void askForBrushSize() {
        String val = JOptionPane.showInputDialog(String.format("Type in the desired brush size (min = 1, max = 10)\nCurrent brush size: %d", brushSize));
        if (val == null) {
            return;
        }
        if (val.isEmpty() || val.matches("[a-zA-Z. ]+")) {
            JOptionPane.showMessageDialog(this, "Invalid input");
            return;
        }
        int res = Integer.valueOf(val);
        if (res >= 10) {
            res = 10;
        } else if (res <= 0) {
            res = 1;
        }
        brushSize = res;
    }

    /**
     * Generates this Frame's menu bar.
     * @return the generated menu bar.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);
        menuBar.add(menu);

        menu = new JMenu("Options");
        item = new JMenuItem("Change Brush Size");
        item.addActionListener(e -> askForBrushSize());
        menu.add(item);

        item = new JMenuItem("Restart");
        item.addActionListener(e -> reset());
        menu.add(item);
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * Generates this Frame's canvas.
     * @return the generated canvas.
     */
    private JPanel generateCanvas() {
        JPanel canvas = new JPanel() {
            public void paintComponent(Graphics g) {
                int rows = subCanvas.getRows(),
                    cols = subCanvas.getCols();

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        float state = subCanvas.state(i, j);
                        g.setColor(state == 0 ? Color.BLACK : Color.getHSBColor(state, .80F, 1));
                        g.fillRect(i * SCALE, j * SCALE, SCALE, SCALE);
                    }
                }
                // g.setColor(Color.WHITE);
                // g.fillRect(cursorX * SCALE, cursorY * SCALE, SCALE, SCALE);
            }
        };
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cursorX = e.getX() / SCALE;
                cursorY = e.getY() / SCALE;
                subCanvas.setRegionOn(cursorX, cursorY, brushSize);
                isRunning = true;
                repaint();
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            // @Override
            // public void mouseMoved(MouseEvent e) {
            //     cursorX = e.getX() / SCALE;
            //     cursorY = e.getY() / SCALE;
            //     repaint();
            // }

            @Override
            public void mouseDragged(MouseEvent e) {
                cursorX = e.getX() / SCALE;
                cursorY = e.getY() / SCALE;
                subCanvas.setRegionOn(cursorX, cursorY, brushSize);
                isRunning = true;
                repaint();
            }
        });
        canvas.setPreferredSize(new Dimension(subCanvas.getRows() * SCALE, subCanvas.getCols() * SCALE));
        return canvas;
    }
}