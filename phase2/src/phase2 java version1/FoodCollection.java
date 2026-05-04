import java.io.*;
import java.util.*;

/**
 * stores all foods (basic and recipes) available in the system.
 *
 * CSV format (foods.csv):
 *   Basic food:  b,name,calories,fat,carb,protein
 *   Recipe:      r,name,f1name,f1count,f2name,f2count,...
 *
 */
public class FoodCollection {

    // LinkedHashMap to preserve insertion order
    private final Map<String, Food> foods = new LinkedHashMap<>();

    public void addFood(Food food) {
        foods.put(food.getName().toLowerCase(), food);
    }

    public Food findFood(String name) {
        if (name == null) return null;
        return foods.get(name.toLowerCase());
    }

    public List<Food> getAllFoods() {
        return new ArrayList<>(foods.values());
    }

    public int size() { return foods.size(); }

    // Persistence
    //loads foods from the given CSV file with right format
    public void load(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String type = parts[0].trim();

                try {
                    if (type.equals("b") && parts.length >= 6) {
                        String name     = parts[1].trim();
                        double calories = Double.parseDouble(parts[2].trim());
                        double fat      = Double.parseDouble(parts[3].trim());
                        double carbs    = Double.parseDouble(parts[4].trim());
                        double protein  = Double.parseDouble(parts[5].trim());
                        addFood(new BasicFood(name, calories, fat, carbs, protein));

                    } else if (type.equals("r") && parts.length >= 4) {
                        String name = parts[1].trim();
                        Recipe recipe = new Recipe(name);

                        // ingridient pairs starting at index 2
                        for (int i = 2; i + 1 < parts.length; i += 2) {
                            String ingredientName = parts[i].trim();
                            double count          = Double.parseDouble(parts[i + 1].trim());
                            Food   ingredient     = findFood(ingredientName);
                            if (ingredient != null) {
                                recipe.add(ingredient, count);
                            } else {
                                System.err.println("Warning: ingredient '" + ingredientName
                                        + "' not found when loading recipe '" + name + "'");
                            }
                        }
                        addFood(recipe);
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Skipping malformed food line: " + line);
                }
            }
        }
    }

    /**
     * Saves all foods to CSV.
     */
    public void save(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            // basic foods
            for (Food food : foods.values()) {
                if (food instanceof BasicFood bf) {
                    writer.write(String.format("b,%s,%.1f,%.1f,%.1f,%.1f",
                            bf.getName(), bf.getCalories(),
                            bf.getFat(), bf.getCarbs(), bf.getProtein()));
                    writer.newLine();
                }
            }

            //  recipes while after all basic foods are loaded, so we can reference them by name
            for (Food food : foods.values()) {
                if (food instanceof Recipe recipe) {
                    StringBuilder sb = new StringBuilder("r,").append(recipe.getName());
                    List<Food>   ingredients = recipe.getIngredients();
                    List<Double> amounts     = recipe.getAmounts();
                    for (int i = 0; i < ingredients.size(); i++) {
                        sb.append(",").append(ingredients.get(i).getName());
                        sb.append(",").append(String.format("%.1f", amounts.get(i)));
                    }
                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
        }
    }
}
