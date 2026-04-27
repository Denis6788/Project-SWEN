import java.util.*;

public interface Food {
    String getName();
    double getCalories();
    double getFat();
    double getCarbs();
    double getProtein();
}



class BasicFood implements Food {
    private String name;
    private double calories, fat, carbs, protein;

    public BasicFood(String name, double calories, double fat, double carbs, double protein) {
        this.name = name;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
    }

    public String getName() { return name; }
    public double getCalories() { return calories; }
    public double getFat() { return fat; }
    public double getCarbs() { return carbs; }
    public double getProtein() { return protein; }
}

/* -------- RECIPE -------- */

class Recipe implements Food {
    private String name;
    private List<Food> ingredients = new ArrayList<>();
    private List<Double> amounts = new ArrayList<>();

    public Recipe(String name) {
        this.name = name;
    }

    public void add(Food food, double amount) {
        ingredients.add(food);
        amounts.add(amount);
    }

    public String getName() { return name; }

    private double sum(java.util.function.ToDoubleFunction<Food> f) {
        double total = 0;
        for (int i = 0; i < ingredients.size(); i++) {
            total += f.applyAsDouble(ingredients.get(i)) * amounts.get(i);
        }
        return total;
    }

    public double getCalories() { return sum(Food::getCalories); }
    public double getFat() { return sum(Food::getFat); }
    public double getCarbs() { return sum(Food::getCarbs); }
    public double getProtein() { return sum(Food::getProtein); }
}

/* -------- FACTORY -------- */

class FoodFactory {
    public static Food createBasic(String name, double c, double f, double cb, double p) {
        return new BasicFood(name, c, f, cb, p);
    }

    public static Recipe createRecipe(String name) {
        return new Recipe(name);
    }
}