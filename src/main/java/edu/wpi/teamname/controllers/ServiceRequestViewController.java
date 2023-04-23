package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.controllers.JFXitems.ReqMenuItems;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.servicerequest.ItemsOrdered;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import edu.wpi.teamname.servicerequest.requestitem.RequestItem;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.SearchableComboBox;

public class ServiceRequestViewController {

  @FXML TableView<ServiceRequest> table;
  @FXML TableColumn requestIDCol;
  @FXML TableColumn patientNameCol;
  @FXML TableColumn roomNumCol;
  @FXML TableColumn requesterIDCol;
  @FXML TableColumn requestedAtCol;
  @FXML TableColumn requestedForCol;
  @FXML TableColumn assignedStaffCol;
  @FXML TableColumn statusCol;
  @FXML SearchableComboBox<String> requestIDText;
  @FXML SearchableComboBox<String> assignStaffText;
  @FXML ComboBox<Status> requestStatusText;

  @FXML MFXButton submitButton;

  @FXML Label detailsLabel;
  @FXML Label detailsLabel1;
  @FXML Label totalLabel;
  @FXML MFXButton backButton;
  @FXML VBox cartBox;
  @FXML AnchorPane summaryPane;
  @FXML MFXButton ViewButton;
  private double totalPrice = 0.0;

  @FXML ComboBox<RequestType> requestTypeCombo;
  @FXML ComboBox<String> requestStaffCombo;
  //  ObservableList<String> serviceType =
  //      FXCollections.observableArrayList(
  //          "", "Meal Request", "Flower Request", "Furniture Request", "Office Supply Request");
  ObservableList<String> statusValue =
      FXCollections.observableArrayList("", "PROCESSING", "BLANK", "DONE");
  @FXML ComboBox<Status> requestStatusCombo;

  /**
   * filters the list of service requests to add it to the table
   *
   * @param one the request type that we want to see
   * @param two the status that we want to see
   * @return the list of filtered items
   * @throws SQLException if there is an error when connecting to the database
   */
  public static ObservableList<ServiceRequest> tableFilter(
      RequestType one, Status two, String username) throws SQLException {
    ObservableList<ServiceRequest> requestList =
        FXCollections.observableList(DataManager.getAllServiceRequests());
    if (!(one == (null)) && !(one.toString().equals(""))) {
      requestList =
          FXCollections.observableList(
              requestList.stream()
                  .filter((request) -> request.getRequestType().toString().equals(one.toString()))
                  .toList());
    }
    if (!(two == (null)) && !(two.toString().equals(""))) {
      requestList =
          FXCollections.observableList(
              requestList.stream()
                  .filter((request) -> request.getStatus().toString().equals(two.toString()))
                  .toList());
    }
    if (!(username == (null)) && !(username.toString().equals(""))) {
      requestList =
          FXCollections.observableList(
              requestList.stream()
                  .filter((request) -> request.getStaffName().equals(username))
                  .toList());
    }
    return requestList;
  }

  /**
   * assigns a staff and status to a request
   *
   * @param id id we want to assign to
   * @param assignStaff staff we want to assign the request to
   * @param requestStatus status we want to assign the request to
   * @throws SQLException if there is an error connecting to the database
   */
  public void assignStuff(String id, String assignStaff, Status requestStatus) throws SQLException {
    DataManager.uploadStatusToServiceRequest(Integer.parseInt(id), requestStatus.getStatusString());
    DataManager.uploadStaffNameToServiceRequest(Integer.parseInt(id), assignStaff);
    requestIDText.setValue(null);
    assignStaffText.setValue(null);
    requestStatusText.setValue(null);
    refreshTable();
  }

  public void refreshTable() throws SQLException {
    ObservableList<ServiceRequest> serviceRequests =
        FXCollections.observableList(DataManager.getAllServiceRequests());
    FilteredList<ServiceRequest> serviceRequests1 = new FilteredList<>(serviceRequests);
    //    serviceRequests1.predicateProperty().bind(table.predicateProperty());
    SortedList<ServiceRequest> sortedServiceReq = new SortedList<>(serviceRequests1);
    table.setItems(sortedServiceReq);
  }

