
public class Photoionization {

    private static final double E_CHARGE = 1.602176634e-19;
    private static final double HBAR = 1.054571817e-34;
    private static final double M_E = 9.1093837e-31;
    private static final double C = 299792458.0;

    private final double omega;
    private final double bandGap;
    private final double reducedMass;

    public Photoionization(
            double wavelength,
            double bandGapEV,
            double electronMassRatio,
            double holeMassRatio) {

        omega = 2.0 * Math.PI * C / wavelength;
        bandGap = bandGapEV * E_CHARGE;

        double me = electronMassRatio * M_E;
        double mh = holeMassRatio * M_E;

        reducedMass = 1.0 / (1.0 / me + 1.0 / mh);
    }

    // Equation (3): Keldysh parameter
    public double getKeldyshParameter(double E) {
        if (E <= 0.0) {
            return Double.POSITIVE_INFINITY;
        }

        return omega * Math.sqrt(reducedMass * bandGap)
                / (E_CHARGE * E);
    }

    // Equation (4): effective band gap
    public double getEffectiveBandGap(double gamma) {
        double g1 = gamma * gamma / (1.0 + gamma * gamma);
        double g2 = 1.0 / (1.0 + gamma * gamma);

        return (2.0 / Math.PI)
                * (bandGap / (HBAR * omega))
                * ellipticE(g2) / Math.sqrt(g1);
    }

    // Equations (5) and (6): photoionization rate
    public double getPhotoionizationRate(double E) {
        if (E <= 0.0) {
            return 0.0;
        }

        double gamma = getKeldyshParameter(E);

        // Avoid numerical overflow at extremely large gamma.
        if (!Double.isFinite(gamma) || gamma > 1e100) {
            return 0.0;
        }

        double g1 = gamma * gamma / (1.0 + gamma * gamma);
        double g2 = 1.0 / (1.0 + gamma * gamma);

        double weff = getEffectiveBandGap(gamma);
        double n = Math.floor(weff + 1.0);

        double K1 = ellipticK(g1);
        double E1 = ellipticE(g1);
        double K2 = ellipticK(g2);
        double E2 = ellipticE(g2);

        double exponentFactor = (K1 - E1) / E2;

        double q = Math.sqrt(Math.PI / (2.0 * K2));
        double sum = 0.0;

        // Numerically truncate the infinite sum in Equation (6).
        for (int j = 0; j < 10000; j++) {
            double exponential = Math.exp(
                    -j * Math.PI * exponentFactor);

            double argument = Math.sqrt(
                    Math.PI * Math.PI * (n - weff + j)
                    / (2.0 * K2 * E2));

            double term = exponential * dawson(argument);
            sum += term;

            if (j > 10 && Math.abs(term) < 1e-12) {
                break;
            }
        }

        q *= sum;

        double prefactor = 2.0 * (2.0 * omega / (9.0 * Math.PI))
                * Math.pow(
                    reducedMass * omega
                    / (HBAR * Math.sqrt(g1)),
                    1.5);

        double exponent = -Math.PI * n * exponentFactor;

        return prefactor * q * Math.exp(exponent);
    }

    // Complete elliptic integral of the first kind K(m)
    private static double ellipticK(double m) {
        m = Math.max(0.0, Math.min(m, 1.0 - 1e-14));

        double a = 0.0;
        double b = Math.PI / 2.0;
        int intervals = 1000;
        double h = (b - a) / intervals;
        double sum = 0.0;

        for (int i = 0; i <= intervals; i++) {
            double theta = a + i * h;
            double value = 1.0 / Math.sqrt(
                    1.0 - m * Math.sin(theta) * Math.sin(theta));

            sum += (i == 0 || i == intervals) ? value
                    : (i % 2 == 0 ? 2.0 : 4.0) * value;
        }

        return sum * h / 3.0;
    }

    // Complete elliptic integral of the second kind E(m)
    private static double ellipticE(double m) {
        m = Math.max(0.0, Math.min(m, 1.0));

        double a = 0.0;
        double b = Math.PI / 2.0;
        int intervals = 1000;
        double h = (b - a) / intervals;
        double sum = 0.0;

        for (int i = 0; i <= intervals; i++) {
            double theta = a + i * h;
            double value = Math.sqrt(
                    1.0 - m * Math.sin(theta) * Math.sin(theta));

            sum += (i == 0 || i == intervals) ? value
                    : (i % 2 == 0 ? 2.0 : 4.0) * value;
        }

        return sum * h / 3.0;
    }

    // Dawson integral: Phi(x) = exp(-x^2) integral exp(t^2) dt
    private static double dawson(double x) {
        if (x < 0.0) {
            return -dawson(-x);
        }

        if (x == 0.0) {
            return 0.0;
        }

        int intervals = Math.max(100, (int) (x * 100));
        if (intervals % 2 != 0) {
            intervals++;
        }

        double h = x / intervals;
        double sum = 0.0;

        for (int i = 0; i <= intervals; i++) {
            double t = i * h;
            double value = Math.exp(t * t - x * x);

            sum += (i == 0 || i == intervals) ? value
                    : (i % 2 == 0 ? 2.0 : 4.0) * value;
        }

        return sum * h / 3.0;
    }
}
