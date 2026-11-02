public class ElectronDynamicsTest {

    public static void main(String[] args) {

        testZeroRates();
        testAbsorption();
        testRecombination();
        testImpactIonization();

    }

    // Test 1: All rates are zero
    public static void testZeroRates() {

        ElectronDynamics model = new ElectronDynamics(10);

        model.update(0, 0, 0, 1e28, 1e-12, 1e-15);

        System.out.println("Test 1: Zero rates");
        printResults(model);
    }

    // Test 2: Absorption transfers electrons between levels
    public static void testAbsorption() {

        ElectronDynamics model = new ElectronDynamics(10);
        model.setLevelPopulation(0, 1e20);

        model.update(0, 0, 1e12, 1e28, 1e-12, 1e-15);

        System.out.println("\nTest 2: Absorption");
        printResults(model);
    }

    // Test 3: Recombination decreases electron density
    public static void testRecombination() {

        ElectronDynamics model = new ElectronDynamics(10);
        model.setLevelPopulation(0, 1e20);

        model.update(0, 0, 0, 1e28, 1e-12, 1e-15);

        System.out.println("\nTest 3: Recombination");
        printResults(model);
    }

    // Test 4: Impact ionization increases electron density
    public static void testImpactIonization() {

        ElectronDynamics model = new ElectronDynamics(10);
        model.setLevelPopulation(9, 1e20);

        model.update(0, 2e12, 0, 1e28, 1e-12, 1e-15);

        System.out.println("\nTest 4: Impact ionization");
        printResults(model);
    }

    public static void printResults(ElectronDynamics model) {

        System.out.println("Electron density: "
                + model.getElectronDensity());

        double sum = 0;

        for (int i = 0; i < model.getLevels().length; i++) {
            double population = model.getLevels()[i];
            sum += population;

            System.out.println("Level " + i + ": " + population);
        }

        System.out.println("Sum of levels: " + sum);
        System.out.println("Difference: "
                + Math.abs(model.getElectronDensity() - sum));
    }
}