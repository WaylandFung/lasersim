public class LaserSource {

    private final double wavelength = 1057e-9; // meters
    private final double pulseDuration = 500e-15; // seconds
    private final double amplitude;

    private final double frequency;
    private final double omega;

    public LaserSource(double amplitude) {
        this.amplitude = amplitude;

        double c = 2.998e8;
        frequency = c / wavelength;
        omega = 2.0 * Math.PI * frequency;
    }

    public double getField(double time) {
        double t0 = 3.0 * pulseDuration;

        double envelope = Math.exp(
                -4.0 * Math.log(2.0)
                * Math.pow((time - t0) / pulseDuration, 2)
        );

        return amplitude * envelope * Math.sin(omega * time);
    }
}