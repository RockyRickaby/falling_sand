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

public class Canvas extends JFrame {
    private static final int SCALE = 5,
                             DELAY = 18;

    private static int cursorX = 0, cursorY = 0,
                       brushSize = 1;

    private static Canvas instance = null;
    private SandSimul subCanvas;
    private Timer timer;
    private boolean isRunning;

    private Canvas() {
        subCanvas = new SandSimul();
        isRunning = false;
        timer = new Timer(DELAY, e -> {
            if (isRunning) {
                subCanvas.tick();
                repaint();
            }
            if (subCanvas.isCurrentlyStatic()) {
                isRunning = false;
            }
        });

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

    public static Canvas getInstance() {
        if (instance == null) {
            instance = new Canvas();
        }
        return instance;
    }

    private void reset() {
        subCanvas.reset();
        repaint();
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);
        menuBar.add(menu);

        menu = new JMenu("Game");
        item = new JMenuItem("Change Brush Size");
        item.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(String.format("Type in the desired brush size (min = 1, max = 10)\nCurrent brush size: %d", brushSize));
            if (val == null || val.isEmpty() || val.matches("[a-zA-Z. ]+")) {
                JOptionPane.showMessageDialog(this, "Invalid input");
                return;
            }
            int res = Integer.valueOf(val);
            if (res <= 10) {
                brushSize = res <= 0 ? 1 : res;
                return;
            }
            brushSize = res % 10;
        });
        menu.add(item);

        item = new JMenuItem("Restart");
        item.addActionListener(e -> reset());
        menu.add(item);
        menuBar.add(menu);

        return menuBar;
    }

    private JPanel generateCanvas() {
        JPanel canvas = new JPanel() {
            public void paintComponent(Graphics g) {
                int rows = subCanvas.getRows(),
                    cols = subCanvas.getCols();

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        float state = subCanvas.state(i, j);
                        g.setColor(state == 0 ? Color.BLACK : Color.getHSBColor(state, 1, 1));
                        g.fillRect(i * SCALE, j * SCALE, SCALE, SCALE);
                    }
                }
                g.setColor(Color.WHITE);
                g.fillRect(cursorX * SCALE, cursorY * SCALE, SCALE, SCALE);
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
            @Override
            public void mouseMoved(MouseEvent e) {
                cursorX = e.getX() / SCALE;
                cursorY = e.getY() / SCALE;
                repaint();
            }

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