

public class LogEntry { //made by Gabirel Muskaj

    private final FoodItem food;
    private final double servings;

    public LogEntry(FoodItem food, double servings) {
        this.food = food;
        this.servings = servings;
    }

    public FoodItem getFood() {
        return food;
    }

    public double getServings() {
        return servings;
    }

    public double getCalories() {
        return food.getCalories() * servings;
    }

    public Nutrients getNutrients() {
        return food.getNutrients().addScaled(new Nutrients(0,0,0), servings);
    }

    @Override
    public String toString() {
        return String.format("%s x %.1f (%.1f cal)",
                food.getName(), servings, getCalories());
    }
}