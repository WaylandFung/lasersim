public class MaxwellSolver {
    private final int nx, ny;
    private final double dx, dy, dt;

    private final double mu0 = 4.0 * Math.PI * 1e-7;
    private final double eps0 = 8.854e-12;

    public MaxwellSolver(int nx, int ny,
                         double dx, double dy, double dt) {
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        this.dt = dt;
    }

    // Damping factor for a field with conductivity sigma.
    private double dampingA(double sigma, double denominator) {
        return Math.exp(-sigma * dt / denominator);
    }

    // Coefficient multiplying the spatial field difference.
    private double dampingB(double sigma, double denominator,
                            double spacing) {
        if (Math.abs(sigma) < 1e-30) {
            return dt / (denominator * spacing);
        }

        return -Math.expm1(-sigma * dt / denominator)
                / (sigma * spacing);
    }

    // Update magnetic fields using total Ez.
    public void updateMagnetic(
            double[][] ez,
            double[][] hx,
            double[][] hy,
            PML pml) {

        // Hx: staggered in y.
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny - 1; j++) {

                double sigmaE = pml.sigmaY(j + 1.0);
                double sigmaM = (mu0 / eps0) * sigmaE;

                double a = dampingA(sigmaM, mu0);
                double b = dampingB(sigmaM, mu0, dy);

                hx[i][j] = a * hx[i][j]
                        - b * (ez[i][j + 1] - ez[i][j]);
            }
        }

        // Hy: staggered in x.
        for (int i = 0; i < nx - 1; i++) {
            for (int j = 0; j < ny; j++) {

                double sigmaE = pml.sigmaX(i + 1.0);
                double sigmaM = (mu0 / eps0) * sigmaE;

                double a = dampingA(sigmaM, mu0);
                double b = dampingB(sigmaM, mu0, dx);

                hy[i][j] = a * hy[i][j]
                        + b * (ez[i + 1][j] - ez[i][j]);
            }
        }
    }

    // Update split electric fields and reconstruct total Ez.
    public void updateElectric(
            double[][] ez,
            double[][] ezx,
            double[][] ezy,
            double[][] hx,
            double[][] hy,
            MaterialGrid materials,
            PML pml) {

        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {

                int id = materials.getMaterial(i, j);

                double epsilon = eps0
                        * MaterialProperties
                          .getRelativePermittivity(id);

                double sigmaX = pml.sigmaX(i + 0.5);
                double sigmaY = pml.sigmaY(j + 0.5);

                double ax = dampingA(sigmaX, epsilon);
                double ay = dampingA(sigmaY, epsilon);

                double bx = dampingB(sigmaX, epsilon, dx);
                double by = dampingB(sigmaY, epsilon, dy);

                double dHyDx = hy[i][j] - hy[i - 1][j];
                double dHxDy = hx[i][j] - hx[i][j - 1];

                ezx[i][j] = ax * ezx[i][j]
                        + bx * dHyDx;

                ezy[i][j] = ay * ezy[i][j]
                        - by * dHxDy;

                ez[i][j] = ezx[i][j] + ezy[i][j];
            }
        }
    }
}