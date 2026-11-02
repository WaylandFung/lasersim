public class ElectronDensity {

    public static double update(
            double currentDensity,
            double availableDensity,
            double rate,
            double dt) {

        double remaining = availableDensity - currentDensity;

        return availableDensity
                - remaining * Math.exp(-rate * dt);
    }
}