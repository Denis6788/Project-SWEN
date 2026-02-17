
public class KelvinTempSensorAdapter implements ITempSensor {

    private KelvinTempSensor sensor;

    public KelvinTempSensorAdapter(KelvinTempSensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public int reading() {
        int kelvinHundredths = sensor.reading();     
        int celsius = (kelvinHundredths - 27315) / 100; 
        return celsius;
    }
}
