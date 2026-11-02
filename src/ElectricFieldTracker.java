public class ElectricFieldTracker {

    private final double[][] maxField;
    private int stepCount = 0;
    private final int stepsPerCycle;

    public ElectricFieldTracker(
            int nx, int ny, int stepsPerCycle) {

        this.maxField = new double[nx][ny];
        this.stepsPerCycle = stepsPerCycle;
    }

    public void update(double[][] ez) {

        for (int i = 0; i < ez.length; i++) {
            for (int j = 0; j < ez[i].length; j++) {

                maxField[i][j] = Math.max(
                        maxField[i][j],
                        Math.abs(ez[i][j])
                );
            }
        }

        stepCount++;
    }

    public boolean cycleComplete() {
        return stepCount >= stepsPerCycle;
    }

    public double[][] getCycleMaximum() {
        double[][] result = new double[maxField.length][];

        for (int i = 0; i < maxField.length; i++) {
            result[i] = maxField[i].clone();
        }

        return result;
    }

    public void reset() {
        for (int i = 0; i < maxField.length; i++) {
            java.util.Arrays.fill(maxField[i], 0.0);
        }

        stepCount = 0;
    }
}