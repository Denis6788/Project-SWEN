
public class LogEntry {

    private final Food   food;
    private final double servings;

    public LogEntry(Food food, double servings) {
        this.food     = food;
        this.servings = servings;
    }

    public Food   getFood()     { return food;     }
    public double getServings() { return servings; }

    @Override
    public String toString() {
        return String.format("%s x %.1f (%.1f cal)",
                food.getName(), servings, food.getCalories() * servings);
    }
}
