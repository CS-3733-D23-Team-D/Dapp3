package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.employees.ClearanceLevel;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

public class ParentController {

  @FXML MFXButton homeButton;
  @FXML MFXButton helpButton;
  @FXML MFXButton mapButton;
  // @FXML MFXButton directionsButton;
  @FXML MFXButton makeRequestsButton;
  @FXML MFXButton showRequestsButton;
  @FXML MFXButton editMapButton;
  @FXML MFXButton exitButton;
  @FXML MFXButton logoutButton;
  @FXML MFXButton loginButton;
  @FXML MFXButton editMoveButton;
  @FXML MFXButton showEmployeesButton;
  @FXML Label titleLabel;

  ArrayList<Screen> secureScreens =
      new ArrayList<>(
          Arrays.asList(
              Screen.MAP_EDIT,
              Screen.MOVE_TABLE,
              Screen.SERVICE_REQUEST,
              Screen.SERVICE_REQUEST_VIEW,
              Screen.EMPLOYEE_TABLE));

  @Setter public static StringProperty titleString = new SimpleStringProperty();

  /** * Disables all the buttons that can not be accessed without logging in */
  public void disableButtonsWhenNotLoggedIn() {
    makeRequestsButton.setVisible(false);
    showRequestsButton.setVisible(false);
    editMapButton.setVisible(false);
    editMoveButton.setVisible(false);
    showEmployeesButton.setVisible(false);
  }

  /** logs the current user out of the application */
  private void logout() {
    HomeController.setLoggedIn(new SimpleBooleanProperty(false));
    loginButton.setVisible(true);
    logoutButton.setVisible(false);
    GlobalVariables.logOut();
    disableButtonsWhenNotLoggedIn();

    if (secureScreens.contains(GlobalVariables.getCurrentScreen())) {
      Navigation.navigate(Screen.HOME);
    }
  }

  @FXML
  public void initialize() throws IOException {
    titleLabel.setText(titleString.getValue());
    System.out.println("Parent!");
    if (HomeController.getLoggedIn().getValue()) {
      // disableButtonsWhenNotLoggedIn();
      loginButton.setVisible(false);
      logoutButton.setVisible(true);
    } else {
      loginButton.setVisible(true);
      logoutButton.setVisible(false);
      makeRequestsButton.setVisible(false);
      showRequestsButton.setVisible(false);
      editMoveButton.setVisible(false);
      editMapButton.setVisible(false);
      showEmployeesButton.setVisible(false);
    }

    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.STAFF)) {
      makeRequestsButton.setDisable(false);
      showRequestsButton.setDisable(false);
      editMoveButton.setVisible(true);
      editMapButton.setVisible(false);
      showEmployeesButton.setVisible(false);
    }
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN)) {
      editMapButton.setDisable(false);
      showEmployeesButton.setDisable(false);
      makeRequestsButton.setDisable(false);
      showRequestsButton.setDisable(false);
      editMoveButton.setDisable(false);
    }
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    //    helpButton.setOnMouseClicked(event -> Navigation.navigate(Screen.));
    mapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    //        directionsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    makeRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    showRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    loginButton.setOnMouseClicked(event -> Navigation.navigate(Screen.LOGIN));
    showEmployeesButton.setOnMouseClicked(event -> Navigation.navigate(Screen.EMPLOYEE_TABLE));
    logoutButton.setOnMouseClicked(event -> logout());
    // Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    editMapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDIT));
    editMoveButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_TABLE));
    exitButton.setOnMouseClicked(event -> System.exit(0));

    // titleLabel.setText(titleString.getValue());
  }
}
