import java.time.LocalDate;

public class AppController {  //made by Denis Keselj

    private final FoodCollection foodCollection;
    private final LogManager logManager;
    private LocalDate currentDate;

    public AppController(FoodCollection foodCollection, LogManager logManager) {
        this.foodCollection = foodCollection;
        this.logManager = logManager;
        this.currentDate = LocalDate.now();

        logManager.getOrCreate(currentDate);
    }

    public void setDate(LocalDate date) {
        this.currentDate = date;
        logManager.getOrCreate(date);
    }

    public void addFood(String name, double servings) {
        FoodItem food = foodCollection.findFood(name);

        if (food == null) {
            System.out.println("Food not found!");
            return;
        }

        logManager.addEntry(currentDate, food, servings);
        System.out.println("Added: " + name);
    }

    public void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n=== DATE: " + currentDate + " ===");

        int i = 0;
        for (LogEntry e : log.getEntries()) {
            System.out.println(i + ": " + e);
            i++;
        }

        double totalCalories = log.getTotalCalories();
        System.out.println("\nTotal calories: " + totalCalories);
        System.out.println("Limit: " + log.getCalorieLimit());

        if (totalCalories > log.getCalorieLimit()) {
            System.out.println("⚠ Over calorie limit!");
        } else {
            System.out.println("✔ Within calorie limit");
        }

        System.out.println("Weight: " + log.getWeight());

        Nutrients n = log.getTotalNutrients();
        System.out.println("Nutrients: " + n);
        System.out.println("Fat: " + n.fatPercent() + "%");
        System.out.println("Carbs: " + n.carbPercent() + "%");
        System.out.println("Protein: " + n.proteinPercent() + "%");
    }

    public void save() throws Exception {
        foodCollection.save("foods.csv");
        logManager.save("log.csv");
        System.out.println("Saved successfully!");
    }
}