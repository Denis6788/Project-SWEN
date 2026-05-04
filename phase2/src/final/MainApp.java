import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main JavaFX application 
 * MVC layout:
 *   Model -> FoodCollection, ExerciseCollection, LogManager, DailyLog
 *   View  -> This class (all Label/ListView/Chart display)
 *   Controller -> Button handlers that mutate the model then call refresh
 */
public class MainApp extends Application {
 
    //model components
    private FoodCollection     foods;
    private ExerciseCollection exercises;
    private LogManager         logManager;
    private LocalDate          currentDate = LocalDate.now();
 
    //view components

    private final ObservableList<String> entryList = FXCollections.observableArrayList();
    private final ListView<String>       listView  = new ListView<>(entryList);
 
    private final Label caloriesLabel  = new Label();
    private final Label statusLabel    = new Label();
    private final Label nutrientsLabel = new Label();
    private final Label weightLabel    = new Label();
 
    private final TextField weightField  = new TextField();
    private final TextField calorieField = new TextField();
 
    private final BarChartView chart = new BarChartView(300, 180);
 
    // Application entry 
    @Override
    public void start(Stage stage) {
        loadData();
        Scene scene = new Scene(buildUI(), 1020, 680);
        stage.setScene(scene);
        stage.setTitle("Diet Manager 2.0");
        stage.setOnCloseRequest(e -> saveAll());
        stage.show();
        refresh();
    }
 
    // Data loading
 
    private void loadData() {
        foods      = new FoodCollection();
        exercises  = new ExerciseCollection();
        logManager = new LogManager();
        logManager.setExerciseCollection(exercises);
 
        try { foods.load("foods.csv"); }
        catch (Exception e) { System.err.println("foods.csv: " + e.getMessage()); }
 
        try { exercises.load("exercise.csv"); }
        catch (Exception e) { System.err.println("exercise.csv: " + e.getMessage()); }
 
        try { logManager.load("log.csv", foods); }
        catch (Exception e) { System.err.println("log.csv: " + e.getMessage()); }
    }
 
