/*
 * Initial Author
 *      Michael J. Lutz
 *
 * Other Contributers
 *
 * Acknowledgements
 */

/*
 * Class for a simple computer based weather station that reports the current
 * temperature (in Celsius) every second. The station is attached to a
 * sensor that reports the temperature as a 16-bit number (0 to 65535)
 * representing the Kelvin temperature to the nearest 1/100th of a degree.
 *
 * This class is implements Runnable so that it can be embedded in a Thread
 * which runs the periodic sensing.
 */

 import java.util.Observable ;


public class WeatherStation extends Observable implements Runnable {

    private final KelvinTempSensor sensor ;
    private final Main.Temperature temperatureSetter; 
     private final Barometer barometer; 

    private final long PERIOD = 1000 ;      // 1 sec = 1000 ms.

    /*
     * When a WeatherStation object is created, it in turn creates the sensor
     * object it will use.
     */
    public WeatherStation(Main.Temperature temperatureSetter) {
        this.temperatureSetter = temperatureSetter;
        this.sensor = new KelvinTempSensor();
        this.barometer = new Barometer();
    }

    /*
     * The "run" method called by the enclosing Thread object when started.
     * Repeatedly sleeps a second, acquires the current temperature from
     * its sensor, and reports this as a formatted output string.
     */
    public void run() {
          // Convert raw Kelvin reading to Celsius

        while( true ) {
            try {
                Thread.sleep(PERIOD) ;
            } catch (InterruptedException e) {

            }    // ignore exceptions

             int reading = sensor.reading() ;
             temperatureSetter.setTemperatureFromKelvin(reading);
             double readingPressure = barometer.pressure();

             temperatureSetter.setPressure(readingPressure);



            setChanged();
            notifyObservers();
            /*
             * System.out.printf prints formatted data on the output screen.
             *
             * Most characters print as themselves.
             *
             * % introduces a format command that usually applies to the
             * next argument of printf:
             *   *  %6.2f formats the "celsius" (2nd) argument in a field
             *      at least 6 characters wide with 2 fractional digits.
             *   *  The %n at the end of the string forces a new line
             *      of output.
             *   *  %% represents a literal percent character.
             *
             * See docs.oracle.com/javase/tutorial/java/data/numberformat.html
             * for more information on formatting output.
             */
            //
            System.out.printf("Reading is %6.2f degrees C, %6.2f degrees K, and %6.2f degrees F, and a pressure is %2.2f inches InHg, and %2.2f millibars hPa%n", 
            temperatureSetter.getCelsius(),
            temperatureSetter.getKelvin(),
            temperatureSetter.getFahrenheit(),
            temperatureSetter.getInch(),
            temperatureSetter.getMillibar()
          ) ;
        }
    }

   
    public Main.Temperature getTemperatureSetter() {
        return temperatureSetter;
    }

}

  
