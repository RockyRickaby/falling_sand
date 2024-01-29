import java.util.Random;

public class SandSimul {
    private float[][] simulation;
    private int[] dirs = {-1, 1};
    private int rows, cols, updated;
    private float hue;

    public SandSimul() {
        rows = 125;
        cols = 125;
        simulation = new float[rows][cols];
        updated = 1;
        hue = .0001F;
    }

    public boolean validIndex(int i, int j) {
        return (i >= 0 && i < rows) && (j >= 0 && j < cols);
    }

    public boolean setOn(int i, int j) {
        if (!validIndex(i, j)) {
            return false;
        }
        if (simulation[i][j] > 0) {
            return true;
        }
        simulation[i][j] = hue;
        hue = hue >= 1 ? 0 : hue + .0001F;
        return true;
    }

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

    public void tick() {
        updated = 0;
        Random r = new Random();
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j > 0; j--) {
                float current = simulation[i][j],
                      above = simulation[i][j - 1],
                      side = -1;

                int dir = dirs[r.nextInt(2)];
                if (validIndex(i + dir, j)) {
                    side = simulation[i + dir][j];
                }

                if (current == 0 && above > 0) {
                    simulation[i][j] = above;
                    simulation[i][j - 1] = 0;
                    updated = 1;
                } else if (side == 0 && above > 0) {
                    simulation[i + dir][j] = above;
                    simulation[i][j - 1] = 0;
                    updated = 1;
                } else if (validIndex(i - dir, j) && simulation[i - dir][j] == 0 && above > 0) {
                    simulation[i - dir][j] = above;
                    simulation[i][j - 1] = 0;
                    updated = 1;
                }
            }
        }
    }

    public boolean isCurrentlyStatic() { return updated == 0; }
    public float state(int i, int j) {
        if (!validIndex(i, j)) {
            return -1;
        }
        return simulation[i][j];
    }
    public int getRows() { return this.rows; }
    public int getCols() { return this.cols; }

    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                simulation[i][j] = 0;
            }
        }
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(simulation[i][j]);
            }
            System.out.println();
        }
    }
}
