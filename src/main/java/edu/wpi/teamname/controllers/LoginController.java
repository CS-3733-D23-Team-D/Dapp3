package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.alerts.Alert;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Song;
import edu.wpi.teamname.extras.Sound;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class LoginController {
  @FXML Label loginLabel;
  @FXML MFXButton exit;
  @FXML AnchorPane rootPane;
  @FXML StackPane paneOfStuff;
  //  String tempPassword;
  @FXML Label newPassword;
  @FXML Label success;
  @FXML MFXButton loginButton;
  @FXML MFXButton forgotPassword;
  @FXML TextField loginText;
  @FXML PasswordField passwordText;
  @FXML MFXButton cancel;
  // @FXML MFXButton help;
  private String tempUser;
  private int failedCounter;

  /**
   * handles when the login button is pressed
   *
   * @param username username
   * @param password password
   * @return a boolean if the login was successful or not
   * @throws SQLException if there is an error connecting to the database
   * @throws ExceptionInInitializerError for testing, when we change pages without initializing the
   *     screen
   */
  public boolean loginPressed(String username, String password)
      throws SQLException, ExceptionInInitializerError, URISyntaxException {
    Employee user = DataManager.checkLogin(username, password);
    if (user != null) {
      GlobalVariables.setCurrentUser(user);
      if (user.getUsername().equals("ian")) {
        Sound.setSong(Song.JETPACKJOYRIDE);
      }
      HomeController.setLoggedIn(new SimpleBooleanProperty(true));
      Navigation.navigate(GlobalVariables.getPreviousScreen());
      return true;
    } else {
      if (tempUser == null) {
        tempUser = username;
        failedCounter = 1;
      } else if (tempUser.equals(username)) {
        failedCounter++;
      } else {
        tempUser = username;
        failedCounter = 1;
      }

      if (failedCounter == 5) {
        Timestamp ts = Timestamp.from(Instant.now());
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        cal.add(Calendar.DAY_OF_WEEK, 14);
        Timestamp tmrw = new Timestamp(cal.getTime().getTime());
        Alert alert =
            new Alert(
                Instant.now().get(ChronoField.MICRO_OF_SECOND),
                ts,
                tmrw,
                "admin",
                "Five failed attempts",
                "User " + username + " failed login 5 times in a row.",
                EmployeeType.ADMINISTRATOR,
                Alert.Urgency.MEDIUM);
        DataManager.addAlert(alert);
      }

      return false;
    }
  }

  /**
   * changes the labels to a diff language
   *
   * @param lang language to set it to
   */
  public void setLanguage(Language lang) {
    switch (lang) {
      case ENGLISH:
        loginLabel.setText("Login");
        loginText.setPromptText("Username");
        passwordText.setPromptText("Password");
        loginButton.setText("Login");
        cancel.setText("Cancel");
        forgotPassword.setText("Forgot Password");
        exit.setText("Exit");
        break;
      case ITALIAN:
        loginLabel.setText("Accesso");
        loginText.setPromptText("Nome utente");
        passwordText.setPromptText("Password");
        loginButton.setText("Accedi");
        cancel.setText("Annulla");
        forgotPassword.setText("Password dimenticata");
        exit.setText("Esci");
        break;
      case FRENCH:
        loginLabel.setText("Connexion");
        loginText.setPromptText("Nom d'utilisateur");
        passwordText.setPromptText("Mot de passe");
        loginButton.setText("Se connecter");
        cancel.setText("Annuler");
        forgotPassword.setText("Mot de passe oubli" + GlobalVariables.getEAcute());
        exit.setText("Sortir");
        break;
      case SPANISH:
        loginLabel.setText("Inicio de sesi" + GlobalVariables.getOAcute() + "n");
        loginText.setPromptText("Nombre de usuario");
        passwordText.setPromptText("Contrase" + GlobalVariables.getNTilda() + "a");
        loginButton.setText("Iniciar sesi" + GlobalVariables.getOAcute() + "n");
        cancel.setText("Cancelar");
        forgotPassword.setText(
            "¿Olvid"
                + GlobalVariables.getOAcute()
                + " su contrase"
                + GlobalVariables.getNTilda()
                + "a?");
        exit.setText("Salir");
        break;
    }
  }

  /** initializes the view for the login page */
  @FXML
  public void initialize() {
    ThemeSwitch.switchTheme(rootPane);
    setLanguage(GlobalVariables.getB().getValue());
    GlobalVariables.b.addListener(
        (options, oldValue, newValue) -> {
          setLanguage(newValue);
        });
    // help.setVisible(false);
    newPassword.setVisible(false);
    success.setText("Username or password\nis incorrect");
    success.setVisible(false);
    exit.setOnMouseClicked(
        event -> {
          try {
            Sound.playSFX(SFX.BUTTONCLICK);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
          try {
            Connection connection = DataManager.DbConnection();
            connection.close();
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
          System.exit(0);
        });
    forgotPassword.disableProperty().bind(Bindings.isEmpty(loginText.textProperty()));
    loginButton.disableProperty().bind(Bindings.isEmpty(loginText.textProperty()));
    loginButton.disableProperty().bind((Bindings.isEmpty(passwordText.textProperty())));
    forgotPassword.setOnMouseClicked(
        event -> {
          try {
            String tempPassword = forgotPasswordPressed(loginText.getText());
            newPassword.setText(tempPassword);
            newPassword.setVisible(true);
            paneOfStuff.setDisable(true);
          } catch (SQLException | URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });
    rootPane.setOnMouseClicked(
        event -> {
          paneOfStuff.setDisable(false);
          newPassword.setVisible(false);
          success.setVisible(false);
        });

    loginButton.setOnMouseClicked(
        event -> {
          try {
            boolean temp = loginPressed(loginText.getText(), passwordText.getText());
            if (!temp) {
              Sound.playSFX(SFX.ERROR);
              paneOfStuff.setDisable(true);
              success.setVisible(true);
              passwordText.clear();
            }
          } catch (SQLException | URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });
    cancel.setOnMouseClicked(
        event -> {
          try {
            Navigation.navigate(GlobalVariables.getPreviousScreen());
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });

    passwordText.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER
              && !passwordText.textProperty().equals("")
              && !loginText.textProperty().equals("")) {
            try {
              boolean temp = loginPressed(loginText.getText(), passwordText.getText());
              if (!temp) {
                paneOfStuff.setDisable(true);
                success.setVisible(true);
                passwordText.clear();
              }
            } catch (SQLException | URISyntaxException e) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  /**
   * Handles when the forgot password button is pressed Sends an alert to ADMINISTRATORs that the
   * user's password needs to be reset
   *
   * @param username the username from the text field that we want to reset the password of
   * @return the new password string
   * @throws SQLException if there is an error connecting to the database
   */
  public static String forgotPasswordPressed(String username)
      throws SQLException, URISyntaxException {
    Sound.playSFX(SFX.BUTTONCLICK);
    //    return DataManager.forgotPassword(username);
    Employee employee = DataManager.getEmployee(username);
    if (employee != null) {
      edu.wpi.teamname.alerts.Alert newAlert =
          new Alert(
              Instant.now().get(ChronoField.MICRO_OF_SECOND),
              new Timestamp(System.currentTimeMillis()),
              (new Timestamp((long) (System.currentTimeMillis() + 6.048e+8))),
              "AUTO",
              "Reset password request for " + username,
              "Reset Password Request",
              EmployeeType.ADMINISTRATOR,
              Alert.Urgency.MILD);
      DataManager.addAlert(newAlert);
      return "Reset Password Request Submitted";
    }
    return "Please Enter a Valid Username";
  }
}
