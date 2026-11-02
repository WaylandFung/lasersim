
public class ElectronDynamics {

    private final double[] levels;
    private double electronDensity;

    public ElectronDynamics(int numberOfLevels) {
        if (numberOfLevels < 2) {
            throw new IllegalArgumentException(
                    "At least two energy levels are required.");
        }

        levels = new double[numberOfLevels];
        electronDensity = 0.0;
    }

    public double getElectronDensity() {
        return electronDensity;
    }

    public double[] getLevels() {
        return levels.clone();
    }

    public void setLevelPopulation(int level, double population) {
    if (level < 0 || level >= levels.length || population < 0.0) {
        throw new IllegalArgumentException("Invalid population.");
    }

    levels[level] = population;

    // Keep total density consistent with the level populations
    electronDensity = 0.0;
    for (double n : levels) {
        electronDensity += n;
    }
}

    public void update(
            double photoRate,
            double impactRate,
            double absorptionRate,
            double valenceDensity,
            double tauR,
            double dt) {

        if (dt <= 0.0 || tauR <= 0.0 || valenceDensity <= 0.0
                || photoRate < 0.0 || impactRate < 0.0
                || absorptionRate < 0.0) {
            throw new IllegalArgumentException(
                    "Invalid model parameter.");
        }

        int m = levels.length - 1;

        double oldNe = electronDensity;
        double oldTop = levels[m];

        if (oldNe >= valenceDensity) {
            throw new IllegalStateException(
                    "Electron density reached valence density.");
        }

        // Photoionization depopulation factor
        double f = 1.0 - oldNe / (valenceDensity - oldNe);

        // Equation (10): total electron density
        double dNe = f * photoRate
                + impactRate * oldTop
                - oldNe / tauR;

        double newNe = oldNe + dt * dNe;

        // Calculate all updated level populations
        double[] next = new double[levels.length];

        // Lowest level
        next[0] = levels[0] + dt * (
        dNe
        + impactRate * oldTop
        - absorptionRate * levels[0]
);

        // Intermediate levels
        for (int j = 1; j < m; j++) {
            next[j] = levels[j] + dt * absorptionRate
                    * (next[j - 1] - levels[j]);
        }

        // Highest level
        next[m] = levels[m] + dt * (
                absorptionRate * next[m - 1]
                - impactRate * oldTop
        );

        // Reject invalid results instead of silently hiding them
        if (newNe < 0.0 || !Double.isFinite(newNe)) {
            throw new ArithmeticException(
                    "Invalid electron density update.");
        }

        for (double population : next) {
            if (population < 0.0 || !Double.isFinite(population)) {
                throw new ArithmeticException(
                        "Invalid energy-level population.");
            }
        }

        electronDensity = newNe;
        System.arraycopy(next, 0, levels, 0, levels.length);
    
    }
}
