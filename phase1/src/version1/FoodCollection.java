import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Manages all foods in the system.
 * Loads from and saves to foods.csv.
 *
 * CSV format:
 *   b,name,calories,fat,carb,protein
 *   r,name,f1name,f1count,f2name,f2count,...
 *
 * No forward references — recipe ingredients must appear earlier in the file.
 */
public class FoodCollection {

    private final LinkedHashMap<String, FoodItem> foods = new LinkedHashMap<>();
    private final List<Runnable> changeListeners = new ArrayList<>();

    public void addChangeListener(Runnable listener) { changeListeners.add(listener); }
    private void notifyListeners() { for (Runnable r : changeListeners) r.run(); }

    // Collection operations 

    public void addFood(FoodItem food) {
        if (foods.containsKey(food.getName()))
            throw new IllegalArgumentException("Duplicate food name: " + food.getName());
        foods.put(food.getName(), food);
        notifyListeners();
    }

    public FoodItem findFood(String name)    { return foods.get(name); }
    public List<FoodItem> getAllFoods()       { return new ArrayList<>(foods.values()); }
    public int size()                         { return foods.size(); }

    //  Load from CSV 

    public void load(String filePath) throws IOException {
        foods.clear();
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                String type = parts[0].trim();

                if (type.equals("b"))      parseBasicFood(parts, lineNum);
                else if (type.equals("r")) parseRecipe(parts, lineNum);
                else throw new IllegalArgumentException(
                    "Line " + lineNum + ": unknown type '" + type + "'");
            }
        }
        notifyListeners();
    }

    private void parseBasicFood(String[] parts, int lineNum) {
        if (parts.length != 6)
            throw new IllegalArgumentException(
                "Line " + lineNum + ": basic food needs 6 fields, got " + parts.length);
        String name = parts[1].trim();
        checkNewName(name, lineNum);
        try {
            double cal     = Double.parseDouble(parts[2].trim());
            double fat     = Double.parseDouble(parts[3].trim());
            double carb    = Double.parseDouble(parts[4].trim());
            double protein = Double.parseDouble(parts[5].trim());
            foods.put(name, new BasicFood(name, cal, fat, carb, protein));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Line " + lineNum + ": bad number — " + e.getMessage());
        }
    }

    private void parseRecipe(String[] parts, int lineNum) {
        if (parts.length < 4 || (parts.length - 2) % 2 != 0)
            throw new IllegalArgumentException(
                "Line " + lineNum + ": recipe needs name + pairs of ingredient/count");
        String name = parts[1].trim();
        checkNewName(name, lineNum);
        Recipe recipe = new Recipe(name);
        for (int i = 2; i < parts.length; i += 2) {
            String ingName = parts[i].trim();
            FoodItem ing = foods.get(ingName);
            if (ing == null)
                throw new IllegalArgumentException(
                    "Line " + lineNum + ": ingredient '" + ingName + "' not found (forward reference?)");
            try {
                double servings = Double.parseDouble(parts[i + 1].trim());
                recipe.addIngredient(ing, servings);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Line " + lineNum + ": bad serving count for '" + ingName + "'");
            }
        }
        foods.put(name, recipe);
    }

    private void checkNewName(String name, int lineNum) {
        if (name.isEmpty())
            throw new IllegalArgumentException("Line " + lineNum + ": name cannot be empty");
        if (foods.containsKey(name))
            throw new IllegalArgumentException("Line " + lineNum + ": duplicate name '" + name + "'");
    }

    //  Save to CSV 

    /**
     * Saves all foods to foods.csv.
     * LinkedHashMap preserves insertion order, which already satisfies
     * the no-forward-reference constraint since load() enforces it.
     */
    public void save(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (FoodItem food : foods.values()) {
                writer.write(food.toCSV());
                writer.newLine();
            }
        }
    }
}