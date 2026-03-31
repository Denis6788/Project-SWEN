/**
 * Component interface for the Composite pattern.
 * Both BasicFood (leaf) and Recipe (composite) implement this.
 */
public interface FoodItem {
    String getName();
    double getCalories();
    Nutrients getNutrients();
    String toCSV();
}
