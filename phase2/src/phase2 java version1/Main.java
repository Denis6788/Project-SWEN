
public class Main {

    public static void main(String[] args) {

        AppController controller = new AppController();

        controller.createFood("Apple", 95, 0.3, 25, 0.5);
        controller.createExercise("Run", 400);

        Log log = controller.getLog();

        Observer view = () -> {
            System.out.println("Consumed: " + log.consumed());
            System.out.println("Burned: " + log.burned());
            System.out.println("Net: " + log.net());
            System.out.println("-----");
        };

        log.addObserver(view);

        controller.addFoodToLog("Apple", 2);
        controller.addExerciseToLog("Run", 30);
    }
}