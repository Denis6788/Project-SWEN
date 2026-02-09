
import java.util.Observable ;
import java.util.Observer ;

public class WeatherStationTextUI implements Observer {

    private Main.Temperature temperatureSetter;

    public WeatherStationTextUI(WeatherStation ts) {
        this.temperatureSetter = ts.getTemperatureSetter();
        ts.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        synchronized (temperatureSetter) {
            System.out.printf(" Temperature is %6.2f °C, %6.2f K, %6.2f F, and pressure is %2.2f inches InHg, and %2.2f millibars hPa%n ",
                    temperatureSetter.getCelsius(),
                    temperatureSetter.getKelvin(),
                    temperatureSetter.getFahrenheit(),
                    temperatureSetter.getInch(),
                    temperatureSetter.getMillibar());
        }
    }
   
}