  /**
   * initializes the serviceRequestView page
   *
   * @throws SQLException if there is an error connecting to the database
   */
  @FXML
  public void initialize() throws SQLException {
    ParentController.titleString.set("Service Request View");
    submitButton.disableProperty().bind(Bindings.isNull(requestIDText.valueProperty()));
    submitButton.disableProperty().bind(Bindings.isNull(assignStaffText.valueProperty()));
    submitButton.disableProperty().bind(Bindings.isNull(requestStatusText.valueProperty()));

    submitButton.setOnMouseClicked(
        event -> {
          try {
            assignStuff(
                requestIDText.valueProperty().getValue(),
                assignStaffText.valueProperty().getValue(),
                requestStatusText.valueProperty().getValue());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    ObservableList<RequestType> requestTypes =
        FXCollections.observableArrayList(RequestType.values());
    requestTypes.add(null);
    requestTypeCombo.setItems(requestTypes);

    ObservableList<String> staffNames =
        FXCollections.observableArrayList(DataManager.getAllUsernames());
    staffNames.add(null);
    requestStaffCombo.setItems(staffNames);
    ObservableList<Status> requestStatuses = FXCollections.observableArrayList(Status.values());
    requestStatuses.add(null);
    requestStatusCombo.setItems(requestStatuses);

    requestIDText.setItems(FXCollections.observableList(DataManager.getAllRequestIDs()));
    assignStaffText.setItems(FXCollections.observableList(DataManager.getAllUsernames()));
    ObservableList<Status> requestStatuses2 = FXCollections.observableArrayList(Status.values());

    requestStatusText.setItems(requestStatuses2);

    ObservableList<ServiceRequest> serviceRequests =
        FXCollections.observableList(DataManager.getAllServiceRequests());
    FilteredList<ServiceRequest> serviceRequests1 = new FilteredList<>(serviceRequests);
    //    serviceRequests1.predicateProperty().bind(table.predicateProperty());
    SortedList<ServiceRequest> sortedServiceReq = new SortedList<>(serviceRequests1);
    requestIDCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("requestID"));
    roomNumCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("roomNumber"));
    assignedStaffCol.setCellValueFactory(
        new PropertyValueFactory<ServiceRequest, String>("staffName"));
    patientNameCol.setCellValueFactory(
        new PropertyValueFactory<ServiceRequest, String>("patientName"));
    requestedAtCol.setCellValueFactory(
        new PropertyValueFactory<ServiceRequest, String>("requestedAt"));
    requestedForCol.setCellValueFactory(
        new PropertyValueFactory<ServiceRequest, String>("deliverBy"));
    statusCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("status"));
    requesterIDCol.setCellValueFactory(
        new PropertyValueFactory<ServiceRequest, String>("requestMadeBy"));

    requestStatusCombo.setOnAction(
        event -> {
          try {
            // update the table when the status combo box is changed
            table.setItems(
                tableFilter(
                    requestTypeCombo.getValue(),
                    requestStatusCombo.getValue(),
                    requestStaffCombo.getValue()));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });

    requestTypeCombo.setOnAction(
        event -> {
          try {
            // update the table when the status combo box is changed
            table.setItems(
                tableFilter(
                    requestTypeCombo.getValue(),
                    requestStatusCombo.getValue(),
                    requestStaffCombo.getValue()));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });

    requestStaffCombo.setOnAction(
        event -> {
          try {
            // update the table when the status combo box is changed
            table.setItems(
                tableFilter(
                    requestTypeCombo.getValue(),
                    requestStatusCombo.getValue(),
                    requestStaffCombo.getValue()));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });

    table
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<ServiceRequest>() {
              @Override
              public void changed(
                  ObservableValue<? extends ServiceRequest> observable,
                  ServiceRequest oldValue,
                  ServiceRequest newValue) {

                table.setOnMouseClicked(
                    event -> {
                      ViewButton.setVisible(true);
                      ViewButton.setDisable(false);
                      ViewButton.setText("View Order #" + newValue.getRequestID());
                    });

                if (newValue != null) {
                  ViewButton.setOnMouseClicked(
                      event -> {
                        table.setVisible(false);
                        table.setDisable(true);
                        ViewButton.setVisible(false);
                        ViewButton.setDisable(true);
                        summaryPane.setVisible(true);
                        summaryPane.setDisable(false);
                        backButton.setVisible(true);
                        backButton.setDisable(false);
                        try {
                          System.out.println("Display");
                          String f = "";
                          int req = newValue.getRequestID();
                          totalPrice = 0.0;
                          fillPane(req, f);
                        } catch (SQLException e) {
                          System.out.println(e);
                        }
                      });
                }
              }
            });

    table.setItems(sortedServiceReq);

    backButton.setOnMouseClicked(
        event -> {
          totalPrice = 0.0;
          System.out.println("Back " + totalPrice);
          table.setVisible(true);
          table.setDisable(false);
          summaryPane.setVisible(false);
          summaryPane.setDisable(true);
          backButton.setVisible(false);
          backButton.setDisable(true);
          ViewButton.setVisible(true);
          ViewButton.setDisable(false);
          cartBox.getChildren().clear();
        });
    if (GlobalVariables.isDoneRequestsPressed()) {
      GlobalVariables.setDoneRequestsPressed(false);
      requestStatusCombo.setValue(Status.DONE);
      requestStaffCombo.setValue(GlobalVariables.getCurrentUser().getUsername());
      table.setItems(
          tableFilter(
              requestTypeCombo.getValue(),
              requestStatusCombo.getValue(),
              requestStaffCombo.getValue()));
    } else if (GlobalVariables.isActiveRequestsPressed()) {
      requestStatusCombo.setValue(Status.PROCESSING);
      requestStaffCombo.setValue(GlobalVariables.getCurrentUser().getUsername());
      GlobalVariables.setActiveRequestsPressed(false);
      table.setItems(
          tableFilter(
              requestTypeCombo.getValue(),
              requestStatusCombo.getValue(),
              requestStaffCombo.getValue()));
    }
  }

  private void fillPane(int reqID, String folder) throws SQLException {
    ServiceRequest request = DataManager.getServiceRequest(reqID);
    ArrayList<ItemsOrdered> orderedItems = new ArrayList<>();
    ArrayList<RequestItem> tempItems = new ArrayList<>();
    orderedItems = DataManager.getItemsFromReq(reqID);
    for (int i = 0; i < orderedItems.size(); i++) {
      ItemsOrdered item = orderedItems.get(i);
      if (item.getItemID() / 100 >= 10 && item.getItemID() / 100 < 11) { // flower
        folder = "FlowerIcons";
        tempItems.add(DataManager.getFlower(item.getItemID()));
      } else if (item.getItemID() / 100 >= 11 && item.getItemID() / 100 < 12) { // meal
        folder = "MealIcons";
        tempItems.add(DataManager.getMeal(item.getItemID()));
      } else if (item.getItemID() / 100 >= 13 && item.getItemID() / 100 < 14) { // furniture
        folder = "FurnitureIcons";
        tempItems.add(DataManager.getFurniture(item.getItemID()));
      } else if (item.getItemID() / 100 >= 14 && item.getItemID() / 100 < 15) { // office supply
        folder = "OfficeIcons";
        tempItems.add(DataManager.getOfficeSupply(item.getItemID()));
      } else if (item.getItemID() / 100 >= 15 && item.getItemID() / 100 < 16) { // medical Supply
        folder = "MedicalIcons";
        tempItems.add(DataManager.getMedicalSupply(item.getItemID()));
      }
      try {
        tempItems.get(i).getItemID();
        totalPrice += (orderedItems.get(i).getQuantity() * tempItems.get(i).getPrice());
        cartBox.getChildren().add(new ReqMenuItems(tempItems.get(i), folder, item.getQuantity()));
      } catch (NullPointerException e) { // no items in request
        totalPrice = 0.0;
      }
    }

    String details = request.getDetails();
    int d = details.indexOf("Deliver");
    detailsLabel.setText(details.substring(0, d)); // cut string at Deliver by
    detailsLabel1.setText(details.substring(d) + "   Status: " + request.getStatus());
    DecimalFormat format = new DecimalFormat("###0.00");
    totalLabel.setText("Order Total: $" + format.format(totalPrice));
  }
}
