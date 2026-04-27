import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class MainApp extends Application {

    private FoodCollection foods;
    private ExerciseCollection exercises; // NEW
    private LogManager logManager;
    private LocalDate currentDate = LocalDate.now();

    private ObservableList<String> entryList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(entryList);

    private Label caloriesLabel = new Label();
    private Label statusLabel = new Label();
    private Label nutrientsLabel = new Label();

    private TextField weightField = new TextField();
    private TextField calorieField = new TextField();

    @Override
    public void start(Stage stage) {

        try {
            foods = new FoodCollection();
            foods.load("foods.csv");

            exercises = new ExerciseCollection(); // NEW
            exercises.load("exercise.csv");

            logManager = new LogManager();
            logManager.setExerciseCollection(exercises); // NEW
            logManager.load("log.csv", foods);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Label title = new Label("Wellness Manager");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        DatePicker datePicker = new DatePicker(currentDate);

        ComboBox<String> foodBox = new ComboBox<>();
        for (FoodItem f : foods.getAllFoods()) {
            foodBox.getItems().add(f.getName());
        }

        TextField servingsField = new TextField();
        servingsField.setPromptText("Servings");

        // NEW EXERCISE UI
        ComboBox<String> exerciseBox = new ComboBox<>();
        for (Exercise e : exercises.getAll()) {
            exerciseBox.getItems().add(e.getName());
        }

        TextField minutesField = new TextField();
        minutesField.setPromptText("Minutes");

        Button addFoodBtn = new Button("Add Food");
        Button addExerciseBtn = new Button("Add Exercise");
        Button deleteBtn = new Button("Delete");
        Button saveBtn = new Button("Save");

        VBox left = new VBox(10,
                new Label("Date"), datePicker,

                new Label("Food"), foodBox,
                servingsField,
                addFoodBtn,

                new Separator(),

                new Label("Exercise"), exerciseBox,
                minutesField,
                addExerciseBtn,

                new Separator(),

                new Label("Weight"), weightField,
                new Label("Calorie Goal"), calorieField,

                saveBtn
        );

        VBox right = new VBox(10,
                listView,
                caloriesLabel,
                statusLabel,
                nutrientsLabel
        );

        HBox root = new HBox(15, left, right);
        root.setPadding(new Insets(15));

        // ======================
        // ACTIONS
        // ======================

        datePicker.setOnAction(e -> {
            currentDate = datePicker.getValue();
            refresh();
        });

        addFoodBtn.setOnAction(e -> {
            try {
                FoodItem f = foods.findFood(foodBox.getValue());
                double s = Double.parseDouble(servingsField.getText());

                logManager.getOrCreate(currentDate)
                        .addEntry(new LogEntry(f, s));

                servingsField.clear();
                refresh();

            } catch (Exception ex) {
                showAlert("Invalid food input");
            }
        });

        // NEW
        addExerciseBtn.setOnAction(e -> {
            try {
                Exercise ex = exercises.find(exerciseBox.getValue());
                double m = Double.parseDouble(minutesField.getText());

                logManager.getOrCreate(currentDate)
                        .addExercise(new ExerciseEntry(ex, m));

                minutesField.clear();
                refresh();

            } catch (Exception ex) {
                showAlert("Invalid exercise input");
            }
        });

        deleteBtn.setOnAction(e -> {
            String selected = listView.getSelectionModel().getSelectedItem();

            DailyLog log = logManager.getOrCreate(currentDate);

            for (LogEntry entry : log.getEntries()) {
                if (entry.toString().equals(selected)) {
                    log.getEntries().remove(entry);
                    break;
                }
            }

            refresh();
        });

        saveBtn.setOnAction(e -> {
            try {
                logManager.save("log.csv");
                exercises.save("exercise.csv"); // NEW
                showAlert("Saved");
            } catch (Exception ex) {
                showAlert("Error saving");
            }
        });

        refresh();

        stage.setScene(new Scene(root, 750, 500));
        stage.setTitle("Wellness Manager");
        stage.show();
    }

    private void refresh() {

        DailyLog log = logManager.getOrCreate(currentDate);

        entryList.clear();

        // show foods
        for (LogEntry e : log.getEntries()) {
            entryList.add("Food: " + e.toString());
        }

        // NEW show exercises
        for (ExerciseEntry e : log.getExercises()) {
            entryList.add("Exercise: " + e.toString());
        }

        double consumed = log.getTotalCalories();
        double burned = log.getBurnedCalories();
        double net = log.getNetCalories();

        caloriesLabel.setText(
                "Consumed: " + consumed +
                " | Burned: " + burned +
                " | Net: " + net
        );

        double diff = log.getCalorieLimit() - net;

        if (net > log.getCalorieLimit()) {
            statusLabel.setText("OVER limit (" + diff + ")");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("UNDER limit (" + diff + ")");
            statusLabel.setStyle("-fx-text-fill: green;");
        }

        Nutrients n = log.getTotalNutrients();

        nutrientsLabel.setText(
                "Fat: " + n.fatPercent() +
                "% | Carbs: " + n.carbPercent() +
                "% | Protein: " + n.proteinPercent() + "%"
        );
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }

    public static void main(String[] args) {
        launch();
    }
}