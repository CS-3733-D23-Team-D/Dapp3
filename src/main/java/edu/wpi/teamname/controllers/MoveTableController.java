package edu.wpi.teamname.controllers;

import edu.wpi.teamname.App;
import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.controllers.helpers.DatePickerEditingCell;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.database.MoveDAOImpl;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.navigation.Move;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

/**
 * This class represents a controller for the Move Table GUI, which allows users to view, add, edit,
 * and delete move data. It initializes the GUI and sets up event handlers for various GUI
 * components.
 */
public class MoveTableController {
  @FXML private TableView<Move> moveTable;
  @FXML private Button importButton;

  @FXML private Button exportButton;
  @FXML private TextField nodeIdTextField;
  @FXML private TextField longNameTextField;
  @FXML private DatePicker datePicker;
  @FXML private MFXButton submitButton;
  @FXML private TextField searchTextField;
  @FXML private CheckBox newMovesCheck;
  @FXML private VBox adminMoveView;

  /** Initializes the GUI and sets up event handlers for various GUI components. */
  public void initialize() {
    DataManager moveDAO = new DataManager();

    // Implement to disable buttons for staff

    // if (GlobalVariables.userIsType(EmployeeType.STAFF)) {
    //      adminMoveView.setVisible(false);
    //      // EXTEND TABLEVIEW SOMEHOW
    //    }

    ParentController.titleString.set("Move Editor");
    TableColumn<Move, Integer> nodeIDColumn = new TableColumn<>("Node ID");
    nodeIDColumn.setCellValueFactory(new PropertyValueFactory<>("nodeID"));

    TableColumn<Move, String> longNameColumn = new TableColumn<>("Long Name");
    longNameColumn.setCellValueFactory(new PropertyValueFactory<>("longName"));

    TableColumn<Move, Timestamp> dateColumn = new TableColumn<>("Date");
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    dateColumn.setCellFactory(tc -> new DatePickerEditingCell());

    moveTable.getColumns().addAll(nodeIDColumn, longNameColumn, dateColumn);
    moveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    dateColumn
        .prefWidthProperty()
        .bind(
            moveTable
                .widthProperty()
                .subtract(nodeIDColumn.widthProperty())
                .subtract(longNameColumn.widthProperty()));
    searchTextField
        .textProperty()
        .addListener((observable, oldValue, newValue) -> filterTable(newValue));

    try {
      ArrayList<Move> moves = moveDAO.getAllMoves();
      moveTable.setItems(FXCollections.observableArrayList(moves));
    } catch (SQLException e) {
      System.err.println("Error getting moves from database: " + e.getMessage());
    }
    importButton.setOnAction(
        event -> {
          Sound.playOnButtonClick();
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Select CSV File");
          fileChooser
              .getExtensionFilters()
              .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
          File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
          if (file != null) {
            try {
              MoveDAOImpl.uploadMoveToPostgreSQL(file.getAbsolutePath());
              // refresh table view
              moveTable.getItems().clear();
              moveTable.getItems().addAll(moveDAO.getAllMoves());
            } catch (SQLException e) {
              System.err.println("Error uploading data to database: " + e.getMessage());
            }
          }
        });

    // event handler for export button
    exportButton.setOnAction(
        event -> {
          Sound.playOnButtonClick();
          FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Save CSV File");
          fileChooser.setInitialFileName("moves.csv");
          fileChooser
              .getExtensionFilters()
              .addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
          File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
          if (file != null) {
            try {
              MoveDAOImpl.exportMoveToCSV(file.getAbsolutePath());
            } catch (SQLException e) {
              System.err.println("Error exporting data to CSV file: " + e.getMessage());
            }
          }
        });
    newMovesCheck.setOnAction(
        event -> {
          Sound.playOnButtonClick();
          if (newMovesCheck.isSelected()) {
            ObservableList<Move> allMoves = moveTable.getItems();
            ObservableList<Move> filteredMoves = FXCollections.observableArrayList();

            LocalDate today = LocalDate.now();
            for (Move move : allMoves) {
              if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
                  || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
                filteredMoves.add(move);
              }
            }

            moveTable.setItems(filteredMoves);
          } else {
            try {
              ArrayList<Move> moves = moveDAO.getAllMoves();
              moveTable.setItems(FXCollections.observableArrayList(moves));
            } catch (SQLException e) {
              System.err.println("Error getting moves from database: " + e.getMessage());
            }
          }
        });
    setupRowFactory();
    submitButton.setOnAction(
        event -> {
          Sound.playOnButtonClick();
          int nodeId = Integer.parseInt(nodeIdTextField.getText());
          String longName = longNameTextField.getText();
          LocalDate localDate = datePicker.getValue();
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          LocalDateTime dateTime = localDate.atStartOfDay();
          Timestamp date = Timestamp.valueOf(dateTime);
          Move move = new Move(nodeId, longName, date);
          try {
            moveDAO.addMoves(move);
            // Add the new move to the table view
            moveTable.getItems().add(move);
            // Clear the input fields after adding the move
            nodeIdTextField.clear();
            longNameTextField.clear();
            datePicker.setValue(null);
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
    if (GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN)) {
      moveTable.setEditable(true);
    }

    nodeIDColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    nodeIDColumn.setOnEditCommit(
        (CellEditEvent<Move, Integer> t) -> {
          Move move = t.getTableView().getItems().get(t.getTablePosition().getRow());
          move.setNodeID(t.getNewValue());
          try {
            moveDAO.syncMove(move);
          } catch (SQLException e) {
            System.err.println("Error updating node ID: " + e.getMessage());
          }
        });

    longNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    longNameColumn.setOnEditCommit(
        (CellEditEvent<Move, String> t) -> {
          Move move = t.getTableView().getItems().get(t.getTablePosition().getRow());
          move.setLongName(t.getNewValue());
          try {
            moveDAO.syncMove(move);
          } catch (SQLException e) {
            System.err.println("Error updating long name: " + e.getMessage());
          }
        });
    dateColumn.setOnEditCommit(
        (CellEditEvent<Move, Timestamp> t) -> {
          Move move = t.getTableView().getItems().get(t.getTablePosition().getRow());
          move.setDate(t.getNewValue());
          try {
            moveDAO.syncMove(move);
          } catch (SQLException e) {
            System.err.println("Error updating date: " + e.getMessage());
          }
        });
    moveTable.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.DELETE) {
            Move selectedMove = moveTable.getSelectionModel().getSelectedItem();
            if (selectedMove != null) {
              Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
              alert.initOwner(App.getPrimaryStage());
              alert.setTitle("Delete Move");
              alert.setHeaderText("Are you sure you want to delete this move?");
              alert.setContentText(
                  "Node ID: "
                      + selectedMove.getNodeID()
                      + "\nLong Name: "
                      + selectedMove.getLongName()
                      + "\nDate: "
                      + selectedMove.getDate().toString());

              Optional<ButtonType> result = alert.showAndWait();
              if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                  moveDAO.deleteMove(selectedMove);
                  moveTable.getItems().remove(selectedMove);
                } catch (SQLException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        });
    if (GlobalVariables.isFutureMovesPressed()) {
      newMovesCheck.setSelected(true);
      if (newMovesCheck.isSelected()) {
        ObservableList<Move> allMoves = moveTable.getItems();
        ObservableList<Move> filteredMoves = FXCollections.observableArrayList();

        LocalDate today = LocalDate.now();
        for (Move move : allMoves) {
          if (move.getDate().toLocalDateTime().toLocalDate().isAfter(today)
              || move.getDate().toLocalDateTime().toLocalDate().isEqual(today)) {
            filteredMoves.add(move);
          }
        }

        moveTable.setItems(filteredMoves);
      } else {
        try {
          ArrayList<Move> moves = moveDAO.getAllMoves();
          moveTable.setItems(FXCollections.observableArrayList(moves));
        } catch (SQLException e) {
          System.err.println("Error getting moves from database: " + e.getMessage());
        }
      }
    }
    if (!(GlobalVariables.userIsClearanceLevel(ClearanceLevel.ADMIN))) {
      nodeIdTextField.setDisable(true);
      longNameTextField.setDisable(true);
      submitButton.setDisable(true);
      datePicker.setDisable(true);
      importButton.setDisable(true);
      exportButton.setDisable(true);
    }
    submitButton.disableProperty().bind(Bindings.isEmpty(nodeIdTextField.textProperty()));
    submitButton.disableProperty().bind(Bindings.isEmpty(longNameTextField.textProperty()));
    submitButton.disableProperty().bind(Bindings.isNull(datePicker.valueProperty()));
  }

