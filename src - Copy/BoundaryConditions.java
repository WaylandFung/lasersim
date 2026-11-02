public class BoundaryConditions {
//temporary, use UPML (Uniaxial Perfectly Matched Layer)
    public void apply(
            double[][] Ez,
            int nx,
            int ny) {

        // Temporary boundary condition:
        // Set the outermost electric-field cells to zero.

        for (int i = 0; i < nx; i++) {
            Ez[i][0] = 0.0;
            Ez[i][ny - 1] = 0.0;
        }

        for (int j = 0; j < ny; j++) {
            Ez[0][j] = 0.0;
            Ez[nx - 1][j] = 0.0;
        }
    }
}