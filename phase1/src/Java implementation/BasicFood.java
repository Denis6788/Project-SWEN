

public class BasicFood implements FoodItem { //made by Marko Obsivac
    private final String name;
    private final double calories;
    private final double fat;
    private final double carb;
    private final double protein;

    public BasicFood(String name, double calories, double fat, double carb, double protein) {
        this.name     = name;
        this.calories = calories;
        this.fat      = fat;
        this.carb     = carb;
        this.protein  = protein;
    }

    @Override public String getName()         { return name; }
    @Override public double getCalories()     { return calories; }
    @Override public Nutrients getNutrients() { return new Nutrients(fat, carb, protein); }

   
    @Override
    public String toCSV() {
        return String.format("b,%s,%.1f,%.1f,%.1f,%.1f", name, calories, fat, carb, protein);
    }

    @Override
    public String toString() {
        return String.format("BasicFood{%s, cal=%.1f}", name, calories);
    }
}
