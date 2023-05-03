package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;

public class AboutPageController {

  @FXML Text titleLabel;
  @FXML Text thanksLabel;
  @FXML Text specialThanksLabel;
  @FXML MFXButton ianButton;
  @FXML MFXButton jasonButton;
  @FXML MFXButton alessandroButton;
  @FXML MFXButton adelynnButton;
  @FXML MFXButton aleksandrButton;
  @FXML MFXButton samuelButton;
  @FXML MFXButton ryanButton;
  @FXML MFXButton conorButton;
  @FXML MFXButton hunterButton;
  @FXML MFXButton arturoButton;
  @FXML MFXButton andrewButton;
  @FXML VBox memberVBox;
  @FXML AnchorPane aboutAnchorPane;

  public void setLanguage(Language lang) {
    switch (lang) {
      case ENGLISH:
        titleLabel.setText(
            "WPI Computer Science Department\n"
                + "\n"
                + "CS3733-D23 Software Engineering\n"
                + "\n"
                + "Prof. Wilson Wong");
        thanksLabel.setText("Thank you!");
        specialThanksLabel.setText(
            "Special thank you to Brigham and Women’s Hospital,\n"
                + "\n"
                + "and their representative Andrew Shinn,\n"
                + "\n"
                + "for their time and input with this project.");
        break;
      case ITALIAN:
        titleLabel.setText(
            "Dipartimento di Informatica del WPI\n"
                + "\n"
                + "Ingegneria del software CS3733-D23\n"
                + "\n"
                + "Prof. Wilson Wong");
        thanksLabel.setText("Grazie!");
        specialThanksLabel.setText(
            "Un ringraziamento speciale à Brigham and Women’s Hospital,\n"
                + "\n"
                + "e al loro rappresentante Andrew Shinn,\n"
                + "\n"
                + "per il loro tempo e contributo a questo progetto.");
        break;
      case SPANISH:
        titleLabel.setText(
            "Departamento de Ciencias de la Computación del WPI\n"
                + "\n"
                + "Ingeniería de Software CS3733-D23\n"
                + "\n"
                + "Prof. Wilson Wong");
        thanksLabel.setText("¡Gracias!");
        specialThanksLabel.setText(
            "Agradecimiento especial al Hospital Brigham and Women,\n"
                + "\n"
                + "y su representante Andrew Shinn,\n"
                + "\n"
                + "por su tiempo y aporte en este proyecto.");
        break;
      case FRENCH:
        titleLabel.setText(
            "Département d'Informatique du WPI\n"
                + "\n"
                + "Ingénierie Logicielle CS3733-D23\n"
                + "\n"
                + "Prof. Wilson Wong");
        thanksLabel.setText("Merci!");
        specialThanksLabel.setText(
            "Un remerciement spécial à l'Hôpital Brigham and Women,\n"
                + "\n"
                + "et leur représentant Andrew Shinn,\n"
                + "\n"
                + "pour leur temps et leur contribution à ce projet.");
        break;
    }
  }

  public void initialize() {
    ThemeSwitch.switchTheme(aboutAnchorPane);
    MFXButton.class.getClassLoader().setDefaultAssertionStatus(false);
    ParentController.titleString.set("About");
    setLanguage(GlobalVariables.getB().getValue());
    GlobalVariables.b.addListener(
        (options, oldValue, newValue) -> {
          setLanguage(newValue);
        });

    EventHandler<MouseEvent> PersonPopup =
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            Sound.playSFX(SFX.BUTTONCLICK);
            MFXButton clickedButton = (MFXButton) event.getSource();
            String fxmlFileName = null;

            switch (clickedButton.getId()) {
              case "ianButton":
                fxmlFileName = "IanPopup.fxml";
                break;
              case "jasonButton":
                fxmlFileName = "JasonPopup.fxml";
                break;
              case "alessandroButton":
                fxmlFileName = "AlessandroPopup.fxml";
                break;
              case "adelynnButton":
                fxmlFileName = "AddyPopup.fxml";
                break;
              case "aleksandrButton":
                fxmlFileName = "AlekPopup.fxml";
                break;
              case "samuelButton":
                fxmlFileName = "SamPopup.fxml";
                break;
              case "ryanButton":
                fxmlFileName = "RyanPopup.fxml";
                break;
              case "conorButton":
                fxmlFileName = "ConorPopup.fxml";
                break;
              case "hunterButton":
                fxmlFileName = "HunterPopup.fxml";
                break;
              case "arturoButton":
                fxmlFileName = "ArturoPopup.fxml";
                break;
              case "andrewButton":
                fxmlFileName = "AndrewPopup.fxml";
                break;
              default:
                break;
            }

            if (fxmlFileName != null) {
              final var resource =
                  getClass().getResource("/edu/wpi/teamname/views/" + fxmlFileName);
              final FXMLLoader loader = new FXMLLoader(resource);
              VBox popupContent;

              try {
                popupContent = loader.load();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              MFXButton close =
                  (MFXButton) ((Pane) popupContent.getChildren().get(3)).getChildren().get(0);

              PopOver popOver = new PopOver(popupContent);
              popOver.show(clickedButton);
              close.setOnMouseClicked(
                  event1 -> {
                    Sound.playSFX(SFX.BUTTONCLICK);
                    popOver.hide();
                  });
            }
          }
        };

    // Set the event handler on each button
    ianButton.setOnMouseClicked(PersonPopup);
    jasonButton.setOnMouseClicked(PersonPopup);
    alessandroButton.setOnMouseClicked(PersonPopup);
    adelynnButton.setOnMouseClicked(PersonPopup);
    aleksandrButton.setOnMouseClicked(PersonPopup);
    samuelButton.setOnMouseClicked(PersonPopup);
    ryanButton.setOnMouseClicked(PersonPopup);
    conorButton.setOnMouseClicked(PersonPopup);
    hunterButton.setOnMouseClicked(PersonPopup);
    arturoButton.setOnMouseClicked(PersonPopup);
    andrewButton.setOnMouseClicked(PersonPopup);

    // Set the event handler for each button and their associated FXML file
  }
}

    /**
     * EventHandler<MouseEvent> PersonPopup = new EventHandler<MouseEvent>() { @Override public void
     * handle(MouseEvent event) {
     *
     * <p>MFXButton createNewButton = ((MFXButton) event.getSource());
     *
     * <p>final var resource = App.class.getResource("IanPopup.fxml"); final FXMLLoader loader = new
     * FXMLLoader(resource); VBox v;
     *
     * <p>try { v = loader.load(); } catch ( IOException e) { throw new RuntimeException(e); }
     *
     * <p>PopOver pop = new PopOver(v);
     *
     * <p>pop.show(createNewButton); } }; } } *
     */
