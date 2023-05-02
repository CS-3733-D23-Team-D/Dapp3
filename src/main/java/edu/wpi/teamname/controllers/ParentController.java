package edu.wpi.teamname.controllers;

import edu.wpi.teamname.App;
import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.extras.Sound;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class ParentController {

  @FXML MFXButton homeButton;
  @FXML MFXButton helpButton;
  //    @FXML
  MFXButton mapButton = new MFXButton();
  @FXML MFXButton mapButtonSelector;

  // @FXML MFXButton directionsButton;
  //    @FXML
  MFXButton makeRequestsButton = new MFXButton();
  @FXML MFXButton makeRequestsButtonSelector;
  MFXButton showRequestsButton = new MFXButton();
  //  @FXML
  MFXButton showRequestsButton1 = new MFXButton();
  //  @FXML
  MFXButton showRequestsButton2 = new MFXButton();
  //  @FXML
  MFXButton editMapButton = new MFXButton();
  @FXML MFXButton exitButton;
  @FXML MFXButton logoutButton;
  @FXML MFXButton loginButton;
  //  @FXML
  MFXButton editMoveButton = new MFXButton();
  @FXML MFXButton showEmployeesButton; // = new MFXButton();
  //  @FXML
  MFXButton requestRoomButton = new MFXButton();
  //    @FXML
  MFXButton viewSignageButton = new MFXButton();
  @FXML MFXButton viewSignageButtonSelector; // = new MFXButton();
  //  @FXML
  MFXButton editSignageButton = new MFXButton();
  @FXML MFXButton viewAlertsButton; // = new MFXButton();
  @FXML Label titleLabel;

  // Top bar label
  @FXML Label timeLabel;
  @FXML Label dateLabel;
  @FXML Label tempLabel;
  @FXML Label descLabel;
  @FXML Label userLabel;

  @FXML VBox SideBar;
  @FXML HBox MainScreen;

  PopOver mapPop;
  PopOver signagePop;
  PopOver servicePop;

  boolean isOnMapButton, isOnMapPop;
  boolean isOnServiceButton, isOnServicePop;
  boolean isOnSignageButton, isOnSignagePop;

  Pane mp2;
  Pane sp2;
  Pane rp2;

  ArrayList<Screen> secureScreens =
      new ArrayList<>(
          Arrays.asList(
              Screen.MAP_EDIT,
              Screen.MOVE_TABLE,
              Screen.SERVICE_REQUEST,
              Screen.SERVICE_REQUEST_VIEW,
              Screen.EMPLOYEE_TABLE,
              Screen.CONFERENCE_ROOM,
              Screen.ALERT,
              Screen.DATA_MANAGER,
              Screen.SIGNAGE_TABLE,
              Screen.SERVICE_REQUEST_ANALYTICS,
              Screen.CONF_VIEW,
              Screen.SMILE));

  @Setter public static StringProperty titleString = new SimpleStringProperty();
  @Setter public static StringProperty timeString = new SimpleStringProperty();
  @Setter public static StringProperty dateString = new SimpleStringProperty();
  @Setter public static StringProperty tempString = new SimpleStringProperty();
  @Setter public static StringProperty userString = new SimpleStringProperty();
  @Setter public static StringProperty weatherString = new SimpleStringProperty();

  /** * Disables all the buttons that can not be accessed without logging in */
  public void disableButtonsWhenNotLoggedIn() {
    makeRequestsButton.setVisible(false);
    showRequestsButton.setVisible(false);
    editMapButton.setVisible(false);
    editMoveButton.setVisible(false);
    showEmployeesButton.setVisible(false);
    editSignageButton.setVisible(false);
    viewAlertsButton.setVisible(false);
    requestRoomButton.setVisible(false);
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
    } else {
      Sound.playOnButtonClick();
    }
  }

  EventHandler<MouseEvent> showMapButtons =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("Show");
          MFXButton button = (MFXButton) event.getSource();
          //          mapPop = loadButtons("views/MapButtons.fxml");
          //          mapPop.setAutoHide(true);
          mapPop.show(button);
          isOnMapButton = true;
        }
      };

  EventHandler<MouseEvent> showSignageButtons =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          MFXButton button = (MFXButton) event.getSource();
          signagePop.show(button);
          isOnSignageButton = true;
        }
      };

  EventHandler<MouseEvent> showServiceRequestButtons =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          MFXButton button = (MFXButton) event.getSource();
          servicePop.show(button);
          isOnServiceButton = true;
        }
      };

  EventHandler<MouseEvent> hideAllPop =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          System.out.println("HideALl");
          //          if (!isOnMapPop) {
          //            mp2.setVisible(false);
          mapPop.hide();
          //          }
          //          if (!isOnMapPop) {
          signagePop.hide();
          //          }
          //          if (!isOnMapPop) {
          servicePop.hide();
          //          }
        }
      };

  public void hideMapPop() {
    if (!isOnMapPop) {
      mp2.setVisible(false);
      //      mapPop.hide();
    }
  }

  public void hideServicePop() {
    if (!isOnServicePop) {
      rp2.setVisible(false);
      //      servicePop.hide();
    }
  }

  public void hideSignagePop() {
    if (!isOnSignagePop) {
      sp2.setVisible(false);
      //      signagePop.hide();
    }
  }

  //  EventHandler<MouseEvent> hideMap =
  //      new EventHandler<MouseEvent>() {
  //        @Override
  //        public void handle(MouseEvent event) {
  //          System.out.println("HideM");
  //          isOnMapButton = false;
  //          //          if(!isOnMapButton && !isOnMapPop){
  //          //          if (!isOnMapPop) {
  //          //            //            mapPop.hide(Duration.seconds(3));
  //          mapPop.hide();
  //          //          }
  //        }
  //      };

  //  EventHandler<MouseEvent> hideSignage =
  //      new EventHandler<MouseEvent>() {
  //        @Override
  //        public void handle(MouseEvent event) {
  //          System.out.println("HideM");
  //          isOnSignageButton = false;
  //          signagePop.hide();
  //        }
  //      };

  //  EventHandler<MouseEvent> hideService =
  //      new EventHandler<MouseEvent>() {
  //        @Override
  //        public void handle(MouseEvent event) {
  //          System.out.println("HideM");
  //          isOnServiceButton = false;
  //          servicePop.hide();
  //        }
  //      };

  EventHandler<MouseEvent> setAllBoolFalse =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

          isOnSignagePop = false;
          isOnMapPop = false;
          isOnServicePop = false;
        }
      };

  EventHandler<MouseEvent> setAllBoolTrue =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          isOnSignagePop = true;
          isOnMapPop = true;
          isOnServicePop = true;

          System.out.println("SABT");

          if (isOnMapPop || isOnMapButton) {}
        }
      };

  //  PopOver loadButtons(String path) {
  VBox loadButtons(String path) {

    final var resource = App.class.getResource(path);
    final FXMLLoader loader = new FXMLLoader(resource);
    //    System.out.println(filename);
    VBox v = null;
    try {
      v = loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    ClearanceLevel c = GlobalVariables.getCurrentUser().getLevel();
    ArrayList<Integer> remInd = new ArrayList<>();
    for (int i = 0; i < v.getChildren().size(); i++) {
      Node n = v.getChildren().get(i);
      System.out.println("CH: " + i);
      try {
        MFXButton button = (MFXButton) n;
        Screen s = textToScreen(button.getId());

        // (1)
        button.setOnMouseClicked(
            event -> {
              Navigation.navigate(s);
              System.out.println("S");
            });

        if (c.accessableScreens().contains(s)) {
          System.out.println("Good: " + i);
        } else {
          System.out.println("REM: " + i);
          remInd.add(i);
        }
      } catch (Exception e) {
        System.out.println("Bar?");
        continue;
      }
    }

    System.out.println("SS: " + v.getChildren().size());
    for (int i = remInd.size() - 1; i >= 0; i--) {
      System.out.println(remInd.get(i));
      v.getChildren().remove(remInd.get(i).intValue());
    }
    System.out.println("ES: " + v.getChildren().size());

    return v;
    //    PopOver pop = new PopOver(v);
    //    v.setOnMouseEntered(setAllBoolTrue);
    //    v.setOnMouseExited(setAllBoolFalse);
    //    return pop;
  }

  private Screen textToScreen(String s) {
    switch (s) {
        //      case ROOT:
        //        break;
        //      case TEMPLATE:
        //        break;
        //      case HOME:
        //        return this.homeButton;
      case "Make Requests":
        return Screen.SERVICE_REQUEST;
      case "View Map":
        return Screen.MAP;
        //      case TEST:
        //        break;
      case "View Signage":
        return Screen.SIGNAGE;
        //      case EDIT_SIGNAGE:
        //        break;
        //      case LOGIN:
        //        return this.loginButton;
      case "View Requests":
        return Screen.SERVICE_REQUEST_VIEW;
      case "View Confrence Room":
        return Screen.CONF_VIEW;
      case "Edit Map":
        return Screen.MAP_EDIT;
      case "View Moves":
        return Screen.MOVE_TABLE;
        //      case EMPLOYEE_TABLE:
        //        return this.showEmployeesButton;
      case "Edit Signage":
        return Screen.SIGNAGE_TABLE;
      case "Request Room":
        return Screen.CONFERENCE_ROOM;
        //      case ALERT:
        //        return this.viewAlertsButton;
        //      case SMILE:
        //        break;
      case "Service Request Analytics":
        return Screen.SERVICE_REQUEST_ANALYTICS;
        //      case ABOUT:
        //        break;
        //      case CREDITS:
        //        break;
        //      case DATA_MANAGER:
        //        break;
        // Got all Besides Personal Popups
    }
    // Basically return something that can be assigned to without error, but will never show up.
    //    return new MFXButton();
    return null; // Test for errors
  }

  private MFXButton screenToButtonRef(Screen screenIn) {
    switch (screenIn) {
        //      case ROOT:
        //        break;
        //      case TEMPLATE:
        //        break;
      case HOME:
        return this.homeButton;
      case SERVICE_REQUEST:
        return this.makeRequestsButton;
      case MAP:
        return this.mapButton;
        //      case TEST:
        //        break;
      case SIGNAGE:
        return this.viewSignageButton;
        //      case EDIT_SIGNAGE:
        //        break;
      case LOGIN:
        return this.loginButton;
      case SERVICE_REQUEST_VIEW:
        return this.showRequestsButton;
      case CONF_VIEW:
        return this.showRequestsButton2;
      case MAP_EDIT:
        return this.editMapButton;
      case MOVE_TABLE:
        return this.editMoveButton;
      case EMPLOYEE_TABLE:
        return this.showEmployeesButton;
      case SIGNAGE_TABLE:
        return this.editSignageButton;
      case CONFERENCE_ROOM:
        return this.requestRoomButton;
      case ALERT:
        return this.viewAlertsButton;
        //      case SMILE:
        //        break;
      case SERVICE_REQUEST_ANALYTICS:
        return this.showRequestsButton1;
        //      case ABOUT:
        //        break;
        //      case CREDITS:
        //        break;
        //      case DATA_MANAGER:
        //        break;
        // Got all Besides Personal Popups
    }
    // Basically return something that can be assigned to without error, but will never show up.
    //    return new MFXButton();
    return null; // Test for errors
  }

  @FXML
  public void initialize() throws IOException {

    titleLabel.setText(titleString.getValue());
    timeLabel.setText(timeString.getValue());
    dateLabel.setText(dateString.getValue());
    descLabel.setText(weatherString.getValue());
    tempLabel.setText(tempString.getValue());
    userLabel.setText(userString.getValue());

    System.out.println("Parent!");

    if (HomeController.getLoggedIn().getValue()) {
      // disableButtonsWhenNotLoggedIn();
      loginButton.setVisible(false);
      logoutButton.setVisible(true);
    } else {
      loginButton.setVisible(true);
      logoutButton.setVisible(false);
      /*/(2)
      viewSignageButton.setVisible(true);
      makeRequestsButton.setVisible(false);
      showRequestsButton.setVisible(false);
      editMoveButton.setVisible(false);
      editMapButton.setVisible(false);
      showEmployeesButton.setVisible(false);
      viewAlertsButton.setVisible(false);
      editSignageButton.setVisible(false);
      requestRoomButton.setVisible(false);
      //*/

    }

    /*/(2)
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.STAFF)) {
      makeRequestsButton.setDisable(false);
      showRequestsButton.setDisable(false);
      editMoveButton.setVisible(true);
      editMapButton.setVisible(false);
      showEmployeesButton.setVisible(false);
      viewAlertsButton.setVisible(false);
      editSignageButton.setVisible(false);
    }
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN)) {
      editMapButton.setDisable(false);
      showEmployeesButton.setDisable(false);
      makeRequestsButton.setDisable(false);
      showRequestsButton.setDisable(false);
      editMoveButton.setDisable(false);
      viewAlertsButton.setDisable(false);
      editSignageButton.setDisable(false);
    }
    //*/

    /*/ (1)
    homeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.HOME));
    mapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    makeRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    showRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    loginButton.setOnMouseClicked(event -> Navigation.navigate(Screen.LOGIN));
    showEmployeesButton.setOnMouseClicked(event -> Navigation.navigate(Screen.EMPLOYEE_TABLE));
    logoutButton.setOnMouseClicked(event -> logout());
    editMapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDIT));
    editMoveButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_TABLE));
    viewSignageButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    editSignageButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE_TABLE));
    viewAlertsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ALERT));
    requestRoomButton.setOnMouseClicked(event -> Navigation.navigate(Screen.CONFERENCE_ROOM));
    // */

    exitButton.setOnMouseClicked(
        event -> {
          try {
            Connection connection = DataManager.DbConnection();
            connection.close();
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
          System.exit(0);
        });
    // darkToggle.setOnAction(event -> GlobalVariables.setDarkMode(darkToggle.isSelected()));

    // titleLabel.setText(titleString.getValue());

    // (1) //This section is only needed to make sure that home and login work
    for (Screen screen : Screen.values()) {
      // Remove the functionality for the basic sidebar buttons that only are for navigation
      //      if (screen != Screen.MAP || screen != Screen.SERVICE_REQUEST || screen !=
      // Screen.SIGNAGE) {
      MFXButton retButton = screenToButtonRef(screen);
      if (retButton != null) {
        System.out.println(retButton.getText());
        retButton.setOnMouseClicked(
            event -> {
              Navigation.navigate(screen);
              System.out.println("S");
            });
      }
      //      }
    }

    logoutButton.setOnMouseClicked(event -> logout());

    // (2)
    ClearanceLevel c = GlobalVariables.getCurrentUser().getLevel();
    for (Screen screen : Screen.values()) {
      MFXButton retButton = screenToButtonRef(screen);
      if (retButton != null) {
        retButton.setVisible(false);
      }
    }
    for (Screen screen : c.accessableScreens()) {
      MFXButton retButton = screenToButtonRef(screen);
      if (retButton != null) {
        retButton.setVisible(true);
        retButton.setDisable(false);
      }
    }

    //    mapButton.setBorder(
    //        new Border(
    //            new BorderStroke(
    //                Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(1),
    // BorderWidths.DEFAULT)));

    mapButtonSelector
        .hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                System.out.println("Hover: " + oldValue + " -> " + newValue);
                System.out.println("MP:  " + isOnMapPop);
                if (newValue) {
                  mp2.setVisible(newValue);
                  //                  mapPop.show(mapButton);
                  //                  mapPop.setAnchorX(mapPop.getAnchorX() - 20);
                } else {
                  Platform.runLater(() -> hideMapPop());
                }
              }
            });

    viewSignageButtonSelector
        .hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                if (newValue) {
                  sp2.setVisible(newValue);
                  //                  signagePop.show(viewSignageButton);
                } else {
                  Platform.runLater(() -> hideSignagePop());
                }
              }
            });

    makeRequestsButtonSelector
        .hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                if (newValue) {
                  rp2.setVisible(newValue);
                  //                  servicePop.show(makeRequestsButton);
                } else {
                  Platform.runLater(() -> hideServicePop());
                }
              }
            });

    Pane mp = (Pane) mapButtonSelector.getParent();
    mp2 = (Pane) mp.getChildren().get(1);
    mp2.getChildren().add(loadButtons("views/MapButtons.fxml"));
    mp2.setVisible(false);
    mp2.hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                System.out.println("MPC");
                isOnMapPop = newValue;
                mp2.setVisible(isOnMapPop);
              }
            });

    Pane sp = (Pane) viewSignageButtonSelector.getParent();
    sp2 = (Pane) sp.getChildren().get(1);
    sp2.getChildren().add(loadButtons("views/SignageButtons.fxml"));
    sp2.setVisible(false);
    sp2.hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                isOnSignagePop = newValue;
                sp2.setVisible(isOnSignagePop);
              }
            });

    Pane rp = (Pane) makeRequestsButtonSelector.getParent();
    rp2 = (Pane) rp.getChildren().get(1);
    rp2.getChildren().add(loadButtons("views/ServiceButtons.fxml"));
    rp2.setVisible(false);
    rp2.hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                isOnServicePop = newValue;
                rp2.setVisible(isOnServicePop);
              }
            });

    SideBar.viewOrderProperty().set(0);
    MainScreen.viewOrderProperty().set(1);

    //    mapButton.setOnMouseEntered(showMapButtons);
    //    viewSignageButton.setOnMouseEntered(showSignageButtons);
    //    makeRequestsButton.setOnMouseEntered(showServiceRequestButtons);

    //    mapPop = loadButtons("views/MapButtons.fxml");
    //    mapPop.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER)
    //    signagePop = loadButtons("views/SignageButtons.fxml");
    //    servicePop = loadButtons("views/ServiceButtons.fxml");

    //    mapButton.setOnMouseExited(hideMap);
    //    viewSignageButton.setOnMouseExited(hideSignage);
    //    makeRequestsButton.setOnMouseExited(hideService);
  }
}
