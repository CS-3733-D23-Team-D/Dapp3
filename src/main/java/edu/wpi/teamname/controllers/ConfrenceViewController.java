package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.ThemeSwitch;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.servicerequest.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.SearchableComboBox;

/** Controller for the UI to view the conference room reservations */
public class ConfrenceViewController {

  @FXML Label filterTableLabel;
  @FXML Label dateLabel;

  @FXML Label assignedLabel;
  @FXML Label assignStaffLabel;
  @FXML Label idLabel;
  @FXML Label assignStaffLabel1;
  @FXML TableView<ConfReservation> table;
  @FXML TableColumn resIDCol;
  @FXML TableColumn dateCol;
  @FXML TableColumn startCol;
  @FXML TableColumn endCol;
  @FXML TableColumn nameCol;
  @FXML TableColumn usernameCol;
  @FXML TableColumn assignedStaffCol;
  @FXML TableColumn madeCol;
  @FXML TableColumn roomCol;
  @FXML SearchableComboBox<Integer> reservationIDText;
  @FXML SearchableComboBox<String> assignStaffText;
  @FXML MFXButton submitButton;

  @FXML MFXButton refreshButton;
  @FXML AnchorPane root;

  private double totalPrice = 0.0;
  @Getter @Setter static int roomNum;
  @FXML DatePicker dateBox;
  @FXML SearchableComboBox<String> requestStaffCombo;
  //  ObservableList<String> serviceType =
  //      FXCollections.observableArrayList(
  //          "", "Meal Request", "Flower Request", "Furniture Request", "Office Supply Request");
  ObservableList<String> statusValue =
      FXCollections.observableArrayList("", "PROCESSING", "BLANK", "DONE");

  /**
   * filters the list of service requests to add it to the table
   *
   * @param date date to be filtered to
   * @param username username of staff to filter to
   * @return the list of filtered items
   * @throws SQLException if there is an error when connecting to the database
   */
  public static ObservableList<ConfReservation> tableFilter(Timestamp date, String username)
      throws SQLException {
    ObservableList<ConfReservation> reservationList =
        FXCollections.observableList(DataManager.getAllConfReservation());
    if (!(date == (null))) {
      reservationList =
          FXCollections.observableList(
              reservationList.stream()
                  .filter(
                      (reservation) ->
                          (reservation.getDateBook().getDate() == date.getDate())
                              && (reservation.getDateBook().getMonth() == date.getMonth()))
                  .toList());
    }
    if (roomNum != 0) {
      reservationList =
          FXCollections.observableList(
              reservationList.stream()
                  .filter((reservation) -> (roomNum == reservation.getRoomID()))
                  .toList());
    }
    if (!(username == (null)) && !(username.toString().equals(""))) {
      reservationList =
          FXCollections.observableList(
              reservationList.stream()
                  .filter((reservation) -> reservation.getStaffAssigned().equals(username))
                  .toList());
    }
    return reservationList;
  }

  /**
   * assigns a staff and status to a request
   *
   * @param id id we want to assign to
   * @param assignStaff staff we want to assign the request to
   * @throws SQLException if there is an error connecting to the database
   */
  public void assignStuff(String id, String assignStaff) throws SQLException {
    DataManager.uploadStaffNameToConferenceRequest(Integer.parseInt(id), assignStaff);
    reservationIDText.setValue(null);
    assignStaffText.setValue(null);
    refreshTable();
  }

  /**
   * refreshes the contents of the table
   *
   * @throws SQLException
   */
  public void refreshTable() throws SQLException {
    ObservableList<ConfReservation> reservations =
        FXCollections.observableList(DataManager.getAllConfReservation());
    FilteredList<ConfReservation> serviceRequests1 = new FilteredList<>(reservations);
    //    serviceRequests1.predicateProperty().bind(table.predicateProperty());
    SortedList<ConfReservation> sortedServiceReq = new SortedList<>(serviceRequests1);
    table.setItems(sortedServiceReq);
  }

