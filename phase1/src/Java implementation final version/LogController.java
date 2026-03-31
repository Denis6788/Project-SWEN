
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

// made by Gabriel Muskaj
public class LogController {

    private final FoodCollection foodCollection;
    private final LogManager logManager;
    private final Scanner scanner;
    private LocalDate currentDate = LocalDate.now();

    // Constructor to initialize controller
    public LogController(FoodCollection foodCollection, LogManager logManager) {
        this.foodCollection = foodCollection;
        this.logManager = logManager;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Wellness Manager ===");

        while (true) {
            // Menu
            System.out.println("\n1. Select Date");
            System.out.println("2. Add Food");
            System.out.println("3. Delete Food");
            System.out.println("4. Show Daily Summary");
            System.out.println("5. Save");
            System.out.println("0. Exit");

            System.out.print("Choose option: ");

            int choice;
            try {
                // reading input and parsing to Int
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input!");
                continue;
            }

            // actions
            switch (choice) {
                case 1 -> selectDate();
                case 2 -> addFood();
                case 3 -> deleteFood();
                case 4 -> showSummary();
                case 5 -> save();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    // date selection
    private void selectDate() {
        try {
            System.out.print("Enter date (yyyy-mm-dd): ");
            currentDate = LocalDate.parse(scanner.nextLine().trim());

            // creating log if it does not exist
            logManager.getOrCreate(currentDate);

            System.out.println("Date set to: " + currentDate);
        } catch (Exception e) {
            System.out.println("Invalid date format!");
        }
    }

    // adding food entry to current date
    private void addFood() {
        // Get log for selected date
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.print("Enter food name: ");
        String name = scanner.nextLine().trim();

        FoodItem food = foodCollection.findFood(name);

        // if food not found, it try ignoring uppercase and lowercase
        if (food == null) {
            for (FoodItem f : foodCollection.getAllFoods()) {
                if (f.getName().equalsIgnoreCase(name)) {
                    food = f;
                    break;
                }
            }
        }

        // showing available foods, if firts item wasnt found
        if (food == null) {
            System.out.println("Food not found!");
            System.out.println("Available foods:");
            for (FoodItem f : foodCollection.getAllFoods()) {
                System.out.println("- " + f.getName());
            }
            return;
        }

        double servings;
        try {
            System.out.print("Enter servings: ");
            servings = Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid number!");
            return;
        }

        // creating and adding new log entry
        log.addEntry(new LogEntry(food, servings));
        System.out.println("Food added.");
    }

    // deleting a food entry from current date
    private void deleteFood() {
        DailyLog log = logManager.getOrCreate(currentDate);
        List<LogEntry> entries = log.getEntries();

        // checking if there are entries
        if (entries.isEmpty()) {
            System.out.println("No entries to delete.");
            return;
        }

        // showing all entries with index
        for (int i = 0; i < entries.size(); i++) {
            System.out.println(i + ": " + entries.get(i));
        }

        int index;
        try {
            System.out.print("Enter index to delete: ");
            index = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input!");
            return;
        }

        // checking valid index
        if (index < 0 || index >= entries.size()) {
            System.out.println("Invalid index!");
            return;
        }

        log.removeEntry(index);
        System.out.println("Entry removed.");
    }

    // showing summary of current date
    private void showSummary() {
        DailyLog log = logManager.getOrCreate(currentDate);

        System.out.println("\n=== DAILY SUMMARY ===");
        System.out.println("Date: " + currentDate);
        System.out.println("Weight: " + log.getWeight());
        System.out.println("Calorie limit: " + log.getCalorieLimit());

        // printing all entries
        System.out.println("\nEntries:");
        for (LogEntry e : log.getEntries()) {
            System.out.println(e);
        }

        System.out.println("\nTotal calories: " + log.getTotalCalories());

        // showing if over or under limit
        if (log.isOverCalorieLimit()) {
            System.out.println("Status: OVER calorie limit");
        } else {
            System.out.println("Status: UNDER calorie limit");
        }

        // showing nutrient percentages
        Nutrients n = log.getTotalNutrients();

        System.out.println("\nNutrients:");
        System.out.println("Fat: " + n.fatPercent() + "%");
        System.out.println("Carbs: " + n.carbPercent() + "%");
        System.out.println("Protein: " + n.proteinPercent() + "%");
    }

    // saving data to CSV file
    private void save() {
        try {
            logManager.save("log.csv");
            System.out.println("Data saved.");
        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }
}