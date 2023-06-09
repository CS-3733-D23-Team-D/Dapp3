package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.controllers.JFXitems.DirectionArrow;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.Pacman;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.navigation.Direction;
import edu.wpi.teamname.navigation.Signage;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * The SignageController class is responsible for managing the "Signage" screen of the application.
 * It allows the user to select a date and a kiosk, and then displays the signage information for
 * the selected kiosk and date.
 */
public class SignageController {
  @FXML Label signageSearchLabel;
  @FXML Label dateLabel;
  @FXML Label kioskIDLabel;

  @FXML AnchorPane root;
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
  /**
   * changes the language of the page
   *
   * @param lang language to change it to
   */
  public void setLanguage(Language lang) {
    switch (lang) {
      case ENGLISH:
        ParentController.titleString.set("Signage");
        signageSearchLabel.setText("Signage Search");
        dateLabel.setText("Choose Date");
        kioskIDLabel.setText("Choose Kiosk ID");
        submit.setText("Submit");
        break;
      case SPANISH:
        ParentController.titleString.set(
            "Se" + GlobalVariables.getNTilda() + "alizaci" + GlobalVariables.getOAcute() + "n");
        signageSearchLabel.setText(
            "B"
                + GlobalVariables.getUAcute()
                + "squeda de se"
                + GlobalVariables.getNTilda()
                + "alizaci"
                + GlobalVariables.getOAcute()
                + "n");
        dateLabel.setText("Seleccione fecha");
        kioskIDLabel.setText("Seleccione ID de quiosco");
        submit.setText("Enviar");
        break;
      case FRENCH:
        ParentController.titleString.set("Signalisation");
        signageSearchLabel.setText("Recherche de signalisation");
        dateLabel.setText("Choisir la date");
        kioskIDLabel.setText("Choisir l'ID du kiosque");
        submit.setText("Soumettre");
        break;
      case ITALIAN:
        ParentController.titleString.set("Segnaletica");
        signageSearchLabel.setText("Ricerca segnaletica");
        dateLabel.setText("Scegli data");
        kioskIDLabel.setText("Scegli ID chiosco");
        submit.setText("Invia");
        break;
    }
  }

  @FXML
  public void initialize() throws SQLException {
    ThemeSwitch.switchTheme(root);

    play.setVisible(false);
    play.setDisable(true);
    dateChos.setValue(LocalDate.now());
    dateChosen =
        Timestamp.valueOf(
            dateChos
                .getValue()
                .atTime(12, 0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")));

    ParentController.titleString.set("Signage");
    setLanguage(GlobalVariables.getB().getValue());
    GlobalVariables.b.addListener(
        (options, oldValue, newValue) -> {
          setLanguage(newValue);
        });

    kioskList = FXCollections.observableArrayList();
    kioskList.add(null);
    KskBox.setItems(kioskList);

    try {
      kioskList = FXCollections.observableArrayList(DataManager.getKiosks(dateChosen));
      KskBox.setItems(kioskList);
    } catch (SQLException e) {
      System.out.println(e);
    }
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
          try {
            Sound.playSFX(SFX.BUTTONCLICK);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
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
                            System.out.println("Pac-man!");
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
    play.setOnMouseClicked(
        event -> {
          try {
            Sound.playSFX(SFX.BUTTONCLICK);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
          try {
            Pacman.pacBear();
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });
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
