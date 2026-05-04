import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Manages the collection of DailyLog objects.
 * Loads and saves to log.csv in the format specified by the project.
 */
public class LogManager {

    private final Map<LocalDate, DailyLog> logs = new TreeMap<>(); 
    private ExerciseCollection exerciseCollection;

    public void setExerciseCollection(ExerciseCollection ec) {
        this.exerciseCollection = ec;
    }


     // accessors
    public DailyLog getOrCreate(LocalDate date) {
        if (!logs.containsKey(date)) {
            DailyLog newLog = new DailyLog(date);
            applyMostRecentDefaults(newLog, date);
            logs.put(date, newLog);
        }
        return logs.get(date);
    }

    public DailyLog getLog(LocalDate date) {
        return logs.get(date);
    }

    //loading the logs from the given CSV file, and populating the logs map
    public void load(String filePath, FoodCollection foodCollection) throws IOException {
        logs.clear();

        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // spliting on first 4 commas only
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                try {
                    int  year  = Integer.parseInt(parts[0].trim());
                    int  month = Integer.parseInt(parts[1].trim());
                    int  day   = Integer.parseInt(parts[2].trim());
                    String type = parts[3].trim();

                    LocalDate date = LocalDate.of(year, month, day);
                    DailyLog  log  = logs.computeIfAbsent(date, DailyLog::new);

                    switch (type) {
                        case "w":
                            if (parts.length > 4) {
                                log.setWeight(Double.parseDouble(parts[4].trim()));
                            }
                            break;

                        case "c":
                            if (parts.length > 4) {
                                log.setCalorieLimit(Double.parseDouble(parts[4].trim()));
                            }
                            break;

                        case "f":
                            if (foodCollection != null && parts.length > 5) {
                                String foodName = parts[4].trim();
                                double servings = Double.parseDouble(parts[5].trim());
                                Food food = foodCollection.findFood(foodName);
                                if (food != null) {
                                    log.addEntry(new LogEntry(food, servings));
                                } else {
                                    System.err.println("Warning: food '" + foodName
                                            + "' not found while loading log.");
                                }
                            }
                            break;

                        case "e":
                            if (exerciseCollection != null && parts.length > 5) {
                                String exName = parts[4].trim();
                                double minutes = Double.parseDouble(parts[5].trim());
                                Exercise ex = exerciseCollection.find(exName);
                                if (ex != null) {
                                    log.addExercise(new ExerciseEntry(ex, minutes));
                                } else {
                                    System.err.println("Warning: exercise '" + exName
                                            + "' not found while loading log.");
                                }
                            }
                            break;

                        default:
                            break;
                    }

                } catch (Exception ex) {
                    System.err.println("Skipping malformed log line: " + line);
                }
            }
        }
    }

    //saving all logs to the given CSV file in the specified format
    public void save(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Map.Entry<LocalDate, DailyLog> entry : logs.entrySet()) {
                LocalDate date = entry.getKey();
                DailyLog  log  = entry.getValue();

                int y = date.getYear();
                int m = date.getMonthValue();
                int d = date.getDayOfMonth();

                if (log.hasWeight()) {
                    writer.write(String.format("%d,%02d,%02d,w,%.1f", y, m, d, log.getWeight()));
                    writer.newLine();
                }

                if (log.hasCalorieLimit()) {
                    writer.write(String.format("%d,%02d,%02d,c,%.1f", y, m, d, log.getCalorieLimit()));
                    writer.newLine();
                }

                for (LogEntry le : log.getEntries()) {
                    writer.write(String.format("%d,%02d,%02d,f,%s,%.1f",
                            y, m, d, le.getFood().getName(), le.getServings()));
                    writer.newLine();
                }

                for (ExerciseEntry ee : log.getExercises()) {
                    writer.write(String.format("%d,%02d,%02d,e,%s,%.1f",
                            y, m, d, ee.getExercise().getName(), ee.getMinutes()));
                    writer.newLine();
                }
            }
        }
    }

    // helper method to apply the most recent weight and calorie limit defaults to a new log
    private void applyMostRecentDefaults(DailyLog newLog, LocalDate date) {
        Double lastWeight  = null;
        Double lastCalories = null;

        for (Map.Entry<LocalDate, DailyLog> e : logs.entrySet()) {
            if (!e.getKey().isBefore(date)) continue; // only look at earlier dates
            DailyLog prior = e.getValue();
            if (prior.hasWeight())        lastWeight   = prior.getWeight();
            if (prior.hasCalorieLimit())  lastCalories = prior.getCalorieLimit();
        }

        if (lastWeight   != null) newLog.setWeight(lastWeight);
        if (lastCalories != null) newLog.setCalorieLimit(lastCalories);
    }
}
