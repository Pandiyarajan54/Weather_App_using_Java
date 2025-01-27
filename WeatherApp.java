import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {



    public static JSONObject getWeatherData(String locationName) {


        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";
        JSONObject weatherData = null;
        try {

            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error! Could not connect to API !!");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));


            JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");

            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherData;

    }

    public static JSONArray getLocationData(String locationName){

        locationName = locationName.replaceAll(" ","+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
               locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);


            if(conn.getResponseCode() != 200){
                System.out.println("Error! Could not connect to API !");
                return null;
            }

            else{

                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());


                while(scanner.hasNext()){

                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();


                JSONParser parser = new JSONParser();
                JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultJsonObject.get("results");
                return locationData;

            }
        }
        catch(Exception e){
            e.printStackTrace();

        }


        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){

        try{

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();

            return conn;

        }
        catch(Exception e){
            e.printStackTrace();
        }


        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {

        String currentTime = getCurrentTime();

        for(int i=0;i<timeList.size();i++){

            String time = (String) timeList.get(i);

            if(time.equalsIgnoreCase(currentTime))
                return i;

        }


        return 0;




    }

    public static String getCurrentTime(){

        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    public static String convertWeatherCode(long weatherCode){

        String WeatherCondition = "";

        if(weatherCode == 0L){
            WeatherCondition = "Clear";}

        else if(weatherCode > 0L && weatherCode <= 3L){
            WeatherCondition = "Cloudy";}

        else if((weatherCode >= 51L && weatherCode <= 67L )|| (weatherCode >= 80L && weatherCode <= 99L)){
            WeatherCondition = "Rain";}

        else if(weatherCode >= 71L && weatherCode <= 77L){
            WeatherCondition = "Snow";}


        return WeatherCondition;
    }

}
