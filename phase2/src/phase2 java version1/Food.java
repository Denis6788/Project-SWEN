import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * Component interface for the Composite pattern.
 * Both BasicFood (leaf) and Recipe (composite) implement this.
 */
public interface Food {
    String getName();
    double getCalories();
    double getFat();
    double getCarbs();
    double getProtein();
}

//Leaf

class BasicFood implements Food {

    private String name;
    private double calories, fat, carbs, protein;

    public BasicFood(String name, double calories,
                     double fat, double carbs, double protein) {
        this.name     = name;
        this.calories = calories;
        this.fat      = fat;
        this.carbs    = carbs;
        this.protein  = protein;
    }

    // setters for edditing functionality
    public void setCalories(double v) { calories = v; }
    public void setFat(double v)      { fat      = v; }
    public void setCarbs(double v)    { carbs    = v; }
    public void setProtein(double v)  { protein  = v; }

    @Override public String getName()     { return name;     }
    @Override public double getCalories() { return calories; }
    @Override public double getFat()      { return fat;      }
    @Override public double getCarbs()    { return carbs;    }
    @Override public double getProtein()  { return protein;  }

    @Override
    public String toString() {
        return name + " (" + calories + " cal)";
    }
}


// COMPOSITE

class Recipe implements Food {

    private String       name;
    private List<Food>   ingredients = new ArrayList<>();
    private List<Double> amounts     = new ArrayList<>();

    public Recipe(String name) {
        this.name = name;
    }

    /** Adding an ingredient with the given number of servings. */
    public void add(Food food, double amount) {
        ingredients.add(food);
        amounts.add(amount);
    }

    /** removing the ingredient at the given index. */
    public void removeIngredient(int index) {
        ingredients.remove(index);
        amounts.remove(index);
    }

    public List<Food>   getIngredients() { return Collections.unmodifiableList(ingredients); }
    public List<Double> getAmounts()     { return Collections.unmodifiableList(amounts); }

    @Override public String getName() { return name; }

    private double sum(ToDoubleFunction<Food> getter) {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++) {
            total += getter.applyAsDouble(ingredients.get(i)) * amounts.get(i);
        }
        return total;
    }

    @Override public double getCalories() { return sum(Food::getCalories); }
    @Override public double getFat()      { return sum(Food::getFat);      }
    @Override public double getCarbs()    { return sum(Food::getCarbs);    }
    @Override public double getProtein()  { return sum(Food::getProtein);  }

    @Override
    public String toString() {
        return name + " (recipe, " + String.format("%.1f", getCalories()) + " cal)";
    }
}
