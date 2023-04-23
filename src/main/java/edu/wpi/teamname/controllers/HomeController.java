package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.navigation.Move;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

public class HomeController {

  @FXML MFXButton helpButton;
  @FXML MFXButton mapButton;
  @FXML MFXButton directionsButton;
  @FXML MFXButton makeRequestsButton;
  //  @FXML MFXButton makeRequestsButton1;
  //  @FXML MFXButton makeRequestsButton2;
  //  @FXML MFXButton makeRequestsButton3;
  @FXML MFXButton showRequestsButton;
  @FXML MFXButton editMapButton;
  @FXML MFXButton exitButton;
  // @FXML MFXButton navigateButton;
  @FXML MFXButton employeeButton;

  @FXML MFXButton activeRequests;
  @FXML MFXButton upcomingMoves;
  @FXML MFXButton doneRequests;

  // test push
  @Setter @Getter private static ObservableBooleanValue loggedIn = new SimpleBooleanProperty(false);

  //  @FXML ImageView imageView;
  @FXML private AnchorPane rootPane;
  @FXML MFXButton loginButton;
  @FXML MFXButton logoutButton;
  @FXML MFXButton editMoveButton;

  /** logs the current user out of the application */
  private void logout() {
    loggedIn = new SimpleBooleanProperty(false);
    loginButton.setVisible(true);
    logoutButton.setVisible(false);
    GlobalVariables.logOut();
    disableButtonsWhenLoggedOut();
  }

  /** * Disables all the buttons that can not be accessed without logging in */
  private void disableButtonsWhenLoggedOut() {
    makeRequestsButton.setDisable(true);
    //    makeRequestsButton1.setDisable(true);
    //    makeRequestsButton2.setDisable(true);
    //    makeRequestsButton3.setDisable(true);
    showRequestsButton.setDisable(true);
    editMapButton.setDisable(true); // these have to be set to visible false for staff/logged out
    editMoveButton.setDisable(true);
    employeeButton.setDisable(true);
    activeRequests.setDisable(true);
    upcomingMoves.setDisable(true);
    doneRequests.setDisable(true);
  }

  @FXML
  public void initialize() throws SQLException {

    // set the width and height to be bound to the panes width and height
    //    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    //    imageView.fitHeightProperty().bind(rootPane.heightProperty());
    // this allows for the image to stay at the same size of the rootPane, which is the parent pane
    // of the Home.fxml

    // Param is EventHandler
    // Lambda Expression. parameter -> expression
    // Basically just runs the Navigation.navigate Function
    // "event" is a parameter, but there is no
    helpButton.setVisible(false);
    if (loggedIn.getValue()) {
      loginButton.setVisible(false);
      logoutButton.setVisible(true);
      logoutButton.setDisable(false);
      ObservableList<ServiceRequest> requestList =
          FXCollections.observableList(
              DataManager.getAllServiceRequests().stream()
                  .filter(
                      (request) ->
                          request
                              .getStaffName()
                              .equals(GlobalVariables.getCurrentUser().getUsername()))
                  .toList());
      ObservableList<ServiceRequest> processingRequestsList =
          FXCollections.observableList(
              requestList.stream()
                  .filter((request) -> request.getStatus().getStatusString().equals("PROCESSING"))
                  .toList());
      ObservableList<ServiceRequest> doneRequestsList =
          FXCollections.observableList(
              requestList.stream()
                  .filter((request) -> request.getStatus().getStatusString().equals("DONE"))
                  .toList());
      int processingSize = processingRequestsList.size();
      int doneSize = doneRequestsList.size();
      activeRequests.setText(processingSize + " Active Request(s)");
      doneRequests.setText(doneSize + " Done Request(s)");
      ObservableList<Move> allMoves = FXCollections.observableArrayList(DataManager.getAllMoves());
      LocalDate today = LocalDate.now();
      int futureMoves = 0;
      for (Move move : allMoves) {
        if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
            || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
          futureMoves++;
        }
      }
      upcomingMoves.setText(futureMoves + " Upcoming Moves");

    } else {
      activeRequests.setText("Log in to see Active Requests");
      doneRequests.setText("Log in to see Done Request(s)");
      upcomingMoves.setText("Log in to see Upcoming Moves");
      loginButton.setVisible(true);
      logoutButton.setVisible(false);
      logoutButton.setDisable(true);
    }
    loginButton.setOnMouseClicked(event -> Navigation.navigate(Screen.LOGIN));
    logoutButton.setOnMouseClicked(event -> logout());
    //    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));

    //        helpButton.setOnMouseClicked(event -> Navigation.navigate(Screen));
    disableButtonsWhenLoggedOut();
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.STAFF)) {
      makeRequestsButton.setDisable(false);
      editMoveButton.setDisable(false);
      //      makeRequestsButton1.setDisable(false);
      //      makeRequestsButton2.setDisable(false);
      //      makeRequestsButton3.setDisable(false);
      activeRequests.setDisable(false);
      upcomingMoves.setDisable(false);
      doneRequests.setDisable(false);
      showRequestsButton.setDisable(false);
    } else if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN)) {
      editMapButton.setDisable(false);
      editMoveButton.setDisable(false);
      employeeButton.setDisable(false);
      activeRequests.setDisable(false);
      upcomingMoves.setDisable(false);
      doneRequests.setDisable(false);
      activeRequests.setDisable(false);
      upcomingMoves.setDisable(false);
      doneRequests.setDisable(false);
      showRequestsButton.setDisable(false);
      makeRequestsButton.setDisable(false);
      editMoveButton.setDisable(false);
    }

    upcomingMoves.setOnMouseClicked(
        event -> {
          GlobalVariables.setFutureMovesPressed(true);
          Navigation.navigate(Screen.MOVE_TABLE);
        });
    activeRequests.setOnMouseClicked(
        event -> {
          GlobalVariables.setActiveRequestsPressed(true);
          Navigation.navigate(Screen.SERVICE_REQUEST_VIEW);
        });
    doneRequests.setOnMouseClicked(
        event -> {
          GlobalVariables.setDoneRequestsPressed(true);
          Navigation.navigate(Screen.SERVICE_REQUEST_VIEW);
        });

    mapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    // directionsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    makeRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    //    makeRequestsButton1.setOnMouseClicked(event ->
    // Navigation.navigate(Screen.SERVICE_REQUEST));
    //    makeRequestsButton2.setOnMouseClicked(event ->
    // Navigation.navigate(Screen.SERVICE_REQUEST));
    //    makeRequestsButton3.setOnMouseClicked(event ->
    // Navigation.navigate(Screen.SERVICE_REQUEST));
    showRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    editMapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDIT));
    exitButton.setOnMouseClicked(event -> System.exit(0));
    // navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    editMoveButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_TABLE));
    employeeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.EMPLOYEE_TABLE));
  }
}
