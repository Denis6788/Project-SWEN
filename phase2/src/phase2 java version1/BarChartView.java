import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BarChartView extends Canvas {

    private double fat = 0;
    private double carbs = 0;
    private double protein = 0;

    public BarChartView(double width, double height) {
        super(width, height);
        draw();
    }

    // called when data changes, updating charts
    public void updateData(double fat, double carbs, double protein) {
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();

        double width = getWidth();
        double height = getHeight();

        gc.clearRect(0, 0, width, height);

        double max = Math.max(fat, Math.max(carbs, protein));

        // empty chart for no data
        if (max == 0) return;

        double barWidth = width / 3;

        double fatHeight = (fat / max) * height;
        double carbHeight = (carbs / max) * height;
        double proteinHeight = (protein / max) * height;

        // red for fat
        gc.setFill(Color.RED);
        gc.fillRect(0, height - fatHeight, barWidth, fatHeight);

        // green for carbs
        gc.setFill(Color.GREEN);
        gc.fillRect(barWidth, height - carbHeight, barWidth, carbHeight);

        // blue for protein 
        gc.setFill(Color.BLUE);
        gc.fillRect(barWidth * 2, height - proteinHeight, barWidth, proteinHeight);
    }
}