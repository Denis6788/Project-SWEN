import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Controller for CLI (UPDATED for Part 2)
 */
public class LogController {

    private final FoodCollection foodCollection;
    private final ExerciseCollection exerciseCollection; // NEW
    private final LogManager logManager;
    private final Scanner scanner;
    private LocalDate currentDate = LocalDate.now();

    public LogController(FoodCollection foodCollection,
                         ExerciseCollection exerciseCollection, // NEW
                         LogManager logManager) {

        this.foodCollection = foodCollection;
        this.exerciseCollection = exerciseCollection; // NEW
        this.logManager = logManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Wellness Manager ===");

        while (true) {

            System.out.println("\n1. Select Date");
            System.out.println("2. Add Food");
            System.out.println("3. Delete Food");
            System.out.println("4. Show Summary");
            System.out.println("5. Save");

            // NEW options
            System.out.println("6. Add Exercise");
            System.out.println("7. Delete Exercise");

            System.out.println("0. Exit");

            System.out.print("Choose option: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input!");
                continue;
            }

            switch (choice) {
                case 1 -> selectDate();
                case 2 -> addFood();
                case 3 -> deleteFood();
                case 4 -> showSummary();
                case 5 -> save();

                // NEW
                case 6 -> addExercise();
                case 7 -> deleteExercise();

                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    // =========================
    // DATE
    // =========================
    private void selectDate() {
        try {
            System.out.print("Enter date (yyyy-mm-dd): ");
            currentDate = LocalDate.parse(scanner.nextLine().trim());

            logManager.getOrCreate(currentDate);

            System.out.println("Date set to: " + currentDate);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
        }
    }

    // =========================
    // FOOD
    // =========================
    private void addFood() {

        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.print("Enter food name: ");
        String name = scanner.nextLine().trim();

        FoodItem food = foodCollection.findFood(name);

        if (food == null) {
            System.out.println("Food not found!");
            return;
        }

        try {
            System.out.print("Enter servings: ");
            double servings = Double.parseDouble(scanner.nextLine());

            log.addEntry(new LogEntry(food, servings));
            System.out.println("Food added.");

        } catch (Exception e) {
            System.out.println("Invalid number!");
        }
    }

    private void deleteFood() {

        DailyLog log = logManager.getOrCreate(currentDate);
        List<LogEntry> entries = log.getEntries();

        if (entries.isEmpty()) {
            System.out.println("No entries.");
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            System.out.println(i + ": " + entries.get(i));
        }

        try {
            System.out.print("Index to delete: ");
            int index = Integer.parseInt(scanner.nextLine());

            log.removeEntry(index);
            System.out.println("Removed.");

        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // =========================
    // NEW: EXERCISE
    // =========================
    private void addExercise() {

        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.print("Enter exercise name: ");
        String name = scanner.nextLine().trim();

        Exercise ex = exerciseCollection.find(name);

        if (ex == null) {
            System.out.println("Exercise not found!");
            return;
        }

        try {
            System.out.print("Enter minutes: ");
            double minutes = Double.parseDouble(scanner.nextLine());

            log.addExercise(new ExerciseEntry(ex, minutes));
            System.out.println("Exercise added.");

        } catch (Exception e) {
            System.out.println("Invalid number!");
        }
    }

    private void deleteExercise() {

        DailyLog log = logManager.getOrCreate(currentDate);
        List<ExerciseEntry> list = log.getExercises();

        if (list.isEmpty()) {
            System.out.println("No exercises.");
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ": " + list.get(i));
        }

        try {
            System.out.print("Index to delete: ");
            int index = Integer.parseInt(scanner.nextLine());

            log.removeExercise(index);
            System.out.println("Removed.");

        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    // =========================
    // SUMMARY (UPDATED)
    // =========================
    private void showSummary() {

        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n=== SUMMARY ===");
        System.out.println("Date: " + currentDate);

        System.out.println("\n--- FOODS ---");
        for (LogEntry e : log.getEntries()) {
            System.out.println(e);
        }

        System.out.println("\n--- EXERCISES ---");
        for (ExerciseEntry e : log.getExercises()) {
            System.out.println(e);
        }

        double consumed = log.getTotalCalories();
        double burned = log.getBurnedCalories();
        double net = log.getNetCalories();

        System.out.println("\nConsumed: " + consumed);
        System.out.println("Burned: " + burned);
        System.out.println("Net: " + net);

        System.out.println("Goal: " + log.getCalorieLimit());

        double diff = log.getCalorieLimit() - net;
        System.out.println("Remaining: " + diff);

        System.out.println("Weight: " + log.getWeight());

        Nutrients n = log.getTotalNutrients();

        System.out.println("\nNutrients:");
        System.out.println("Fat: " + n.fatPercent() + "%");
        System.out.println("Carbs: " + n.carbPercent() + "%");
        System.out.println("Protein: " + n.proteinPercent() + "%");
    }

    // =========================
    // SAVE
    // =========================
    private void save() {
        try {
            logManager.save("log.csv");
            exerciseCollection.save("exercise.csv"); // NEW
            System.out.println("Saved.");
        } catch (Exception e) {
            System.out.println("Error saving.");
        }
    }
}