    // UI construction 
    private HBox buildUI() {
 
        // Date picker 
        DatePicker datePicker = new DatePicker(currentDate);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setOnAction(e -> {
            currentDate = datePicker.getValue();
            refresh();
        });
 
        // Log food 
        ComboBox<String> foodBox = new ComboBox<>();
        foodBox.setPromptText("Select food");
        foodBox.setMaxWidth(Double.MAX_VALUE);
 
        TextField servingsField = new TextField();
        servingsField.setPromptText("Servings (e.g. 1.5)");
 
        Button addFoodToLogBtn = styledButton("Log Food", "#2196F3");
        addFoodToLogBtn.setOnAction(e -> {
            String sel = foodBox.getValue();
            if (sel == null) { alert("Please select a food."); return; }
            Food food = foods.findFood(sel);
            if (food == null) { alert("Food not found."); return; }
            double servings;
            try {
                servings = Double.parseDouble(servingsField.getText().trim());
                if (servings <= 0) { alert("Servings must be positive."); return; }
            } catch (NumberFormatException ex) { alert("Enter a valid number for servings."); return; }
            logManager.getOrCreate(currentDate).addEntry(new LogEntry(food, servings));
            servingsField.clear();
            refresh();
        });
 
        //  Log exercise 
        ComboBox<String> exerciseBox = new ComboBox<>();
        exerciseBox.setPromptText("Select exercise");
        exerciseBox.setMaxWidth(Double.MAX_VALUE);
 
        TextField minutesField = new TextField();
        minutesField.setPromptText("Minutes (e.g. 30)");
 
        Button addExerciseToLogBtn = styledButton("Log Exercise", "#4CAF50");
        addExerciseToLogBtn.setOnAction(e -> {
            String sel = exerciseBox.getValue();
            if (sel == null) { alert("Please select an exercise."); return; }
            Exercise ex = exercises.find(sel);
            if (ex == null) { alert("Exercise not found."); return; }
            double mins;
            try {
                mins = Double.parseDouble(minutesField.getText().trim());
                if (mins <= 0) { alert("Minutes must be positive."); return; }
            } catch (NumberFormatException ex2) { alert("Enter a valid number for minutes."); return; }
            logManager.getOrCreate(currentDate).addExercise(new ExerciseEntry(ex, mins));
            minutesField.clear();
            refresh();
        });
 
        // Weight and calorie goal 
        weightField.setPromptText("lbs");
        calorieField.setPromptText("calories");
 
        Button setWeightBtn  = styledButton("Set", "#607D8B");
        Button setCalorieBtn = styledButton("Set", "#607D8B");
 
        setWeightBtn.setOnAction(e -> {
            try {
                double w = Double.parseDouble(weightField.getText().trim());
                if (w <= 0) { alert("Weight must be positive."); return; }
                logManager.getOrCreate(currentDate).setWeight(w);
                refresh();
            } catch (NumberFormatException ex) { alert("Enter a valid weight in lbs."); }
        });
 
        setCalorieBtn.setOnAction(e -> {
            try {
                double c = Double.parseDouble(calorieField.getText().trim());
                if (c < 0) { alert("Calorie goal cannot be negative."); return; }
                logManager.getOrCreate(currentDate).setCalorieLimit(c);
                refresh();
            } catch (NumberFormatException ex) { alert("Enter a valid calorie goal."); }
        });
 
        // delete & Save 
        Button deleteBtn = styledButton("Delete Selected", "#f44336");
        Button saveBtn   = styledButton("Save All", "#FF9800");
        deleteBtn.setOnAction(e -> deleteSelectedEntry());
        saveBtn.setOnAction(e -> saveAll());
 
        // xollection management 
        Button newFoodBtn   = styledButton("Add New Food",     "#5C6BC0");
        Button editFoodBtn  = styledButton("Edit Food",        "#5C6BC0");
        Button newRecipeBtn = styledButton("Add New Recipe",   "#5C6BC0");
        Button newExBtn     = styledButton("Add New Exercise", "#5C6BC0");
        Button editExBtn    = styledButton("Edit Exercise",    "#5C6BC0");
 
        newFoodBtn.setOnAction(e   -> showAddFoodDialog(foodBox));
        editFoodBtn.setOnAction(e  -> showEditFoodDialog(foodBox));
        newRecipeBtn.setOnAction(e -> showAddRecipeDialog(foodBox));
        newExBtn.setOnAction(e     -> showAddExerciseDialog(exerciseBox));
        editExBtn.setOnAction(e    -> showEditExerciseDialog(exerciseBox));
 
        populateFoodBox(foodBox);
        populateExerciseBox(exerciseBox);
 
        //Left panel 
        VBox left = new VBox(6,
            section("Date"),
            datePicker,
            separator(),
            section("Log Food"),
            foodBox,
            servingsField,
            addFoodToLogBtn,
            separator(),
            section("Log Exercise"),
            exerciseBox,
            minutesField,
            addExerciseToLogBtn,
            separator(),
            section("Daily Settings"),
            row("Weight (lbs):", weightField, setWeightBtn),
            row("Calorie Goal:", calorieField, setCalorieBtn),
            separator(),
            section("Manage Collections"),
            newFoodBtn, editFoodBtn, newRecipeBtn,
            newExBtn,   editExBtn,
            separator(),
            deleteBtn,
            saveBtn
        );
        left.setPadding(new Insets(14));
        left.setPrefWidth(270);
        left.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #E0E0E0; -fx-border-width: 0 1 0 0;");
 
        // right panel 
        listView.setPrefHeight(230);
        listView.setStyle("-fx-font-size: 13px;");
 
        caloriesLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        weightLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        nutrientsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
 
        VBox right = new VBox(8,
            section("Today's Log"),
            listView,
            caloriesLabel,
            statusLabel,
            weightLabel,
            nutrientsLabel,
            separator(),
            section("Nutrition Chart   (Red = Fat     Green = Carbs     Blue = Protein)"),
            chart
        );
        right.setPadding(new Insets(14));
 
        HBox root = new HBox(0, left, right);
        HBox.setHgrow(right, Priority.ALWAYS);
        return root;
    }
 
