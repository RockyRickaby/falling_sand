import java.util.Random;

/**
 * This class is responsible for emulatiing the behavior of 
 * falling sand (albeit in a very basic manner). Each grain of 
 * sand has its own falling velocity, which updates every tick.
 * Each sand grain has an acceleration of exactly 1 pixel per
 * tick squared.
 */
public class SandSimul {
    /**
     * Private auxiliary class. Serves only to store
     * the hue values of each SandGrain and their respective
     * falling velocities.
     */
    private static class SandGrain {
        static final int MAX_VELOCITY = 11;
        int velocity;
        float hue_value;

        /**
         * Creates a new SandGrain which holds the value
         * in {@code hue_value}.
         * @param hue_value
         */
        public SandGrain(float hue_value) {
            this.hue_value = hue_value;
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
         * Resets velocity and the SandGrain's {@code hue_value}.
         */
        public void resetPoint() {
            this.resetVelocity();
            this.hue_value = 0;
        }
    }

    private static final float POINT_VALUE_INCREMENT = .0001F;
    private static final int[] DIRS = {-1, 1};

    // private float[][] simulation;
    private SandGrain[][] simul;
    private int rows, cols, updated;
    private float hue_value;

    /**
     * Creates a new empty Falling Sand Simulator.
     */
    public SandSimul() {
        rows = 125;
        cols = 125;
        // simulation = new float[rows][cols];
        simul = new SandGrain[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // simulation[i][j] = 0;
                simul[i][j] = new SandGrain(0);
            }
        }
        updated = 0;
        hue_value = POINT_VALUE_INCREMENT;
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
     * of sand or if the given cell was already one.
     */
    public boolean setOn(int i, int j) {
        updated = 1;
        if (!validIndex(i, j)) {
            return false;
        }
        // if (simulation[i][j] > 0) {
        //     return true;
        // }
        if (simul[i][j].hue_value > 0) {
            return true;
        }
        // simulation[i][j] = hue_value;
        simul[i][j].hue_value = hue_value;
        hue_value = hue_value >= 1 ? 0 : hue_value + POINT_VALUE_INCREMENT;
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

    //             int dir = DIRS[r.nextInt(2)];
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
                SandGrain above = simul[i][j - 1],
                          side = null;

                int dir = DIRS[r.nextInt(2)];
                if (validIndex(i + dir, j)) {
                    side = simul[i + dir][j];
                }

                if (above.hue_value > 0) {
                    int idx = j - 1 + above.velocity;
                    while ((!validIndex(i, idx) || simul[i][idx].hue_value > 0) && idx != j - 1) {
                        idx--;
                    }
                    if (idx == j - 1) {
                        //above.resetAccel();
                        if (side != null && side.hue_value == 0) {
                            side.hue_value = above.hue_value;
                            above.hue_value = 0;
                            updated = 1;
                        } else if (validIndex(i - dir, j) && simul[i - dir][j].hue_value == 0) {
                            simul[i - dir][j].hue_value = above.hue_value;
                            above.hue_value = 0;
                            updated = 1;
                        }
                        continue;
                    }
                    SandGrain curr = simul[i][idx];
                    curr.hue_value = above.hue_value;
                    curr.velocity = above.velocity;
                    curr.increaseVelocity();
                    above.hue_value = 0;
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
     * Returns the hue_value of the SandGrain at the given matrix index.
     * @param i
     * @param j
     * @return the hue_value of the SandGrain ([0, 1]).
     */
    public float state(int i, int j) {
        if (!validIndex(i, j)) {
            return -1;
        }
        return simul[i][j].hue_value;
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
    public void clear() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // simulation[i][j] = 0;
                simul[i][j].resetPoint();
            }
        }
    }

    /**
     * Prints the Simulator's grid on the terminal.
     * The printes values correspond to the SandGrain's hue_value.
     */
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(simul[i][j].hue_value + " ");
            }
            System.out.println();
        }
    }
}
