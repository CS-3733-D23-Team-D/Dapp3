package edu.wpi.teamname.controllers;

import edu.wpi.teamname.*;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.extras.Weather;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class ParentController {
  //  @FXML ComboBox<Language> languageChooser;
  @FXML CheckBox darkToggle;

  @FXML MFXButton homeButton;
  //  @FXML MFXButton helpButton;
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

  @FXML MFXButton SettingsButton;
  @FXML MFXButton EmergencyButton;

  PopOver mapPop;
  PopOver signagePop;
  PopOver servicePop;

  boolean isOnMapButton, isOnMapPop;
  boolean isOnServiceButton, isOnServicePop;
  boolean isOnSignageButton, isOnSignagePop;

  Pane mp2;
  Pane sp2;
  Pane rp2;

  int buttonSize = 50;

  @Getter @Setter private static ParentController currParentController;

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
  private void logout() throws IOException, URISyntaxException {
    HomeController.setLoggedIn(new SimpleBooleanProperty(false));
    loginButton.setVisible(true);
    logoutButton.setVisible(false);

    GlobalVariables.logOut();
    disableButtonsWhenNotLoggedIn();
    if (GlobalVariables.getCurrentScreen().equals(Screen.SETTINGS)) {
      SettingsController.getCurrController().logout();
    }
    if (GlobalVariables.getCurrentUser().getLevel() == ClearanceLevel.GUEST) {
      makeRequestsButtonSelector.setVisible(false);
    } else {
      makeRequestsButtonSelector.setVisible(true);
    }
    if (secureScreens.contains(GlobalVariables.getCurrentScreen())) {
      Navigation.navigate(Screen.HOME);
    } else {
      Sound.playSFX(SFX.BUTTONCLICK);
    }
  }

  EventHandler<MouseEvent> showMapButtons =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          //          System.out.println("Show");
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
          //          System.out.println("HideALl");
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
      removeAllChildren(mp2);
      Pane mp = (Pane) mapButtonSelector.getParent();
      VBox.setMargin(mp, new Insets(0, 0, 0, 0));
      //      mapPop.hide();
    }
  }

  public void hideServicePop() {
    if (!isOnServicePop) {
      rp2.setVisible(false);
      removeAllChildren(rp2);
      Pane rp = (Pane) makeRequestsButtonSelector.getParent();
      VBox.setMargin(rp, new Insets(0, 0, 0, 0));
      //      servicePop.hide();
    }
  }

  public void hideSignagePop() {
    if (!isOnSignagePop) {
      sp2.setVisible(false);
      removeAllChildren(sp2);
      Pane sp = (Pane) viewSignageButtonSelector.getParent();
      VBox.setMargin(sp, new Insets(0, 0, 0, 0));
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
      //      System.out.println("CH: " + i);
      try {
        MFXButton button = (MFXButton) n;
        Screen s = textToScreen(button.getId());

        // (1)
        button.setOnMouseClicked(
            event -> {
              try {
                Navigation.navigate(s);
              } catch (URISyntaxException e) {
                throw new RuntimeException(e);
              }
              //              System.out.println("S");
            });

        if (c.accessableScreens().contains(s)) {
          //          System.out.println("Good: " + i);
        } else {
          //          System.out.println("REM: " + i);
          remInd.add(i);
        }
      } catch (Exception e) {
        //        System.out.println("Bar?");
        continue;
      }
    }
    switch (path) {
      case "views/MapButtons.fxml":
        switch (GlobalVariables.getB().getValue()) {
          case ENGLISH:
            ((MFXButton) (v.getChildren().get(0))).setText("View Map");
            ((MFXButton) (v.getChildren().get(1))).setText("View Moves");
            ((MFXButton) (v.getChildren().get(2))).setText("Edit Map");
            break;
          case FRENCH:
            ((MFXButton) (v.getChildren().get(0))).setText("Voir la carte");
            ((MFXButton) (v.getChildren().get(1))).setText("Voir les mouvements");
            ((MFXButton) (v.getChildren().get(2))).setText("Modifier la carte");
            break;
          case SPANISH:
            ((MFXButton) (v.getChildren().get(0))).setText("Ver mapa");
            ((MFXButton) (v.getChildren().get(1))).setText("Ver movimientos");
            ((MFXButton) (v.getChildren().get(2))).setText("Editar mapa");
            break;
          case ITALIAN:
            ((MFXButton) (v.getChildren().get(0))).setText("Visualizza mappa");
            ((MFXButton) (v.getChildren().get(1))).setText("Visualizza mosse");
            ((MFXButton) (v.getChildren().get(2))).setText("Modifica mappa");
            break;
        }
        break;
      case "views/SignageButtons.fxml":
        switch (GlobalVariables.getB().getValue()) {
          case ENGLISH:
            ((MFXButton) (v.getChildren().get(0))).setText("View Signage");
            ((MFXButton) (v.getChildren().get(1))).setText("Edit Signage");
            break;
          case FRENCH:
            ((MFXButton) (v.getChildren().get(0))).setText("Voir la signalisation");
            ((MFXButton) (v.getChildren().get(1))).setText("Modifier la signalisation");
            break;
          case SPANISH:
            ((MFXButton) (v.getChildren().get(0)))
                .setText(
                    "Ver se"
                        + GlobalVariables.getNTilda()
                        + "alizaci"
                        + GlobalVariables.getOAcute()
                        + "n");
            ((MFXButton) (v.getChildren().get(1)))
                .setText(
                    "Editar se"
                        + GlobalVariables.getNTilda()
                        + "alizaci"
                        + GlobalVariables.getOAcute()
                        + "n");
            break;
          case ITALIAN:
            ((MFXButton) (v.getChildren().get(0))).setText("Visualizza la segnaletica");
            ((MFXButton) (v.getChildren().get(1))).setText("Modifica la segnaletica");
            break;
        }
        break;
      case "views/ServiceButtons.fxml":
        switch (GlobalVariables.getB().getValue()) {
          case ENGLISH:
            ((MFXButton) (v.getChildren().get(0))).setText("Make Requests");
            ((MFXButton) (v.getChildren().get(1))).setText("Request Room");
            ((MFXButton) (v.getChildren().get(2))).setText("View Requests");
            ((MFXButton) (v.getChildren().get(3))).setText("Service Request Analytics");
            ((MFXButton) (v.getChildren().get(4))).setText("View Conference Room");
            break;
          case FRENCH:
            ((MFXButton) (v.getChildren().get(0))).setText("Faire des demandes");
            ((MFXButton) (v.getChildren().get(1))).setText("Demander une chambre");
            ((MFXButton) (v.getChildren().get(2))).setText("Voir les demandes");
            ((MFXButton) (v.getChildren().get(3))).setText("Analyse des demandes de service");
            ((MFXButton) (v.getChildren().get(4)))
                .setText("Voir la salle de conf" + GlobalVariables.getEAcute() + "rence");
            break;
          case SPANISH:
            ((MFXButton) (v.getChildren().get(0))).setText("Realizar solicitudes");
            ((MFXButton) (v.getChildren().get(1)))
                .setText("Solicitar una habitaci" + GlobalVariables.getOAcute() + "n");
            ((MFXButton) (v.getChildren().get(2))).setText("Ver solicitudes");
            ((MFXButton) (v.getChildren().get(3)))
                .setText("An" + GlobalVariables.getAAcute() + "lisis de solicitudes de servicio");
            ((MFXButton) (v.getChildren().get(4))).setText("Ver sala de conferencias");
            break;
          case ITALIAN:
            ((MFXButton) (v.getChildren().get(0))).setText("Fai richieste");
            ((MFXButton) (v.getChildren().get(1))).setText("Richiedi una stanza");
            ((MFXButton) (v.getChildren().get(2))).setText("Visualizza le richieste");
            ((MFXButton) (v.getChildren().get(3))).setText("Analytics di richieste di servizio");
            ((MFXButton) (v.getChildren().get(4))).setText("Visualizza sala conferenze");
            break;
        }
        break;
    }

    //

    //    System.out.println("SS: " + v.getChildren().size());
    for (int i = remInd.size() - 1; i >= 0; i--) {
      //      System.out.println(remInd.get(i));
      v.getChildren().remove(remInd.get(i).intValue());
    }
    //    System.out.println("ES: " + v.getChildren().size());

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
      case "Settings":
        return Screen.SETTINGS;
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
      case SETTINGS:
        return this.SettingsButton;
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

  /**
   * changes the language of the app
   *
   * @param lang language to change it to
   */
  public void setLanguage(Language lang) {
    switch (lang) {
      case ENGLISH:
        EmergencyButton.setText("Emergency");
        homeButton.setText("Home");
        SettingsButton.setText("Settings");
        mapButton.setText("Map");
        viewSignageButton.setText("View Signage");
        makeRequestsButton.setText("Make Requests");
        requestRoomButton.setText("Request Room");
        showRequestsButton.setText("View Requests");
        editMoveButton.setText("View Moves");
        editSignageButton.setText("Edit Signage");
        editMapButton.setText("Edit Map");
        showEmployeesButton.setText("Show Employees");
        viewAlertsButton.setText("View Alerts");
        loginButton.setText("Login");
        logoutButton.setText("Logout");
        exitButton.setText("Exit");
        mapButtonSelector.setText("Map");
        viewSignageButtonSelector.setText("Signage");
        makeRequestsButtonSelector.setText("Service Requests");
        break;
      case FRENCH:
        EmergencyButton.setText("Urgence");

        homeButton.setText("Page D'accueil");
        mapButton.setText("Carte");
        viewSignageButton.setText("Voir la signalisation");
        makeRequestsButton.setText("Faire des demandes");
        requestRoomButton.setText("Demander une chambre");
        showRequestsButton.setText("Afficher les demandes");
        editMoveButton.setText("Voir les mouvements");
        editSignageButton.setText("Modifier la signalisation");
        editMapButton.setText("Modifier la carte");
        showEmployeesButton.setText("Afficher les employ" + GlobalVariables.getEAcute() + "s");
        viewAlertsButton.setText("Afficher les alertes");
        loginButton.setText("Connexion");
        logoutButton.setText("Se d" + GlobalVariables.getEAcute() + "connecter");
        exitButton.setText("Sortie");
        SettingsButton.setText("Param" + GlobalVariables.getEGrave() + "tres");
        mapButtonSelector.setText("Carte");
        viewSignageButtonSelector.setText("Signalisation");
        makeRequestsButtonSelector.setText("Demandes");
        break;
      case ITALIAN:
        EmergencyButton.setText("Emergenza");
        homeButton.setText("Pagina Iniziale");
        mapButton.setText("Mappa");
        viewSignageButton.setText("Segnaletica");
        makeRequestsButton.setText("Fare Una Richiesta");
        requestRoomButton.setText("Richiedi Una Camera");
        showRequestsButton.setText("Visualizzare Le Richieste");
        editMoveButton.setText("Visualizza Mosse");
        editSignageButton.setText("Modifica Segnaletica");
        editMapButton.setText("Modifica Mappa");
        showEmployeesButton.setText("Mostra Dipendenti");
        viewAlertsButton.setText("Visualizza Avvisi");
        loginButton.setText("Login");
        logoutButton.setText("Disconnettersi");
        exitButton.setText("Uscire");
        mapButtonSelector.setText("Mappa");
        SettingsButton.setText("Impostazioni");
        viewSignageButtonSelector.setText("Segnaletica");
        makeRequestsButtonSelector.setText("Richieste");
        break;
      case SPANISH:
        SettingsButton.setText("Configuraci" + GlobalVariables.getOAcute() + "n");
        EmergencyButton.setText("Emergencia");
        homeButton.setText("P" + GlobalVariables.getAAcute() + "gina de Inicio");
        mapButton.setText("Mapa");
        viewSignageButton.setText(
            "Ver se" + GlobalVariables.getNTilda() + "alizaci" + GlobalVariables.getOAcute() + "n");
        makeRequestsButton.setText("Hacer peticiones");
        requestRoomButton.setText("Solicitar Habitaci" + GlobalVariables.getOAcute() + "n");
        showRequestsButton.setText("Ver solicitudes");
        editMoveButton.setText("Ver movimientos");
        editSignageButton.setText("Editar señalización");
        editMapButton.setText("Editar mapa");
        showEmployeesButton.setText("Mostrar empleados");
        viewAlertsButton.setText("Ver alertas");
        loginButton.setText("Acceso");
        logoutButton.setText("Cerrar sesión");
        exitButton.setText("Salir");
        mapButtonSelector.setText("Mapa");
        viewSignageButtonSelector.setText(
            "Se" + GlobalVariables.getNTilda() + "alizaci" + GlobalVariables.getOAcute() + "n");
        makeRequestsButtonSelector.setText("Peticiones");
        break;
    }
  }

  /**
   * template for when we want to add a new language, so it is easier to copy paste First block is
   * the list of words to be translated, second is the method to set the text for each, where you
   * change the letter to be the translated version
   */
  /*
  Home
  Map
  View Signage
  Make Requests
  Request Room
  View Requests
  View Moves
  Edit Signage
  Edit Map
  Show Employees
  View Alerts
  Login
  Logout
  Exit
     */
  /*
  homeButton.setText("A");
        mapButton.setText("B");
        viewSignageButton.setText("C");
        makeRequestsButton.setText("D");
        requestRoomButton.setText("E");
        showRequestsButton.setText("F");
        editMoveButton.setText("G");
        editSignageButton.setText("H");
        editMapButton.setText("I");
        showEmployeesButton.setText("J");
        viewAlertsButton.setText("K");
        loginButton.setText("L");
        logoutButton.setText("M");
        exitButton.setText("N");
   */

  @FXML
  public void initialize() throws IOException {
    ThemeSwitch.switchTheme(MainScreen);
    currParentController = this;
    titleString.addListener((observable, oldValue, newValue) -> titleLabel.setText(newValue));
    //    languageChooser.setItems(
    //        FXCollections.observableList(Arrays.stream(Language.values()).toList()));
    //    languageChooser.setValue(GlobalVariables.getB().getValue());
    //    languageChooser
    //        .getSelectionModel()
    //        .selectedItemProperty()
    //        .addListener(
    //            (options, oldValue, newValue) -> {
    //              setLanguage(newValue);
    //              GlobalVariables.b.setValue(newValue);
    //            });
    setLanguage(GlobalVariables.getB().getValue());

    Thread t =
        new Thread(
            () -> {
              try {
                tempString.set(Weather.getTemperature());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              try {
                weatherString.set(Weather.getDescription());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              while (true) {
                timeString.set(Weather.getTime());
                dateString.set(Weather.getDate());
                userString.set(GlobalVariables.getCurrentUser().getUsername());

                Platform.runLater(
                    () -> {
                      timeLabel.setText(timeString.getValue());
                      dateLabel.setText(dateString.getValue());
                      descLabel.setText(weatherString.getValue());
                      tempLabel.setText(tempString.getValue());
                      userLabel.setText(userString.getValue());
                    });
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }
            });
    // System.out.println(Thread.activeCount());
    //    if (Thread.activeCount() == 1) {
    //      System.out.println("Thread tart");
    t.start();
    //    } else {
    //      System.out.println("Thread already working");
    //    }

    System.out.println("Parent!: " + HomeController.getLoggedIn().getValue());

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

    titleLabel.setText(titleString.getValue());

    // (1) //This section is only needed to make sure that home and login work
    for (Screen screen : Screen.values()) {
      // Remove the functionality for the basic sidebar buttons that only are for navigation
      //      if (screen != Screen.MAP || screen != Screen.SERVICE_REQUEST || screen !=
      // Screen.SIGNAGE) {
      MFXButton retButton = screenToButtonRef(screen);
      if (retButton != null) {
        //        System.out.println(retButton.getText());
        retButton.setOnMouseClicked(
            event -> {
              try {
                Navigation.navigate(screen);
              } catch (URISyntaxException e) {
                throw new RuntimeException(e);
              }
              //              System.out.println("S");
            });
      }
      //      }
    }
    logoutButton.setOnMouseClicked(
        event -> {
          try {
            logout();
          } catch (IOException e) {
            throw new RuntimeException(e);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });

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

    if (HomeController.getLoggedIn().getValue()) {
      // disableButtonsWhenNotLoggedIn();
      //      System.out.println("VIS");
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

                isOnMapPop = newValue;
                if (newValue) {
                  Pane mp = (Pane) mapButtonSelector.getParent();
                  mp2 = (Pane) mp.getChildren().get(1);
                  mp2.getChildren().add(loadButtons("views/MapButtons.fxml"));
                  resize(mp, mp2);
                  //                  Platform.runLater(() -> resize(mp, mp2));
                } else {
                  removeAllChildren(mp2);
                  Platform.runLater(() -> hideMapPop());
                }
                mp2.setVisible(isOnMapPop);
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

                isOnSignagePop = newValue;
                if (newValue) {
                  Pane sp = (Pane) viewSignageButtonSelector.getParent();
                  sp2 = (Pane) sp.getChildren().get(1);
                  sp2.getChildren().add(loadButtons("views/SignageButtons.fxml"));
                  resize(sp, sp2);
                  //                  Platform.runLater(() -> resize(sp, sp2));
                } else {
                  removeAllChildren(sp2);
                  Platform.runLater(() -> hideSignagePop());
                }
                sp2.setVisible(isOnSignagePop);
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

                isOnServicePop = newValue;
                Pane rp = (Pane) makeRequestsButtonSelector.getParent();
                if (newValue) {
                  rp2 = (Pane) rp.getChildren().get(1);
                  rp2.getChildren().add(loadButtons("views/ServiceButtons.fxml"));
                  if (((Pane) rp2.getChildren().get(0)).getChildren().size() == 0) {
                    makeRequestsButtonSelector.setVisible(false);
                  } else {
                    makeRequestsButtonSelector.setVisible(true);
                  }
                  resize(rp, rp2);
                  //                  Platform.runLater(() -> resize(rp, rp2));
                } else {
                  removeAllChildren(rp2);
                  Platform.runLater(() -> hideServicePop());
                }
                rp2.setVisible(isOnServicePop);
              }
            });

    Pane mp = (Pane) mapButtonSelector.getParent();
    mp2 = (Pane) mp.getChildren().get(1);
    //    mp2.getChildren().add(loadButtons("views/MapButtons.fxml"));
    mp2.setVisible(false);
    mp2.hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                //                System.out.println("MPC");

                isOnMapPop = newValue;
                if (newValue) {
                  mp2 = (Pane) mp.getChildren().get(1);
                  mp2.getChildren().add(loadButtons("views/MapButtons.fxml"));
                  resize(mp, mp2);
                  //                  Platform.runLater(() -> resize(mp, mp2));
                } else {
                  removeAllChildren(mp2);
                  VBox.setMargin(mp, new Insets(0, 0, 0, 0));
                }
                mp2.setVisible(isOnMapPop);
              }
            });

    Pane sp = (Pane) viewSignageButtonSelector.getParent();
    sp2 = (Pane) sp.getChildren().get(1);
    //    sp2.getChildren().add(loadButtons("views/SignageButtons.fxml"));
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
                if (newValue) {
                  sp2 = (Pane) sp.getChildren().get(1);
                  sp2.getChildren().add(loadButtons("views/SignageButtons.fxml"));
                  resize(sp, sp2);
                  //                  Platform.runLater(() -> resize(sp, sp2));
                } else {
                  removeAllChildren(sp2);
                  //                  VBox.setMargin(sp, new Insets(0,0,0,0));
                  VBox.setMargin(sp, new Insets(0, 0, 0, 0));
                }
                sp2.setVisible(isOnSignagePop);
              }
            });

    if (GlobalVariables.getCurrentUser().getLevel() == ClearanceLevel.GUEST) {
      makeRequestsButtonSelector.setVisible(false);
    } else {
      makeRequestsButtonSelector.setVisible(true);
    }
    Pane rp = (Pane) makeRequestsButtonSelector.getParent();
    rp2 = (Pane) rp.getChildren().get(1);
    //    rp2.getChildren().add(loadButtons("views/ServiceButtons.fxml"));
    rp2.setVisible(false);
    rp2.hoverProperty()
        .addListener(
            new ChangeListener<Boolean>() {
              @Override
              public void changed(
                  ObservableValue<? extends Boolean> observable,
                  Boolean oldValue,
                  Boolean newValue) {
                //                isOnServicePop = newValue;
                //                rp2.setVisible(isOnServicePop);

                isOnServicePop = newValue;
                if (newValue) {
                  rp2 = (Pane) rp.getChildren().get(1);
                  rp2.getChildren().add(loadButtons("views/ServiceButtons.fxml"));
                  if (((Pane) rp2.getChildren().get(0)).getChildren().size() == 0) {
                    makeRequestsButtonSelector.setVisible(false);
                  } else {
                    makeRequestsButtonSelector.setVisible(true);
                  }
                  resize(rp, rp2);
                  //                  Platform.runLater(() -> resize(rp, rp2));
                } else {
                  removeAllChildren(rp2);
                  VBox.setMargin(rp, new Insets(0, 0, 0, 0));
                }
                rp2.setVisible(isOnServicePop);
              }
            });

    //    if (((Pane) rp2.getChildren().get(0)).getChildren().size() == 0) {
    //      makeRequestsButtonSelector.setVisible(false);
    //    } else {
    //      makeRequestsButtonSelector.setVisible(true);
    //    }

    //    EmergencyButton.setOnMouseClicked();
    EmergencyButton.setOnMouseClicked(
        event -> {
          GlobalVariables.setPathToExit(true);
          try {
            Navigation.navigate(Screen.MAP);
          } catch (URISyntaxException e) {
            throw new RuntimeException(e);
          }
        });

    //    makeRequestsButtonSelector
    //    viewSignageButtonSelector
    //    mapButtonSelector.setMaxHeight(buttonSize);
    //    viewSignageButtonSelector.setMaxHeight(buttonSize);
    //    makeRequestsButtonSelector.setMaxHeight(buttonSize);

    SideBar.viewOrderProperty().set(0);
    MainScreen.viewOrderProperty().set(1);

    //    mapButton.setOnMouseEntered(showMapButtons);
    //    viewSignageButton.setOnMouseEntered(showSignageButtons);
    //    makeRequestsButton.setOnMouseEntered(showServiceRequestButtons);

    //    mapPop = loadButtons("views/MapButtons.fxml");
    //    signagePop = loadButtons("views/SignageButtons.fxml");
    //    servicePop = loadButtons("views/ServiceButtons.fxml");

    //    mapButton.setOnMouseExited(hideMap);
    //    viewSignageButton.setOnMouseExited(hideSignage);
    //    makeRequestsButton.setOnMouseExited(hideService);
  }

  private void resize(Pane one, Pane two) {
    ReadOnlyDoubleProperty height = two.heightProperty();
    double realHeight = height.get();
    realHeight = ((Pane) (two.getChildren().get(0))).getChildren().size() * buttonSize;
    System.out.println("RH: " + realHeight);
    int extra = 0;
    VBox.setMargin(one, new Insets(0, 0, -realHeight + buttonSize - extra, 0));
    //    VBox.setMargin(one, new Insets(0, 0, -180, 0));
  }

  private void removeAllChildren(Pane pane) {
    for (int i = pane.getChildren().size() - 1; i >= 0; i--) {
      pane.getChildren().remove(i);
    }
  }
}
