import java.util.Stack;

/**
 * Command interface for undo/redo functionality
 */
public interface Command {
    void execute();
    void undo();
}

/**
 * Command for adding food
 */
class AddFoodCommand implements Command {
    private final DailyLog log;
    private final LogEntry entry;
    private boolean executed = false;
    
    public AddFoodCommand(DailyLog log, LogEntry entry) {
        this.log = log;
        this.entry = entry;
    }
    
    @Override
    public void execute() {
        if (!executed) {
            log.addEntry(entry);
            executed = true;
        }
    }
    
    @Override
    public void undo() {
        if (executed) {
            log.getEntries().remove(entry);
            executed = false;
        }
    }
}

/**
 * Command for adding exercise
 */
class AddExerciseCommand implements Command {
    private final DailyLog log;
    private final ExerciseEntry entry;
    private boolean executed = false;
    
    public AddExerciseCommand(DailyLog log, ExerciseEntry entry) {
        this.log = log;
        this.entry = entry;
    }
    
    @Override
    public void execute() {
        if (!executed) {
            log.addExercise(entry);
            executed = true;
        }
    }
    
    @Override
    public void undo() {
        if (executed) {
            log.getExercises().remove(entry);
            executed = false;
        }
    }
}

/**
 * Command manager for undo/redo
 */
class CommandManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
    
    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
}