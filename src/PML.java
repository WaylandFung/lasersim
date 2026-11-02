
public class PML {
    private final int nx, ny, thickness;
    private final double sigmaMax;

    public PML(int nx, int ny, int thickness,
               double dx, double dy, double dt) {
        if (thickness <= 0 ||
            2 * thickness >= Math.min(nx, ny)) {
            throw new IllegalArgumentException(
                    "Invalid PML thickness");
        }

        this.nx = nx;
        this.ny = ny;
        this.thickness = thickness;

        double targetReflection = 1e-8;
        double eta0 = Math.sqrt(
                4.0 * Math.PI * 1e-7 / 8.854e-12);

        double length = thickness * Math.min(dx, dy);
        int polynomialOrder = 3;

        // Peak electric conductivity, in S/m.
        sigmaMax = -(polynomialOrder + 1)
                * Math.log(targetReflection)
                * eta0 / (2.0 * length);
    }

    public double sigmaX(double x) {
        double left = Math.max(
                0.0, (thickness - x) / thickness);

        double right = Math.max(
                0.0, (x - (nx - thickness)) / thickness);

        double p = Math.max(left, right);
        return sigmaMax * Math.pow(p, 3);
    }

    public double sigmaY(double y) {
        double bottom = Math.max(
                0.0, (thickness - y) / thickness);

        double top = Math.max(
                0.0, (y - (ny - thickness)) / thickness);

        double p = Math.max(bottom, top);
        return sigmaMax * Math.pow(p, 3);
    }
}
