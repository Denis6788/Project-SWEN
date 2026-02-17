public class Main {

    public static void main(String[] args) {

        
        Main.Temperature temperatureSetter = new Main.Temperature();

     
        ITempSensor tempSensor =
            new KelvinTempSensorAdapter(new KelvinTempSensor());

        IBarometer barometer = new Barometer();

       
        WeatherStation station =
            new WeatherStation(temperatureSetter, tempSensor, barometer);

        new Thread(station).start();
    }

 
    public static class Temperature {
        private double kelvin;
        private double celsius;
        private double fahrenheit;
        private double inch;
       

        public synchronized void setTemperatureFromKelvin(int reading) {
            kelvin = reading / 100.0;
            celsius = kelvin - 273.15;
            fahrenheit = kelvin * 9 / 5 - 459.67;
        }

        public synchronized void setPressure(double readingPressure) {
            inch = readingPressure;
            
        }

        public synchronized double getKelvin() {
            return kelvin;
        }

        public synchronized double getCelsius() {
            return celsius;
        }

        public synchronized double getFahrenheit() {
            return fahrenheit;
        }

        public synchronized double getInch() {
            return inch;
        }

       
    }
}