  public void setLanguage(Language lang) throws SQLException {
    switch (lang) {
      case ENGLISH:
        ParentController.titleString.set("Conference Room Reservations View");
        filterTableLabel.setText("Filter Table");
        dateLabel.setText("Date");
        refreshButton.setText("Refresh");
        assignedLabel.setText("Assigned Staff");
        requestStaffCombo.setPromptText("Choose Staff");
        assignStaffLabel.setText("Assign Staff to Reservation");
        idLabel.setText("Reservation ID");
        reservationIDText.setPromptText("Select Reservation ID");
        assignStaffLabel1.setText("Assign Staff");
        assignStaffText.setPromptText("Select Staff");
        submitButton.setText("Assign");
        resIDCol.setText("Reservation ID");
        dateCol.setText("Datebook");
        startCol.setText("Start Time");
        endCol.setText("End Time");
        nameCol.setText("Name");
        usernameCol.setText("Username");
        assignedStaffCol.setText("Assigned Staff");
        madeCol.setText("Date Made");
        roomCol.setText("Room ID");
        break;
      case FRENCH:
        ParentController.titleString.set("Vue des réservations de salle de conférence");
        filterTableLabel.setText("Filtrer la table");
        dateLabel.setText("Date");
        refreshButton.setText("Actualiser");
        assignedLabel.setText("Personnel assigné");
        requestStaffCombo.setPromptText("Choisir le personnel");
        assignStaffLabel.setText("Assigner le personnel à la réservation");
        idLabel.setText("ID de réservation");
        reservationIDText.setPromptText("Sélectionner l'ID de réservation");
        assignStaffLabel1.setText("Assigner le personnel");
        assignStaffText.setPromptText("Sélectionner le personnel");
        submitButton.setText("Assigner");
        resIDCol.setText("ID de réservation");
        dateCol.setText("Date");
        startCol.setText("Heure de début");
        endCol.setText("Heure de fin");
        nameCol.setText("Nom");
        usernameCol.setText("Nom d'utilisateur");
        assignedStaffCol.setText("Personnel assigné");
        madeCol.setText("Date de création");
        roomCol.setText("ID de la salle");
        break;
      case ITALIAN:
        ParentController.titleString.set("Visualizzazione prenotazioni sala conferenze");
        filterTableLabel.setText("Filtrare la Tabella");
        dateLabel.setText("Data");
        refreshButton.setText("Aggiorna");
        assignedLabel.setText("Personale assegnato");
        requestStaffCombo.setPromptText("Scegli il personale");
        assignStaffLabel.setText("Assegna il personale alla prenotazione");
        idLabel.setText("ID prenotazione");
        reservationIDText.setPromptText("Seleziona l'ID della prenotazione");
        assignStaffLabel1.setText("Assegna il personale");
        assignStaffText.setPromptText("Seleziona il personale");
        submitButton.setText("Assegna");
        resIDCol.setText("ID prenotazione");
        dateCol.setText("Data");
        startCol.setText("Ora di inizio");
        endCol.setText("Ora di fine");
        nameCol.setText("Nome");
        usernameCol.setText("Nome utente");
        assignedStaffCol.setText("Personale assegnato");
        madeCol.setText("Data di creazione");
        roomCol.setText("ID della sala");
        break;
      case SPANISH:
        ParentController.titleString.set("Vista de Reservas de Sala de Conferencias");
        filterTableLabel.setText("Filtrar Tabla");
        dateLabel.setText("Fecha");
        refreshButton.setText("Actualizar");
        assignedLabel.setText("Personal Asignado");
        requestStaffCombo.setPromptText("Elegir Personal");
        assignStaffLabel.setText("Asignar Personal a la Reserva");
        idLabel.setText("ID de Reserva");
        reservationIDText.setPromptText("Seleccionar ID de Reserva");
        assignStaffLabel1.setText("Asignar Personal");
        assignStaffText.setPromptText("Seleccionar Personal");
        submitButton.setText("Asignar");
        resIDCol.setText("ID de Reserva");
        dateCol.setText("Fecha");
        startCol.setText("Hora de Inicio");
        endCol.setText("Hora de Finalización");
        nameCol.setText("Nombre");
        usernameCol.setText("Nombre de Usuario");
        assignedStaffCol.setText("Personal Asignado");
        madeCol.setText("Fecha de Creación");
        roomCol.setText("ID de Sala");
        break;
    }
    ObservableList<String> staffNames =
        FXCollections.observableArrayList(DataManager.getAllUsernames());
    staffNames.add(null);
    requestStaffCombo.setItems(staffNames);

    reservationIDText.setItems(
        FXCollections.observableList(DataManager.getAllConferenceRequestIDs()));
    assignStaffText.setItems(FXCollections.observableList(DataManager.getAllUsernames()));
  }
  /**
   * initializes the serviceRequestView page
   *
   * @throws SQLException if there is an error connecting to the database
   */
  @FXML
  public void initialize() throws SQLException {
    roomNum = 0;
    ThemeSwitch.switchTheme(root);
    ParentController.titleString.set("Conference Room Reservations View");
    setLanguage(GlobalVariables.getB().getValue());
    GlobalVariables.b.addListener(
        (options, oldValue, newValue) -> {
          try {
            setLanguage(newValue);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    submitButton.disableProperty().bind(Bindings.isNull(reservationIDText.valueProperty()));
    submitButton.disableProperty().bind(Bindings.isNull(assignStaffText.valueProperty()));

    submitButton.setOnMouseClicked(
        event -> {
          try {
            assignStuff(
                String.valueOf(reservationIDText.valueProperty().getValue()),
                assignStaffText.valueProperty().getValue());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    ObservableList<String> staffNames =
        FXCollections.observableArrayList(DataManager.getAllUsernames());
    staffNames.add(null);
    requestStaffCombo.setItems(staffNames);

    reservationIDText.setItems(
        FXCollections.observableList(DataManager.getAllConferenceRequestIDs()));
    assignStaffText.setItems(FXCollections.observableList(DataManager.getAllUsernames()));

    ObservableList<ConfReservation> reservations =
        FXCollections.observableList(DataManager.getAllConfReservation());
    FilteredList<ConfReservation> reservation1 = new FilteredList<>(reservations);
    //    serviceRequests1.predicateProperty().bind(table.predicateProperty());
    SortedList<ConfReservation> sortedRes = new SortedList<>(reservation1);
    resIDCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("resID"));
    startCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("startTime"));
    endCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("endTime"));
    dateCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("dateBook"));
    nameCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("name"));
    usernameCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("username"));
    assignedStaffCol.setCellValueFactory(
        new PropertyValueFactory<ConfReservation, String>("staffAssigned"));
    madeCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("dateMade"));
    roomCol.setCellValueFactory(new PropertyValueFactory<ConfReservation, String>("roomID"));

