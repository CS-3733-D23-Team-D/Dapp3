package edu.wpi.teamname.controllers;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.controllers.JFXitems.ReqMenuItems;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import edu.wpi.teamname.servicerequest.requestitem.*;
import io.github.palexdev.materialfx.controls.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

public class ServiceRequestController {

  // requestInfo Error if not added anything to both meal and side

  /**
   * @FXML MFXButton backButton; @FXML MFXButton setDateButton; @FXML MFXButton
   * printDateButton; @FXML MFXButton printMealButton; @FXML MFXButton addFriesButton; @FXML
   * MFXButton addSandwichButton; @FXML MFXButton addFlowersButton;
   */

  // bot2
  // Sam's Form GUI
  @FXML HBox rootPane;

  @FXML ImageView background;
  private int requestPage = 0; // used for keeping track of which page is active

  // Bottom Bar
  @FXML MFXButton nextButton;
  @FXML StackPane requestPane;
  @FXML MFXButton clearButton;
  @FXML MFXButton cancelButton;

  // Form pane
  @FXML AnchorPane formAnchor;
  @FXML AnchorPane formPane;
  // Form fields
  // @FXML TextField staffName;
  @FXML TextField patientName;
  @FXML ComboBox nodeBox;
  ObservableList<String> longNames =
      FXCollections.observableArrayList(DataManager.getNamesAlphabetically());
  @FXML DatePicker dateBox;
  @FXML ComboBox timeBox;
  ObservableList<String> timeValues = FXCollections.observableArrayList();
  ObservableList<String> serviceType =
      FXCollections.observableArrayList(
          "Meal Delivery",
          "Flower Delivery",
          "Office Supply Delivery",
          "Furniture Delivery",
          "Medical Supply Delivery");
  @FXML ComboBox requestType;

  // menu item page
  @FXML AnchorPane menuPane;
  @FXML TextField searchBar;
  @FXML VBox itemBox;
  @FXML ScrollPane glitchyPane;

  @FXML AnchorPane summaryPane;
  @FXML Label summaryLabel;

  @FXML VBox cartBox;

  public VBox getCartBox() {
    return cartBox;
  }

  @FXML Label totalLabel;
  @FXML MFXButton forgotButton;

  @Setter @Getter private ServiceRequest request;

  // ArrayList<Integer> itemIDs;

  public ServiceRequestController() throws SQLException {}

  /**
   * Controls the switching and progression through creating the service request
   *
   * @throws SQLException
   */
  private void nextPane() throws SQLException {

    System.out.println("NEXT");
    if (requestPage == 0) {
      String folder;
      String timeString = timeBox.getValue().toString();
      System.out.println(timeString);
      int hour = Integer.valueOf(timeString.split(":")[0]);
      int min = Integer.valueOf(timeString.split(":")[1]);
      System.out.println(hour);
      System.out.println(min);
      LocalDate date = dateBox.getValue();
      System.out.println(date);
      LocalTime time = LocalTime.of(hour, min);
      System.out.println(time);
      LocalDateTime reqDateTime = date.atTime(time);
      System.out.println(reqDateTime.toString());
      Timestamp reqTS = Timestamp.valueOf(reqDateTime);
      RequestType reqType;
      ArrayList<RequestItem> items = new ArrayList<>();
      if (requestType.getValue() == "Meal Delivery") {
        folder = "MealIcons";
        ArrayList<Meal> tems = DataManager.getAllMeals();
        items.addAll(tems);
        reqType = RequestType.MEAL;
      } else if (requestType.getValue() == "Flower Delivery") {
        folder = "FlowerIcons";
        ArrayList<Flower> tems = DataManager.getAllFlowers();
        items.addAll(tems);
        reqType = RequestType.FLOWER;
      } else if (requestType.getValue() == "Office Supply Delivery") {
        folder = "OfficeIcons";
        ArrayList<OfficeSupply> tems = DataManager.getAllOfficeSupplies();
        items.addAll(tems);
        reqType = RequestType.OFFICESUPPLY;
      } else if (requestType.getValue() == "Medical Supply Delivery") {
        folder = "MedicalIcons";
        ArrayList<MedicalSupply> temp = DataManager.getAllMedicalSupplies();
        items.addAll(temp);
        reqType = RequestType.MEDICALSUPPLY;
      } else { // "Furniture Delivery"
        folder = "FurnitureIcons";
        ArrayList<Furniture> tems = DataManager.getAllFurniture();
        items.addAll(tems);
        reqType = RequestType.FURNITURE;
      }
      String loc = (String) nodeBox.getValue();
      setRequest(
          new ServiceRequest(
              Instant.now().get(ChronoField.MICRO_OF_SECOND),
              "null",
              patientName.toString(),
              loc,
              reqTS,
              Timestamp.from(Instant.now()),
              Status.BLANK,
              GlobalVariables.getCurrentUser().getUsername(),
              reqType));
      // glitchyPane.setMaxHeight(glitchyPane.getHvalue());
      for (int a = 0; a < items.size(); a++) {
        // if (a < 10) {
        itemBox.getChildren().add(new ReqMenuItems(items.get(a), folder, getRequest(), true, this));
        // }
      }

      itemBox.setFillWidth(true);
      setVisibleScreen(1);
      nextButton.setText("Next");

      requestPage = 1;

      request.setPatientName(patientName.getCharacters().toString());
      // request.setRoomNumber(roomNum.getCharacters().toString());
      // request.setRoomNumber("");
      // request.setDeliverBy(dateBox.getValue().atStartOfDay());

    } else if (requestPage == 1) {
      setVisibleScreen(2);
      nextButton.setText("Submit");
      requestPage = 2;
      summaryLabel.setText(request.getDetails());

      ArrayList<RequestItem> tem = new ArrayList<>();
      double totalPrice = 0.0;
      String t = request.getRequestType().toString();
      String f;
      if (t == "Meal Request") {
        f = "MealIcons";
        tem.addAll(DataManager.getAllMeals());
      } else if (t == "Flower Request") {
        f = "FlowerIcons";
        tem.addAll(DataManager.getAllFlowers());
      } else if (t == "Office Supply Request") {
        f = "OfficeIcons";
        tem.addAll(DataManager.getAllOfficeSupplies());
      } else if (t == "Medical Supply Request") {
        f = "MedicalIcons";
        tem.addAll(DataManager.getAllMedicalSupplies());
      } else {
        f = "FurnitureIcons";
        System.out.println(t);
        tem.addAll(DataManager.getAllFurniture());
      }
      int c = 0;
      // System.out.println(tem);
      // System.out.println(request.getItems());
      for (RequestItem item : tem) {
        c = request.countItem(item.getItemID());
        // System.out.println(c);
        if (c > 0) {
          cartBox.getChildren().add(new ReqMenuItems(item, f, this.request, false, this, c));
          // totalPrice += c * item.getPrice();
        }
      }
      refreshPrice();
      //      System.out.println(totalPrice);
      //      DecimalFormat format = new DecimalFormat("###.00");
      //      totalLabel.setText(totalLabel.getText() + format.format(totalPrice));

    } else if (requestPage == 2) {
      setVisibleScreen(0);
      requestPage = 0;
      nextButton.setText("Next");
      DataManager.addServiceRequest(request);
      Navigation.navigate(Screen.HOME);

      System.out.println(request);
    }
  }

