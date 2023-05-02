package edu.wpi.teamname.controllers;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.Sound;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The DataController class is responsible for handling the importing and exporting of data to and
 * from the database. It contains methods for initializing the GUI components and for handling the
 * actions performed by the user on the GUI.
 */
public class DataController implements Initializable {
  @FXML private ComboBox<String> importComboBox;

  @FXML private ComboBox<String> exportComboBox;

  @FXML private Button importButton;

  @FXML private Button exportButton;

  private static final String[] FIELDS = {
    "Alert",
    "Conference Rooms",
    "Conference Reservations",
    "Edge",
    "Employee",
    "Employee Type",
    "Flowers",
    "Furniture",
    "Items Ordered",
    "Location Name",
    "Meal",
    "Medical Supplies",
    "Move",
    "Node",
    "Office Supply",
    "Path Messages",
    "Pharmaceutical",
    "Service Request",
    "Signage",
    "Feedback"
  };

  /**
   * Initializes the GUI components of the Data Manager page. Sets the title of the page, populates
   * the import and export ComboBoxes with the available options, and sets the action listeners for
   * the buttons.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ParentController.titleString.set("Data Manager");
    importComboBox.getItems().addAll(FIELDS);
    exportComboBox.getItems().addAll(FIELDS);

    importButton.setOnAction(e -> onImportButtonClicked());
    exportButton.setOnAction(e -> onExportButtonClicked());
  }

  private void onImportButtonClicked() {
    Sound.playOnButtonClick();
    FileChooser fileChooser = new FileChooser();
    String selectedItem = importComboBox.getSelectionModel().getSelectedItem();
    fileChooser.setTitle("Open CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    Stage stage = (Stage) importButton.getScene().getWindow();
    File csvFile = fileChooser.showOpenDialog(stage);
    if (csvFile != null) {
      if (selectedItem != null) {
        try {
          switch (selectedItem) {
            case "Edge":
              DataManager.uploadEdge(csvFile.getPath());
              break;
            case "Node":
              DataManager.uploadNode(csvFile.getPath());
              break;
            case "Conference Room":
              // DataManager.upload(csvFile.getPath());
              break;
            case "Conference Reservations":
              // DataManager.upload(csvFile.getPath());
              break;
            case "Alert":
              DataManager.uploadAlert(csvFile.getPath());
              break;
            case "Employee":
              DataManager.uploadEmployee(csvFile.getPath());
              break;
            case "Employee Type":
              // DataManager.uploadEmployeeType(csvFile.getPath());
              break;
            case "Flowers":
              DataManager.uploadFlower(csvFile.getPath());
              break;
            case "Furniture":
              DataManager.uploadFurniture(csvFile.getPath());
              break;
            case "Items Ordered":
              DataManager.uploadItemsOrdered(csvFile.getPath());
              break;
            case "Location Name":
              DataManager.uploadLocationName(csvFile.getPath());
              break;
            case "Meal":
              DataManager.uploadMeal(csvFile.getPath());
              break;
            case "Medical Supplies":
              DataManager.uploadMedicalSupply(csvFile.getPath());
              break;
            case "Move":
              DataManager.uploadMove(csvFile.getPath());
              break;
            case "Office Supply":
              DataManager.uploadOfficeSupply(csvFile.getPath());
              break;
            case "Path Messages":
              // DataManager.uploadPathMessages(csvFile.getPath());
              break;
            case "Service Requests":
              DataManager.uploadServiceRequest(csvFile.getPath());
              break;
            case "Signage":
              DataManager.uploadSignage(csvFile.getPath());
              break;
            case "Feedback":
              DataManager.uploadFeedback(csvFile.getPath());
              break;
            case "Pharmaceutical":
              DataManager.uploadPharmaceutical(csvFile.getPath());
              break;
          }
        } catch (SQLException | ParseException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void onExportButtonClicked() {
    Sound.playOnButtonClick();
    String selectedItem = exportComboBox.getSelectionModel().getSelectedItem();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save CSV File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

    if (selectedItem != null) {
      fileChooser.setInitialFileName(selectedItem.toLowerCase() + ".csv");
    }

    Stage stage = (Stage) exportButton.getScene().getWindow();
    File csvFile = fileChooser.showSaveDialog(stage);

    if (csvFile != null) {
      if (selectedItem != null) {
        try {
          switch (selectedItem) {
            case "Edge":
              fileChooser.setInitialFileName("edge.csv");
              DataManager.exportEdgeToCSV(csvFile.getPath());
              break;
            case "Node":
              fileChooser.setInitialFileName("node.csv");
              DataManager.exportNodeToCSV(csvFile.getPath());
              break;
            case "Conference Room":
              fileChooser.setInitialFileName("conferenceRoom.csv");
              // DataManager.exportConferenceRoomToCSV(csvFile.getPath());
              break;
            case "Conference Reservations":
              fileChooser.setInitialFileName("conferenceReservations.csv");
              // DataManager.exportConferenceRoomToCSV(csvFile.getPath());
              break;
            case "Alert":
              fileChooser.setInitialFileName("alert.csv");
              DataManager.exportAlertToCSV(csvFile.getPath());
              break;
            case "Employee":
              fileChooser.setInitialFileName("employee.csv");
              DataManager.exportEmployeeToCSV(csvFile.getPath());
              break;
            case "Employee Type":
              fileChooser.setInitialFileName("employeeType.csv");
              // DataManager.exportEmployeeTypeToCSV(csvFile.getPath());
              break;
            case "Flowers":
              fileChooser.setInitialFileName("flowers.csv");
              DataManager.exportFlowersToCSV(csvFile.getPath());
              break;
            case "Furniture":
              fileChooser.setInitialFileName("furniture.csv");
              DataManager.exportFurnitureToCSV(csvFile.getPath());
              break;
            case "Items Ordered":
              fileChooser.setInitialFileName("itemsOrdered.csv");
              DataManager.exportItemsOrderedToCSV(csvFile.getPath());
              break;
            case "Location Name":
              fileChooser.setInitialFileName("locationName.csv");
              DataManager.exportLocationNameToCSV(csvFile.getPath());
              break;
            case "Meal":
              fileChooser.setInitialFileName("meal.csv");
              DataManager.exportMealToCSV(csvFile.getPath());
              break;
            case "Medical Supplies":
              fileChooser.setInitialFileName("medicalSupplies.csv");
              DataManager.exportMedicalSupplyToCSV(csvFile.getPath());
              break;
            case "Office Supplies":
              fileChooser.setInitialFileName("officeSupplies.csv");
              DataManager.exportOfficeSupplyToCSV(csvFile.getPath());
              break;
            case "Path Messages":
              fileChooser.setInitialFileName("pathMessages.csv");
              // DataManager.exportPathMessagesToCSV(csvFile.getPath());
              break;
            case "Pharmaceutical":
              fileChooser.setInitialFileName("pharmaceutical.csv");
              DataManager.exportPharmaceuticalToCSV(csvFile.getPath());
              break;
            case "Service Request":
              fileChooser.setInitialFileName("serviceRequest.csv");
              DataManager.exportServiceRequestToCSV(csvFile.getPath());
              break;
            case "Signage":
              fileChooser.setInitialFileName("signage.csv");
              DataManager.exportSignageToCSV(csvFile.getPath());
              break;
            case "Feedback":
              fileChooser.setInitialFileName("feedback.csv");
              DataManager.exportFeedbackToCSV(csvFile.getPath());
              break;
          }
        } catch (SQLException e) {
          throw new RuntimeException(e);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
