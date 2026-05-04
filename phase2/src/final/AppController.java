import java.time.LocalDate;

/**
 * Application level controller 
 * Coordinates FoodCollection, ExerciseCollection, and LogManager.
 */
public class AppController {

    private final FoodCollection     foodCollection;
    private final LogManager         logManager;
    private final ExerciseCollection exerciseCollection;
    private LocalDate currentDate;

    public AppController(FoodCollection foodCollection,
                         LogManager logManager,
                         ExerciseCollection exerciseCollection) {

        if (foodCollection == null || logManager == null || exerciseCollection == null) {
            throw new IllegalArgumentException("Collections cannot be null");
        }

        this.foodCollection     = foodCollection;
        this.logManager         = logManager;
        this.exerciseCollection = exerciseCollection;
        this.currentDate        = LocalDate.now();

        logManager.getOrCreate(currentDate);
    }

    public void setDate(LocalDate date) {
        currentDate = date;
        logManager.getOrCreate(date);
    }

    public void addFood(String name, double servings) {
        Food food = foodCollection.findFood(name);
        if (food == null) {
            System.out.println("Food not found: " + name);
            return;
        }
        logManager.getOrCreate(currentDate).addEntry(new LogEntry(food, servings));
        System.out.println("Added food: " + name);
    }

    public void addExercise(String name, double minutes) {
        Exercise ex = exerciseCollection.find(name);
        if (ex == null) {
            System.out.println("Exercise not found: " + name);
            return;
        }
        logManager.getOrCreate(currentDate).addExercise(new ExerciseEntry(ex, minutes));
        System.out.println("Added exercise: " + name);
    }

    public void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n===== DAILY SUMMARY =====");
        System.out.println("Date: " + currentDate);

        System.out.println("\nFOODS:");
        if (log.getEntries().isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (LogEntry e : log.getEntries()) {
                System.out.println("  " + e);
            }
        }

        // showing all exercise entries
        System.out.println("\nEXERCISES:");
        if (log.getExercises().isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (ExerciseEntry e : log.getExercises()) {
                double burned = e.caloriesBurned(log.getWeight());
                System.out.printf("  %s — %.0f cal burned%n",
                        e, Math.round(burned));
            }
        }

        // summary
        System.out.printf("%nConsumed: %.1f cal%n", log.getTotalCalories());
        System.out.printf("Burned:   %.1f cal%n",   log.getBurnedCalories());
        System.out.printf("Net:      %.1f cal%n",   log.getNetCalories());

        // Showing calorie goal if it exists
        if (log.hasCalorieLimit()) {
            double diff = log.getCalorieLimit() - log.getNetCalories();
            System.out.printf("Goal:     %.0f cal%n", log.getCalorieLimit());
            if (diff >= 0) {
                System.out.printf("Remaining: %.1f cal%n", diff);
            } else {
                System.out.printf("Over by:   %.1f cal%n", -diff);
            }
        } else {
            System.out.println("Calorie Goal: not set");
        }

        System.out.printf("Weight:   %.1f lbs%n", log.getWeight());

        // shwoing nutrient breakdown if data exists
        Nutrients n = log.getTotalNutrients();
        if (n.totalGrams() > 0) {
            System.out.println("\nNutrient breakdown (% of total grams):");
            System.out.printf("  Fat:     %.0f%%%n", n.fatPercent());
            System.out.printf("  Carbs:   %.0f%%%n", n.carbPercent());
            System.out.printf("  Protein: %.0f%%%n", n.proteinPercent());
        }
    }
        //saving all data to csv files
    public void save() {
        try {
            foodCollection.save("foods.csv");
            exerciseCollection.save("exercise.csv");
            logManager.save("log.csv");
            System.out.println("Saved successfully.");
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }
}
