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

        // Initialize components
        MaterialGrid materialGrid =
                new MaterialGrid(nx, ny);

        Geometry.buildLayers(materialGrid, nx, ny);

        MaxwellSolver solver =
                new MaxwellSolver(nx, ny, dx, dy, dt);

        LaserSource laser = new LaserSource(1e6);

        BoundaryConditions boundaries =
                new BoundaryConditions();

        // Source location (temporary test position)
        int sourceX = nx / 2;
        int sourceY = ny / 2;

        double time = 0.0;
        int totalSteps = 1000;

        // Main simulation loop
        for (int n = 0; n < totalSteps; n++) {

            solver.updateMagnetic(Ez, Hx, Hy);

            solver.updateElectric(
                    Ez, Hx, Hy, materialGrid);
    

            time += dt;

            Ez[sourceX][sourceY] += laser.getField(time);

            boundaries.apply(Ez, nx, ny);
            if (n % 20 == 0) {
                String filename = String.format(
                    "frames/frame_%05d.png", n / 20
                );

                FieldVisualizer.saveEz(Ez, filename);
            }
        }

        System.out.println("Simulation finished.");
        
    }
}