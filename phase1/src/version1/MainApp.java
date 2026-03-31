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
    private LogManager logManager;
    private LocalDate currentDate = LocalDate.now();

    // list used to show entries in UI
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
            // loading foods and logs from files
            foods = new FoodCollection();
            foods.load("foods.csv");

            logManager = new LogManager();
            logManager.load("log.csv", foods);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Label title = new Label("Wellness Manager");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label("Track your daily food, weight and calorie goal");
        subtitle.setStyle("-fx-text-fill: #666;");

        // simple explanation for users
        Label help = new Label(
                "Weight = your body weight in kilograms (kg).\n" +
                "Calorie limit = your daily goal (e.g. 2000)."
        );
        help.setStyle("-fx-text-fill: #555;");

        DatePicker datePicker = new DatePicker(currentDate);

        // dropdown for selecting food from list
        ComboBox<String> foodBox = new ComboBox<>();
        for (FoodItem f : foods.getAllFoods()) {
            foodBox.getItems().add(f.getName());
        }
        foodBox.setPromptText("Choose food");

        TextField servingsField = new TextField();
        servingsField.setPromptText("Servings (e.g. 1.5)");

        // user enters weight in kg
        weightField.setPromptText("Weight (kg)");
        calorieField.setPromptText("Calorie goal");

        Button addBtn = new Button("Add");
        Button deleteBtn = new Button("Delete");
        Button saveBtn = new Button("Save");
        Button updateBtn = new Button("Apply");
        Button resetBtn = new Button("Reset");

        // basic style colors
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        saveBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        resetBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        // left side for inputs
        VBox leftPanel = new VBox(10,
                new Label("Date"), datePicker,
                new Separator(),

                new Label("Food"), foodBox,
                new Label("Servings"), servingsField,
                new HBox(10, addBtn, deleteBtn),

                new Separator(),

                new Label("Weight"), weightField,
                new Label("Calorie Limit"), calorieField,
                updateBtn,

                new Separator(),

                saveBtn, resetBtn
        );

        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #ddd;"
        );

        // right side for data display
        VBox rightPanel = new VBox(10,
                new Label("Daily Entries"),
                listView,
                new Separator(),
                caloriesLabel,
                statusLabel,
                nutrientsLabel
        );

        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #ddd;"
        );

        HBox mainLayout = new HBox(15, leftPanel, rightPanel);
        mainLayout.setPadding(new Insets(15));

        VBox root = new VBox(10, title, subtitle, help, mainLayout);
        root.setStyle("-fx-background-color: #eef2f7;");

        // changing date and reloading data
        datePicker.setOnAction(e -> {
            currentDate = datePicker.getValue();
            refresh();
        });

        // adding food entry to current day
        addBtn.setOnAction(e -> {
            String name = foodBox.getValue();
            String servStr = servingsField.getText().trim();

            if (name == null || servStr.isEmpty()) {
                showAlert("Select food and servings");
                return;
            }

            double servings;
            try {
                servings = Double.parseDouble(servStr);
                if (servings <= 0) throw new Exception();
            } catch (Exception ex) {
                showAlert("Invalid servings");
                return;
            }

            FoodItem food = foods.findFood(name);
            DailyLog log = logManager.getOrCreate(currentDate);
            log.addEntry(new LogEntry(food, servings));

            servingsField.clear();
            refresh();
        });

        // deleting selected entry from list
        deleteBtn.setOnAction(e -> {

            String selected = listView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("Select an entry first");
                return;
            }

            DailyLog log = logManager.getOrCreate(currentDate);

            LogEntry toRemove = null;

            for (LogEntry entry : log.getEntries()) {
                if (entry.toString().equals(selected)) {
                    toRemove = entry;
                    break;
                }
            }

            if (toRemove != null) {
                log.getEntries().remove(toRemove);
            }

            refresh();
        });

        // updating weight and calorie limit
        updateBtn.setOnAction(e -> {
            DailyLog log = logManager.getOrCreate(currentDate);

            try {
                double kg = Double.parseDouble(weightField.getText());
                double lbs = kg * 2.20462; 
                log.setWeight(lbs);
            } catch (Exception ignored) {}

            try {
                double c = Double.parseDouble(calorieField.getText());
                log.setCalorieLimit(c);
            } catch (Exception ignored) {}

            refresh();
        });

        // save everything to file
        saveBtn.setOnAction(e -> {
            try {
                logManager.save("log.csv");
                showAlert("Data saved");
            } catch (Exception ex) {
                showAlert("Error saving");
            }
        });

        // clearing all entries for th selected day
        resetBtn.setOnAction(e -> {
            DailyLog log = logManager.getOrCreate(currentDate);
            log.clearEntries();
            refresh();
        });

        refresh();

        stage.setTitle("Wellness Manager");
        stage.setScene(new Scene(root, 750, 520));
        stage.show();
    }

    private void refresh() {
        DailyLog log = logManager.getOrCreate(currentDate);

        entryList.clear();
        for (LogEntry e : log.getEntries()) {
            entryList.add(e.toString());
        }

        // auto selected first entry for easier delete
        if (!entryList.isEmpty()) {
            listView.getSelectionModel().select(0);
        }

        // converting lbs back to kg for display
        double kg = log.getWeight() / 2.20462;
        weightField.setText(String.format("%.1f", kg));

        calorieField.setText(String.valueOf(log.getCalorieLimit()));

        caloriesLabel.setText("Total Calories: " + log.getTotalCalories());

        if (log.isOverCalorieLimit()) {
            statusLabel.setText("Status: OVER limit");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            statusLabel.setText("Status: UNDER limit");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }

        Nutrients n = log.getTotalNutrients();

        nutrientsLabel.setText(
                "Fat: " + n.fatPercent() + "% | " +
                "Carbs: " + n.carbPercent() + "% | " +
                "Protein: " + n.proteinPercent() + "%"
        );
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}