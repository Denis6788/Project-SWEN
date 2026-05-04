/**
 * Represents a single exercise in the exercise collection.
 * Stores the name and the calories burned per hour per 100 lbs.
 */
public class Exercise {

    private String name;
    private double burnRate; // calories per hour per 100 lbs

    public Exercise(String name, double burnRate) {
        this.name     = name;
        this.burnRate = burnRate;
    }

    public String getName()      { return name; }
    public double getBurnRate()  { return burnRate; }
    public void   setBurnRate(double burnRate) { this.burnRate = burnRate; }

    /**
     * Calculates calories burned given a body weight and duration.
     *
     * Formula: burnRate * (weightLbs / 100) * (minutes / 60)
     *
     * @param weightLbs  body weight in pounds
     * @param minutes    duration in minutes
     * @return calories burned, rounded to nearest whole calorie
     */
    public double caloriesBurned(double weightLbs, double minutes) {
        return burnRate * (weightLbs / 100.0) * (minutes / 60.0);
    }

    @Override
    public String toString() { return name; }
}
