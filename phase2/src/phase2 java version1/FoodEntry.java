import java.time.LocalDate;
import java.util.*;



interface Food {
    String getName();
    double getCalories();
}



interface Observer {
    void update();
}

interface Subject {
    void addObserver(Observer o);
    void notifyObservers();
}



class Exercise {
    private String name;
    private double burnRate;

    public Exercise(String name, double burnRate) {
        this.name = name;
        this.burnRate = burnRate;
    }

    public double burn(double weight, double minutes) {
        return burnRate * weight * minutes;
    }

    public String getName() {
        return name;
    }
}



public class FoodEntry {
    private Food food;
    private double amount;

    public FoodEntry(Food food, double amount) {
        this.food = food;
        this.amount = amount;
    }

    public double calories() {
        return food.getCalories() * amount;
    }

    public Food getFood() {
        return food;
    }

    public double getAmount() {
        return amount;
    }
}

/* -------- EXERCISE ENTRY -------- */

class ExerciseEntry {
    Exercise exercise;
    double minutes;

    public ExerciseEntry(Exercise exercise, double minutes) {
        this.exercise = exercise;
        this.minutes = minutes;
    }

    public double calories(double weight) {
        return exercise.burn(weight, minutes);
    }
}

/* -------- LOG -------- */

class Log implements Subject {
    private LocalDate date;
    private List<FoodEntry> foods = new ArrayList<>();
    private List<ExerciseEntry> exercises = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    private double weight = 150.0;

    public Log(LocalDate date) {
        this.date = date;
    }

    public void addFood(FoodEntry f) {
        foods.add(f);
        notifyObservers();
    }

    public void addExercise(ExerciseEntry e) {
        exercises.add(e);
        notifyObservers();
    }

    public double consumed() {
        return foods.stream().mapToDouble(FoodEntry::calories).sum();
    }

    public double burned() {
        return exercises.stream().mapToDouble(e -> e.calories(weight)).sum();
    }

    public double net() {
        return consumed() - burned();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}