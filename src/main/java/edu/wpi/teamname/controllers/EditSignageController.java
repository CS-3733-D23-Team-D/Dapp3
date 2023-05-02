package edu.wpi.teamname.controllers;

import static edu.wpi.teamname.database.DataManager.syncSignage;

import edu.wpi.teamname.controllers.helpers.DatePickerTableCell;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.database.SignageDAOImpl;
import edu.wpi.teamname.database.interfaces.SignageDAO;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.navigation.Direction;
import edu.wpi.teamname.navigation.Signage;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.sql.SQLException;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jdk.jfr.Timestamp;

/**
 * This class represents a controller for the Signage Table GUI, which allows users to view, add,
 * edit, and delete signage data. It initializes the GUI and sets up event handlers for various GUI
 * components.
 */
public class EditSignageController {

  @FXML private TableView<Signage> editSignageTable;
  @FXML private TextField signIDinput;
  @FXML private TextField longNameInput;
  @FXML private TextField shortNameInput;
  @FXML private DatePicker dateInput;
  @FXML private ComboBox<String> directionPicker;
  @FXML private TextField kioskInput;
  @FXML private DatePicker endDateInput;
  @FXML private MFXButton submitButton;
  //  @FXML private MFXButton importButton;
  //  @FXML private MFXButton exportButton;
  @FXML private AnchorPane rootPane;
  @FXML private TextField searchBar;
  /** Initializes the GUI and sets up event handlers for various GUI components. */
  public void initialize() {
    DataManager signageDAO = new DataManager();
    ParentController.titleString.set("Signage Editor");
    TableColumn<Signage, String> longNameColumn = new TableColumn<>("Long Name");
    longNameColumn.setCellValueFactory(new PropertyValueFactory<>("longName"));

    TableColumn<Signage, String> shortNameColumn = new TableColumn<>("Short Name");
    shortNameColumn.setCellValueFactory(new PropertyValueFactory<>("shortName"));

    TableColumn<Signage, Timestamp> dateColumn = new TableColumn<>("Date");
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    dateColumn.setCellFactory(
        new Callback<TableColumn<Signage, Timestamp>, TableCell<Signage, Timestamp>>() {
          @Override
          public TableCell<Signage, Timestamp> call(TableColumn<Signage, Timestamp> param) {
            return new DatePickerTableCell();
          }
        });
    TableColumn<Signage, Timestamp> endDateColumn = new TableColumn<>("End Date");
    endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    endDateColumn.setCellFactory(
        new Callback<TableColumn<Signage, Timestamp>, TableCell<Signage, Timestamp>>() {
          @Override
          public TableCell<Signage, Timestamp> call(TableColumn<Signage, Timestamp> param) {
            return new DatePickerTableCell();
          }
        });

    TableColumn<Signage, Direction> arrowDirectionColumn = new TableColumn<>("Direction");
    arrowDirectionColumn.setCellValueFactory(new PropertyValueFactory<>("arrowDirection"));

    TableColumn<Signage, Integer> signIDColumn = new TableColumn<>("Sign ID");
    signIDColumn.setCellValueFactory(new PropertyValueFactory<>("signId"));

    TableColumn<Signage, Integer> kioskIDColumn = new TableColumn<>("Kiosk ID");
    kioskIDColumn.setCellValueFactory(new PropertyValueFactory<>("kioskId"));

    longNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    shortNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    StringConverter<Direction> directionConverter =
        new StringConverter<Direction>() {
          @Override
          public String toString(Direction direction) {
            if (direction == null) {
              return null;
            }
            return direction.name();
          }

          @Override
          public Direction fromString(String string) {
            if (string == null || string.isEmpty()) {
              return null;
            }
            for (Direction direction : Direction.values()) {
              if (direction.getString().equalsIgnoreCase(string)) {
                return direction;
              }
            }
            return null;
          }
        };

    arrowDirectionColumn.setCellFactory(TextFieldTableCell.forTableColumn(directionConverter));

    ContextMenu contextMenu = new ContextMenu();
    MenuItem deleteMenuItem = new MenuItem("Delete");
    contextMenu.getItems().add(deleteMenuItem);

    // Make the TableView editable
    editSignageTable.setEditable(true);

    // Add the event handlers for committing the edits
    signIDColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setSignId(event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });
    kioskIDColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setKioskId(event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    longNameColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setLongName(event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    shortNameColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setShortName(event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    arrowDirectionColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setArrowDirection(event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    dateColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setDate((java.sql.Timestamp) event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    endDateColumn.setOnEditCommit(
        event -> {
          Signage editedSignage = event.getRowValue();
          editedSignage.setEndDate((java.sql.Timestamp) event.getNewValue());
          try {
            syncSignage(editedSignage);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        });

    editSignageTable.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (event.getCode() == KeyCode.DELETE) {
            deleteSelectedSignageWithWarning();
          }
        });
    searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
    //    exportButton.setOnAction(
    //        event -> {
    //          FileChooser fileChooser = new FileChooser();
    //          fileChooser.setTitle("Save CSV File");
    //          fileChooser.setInitialFileName("signage.csv");
    //          fileChooser
    //              .getExtensionFilters()
    //              .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    //          File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
    //          if (file != null) {
    //            try {
    //              signageDAO.exportSignageToCSV(file.getAbsolutePath());
    //            } catch (SQLException | IOException e) {
    //              System.err.println("Error exporting data to CSV file: " + e.getMessage());
    //            }
    //          }
    //        });
    //    importButton.setOnAction(
    //        event -> {
    //          FileChooser fileChooser = new FileChooser();
    //          fileChooser.setTitle("Select CSV File");
    //          fileChooser
    //              .getExtensionFilters()
    //              .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    //          File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
    //          if (file != null) {
    //            try {
    //              signageDAO.uploadSignage(file.getAbsolutePath());
    //              // refresh table view
    //              signageDAO.getAllSignage().clear();
    //              editSignageTable.getItems().addAll(signageDAO.getAllSignage());
    //            } catch (SQLException e) {
    //              System.err.println("Error uploading data to database: " + e.getMessage());
    //            } catch (ParseException e) {
    //              throw new RuntimeException(e);
    //            }
    //          }
    //        });
    editSignageTable.setRowFactory(
        TableView -> {
          TableRow<Signage> row = new TableRow<>();

          ContextMenu ccontextMenu = new ContextMenu();
          MenuItem ddeleteMenuItem = new MenuItem("Delete");
          ddeleteMenuItem.setOnAction(event -> deleteSelectedSignageWithWarning());
          ccontextMenu.getItems().add(ddeleteMenuItem);

          row.contextMenuProperty()
              .bind(
                  Bindings.when(row.emptyProperty())
                      .then((ContextMenu) null)
                      .otherwise(ccontextMenu));

          return row;
        });

    editSignageTable
        .getColumns()
        .addAll(
            longNameColumn,
            shortNameColumn,
            dateColumn,
            endDateColumn,
            arrowDirectionColumn,
            signIDColumn,
            kioskIDColumn);
    loadData();
    directionPicker.getItems().addAll(Direction.getAll());
  }
  /**
   * This method loads all the data from the SignageDAOImpl and populates the editSignageTable with
   * it.
   */
  private void loadData() {
    SignageDAO signageDAO = new SignageDAOImpl();
    try {
      ObservableList<Signage> signageData = FXCollections.observableArrayList(signageDAO.getAll());
      editSignageTable.setItems(signageData);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  /**
   * This method filters the editSignageTable by searching for the searchText parameter in the
   * longName, shortName,
   *
   * <p>signId, and kioskId fields of each signage object.
   *
   * @param searchText A String representing the text to search for in the editSignageTable.
   */
  private void filterTable(String searchText) {
    SignageDAO signageDAO = new SignageDAOImpl();
    try {
      ObservableList<Signage> signageData = FXCollections.observableArrayList(signageDAO.getAll());

      if (searchText == null || searchText.isEmpty()) {
        editSignageTable.setItems(signageData);
      } else {
        ObservableList<Signage> filteredData = FXCollections.observableArrayList();

        for (Signage signage : signageData) {
          if (signage.getLongName().toLowerCase().contains(searchText.toLowerCase())
              || signage.getShortName().toLowerCase().contains(searchText.toLowerCase())
              || Integer.toString(signage.getSignId()).contains(searchText)
              || Integer.toString(signage.getKioskId()).contains(searchText)) {
            filteredData.add(signage);
          }
        }

        editSignageTable.setItems(filteredData);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  /**
   * This method handles the submit button for adding a new signage object to the SignageDAOImpl and
   * the editSignageTable.
   *
   * <p>It takes user input from various input fields and creates a new Signage object with that
   * data.
   *
   * @throws NumberFormatException If the signId or kioskId input field is not a valid integer.
   */
  public void handleSubmitButton() {
    Sound.playOnButtonClick();
    StringConverter<Direction> directionConverter =
        new StringConverter<Direction>() {
          @Override
          public String toString(Direction direction) {
            if (direction == null) {
              return null;
            }
            return direction.name();
          }

          @Override
          public Direction fromString(String string) {
            if (string == null || string.isEmpty()) {
              return null;
            }
            for (Direction direction : Direction.values()) {
              if (direction.getString().equalsIgnoreCase(string)) {
                return direction;
              }
            }
            return null;
          }
        };
    int signId = Integer.parseInt(signIDinput.getText());
    String longName = longNameInput.getText();
    String shortName = shortNameInput.getText();
    java.sql.Timestamp date = java.sql.Timestamp.valueOf(dateInput.getValue().atStartOfDay());
    java.sql.Timestamp endDate = java.sql.Timestamp.valueOf(endDateInput.getValue().atStartOfDay());
    Direction direction = directionConverter.fromString(directionPicker.getValue());
    int kioskId = Integer.parseInt(kioskInput.getText());

    Signage newSignage =
        new Signage(longName, shortName, date, endDate, direction, signId, kioskId);
    SignageDAO signageDAO = new SignageDAOImpl();

    try {
      signageDAO.add(newSignage);
      editSignageTable.getItems().add(newSignage);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Clear input fields
    signIDinput.clear();
    longNameInput.clear();
    shortNameInput.clear();
    dateInput.setValue(null);
    endDateInput.setValue(null);
    kioskInput.clear();
    directionPicker.setValue(null);
  }
  // Deletes the selected signage from the editSignageTable and the database after showing a warning
  private void deleteSelectedSignageWithWarning() {
    Signage selectedSignage = editSignageTable.getSelectionModel().getSelectedItem();
    if (selectedSignage != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Signage");
      alert.setHeaderText("Are you sure you want to delete this signage?");
      alert.setContentText(
          "Sign ID: "
              + selectedSignage.getSignId()
              + "\nLong Name: "
              + selectedSignage.getLongName()
              + "\nShort Name: "
              + selectedSignage.getShortName()
              + "\nArrow Direction: "
              + selectedSignage.getArrowDirection()
              + "\nDate: "
              + selectedSignage.getDate()
              + "\nEnd Date: "
              + selectedSignage.getEndDate()
              + "\nKiosk ID: "
              + selectedSignage.getKioskId());

      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
          DataManager signageDAO = new DataManager();
          signageDAO.deleteSignage(selectedSignage);
          editSignageTable.getItems().remove(selectedSignage);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
