import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Manages all daily logs.
 * UPDATED: now supports exercises
 */
public class LogManager {

    private final Map<LocalDate, DailyLog> logs = new HashMap<>();

    // NEW: reference to exercises
    private ExerciseCollection exerciseCollection;

    // NEW: setter to connect exercises
    public void setExerciseCollection(ExerciseCollection ec) {
        this.exerciseCollection = ec;
    }

    public DailyLog getOrCreate(LocalDate date) {
        return logs.computeIfAbsent(date, d -> new DailyLog(d));
    }

    public DailyLog getLog(LocalDate date) {
        return logs.get(date);
    }

    // UPDATED: now loads exercises too
    public void load(String filePath, FoodCollection foodCollection) throws IOException {
        logs.clear();

        File file = new File(filePath);
        if (!file.exists()) return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(",");

            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            LocalDate date = LocalDate.of(year, month, day);
            String type = parts[3];

            DailyLog log = getOrCreate(date);

            switch (type) {

                case "w":
                    log.setWeight(Double.parseDouble(parts[4]));
                    break;

                case "c":
                    log.setCalorieLimit(Double.parseDouble(parts[4]));
                    break;

                case "f":
                    String foodName = parts[4];
                    double servings = Double.parseDouble(parts[5]);

                    FoodItem food = foodCollection.findFood(foodName);
                    if (food != null) {
                        log.addEntry(new LogEntry(food, servings));
                    }
                    break;

                // NEW: load exercise
                case "e":
                    if (exerciseCollection != null) {
                        String exName = parts[4];
                        double minutes = Double.parseDouble(parts[5]);

                        Exercise ex = exerciseCollection.find(exName);
                        if (ex != null) {
                            log.addExercise(new ExerciseEntry(ex, minutes));
                        }
                    }
                    break;
            }
        }

        reader.close();
        applyDefaults();
    }

    private void applyDefaults() {
        double lastWeight = 150.0;
        double lastCalories = 2000.0;

        List<LocalDate> dates = new ArrayList<>(logs.keySet());
        Collections.sort(dates);

        for (LocalDate date : dates) {
            DailyLog log = logs.get(date);

            if (!log.hasWeight()) log.setWeight(lastWeight);
            else lastWeight = log.getWeight();

            if (!log.hasCalorieLimit()) log.setCalorieLimit(lastCalories);
            else lastCalories = log.getCalorieLimit();
        }
    }

    // UPDATED: now saves exercises too
    public void save(String filePath) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        List<LocalDate> dates = new ArrayList<>(logs.keySet());
        Collections.sort(dates);

        for (LocalDate date : dates) {

            DailyLog log = logs.get(date);

            int y = date.getYear();
            int m = date.getMonthValue();
            int d = date.getDayOfMonth();

            writer.write(String.format("%d,%02d,%02d,w,%.1f", y, m, d, log.getWeight()));
            writer.newLine();

            writer.write(String.format("%d,%02d,%02d,c,%.1f", y, m, d, log.getCalorieLimit()));
            writer.newLine();

            for (LogEntry entry : log.getEntries()) {
                writer.write(String.format("%d,%02d,%02d,f,%s,%.1f",
                        y, m, d,
                        entry.getFood().getName(),
                        entry.getServings()));
                writer.newLine();
            }

            // NEW: save exercises
            for (ExerciseEntry entry : log.getExercises()) {
                writer.write(String.format("%d,%02d,%02d,e,%s,%.1f",
                        y, m, d,
                        entry.getExercise().getName(),
                        entry.getMinutes()));
                writer.newLine();
            }
        }

        writer.close();
    }
}