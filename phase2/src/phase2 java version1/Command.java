
public interface Command {
    void execute();
}

class AddFoodCommand implements Command {
    private Log log;
    private FoodEntry entry;

    public AddFoodCommand(Log l, FoodEntry e) {
        log = l; entry = e;
    }

    public void execute() {
        log.addFood(entry);
    }
}

class AddExerciseCommand implements Command {
    private Log log;
    private ExerciseEntry entry;

    public AddExerciseCommand(Log l, ExerciseEntry e) {
        log = l; entry = e;
    }

    public void execute() {
        log.addExercise(entry);
    }
}