import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * holding everything logged for a single day like food entries, exercise entries,
 * body weight, and the calorie goal for that day.
 *
 * i used these values per spec: weight = 150.0 lbs (used lbs bcs of sume bags when i converted to kg), calorie limit = 2000.0.
 */
public class DailyLog {

    private final LocalDate         date;
    private final List<LogEntry>     entries;
    private final List<ExerciseEntry> exerciseEntries;

    private double  weight        = 150.0;  
    private double  calorieLimit  = 2000.0; 
    private boolean hasWeight     = false;
    private boolean hasCalorieLimit = false;

    public DailyLog(LocalDate date) {
        this.date            = date;
        this.entries         = new ArrayList<>();
        this.exerciseEntries = new ArrayList<>();
    }

   //getter and setters
    public LocalDate          getDate()      { return date;            }
    public List<LogEntry>     getEntries()   { return entries;         }
    public List<ExerciseEntry> getExercises() { return exerciseEntries; }
    public double  getWeight()               { return weight;          }
    public double  getCalorieLimit()         { return calorieLimit;    }
    public boolean hasWeight()               { return hasWeight;       }
    public boolean hasCalorieLimit()         { return hasCalorieLimit; }

    public void setWeight(double weight) {
        this.weight    = weight;
        this.hasWeight = true;
    }

    public void setCalorieLimit(double limit) {
        this.calorieLimit    = limit;
        this.hasCalorieLimit = true;
    }

    //entries
    public void addEntry(LogEntry entry)      { entries.add(entry);         }
    public void addExercise(ExerciseEntry e)  { exerciseEntries.add(e);     }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public void removeExercise(int index) {
        if (index >= 0 && index < exerciseEntries.size()) {
            exerciseEntries.remove(index);
        }
    }

    /** calories consumed from food today. */
    public double getTotalCalories() {
        double total = 0;
        for (LogEntry e : entries) {
            total += e.getFood().getCalories() * e.getServings();
        }
        return total;
    }

    /**
     * calories burned through exercise today.
     */
    public double getBurnedCalories() {
        double total = 0;
        for (ExerciseEntry e : exerciseEntries) {
            total += e.caloriesBurned(weight);
        }
        return total;
    }

    public double getNetCalories() {
        return getTotalCalories() - getBurnedCalories();
    }

    public Nutrients getTotalNutrients() {
        Nutrients total = new Nutrients(0, 0, 0);
        for (LogEntry e : entries) {
            double servings = e.getServings();
            Nutrients n = new Nutrients(
                    e.getFood().getFat()     * servings,
                    e.getFood().getCarbs()   * servings,
                    e.getFood().getProtein() * servings
            );
            total = total.add(n);
        }
        return total;
    }

    /** True if net calories exceeds the calorie limit. */
    public boolean isOverCalorieLimit() {
        return hasCalorieLimit && getNetCalories() > calorieLimit;
    }

    @Override
    public String toString() {
        return "DailyLog[" + date + "]: net=" + String.format("%.1f", getNetCalories())
                + ", limit=" + calorieLimit;
    }
}
