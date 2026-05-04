import java.time.LocalDate;
import java.util.Scanner;

/**
 * Entry point for the command line version of Wellness Manager.
 * Loads data from CSV files, runs an interactive menu, and saves on exit.
 */
public class Main {

    private static FoodCollection     foods;
    private static ExerciseCollection exercises;
    private static LogManager         logManager;
    private static LocalDate          currentDate = LocalDate.now();
    private static final Scanner      scanner     = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        runMenu();
    }

    //data loading and saving methods

    private static void loadData() {
        foods     = new FoodCollection();
        exercises = new ExerciseCollection();
        logManager = new LogManager();
        logManager.setExerciseCollection(exercises);

        try {
            foods.load("foods.csv");
            System.out.println("Loaded " + foods.size() + " food(s).");
        } catch (Exception e) {
            System.out.println("Could not load foods.csv: " + e.getMessage());
        }

        try {
            exercises.load("exercise.csv");
            System.out.println("Loaded " + exercises.size() + " exercise(s).");
        } catch (Exception e) {
            System.out.println("Could not load exercise.csv: " + e.getMessage());
        }

        try {
            logManager.load("log.csv", foods);
            System.out.println("Log loaded.");
        } catch (Exception e) {
            System.out.println("Could not load log.csv: " + e.getMessage());
        }
    }

    //Menu and actions
    private static void runMenu() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         WELLNESS MANAGER");
            System.out.println("========================================");
            System.out.println("Current date: " + currentDate);
            System.out.println("----------------------------------------");
            System.out.println("1. Select Date");
            System.out.println("2. Add Food");
            System.out.println("3. Add Exercise");
            System.out.println("4. Set Weight (lbs)");
            System.out.println("5. Set Calorie Goal");
            System.out.println("6. Show Summary");
            System.out.println("7. Delete Entry");
            System.out.println("8. Save");
            System.out.println("0. Exit");
            System.out.println("========================================");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> selectDate();
                case 2 -> addFood();
                case 3 -> addExercise();
                case 4 -> setWeight();
                case 5 -> setCalorieGoal();
                case 6 -> showSummary();
                case 7 -> deleteEntry();
                case 8 -> save();
                case 0 -> {
                    save();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    //Menu action methods
    private static void selectDate() {
        System.out.print("Enter date (yyyy-mm-dd): ");
        try {
            currentDate = LocalDate.parse(scanner.nextLine().trim());
            logManager.getOrCreate(currentDate);
            System.out.println("Date set to: " + currentDate);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use yyyy-mm-dd.");
        }
    }

    private static void addFood() {
        System.out.print("Food name: ");
        String name = scanner.nextLine().trim();

        Food food = foods.findFood(name);
        if (food == null) {
            System.out.println("Food not found: '" + name + "'");
            return;
        }

        System.out.print("Servings: ");
        try {
            double servings = Double.parseDouble(scanner.nextLine().trim());
            if (servings <= 0) { System.out.println("Servings must be positive."); return; }
            logManager.getOrCreate(currentDate).addEntry(new LogEntry(food, servings));
            System.out.printf("Added: %s x %.1f%n", food.getName(), servings);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private static void addExercise() {
        System.out.print("Exercise name: ");
        String name = scanner.nextLine().trim();

        Exercise exercise = exercises.find(name);
        if (exercise == null) {
            System.out.println("Exercise not found: '" + name + "'");
            return;
        }

        System.out.print("Minutes: ");
        try {
            double minutes = Double.parseDouble(scanner.nextLine().trim());
            if (minutes <= 0) { System.out.println("Minutes must be positive."); return; }
            logManager.getOrCreate(currentDate).addExercise(new ExerciseEntry(exercise, minutes));
            System.out.printf("Added: %s x %.1f min%n", exercise.getName(), minutes);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private static void setWeight() {
        System.out.print("Weight (lbs): ");
        try {
            double weight = Double.parseDouble(scanner.nextLine().trim());
            if (weight <= 0) { System.out.println("Weight must be positive."); return; }
            logManager.getOrCreate(currentDate).setWeight(weight);
            System.out.printf("Weight set to %.1f lbs.%n", weight);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private static void setCalorieGoal() {
        System.out.print("Calorie goal: ");
        try {
            double goal = Double.parseDouble(scanner.nextLine().trim());
            if (goal < 0) { System.out.println("Calorie goal cannot be negative."); return; }
            logManager.getOrCreate(currentDate).setCalorieLimit(goal);
            System.out.printf("Calorie goal set to %.0f.%n", goal);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private static void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n========================================");
        System.out.println("           DAILY SUMMARY");
        System.out.println("========================================");
        System.out.println("Date: " + currentDate);

        System.out.println("\n--- FOODS ---");
        if (log.getEntries().isEmpty()) {
            System.out.println("  (no food logged)");
        } else {
            for (LogEntry e : log.getEntries()) {
                System.out.printf("  %s%n", e);
            }
        }

        System.out.println("\n--- EXERCISES ---");
        if (log.getExercises().isEmpty()) {
            System.out.println("  (no exercise logged)");
        } else {
            for (ExerciseEntry e : log.getExercises()) {
                double burned = e.caloriesBurned(log.getWeight());
                System.out.printf("  %s — %.0f cal burned%n", e, Math.round(burned));
            }
        }

        System.out.println("\n--- TOTALS ---");
        double consumed = log.getTotalCalories();
        double burned   = log.getBurnedCalories();
        double net      = log.getNetCalories();

        System.out.printf("  Consumed: %.1f cal%n", consumed);
        System.out.printf("  Burned:   %.1f cal%n", burned);
        System.out.printf("  Net:      %.1f cal%n", net);

        if (log.hasCalorieLimit()) {
            double diff = log.getCalorieLimit() - net;
            System.out.printf("  Goal:     %.0f cal%n", log.getCalorieLimit());
            if (diff >= 0) {
                System.out.printf("  Remaining: %.1f cal  [UNDER GOAL]%n", diff);
            } else {
                System.out.printf("  Over by:   %.1f cal  [OVER GOAL]%n", -diff);
            }
        } else {
            System.out.println("  Goal: not set");
        }

        System.out.printf("  Weight: %.1f lbs%n", log.getWeight());

        Nutrients n = log.getTotalNutrients();
        if (n.totalGrams() > 0) {
            System.out.println("\n--- NUTRIENTS (% of total grams) ---");
            System.out.printf("  Fat:     %.0f%%%n", n.fatPercent());
            System.out.printf("  Carbs:   %.0f%%%n", n.carbPercent());
            System.out.printf("  Protein: %.0f%%%n", n.proteinPercent());
        }

        System.out.println("========================================");
    }

    private static void deleteEntry() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\nSelect entry to delete:");
        System.out.println("FOODS:");

        int index = 1;
        if (log.getEntries().isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (LogEntry e : log.getEntries()) {
                System.out.printf("  %d. %s%n", index++, e);
            }
        }

        int foodCount = log.getEntries().size();

        System.out.println("\nEXERCISES:");
        if (log.getExercises().isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (ExerciseEntry e : log.getExercises()) {
                System.out.printf("  %d. %s%n", index++, e);
            }
        }

        System.out.print("\nEnter number to delete (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;

            if (choice >= 1 && choice <= foodCount) {
                LogEntry removed = log.getEntries().remove(choice - 1);
                System.out.println("Deleted: " + removed);
            } else if (choice <= foodCount + log.getExercises().size()) {
                ExerciseEntry removed = log.getExercises().remove(choice - foodCount - 1);
                System.out.println("Deleted: " + removed);
            } else {
                System.out.println("Invalid number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void save() {
        try {
            foods.save("foods.csv");
            exercises.save("exercise.csv");
            logManager.save("log.csv");
            System.out.println("Saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
}
