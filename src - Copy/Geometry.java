public class Geometry {

    public static void buildLayers(
            MaterialGrid grid,
            int nx,
            int ny) {

        // Example layer boundaries; replace with
        // positions calculated from the paper.

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                //grid.setMaterial(i, j, MaterialGrid.SIO2);
                if (j < 50) {
                    grid.setMaterial(i, j, MaterialGrid.VACUUM);

                } else if (j < 100) {
                    grid.setMaterial(i, j, MaterialGrid.SIO2);

                } else if (j < 150) {
                    grid.setMaterial(i, j, MaterialGrid.HFO2);

                } else {
                    grid.setMaterial(i, j, MaterialGrid.GOLD);
                }
            }
        }
    }
}