
# Design Sketch - Food Tracker

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