    dateBox.setOnAction(
        event -> {
          try {
            // update the table when the status combo box is changed
            table.setItems(
                tableFilter(
                    Timestamp.valueOf(dateBox.getValue().atStartOfDay()),
                    requestStaffCombo.getValue()));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
    refreshButton.setOnAction(
        event -> {
          dateBox.cancelEdit();
          dateBox.setValue(null);
          try {
            table.setItems(tableFilter(null, requestStaffCombo.getValue()));
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    requestStaffCombo.setOnAction(
        event -> {
          try {
            // update the table when the status combo box is changed
            table.setItems(
                tableFilter(
                    Timestamp.valueOf(dateBox.getValue().atStartOfDay()),
                    requestStaffCombo.getValue()));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });

    //    table
    //        .getSelectionModel()
    //        .selectedItemProperty()
    //        .addListener(
    //            new ChangeListener<ServiceRequest>() {
    //              @Override
    //              public void changed(
    //                  ObservableValue<? extends ServiceRequest> observable,
    //                  ServiceRequest oldValue,
    //                  ServiceRequest newValue) {
    //
    //                table.setOnMouseClicked(
    //                    event -> {
    //                      ViewButton.setVisible(true);
    //                      ViewButton.setDisable(false);
    //                      ViewButton.setText("View Order #" + newValue.getRequestID());
    //                    });
    //
    //                if (newValue != null) {
    //                  ViewButton.setOnMouseClicked(
    //                      event -> {
    //                        table.setVisible(false);
    //                        table.setDisable(true);
    //                        ViewButton.setVisible(false);
    //                        ViewButton.setDisable(true);
    //                        summaryPane.setVisible(true);
    //                        summaryPane.setDisable(false);
    //                        backButton.setVisible(true);
    //                        backButton.setDisable(false);
    //                        try {
    //                          System.out.println("Display");
    //                          String f = "";
    //                          int req = newValue.getRequestID();
    //                          totalPrice = 0.0;
    //                          fillPane(req, f);
    //                        } catch (SQLException e) {
    //                          System.out.println(e);
    //                        }
    //                      });
    //                }
    //              }
    //            });

    table.setItems(sortedRes);

    if (GlobalVariables.isRequestFromMap()) {
      dateBox.setValue(GlobalVariables.getDateFromMap().toLocalDateTime().toLocalDate());
      setRoomNum(GlobalVariables.getRoomIDFromMap());
      GlobalVariables.setRequestFromMap(false);
      try {
        // update the table when the status combo box is changed
        table.setItems(
            tableFilter(
                Timestamp.valueOf(dateBox.getValue().atStartOfDay()),
                requestStaffCombo.getValue()));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    //    backButton.setOnMouseClicked(
    //        event -> {
    //          totalPrice = 0.0;
    //          System.out.println("Back " + totalPrice);
    //          table.setVisible(true);
    //          table.setDisable(false);
    //          summaryPane.setVisible(false);
    //          summaryPane.setDisable(true);
    //          backButton.setVisible(false);
    //          backButton.setDisable(true);
    //          ViewButton.setVisible(true);
    //          ViewButton.setDisable(false);
    //          cartBox.getChildren().clear();
    //        });
    //    if (GlobalVariables.isDoneRequestsPressed()) {
    //      GlobalVariables.setDoneRequestsPressed(false);
    //      requestStatusCombo.setValue(Status.DONE);
    //      requestStaffCombo.setValue(GlobalVariables.getCurrentUser().getUsername());
    //      table.setItems(
    //          tableFilter(
    //              requestTypeCombo.getValue(),
    //              requestStatusCombo.getValue(),
    //              requestStaffCombo.getValue()));
    //    } else if (GlobalVariables.isActiveRequestsPressed()) {
    //      requestStatusCombo.setValue(Status.PROCESSING);
    //      requestStaffCombo.setValue(GlobalVariables.getCurrentUser().getUsername());
    //      GlobalVariables.setActiveRequestsPressed(false);
    //      table.setItems(
    //          tableFilter(
    //              requestTypeCombo.getValue(),
    //              requestStatusCombo.getValue(),
    //              requestStaffCombo.getValue()));
    //    }
    //  }

    //  private void fillPane(int reqID, String folder) throws SQLException {
    //    ServiceRequest request = DataManager.getServiceRequest(reqID);
    //    ArrayList<ItemsOrdered> orderedItems = new ArrayList<>();
    //    ArrayList<RequestItem> tempItems = new ArrayList<>();
    //    orderedItems = DataManager.getItemsFromReq(reqID);
    //    for (int i = 0; i < orderedItems.size(); i++) {
    //      ItemsOrdered item = orderedItems.get(i);
    //      if (item.getItemID() / 100 >= 10 && item.getItemID() / 100 < 11) { // flower
    //        folder = "FlowerIcons";
    //        tempItems.add(DataManager.getFlower(item.getItemID()));
    //      } else if (item.getItemID() / 100 >= 11 && item.getItemID() / 100 < 12) { // meal
    //        folder = "MealIcons";
    //        tempItems.add(DataManager.getMeal(item.getItemID()));
    //      } else if (item.getItemID() / 100 >= 13 && item.getItemID() / 100 < 14) { // furniture
    //        folder = "FurnitureIcons";
    //        tempItems.add(DataManager.getFurniture(item.getItemID()));
    //      } else if (item.getItemID() / 100 >= 14 && item.getItemID() / 100 < 15) { // office
    // supply
    //        folder = "OfficeIcons";
    //        tempItems.add(DataManager.getOfficeSupply(item.getItemID()));
    //      } else if (item.getItemID() / 100 >= 15 && item.getItemID() / 100 < 16) { // medical
    // Supply
    //        folder = "MedicalIcons";
    //        tempItems.add(DataManager.getMedicalSupply(item.getItemID()));
    //      }
    //      try {
    //        tempItems.get(i).getItemID();
    //        totalPrice += (orderedItems.get(i).getQuantity() * tempItems.get(i).getPrice());
    //        cartBox.getChildren().add(new ReqMenuItems(tempItems.get(i), folder,
    // item.getQuantity()));
    //      } catch (NullPointerException e) { // no items in request
    //        System.out.println(e);
    //        totalPrice = 0.0;
    //      }
    //    }
    //
    //    String details = request.getDetails();
    //    int d = details.indexOf("Deliver");
    //    detailsLabel.setText(details.substring(0, d)); // cut string at Deliver by
    //    detailsLabel1.setText(details.substring(d) + "   Status: " + request.getStatus());
    //    DecimalFormat format = new DecimalFormat("###0.00");
    //    totalLabel.setText("Order Total: $" + format.format(totalPrice));
  }
}
