package edu.wpi.teamname;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.wpi.teamname.database.DataManager;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;
import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class Main {
  public static void main(String[] args) throws SQLException, IOException, ParseException {

    DataManager.configConnection(
        "jdbc:postgresql://database.cs.wpi.edu:5432/teamddb?currentSchema=\"teamD\"",
        "teamd",
        "teamd40");
    // App.launch(App.class, args);
    try {
      // Set property as Kevin Dictionary
      System.setProperty(
          "freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

      // Register Engine
      Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");

      // Create a Synthesizer
      Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

      // Allocate synthesizer
      synthesizer.allocate();
      synthesizer.getSynthesizerProperties().setSpeakingRate(150);
      // synthesizer.getSynthesizerProperties().setVoice();
      // Resume Synthesizer
      synthesizer.resume();

      // Speaks the given text
      // until the queue is empty.
      synthesizer.speakPlainText("test", null);
      synthesizer.speakPlainText("Edit Map", null);
      synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

      // Deallocate the Synthesizer.
      synthesizer.deallocate();
      VoiceManager voiceManager = VoiceManager.getInstance();
      Voice[] voices = voiceManager.getVoices();
      for (Voice v : voices) {
        System.out.println(v.getName() + " " + v.getDescription());
      }
      Voice voice = voiceManager.getVoice("kevin16");
      voice.allocate();
      voice.speak("Hello, world!");
      voice.deallocate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // shortcut: psvm
}
