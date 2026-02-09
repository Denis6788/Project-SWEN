
import java.awt.* ;
import java.awt.event.* ;
import java.util.Observable;
import java.util.Observer;

public class WeatherStationAWTUI extends Frame implements Observer{

    public Label kelvinLabel;
    public Label celsiusLabel;
    public Label fahrenheitLabel;
    public Label inchLabel;
    public Label millibarLabel;
    public Main.Temperature temperatureSetter;
    




 public class Temperature {

 }


 public WeatherStationAWTUI(WeatherStation ts) {

    this.temperatureSetter = ts.getTemperatureSetter();
    ts.addObserver(this);

    setTitle("Weather station AWT");
    setSize(500, 700);
    setLayout(new GridLayout(2,5));

    Panel panel = createPanel();
        add(panel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
         pack();

        setVisible(true);
    }
        
  
     
     private Panel createPanel() {
     Panel panel = new Panel();
     panel.setLayout(new GridLayout(5, 3));

     kelvinLabel = new Label("Kelvin temperature");
     celsiusLabel = new Label("Celsius temperature");
     fahrenheitLabel = new Label("Fahrenheit temperature");
     inchLabel = new Label("Inch pressure");
     millibarLabel = new Label("Millibar pressure");


      panel.add(kelvinLabel);
      panel.add(celsiusLabel);
      panel.add(fahrenheitLabel);
      panel.add(inchLabel);
      panel.add(millibarLabel);


     return panel;

    }

  

   
    @Override
    public void update(Observable o, Object arg) {
        java.awt.EventQueue.invokeLater(() -> {
            synchronized (temperatureSetter) {
                kelvinLabel.setText(String.format("Kelvin: %6.2f", temperatureSetter.getKelvin()));
                celsiusLabel.setText(String.format("Celsius: %6.2f", temperatureSetter.getCelsius()));
                fahrenheitLabel.setText(String.format("Fahrenheit: %6.2f", temperatureSetter.getFahrenheit()));
                inchLabel.setText(String.format("Inches: %6.2f", temperatureSetter.getInch()));
                millibarLabel.setText(String.format("Millibars: %6.2f", temperatureSetter.getMillibar()));
                

            }
        });
    }
 



 
      


}