
public class Exercise {
    private String name;
    private double cal;

    public Exercise(String n, double c) {
        name = n; cal = c;
    }

    public String getName() { return name; }

    public double burn(double w, double m) {
        return cal * (w / 100.0) * (m / 60.0);
    }

    public static Exercise create(String n, double c) {
        return new Exercise(n, c);
    }
}