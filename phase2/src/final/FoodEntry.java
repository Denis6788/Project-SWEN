
public class FoodEntry {

    private final Food   food;
    private final double amount; // number of servings

    public FoodEntry(Food food, double amount) {
        this.food   = food;
        this.amount = amount;
    }

    public Food   getFood()   { return food;   }
    public double getAmount() { return amount; }

    /** Total calories for this entry. */
    public double calories() {
        return food.getCalories() * amount;
    }

    @Override
    public String toString() {
        return String.format("%s x %.1f (%.1f cal)",
                food.getName(), amount, calories());
    }
}