    // delete selected entry 
    private void deleteSelectedEntry() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) { alert("Please select an entry to delete."); return; }
 
        DailyLog log = logManager.getOrCreate(currentDate);
 
        if (selected.startsWith("Food: ")) {
            String match = selected.substring(6);
            for (int i = 0; i < log.getEntries().size(); i++) {
                if (log.getEntries().get(i).toString().equals(match)) {
                    log.removeEntry(i);
                    refresh();
                    return;
                }
            }
        } else if (selected.startsWith("Exercise: ")) {
            String match = selected.substring(10);
            int dash = match.lastIndexOf(" - ");
            if (dash >= 0) match = match.substring(0, dash);
            for (int i = 0; i < log.getExercises().size(); i++) {
                if (log.getExercises().get(i).toString().equals(match)) {
                    log.removeExercise(i);
                    refresh();
                    return;
                }
            }
        }
        alert("Could not find the selected entry.");
    }
 
    // Add new basic food
 
    private void showAddFoodDialog(ComboBox<String> foodBox) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add New Food");
        dlg.setHeaderText("Enter nutritional information (per serving):");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        TextField nameF    = field("Food name");
        TextField calF     = field("Calories");
        TextField fatF     = field("Fat (g)");
        TextField carbF    = field("Carbs (g)");
        TextField proteinF = field("Protein (g)");
 
        dlg.getDialogPane().setContent(buildGrid(
            "Name:",        nameF,
            "Calories:",    calF,
            "Fat (g):",     fatF,
            "Carbs (g):",   carbF,
            "Protein (g):", proteinF
        ));
 
        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                String name = nameF.getText().trim();
                if (name.isEmpty())               { alert("Name cannot be empty."); return; }
                if (name.contains(","))           { alert("Name may not contain commas."); return; }
                if (foods.findFood(name) != null) { alert("A food named '" + name + "' already exists."); return; }
 
                double cal  = Double.parseDouble(calF.getText().trim());
                double fat  = Double.parseDouble(fatF.getText().trim());
                double carb = Double.parseDouble(carbF.getText().trim());
                double prot = Double.parseDouble(proteinF.getText().trim());
 
                foods.addFood(new BasicFood(name, cal, fat, carb, prot));
                populateFoodBox(foodBox);
                alert("'" + name + "' added to foods.");
            } catch (NumberFormatException ex) {
                alert("All numeric fields must be valid numbers.");
            }
        });
    }
 
    // edditing existing food
 
    private void showEditFoodDialog(ComboBox<String> foodBox) {
        ChoiceDialog<String> chooser = new ChoiceDialog<>();
        chooser.setTitle("Edit Food");
        chooser.setHeaderText("Select a food to edit:");
        for (Food f : foods.getAllFoods()) chooser.getItems().add(f.getName());
        if (chooser.getItems().isEmpty()) { alert("No foods in the collection yet."); return; }
        chooser.setSelectedItem(chooser.getItems().get(0));
 
        Optional<String> picked = chooser.showAndWait();
        if (picked.isEmpty()) return;
 
        Food food = foods.findFood(picked.get());
        if (food instanceof BasicFood bf) {
            showEditBasicFoodDialog(bf);
        } else if (food instanceof Recipe r) {
            showEditRecipeDialog(r, foodBox);
        }
    }
 
    private void showEditBasicFoodDialog(BasicFood bf) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Food: " + bf.getName());
        dlg.setHeaderText("Update nutritional values for one serving:");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        TextField calF     = field(String.valueOf(bf.getCalories()));
        TextField fatF     = field(String.valueOf(bf.getFat()));
        TextField carbF    = field(String.valueOf(bf.getCarbs()));
        TextField proteinF = field(String.valueOf(bf.getProtein()));
 
        dlg.getDialogPane().setContent(buildGrid(
            "Calories:",    calF,
            "Fat (g):",     fatF,
            "Carbs (g):",   carbF,
            "Protein (g):", proteinF
        ));
 
        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                bf.setCalories(Double.parseDouble(calF.getText().trim()));
                bf.setFat(Double.parseDouble(fatF.getText().trim()));
                bf.setCarbs(Double.parseDouble(carbF.getText().trim()));
                bf.setProtein(Double.parseDouble(proteinF.getText().trim()));
                refresh();
                alert("'" + bf.getName() + "' updated.");
            } catch (NumberFormatException ex) {
                alert("All fields must be valid numbers.");
            }
        });
    }
 
    private void showEditRecipeDialog(Recipe recipe, ComboBox<String> foodBox) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Edit Recipe: " + recipe.getName());
        dlg.setHeaderText("Add or remove ingredients:");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        ListView<String> ingList = new ListView<>();
        refreshIngredientList(ingList, recipe);
        ingList.setPrefHeight(150);
 
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            int idx = ingList.getSelectionModel().getSelectedIndex();
            if (idx >= 0) { recipe.removeIngredient(idx); refreshIngredientList(ingList, recipe); }
        });
 
        ComboBox<String> addFoodCombo = new ComboBox<>();
        for (Food f : foods.getAllFoods())
            if (!f.getName().equals(recipe.getName()))
                addFoodCombo.getItems().add(f.getName());
        if (!addFoodCombo.getItems().isEmpty()) addFoodCombo.setValue(addFoodCombo.getItems().get(0));
 
        TextField addServings = new TextField();
        addServings.setPromptText("Servings");
 
        Button addIngBtn = new Button("Add Ingredient");
        addIngBtn.setOnAction(e -> {
            String sel = addFoodCombo.getValue();
            if (sel == null) { alert("Pick a food."); return; }
            Food f = foods.findFood(sel);
            if (f == null) { alert("Food not found."); return; }
            try {
                double srv = Double.parseDouble(addServings.getText().trim());
                if (srv <= 0) { alert("Servings must be positive."); return; }
                recipe.add(f, srv);
                refreshIngredientList(ingList, recipe);
                addServings.clear();
            } catch (NumberFormatException ex) { alert("Enter a valid number."); }
        });
 
        VBox content = new VBox(8,
            new Label("Current ingredients:"), ingList, removeBtn,
            separator(),
            new Label("Add ingredient:"),
            new HBox(5, addFoodCombo, addServings, addIngBtn)
        );
        content.setPadding(new Insets(10));
        dlg.getDialogPane().setContent(content);
 
        dlg.showAndWait().ifPresent(btn -> { if (btn == ButtonType.OK) refresh(); });
    }
 
    // adding new recipe
 
    private void showAddRecipeDialog(ComboBox<String> foodBox) {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("New Recipe");
        nameDialog.setHeaderText("Enter a name for the new recipe:");
        nameDialog.setContentText("Name:");
 
        Optional<String> nameOpt = nameDialog.showAndWait();
        if (nameOpt.isEmpty() || nameOpt.get().trim().isEmpty()) return;
        String recipeName = nameOpt.get().trim();
        if (recipeName.contains(","))           { alert("Name may not contain commas."); return; }
        if (foods.findFood(recipeName) != null) { alert("A food named '" + recipeName + "' already exists."); return; }
 
        Recipe recipe = new Recipe(recipeName);
 
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Build Recipe: " + recipeName);
        dlg.setHeaderText("Add the ingredients that make up this recipe:");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        ListView<String> ingList = new ListView<>();
        ingList.setPrefHeight(150);
 
        ComboBox<String> ingBox = new ComboBox<>();
        for (Food f : foods.getAllFoods()) ingBox.getItems().add(f.getName());
        if (!ingBox.getItems().isEmpty()) ingBox.setValue(ingBox.getItems().get(0));
 
        TextField srvField = new TextField();
        srvField.setPromptText("Servings");
 
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            String sel = ingBox.getValue();
            if (sel == null) { alert("Select a food."); return; }
            Food f = foods.findFood(sel);
            if (f == null) { alert("Food not found."); return; }
            if (f.getName().equals(recipeName)) { alert("A recipe cannot include itself."); return; }
            try {
                double srv = Double.parseDouble(srvField.getText().trim());
                if (srv <= 0) { alert("Servings must be positive."); return; }
                recipe.add(f, srv);
                refreshIngredientList(ingList, recipe);
                srvField.clear();
            } catch (NumberFormatException ex) { alert("Enter a valid number for servings."); }
        });
 
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            int idx = ingList.getSelectionModel().getSelectedIndex();
            if (idx >= 0) { recipe.removeIngredient(idx); refreshIngredientList(ingList, recipe); }
        });
 
        VBox content = new VBox(8,
            new Label("Ingredients:"), ingList, removeBtn,
            separator(),
            new Label("Add ingredient:"),
            new HBox(5, ingBox, srvField, addBtn)
        );
        content.setPadding(new Insets(10));
        dlg.getDialogPane().setContent(content);
 
        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            if (recipe.getIngredients().isEmpty()) { alert("A recipe needs at least one ingredient."); return; }
            foods.addFood(recipe);
            populateFoodBox(foodBox);
            alert("Recipe '" + recipeName + "' added.");
        });
    }
 
    // adding new exercise
 
    private void showAddExerciseDialog(ComboBox<String> exerciseBox) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Add New Exercise");
        dlg.setHeaderText("Enter exercise details:");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        TextField nameF = field("Exercise name");
        TextField calF  = field("Cal/hr per 100 lbs");
 
        dlg.getDialogPane().setContent(buildGrid(
            "Name:",               nameF,
            "Cal/hr per 100 lbs:", calF
        ));
 
        dlg.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            String name = nameF.getText().trim();
            if (name.isEmpty())               { alert("Name cannot be empty."); return; }
            if (name.contains(","))           { alert("Name may not contain commas."); return; }
            if (exercises.find(name) != null) { alert("An exercise named '" + name + "' already exists."); return; }
            try {
                double rate = Double.parseDouble(calF.getText().trim());
                exercises.addExercise(new Exercise(name, rate));
                populateExerciseBox(exerciseBox);
                alert("'" + name + "' added to exercises.");
            } catch (NumberFormatException ex) {
                alert("Enter a valid number for calories.");
            }
        });
    }
 
    // editing exercise burn rate
 
    private void showEditExerciseDialog(ComboBox<String> exerciseBox) {
        ChoiceDialog<String> chooser = new ChoiceDialog<>();
        chooser.setTitle("Edit Exercise");
        chooser.setHeaderText("Select an exercise to edit:");
        for (Exercise ex : exercises.getAll()) chooser.getItems().add(ex.getName());
        if (chooser.getItems().isEmpty()) { alert("No exercises in the collection yet."); return; }
        chooser.setSelectedItem(chooser.getItems().get(0));
 
        chooser.showAndWait().ifPresent(name -> {
            Exercise ex = exercises.find(name);
            if (ex == null) return;
 
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Edit Exercise: " + ex.getName());
            dlg.setHeaderText("Update the burn rate:");
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
            TextField rateF = field(String.valueOf(ex.getBurnRate()));
            dlg.getDialogPane().setContent(buildGrid("Cal/hr per 100 lbs:", rateF));
 
            dlg.showAndWait().ifPresent(btn -> {
                if (btn != ButtonType.OK) return;
                try {
                    ex.setBurnRate(Double.parseDouble(rateF.getText().trim()));
                    refresh();
                    alert("'" + ex.getName() + "' updated.");
                } catch (NumberFormatException ex2) {
                    alert("Enter a valid number.");
                }
            });
        });
    }
 
    // refreshing view from model
 
    private void refresh() {
        if (logManager == null) return;
        DailyLog log = logManager.getOrCreate(currentDate);
 
        entryList.clear();
 
        for (LogEntry e : log.getEntries()) {
            entryList.add("Food: " + e);
        }
        for (ExerciseEntry e : log.getExercises()) {
            double burned = e.caloriesBurned(log.getWeight());
            entryList.add(String.format("Exercise: %s - %.0f cal burned", e, burned));
        }
        if (entryList.isEmpty()) {
            entryList.add("(no entries for this day)");
        }
 
        double consumed = log.getTotalCalories();
        double burned   = log.getBurnedCalories();
        double net      = log.getNetCalories();
 
        caloriesLabel.setText(String.format(
            "Consumed: %.1f  |  Burned: %.1f  |  Net: %.1f cal",
            consumed, burned, net
        ));
 
        weightLabel.setText(String.format("Weight: %.1f lbs", log.getWeight()));
 
        if (!log.hasCalorieLimit()) {
            statusLabel.setText("No calorie goal set.");
            statusLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        } else {
            double diff = log.getCalorieLimit() - net;
            if (net > log.getCalorieLimit()) {
                statusLabel.setText(String.format(
                    "OVER goal (%.0f cal) by %.1f cal", log.getCalorieLimit(), -diff));
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else {
                statusLabel.setText(String.format(
                    "Under goal (%.0f cal) by %.1f cal", log.getCalorieLimit(), diff));
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 12px;");
            }
        }
 
        Nutrients n = log.getTotalNutrients();
        if (n != null && n.totalGrams() > 0) {
            nutrientsLabel.setText(String.format(
                "Nutrients:  Fat %.0f%%   Carbs %.0f%%   Protein %.0f%%",
                n.fatPercent(), n.carbPercent(), n.proteinPercent()
            ));
            chart.updateData(n.getFat(), n.getCarbs(), n.getProtein());
        } else {
            nutrientsLabel.setText("No nutrient data yet - add some food.");
            chart.updateData(0, 0, 0);
        }
 
        weightField.setText(log.hasWeight()
            ? String.format("%.1f", log.getWeight()) : "");
        calorieField.setText(log.hasCalorieLimit()
            ? String.format("%.0f", log.getCalorieLimit()) : "");
    }
 
    // save
 
    private void saveAll() {
        try {
            foods.save("foods.csv");
            exercises.save("exercise.csv");
            logManager.save("log.csv");
            System.out.println("Saved successfully.");
        } catch (Exception e) {
            alert("Error saving: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    // helpers for dialogs and UI
 
    private void populateFoodBox(ComboBox<String> box) {
        String current = box.getValue();
        box.getItems().clear();
        for (Food f : foods.getAllFoods()) box.getItems().add(f.getName());
        if (current != null && box.getItems().contains(current)) box.setValue(current);
        else if (!box.getItems().isEmpty()) box.setValue(box.getItems().get(0));
    }
 
    private void populateExerciseBox(ComboBox<String> box) {
        String current = box.getValue();
        box.getItems().clear();
        for (Exercise e : exercises.getAll()) box.getItems().add(e.getName());
        if (current != null && box.getItems().contains(current)) box.setValue(current);
        else if (!box.getItems().isEmpty()) box.setValue(box.getItems().get(0));
    }
 
    private void refreshIngredientList(ListView<String> lv, Recipe recipe) {
        lv.getItems().clear();
        List<Food>   ings    = recipe.getIngredients();
        List<Double> amounts = recipe.getAmounts();
        for (int i = 0; i < ings.size(); i++) {
            lv.getItems().add(String.format("%s  x %.1f", ings.get(i).getName(), amounts.get(i)));
        }
    }
 
    private TextField field(String promptOrValue) {
        TextField tf = new TextField();
        try { Double.parseDouble(promptOrValue); tf.setText(promptOrValue); }
        catch (NumberFormatException e) { tf.setPromptText(promptOrValue); }
        return tf;
    }
 
    private GridPane buildGrid(Object... pairs) {
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(12));
        for (int i = 0; i < pairs.length; i += 2) {
            gp.add(new Label(pairs[i].toString()), 0, i / 2);
            gp.add((javafx.scene.Node) pairs[i + 1], 1, i / 2);
        }
        return gp;
    }
 
    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #444;");
        return l;
    }
 
    private Button styledButton(String text, String color) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
                 + "-fx-font-weight: bold; -fx-background-radius: 4;");
        return b;
    }
 
    private HBox row(String label, TextField tf, Button btn) {
        Label l = new Label(label);
        l.setMinWidth(90);
        HBox hb = new HBox(6, l, tf, btn);
        hb.setStyle("-fx-alignment: center-left;");
        HBox.setHgrow(tf, Priority.ALWAYS);
        return hb;
    }
 
    private Separator separator() {
        return new Separator();
    }
 
    private void alert(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Diet Manager");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}