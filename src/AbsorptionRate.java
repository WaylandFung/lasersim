public class AbsorptionRate {

    private final double photonEnergyEV;

    public AbsorptionRate(double photonEnergyEV) {
        this.photonEnergyEV = photonEnergyEV;
    }

    public double getElectronEnergy(int level) {
        return level * photonEnergyEV;
    }

    public double getRate(int level, double electricField) {

        double electronEnergy = getElectronEnergy(level);

        // Placeholder until the physical rate law is established.
        return 1e12;
    }
}