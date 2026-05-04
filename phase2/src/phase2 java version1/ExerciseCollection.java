import java.io.*;
import java.util.*;

/**
 * keeps all exercises available in the system.
 * loads and saves to exercise.csv in the format specified:
 *   e,name,calories
 */
public class ExerciseCollection {

    private final Map<String, Exercise> exercises = new LinkedHashMap<>();

    public void addExercise(Exercise exercise) {
        exercises.put(exercise.getName().toLowerCase(), exercise);
    }

    public Exercise find(String name) {
        if (name == null) return null;
        return exercises.get(name.toLowerCase());
    }

    public List<Exercise> getAll() {
        return new ArrayList<>(exercises.values());
    }

    public int size() { return exercises.size(); }

    
    public void load(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                if (!parts[0].trim().equals("e")) continue;

                try {
                    String name     = parts[1].trim();
                    double burnRate = Double.parseDouble(parts[2].trim());
                    addExercise(new Exercise(name, burnRate));
                } catch (NumberFormatException ex) {
                    System.err.println("Skipping malformed exercise line: " + line);
                }
            }
        }
    }

    /**
     * saving exercises to the given CSV file.
     */
    public void save(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Exercise ex : exercises.values()) {
                writer.write("e," + ex.getName() + "," + ex.getBurnRate());
                writer.newLine();
            }
        }
    }
}
