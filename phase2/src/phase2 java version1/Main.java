public class Main {

    public static void main(String[] args) {

        try {
            FoodCollection foods = new FoodCollection();
            foods.load("foods.csv");

            ExerciseCollection exercises = new ExerciseCollection(); // NEW
            exercises.load("exercise.csv");

            LogManager logManager = new LogManager();

            // NEW: connect exercises
            logManager.setExerciseCollection(exercises);

            logManager.load("log.csv", foods);

            AppController controller =
                    new AppController(foods, logManager, exercises); // UPDATED

            controller.showSummary();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}