
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DailyLog { //made b Gabriel Muskaj

    private final LocalDate date;
    private final List<LogEntry> entries;
    private double weight;
    private double calorieLimit;
    private boolean hasWeight;
    private boolean hasCalorieLimit;

    // constructor for creating empty log for a date
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
        return new ArrayList<>(entries);
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

   
    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public double getTotalCalories() {
        double total = 0;
        for (LogEntry e : entries) {
            total += e.getCalories();
        }
        return total;
    }

   
    public Nutrients getTotalNutrients() {
        Nutrients total = new Nutrients(0, 0, 0);

        for (LogEntry e : entries) {
            total = total.addScaled(e.getFood().getNutrients(), e.getServings());
        }
        return total;
    }

    public boolean isOverCalorieLimit() {
        return getTotalCalories() > calorieLimit;
    }

    @Override
    public String toString() {
        return String.format("DailyLog[%s]: %.1f cal / limit %.1f",
                date, getTotalCalories(), calorieLimit);
    }
}