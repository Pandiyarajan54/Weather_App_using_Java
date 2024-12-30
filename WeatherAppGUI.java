import org.json.simple.JSONObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class WeatherAppGUI extends JFrame {

    private JSONObject weatherData;


    public WeatherAppGUI() {

        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(500,600);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGUIComponents();

        setVisible(true);

    }

    private void addGUIComponents(){


        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15,15,400,40);
        searchTextField.setFont(new Font( "Dialog",Font.PLAIN,25));
        add(searchTextField);


        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(90,60,250,250);
        add(weatherConditionImage);


        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(10,280,400,60);
        temperatureText.setFont(new Font("Dialog",Font.BOLD,35));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);


        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(60,340,300,30);
        weatherConditionDesc.setFont(new Font("Dialog",Font.BOLD,22));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);


        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(20,440,60,70);
        add(humidityImage);


        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(85,440,70,30);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,13));
        add(humidityText);


        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(270,440,80,70);
        add(windspeedImage);
        
        
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(370,450,90,30);
        windspeedText.setFont(new Font("Dialog",Font.PLAIN,13));
        add(windspeedText);


        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(415,15,50,40);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = searchTextField.getText();

                if(userInput.replaceAll("\\s","").length() <= 0){
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){

                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;

                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;

                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;

                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;


                }


                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> "+ humidity +"%</html>");


                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> "+ windspeed+ "km/h</html>");




            }
        });
        add(searchButton);




    }


    private ImageIcon loadImage(String sourcePath)
    {
        try {

            BufferedImage image = ImageIO.read(new File((sourcePath)));
            return new ImageIcon(image);
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
        System.out.println("Could  not find resource ");
        return null;
    }

}
