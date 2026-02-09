import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        
        Main.Temperature temperatureSetter = new Main.Temperature();

        
        WeatherStation station = new WeatherStation(temperatureSetter);
        new Thread(station).start();

        
        new WeatherStationTextUI(station);             // Text UI
        SwingUtilities.invokeLater(() -> new WeatherStationSwingUI(station)); // Swing UI
        new WeatherStationAWTUI(station);             // AWT UI
    }

    public static class Temperature {
        private double kelvin;
        private double celsius;
        private double fahrenheit;
        private double inch;
        private double millibar;

        public synchronized void setTemperatureFromKelvin(int reading) {
            kelvin = reading / 100.0;
            celsius = kelvin - 273.15;
            fahrenheit = kelvin * 9/5 - 459.67;
        }

        public synchronized  void setPressure(double readingPressure) {

            inch = readingPressure;

            millibar = readingPressure / 0.02953;


        }

        public synchronized double getKelvin() { 
            return kelvin; 
        }
        public synchronized double getCelsius() { 
            return celsius; 
        }

        public synchronized  double getFahrenheit() {

            return fahrenheit;
        }

        public synchronized double getInch() {

            return inch;
        }

        public synchronized double getMillibar() {

            return millibar;
        }


    }
}