  /**
   * Filters the moveTable based on a search string, showing only moves that match the search
   * criteria.
   *
   * <p>If the search string is empty or null, shows all moves in the table.
   *
   * @param searchText the string to search for in the table
   */
  private void filterTable(String searchText) {
    DataManager moveDAO = new DataManager();
    if (searchText == null || searchText.isEmpty()) {
      // Show all moves when search text is empty
      try {
        ArrayList<Move> moves = moveDAO.getAllMoves();
        moveTable.setItems(FXCollections.observableArrayList(moves));
      } catch (SQLException e) {
        System.err.println("Error getting moves from database: " + e.getMessage());
      }
    } else {
      ObservableList<Move> allMoves = moveTable.getItems();
      ObservableList<Move> filteredMoves = FXCollections.observableArrayList();

      for (Move move : allMoves) {
        if (String.valueOf(move.getNodeID()).contains(searchText)
            || move.getLongName().toLowerCase().contains(searchText.toLowerCase())) {
          filteredMoves.add(move);
        }
      }

      moveTable.setItems(filteredMoves);
    }
  }

  /** Sets up the row factory for the moveTable to enable context menu and delete functionality. */
  private void setupRowFactory() {
    moveTable.setRowFactory(
        tableView -> {
          TableRow<Move> row = new TableRow<>();
          ContextMenu contextMenu = new ContextMenu();
          MenuItem deleteMenuItem = new MenuItem("Delete");
          deleteMenuItem.setOnAction(
              event -> {
                Move move = row.getItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(App.getPrimaryStage());
                alert.setTitle("Delete Move");
                alert.setHeaderText("Are you sure you want to delete this move?");
                alert.setContentText(
                    "Node ID: "
                        + move.getNodeID()
                        + "\nLong Name: "
                        + move.getLongName()
                        + "\nDate: "
                        + move.getDate().toString());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                  try {
                    DataManager moveDAO = new DataManager();
                    moveDAO.deleteMove(move);
                    moveTable.getItems().remove(move);
                  } catch (SQLException e) {
                    e.printStackTrace();
                  }
                }
              });
          contextMenu.getItems().add(deleteMenuItem);

          row.contextMenuProperty()
              .bind(
                  Bindings.when(row.emptyProperty())
                      .then((ContextMenu) null)
                      .otherwise(contextMenu));

          return row;
        });
  }
}
