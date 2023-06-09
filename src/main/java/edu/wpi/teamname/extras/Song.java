package edu.wpi.teamname.extras;

import edu.wpi.teamname.App;

public enum Song {
  OTJANBIRD1("Otjanbird-Pt.-I.mp3", "Otjanbird Pt. I"),
  OTJANBIRD2("Otjanbird-Pt.-II.mp3", "Otjanbird Pt. II"),
  OTJANBIRD3("Otjanbird-Pt.-III.mp3", "Otjanbird Pt. III"),
  CRUISINALONG("cruisin-along.mp3", "Cruisin Along"),
  EVENINGIMPROVISATION(
      "Evening-Improvisation-with-Ethera.mp3", "Evening Improvisation (with Ethera)"),
  HOMEWORK("homework.mp3", "Homework"),
  SERENA("serena.mp3", "Serena"),
  SKOGUR("skogur.mp3", "Skogur"),
  JETPACKJOYRIDE("jetpackjoyridegoodloop.mp3", "Jetpack Joyride Main Theme");

  private final String filename;
  private final String title;

  Song(String filename, String title) {
    this.filename = filename;
    this.title = title;
  }

  public String getFilename() {
    String fn = "sounds/" + filename;
    // return getClass().getResource(fn);
    return App.class.getResource(fn).toString();
  }

  public String getTitle() {
    return title;
  }
}
