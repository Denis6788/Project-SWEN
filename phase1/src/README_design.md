
# Design Sketch - Food Tracker // 04.03.2026.

## Class Descriptions and contributions

FoodComponent: Abstract class representing a food item or a recipe. Contains methods to get calories and nutrients.

BasicFood: Represents a single food item with known calories and macronutrients.

Recipe: Represents a recipe that can contain BasicFood or other Recipe objects.

FoodCollection: A collection of all available food items and recipes. Allows adding, removing, and retrieving foods.

DayLog: Daily record of food intake, weight, and calorie limit for a given day.

LogEntry: A single food entry in the daily log, including the number of servings and reference to a FoodComponent.

Control: Manages user interaction with the model.

View: Displays data to the user and receives input.

## Design Rationale

The system uses the Composite pattern so that recipes and basic foods can be treated uniformly, which simplifies calorie calculation and adding new recipes.

The MVC pattern separates the view from the logic, making maintenance and future extensions easier.

## Diagrams


The class diagram and sequence diagrams are located in the `docs/` folder as images.

## Contributions // 11.03.2026.

Team Contributions for this week:

Denis Keselj : Created and structured the foods.csv file, including all basic foods and recipes. Ensured the format followed project requirements, with no forward references and unique food names.

Gabriel Muskaj: Managed the log.csv file for daily food intake, weights, and calorie targets. Implemented the tracking of calories, macronutrient distribution, and weight trends over time.

Marko Obsivac: Designed and implemented the Composite and MVC patterns. Made sure that both basic foods and recipes could be treated uniformly, and structured the project so the model, view, and controller communicate effectively.

## Contributions // 25.03.2026.

Team Contributions for this week:

Denis Keselj: started with Java implementation, shared the tasks, and started to work on AppController java file, implementing MVC pattern, a Fooditem interface java file after submitting a version1 of the code files 

Gabriel Muskaj: after submitting log.csv file on /src folder, started with java. First he made Nutrients java file, and submitted on version1 folder in the /src folder, and then started to work on logManager java file

Marko Obsivac: strted to work on java, made the version1 of the FoodCollection, Recipe and BasicFood java file, and started to finish it to make the final version.

## Contributions // 30.03.2026.

Team Contributions for this week:

Denis Keselj: started editing the version 1 of the java files, finishing the appcontroller and main java files, and putting all of the together to test the files.

Gabriel Muskaj: submitting the logManager file, and starting with Marko Obsivac to work on the presentation of the project.

Marko  Obsivac: finishing his files to the final version, submits them on /src folder as source code, and starts to work on presentation.

## Contribution //31.03.2026

Gabriel Muskaj: finished with the logic and worked with javafx (GUI). Submitted: DailyLog, LogController, LogEntry, LogManager, Main-MainApp.

## Compiling And Running The App
Compiling: javac --module-path "C:\javafx\javafx-sdk-17.0.18\lib" --add-modules javafx.controls *.java
Running: java --module-path "C:\javafx\javafx-sdk-17.0.18\lib" --add-modules javafx.controls MainApp 
The path is based on students workspace and can be different for others.
