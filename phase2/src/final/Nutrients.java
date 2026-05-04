
public class Nutrients {

    private double fat;
    private double carbs;
    private double protein;

    public Nutrients(double fat, double carbs, double protein) {
        this.fat     = fat;
        this.carbs   = carbs;
        this.protein = protein;
    }

    public double getFat()     { return fat;     }
    public double getCarbs()   { return carbs;   }
    public double getProtein() { return protein; }

    public Nutrients add(Nutrients other) {
        return new Nutrients(
                this.fat     + other.fat,
                this.carbs   + other.carbs,
                this.protein + other.protein
        );
    }

    public double totalGrams() {
        return fat + carbs + protein;
    }


    public double getCalories() {
        return (fat * 9) + (carbs * 4) + (protein * 4);
    }

    public double fatPercent() {
        double total = totalGrams();
        if (total == 0) return 0;
        return Math.round((fat / total) * 100.0);
    }

    public double carbPercent() {
        double total = totalGrams();
        if (total == 0) return 0;
        return Math.round((carbs / total) * 100.0);
    }

    public double proteinPercent() {
        double total = totalGrams();
        if (total == 0) return 0;
        return 100.0 - fatPercent() - carbPercent();
    }

    @Override
    public String toString() {
        return String.format("Fat: %.1fg | Carbs: %.1fg | Protein: %.1fg",
                fat, carbs, protein);
    }
}
