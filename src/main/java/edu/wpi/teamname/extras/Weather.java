package edu.wpi.teamname.extras;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.teamname.GlobalVariables;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.SneakyThrows;

public class Weather implements Runnable {
    private static String location = "Worcester";
    private static String API_KEY = "7fb93b3f27be83d9f1c8eb7c0ac90ba1";
    private String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private boolean doStop = false;

    public static String getTemperature() throws IOException {
        URL url =
                new URL(
                        "http://api.openweathermap.org/data/2.5/weather?q="
                                + location
                                + "&units=imperial&appid="
                                + API_KEY);
        ObjectMapper MAPPER = new ObjectMapper();
        JsonNode weather = MAPPER.readTree(url);
        JsonNode temperature = weather.path("main").path("temp");
        int temp = (int) Double.parseDouble(temperature.toString());
        return Integer.toString(temp) + " F";
    }

    public static int getTemp() throws IOException {
        URL url =
                new URL(
                        "http://api.openweathermap.org/data/2.5/weather?q="
                                + location
                                + "&units=imperial&appid="
                                + API_KEY);
        ObjectMapper MAPPER = new ObjectMapper();
        JsonNode weather = MAPPER.readTree(url);
        JsonNode temperature = weather.path("main").path("temp");
        return (int) Double.parseDouble(temperature.toString());
    }

    public static String getDescription() throws IOException {
        URL url =
                new URL(
                        "http://api.openweathermap.org/data/2.5/weather?q="
                                + location
                                + "&units=metric&appid="
                                + API_KEY);
        ObjectMapper MAPPER = new ObjectMapper();
        JsonNode weather = MAPPER.readTree(url);
        JsonNode description = weather.path("weather").get(0).path("description");
        return description.toString();
    }

    public static String getTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
    }

    public static String getDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = dateFormat.format(currentDate);
        return formattedDate;
    }

    public static int getMin() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm");
        String formattedTime = currentTime.format(formatter);
        return Integer.parseInt(formattedTime);
    }

    public static int getDay() {
        Date currentDay = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String formattedDay = dayFormat.format(currentDay);
        return Integer.parseInt(formattedDay);
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    @SneakyThrows
    @Override
    public void run() {
        int prevMin = getMin();
        int prevDate = getDay();
        int prevTemp = getTemp();
        String prevUser = GlobalVariables.getCurrentUser().getUsername();

        while (!doStop) {
            if (prevMin != getMin()) {
                System.out.println(getTime());
                prevMin = getMin();
            }
            if (prevDate != getDay()) {
                System.out.println(getDate());
                System.out.println(prevDate);
                prevDate = getDay();
            }
            //      if (prevTemp != getTemp()) {
            //        System.out.println(getTemperature());
            //        prevTemp = getTemp();
            //      }
            if (prevUser != GlobalVariables.getCurrentUser().getUsername()) {
                System.out.println(GlobalVariables.getCurrentUser().getUsername());
                prevUser = GlobalVariables.getCurrentUser().getUsername();
            }
            Thread.sleep(100);
        }
    }
}
