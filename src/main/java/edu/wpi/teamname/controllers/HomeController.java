package edu.wpi.teamname.controllers;

import edu.wpi.teamname.*;
import edu.wpi.teamname.alerts.Alert;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.employees.EmployeeType;
import edu.wpi.teamname.extras.Joke;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.navigation.Move;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.PopOver;

public class HomeController {
  @FXML Label actionItemsLabel;
  @FXML Label staffItemsLabel;
  @FXML Label navigationLabel;
  @FXML ComboBox<Language> languageChooser;
  @FXML MFXButton notificationPopupButtonSimple;
  @FXML MFXNotificationCenter notifsButton;
  @FXML MFXButton helpButton;
  @FXML MFXButton mapButton;
  @FXML VBox actionVBox;
  @FXML VBox SRVBox;
  @FXML VBox mapVBox;
  @FXML GridPane homeGrid;
  @FXML MFXButton directionsButton;
  @FXML MFXButton makeRequestsButton;
  //  @FXML MFXButton makeRequestsButton1;
  //  @FXML MFXButton makeRequestsButton2;
  //  @FXML MFXButton makeRequestsButton3;
  @FXML MFXButton showRequestsButton;
  @FXML MFXButton editMapButton;
  @FXML MFXButton exitButton;
  @FXML MFXButton employeeButton;
  @FXML MFXButton aboutButton;
  @FXML MFXButton creditButton;

  @FXML MFXButton viewSignageButton;
  @FXML MFXButton editSignageButton;
  @FXML MFXButton requestRoomButton;
  @FXML MFXButton viewConfrenceRoomButton;
  @FXML MFXButton viewAlertsButton; // TAKE OUT LATER

  @FXML MFXButton activeRequests;
  @FXML MFXButton upcomingMoves;
  @FXML MFXButton doneRequests;
  @FXML MFXButton settingsButton;
  @FXML MFXButton serviceRequestAnalyticsButton;

  // test push
  @Setter @Getter private static ObservableBooleanValue loggedIn = new SimpleBooleanProperty(false);

  //  @FXML ImageView imageView;
  @FXML private AnchorPane rootPane;
  @FXML MFXButton loginButton;
  @FXML MFXButton logoutButton;
  @FXML MFXButton editMoveButton;
  @FXML Label jokesLabel;
  @FXML Label jokeIDLabel;

  /** logs the current user out of the application */
  private void logout() {
    Sound.playSFX(SFX.BUTTONCLICK);
    loggedIn = new SimpleBooleanProperty(false);
    loginButton.setVisible(true);
    logoutButton.setVisible(false);
    GlobalVariables.logOut();
    disableButtonsWhenLoggedOut();
  }

  /** Disables all the buttons that can not be accessed without logging in */
  private void disableButtonsWhenLoggedOut() {
    homeGrid.setConstraints(mapVBox, 1, 1);
    actionVBox.setVisible(false);
    SRVBox.setVisible(false);
    makeRequestsButton.setVisible(false);
    showRequestsButton.setVisible(false);
    editMapButton.setVisible(false); // these have to be set to visible false for staff/logged out
    editMoveButton.setVisible(false);
    employeeButton.setVisible(false);
    activeRequests.setVisible(false);
    upcomingMoves.setVisible(false);
    doneRequests.setVisible(false);
    editSignageButton.setVisible(false);
    settingsButton.setVisible(true);
    showRequestsButton.setManaged(false);
    editMapButton.setManaged(false);
    editMoveButton.setManaged(false);
    employeeButton.setManaged(false);
    activeRequests.setManaged(false);
    upcomingMoves.setManaged(false);
    doneRequests.setManaged(false);
    makeRequestsButton.setManaged(false);
    actionVBox.setManaged(false);
    SRVBox.setManaged(false);
    editSignageButton.setManaged(false);
    settingsButton.setManaged(true);
  }

