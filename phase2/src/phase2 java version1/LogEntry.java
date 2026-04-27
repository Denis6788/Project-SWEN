/**
 * Represents one food entry in the daily log.
 * Stores the food and number of servings.
 */
public class LogEntry {

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

    /**
     * Total calories for this entry
     */
    public double getCalories() {
        return food.getCalories() * servings;
    }

    /**
     * Total nutrients for this entry (FIXED)
     */
    public Nutrients getNutrients() {
        // FIX: correctly scale nutrients
        return new Nutrients(0, 0, 0)
                .addScaled(food.getNutrients(), servings);
    }

    @Override
    public String toString() {
        return String.format("%s x %.1f (%.1f cal)",
                food.getName(),
                servings,
                getCalories());
    }
}