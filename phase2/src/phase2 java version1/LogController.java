import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * as an alternative CLI controller. Main.java is the primary entry point;
 * i keept this class as a backup for testing
 */
public class LogController {

    private final FoodCollection     foodCollection;
    private final ExerciseCollection exerciseCollection;
    private final LogManager         logManager;
    private final Scanner            scanner;
    private LocalDate currentDate = LocalDate.now();

    public LogController(FoodCollection foodCollection,
                         ExerciseCollection exerciseCollection,
                         LogManager logManager) {
        this.foodCollection     = foodCollection;
        this.exerciseCollection = exerciseCollection;
        this.logManager         = logManager;
        this.scanner            = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Wellness Manager ===");

        while (true) {
            System.out.println("\n1. Select Date");
            System.out.println("2. Add Food");
            System.out.println("3. Delete Food");
            System.out.println("4. Add Exercise");
            System.out.println("5. Delete Exercise");
            System.out.println("6. Show Summary");
            System.out.println("7. Save");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                continue;
            }

            switch (choice) {
                case 1 -> selectDate();
                case 2 -> addFood();
                case 3 -> deleteFood();
                case 4 -> addExercise();
                case 5 -> deleteExercise();
                case 6 -> showSummary();
                case 7 -> save();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void selectDate() {
        System.out.print("Enter date (yyyy-mm-dd): ");
        try {
            currentDate = LocalDate.parse(scanner.nextLine().trim());
            logManager.getOrCreate(currentDate);
            System.out.println("Date set to: " + currentDate);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }
    }

    private void addFood() {
        DailyLog log = logManager.getOrCreate(currentDate);
        System.out.print("Enter food name: ");
        String name = scanner.nextLine().trim();

        Food food = foodCollection.findFood(name);
        if (food == null) {
            System.out.println("Food not found.");
            return;
        }

        try {
            System.out.print("Enter servings: ");
            double servings = Double.parseDouble(scanner.nextLine().trim());
            log.addEntry(new LogEntry(food, servings));
            System.out.println("Food added.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void deleteFood() {
        DailyLog log = logManager.getOrCreate(currentDate);
        List<LogEntry> entries = log.getEntries();

        if (entries.isEmpty()) {
            System.out.println("No food entries.");
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            System.out.println(i + ": " + entries.get(i));
        }

        System.out.print("Index to delete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            log.removeEntry(index);
            System.out.println("Removed.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void addExercise() {
        DailyLog log = logManager.getOrCreate(currentDate);
        System.out.print("Enter exercise name: ");
        String name = scanner.nextLine().trim();

        Exercise ex = exerciseCollection.find(name);
        if (ex == null) {
            System.out.println("Exercise not found.");
            return;
        }

        try {
            System.out.print("Enter minutes: ");
            double minutes = Double.parseDouble(scanner.nextLine().trim());
            log.addExercise(new ExerciseEntry(ex, minutes));
            System.out.println("Exercise added.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void deleteExercise() {
        DailyLog log = logManager.getOrCreate(currentDate);
        List<ExerciseEntry> list = log.getExercises();

        if (list.isEmpty()) {
            System.out.println("No exercise entries.");
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ": " + list.get(i));
        }

        System.out.print("Index to delete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim());
            log.removeExercise(index);
            System.out.println("Removed.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n=== SUMMARY ===");
        System.out.println("Date: " + currentDate);

        System.out.println("\n--- FOODS ---");
        for (LogEntry e : log.getEntries()) System.out.println("  " + e);

        System.out.println("\n--- EXERCISES ---");
        for (ExerciseEntry e : log.getExercises()) {
            System.out.printf("  %s — %.0f cal burned%n",
                    e, Math.round(e.caloriesBurned(log.getWeight())));
        }

        double consumed = log.getTotalCalories();
        double burned   = log.getBurnedCalories();
        double net      = log.getNetCalories();

        System.out.printf("%nConsumed: %.1f | Burned: %.1f | Net: %.1f%n",
                consumed, burned, net);
        System.out.printf("Goal: %.0f | Remaining: %.1f%n",
                log.getCalorieLimit(), log.getCalorieLimit() - net);
        System.out.printf("Weight: %.1f lbs%n", log.getWeight());

        Nutrients n = log.getTotalNutrients();
        if (n.totalGrams() > 0) {
            System.out.printf("%nFat: %.0f%% | Carbs: %.0f%% | Protein: %.0f%%%n",
                    n.fatPercent(), n.carbPercent(), n.proteinPercent());
        }
    }

    private void save() {
        try {
            foodCollection.save("foods.csv");
            exerciseCollection.save("exercise.csv");
            logManager.save("log.csv");
            System.out.println("Saved.");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
}
