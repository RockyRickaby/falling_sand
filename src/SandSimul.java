import java.util.Random;

/**
 * This class is responsible for emulatiing the behavior of 
 * falling sand (albeit in a very basic manner). Each grain of 
 * sand has its own downwards velocity, which updates every tick.
 */
public class SandSimul {
    /**
     * Private auxiliary class. Serves only to store
     * the hue values of each point and their respective
     * falling velocities.
     */
    private static class Point {
        private static final int MAX_VELOCITY = 10;
        private int velocity;
        private float value;

        /**
         * Creates a new Point which holds the value
         * in {@code value}.
         * @param value
         */
        public Point(float value) {
            this.value = value;
            this.velocity = 1;
        }

        /**
         * Increments the current velocity by one.
         */
        public void increaseVelocity() {
            this.velocity += 1;
            this.velocity = velocity >= MAX_VELOCITY ? MAX_VELOCITY : velocity;
        }

        /**
         * Puts the velocity back to one.
         */
        public void resetVelocity() {
            this.velocity = 1;
        }

        /**
         * Resets velocity and the Point's {@code value}.
         */
        public void resetPoint() {
            this.resetVelocity();
            this.value = 0;
        }
    }

    private static final float INCR = .0001F;
    private static final int[] dirs = {-1, 1};

    // private float[][] simulation;
    private Point[][] simul;
    private int rows, cols, updated;
    private float hue;

    /**
     * Creates a new empty Falling Sand Simulator.
     */
    public SandSimul() {
        rows = 125;
        cols = 125;
        // simulation = new float[rows][cols];
        simul = new Point[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // simulation[i][j] = 0;
                simul[i][j] = new Point(0);
            }
        }
        updated = 0;
        hue = INCR;
    }

    /**
     * Checks if a given matrix index is valid.
     * @param i
     * @param j
     * @return {@code true} if it is.
     */
    private boolean validIndex(int i, int j) {
        return (i >= 0 && i < rows) && (j >= 0 && j < cols);
    }

    /**
     * Sets the cell at the given index as
     * a grain of sand.
     * @param i
     * @param j
     * @return {@code true} if it was possible to generate the grain
     * of sand or it the given cell was already one.
     */
    public boolean setOn(int i, int j) {
        updated = 1;
        if (!validIndex(i, j)) {
            return false;
        }
        // if (simulation[i][j] > 0) {
        //     return true;
        // }
        if (simul[i][j].value > 0) {
            return true;
        }
        // simulation[i][j] = hue;
        simul[i][j].value = hue;
        hue = hue >= 1 ? 0 : hue + INCR;
        return true;
    }

    /**
     * Sets the region around the given matrix index (and
     * the point itself) as grains of sand.
     * @param i
     * @param j
     * @param size the size of the region.
     */
    public void setRegionOn(int i, int j, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size of region must be positive.");
        }
        int extent = size / 2;
        if (size % 2 == 1) {
            for (int k = -extent; k <= extent; k++) {
                for (int l = -extent; l <= extent; l++) {
                    setOn(k + i, l + j);
                }
            }
            return;
        }
        for (int k = -extent; k < extent; k++) {
            for (int l = -extent; l < extent; l++) {
                setOn(k + i, l + j);
            }
        }
    }

    // public void tick() {
    //     updated = 0;
    //     Random r = new Random();
    //     for (int i = rows - 1; i >= 0; i--) {
    //         for (int j = cols - 1; j > 0; j--) {
    //             float current = simulation[i][j],
    //                   above = simulation[i][j - 1],
    //                   side = -1;

    //             int dir = dirs[r.nextInt(2)];
    //             if (validIndex(i + dir, j)) {
    //                 side = simulation[i + dir][j];
    //             }

    //             if (current == 0 && above > 0) {
    //                 simulation[i][j] = above;
    //                 simulation[i][j - 1] = 0;
    //                 updated = 1;
    //             } else if (side == 0 && above > 0) {
    //                 simulation[i + dir][j] = above;
    //                 simulation[i][j - 1] = 0;
    //                 updated = 1;
    //             } else if (validIndex(i - dir, j) && simulation[i - dir][j] == 0 && above > 0) {
    //                 simulation[i - dir][j] = above;
    //                 simulation[i][j - 1] = 0;
    //                 updated = 1;
    //             }
    //         }
    //     }
    // }

    /**
     * Updates the current state of the Simulator once.
     */
    public void tick() {
        updated = 0;
        Random r = new Random();
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j > 0; j--) {
                Point above = simul[i][j - 1],
                      side = null;

                int dir = dirs[r.nextInt(2)];
                if (validIndex(i + dir, j)) {
                    side = simul[i + dir][j];
                }

                if (above.value > 0) {
                    int velocity = above.velocity;
                    int idx = j - 1 + velocity;
                    while ((!(validIndex(i, idx)) || simul[i][idx].value > 0) && idx != j - 1) {
                        idx--;
                    }
                    if (idx == j - 1) {
                        //above.resetAccel();
                        if (side != null && side.value == 0) {
                            side.value = above.value;
                            above.value = 0;
                            updated = 1;
                        } else if (validIndex(i - dir, j) && simul[i - dir][j].value == 0) {
                            simul[i - dir][j].value = above.value;
                            above.value = 0;
                            updated = 1;
                        }
                        continue;
                    }
                    simul[i][idx].value = above.value;
                    simul[i][idx].velocity = above.velocity;
                    simul[i][idx].increaseVelocity();
                    above.value = 0;
                    above.resetVelocity();
                    updated = 1;
                }
            }
        }
    }

    /**
     * Checks if the Simulator is currently static (if it has had
     * no changes since the last tick).
     * @return {@code true} if it is.
     */
    public boolean isCurrentlyStatic() { return updated == 0; }
    // public float state(int i, int j) {
    //     if (!validIndex(i, j)) {
    //         return -1;
    //     }
    //     return simulation[i][j];
    // }

    /**
     * Returns the value of the Point at the given matrix index.
     * @param i
     * @param j
     * @return the value of the Point ([0, 1]).
     */
    public float state(int i, int j) {
        if (!validIndex(i, j)) {
            return -1;
        }
        return simul[i][j].value;
    }
    /**
     * Returns the number of rows of this Simulator.
     * @return
     */
    public int getRows() { return this.rows; }
    /**
     * Returns the number of columns of this Simulator.
     * @return
     */
    public int getCols() { return this.cols; }

    /**
     * Resets this Simulator. All the Points will have
     * their values set to zero.
     */
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // simulation[i][j] = 0;
                simul[i][j].resetPoint();
            }
        }
    }

    /**
     * Prints the Simulator's grid on the terminal.
     * The printes values correspond to the Point's value.
     */
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(simul[i][j].value + " ");
            }
            System.out.println();
        }
    }
}
