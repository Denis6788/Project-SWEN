/**
 *  single exercise log entry: one exercise performed for some
 * number of minutes on a given day.
 */
public class ExerciseEntry {

    private final Exercise exercise;
    private final double   minutes;

    public ExerciseEntry(Exercise exercise, double minutes) {
        this.exercise = exercise;
        this.minutes  = minutes;
    }

    public Exercise getExercise() { return exercise; }
    public double   getMinutes()  { return minutes; }

    /**
     * Returns calories burned for the entry given the userss weight.
     *
     * @param weightLbs body weight in pounds
     */
    public double caloriesBurned(double weightLbs) {
        return exercise.caloriesBurned(weightLbs, minutes);
    }

    @Override
    public String toString() {
        return exercise.getName() + " x " + minutes + " min";
    }
}
