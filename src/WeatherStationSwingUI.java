
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

public class WeatherStationSwingUI extends JFrame implements Observer {

    private JLabel celsiusLabel;
    private JLabel kelvinLabel; 
    private JLabel fahrenheitLabel;
    private JLabel inchLabel;
    private JLabel millibarLabel;
    public Main.Temperature temperatureSetter;


    public WeatherStationSwingUI(WeatherStation ts) {
        this.temperatureSetter = ts.getTemperatureSetter();
        ts.addObserver(this);

        setTitle("Weather Station Swing");
        setLayout(new GridLayout(5,4));

        JPanel panel = createPanel(); 
        add(panel);


        pack();
        setVisible(true);


    }

    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }


    private JPanel createPanel() {
        JPanel panel = new JPanel(new GridLayout(5,4));

        kelvinLabel = createLabel("Kelvin");
        celsiusLabel = createLabel("Celsius");
        fahrenheitLabel = createLabel("Fahrenheit");
        inchLabel = createLabel("Inches");
        millibarLabel = createLabel("Millibars");


        panel.add(kelvinLabel);
        panel.add(celsiusLabel);
        panel.add(fahrenheitLabel);
        panel.add(inchLabel);
        panel.add(millibarLabel);

        return panel;
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            synchronized (temperatureSetter) {
                kelvinLabel.setText(
                    String.format("Kelvin: %6.2f", temperatureSetter.getKelvin())
                );
                celsiusLabel.setText(
                    String.format("Celsius: %6.2f", temperatureSetter.getCelsius())
                );
                fahrenheitLabel.setText( 
                    String.format("Fahrenheit: %6.2f", temperatureSetter.getFahrenheit())
                );
                inchLabel.setText(
                    String.format("Inches: %6.2f", temperatureSetter.getInch())

                );
                millibarLabel.setText(
                    String.format("Millibars: %6.2f", temperatureSetter.getMillibar() )
                );
        
            }
        });
    }
    
}




    





    
    

    
