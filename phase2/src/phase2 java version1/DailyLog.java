import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * presents one day of logging.
 * Stores food entries, weight and calorie limit.
 */
public class DailyLog {

    private final LocalDate date;
    private final List<LogEntry> entries;

    // NEW: store exercises
    private final List<ExerciseEntry> exerciseEntries = new ArrayList<>();

    private double weight;
    private double calorieLimit;
    private boolean hasWeight;
    private boolean hasCalorieLimit;

    public DailyLog(LocalDate date) {
        this.date = date;
        this.entries = new ArrayList<>();
        this.hasWeight = false;
        this.hasCalorieLimit = false;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    // NEW
    public List<ExerciseEntry> getExercises() {
        return exerciseEntries;
    }

    public double getWeight() {
        return weight;
    }

    public double getCalorieLimit() {
        return calorieLimit;
    }

    public boolean hasWeight() {
        return hasWeight;
    }

    public boolean hasCalorieLimit() {
        return hasCalorieLimit;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        this.hasWeight = true;
    }

    public void setCalorieLimit(double calorieLimit) {
        this.calorieLimit = calorieLimit;
        this.hasCalorieLimit = true;
    }

    public void addEntry(LogEntry entry) {
        entries.add(entry);
    }

    // NEW
    public void addExercise(ExerciseEntry e) {
        exerciseEntries.add(e);
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    // NEW
    public void removeExercise(int index) {
        if (index >= 0 && index < exerciseEntries.size()) {
            exerciseEntries.remove(index);
        }
    }

    public void clearEntries() {
        entries.clear();
        exerciseEntries.clear(); // UPDATED
    }

    public double getTotalCalories() {
        double total = 0;
        for (LogEntry e : entries) {
            total += e.getCalories();
        }
        return total;
    }

    // NEW: burned calories
    public double getBurnedCalories() {
        double total = 0;
        for (ExerciseEntry e : exerciseEntries) {
            total += e.getCalories(weight);
        }
        return total;
    }

    // NEW: net calories
    public double getNetCalories() {
        return getTotalCalories() - getBurnedCalories();
    }

    public Nutrients getTotalNutrients() {
        Nutrients total = new Nutrients(0, 0, 0);

        for (LogEntry e : entries) {
            total = total.addScaled(e.getFood().getNutrients(), e.getServings());
        }
        return total;
    }

    public boolean isOverCalorieLimit() {
        return getNetCalories() > calorieLimit; // UPDATED
    }

    @Override
    public String toString() {
        return String.format("DailyLog[%s]: %.1f net cal / limit %.1f",
                date, getNetCalories(), calorieLimit);
    }
}