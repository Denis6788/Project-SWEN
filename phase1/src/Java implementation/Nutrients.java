
public class Nutrients { // made by Denis Keselj and Marko Obsivac
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