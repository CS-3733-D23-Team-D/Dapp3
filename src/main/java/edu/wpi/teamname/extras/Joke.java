package edu.wpi.teamname.extras;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;*/

public class Joke {
  @Getter @Setter int id;
  @Getter @Setter String type;
  @Getter @Setter String setup;
  @Getter @Setter String punchline;
  private static final String API_URL =
      "https://official-joke-api.appspot.com/jokes/general/random";
  private static final ArrayList<Integer> badJokeIDs =
      new ArrayList<>(
          List.of(
              53, 92, 45, 208, 46, 48, 140, 133, 238, 81, 203, 280, 399, 144, 276, 238, 364, 79, 98,
              91, 101, 141, 202, 278, 39));

  public Joke(String type, String setup, String punchline, int id) {
    this.type = type;
    this.setup = setup;
    this.punchline = punchline;
    this.id = id;
  }

  public Joke() {}

  // Don't delete this code, the program does not start up if it's deleted
  /*public static Joke getJoke() throws IOException {
    // create a Retrofit instance
    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl("https://official-joke-api.appspot.com/jokes/programming/random")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // create an instance of the API interface
    JokeAPI jokeApi = retrofit.create(JokeAPI.class);

    // make a request to the API
    Call<Joke> call = jokeApi.getRandomJoke("programming", new int[] {53, 92, 45, 208, 46});
    Response<Joke> response = call.execute();

    // check if the request was successful
    if (response.isSuccessful()) {
      Joke joke = response.body();
      return joke;
    } else {
      return null;
    }
  }*/

  public String toString() {
    if (punchline.charAt(0) == ' ') {
      punchline = punchline.substring(1);
    }
    // Fixes Typo
    if (id == 312) {
      setup = "Why can't you use \"Beef stew\" as a password?";
    }
    if (id == 79) {
      punchline = "Pop, goes the weasel.";
    }
    return this.getSetup() + "\n\n" + this.getPunchline();
  }

  /**
   * * Queries the Official Joke API and gets a random general joke If the joke is one of the bad
   * jokes, it queries again until it is a good joke
   *
   * @return the joke to display on the homepage
   * @throws IOException Connection to the API fails
   */
  public static Joke getJoke(int count) throws IOException {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL).build();
    Response response = client.newCall(request).execute();
    String responseBody = response.body().string();
    ObjectMapper mapper = new ObjectMapper();

    try {
      ArrayList<Joke> jokes =
          mapper.readValue(responseBody, new TypeReference<ArrayList<Joke>>() {});
      if (badJokeIDs.contains(jokes.get(0).getId())) {
        if (count > 3) {
          return new Joke(
              "general", "Why did the chicken cross the road?", "To get to the other side!", 0);
        } else {
          return getJoke(count + 1);
        }
      }
      return jokes.get(0);
    } catch (Exception e) {
      Joke joke = mapper.readValue(responseBody, new TypeReference<Joke>() {});
      if (badJokeIDs.contains(joke.getId())) {
        System.out.println("here");
        if (count > 3) {
          return new Joke(
              "general", "Why did the chicken cross the road?", "To get to the other side!", 0);
        } else {
          return getJoke(count + 1);
        }
      }
      return joke;
    }
  }
}
