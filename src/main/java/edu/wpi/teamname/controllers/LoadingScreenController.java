package edu.wpi.teamname.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class LoadingScreenController {
  @FXML StackPane load1;
  @FXML StackPane load2;
  @FXML StackPane load3;

  @FXML
  public void initialize(int times) throws InterruptedException {
    System.out.println("Loading");
    for (int i = 0; i < times; i++) { // loop how ever many times, each time is 5 seconds
      //      load1.setVisible(false);
      //      load3.setVisible(false);
      //      load2.setVisible(false);
      //      Thread.sleep(1 * 1000); // 1 second
      //      load1.setVisible(true);
      //      Thread.sleep(1 * 1000); // 1 second
      //      load2.setVisible(true);
      //      Thread.sleep(1 * 1000); // 1 second
      //      load3.setVisible(true);
      Thread.sleep(2 * 1000); // 2 seconds
    }
  }
}
