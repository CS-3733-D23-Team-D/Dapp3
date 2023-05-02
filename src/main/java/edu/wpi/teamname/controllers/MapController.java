package edu.wpi.teamname.controllers;

import edu.wpi.teamname.*;
import edu.wpi.teamname.controllers.JFXitems.DirectionArrow;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.database.PathMessageDAOImpl;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.extras.Weather;
import edu.wpi.teamname.navigation.AlgoStrategy.AStarAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.BFSAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.DFSAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.DijkstraAlgo;
import edu.wpi.teamname.navigation.Direction;
import edu.wpi.teamname.navigation.Map;
import edu.wpi.teamname.navigation.Node;
import edu.wpi.teamname.navigation.PathMessage;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.*;
import net.kurobako.gesturefx.GesturePane;

public class MapController {

  static Map map;
  @FXML GesturePane gp;
  @FXML AnchorPane anchor;
  @FXML HBox SelectCombo;
  @FXML ComboBox<String> LocationOne;
  public static ComboBox<String> LocOne;
  @FXML ComboBox<String> EndPointSelect;
  public static ComboBox<String> endSel;
  @FXML MFXButton DeleteNodeButton; // On this page, this is actually the reset button
  @FXML MFXButton findPathButton;
  // @FXML ComboBox<String> FloorSelect = new ComboBox<>();
  @FXML ComboBox<String> AlgoSelect = new ComboBox<>();

  @FXML CheckBox LongNameSelector;
  @FXML CheckBox ShortNameSelector;
  @FXML CheckBox IdSelector;
  ArrayList<CheckBox> nameSelectBoxes = new ArrayList<>();
  @FXML CheckBox EdgeSelector;
  @FXML CheckBox HallNamesSelector;
  @FXML CheckBox NodeSelector;
  @FXML CheckBox LegendSelector;
  @FXML CheckBox AvoidElevatorsToggle;

  @FXML CheckBox FloorsToggle = new CheckBox();

  @FXML DatePicker datePickerUI;

  @FXML ComboBox<String> FloorSelect;

  @FXML MFXButton downFloor;
  @FXML MFXButton upFloor;
  @FXML MFXButton ViewMessageButton;
  @FXML MFXButton AddMessageButton;

  @FXML HBox floorSelector;

  @FXML AnchorPane OuterMapAnchor;
  @FXML TableView<PathMessage> MessageTableView;

  @FXML TableColumn<PathMessage, Timestamp> DateColumn;
  @FXML TableColumn<PathMessage, Integer> AdminColumn;
  @FXML TableColumn<PathMessage, String> MessageColumn;

  // Add Message Pop-Up
  @FXML TextField AdminIDVal;
  @FXML TextField MessageVal;
  @FXML Label AdminIDLabel;
  @FXML Label MessageLabel;
  @FXML VBox AddMessageVBox;
  @FXML MFXButton MessageSubmitButton;

  // New Floor Button Layout
  @FXML MFXButton ThirdFloorButton;
  @FXML MFXButton SecondFloorButton;
  @FXML MFXButton FirstFloorButton;
  @FXML MFXButton LowerFirstButton;
  @FXML MFXButton LowerSecondButton;

  // New UI Layout
  // @FXML Accordion MapAccordion;
  @FXML ScrollPane MapScrollPane;

  @FXML TitledPane PathfindingTitlePane;
  @FXML TitledPane DirectionsTitlePane;
  @FXML TitledPane FloorTitlePane;
  @FXML TitledPane TickTitlePane;
  @FXML TitledPane DateTitlePane;
  @FXML TitledPane MessageTitlePane;

  //  @FXML TitledPane DateTitledPane;

  String defaultFloor = "L1";
  int clickCount = 0;
  Point2D firstClick = null;
  Point2D secondClick = null;
  String floor1;
  String floor2;
  String currFloor = "Lower Level 1";
  int sNode = 0;
  int eNode = 0;
  Node globalStartNode;

  String currentAlgo = "";
  ArrayList<MFXButton> floorButtons = new ArrayList<>();
  @FXML VBox directionsBox;

  @FXML VBox Legend;

  EventHandler<MouseEvent> e =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          clickCount++;

