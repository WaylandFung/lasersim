
public class MaterialProperties {

    

    public static double getRelativePermittivity(int materialId) {
        switch (materialId) {
            case MaterialGrid.VACUUM:
                return 1.0;

            case MaterialGrid.SIO2:
                return 2.1;  // Approximate

            case MaterialGrid.HFO2:
                return 4.2;  // Approximate

            case MaterialGrid.GOLD:
                return 1.0;  // Temporary placeholder

            default:
                throw new IllegalArgumentException("Unknown material ID");
        }
    }
}