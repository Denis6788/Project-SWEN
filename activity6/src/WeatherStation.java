import java.util.Observable;

public class WeatherStation extends Observable implements Runnable {

    private final ITempSensor tempSensor;
    private final IBarometer barometer;
    private final Main.Temperature temperatureSetter;

    private final long PERIOD = 1000;

    public WeatherStation(Main.Temperature temperatureSetter,
                          ITempSensor tempSensor,
                          IBarometer barometer) {
        this.temperatureSetter = temperatureSetter;
        this.tempSensor = tempSensor;
        this.barometer = barometer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PERIOD);
            } catch (InterruptedException e) {
            }

           
            int tempC = tempSensor.reading();

            
            int kelvinHundredths = (tempC * 100) + 27315;
            temperatureSetter.setTemperatureFromKelvin(kelvinHundredths);

            double pressure = barometer.pressure();
            temperatureSetter.setPressure(pressure);

            System.out.printf("Temp: %6.2f °C, %6.2f °F, Pressure: %6.2f inHg%n",
                          temperatureSetter.getCelsius(),
                          temperatureSetter.getFahrenheit(),
                          temperatureSetter.getInch());

            setChanged();
            notifyObservers();
        }
    }

    public Main.Temperature getTemperatureSetter() {
        return temperatureSetter;
    }

    

}