          if (clickCount == 1) {

            // Capture the first click
            firstClick = new Point2D(event.getX(), event.getY());
            LocationOne.setOnAction(e -> {});

            floor1 = map.takeFloor(FloorSelect.getValue(), false);

          } else if (clickCount == 2) {
            // Capture the second click
            secondClick = new Point2D(event.getX(), event.getY());

            floor2 = map.takeFloor(FloorSelect.getValue(), false);

            List<Node> allNodes = map.graph.getNodes();

            //    System.out.println(firstClick);
            //    System.out.println(secondClick); // Coordinates in inner, now goes up to 5000

            int startIndex = -1;
            int endIndex = -1;
            double leastDistance;
            double nodeDist;

            Point2D currentClick;
            String currentFloor;
            int checkIndex;

            for (int j = 0; j < 2; j++) {
              if (j == 0) {
                // Start Node
                currentFloor = floor1;
                currentClick = firstClick;
              } else {
                // End Node
                currentFloor = floor2;
                currentClick = secondClick;
              }

              leastDistance = Double.MAX_VALUE;
              checkIndex = -1;

              for (int i = 0; i < allNodes.size(); i++) {
                if (i == startIndex) {
                  continue;
                } else {
                  Node currentNode = allNodes.get(i);
                  if (currentNode.getFloor().equals(currentFloor)) {
                    nodeDist = currentClick.distance(currentNode.getX(), currentNode.getY());
                    if (nodeDist < leastDistance) {
                      leastDistance = nodeDist;
                      checkIndex = i;
                    }
                  }
                }
              }

              if (j == 0) {
                // Start Node
                startIndex = checkIndex;
              } else {
                // End Node
                endIndex = checkIndex;
              }
            }

            //    Node startNode = allNodes.get(startIndex);
            //    Node endNode = allNodes.get(endIndex);
            System.out.println(startIndex + " " + endIndex);
            int startId = (startIndex * 5) + 100; // allNodes.get(startIndex).getId();
            int endId = (endIndex * 5) + 100; // allNodes.get(endIndex).getId();

            System.out.println("startId: " + startId);
            System.out.println("endId: " + endId);

            sNode = startId;
            eNode = endId;

            findPathButton.setVisible(true);
            //            try {
            //              LocationOne.setValue(DataManager.getAllLocationNamesMappedByNode( new
            // Timestamp(System.currentTimeMillis())).get(sNode).get(0).getLongName());
            //            } catch (SQLException ex) {
            //              throw new RuntimeException(ex);
            //            }

            LocationOne.setValue(GlobalVariables.getHMap().get(sNode).get(0).getLongName());

            //            try {
            //              EndPointSelect.setValue(
            //                  DataManager.getAllLocationNamesMappedByNode(
            //                          new Timestamp(System.currentTimeMillis()))
            //                      .get(eNode)
            //                      .get(0)
            //                      .getLongName());
            //            } catch (SQLException ex) {
            //              throw new RuntimeException(ex);
            //            }

            EndPointSelect.setValue(GlobalVariables.getHMap().get(eNode).get(0).getLongName());

            // Call drawAStarPath with both points
            // map.drawPath(anchor, firstClick, secondClick, floor1, floor2);
            //            String[] parts = nToPars.split("_");
            //            int sInd = Integer.parseInt(parts[0]);
            //            int eInd = Integer.parseInt(parts[1]);
            // listPaths = map.drawAStarPath(anchor, sInd, eInd);
            //            int secInd = map.getAllFloors().indexOf(FloorSelect.getValue());
            //            System.out.println(FloorSelect.getValue() + " " + secInd);
            //            anchor.getChildren().addAll(map.getShapes().get(secInd));

            // clickCount = 0;
          }
        }
      };

  EventHandler<MouseEvent> deleteNodeButton =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("DN");
          //          if (!map.getPrevPath().isEmpty()) {
          //            for (int i = anchor.getChildren().size() - 1; i >= 0; i--) {
          //              if (map.getPrevPath().contains(anchor.getChildren().get(i))) {
          //                anchor.getChildren().remove(i);
          //              }
          //            }
          //            map.setPrevPath(null);
          //          }
          try {
            initialize();
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        }
      };

  EventHandler<MouseEvent> submitMessage =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          System.out.println("Submit Button Pressed");

          //          String adminIDIn = AdminIDVal.getText();
          //          String messageIn = MessageVal.getText();
          //
          //          System.out.println("AdminId -- Message: " + adminIDIn + " -- " + messageIn);

          int currentSNode = sNode;
          int currentENode = eNode;
          String currentAlgo = AlgoSelect.getValue();
          Timestamp date = new Timestamp(System.currentTimeMillis());
          int adminID = Integer.parseInt(AdminIDVal.getText());
          String message = MessageVal.getText();
          System.out.println(
              "Adding Message: "
                  + currentENode
                  + ", "
                  + currentSNode
                  + ", "
                  + currentAlgo
                  + ", "
                  + date
                  + ", "
                  + adminID
                  + ", "
                  + message);
          PathMessage pm =
              new PathMessage(currentSNode, currentENode, currentAlgo, date, adminID, message);
          PathMessageDAOImpl pmdao = new PathMessageDAOImpl();
          try {
            pmdao.add(pm);
          } catch (SQLException ex) {
            System.out.println("Error adding: " + ex);
          }

          // Gets rid of the Pop-Up after submission
          MessageVal.setVisible(false);
          MessageSubmitButton.setVisible(false);
          MessageLabel.setVisible(false);
          AdminIDLabel.setVisible(false);
          AdminIDVal.setVisible(false);
          AddMessageVBox.setVisible(false);

          MessageVal.setText("");
          AdminIDVal.setText("");
          MessageVal.setPromptText("Type Message");
          AdminIDVal.setPromptText("Type Admin ID");
          AddMessageButton.setText("Add Message");
        }
      };

  EventHandler<MouseEvent> viewMessage =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          System.out.println("Viewing Message");

          if (MessageTableView.isVisible() == true) {
            MessageTableView.setVisible(false);
            ViewMessageButton.setText("View Messages");
          } else {

            MessageTableView.setVisible(true);
            MessageTableView.setDisable(false);
            int currentSNode = sNode;
            int currentENode = eNode;
            String currentAlgo = AlgoSelect.getValue();
            System.out.println(
                "Viewing Message: " + currentSNode + ", " + currentENode + ", " + currentAlgo);

            // displaying messages
            ObservableList<PathMessage> PMS = null;
            try {
              PMS =
                  FXCollections.observableList(
                      DataManager.getPathMessage(currentSNode, currentENode, currentAlgo).stream()
                          .filter(Objects::nonNull)
                          .toList());
            } catch (SQLException ex) {
              System.out.println("Error viewing: " + ex);
            }

            System.out.println(PMS.size());
            FilteredList<PathMessage> PMS1 = new FilteredList<>(PMS);
            SortedList<PathMessage> sortedPM = new SortedList<>(PMS1);
            MessageTableView.setItems(sortedPM);
            System.out.println(sortedPM);

            ViewMessageButton.setText("Cancel");
          }
        }
      };

  EventHandler<MouseEvent> addMessage =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          System.out.println("Add Message Running");

          if (MessageVal.isVisible() == true) {
            MessageVal.setVisible(false);
            MessageVal.setPromptText("Type Message");
            MessageLabel.setVisible(false);
            AdminIDVal.setVisible(false);
            AdminIDVal.setPromptText("Type AdminID");
            AdminIDLabel.setVisible(false);
            AddMessageVBox.setVisible(false);
            MessageSubmitButton.setVisible(false);
            AddMessageButton.setText("Add Message");
          } else {

            MessageVal.setVisible(true);
            MessageVal.setPromptText("Type Message");
            MessageLabel.setVisible(true);
            AdminIDVal.setVisible(true);
            AdminIDVal.setPromptText("Type AdminID");
            AdminIDLabel.setVisible(true);
            AddMessageVBox.setVisible(true);
            MessageSubmitButton.setVisible(true);
            AddMessageButton.setText("Cancel");

            //          String adminIDIn = AdminIDVal.getText();
            //          String messageIn = MessageVal.getText();
            //
            //          System.out.println("AdminId -- Message: " + adminIDIn + " -- " + messageIn);

          }
        }
      };

  public void clearTextDriections() {
    for (int i = directionsBox.getChildren().size() - 1; i >= 0; i--) {
      directionsBox.getChildren().remove(i);
    }
  }

  public void generateTextDirections(int startFloorIndex) {
    int floorIndex = startFloorIndex;
    //    ArrayList<String> directions = map.getTextDirections();
    ArrayList<Node> nodePath = map.getNodeDirections();
    int ind = 1;
    int changes = 0;
    int i = 1;
    String oldFloor = map.getFloorArr()[floorIndex];
    String newFloor = "";

    Direction direction;
    double distance;

    int directionNum = 1;

    int deltaFloor = 0;

    distance = 0;

    while (i != nodePath.size()) {
      //      System.out.println("IO: " + i + " DS: " + nodePath.size());

      StringBuilder sb = new StringBuilder();
      sb = new StringBuilder();

      ArrayList<DirectionArrow> directionObjs = new ArrayList<>();
      //      String textDirections = "";
      for (i = ind; i < nodePath.size(); i++) {
        //        System.out.println("II: " + i + " DS: " + nodePath.size());
        Node prevNode;
        Node node;
        Node nextNode;

        prevNode = nodePath.get(i - 1);
        node = nodePath.get(i);

        String textLine = "";
        int height = 35;

        if (i == nodePath.size() - 1) {
          distance += map.getNumericalDistance(prevNode, node);

          textLine += String.valueOf(directionNum) + ": ";
          textLine += String.format("In %.2f Units, you will reach your destination!\n", distance);

          //          sb.append(String.valueOf(directionNum) + ": ");
          //          sb.append(String.format("In %.2f Units, you will reach your destination\n",
          // distance));

          directionObjs.add(new DirectionArrow(Direction.END, textLine, height));

        } else {
          nextNode = nodePath.get(i + 1);

          direction = map.getDirection(prevNode, node, nextNode);
          distance += map.getNumericalDistance(prevNode, node);

          if (direction == Direction.STRAIGHT) {
            // Do Nothing
          } else {

            textLine += String.valueOf(directionNum) + ": ";
            //            sb.append(String.valueOf(directionNum) + ": ");

            if (i == ind && i != 1) {

              //              sb.append(String.format("Turn %s\n", direction.getString()));

              //              if (deltaFloor < 0) {
              //                System.out.println("INV");
              //              }
              //
              //              textLine += (String.format("Turn %s\n", direction.getString()));
              //              directionObjs.add(new DirectionArrow(direction, textLine, height));

              directionNum--;

            } else if (direction == Direction.UP || direction == Direction.DOWN) {
              deltaFloor = (int) map.getNumericalDistance(node, nextNode);
              floorIndex += deltaFloor;
              changes++;

              ind = i + 1;

              //              sb.append(String.format("In %.2f Units, Go up %d floors\n", distance,
              // deltaFloor));

              textLine +=
                  String.format(
                      "In %.2f Units, Go %s %d floors\n",
                      distance, direction.getString(), Math.abs(deltaFloor));
              directionObjs.add(new DirectionArrow(direction, textLine, height));
              distance = 0;
              directionNum++;
              break;
            } else {
              //              sb.append(String.format("In %.2f Units, Turn %s\n", distance,
              // direction.getString()));
              textLine +=
                  String.format("In %.2f Units, Turn %s\n", distance, direction.getString());
              directionObjs.add(new DirectionArrow(direction, textLine, height));
            }
            distance = 0;
            directionNum++;
          }
        }
      }

      final var resource = App.class.getResource("views/TitlePane.fxml");
      final FXMLLoader loader = new FXMLLoader(resource);
      TitledPane t = null;
      try {
        t = loader.load();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      //      Label l = (Label) ((AnchorPane) t.getContent()).getChildren().get(0);
      VBox v = (VBox) ((AnchorPane) t.getContent()).getChildren().get(0);

      v.setMinWidth(250);
      v.setMaxWidth(250);

      v.getChildren().addAll(directionObjs);

      newFloor = map.getFloorArr()[floorIndex];

      t.setText(oldFloor);
      oldFloor = newFloor;
      t.setExpanded(false);

      directionsBox.getChildren().add(t);
    }
    System.out.println("DOne!");
  }

  EventHandler<MouseEvent> findPathWButton =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          ViewMessageButton.setDisable(false);
          AddMessageButton.setDisable(false);
          map.drawPath(anchor, sNode, eNode);
          int secInd = map.getAllFloors().indexOf(currFloor);
          System.out.println("secInd: " + secInd);
          anchor.getChildren().addAll(map.getShapes().get(secInd));

          ArrayList<Node> allNodes;
          try {
            allNodes = DataManager.getAllNodes();
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }

          int floorIndex = -1;

          int indOfStart = Node.idToIndex(sNode);
          //          DataManager.getNode(sNode)
          String floorForSNode = map.takeFloor(allNodes.get(indOfStart).getFloor(), true);
          System.out.println("Floor to move to " + floorForSNode);
          if (floorForSNode == "Third Floor") {
            floorIndex = 4;
            ThirdFloorButton.fire();
          } else if (floorForSNode == "Second Floor") {
            floorIndex = 3;
            SecondFloorButton.fire();
          } else if (floorForSNode == "First Floor") {
            floorIndex = 2;
            // System.out.println("Got to First Floor");
            FirstFloorButton.fire();
          } else if (floorForSNode == "Lower Level 1") {
            floorIndex = 1;
            LowerFirstButton.fire();
          } else if (floorForSNode == "Lower Level 2") {
            floorIndex = 0;
            LowerSecondButton.fire();
          } else {
            System.out.println("Move to start node floor failed, should not be here");
          }

          FloorsToggle.setDisable(false);
          showPathFloors(false);

          clearTextDriections();
          generateTextDirections(floorIndex);

          map.centerAndZoomStart(gp, OuterMapAnchor, globalStartNode);

          clickCount = 0;
        }
      };

  EventHandler<ActionEvent> changeStart =
      new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
          Node nodeForStart;
          System.out.println("changed start " + LocationOne.getValue());
          // System.out.println(LocationOne.getValue());
          // System.out.println(EndPointSelect.getValue());
          String startLName = LocationOne.getValue();

          try {
            nodeForStart = DataManager.getNodeByLocationName(startLName, map.getCurrTime());
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }
          globalStartNode = nodeForStart;
          sNode = nodeForStart.getId(); // Integer.parseInt(LocationOne.getValue());
          // System.out.println("sNode: " + sNode);
          if (eNode != 0 && sNode != 0) {
            findPathButton.setDisable(
                false); //            map.drawAStarPath(anchor, floor1, floor2, sNode, eNode);
            // map.drawPath(anchor, sNode, eNode);
          }
        }
      };

  EventHandler<ActionEvent> changeEnd =
      new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
          Node nodeForEnd;

          System.out.println("changed end " + EndPointSelect.getValue());
          String endLName = EndPointSelect.getValue();
          try {
            nodeForEnd = DataManager.getNodeByLocationName(endLName, map.getCurrTime());
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }
          eNode = nodeForEnd.getId();
          //          System.out.println("eNode LName: " + endLName);
          //          System.out.println("eNode: " + eNode);
          if (sNode != 0 && eNode != 0) {
            findPathButton.setDisable(false);
            //            map.drawAStarPath(anchor, floor1, floor2, sNode, eNode);
          }
        }
      };

  EventHandler<ActionEvent> changeFloor =
      new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
          System.out.println("CF");

          //          if (!map.getPrevPath().isEmpty()) {
          //            for (int i = anchor.getChildren().size() - 1; i >= 0; i--) {
          //              if (map.getPrevPath().contains(anchor.getChildren().get(i))) {
          //                anchor.getChildren().remove(i);
          //              }
          //            }
          //            map.setPrevPath(null);
          //          }

          String floor = FloorSelect.getValue();

          System.out.println(floor);

          try {
            map.setCurrentDisplayFloor(floor);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          //          System.out.println(floor);
          //          currFloor = map.takeFloor(floor, true);

          //          if (!map.getShapes().isEmpty()) {
          //
          //            if (currFloor.equals("L1")) {
          //              //            for (int i = 0; i < map.getShapes().get(1).size(); i++) {
          //              //              System.out.print(" " + map.getShapes().get(1).get(i));
          //              //            }
          //
          //              anchor.getChildren().addAll(map.getShapes().get(1));
          //              map.setPrevPath(map.getShapes().get(1));
          //            } else if (currFloor.equals("L2") && clickCount == 0) {
          //              anchor.getChildren().addAll(map.getShapes().get(0));
          //              map.setPrevPath(map.getShapes().get(0));
          //            } else if (currFloor.equals("1") || currFloor.equals("G1")) {
          //              anchor.getChildren().addAll(map.getShapes().get(2));
          //              map.setPrevPath(map.getShapes().get(2));
          //            } else if (currFloor.equals("2") || currFloor.equals("G2")) {
          //              anchor.getChildren().addAll(map.getShapes().get(3));
          //              map.setPrevPath(map.getShapes().get(3));
          //            } else if (currFloor.equals("3") || currFloor.equals("G3")) {
          //              anchor.getChildren().addAll(map.getShapes().get(4));
          //              map.setPrevPath(map.getShapes().get(4));
          //            } else {
          //              System.out.println("What are you doing?");
          //            }
          //          }

          //          try {
          //            LocationOne.setItems(map.getAllNodeNames(map.takeFloor(floor, false)));
          //          } catch (SQLException ex) {
          //            throw new RuntimeException(ex);
          //          }
          //          try {
          //            EndPointSelect.setItems(map.getAllNodeNames(map.takeFloor(floor, false)));
          //          } catch (SQLException ex) {
          //            throw new RuntimeException(ex);
          //          }

          //          anchor.getStyleClass().remove(0);
          //          anchor.getStyleClass().add(map.takeFloor(floor, true));

          //                           System.out.println(LocationOne.getValue());

        }
      };

  private void showPathFloors(boolean flag) {
    if (flag) {
      ThirdFloorButton.setDisable(false);
      SecondFloorButton.setDisable(false);
      FirstFloorButton.setDisable(false);
      LowerFirstButton.setDisable(false);
      LowerSecondButton.setDisable(false);

      // FloorSelect.setItems(map.getAllFloorsInPath());
    } else {
      ThirdFloorButton.setDisable(true);
      SecondFloorButton.setDisable(true);
      FirstFloorButton.setDisable(true);
      LowerFirstButton.setDisable(true);
      LowerSecondButton.setDisable(true);

      // FloorSelect.setItems(map.getAllFloors());
      for (String s : map.getAllFloorsInPath()) {
        if (s == "G3") {
          ThirdFloorButton.setDisable(false);
        } else if (s == "G2") {
          SecondFloorButton.setDisable(false);
        } else if (s == "G1") {
          FirstFloorButton.setDisable(false);
        } else if (s == "L1") {
          LowerFirstButton.setDisable(false);
        } else if (s == "L2") {
          LowerSecondButton.setDisable(false);
        } else {
          System.out.println("showPathFloors should not get here");
        }
      }
    }
  }

  EventHandler<ActionEvent> selectAlgo =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Algo Changed");
          currentAlgo = AlgoSelect.getValue();

          AvoidElevatorsToggle.setDisable(true);
          switch (AlgoSelect.getValue()) {
            case ("A-Star"):
              map.graph.setPathfindingAlgo(new AStarAlgo());
              AvoidElevatorsToggle.setDisable(false);
              break;
            case ("Breadth First Search"):
              map.graph.setPathfindingAlgo(new BFSAlgo());
              break;
            case ("Depth First Search"):
              map.graph.setPathfindingAlgo(new DFSAlgo());
              break;
            case ("Dijkstra's Algorithm"):
              map.graph.setPathfindingAlgo(new DijkstraAlgo());
              break;
            default:
              System.out.println("Not supposed to be here: Wrong Algo");
          }
        }
      };

  EventHandler<MouseEvent> toggleFloorMethod =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          System.out.println("This is the toggle");

          //          double parentW = map.getMapWitdh(OuterMapAnchor);
          //          double parentH = map.getMapHeight(OuterMapAnchor);
          //
          //          System.out.println(
          //              "Check"
          //                  + parentW
          //                  + ", "
          //                  + parentH
          //                  + " :"
          //                  + gp.getCurrentScale()
          //                  + ", "
          //                  + gp.getCurrentX()
          //                  + ", "
          //                  + gp.getCurrentY());

          // -1627.2856715232545, -690.8681650647059

          if (FloorsToggle.isSelected()) {
            showPathFloors(true);
          } else {
            showPathFloors(false);
          }
        }
      };

  public void changeFloor() {
    System.out.println("CF");
    String floor = FloorSelect.getValue();
    System.out.println(floor);

    try {
      map.setCurrentDisplayFloor(floor);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  EventHandler<ActionEvent> triggerChangeFloor =
      new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {

          changeFloor();
          // Update Nodes
        }
      };

  EventHandler<MouseEvent> changeFloorUp =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          System.out.println("CFU");

          ObservableList<String> floors = map.getAllFloors();
          int currFlorIndex = floors.indexOf(map.getCurrentDisplayFloor());
          String newFloor = floors.get((currFlorIndex + 1) % floors.size());

          try {
            map.setCurrentDisplayFloor(newFloor);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> changeFloorDown =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          System.out.println("CFU");

          ObservableList<String> floors = map.getAllFloors();
          int currFlorIndex = floors.indexOf(map.getCurrentDisplayFloor());
          String newFloor = floors.get((currFlorIndex - 1) % floors.size());

          try {
            map.setCurrentDisplayFloor(newFloor);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> checkPoints =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          System.out.println("M");
        }
      };

  EventHandler<ActionEvent> setThirdFloor =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Switching to 3rd");
          currFloor = "Third Floor";
          setAllButtons();
          ThirdFloorButton.setStyle("-fx-background-color: yellow;");

          try {
            map.setCurrentDisplayFloor("Third Floor");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<ActionEvent> setSecondFloor =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Switching to 2nd");
          currFloor = "Second Floor";
          setAllButtons();
          SecondFloorButton.setStyle("-fx-background-color: yellow;");

          try {
            map.setCurrentDisplayFloor("Second Floor");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<ActionEvent> setFirstFloor =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Switching to 1st");
          currFloor = "First Floor";
          setAllButtons();
          FirstFloorButton.setStyle("-fx-background-color: yellow;");

          try {
            map.setCurrentDisplayFloor("First Floor");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }

        //        public void handleNoClick() {
        //          System.out.println("Switching to 1st no Mouse Clicked");
        //          currFloor = "First Floor";
        //
        //          try {
        //            map.setCurrentDisplayFloor("First Floor", true);
        //          } catch (SQLException e) {
        //            throw new RuntimeException(e);
        //          } catch (IOException e) {
        //            throw new RuntimeException(e);
        //          }
        //        }
      };

  EventHandler<ActionEvent> setLowerFirst =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Switching to L1");
          currFloor = "Lower Level 1";
          setAllButtons();
          LowerFirstButton.setStyle("-fx-background-color: yellow;");

          try {
            map.setCurrentDisplayFloor("Lower Level 1");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<ActionEvent> setLowerSecond =
      new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          System.out.println("Switching to L2");
          currFloor = "Lower Level 2";
          setAllButtons();
          LowerSecondButton.setStyle("-fx-background-color: yellow;");

          try {
            map.setCurrentDisplayFloor("Lower Level 2");
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<ActionEvent> changeFloors =
      new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
          Sound.playOnButtonClick();

          MFXButton newButton = ((MFXButton) event.getSource());

          String oldFloor = map.getCurrentDisplayFloor();
          //          System.out.println("OF: " + oldFloor);

          for (MFXButton floorButton : floorButtons) {
            //            System.out.println("F: " + floorButton.getId());
            if (floorButton.getId().equals(oldFloor)) {
              //              System.out.println("Old");
              floorButton.getStyleClass().remove("primary");
              floorButton.getStyleClass().add("primary-container");
            }
          }

          // re-color new button
          newButton.getStyleClass().remove("primary-container");
          newButton.getStyleClass().add("primary");

          try {
            map.setCurrentDisplayFloor(newButton.getId());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  public void setAllButtons() {
    ThirdFloorButton.setStyle("-fx-background-color: blue;");
    SecondFloorButton.setStyle("-fx-background-color: blue;");
    FirstFloorButton.setStyle("-fx-background-color: blue;");
    LowerFirstButton.setStyle("-fx-background-color: blue;");
    LowerSecondButton.setStyle("-fx-background-color: blue;");
  }

  EventHandler<MouseEvent> changeLabels =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          CheckBox newCheck = ((CheckBox) event.getSource());

          int oldLabel = map.getLabelTextType();

          if (oldLabel != -1) {
            CheckBox oldCheck = nameSelectBoxes.get(oldLabel);
            oldCheck.setSelected(false);
          }

          int newLabel = Integer.parseInt(newCheck.getId());
          if (newLabel == oldLabel) {
            newLabel = -1;
          }

          map.setLabelTextType(newLabel);

          try {
            map.setCurrentDisplayFloor(map.getCurrentDisplayFloor());
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> toggleEdges =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          map.setShowEdges(EdgeSelector.isSelected());
          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> toggleNodes =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          //        map.setShowEdges(EdgeSelector.isSelected());
          map.setShowNodes(NodeSelector.isSelected());

          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> toggleLegend =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          //        map.setShowEdges(EdgeSelector.isSelected());
          map.setShowLegend(LegendSelector.isSelected()); // && NodeSelector.isSelected());
          Legend.setVisible(map.getShowLegend());

          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> toggleHalls =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          map.setShowTypeLabels(new boolean[] {HallNamesSelector.isSelected()});

          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  EventHandler<MouseEvent> toggleUseElevators =
      new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
          Sound.playOnButtonClick();
          AStarAlgo.setNoStairs(AvoidElevatorsToggle.isSelected());
        }
      };

  public static void updateNames() {
    try {
      LocOne.setItems(new SimpleListProperty<>());
      endSel.setItems(new SimpleListProperty<>());
      LocOne.setItems(map.getAllNodeNames());
      endSel.setItems(map.getAllNodeNames());
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @FXML
  public void initialize() throws SQLException, IOException {
    ThemeSwitch.switchTheme(OuterMapAnchor);

    map = new Map(anchor, true);

    //    AnchorPane.setLeftAnchor(SelectCombo, 0.0);
    //    AnchorPane.setRightAnchor(SelectCombo, 0.0);
    //    AnchorPane.setTopAnchor(SelectCombo, 100.0);

    //    anchor.getChildren().add(SelectCombo);
    //    AnchorPane.setTopAnchor(anchor, 0.0);
    //    AnchorPane.setBottomAnchor(anchor, 1000.0);

    gp.setMinScale(0.11);

    //    double parentW = map.getMapWitdh(OuterMapAnchor);
    //    double parentH = map.getMapHeight(OuterMapAnchor);
    //
    //    System.out.println("" + parentW + ", " + parentH);

    //    gp.setOnMouseMoved(checkPoints);

    //    anchor.on

    ArrayList<javafx.scene.Node> currentFloorNodes = (map.makeAllFloorShapes(defaultFloor));
    anchor.getChildren().addAll(currentFloorNodes);
    map.setCurrentFloorShapes(currentFloorNodes);
    //  anchor.getChildren().addAll(map.makeAllFloorNodes(defaultFloor, true));

    Platform.runLater(() -> map.centerAndZoom(gp, OuterMapAnchor));

    // DeleteNodeButton.setOnMouseClicked(deleteNodeButton);
    DeleteNodeButton.setOnMouseClicked(event -> Navigation.navigate(Screen.MAP));
    //    DeleteNodeButton.setOnMouseClicked(
    //        event -> {
    //          try {
    //            map.refresh();
    //          } catch (SQLException ex) {
    //            throw new RuntimeException(ex);
    //          } catch (IOException ex) {
    //            throw new RuntimeException(ex);
    //          }
    //        });

    findPathButton.setOnMouseClicked(findPathWButton);
    findPathButton.setDisable(true);

    //    LocationOne.setStyle("-fx-padding: 5 25 5 5;");
    LocationOne.setPromptText("Select start");
    LocationOne.setItems(
        map.getAllNodeNames()); // change for when the floor changes to update the nodes shown
    LocationOne.setOnAction(changeStart);

    EndPointSelect.setPromptText("Select end");
    EndPointSelect.setItems(map.getAllNodeNames()); // switched to every node in map
    EndPointSelect.setOnAction(changeEnd);

    //    FloorSelect.setPromptText("Select floor");
    //    FloorSelect.setItems(map.getAllFloors());
    //    FloorSelect.setOnAction(changeFloor);
    //    FloorSelect.setValue("Lower Level 1");

    //    FloorSelect.setPromptText("Select floor");
    //    FloorSelect.setItems(map.getAllFloors());
    //    FloorSelect.setOnAction(changeFloor);
    //    FloorSelect.setValue("Lower Level 1");

    //    upFloor.setOnMouseClicked(changeFloorUp);
    //    downFloor.setOnMouseClicked(changeFloorDown);

    AlgoSelect.setPromptText("Select Algorithm");
    AlgoSelect.setItems(map.getAllAlgos());
    AlgoSelect.setOnAction(selectAlgo);
    AlgoSelect.setValue("A-Star");

    FloorsToggle.setOnMouseClicked(toggleFloorMethod);
    FloorsToggle.setSelected(false);
    FloorsToggle.setDisable(true);

    ViewMessageButton.setOnMouseClicked(viewMessage);
    ViewMessageButton.setDisable(true);

    AddMessageButton.setOnMouseClicked(addMessage);
    AddMessageButton.setDisable(true);

    MessageTableView.setVisible(false);

    // Initialize for AddMessage Pop-Up
    AdminIDVal.setVisible(false);
    MessageVal.setVisible(false);
    AdminIDLabel.setVisible(false);
    MessageLabel.setVisible(false);
    AddMessageVBox.setVisible(false);
    MessageSubmitButton.setVisible(false);
    MessageSubmitButton.setOnMouseClicked(submitMessage);

    //    anchor.setOnMouseClicked(e);

    // New Floor Button Layout
    //    ThirdFloorButton.setOnAction(setThirdFloor);
    //    SecondFloorButton.setOnAction(setSecondFloor);
    //    FirstFloorButton.setOnAction(setFirstFloor);
    //    LowerFirstButton.setOnAction(setLowerFirst);
    //    LowerSecondButton.setOnAction(setLowerSecond);

    datePickerUI
        .valueProperty()
        .addListener(
            (ov, oldValue, newValue) -> {
              LocalDate localDate = datePickerUI.getValue();
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
              System.out.println("Print date");
              LocalDateTime dateTime = localDate.atStartOfDay();
              Timestamp date = Timestamp.valueOf(dateTime);
              map.setCurrTime(date);
              try {
                map.refresh(date);
              } catch (SQLException ex) {
                throw new RuntimeException(ex);
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            });

    // New Floor Stuff
    floorButtons.add(ThirdFloorButton);
    floorButtons.add(SecondFloorButton);
    floorButtons.add(FirstFloorButton);
    floorButtons.add(LowerFirstButton);
    floorButtons.add(LowerSecondButton);

    for (MFXButton floorButton : floorButtons) {
      floorButton.setOnAction(changeFloors);
    }

    nameSelectBoxes.add(LongNameSelector);
    nameSelectBoxes.add(ShortNameSelector);
    nameSelectBoxes.add(IdSelector);

    for (CheckBox selectBox : nameSelectBoxes) {
      selectBox.setOnMouseClicked(changeLabels);
    }

    EdgeSelector.setOnMouseClicked(toggleEdges);
    HallNamesSelector.setOnMouseClicked(toggleHalls);

    NodeSelector.setOnMouseClicked(toggleNodes);
    LegendSelector.setOnMouseClicked(toggleLegend);

    AvoidElevatorsToggle.setOnMouseClicked(toggleUseElevators);

    // MapAccordion.setExpandedPane(PathfindingTitlePane); // set initial expanded pane
    DirectionsTitlePane.setExpanded(false);
    FloorTitlePane.setExpanded(false);
    TickTitlePane.setExpanded(false);
    DateTitlePane.setExpanded(false);
    MessageTitlePane.setExpanded(false);

    DateColumn.setCellValueFactory((row) -> new SimpleObjectProperty<>(row.getValue().getDate()));
    AdminColumn.setCellValueFactory(
        (row) -> new SimpleObjectProperty<>(row.getValue().getAdminID()));
    MessageColumn.setCellValueFactory(
        (row) -> new SimpleObjectProperty<>(row.getValue().getMessage()));

    //    System.out.println(getAllNodeNames("L1"));

    ParentController.titleString.set("Map");
    ParentController.timeString.set(Weather.getTime());
    ParentController.dateString.set(Weather.getDate());
    ParentController.tempString.set(Weather.getTemperature());
    ParentController.weatherString.set(Weather.getDescription());
    ParentController.userString.set(GlobalVariables.getCurrentUser().getUsername());

    endSel = EndPointSelect;
    LocOne = LocationOne;
  }
}
