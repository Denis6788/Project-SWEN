import java.time.LocalDate;

public class TestLogManager {

    public static void main(String[] args) {

        try {
            FoodCollection foods = new FoodCollection();
            foods.load("../foods.csv");

            LogManager logManager = new LogManager();
            logManager.load("../log.csv", foods);

            LocalDate date = LocalDate.of(2026, 3, 22);

            DailyLog log = logManager.getLog(date);

            System.out.println("=== DAILY LOG ===");
            System.out.println("Date: " + date);
            System.out.println("Weight: " + log.getWeight());
            System.out.println("Calorie limit: " + log.getCalorieLimit());

            System.out.println("\nEntries:");
            for (LogEntry e : log.getEntries()) {
                System.out.println(e);
            }

            System.out.println("\nTotal calories: " + log.getTotalCalories());
            System.out.println("Over limit: " + log.isOverCalorieLimit());

            System.out.println("\nNutrients:");
            System.out.println(log.getTotalNutrients());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}