import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all daily logs.
 * Loads from and saves to log.csv.
 */
public class LogManager {

    // storing logs by date 
    private final Map<LocalDate, DailyLog> logs = new HashMap<>();

    public DailyLog getOrCreate(LocalDate date) {
        return logs.computeIfAbsent(date, d -> new DailyLog(d));
    }

    // getting log 
    public DailyLog getLog(LocalDate date) {
        return logs.get(date);
    }

    // reading log.csv and filling the logs map, also clearing old logs
    public void load(String filePath, FoodCollection foodCollection) throws IOException {
        logs.clear(); 

        File file = new File(filePath);
        if (!file.exists()) return; 
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                int year  = Integer.parseInt(parts[0].trim());
                int month = Integer.parseInt(parts[1].trim());
                int day   = Integer.parseInt(parts[2].trim());

                LocalDate date = LocalDate.of(year, month, day);
                String type = parts[3].trim(); 

                DailyLog log = getOrCreate(date);

                switch (type) {
                    case "w":
                        // weight entry
                        double weight = Double.parseDouble(parts[4].trim());
                        log.setWeight(weight);
                        break;

                    case "c":
                        // calorie limit entry
                        double calories = Double.parseDouble(parts[4].trim());
                        log.setCalorieLimit(calories);
                        break;

                    case "f":
                        // food entry
                        String foodName = parts[4].trim();
                        double servings = Double.parseDouble(parts[5].trim());
                        FoodItem food = foodCollection.findFood(foodName);
                        if (food != null) {
                            log.addEntry(new LogEntry(food, servings));
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown type: " + type);
                }
            }
        }

        applyDefaults();
    }

    // applying rules for weight and calorie limit if missing
    private void applyDefaults() {
        double lastWeight = 150.0;     
        double lastCalories = 2000.0;  

        List<LocalDate> dates = new ArrayList<>(logs.keySet());
        Collections.sort(dates);

        for (LocalDate date : dates) {
            DailyLog log = logs.get(date);

            // If no weight and calorie limit, it uses last known weight and limit
            if (!log.hasWeight()) {
                log.setWeight(lastWeight);
            } else {
                lastWeight = log.getWeight();
            }

            if (!log.hasCalorieLimit()) {
                log.setCalorieLimit(lastCalories);
            } else {
                lastCalories = log.getCalorieLimit();
            }
        }
    }

    // writting all logs into log.csv
    public void save(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            // sorting dates before saving
            List<LocalDate> dates = new ArrayList<>(logs.keySet());
            Collections.sort(dates);

            for (LocalDate date : dates) {
                DailyLog log = logs.get(date);

                int y = date.getYear();
                int m = date.getMonthValue();
                int d = date.getDayOfMonth();

                // writting weight
                writer.write(String.format("%d,%02d,%02d,w,%.1f", y, m, d, log.getWeight()));
                writer.newLine();

                // writing calorie limit
                writer.write(String.format("%d,%02d,%02d,c,%.1f", y, m, d, log.getCalorieLimit()));
                writer.newLine();

                // writing each food entry
                for (LogEntry entry : log.getEntries()) {
                    writer.write(String.format("%d,%02d,%02d,f,%s,%.1f",
                            y, m, d,
                            entry.getFood().getName(),
                            entry.getServings()));
                    writer.newLine();
                }
            }
        }
    }
}