/**
 * Value object holding fat, carb, and protein grams for one serving.
 */
public class Nutrients {
    private double fat;
    private double carb;
    private double protein;

    public Nutrients(double fat, double carb, double protein) {
        this.fat     = fat;
        this.carb    = carb;
        this.protein = protein;
    }

    public double getFat()     { return fat; }
    public double getCarb()    { return carb; }
    public double getProtein() { return protein; }
    public double getTotal()   { return fat + carb + protein; }

    /** Returns a new Nutrients scaled by servings and added to this. */
    public Nutrients addScaled(Nutrients other, double servings) {
        return new Nutrients(
            this.fat     + other.fat     * servings,
            this.carb    + other.carb    * servings,
            this.protein + other.protein * servings
        );
    }

    public int fatPercent() {
        return percent(fat);
    }

    public int carbPercent() {
        return percent(carb);
    }

    /** Computed as remainder so fat+carb+protein always sums to exactly 100. */
    public int proteinPercent() {
        if (getTotal() == 0) return 0;
        return 100 - percent(fat) - percent(carb);
    }

    private int percent(double grams) {
        if (getTotal() == 0) return 0;
        return (int) Math.round((grams / getTotal()) * 100);
    }

    @Override
    public String toString() {
        return String.format("fat=%.1fg carb=%.1fg protein=%.1fg", fat, carb, protein);
    }
}
