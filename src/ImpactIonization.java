public class ImpactIonization {

    // Equation (8)
    public static double getRate(
            double electronEnergyEV,
            double thresholdEnergyEV,
            double alpha0) {

        if (electronEnergyEV <= thresholdEnergyEV) {
            return 0.0;
        }

        double ratio =
                (electronEnergyEV - thresholdEnergyEV)
                / thresholdEnergyEV;

        return alpha0 * ratio * ratio;
    }

    // Equation (9), using the paper's approximate expression.
    public static double getAlpha0SI(
        double epsilonR,
        double electronMassRatio,
        double holeMassRatio,
        double Ac,
        double Av) {

        double e = 1.602176634e-19;
        double eps0 = 8.8541878128e-12;
        double hbar = 1.054571817e-34;
        double m0 = 9.1093837e-31;

        double me = electronMassRatio * m0;
        double mh = holeMassRatio * m0;

        double coulombEnergyLength =
                e * e / (4.0 * Math.PI * eps0 * epsilonR);

        return Math.pow(coulombEnergyLength, 2)
                * me * Ac * Ac * Av * Av
                / (Math.pow(hbar, 3)
                * Math.pow(1.0 + 2.0 * me / mh, 1.5));
    }
    public static double getIonizationThresholdEV(
        double bandGapEV,
        double electronMassRatio,
        double holeMassRatio) {

        double ratio = electronMassRatio / holeMassRatio;
        double beta0 = (1.0 + 2.0 * ratio) / (1.0 + ratio);

        return beta0 * bandGapEV;
    }

    public static double joulesToEV(double energyJ) {
        return energyJ / 1.602176634e-19;
    }
}