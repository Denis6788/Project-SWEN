import java.time.LocalDate;

public class AppController {

    private final FoodCollection foodCollection;
    private final LogManager logManager;
    private final ExerciseCollection exerciseCollection; // NEW
    private LocalDate currentDate;

    public AppController(FoodCollection foodCollection, LogManager logManager, ExerciseCollection ec) {
        this.foodCollection = foodCollection;
        this.logManager = logManager;
        this.exerciseCollection = ec; // NEW
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

        logManager.getOrCreate(currentDate).addEntry(new LogEntry(food, servings));
        System.out.println("Added food: " + name);
    }

    // NEW
    public void addExercise(String name, double minutes) {
        Exercise ex = exerciseCollection.find(name);

        if (ex == null) {
            System.out.println("Exercise not found!");
            return;
        }

        logManager.getOrCreate(currentDate).addExercise(new ExerciseEntry(ex, minutes));
        System.out.println("Added exercise: " + name);
    }

    public void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n=== DATE: " + currentDate + " ===");

        System.out.println("\n--- FOODS ---");
        for (LogEntry e : log.getEntries()) {
            System.out.println(e);
        }

        System.out.println("\n--- EXERCISES ---"); // NEW
        for (ExerciseEntry e : log.getExercises()) {
            System.out.println(e);
        }

        double consumed = log.getTotalCalories();
        double burned = log.getBurnedCalories(); // NEW
        double net = log.getNetCalories();       // NEW

        System.out.println("\nConsumed: " + consumed);
        System.out.println("Burned: " + burned);
        System.out.println("Net: " + net);

        System.out.println("Goal: " + log.getCalorieLimit());

        double diff = log.getCalorieLimit() - net; // NEW
        System.out.println("Remaining: " + diff);

        System.out.println("Weight: " + log.getWeight());

        Nutrients n = log.getTotalNutrients();
        System.out.println("Fat: " + n.fatPercent() + "%");
        System.out.println("Carbs: " + n.carbPercent() + "%");
        System.out.println("Protein: " + n.proteinPercent() + "%");
    }

    public void save() throws Exception {
        foodCollection.save("foods.csv");
        exerciseCollection.save("exercise.csv"); // NEW
        logManager.save("log.csv");

        System.out.println("Saved successfully!");
    }
}