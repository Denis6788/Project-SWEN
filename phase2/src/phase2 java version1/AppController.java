import java.time.LocalDate;
import java.util.*;

public class AppController {

    private Map<String, Food> foods = new HashMap<>();
    private Map<String, Exercise> exercises = new HashMap<>();
    private Log log = new Log(LocalDate.now());

    public Log getLog() {
        return log;
    }

   

    public void createFood(String name, double calories, double fat, double carbs, double protein) {
        foods.put(name, FoodFactory.createBasic(name, calories, fat, carbs, protein));
    }

    

    public void createExercise(String name, double burnRate) {
        exercises.put(name, new Exercise(name, burnRate));
    }

   

    public void addFoodToLog(String name, double amount) {
        Food food = foods.get(name);

        if (food != null) {
            log.addFood(new FoodEntry(food, amount));
        }
    }

    public void addExerciseToLog(String name, double minutes) {
        Exercise exercise = exercises.get(name);

        if (exercise != null) {
            log.addExercise(new ExerciseEntry(exercise, minutes));
        }
    }
}