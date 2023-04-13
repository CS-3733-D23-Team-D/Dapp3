package edu.wpi.teamname.controllers;

import edu.wpi.teamname.database.Login;
import edu.wpi.teamname.system.Navigation;
import edu.wpi.teamname.system.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.awt.*;
import java.sql.SQLException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class LoginController {
  @FXML MFXButton exit;
  @FXML AnchorPane rootPane;
  @FXML StackPane paneOfStuff;
  //  String tempPassword;
  @FXML Label newPassword;
  @FXML Label success;
  @FXML MFXButton loginButton;
  @FXML MFXButton forgotPassword;
  @FXML MFXTextField loginText;
  @FXML MFXPasswordField passwordText;
  @FXML MFXButton cancel;

  /**
   * handles when the login button is pressed
   * @param username username
   * @param password password
   * @return a boolean if the login was successful or not
   * @throws SQLException if there is an error connecting to the database
   * @throws ExceptionInInitializerError for testing, when we change pages without initializing the screen
   */
  public static boolean loginPressed(String username, String password)
      throws SQLException, ExceptionInInitializerError {
    Login user = new Login(username, password);
    boolean successLog = user.LogInto();
    if (successLog) {
      HomeController.setLoggedIn(true);
      Navigation.navigate(Screen.HOME);
      return true;
    } else {
      return false;
    }
  }

  /**
   * initializes the view for the login page
   */
  @FXML
  public void initialize() {
    newPassword.setVisible(false);
    success.setText("Username or password\nis incorrect");
    success.setVisible(false);
    exit.setOnMouseClicked(event -> System.exit(0));
    forgotPassword.disableProperty().bind(Bindings.isEmpty(loginText.textProperty()));
    loginButton.disableProperty().bind(Bindings.isEmpty(loginText.textProperty()));
    loginButton.disableProperty().bind((Bindings.isEmpty(passwordText.textProperty())));
    forgotPassword.setOnMouseClicked(
        event -> {
          try {
            String tempPassword = forgotPasswordPressed(loginText.getText());
            newPassword.setText("Your new password is: \n" + tempPassword);
            newPassword.setVisible(true);
            paneOfStuff.setDisable(true);
          } catch (SQLException e) {
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
              paneOfStuff.setDisable(true);
              success.setVisible(true);
              passwordText.clear();
            }
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    cancel.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
  }

  /**
   * handles when the forgot password button is pressed
   * @param username the username from the text field that we want to reset the password of
   * @return the new password string
   * @throws SQLException if there is an error connecting to the database
   */
  public static String forgotPasswordPressed(String username) throws SQLException {
    //    return DataManager.forgotPassword(username);
    Login temp = new Login(username, "");
    return temp.resetPass("NewPassword");
  }
}
