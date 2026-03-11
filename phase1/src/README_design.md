
# Design Sketch - Food Tracker // 04.03.2026.

## Class Descriptions

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
