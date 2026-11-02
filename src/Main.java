import static java.lang.Math.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class Main {
/*     int wave_length = 1057;
    int pulse_duration = 500;
    char polarization = 's';
    double incident_angle = 77.2;
    double temporal_envelope;

    double band_gap = 9.0;
    double refractive_index = 1.45;
    double hole_mass = 1;
    double electron_mass = 0.6;
    int recombination_time = 1;



    int[][] material;

    

    

    public record material(float band_gap, float refractive_index, float hole_mass, float electron_mass, int recombination_time){

    } 
*/

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get("frames"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Grid dimensions
        int nx = 1900;
        int ny = 300;

        // Grid spacing and time step (SI units)
        double dx = 12e-9;
        double dy = 12e-9;
        double dt = 0.0283e-15;

        // Electromagnetic fields
        double[][] Ez = new double[nx][ny];
        double[][] Hx = new double[nx][ny - 1];
        double[][] Hy = new double[nx - 1][ny];

        double[][] Ezx = new double[nx][ny];
        double[][] Ezy = new double[nx][ny];

        PML pml = new PML(nx, ny, 30, dx, dy, dt);

        // Initialize components
        MaterialGrid materialGrid =
                new MaterialGrid(nx, ny);

        Geometry.buildLayers(materialGrid, nx, ny);

        MaxwellSolver solver =
                new MaxwellSolver(nx, ny, dx, dy, dt);

        LaserSource laser = new LaserSource(1e6);

        BoundaryConditions boundaries =
                new BoundaryConditions();

        double absorptionRate = AbsorptionRate.getRate(0, 0);

        // Source location (temporary test position)
        int sourceX = nx / 2;
        int sourceY = ny-50;

        double time = 0.0;
        int totalSteps = 1000;

        //Initialize number of levels

        int numberOfLevels = 10;
        ElectronDynamics dynamics = new ElectronDynamics(numberOfLevels);
        
        ElectronDynamics[][] electrons =
        new ElectronDynamics[nx][ny];

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                electrons[i][j] = new ElectronDynamics(10);
            }
        }

        double[][] electronEnergy = new double[nx][ny];

        

        //test values
        Photoionization siO2PI = new Photoionization(
                1057e-9,  // wavelength (m)
                9.0,      // band gap (eV)
                0.60,     // electron mass / free-electron mass
                8.00      // hole mass / free-electron mass
        );
/* 
        double testField = 1e10; // V/m

        System.out.println("Gamma = "
                + siO2PI.getKeldyshParameter(testField));

        System.out.println("Effective gap = "
                + siO2PI.getEffectiveBandGap(
                        siO2PI.getKeldyshParameter(testField)));

        System.out.println("Photoionization rate = "
                + siO2PI.getPhotoionizationRate(testField)
                + " m^-3 s^-1");

        // Test the Keldysh parameter
        
        
        
        
        double[] fields = {
            1e8, 1e9, 1e10, 1e11, 1e12
        };

        for (double E : fields) {
            double gamma = siO2PI.getKeldyshParameter(E);
            double rate = siO2PI.getPhotoionizationRate(E);

            System.out.printf(
                "E = %.1e V/m, gamma = %.4f, rate = %.4e%n",
                E, gamma, rate
            );
        }

        


ElectronDynamics dynamics = new ElectronDynamics(10);

double photoRate = 1e25;      // temporary test value, m^-3 s^-1
double impactRate = 0.0;      // s^-1
double absorptionRate = absorptionRate; // s^-1
double valenceDensity = 1e28; // temporary test value, m^-3
double dt1 = 0.0283e-15;       // your FDTD time step

for (int i = 0; i < 1000; i++) {
    dynamics.update(
        photoRate,
        impactRate,
        absorptionRate,
        valenceDensity,
        dt1
    );
}

System.out.println(
    "Electron density = " + dynamics.getElectronDensity()
);
*/

double bandGapEV = 9.0;
double electronMassRatio = 0.60;
double holeMassRatio = 8.0;

double ionizationThresholdEV =
        ImpactIonization.getIonizationThresholdEV(
                bandGapEV,
                electronMassRatio,
                holeMassRatio
        );



double h = 6.62607015e-34;
double c = 299792458.0;
double wavelength = 1057e-9;

double photonEnergyEV =
        (h * c / wavelength) / 1.602176634e-19;




System.out.println("Ionization threshold: "
        + ionizationThresholdEV + " eV");
System.out.println("Photon energy: "
        + photonEnergyEV + " eV");
System.out.println("Number of levels: "
        + numberOfLevels);












        ElectricFieldTracker tracker =
        new ElectricFieldTracker(nx, ny, 125);

        double[][] photoRate = new double[nx][ny];
        double[][] impactRate = new double[nx][ny];

        Photoionization photoionization =
        new Photoionization(
                1057e-9,  // wavelength in meters
                bandGapEV,
                electronMassRatio,
                holeMassRatio
        );


        // Main simulation loop
        for (int n = 0; n < totalSteps; n++) {

            solver.updateMagnetic(Ez, Hx, Hy, pml);

            solver.updateElectric(
                    Ez, Ezx, Ezy, Hx, Hy, materialGrid, pml);
    

            time += dt;

            Ez[sourceX][sourceY] += laser.getField(time);

            Ezx[sourceX][sourceY] += laser.getField(time);
            Ez[sourceX][sourceY] =
            Ezx[sourceX][sourceY] + Ezy[sourceX][sourceY];
            boundaries.apply(Ez, nx, ny);

            // Track maximum electric field
            tracker.update(Ez);

            if (tracker.cycleComplete()) {

                double[][] cycleMaximum = tracker.getCycleMaximum();

                for (int i = 0; i < nx; i++) {
                    for (int j = 0; j < ny; j++) {

                        double electricField = cycleMaximum[i][j];

                        photoRate[i][j] =
                                photoionization.getPhotoionizationRate(electricField);
                    }
                }

                tracker.reset();
            }
            if (n % 20 == 0) {
                String filename = String.format(
                    "frames/frame_%05d.png", n / 20
                );

                FieldVisualizer.saveEz(Ez, materialGrid, filename);
            }
        }

        System.out.println("Simulation finished.");
        
    }
}