  /** Clears the service request and brings you back to the home page */
  private void cancelAction() {
    clearAction();
    Navigation.navigate(Screen.HOME);
  }

  /** Clears the service request form and currently created service request */
  private void clearAction() {
    patientName.clear();
    // staffName.clear();
    // roomNum.clear();
    requestType.cancelEdit();
    dateBox.cancelEdit();
    if (requestPage > 0) {
      setVisibleScreen(0);
      requestPage = 0;
      nextButton.setText("Next");
      // request.clearItems();
    }
    itemBox.getChildren().clear();
    cartBox.getChildren().clear();
  }

  /**
   * Sets the visible page of the service request form
   *
   * @param n the page number for the page to display 0 is the form 1 is the menu 2 is the summary
   */
  public void setVisibleScreen(int n) {
    if (n == 1) {
      formPane.setVisible(false);
      formPane.setDisable(true);
      menuPane.setDisable(false);
      menuPane.setVisible(true);
      summaryPane.setVisible(false);
      summaryPane.setDisable(true);
      nextButton.setText("Next");
      requestPage = 1;
    } else if (n == 2) {
      formPane.setVisible(false);
      formPane.setDisable(true);
      menuPane.setDisable(true);
      menuPane.setVisible(false);
      summaryPane.setVisible(true);
      summaryPane.setDisable(false);
      nextButton.setText("Submit");
      requestPage = 2;
    } else {
      formPane.setVisible(true);
      formPane.setDisable(false);
      menuPane.setDisable(true);
      menuPane.setVisible(false);
      summaryPane.setVisible(false);
      summaryPane.setDisable(true);
      timeBox.setDisable(false);
      nextButton.setText("Next");
      requestPage = 0;
    }
  }

  public void initialize() {
    ParentController.titleString.set("Service Request");
    setVisibleScreen(0);

    for (int h = 0; h < 24; h++) {

      timeValues.add(Integer.toString(h) + ":00");
      timeValues.add(Integer.toString(h) + ":15");
      timeValues.add(Integer.toString(h) + ":30");
      timeValues.add(Integer.toString(h) + ":45");
    }
    timeBox.setItems(timeValues);
    nodeBox.setItems(longNames);

    nextButton.setText("Next");

    nextButton.setOnMouseClicked(
        event -> {
          try {
            nextPane();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    cancelButton.setOnMouseClicked(event -> cancelAction());
    clearButton.setOnMouseClicked(event -> clearAction());

    requestType.setItems(serviceType);

    // request = new ServiceRequest();
    requestPage = 0;

    itemBox.setFillWidth(true);
    itemBox.setSpacing(25);

    forgotButton.setOnMouseClicked(
        event -> {
          setVisibleScreen(1);
          cartBox.getChildren().clear();
          totalLabel.setText("Total Price: ");
        });

    Platform.runLater(
        () -> {
          double height = glitchyPane.getHeight();
          glitchyPane.setMaxHeight(height);
        });
  }

  public void refreshPrice() throws SQLException {
    totalLabel.setText("Total Price: ");
    ArrayList<RequestItem> tem = new ArrayList<>();
    double totalPrice = 0.0;
    String t = request.getRequestType().toString();
    if (t == "Meal Request") {
      tem.addAll(DataManager.getAllMeals());
    } else if (t == "Flower Request") {
      tem.addAll(DataManager.getAllFlowers());
    } else if (t == "Office Supply Request") {
      tem.addAll(DataManager.getAllOfficeSupplies());
    } else if (t == "Medical Supply Request") {
      tem.addAll(DataManager.getAllMedicalSupplies());
    } else {
      System.out.println(t);
      tem.addAll(DataManager.getAllFurniture());
    }
    int c = 0;
    // System.out.println(tem);
    // System.out.println(request.getItems());
    for (RequestItem item : tem) {
      c = request.countItem(item.getItemID());
      System.out.println(c);
      if (c > 0) {
        totalPrice += c * item.getPrice();
      }
    }
    System.out.println(totalPrice);
    DecimalFormat format = new DecimalFormat("###0.00");
    totalLabel.setText(totalLabel.getText() + format.format(totalPrice));
  }
}
