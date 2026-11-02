
public class PhotoionizationCurrent {

    public static double calculate(
            double ez,
            double cycleField,
            double effectiveGap,
            double photoRate,
            double depopulationFactor) {

        if (cycleField <= 0.0 || Math.abs(ez) == 0.0) {
            return 0.0;
        }

        return effectiveGap * photoRate
                * depopulationFactor / cycleField
                * Math.signum(ez);
    }
}
