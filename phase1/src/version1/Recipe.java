import java.util.ArrayList;
import java.util.List;

/**
 * Composite node in the Composite pattern.
 * Computes nutrition recursively from its ingredients.
 * Ingredients can be BasicFood or other Recipes (sub-recipes).
 */
public class Recipe implements FoodItem {
    private final String name;
    private final List<IngredientEntry> ingredients = new ArrayList<>();

    public Recipe(String name) {
        this.name = name;
    }

    public void addIngredient(FoodItem food, double servings) {
        ingredients.add(new IngredientEntry(food, servings));
    }

    public List<IngredientEntry> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    @Override public String getName() { return name; }

    @Override
    public double getCalories() {
        double total = 0;
        for (IngredientEntry e : ingredients)
            total += e.food.getCalories() * e.servings;
        return total;
    }

    @Override
    public Nutrients getNutrients() {
        Nutrients total = new Nutrients(0, 0, 0);
        for (IngredientEntry e : ingredients)
            total = total.addScaled(e.food.getNutrients(), e.servings);
        return total;
    }

    /** r,name,f1name,f1count,f2name,f2count,... */
    @Override
    public String toCSV() {
        StringBuilder sb = new StringBuilder("r,").append(name);
        for (IngredientEntry e : ingredients)
            sb.append(",").append(e.food.getName()).append(",").append(String.format("%.1f", e.servings));
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Recipe{%s, %d ingredients}", name, ingredients.size());
    }

    public static class IngredientEntry {
        public final FoodItem food;
        public final double servings;
        public IngredientEntry(FoodItem food, double servings) {
            this.food = food; this.servings = servings;
        }
    }
}
