
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FieldVisualizer {

    public static void saveEz(
            double[][] Ez,
            MaterialGrid materials,
            String filename) {

        int nx = Ez.length;
        int ny = Ez[0].length;
        int scale = 3;

        BufferedImage image = new BufferedImage(
                nx * scale,
                ny * scale,
                BufferedImage.TYPE_INT_RGB
        );

        // Find maximum absolute electric field
        double max = 0.0;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                max = Math.max(max, Math.abs(Ez[i][j]));
            }
        }

        // Color each pixel
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {

                double value = max > 0
                        ? Ez[i][j] / max : 0.0;

                // Electric-field colors: blue to white to red
                Color fieldColor;

                if (value < 0) {
                    int c = (int) (255 * (1 + value));
                    fieldColor = new Color(c, c, 255);
                } else {
                    int c = (int) (255 * (1 - value));
                    fieldColor = new Color(255, c, c);
                }

                // Get material color
                int id = materials.getMaterial(i, j);
                Color materialColor = getMaterialColor(id);

                // Blend field and material colors
                double alpha = 0.25;

                int r = (int) (
                        (1 - alpha) * fieldColor.getRed()
                        + alpha * materialColor.getRed());

                int g = (int) (
                        (1 - alpha) * fieldColor.getGreen()
                        + alpha * materialColor.getGreen());

                int b = (int) (
                        (1 - alpha) * fieldColor.getBlue()
                        + alpha * materialColor.getBlue());

                Color finalColor = new Color(r, g, b);

                // Scale pixels and flip vertical axis
                for (int a = 0; a < scale; a++) {
                    for (int b2 = 0; b2 < scale; b2++) {
                        image.setRGB(
                                i * scale + a,
                                (ny - 1 - j) * scale + b2,
                                finalColor.getRGB()
                        );
                    }
                }
            }
        }

        // Save PNG frame
        try {
            File output = new File(filename);
            File parent = output.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            ImageIO.write(image, "png", output);
            System.out.println("Saved field map: " + filename);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Assign a color to each material
    private static Color getMaterialColor(int id) {
        switch (id) {
            case MaterialGrid.VACUUM:
                return new Color(40, 40, 40);       // Dark gray

            case MaterialGrid.SIO2:
                return new Color(56, 168, 232);     // Blue

            case MaterialGrid.HFO2:
                return new Color(244, 197, 66);     // Yellow

            case MaterialGrid.GOLD:
                return new Color(232, 93, 93);      // Red

            default:
                return Color.BLACK;
        }
    }
}
