public class TestSimul {
    public static void main(String[] args) {
        SandSimul sand = new SandSimul();
        sand.setOn(1, 2);
        sand.print();
        System.out.println();
        while (!sand.isCurrentlyStatic()) {
            sand.tick();
            sand.print();
            System.out.println();
        }
    }
}
