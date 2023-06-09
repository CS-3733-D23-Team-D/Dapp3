package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.Feedback;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.servicerequest.Status;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class FeedbackController {
  @FXML Label bugReportLabel;
  @FXML AnchorPane root;
  @FXML TextArea descriptionField;

  private DataManager dataManager;

  /**
   * Changes the language of the app for this page
   *
   * @param lang to set it to
   */
  public void setLanguage(Language lang) {
    switch (lang) {
      case ENGLISH:
        ParentController.titleString.set("Feedback Submission");
        bugReportLabel.setText("Bug Report");
        descriptionField.setPromptText("Give a description of what occurred...");
        break;
      case ITALIAN:
        ParentController.titleString.set("Invio Commenti");
        bugReportLabel.setText("Segnala un Problema");
        descriptionField.setPromptText(
            "Fornisci una descrizione di ci"
                + GlobalVariables.getOGrave()
                + " che "
                + GlobalVariables.getEGrave()
                + " accaduto...");
        break;
      case FRENCH:
        ParentController.titleString.set("Soumission de Commentaires");
        bugReportLabel.setText("Signalement de Problème");
        descriptionField.setPromptText("Donnez une description de ce qui s'est produit...");
        break;
      case SPANISH:
        ParentController.titleString.set("Env" + GlobalVariables.getIAcute() + "o de Comentarios");
        bugReportLabel.setText("Informe de Error");
        descriptionField.setPromptText(
            "Proporcione una descripci"
                + GlobalVariables.getOAcute()
                + "n de lo que ocurri"
                + GlobalVariables.getOAcute()
                + "...");
        break;
    }
  }

  public void initialize() {
    ThemeSwitch.switchTheme(root);
    setLanguage(GlobalVariables.getB().getValue());
    GlobalVariables.b.addListener(
        (options, oldValue, newValue) -> {
          setLanguage(newValue);
        });
    ParentController.titleString.set("Feedback Submission");
    dataManager = new DataManager();
  }

  @FXML
  private void handleSubmit() throws URISyntaxException {
    String description = descriptionField.getText();

    Feedback feedback = new Feedback();
    feedback.setReporter(
        String.valueOf(
            GlobalVariables.getCurrentUser()
                .getUsername())); // Replace with the actual reporter info
    feedback.setDateReported(new Timestamp(System.currentTimeMillis()));
    feedback.setDescription(description);
    feedback.setAssignee("Unassigned");
    feedback.setStatus(Status.BLANK);

    try {
      dataManager.addFeedback(feedback);
      descriptionField.clear();
      Sound.playSFX(SFX.SUCCESS);
      Navigation.navigate(Screen.SETTINGS);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      showErrorAlert("Error", "Failed to submit feedback", e.getMessage());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void showErrorAlert(String title, String header, String content)
      throws URISyntaxException {
    Sound.playSFX(SFX.ERROR);
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
