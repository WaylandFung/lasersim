import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class FieldVisualizer {

    public static void saveEz(double[][] Ez, String filename) {
        
        int nx = Ez.length;
        int ny = Ez[0].length;

        int scale = 3;

        BufferedImage image = new BufferedImage(
            nx * scale, ny * scale,
            BufferedImage.TYPE_INT_RGB
        );

        // Find the largest absolute field value
        double max = 0.0;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                max = Math.max(max, Math.abs(Ez[i][j]));
            }
        }


        

        // Convert field values to colors
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {

                double value = max > 0
                    ? Ez[i][j] / max : 0.0;

                Color color;

                if (value < 0) {
                    // Blue to white
                    int c = (int) (255 * (1 + value));
                    color = new Color(c, c, 255);
                } else {
                    // White to red
                    int c = (int) (255 * (1 - value));
                    color = new Color(255, c, c);
                }

                // Flip vertical axis for display
                for (int a = 0; a < scale; a++) {
                    for (int b = 0; b < scale; b++) {
                        image.setRGB(
                            i * scale + a,
                            (ny - 1 - j) * scale + b,
                            color.getRGB()
                        );
                    }
                }
            }
        }
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Saved field map: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}