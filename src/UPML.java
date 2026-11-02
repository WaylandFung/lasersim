public class UPML {

    private final int nx;
    private final int ny;
    private final int thickness;
    private final double sigmaMax;

    public UPML(int nx, int ny, int thickness,
                double dx, double dy, double dt) {
        this.nx = nx;
        this.ny = ny;
        this.thickness = thickness;

        // Approximate starting value for a graded absorber.
        double sigmaX = 4.0 / (377.0 * thickness * dx);
        double sigmaY = 4.0 / (377.0 * thickness * dy);

        sigmaMax = Math.max(sigmaX, sigmaY);
    }

    // Returns a damping profile for a grid position.
    public double getSigma(int i, int j) {
        double px = Math.max(
            0.0,
            (thickness - Math.min(i, nx - 1 - i))
                / (double) thickness
        );

        double py = Math.max(
            0.0,
            (thickness - Math.min(j, ny - 1 - j))
                / (double) thickness
        );

        return sigmaMax * Math.pow(
            Math.max(px, py), 3
        );
    }
}