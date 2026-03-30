public class Main {

    public static void main(String[] args) {

        try {
            FoodCollection foods = new FoodCollection();
            foods.load("foods.csv");

            System.out.println("Foods loaded: " + foods.size());

            for (FoodItem f : foods.getAllFoods()) {
                System.out.println(f.getName());
            }

            LogManager logManager = new LogManager();
            logManager.load("log.csv", foods);

            LogController controller = new LogController(foods, logManager);
            controller.start();

        } catch (Exception e) {
            System.out.println("Error starting application:");
            e.printStackTrace();
        }
    }
}