  public void setLanguage(Language lang) throws SQLException {
    switch (lang) {
      case ENGLISH:
        actionItemsLabel.setText("Action Items");
        staffItemsLabel.setText("Staff Items");
        navigationLabel.setText("Navigation");
        if (loggedIn.getValue()) {
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
                      .filter(
                          (request) -> request.getStatus().getStatusString().equals("PROCESSING"))
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
          ObservableList<Move> allMoves =
              FXCollections.observableArrayList(DataManager.getAllMoves());
          LocalDate today = LocalDate.now();
          int futureMoves = 0;
          for (Move move : allMoves) {
            if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
                || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
              futureMoves++;
            }
          }
          upcomingMoves.setText(futureMoves + " Upcoming Moves");
        }
        makeRequestsButton.setText("Make a Request");
        showRequestsButton.setText("View Service Requests");
        serviceRequestAnalyticsButton.setText("View Service Request Analytics");
        employeeButton.setText("View Employees");
        requestRoomButton.setText("Request Conference Room");
        viewConfrenceRoomButton.setText("View Conference Room");
        viewAlertsButton.setText("View Alerts");
        mapButton.setText("View Map");
        editMoveButton.setText("View Moves");
        editMapButton.setText("Edit Map");
        viewSignageButton.setText("View Signage");
        editSignageButton.setText("Edit Signage");
        exitButton.setText("Exit");
        settingsButton.setText("Settings");
        notificationPopupButtonSimple.setText("Notifications");
        creditButton.setText("Credits");
        aboutButton.setText("About");
        helpButton.setText("Help");
        loginButton.setText("Login");
        logoutButton.setText("Logout");
        break;
      case ITALIAN:
        actionItemsLabel.setText("Elementi di azione");
        staffItemsLabel.setText("Elementi del personale");
        navigationLabel.setText("Navigazione");
        if (loggedIn.getValue()) {
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
                      .filter(
                          (request) -> request.getStatus().getStatusString().equals("PROCESSING"))
                      .toList());
          ObservableList<ServiceRequest> doneRequestsList =
              FXCollections.observableList(
                  requestList.stream()
                      .filter((request) -> request.getStatus().getStatusString().equals("DONE"))
                      .toList());
          int processingSize = processingRequestsList.size();
          int doneSize = doneRequestsList.size();
          activeRequests.setText(processingSize + " Richieste attive");
          doneRequests.setText(doneSize + " Richieste completate");
          ObservableList<Move> allMoves =
              FXCollections.observableArrayList(DataManager.getAllMoves());
          LocalDate today = LocalDate.now();
          int futureMoves = 0;
          for (Move move : allMoves) {
            if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
                || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
              futureMoves++;
            }
          }
          upcomingMoves.setText(futureMoves + " Spostamenti futuri");
        }
        makeRequestsButton.setText("Fai una richiesta");
        showRequestsButton.setText("Visualizza le richieste di servizio");
        serviceRequestAnalyticsButton.setText("Visualizza le analisi delle richieste di servizio");
        employeeButton.setText("Visualizza i dipendenti");
        requestRoomButton.setText("Richiedi una sala conferenze");
        viewConfrenceRoomButton.setText("Visualizza le sale conferenze");
        viewAlertsButton.setText("Visualizza le notifiche");
        mapButton.setText("Visualizza la mappa");
        editMoveButton.setText("Visualizza gli spostamenti");
        editMapButton.setText("Modifica la mappa");
        viewSignageButton.setText("Visualizza la segnaletica");
        editSignageButton.setText("Modifica la segnaletica");
        exitButton.setText("Uscire");
        settingsButton.setText("Impostazioni");
        notificationPopupButtonSimple.setText("Notifiche");
        creditButton.setText("Crediti");
        aboutButton.setText("Informazioni");
        helpButton.setText("Aiuto");
        loginButton.setText("Login");
        logoutButton.setText("Disconnettersi");
        break;
      case FRENCH:
        actionItemsLabel.setText("Tâches à effectuer");
        staffItemsLabel.setText("Éléments du personnel");
        navigationLabel.setText("Navigation");
        if (loggedIn.getValue()) {
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
                      .filter(
                          (request) -> request.getStatus().getStatusString().equals("PROCESSING"))
                      .toList());
          ObservableList<ServiceRequest> doneRequestsList =
              FXCollections.observableList(
                  requestList.stream()
                      .filter((request) -> request.getStatus().getStatusString().equals("DONE"))
                      .toList());
          int processingSize = processingRequestsList.size();
          int doneSize = doneRequestsList.size();
          activeRequests.setText(processingSize + " demande(s) active(s)");
          doneRequests.setText(doneSize + " demande(s) effectuée(s)");
          ObservableList<Move> allMoves =
              FXCollections.observableArrayList(DataManager.getAllMoves());
          LocalDate today = LocalDate.now();
          int futureMoves = 0;
          for (Move move : allMoves) {
            if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
                || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
              futureMoves++;
            }
          }
          upcomingMoves.setText(futureMoves + " Déplacements à venir");
        }
        makeRequestsButton.setText("Faire une demande");
        showRequestsButton.setText("Voir les demandes de service");
        serviceRequestAnalyticsButton.setText("Voir l'analyse des demandes de service");
        employeeButton.setText("Voir les employés");
        requestRoomButton.setText("Demander une salle de conférence");
        viewConfrenceRoomButton.setText("Voir les salles de conférence");
        viewAlertsButton.setText("Voir les alertes");
        mapButton.setText("Voir la carte");
        editMoveButton.setText("Voir les déplacements");
        editMapButton.setText("Modifier la carte");
        viewSignageButton.setText("Voir la signalisation");
        editSignageButton.setText("Modifier la signalisation");
        exitButton.setText("Sortir");
        settingsButton.setText("Paramètres");
        notificationPopupButtonSimple.setText("Notifications");
        creditButton.setText("Crédits");
        aboutButton.setText("À propos");
        helpButton.setText("Aide");
        loginButton.setText("Se connecter");
        logoutButton.setText("Se déconnecter");
        break;
      case SPANISH:
        actionItemsLabel.setText("Elementos de acción");
        staffItemsLabel.setText("Elementos del personal");
        navigationLabel.setText("Navegación");
        if (loggedIn.getValue()) {
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
                      .filter(
                          (request) -> request.getStatus().getStatusString().equals("PROCESSING"))
                      .toList());
          ObservableList<ServiceRequest> doneRequestsList =
              FXCollections.observableList(
                  requestList.stream()
                      .filter((request) -> request.getStatus().getStatusString().equals("DONE"))
                      .toList());
          int processingSize = processingRequestsList.size();
          int doneSize = doneRequestsList.size();
          activeRequests.setText(processingSize + " solicitud(es) activa(s)");
          doneRequests.setText(doneSize + " solicitud(es) completada(s)");
          ObservableList<Move> allMoves =
              FXCollections.observableArrayList(DataManager.getAllMoves());
          LocalDate today = LocalDate.now();
          int futureMoves = 0;
          for (Move move : allMoves) {
            if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
                || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
              futureMoves++;
            }
          }
          upcomingMoves.setText(futureMoves + " Movimientos futuros");
        }
        makeRequestsButton.setText("Hacer una solicitud");
        showRequestsButton.setText("Ver solicitudes de servicio");
        serviceRequestAnalyticsButton.setText("Ver análisis de solicitudes de servicio");
        employeeButton.setText("Ver empleados");
        requestRoomButton.setText("Solicitar una sala de conferencias");
        viewConfrenceRoomButton.setText("Ver salas de conferencias");
        viewAlertsButton.setText("Ver alertas");
        mapButton.setText("Ver mapa");
        editMoveButton.setText("Ver movimientos");
        editMapButton.setText("Editar mapa");
        viewSignageButton.setText("Ver señalización");
        editSignageButton.setText("Editar señalización");
        exitButton.setText("Salir");
        settingsButton.setText("Configuración");
        notificationPopupButtonSimple.setText("Notificaciones");
        creditButton.setText("Créditos");
        aboutButton.setText("Acerca de");
        helpButton.setText("Ayuda");
        loginButton.setText("Iniciar sesión");
        logoutButton.setText("Cerrar sesión");
        break;
    }
  }

  private ImageView getSizedGraphic(String url) {
    ImageView imageView = new ImageView(url);
    imageView.setFitHeight(48);
    imageView.setFitWidth(48);
    return imageView;
  }

  @FXML
  public void initialize() throws SQLException, IOException {
    ThemeSwitch.switchTheme(rootPane);

    if (!GlobalVariables.getDarkMode().get()) {
      makeRequestsButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/assignment.png"));
      showRequestsButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/edit_note.png"));
      serviceRequestAnalyticsButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/add_chart.png"));

      requestRoomButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/meeting_room.png"));
      viewConfrenceRoomButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/room_preferences.png"));
      viewAlertsButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/circle_notifications.png"));
      employeeButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/badge.png"));

      mapButton.setGraphic(getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/map.png"));
      editMapButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/edit_location_alt.png"));
      editMoveButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/edit_location.png"));

      viewSignageButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/directions.png"));
      editSignageButton.setGraphic(
          getSizedGraphic("edu/wpi/teamname/images/MenuIcons/light/fork_left.png"));
    }

    languageChooser.setItems(
        FXCollections.observableList(Arrays.stream(Language.values()).toList()));
    languageChooser.setValue(GlobalVariables.getB().getValue());
    languageChooser
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (options, oldValue, newValue) -> {
              try {
                setLanguage(newValue);
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
              GlobalVariables.b.setValue(newValue);
            });
    setLanguage(GlobalVariables.getB().getValue());

    EventHandler<MouseEvent> NotificationPopupEvent =
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            Sound.playSFX(SFX.BUTTONCLICK);
            ObservableList<Alert> alertList = null;

            MFXButton createNewButton = ((MFXButton) event.getSource());
            HBox outerPane = (HBox) createNewButton.getParent();

            final var resource = App.class.getResource("views/NotificationPane.fxml");
            final FXMLLoader loader = new FXMLLoader(resource);
            ScrollPane p;
            try {
              p = loader.load();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            VBox v = (VBox) p.getContent();
            v.getChildren().clear();
            v.getChildren().removeAll();
            if (GlobalVariables.getCurrentUser() != null
                && !(GlobalVariables.getCurrentUser().getType().toString().equals("NONE"))) {

              try {

                alertList = FXCollections.observableList(DataManager.getAllAlerts());
                //                alertList.stream().sorted().collect(Collectors.toList());
                alertList =
                    FXCollections.observableList(
                        alertList.stream()
                            .filter(
                                (alert) ->
                                    alert
                                            .getType()
                                            .toString()
                                            .equals(
                                                GlobalVariables.getCurrentUser()
                                                    .getType()
                                                    .toString())
                                        || alert
                                            .getType()
                                            .toString()
                                            .equals(EmployeeType.ALL.toString()))
                            .toList());
                alertList =
                    FXCollections.observableList(
                        alertList.stream()
                            .filter(
                                (alert) ->
                                    alert.getStartDisplayDate().toInstant().isBefore(Instant.now()))
                            .toList());
                alertList =
                    FXCollections.observableList(
                        alertList.stream()
                            .filter(
                                (alert) ->
                                    alert.getEndDisplayDate().toInstant().isAfter(Instant.now()))
                            .toList());
                for (int i = 0; i < alertList.size(); i++) {
                  Alert tempAlert = alertList.get(i);
                  final var innerNotif = App.class.getResource("views/Notification.fxml");
                  final FXMLLoader loader1 = new FXMLLoader(innerNotif);
                  HBox in;
                  //                  new HBox();
                  try {
                    in = loader1.load();
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                  Label idLabel = (Label) ((Pane) in.getChildren().get(0)).getChildren().get(0);
                  idLabel.setText("Alert ID: " + tempAlert.getId());
                  Label announcement =
                      (Label) ((Pane) in.getChildren().get(0)).getChildren().get(1);
                  announcement.setText(tempAlert.getAnnouncement());
                  Label description = (Label) ((Pane) in.getChildren().get(0)).getChildren().get(2);
                  description.setText(tempAlert.getDescription());
                  Label urgency = (Label) ((Pane) (in.getChildren().get(1))).getChildren().get(0);
                  urgency.setText(tempAlert.getUrgency().getString());
                  switch (tempAlert.getUrgency()) {
                    case MILD:
                      in.getStyleClass().add("mild");
                      break;
                    case NONE:
                      in.getStyleClass().add("none");
                      break;
                    case MEDIUM:
                      in.getStyleClass().add("medium");
                      break;
                    case SEVERE:
                      in.getStyleClass().add("severe");
                      break;
                  }
                  v.getChildren().add(in);

                  //
                  //                  temp.getStylesheets()
                  //                      .add(
                  //                          App.class
                  //                              .getResource("stylesheets/Colors/lightTheme.css")
                  //                              .toExternalForm());
                  //                  temp.getStyleClass().add("primary-text-container");
                  //                  temp.getStyleClass().add("primary");
                  //                  Label description = new Label();
                  //                  description.setText(alertList.get(i).getDescription());
                  //                  description.getStyleClass().add("primary-text-container");
                  //                  description.getStyleClass().add("primary");
                  //                  Label announcement = new Label();
                  //                  announcement.setText(alertList.get(i).getAnnouncement());
                  //                  announcement.getStyleClass().add("primary-text-container");
                  //                  announcement.getStyleClass().add("primary");
                  //                  announcement.wrapTextProperty().set(true);
                  //                  description.wrapTextProperty().set(true);
                  //                  HBox.setHgrow(description, Priority.SOMETIMES);
                  //                  HBox.setHgrow(announcement, Priority.ALWAYS);
                  //                  temp.setSpacing(20);
                  //                  temp.getChildren().add(announcement);
                  //                  temp.getChildren().add(description);
                  //                  VBox.setVgrow(temp, Priority.ALWAYS);

                }
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }
            }

            PopOver pop = new PopOver(v);
            pop.show(createNewButton);
          }
        };

    //    notifsButton.getNotifications().clear();

    // set the width and height to be bound to the panes width and height
    //    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    //    imageView.fitHeightProperty().bind(rootPane.heightProperty());
    // this allows for the image to stay at the same size of the rootPane, which is the parent pane
    // of the Home.fxml

    // Param is EventHandler
    // Lambda Expression. parameter -> expression
    // Basically just runs the Navigation.navigate Function
    // "event" is a parameter, but there is no
    Joke joke = Joke.getJoke(0);
    jokeIDLabel.setText("Joke #" + Integer.toString(joke.getId()));
    jokesLabel.setText(joke.toString());

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

    /** * Disables all the buttons that can not be accessed as Staff */
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.STAFF)) {
      homeGrid.setConstraints(mapVBox, 1, 1);
      homeGrid.setConstraints(actionVBox, 0, 1);
      homeGrid.setConstraints(SRVBox, 2, 1);
      actionVBox.setVisible(true);
      SRVBox.setVisible(true);
      makeRequestsButton.setVisible(true);
      makeRequestsButton.setManaged(true);
      editMoveButton.setVisible(true);
      editMoveButton.setManaged(true);
      employeeButton.setVisible(false);
      employeeButton.setManaged(false);
      editMapButton.setVisible(false);
      editMapButton.setManaged(false);
      activeRequests.setVisible(true);
      activeRequests.setManaged(true);
      upcomingMoves.setVisible(true);
      upcomingMoves.setManaged(true);
      doneRequests.setVisible(true);
      doneRequests.setManaged(true);
      viewAlertsButton.setVisible(false);
      viewAlertsButton.setManaged(false);
      showRequestsButton.setVisible(true);
      showRequestsButton.setManaged(true);
      actionVBox.setManaged(true);
      SRVBox.setManaged(true);
      editSignageButton.setVisible(false);
      editSignageButton.setManaged(false);

      /** * Enables all buttons for the Admin login */
    } else if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN)) {
      homeGrid.setConstraints(mapVBox, 1, 1);
      homeGrid.setConstraints(actionVBox, 0, 1);
      homeGrid.setConstraints(SRVBox, 2, 1);
      actionVBox.setVisible(true);
      SRVBox.setVisible(true);
      editMapButton.setVisible(true);
      editMapButton.setDisable(false);
      editMapButton.setManaged(true);
      editMoveButton.setVisible(true);
      editMoveButton.setDisable(false);
      editMoveButton.setManaged(true);
      employeeButton.setVisible(true);
      employeeButton.setDisable(false);
      employeeButton.setManaged(true);
      activeRequests.setVisible(true);
      activeRequests.setDisable(false);
      activeRequests.setManaged(true);
      upcomingMoves.setVisible(true);
      upcomingMoves.setDisable(false);
      upcomingMoves.setManaged(true);
      doneRequests.setVisible(true);
      doneRequests.setDisable(false);
      doneRequests.setManaged(true);
      activeRequests.setVisible(true);
      activeRequests.setDisable(false);
      activeRequests.setManaged(true);
      doneRequests.setVisible(true);
      doneRequests.setDisable(false);
      doneRequests.setManaged(true);
      showRequestsButton.setVisible(true);
      showRequestsButton.setDisable(false);
      showRequestsButton.setManaged(true);
      makeRequestsButton.setVisible(true);
      makeRequestsButton.setDisable(false);
      makeRequestsButton.setManaged(true);
      editMoveButton.setVisible(true);
      editMoveButton.setDisable(false);
      editMoveButton.setManaged(true);
      actionVBox.setManaged(true);
      SRVBox.setManaged(true);
      editSignageButton.setVisible(true);
      editSignageButton.setManaged(true);
      settingsButton.setVisible(true);
      settingsButton.setManaged(true);
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
    makeRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
    showRequestsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST_VIEW));
    editMapButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP_EDIT));
    exitButton.setOnMouseClicked(
        event -> {
          Sound.playSFX(SFX.BUTTONCLICK);
          try {
            Connection connection = DataManager.DbConnection();
            connection.close();
          } catch (SQLException e) {
            System.out.println(e.getMessage());
          }
          System.exit(0);
        });
    editMoveButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MOVE_TABLE));
    employeeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.EMPLOYEE_TABLE));
    editSignageButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE_TABLE));
    viewSignageButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SIGNAGE));
    viewAlertsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ALERT));
    requestRoomButton.setOnMouseClicked(event -> Navigation.navigate(Screen.CONFERENCE_ROOM));
    notificationPopupButtonSimple.setOnMouseClicked(NotificationPopupEvent);
    aboutButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ABOUT));
    settingsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SETTINGS));
    creditButton.setOnMouseClicked(event -> Navigation.navigate(Screen.CREDITS));
    serviceRequestAnalyticsButton.setOnMouseClicked(
        event -> Navigation.navigate(Screen.SERVICE_REQUEST_ANALYTICS));
    viewConfrenceRoomButton.setOnMouseClicked(event -> Navigation.navigate(Screen.CONF_VIEW));
    //    notifsButton.setOnMouseClicked(event -> Navigation.navigate(Screen.ALERT));
  }
}
