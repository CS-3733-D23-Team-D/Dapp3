package edu.wpi.teamname.extras;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Weather {
  private Date currentDate;
  private Date currentTime;

  private String location = "Worcester";
  private String API_KEY = "7fb93b3f27be83d9f1c8eb7c0ac90ba1";
  private String API_URL = "https://api.openweathermap.org/data/2.5/weather";

  public int getTemperature() throws IOException {
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
    return temp;
  }

  public String getDescription() throws IOException {
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

  public String getTime() {
    LocalTime currentTime = LocalTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String formattedTime = currentTime.format(formatter);
    return formattedTime;
  }

  public String getDate() {
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    String formattedDate = dateFormat.format(currentDate);
    return formattedDate;
  }
}
