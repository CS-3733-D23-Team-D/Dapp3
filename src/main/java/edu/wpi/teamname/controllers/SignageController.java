package edu.wpi.teamname.controllers;

import edu.wpi.teamname.controllers.JFXitems.DirectionArrow;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Pacman;
import edu.wpi.teamname.navigation.Direction;
import edu.wpi.teamname.navigation.Signage;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * The SignageController class is responsible for managing the "Signage" screen of the application.
 * It allows the user to select a date and a kiosk, and then displays the signage information for
 * the selected kiosk and date.
 */
public class SignageController {
  @FXML ComboBox<Integer> KskBox;
  @FXML ObservableList<Integer> kioskList;
  @FXML DatePicker dateChos;
  @FXML MFXButton submit;
  @FXML MFXButton play;
  @FXML VBox textVbox;

  private static boolean submited = false;
  private static Timestamp dateChosen;
  private static ArrayList<Signage> signsForDate = new ArrayList<>();
  private static ArrayList<Direction> directions = new ArrayList<>();

  private static int l = 0;
  private static int r = 0;
  private static int u = 0;
  private static int d = 0;
  private static int s = 0;
  private static int leftC = 0;
  private static int rightC = 0;
  private static int upC = 0;
  private static int downC = 0;
  private static int stopC = 0;

  /** Initializes the SignageController and sets up the UI elements and functionality. */
  @FXML
  public void initialize() throws SQLException, IOException {
    play.setVisible(false);
    play.setDisable(true);
    ParentController.titleString.set("Signage");
    kioskList = FXCollections.observableArrayList();
    kioskList.add(null);
    KskBox.setItems(kioskList);
    dateChos
        .valueProperty()
        .addListener(
            (t, o, n) -> {
              submited = false;
              if (dateChos.getValue() != null) {
                System.out.println(
                    dateChos
                        .getValue()
                        .atTime(12, 0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")));
                dateChosen =
                    Timestamp.valueOf(
                        dateChos
                            .getValue()
                            .atTime(12, 0)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")));
                try {
                  kioskList = FXCollections.observableArrayList(DataManager.getKiosks(dateChosen));
                  KskBox.setItems(kioskList);
                } catch (SQLException e) {
                  System.out.println(e);
                }
              }
            });

    KskBox.setOnAction(
        event -> {
          submited = false;
          if (KskBox.getValue() != null) {
            try {
              signsForDate.clear();
              signsForDate = DataManager.getSignages(KskBox.getValue(), dateChosen);
            } catch (SQLException e) {
              System.out.println(e);
            }
          }
        });

    submit.setOnMouseClicked(
        event -> {
          directions.clear();
          leftC = 0;
          rightC = 0;
          upC = 0;
          downC = 0;
          stopC = 0;
          play.setVisible(false);
          play.setDisable(true);
          submited = true;
          textVbox.getChildren().clear();
          System.out.println(signsForDate);
          for (int i = 0; i < signsForDate.size(); i++) {
            Direction dir = signsForDate.get(i).getArrowDirection();
            directions.add(dir);
            String text = signsForDate.get(i).getLongName();
            DirectionArrow da = new DirectionArrow(dir, text, 100);
            da.setMaxHeight(100);
            textVbox.getChildren().add(da);
          }
          fillDir();
        });

    Platform.runLater(
        () ->
            submit
                .getScene()
                .addEventFilter(
                    KeyEvent.KEY_PRESSED,
                    event -> {
                      if (submited) {
                        System.out.println(event.getCode());

                        if (event.getCode().equals(KeyCode.LEFT)) {
                          leftC++;
                        } else if (event.getCode().equals(KeyCode.RIGHT)) {
                          rightC++;
                        } else if (event.getCode().equals(KeyCode.DOWN)) {
                          downC++;
                        } else if (event.getCode().equals(KeyCode.UP)) {
                          upC++;
                        } else if (event.getCode().equals(KeyCode.SPACE)) {
                          stopC++;
                        } else if (event.getCode().equals(KeyCode.ENTER)) {
                          System.out.println(
                              leftC + ", " + rightC + ", " + downC + ", " + upC + ", " + stopC);
                          System.out.println(l + ", " + r + ", " + d + ", " + u + ", " + s);
                          if (leftC == l && rightC == r && upC == u && downC == d && stopC == s) {
                            play.setVisible(true);
                            play.setDisable(false);
                            System.out.println("Pacman!");
                          }
                          leftC = 0;
                          rightC = 0;
                          upC = 0;
                          downC = 0;
                          stopC = 0;
                        } else {
                          System.out.println("Nothing");
                        }
                      }
                      event.consume();
                    }));
    play.setOnMouseClicked(event -> Pacman.pacBear());
  }

  private void fillDir() {
    l = 0;
    r = 0;
    u = 0;
    d = 0;
    s = 0;
    // get the specific directions for specific signage
    for (int i = 0; i < directions.size(); i++) {
      String dir = directions.get(i).toString();
      switch (dir) {
        case "LEFT":
          l++;
          break;
        case "RIGHT":
          r++;
          break;
        case "UP":
          u++;
          break;
        case "DOWN":
          d++;
          break;
        case "STOPHERE":
          s++;
          break;
      }
    }
  }
}
