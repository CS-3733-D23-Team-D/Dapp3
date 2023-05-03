package edu.wpi.teamname.navigation;

import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.controllers.MapController;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.RoomManager;
import edu.wpi.teamname.servicerequest.requestitem.ConfRoom;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.shape.Shape;
import lombok.Getter;
import lombok.Setter;
import net.kurobako.gesturefx.GesturePane;

public class Map {
  public Graph graph;
  public ArrayList<Emergency> emergencies;
  private Point2D centerPoint;
  private Point2D centerTL;

  @Getter @Setter private int labelTextType; // 0 = Long Name, 1 = Short Name, 2 = ID

  @Getter @Setter private ArrayList<Shape> prevPath = new ArrayList<Shape>();

  @Getter @Setter private ArrayList<ArrayList<Shape>> shapes = new ArrayList<ArrayList<Shape>>();

  @Getter @Setter
  private ArrayList<javafx.scene.Node> currentFloorShapes = new ArrayList<javafx.scene.Node>();

  @Getter private String currentDisplayFloor;
  private AnchorPane subAnchor;

  /** An array of strings that represent the names of different floors. */
  @Getter
  private String[] floorArr = {
    "Lower Level 2", "Lower Level 1", "First Floor", "Second Floor", "Third Floor"
  };

  private String[] algoArr = {
    "A-Star", "Breadth First Search", "Depth First Search", "Dijkstra's Algorithm"
  };

  private boolean isMapPage;
  @Getter @Setter private boolean showEdges;
  @Getter @Setter private boolean showNodes;
  @Setter private boolean showLegend;

  @Getter private ArrayList<String> roomTypes = new ArrayList<>();
  @Getter @Setter private boolean[] showTypeLabels = {false}; // HALL

  //  @Getter @Setter private ArrayList<Node> alignSelection = new ArrayList<>();
  @Getter @Setter private ArrayList<Integer> alignSelection = new ArrayList<>();

  @Getter @Setter private int startEdgeNodeId;
  @Getter @Setter private int movingNodeId;

  @Getter @Setter private ArrayList<String> textDirections;
  @Getter @Setter private ArrayList<Node> nodeDirections;

  @Getter @Setter private ArrayList<Integer> floorChanges;

  @Getter @Setter private String startFloor;
  @Getter @Setter private String endFloor;

  @Getter @Setter private Timestamp currTime;

  @Getter @Setter private RoomManager rm;

  /**
   * Constructs a Map object with the given sub-anchor pane.
   *
   * @param subAnchor the sub-anchor pane used to display the map.
   * @throws SQLException if there is an error accessing the database.
   */
  public Map(AnchorPane subAnchor, boolean isMapPage) throws SQLException {
    this.graph = new Graph();
    this.currentDisplayFloor = "Lower Level 1";
    this.subAnchor = subAnchor;

    setGlobalVars(new Timestamp(System.currentTimeMillis()));

    this.labelTextType = 1;
    this.isMapPage = isMapPage;
    this.showEdges = !this.isMapPage;
    this.roomTypes.add("HALL");
    this.startEdgeNodeId = -1;
    this.movingNodeId = -1;

    this.showNodes = !this.isMapPage;
    //    this.showLegend = !this.isMapPage;
    this.showLegend = true;
    this.currTime = new Timestamp(System.currentTimeMillis());

    this.rm = new RoomManager();
    //    try {
    //      this.refresh();
    //    } catch (IOException e) {
    //      throw new RuntimeException(e);
    //    }
  }

  public void setGlobalVars(Timestamp time) throws SQLException {
    GlobalVariables.setHMap(DataManager.getAllLocationNamesMappedByNode(time));
    GlobalVariables.setServiceRequests(DataManager.getAllServiceRequests());
    GlobalVariables.setConfReservations(DataManager.getAllConfReservation());
    GlobalVariables.setConfRooms(DataManager.getAllConfRoom());

    ArrayList<ArrayList<ConfReservation>> reses = new ArrayList<>();
    ArrayList<ConfRoom> confs = GlobalVariables.getConfRooms();
    for (int i = 0; i < confs.size(); i++) {
      reses.add(DataManager.getResForRoom(confs.get(i)));
    }
    GlobalVariables.setAllRes(reses);
  }

  public boolean getShowLegend() {
    return this.showLegend;
  }

  /**
   * Creates and returns an ArrayList of all nodes and edges on the given floor.
   *
   * @param floor the floor to retrieve nodes and edges from.
   * @return an ArrayList of all nodes and edges on the given floor as JavaFX Node objects.
   * @throws SQLException if there is an error accessing the database.
   * @throws IOException if there is an error reading a file.
   */
  public ArrayList<javafx.scene.Node> makeAllFloorShapes(String floor)
      throws SQLException, IOException {
    ArrayList<javafx.scene.Node> allCirclesAndEdges = new ArrayList<>();

    //    System.out.println("E: " + this.showEdges + ", " + this.showNodes);

    if (this.showEdges) {
      //      System.out.println("ADDEDGES");
      allCirclesAndEdges.addAll(this.makeAllFloorEdges(floor));
    }
    if (this.showNodes) {
      allCirclesAndEdges.addAll(this.makeAllFloorNodes(floor));
    }
    return allCirclesAndEdges;
  }

  /**
   * Creates and returns an ArrayList of all edges on the given floor.
   *
   * @param floor the floor to retrieve edges from.
   * @return an ArrayList of all edges on the given floor as JavaFX Node objects.
   */
  public ArrayList<javafx.scene.Node> makeAllFloorEdges(String floor) {

    ArrayList<javafx.scene.Node> allEdges = new ArrayList<>();

    ArrayList<Edge> mapEdges;
    mapEdges = this.graph.getEdges();

    try {
      mapEdges = DataManager.getAllEdges();
      System.out.println("Success Edges");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    ArrayList<Node> AllNodes;
    try {
      AllNodes = DataManager.getAllNodes();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    for (int i = 0; i < mapEdges.size(); i++) {
      //            Node StartNode = null;
      //            Node EndNode = null;
      //            try {
      //              StartNode = DataManager.getNode(mapEdges.get(i).getStartNodeID());
      //              EndNode = DataManager.getNode(mapEdges.get(i).getEndNodeID());
      //            } catch (SQLException e) {
      //              throw new RuntimeException(e);
      //            }

      Node StartNode = AllNodes.get(Node.idToIndex(mapEdges.get(i).getStartNodeID()));
      Node EndNode = AllNodes.get(Node.idToIndex(mapEdges.get(i).getEndNodeID()));

      //      Node StartNode = this.graph.findNodeByID(mapEdges.get(i).getStartNodeID());
      //      Node EndNode = this.graph.findNodeByID(mapEdges.get(i).getEndNodeID());

      if (StartNode.getFloor().equals(floor) && EndNode.getFloor().equals(floor)) {
        EdgeRectangle er = new EdgeRectangle(StartNode, EndNode, this.isMapPage, this);
        allEdges.add(er.p);
      }
    }

    return allEdges;
  }

  /**
   * Generates a list of all nodes to display for a given floor, including nodes with type "HALL".
   *
   * @param floor the floor for which to generate nodes
   * @return the list of nodes to display
   * @throws SQLException if there is an error accessing the database
   * @throws IOException if there is an error loading image resources
   */
  public ArrayList<javafx.scene.Node> makeAllFloorNodes(String floor)
      throws SQLException, IOException {
    ArrayList<javafx.scene.Node> nodes = new ArrayList<>(); // list of shapes to be displayed
    ArrayList<NodeCircle> circles = new ArrayList<>(); // List of NodeCircle Objects

    HashMap<Integer, ArrayList<LocationName>> idToLocation = GlobalVariables.getHMap();

    ArrayList<Integer> sortedKeys = new ArrayList<>(idToLocation.keySet());
    System.out.println("FloorIN: " + floor);

    //    DataManager.getNodeByLocationName()

    ArrayList<Node> allNodes = DataManager.getAllNodes();
    for (int i = 0; i < allNodes.size(); i++) {
      Node n = allNodes.get(i);
      if (n.getFloor().equals(floor)) {
        String defShortName;
        //        if (i < sortedKeys.size()) {
        //          defShortName = map.get(sortedKeys.get(i)).get(0).getShortName();
        //        } else {
        //          defShortName = "" + n.getId();
        //        }

        //        if (!(locations == null) && locations.size() > 0) {

        if (idToLocation.get(n.getId()) != null) {
          defShortName = idToLocation.get(n.getId()).get(0).getShortName();
        } else {
          defShortName = "" + n.getId();
        }

        circles.add(new NodeCircle(n, this.isMapPage, defShortName, this));
      }
    }
    for (NodeCircle c : circles) {
      nodes.add(c.p);
    }
    return nodes;
  }

  public void refresh() throws SQLException, IOException {
    this.setCurrentDisplayFloor(this.getCurrentDisplayFloor());
  }

  public void refresh(Timestamp time) throws SQLException, IOException {
    this.setCurrentDisplayFloor(this.getCurrentDisplayFloor(), time);
  }

  public void setCurrentDisplayFloor(String currentDisplayFloor) throws SQLException, IOException {
    this.setCurrentDisplayFloor(currentDisplayFloor, currTime);
  }

  public void setCurrentDisplayFloor(String currentDisplayFloor, Timestamp time)
      throws SQLException, IOException {
    this.currentDisplayFloor = currentDisplayFloor;

    // Time
    this.setGlobalVars(time);

    //    System.out.println("Done");

    if (this.isMapPage) {
      Platform.runLater(() -> MapController.updateNames());
    }

    // Time
    subAnchor.getStyleClass().remove(0);

    String cssFloorName = this.takeFloor(currentDisplayFloor, true);
    String shortRealFloorName = this.takeFloor(currentDisplayFloor, false);

    // Delete all nodeCircles

    //    System.out.println("Change Floor");
    //    System.out.println("SubLC:" + subAnchor.getChildren().size());
    //    subAnchor.getChildren();currentFloorNodes;
    if (!this.currentFloorShapes.isEmpty()) {
      for (int i = subAnchor.getChildren().size() - 1; i >= 0; i--) {
        if (this.currentFloorShapes.contains(subAnchor.getChildren().get(i))) {
          //          System.out.println("Rem: " + i);
          subAnchor.getChildren().remove(i);
        }
      }
      //      this.setPrevPath(null);
      this.setCurrentFloorShapes(null);
    }
    //    System.out.println("SubLC:" + subAnchor.getChildren().size());

    if (!this.getPrevPath().isEmpty()) {
      for (int i = subAnchor.getChildren().size() - 1; i >= 0; i--) {
        if (this.getPrevPath().contains(subAnchor.getChildren().get(i))) {
          subAnchor.getChildren().remove(i);
        }
      }
      this.setPrevPath(null);
    }

    if (!this.getShapes().isEmpty() && this.isMapPage) {

      if (cssFloorName.equals("L1")) {
        //            for (int i = 0; i < map.getShapes().get(1).size(); i++) {
        //              System.out.print(" " + map.getShapes().get(1).get(i));
        //            }

        subAnchor.getChildren().addAll(this.getShapes().get(1));
        this.setPrevPath(this.getShapes().get(1));
      } else if (cssFloorName.equals("L2")) {
        subAnchor.getChildren().addAll(this.getShapes().get(0));
        this.setPrevPath(this.getShapes().get(0));
      } else if (cssFloorName.equals("1") || cssFloorName.equals("G1")) {
        subAnchor.getChildren().addAll(this.getShapes().get(2));
        this.setPrevPath(this.getShapes().get(2));
      } else if (cssFloorName.equals("2") || cssFloorName.equals("G2")) {
        subAnchor.getChildren().addAll(this.getShapes().get(3));
        this.setPrevPath(this.getShapes().get(3));
      } else if (cssFloorName.equals("3") || cssFloorName.equals("G3")) {
        subAnchor.getChildren().addAll(this.getShapes().get(4));
        this.setPrevPath(this.getShapes().get(4));
      } else {
        System.out.println("What are you doing?");
      }
    }

    // Re add based on new floor

    // Lots Of time.
    currentFloorShapes = (this.makeAllFloorShapes(shortRealFloorName));
    //    System.out.println("SetFloor :" + shortRealFloorName);

    //    System.out.println("SubL:" + subAnchor.getChildren().size());
    subAnchor.getChildren().addAll(currentFloorShapes);
    //    System.out.println("SubL:" + subAnchor.getChildren().size());

    subAnchor.getStyleClass().add(cssFloorName);
  }

  /**
   * Creates a path with circles representing the nodes from the given list of nodes.
   *
   * @param nodes List of nodes to create the path from
   * @return An ArrayList of Shape objects representing the path
   */
  private ArrayList<ArrayList<Shape>> makeShapePath(ArrayList<Node> nodes) throws SQLException {
    ArrayList<Node> listFloor1 = new ArrayList<>();
    ArrayList<Node> listFloor2 = new ArrayList<>();
    ArrayList<Node> listUpper1 = new ArrayList<>();
    ArrayList<Node> listUpper2 = new ArrayList<>();
    ArrayList<Node> listUpper3 = new ArrayList<>();

    ArrayList<Shape> lF2 = new ArrayList<Shape>();
    ArrayList<Shape> lF1 = new ArrayList<Shape>();
    ArrayList<Shape> uF1 = new ArrayList<Shape>();
    ArrayList<Shape> uF2 = new ArrayList<Shape>();
    ArrayList<Shape> uF3 = new ArrayList<Shape>();

    ArrayList<ArrayList<Shape>> pathAllFloor = new ArrayList<ArrayList<Shape>>();

    for (int i = 0; i < 5; i++) {
      pathAllFloor.add(new ArrayList<Shape>());
    }

    // pathAllFloor.add(0,listFloor2);

    for (int i = 0; i < nodes.size(); i++) {
      Node n = nodes.get(i);
      if (i == 0) {
        startFloor = n.getFloor();
      }
      if (i == nodes.size() - 1) {
        endFloor = n.getFloor();
      }

      if (n.getFloor().equals("L1")) {
        listFloor1.add(n);
      } else if (n.getFloor().equals("L2")) {
        listFloor2.add(n);
      } else if (n.getFloor().equals("1") || n.getFloor().equals("G1")) {
        listUpper1.add(n);
      } else if (n.getFloor().equals("2") || n.getFloor().equals("G2")) {
        listUpper2.add(n);
      } else if (n.getFloor().equals("3") || n.getFloor().equals("G3")) {
        listUpper3.add(n);
      } else {
        System.out.println("This should not be here");
      }
    }

    if (listFloor2.size() != 0) {
      pathAllFloor.add(0, makeShapePathFloor(listFloor2, "L2", true));
    }

    if (listFloor1.size() != 0) {
      System.out.println("size of list1: " + listFloor1.size());
      // System.out.println("size of list in list: " + pathAllFloor.get(1).size());
      pathAllFloor.add(1, makeShapePathFloor(listFloor1, "L1", true));
    }

    if (listUpper1.size() != 0) {
      pathAllFloor.add(2, makeShapePathFloor(listUpper1, "1", true));
    }

    if (listUpper2.size() != 0) {
      pathAllFloor.add(3, makeShapePathFloor(listUpper2, "2", true));
    }

    if (listUpper3.size() != 0) {
      pathAllFloor.add(4, makeShapePathFloor(listUpper3, "3", true));
    }

    return pathAllFloor;

    //    ArrayList<Shape> shapes = new ArrayList<Shape>();
    //
    //    Circle c;
    //    Path path;
    //
    //    for (int j = 0; j < 2; j++) {
    //      path = new Path();
    //      if (j == 0) {
    //        path.setStroke(borderColor);
    //      } else {
    //        path.setStroke(insideColor);
    //      }
    //      path.setStrokeWidth(lineT - (lineTout * 2 * j));
    //      path.getElements().add(new MoveTo(nodes.get(0).getX(), nodes.get(0).getY()));
    //
    //
    //      for (int i = 1; i < nodes.size(); i++) {
    //        path.getElements().add(new LineTo(nodes.get(i).getX(), nodes.get(i).getY()));
    //      }
    //      path.setStrokeLineJoin(StrokeLineJoin.ROUND);
    //      shapes.add(path);
    //    }
    //
    //    for (int i = 0; i < nodes.size(); i++) {
    //
    //      if (i == 0 || i == nodes.size() - 1) {
    //        c = new Circle(nodes.get(i).getX(), nodes.get(i).getY(), circleR + lineTout);
    //        c.setFill(borderColor);
    //        shapes.add(c);
    //
    //        c = new Circle(nodes.get(i).getX(), nodes.get(i).getY(), circleR);
    //        c.setFill(insideColor);
    //        shapes.add(c);
    //      }
    //    }
    //    prevPath.addAll(shapes);
    //    return shapes;
  }

  private ArrayList<Shape> makeShapePathFloor(ArrayList<Node> listNode, String floor, boolean flag)
      throws SQLException {
    ArrayList<Shape> shapes = new ArrayList<Shape>();
    ArrayList<Node> subsectionListNodes = new ArrayList<>();

    int saveIndex;
    Circle c;
    Path path;

    for (int j = 0; j < 2; j++) {
      path = new Path();
      if (j == 0) { // runs first time
        path.setStroke(GlobalVariables.getBorderColor());
      } else { // runs second time
        path.setStroke(GlobalVariables.getInsideColor());
      }

      // This is setting the line width
      path.setStrokeWidth(
          GlobalVariables.getLineT() - (GlobalVariables.getStrokeThickness() * 2 * j));

      // Adds new MoveTo object to the path elements, runs twice
      path.getElements().add(new MoveTo(listNode.get(0).getX(), listNode.get(0).getY()));

      for (int i = 1; i < listNode.size(); i++) {
        boolean isConnectedToPrev = false;
        for (Edge e : DataManager.getAllEdges()) {
          if (((e.getEndNodeID() == listNode.get(i).getId())
                  && (e.getStartNodeID() == listNode.get(i - 1).getId()))
              || ((e.getStartNodeID() == listNode.get(i).getId())
                  && (e.getEndNodeID() == listNode.get(i - 1).getId()))) {
            path.getElements().add(new LineTo(listNode.get(i).getX(), listNode.get(i).getY()));
            isConnectedToPrev = true;
          }
        }

        if (!isConnectedToPrev) {
          ArrayList<Node> subSection = new ArrayList<>(listNode.subList(i, listNode.size()));
          subsectionListNodes = subSection;
          break;
        }
        // path.getElements().add(new LineTo(listNode.get(i).getX(), listNode.get(i).getY()));
      }

      path.setStrokeLineJoin(StrokeLineJoin.ROUND);
      shapes.add(path);
    }

    // goto to here!!

    for (int i = 0; i < listNode.size(); i++) {

      if (i == 0) {

        ArrayList<Shape> newShapes = new ArrayList<>();

        if (floor.equals(startFloor)) {
          System.out.println("ABST");
          newShapes = NodeCircle.makeNodeShape("ABST");
        } else {
          System.out.println("STAR");
          newShapes = NodeCircle.makeNodeShape("STAR");
        }

        for (Shape s : newShapes) {
          s.setTranslateX(listNode.get(i).getX());
          s.setTranslateY(listNode.get(i).getY());
        }

        shapes.addAll(newShapes);

      } else if (i == listNode.size() - 1) {

        ArrayList<Shape> newShapes = new ArrayList<>();

        if (floor.equals(endFloor)) {
          System.out.println("ABSE");
          newShapes = NodeCircle.makeNodeShape("ABSE");
        } else {
          System.out.println("ENDF");
          newShapes = NodeCircle.makeNodeShape("ENDF");
        }

        for (Shape s : newShapes) {
          s.setTranslateX(listNode.get(i).getX());
          s.setTranslateY(listNode.get(i).getY());
        }

        shapes.addAll(newShapes);
      }
    }

    if (flag && subsectionListNodes.size() > 0) {
      shapes.addAll(makeShapePathFloor(subsectionListNodes, floor, false));
    }
    prevPath.addAll(shapes);
    return shapes;
  }

  /**
   * Draws the A* path between two points on a given floor.
   *
   * @param parent The Pane to draw the path on
   * @param firstClick The starting Point2D object
   * @param secondClick The ending Point2D object
   * @param floor1 The starting floor
   * @param floor2 The ending floor
   */
  public void drawPath(
      Pane parent, Point2D firstClick, Point2D secondClick, String floor1, String floor2)
      throws SQLException {

    //    String floor = "L1";

    List<Node> allNodes = this.graph.getNodes();

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

    //    MapNode startNode = allNodes.get(startIndex);
    //    MapNode endNode = allNodes.get(endIndex);
    System.out.println(startIndex + " " + endIndex);
    int startId = allNodes.get(startIndex).getId();
    int endId = allNodes.get(endIndex).getId();

    drawPath(parent, startId, endId);
  }

  /**
   * Draws the A* path between two nodes on a given floor and adds it to the specified parent Pane.
   *
   * @param parent the Pane to add the path to
   * @param startNodeId the starting MapNode ID
   * @param endNodeId the ending MapNode ID
   */
  public void drawPath(Pane parent, int startNodeId, int endNodeId) throws SQLException {
    ArrayList<Node> nodePath = this.graph.getPathBetween(startNodeId, endNodeId);

    System.out.println("SIZE: " + nodePath.size());

    shapes = makeShapePath(nodePath);

    this.floorChanges = new ArrayList<>();
    this.nodeDirections = nodePath;
    this.textDirections = getTextual(nodePath);

    // return shapes

    //    System.out.println(nodePath);

    // parent.getChildren().addAll(shapes);

  }

  public ArrayList<String> getTextual(ArrayList<Node> nodePath) {
    ArrayList<String> textuals = new ArrayList<>();

    StringBuilder sb = new StringBuilder();
    Direction direction;
    String distance;

    //    sb.append("1: ");
    sb.append("1: Go ");

    System.out.print("1: ");
    direction = Direction.STRAIGHT;
    sb.append(direction.getString());
    System.out.print(direction.getString());
    distance = getTextDistance(nodePath.get(0), nodePath.get(1));
    sb.append(distance);
    System.out.println(distance);

    textuals.add(sb.toString());

    int distanceNum = 0;

    if (nodePath.size() > 2) {
      for (int i = 1; i < nodePath.size() - 1; i++) {
        sb = new StringBuilder();
        Node prevNode = nodePath.get(i - 1);
        Node node = nodePath.get(i);
        Node nextNode = nodePath.get(i + 1);
        sb.append(String.valueOf(i + 1) + ": ");
        System.out.print(String.valueOf(i + 1) + ": ");
        direction = getDirection(prevNode, node, nextNode);

        if (direction.getString().equals("Straight")) {
          switch (GlobalVariables.getB().getValue()) {
            case ENGLISH:
              sb.append("Continue Straight ");
              break;
            case FRENCH:
              sb.append("Continuer Droit");
              break;
            case ITALIAN:
              sb.append("Continua Dritto");
              break;
            case SPANISH:
              sb.append("Continuar Recto");
              break;
          }

        } else if (direction.getString().equals("Down") || direction.getString().equals("Up")) {
          switch (GlobalVariables.getB().getValue()) {
            case ENGLISH:
              sb.append("Go ");
              if (direction.getString().equals("Down")) {
                sb.append("Down");
              } else {
                sb.append("Up");
              }
              break;
            case FRENCH: // En haut
              //              sb.append("Go ");
              if (direction.getString().equals("Down")) {
                sb.append("Descendez.");
              } else {
                sb.append("Monter");
              }
              break;
            case ITALIAN:
              sb.append("Vai ");
              if (direction.getString().equals("Down")) {
                sb.append("Giu");
              } else {
                sb.append("Su");
              }
              break;
            case SPANISH: // sube
              //              sb.append("Go ");
              if (direction.getString().equals("Down")) {
                sb.append("Baje");
              } else {
                sb.append("Sube");
              }
              break;
          }
        } else {
          switch (GlobalVariables.getB().getValue()) {
            case ENGLISH:
              sb.append("Turn " + direction.getString() + " then Continue Straight ");
              break;
            case ITALIAN:
              sb.append("Giri " + direction.getTranslatedString() + ", e poi vai dritto");
          }

          sb.append("Turn " + direction.getString() + " then Continue Straight ");
        }
        //        sb.append(direction.getString());

        System.out.print(direction.getString());
        distance = getTextDistance(node, nextNode);
        sb.append(distance);
        System.out.println(distance);

        textuals.add(sb.toString());
      }
    }

    //    DatePicker datePicker = new DatePicker();
    //
    //    datePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
    //      map.refresh(newValue);
    ////      newValue;
    //    });

    return textuals;
  }

  public Direction getDirection(Node prevNode, Node node, Node nextNode) {
    Direction direction;
    // y's are flipped I think since +y is down
    double angPrev = Math.atan2(prevNode.getY() - node.getY(), node.getX() - prevNode.getX());
    double angNext = Math.atan2(nextNode.getY() - node.getY(), node.getX() - nextNode.getX());
    double angDelta = angNext - angPrev;
    // adjust overturn
    if (angDelta > Math.PI) {
      angDelta -= 2 * Math.PI;
    } else if (angDelta < -Math.PI) {
      angDelta += 2 * Math.PI;
    }

    int curFloor = node.getFloorNum();
    int nextFloor = nextNode.getFloorNum();
    // System.out.println(angDelta);
    if (curFloor != nextFloor) {
      int delFloor = nextFloor - curFloor;
      this.floorChanges.add(Math.abs(delFloor));
      if (delFloor > 0) {
        direction = Direction.UP;
      } else {
        direction = Direction.DOWN;
      }
    } else {

      if ((angDelta > 7 * Math.PI / 8) || (angDelta < -7 * Math.PI / 8)) {
        direction = Direction.STRAIGHT;
      } else if (angDelta > Math.PI / 8) {
        direction = Direction.RIGHT;
      } else if (angDelta < Math.PI / 8) {
        direction = Direction.LEFT;
      } else {
        direction = Direction.BACK;
      }
    }

    return direction;
  }

  public double getNumericalDistance(Node node, Node nextNode) {
    int curFloor = node.getFloorNum();
    int nextFloor = nextNode.getFloorNum();
    if (curFloor != nextFloor) {
      int delFloor = nextFloor - curFloor;
      return delFloor;
    } else {
      double val = Math.round(node.calculateHeuristic(nextNode) * 100.0) / 100.0;
      return val;
    }
  }

  public String getTextDistance(Node node, Node nextNode) {
    String distance;
    int curFloor = node.getFloorNum();
    int nextFloor = nextNode.getFloorNum();
    if (curFloor != nextFloor) {
      int delFloor = nextFloor - curFloor;
      //      distance = " Floors: " + String.valueOf(Math.abs(delFloor));

      if (delFloor > 1) {
        distance = " " + String.valueOf(Math.abs(delFloor)) + " Floors";
      } else {
        distance = " " + String.valueOf(Math.abs(delFloor)) + " Floor";
      }

    } else {

      double val = Math.round(node.calculateHeuristic(nextNode) * 100.0) / 100.0;

      distance = " " + String.valueOf(val) + " units";
      //      distance = " Pixels: " + String.valueOf(node.calculateHeuristic(nextNode));
    }
    return distance;
  }

  /**
   * Draws the A* path between two given nodes and adds it to the specified parent Pane.
   *
   * @param parent the Pane to add the path to
   */
  //  public ArrayList<ArrayList<Shape>> drawAStarPath(Pane parent, int sInd, int eInd) {
  //    List<Node> allNodes = this.graph.getNodes();
  //
  //    MapNode startNode = allNodes.get(sInd);
  //    MapNode endNode = allNodes.get(eInd);
  //
  //    ArrayList<Node> nodePath = this.graph.AStar(startNode, endNode);
  //
  //    ArrayList<ArrayList<Shape>> shapes = makeShapePath(nodePath);
  //
  //    // System.out.println(shapes.get(1));
  //    return shapes;
  //
  //    // parent.getChildren().addAll(shapes.get(1));
  //
  //  }

  /**
   * Retrieves the names of all nodes on the specified floor and returns them in an ObservableList.
   *
   * @return an ObservableList of node names on the specified floor
   * @throws SQLException if there is an error with the SQL database
   */
  public ObservableList<String> getAllNodeNames() throws SQLException {
    ObservableList<String> nodeNames = FXCollections.observableArrayList();
    //    for (Node n : DataManager.getAllNodes()) {
    //      //      if (n.getFloor().equals(floor)) {
    //      //        nodeNames.addAll(("" + n.getId()));
    //      //      }
    //      nodeNames.addAll(("" + n.getId()));
    //    }

    HashMap<Integer, ArrayList<LocationName>> hMap = GlobalVariables.getHMap();
    //    HashMap<Integer, ArrayList<LocationName>> hMap =
    //        DataManager.getAllLocationNamesMappedByNode(new
    // Timestamp(System.currentTimeMillis()));

    for (Integer i : hMap.keySet()) {
      // Gets rid of all Hallway locations
      if (!hMap.get(i).get(0).getNodeType().equals("HALL")) {
        nodeNames.add(hMap.get(i).get(0).getLongName());
      }
    }

    Collections.sort(nodeNames);

    return nodeNames;
  }

  /**
   * Returns an observable list of all floor names.
   *
   * @return an observable list of all floor names
   */
  public ObservableList<String> getAllFloors() {
    ObservableList<String> floorNames = FXCollections.observableArrayList();

    for (String f : floorArr) {
      floorNames.addAll(f);
    }

    return floorNames;
  }

  public ArrayList<String> getAllFloorsInPath() {
    // ObservableList<String> floorNames = FXCollections.observableArrayList();
    ArrayList<String> floorPathArr = new ArrayList<>();

    for (int i = 0; i < shapes.size(); i++) {
      if (!shapes.get(i).isEmpty()) {
        if (i == 0) {
          floorPathArr.add("L2");
        } else if (i == 1) {
          floorPathArr.add("L1");
        } else if (i == 2) {
          floorPathArr.add("G1");
        } else if (i == 3) {
          floorPathArr.add("G2");
        } else if (i == 4) {
          floorPathArr.add("G3");
        }
      }
    }
    return floorPathArr;
  }

  public ObservableList<String> getAllAlgos() {
    ObservableList<String> algoNames = FXCollections.observableArrayList();

    for (String a : algoArr) {
      algoNames.addAll(a);
    }

    return algoNames;
  }

  public String takeFloor(String f, boolean flag) {
    String retStr = "";
    if (f == null) {
      return "L1";
    }
    //    System.out.println(f);
    switch (f) {
      case ("Lower Level 1"):
        return "L1";
      case ("Lower Level 2"):
        return "L2";
      case ("Ground Floor"):
        retStr = "GG";
        return retStr;
      case ("First Floor"):
        if (flag) {
          retStr = "G1";
        } else {
          retStr = "1";
        }
        return retStr;
      case ("Second Floor"):
        if (flag) {
          retStr = "G2";
        } else {
          retStr = "2";
        }
        return retStr;
      case ("Third Floor"):
        if (flag) {
          retStr = "G3";
        } else {
          retStr = "3";
        }
        return retStr;
      case ("L1"):
        return "Lower Level 1";
      case ("L2"):
        return "Lower Level 2";
      case ("1"):
        return "First Floor";
      case ("2"):
        return "Second Floor";
      case ("3"):
        return "Third Floor";
      default:
        return "You should never see  this!!!";
    }
  }

  /**
   * An event handler that sets the visibility of a Circle object to true when triggered by a
   * MouseEvent. When this event handler is invoked, it prints "V" to the console, gets the source
   * of the event (assumed to be a Circle object), and sets the visibility of the Circle object to
   * true. Additionally, it prints the ID of the Circle object to the console.
   *
   * @param <MouseEvent> the type of the MouseEvent that triggers this event handler
   */
  EventHandler<MouseEvent> makeVisible =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Circle outer = ((Circle) event.getSource());
          System.out.println("V");
          System.out.println(outer.getId());
          outer.setVisible(true);
        }
      };

  /**
   * An event handler that sets the visibility of a Circle object to false when triggered by a
   * MouseEvent. When this event handler is invoked, it prints "H" to the console, gets the source
   * of the event (assumed to be a Circle object), and sets the visibility of the Circle object to
   * false. Additionally, it prints the ID of the Circle object to the console.
   *
   * @param <MouseEvent> the type of the MouseEvent that triggers this event handler
   */
  EventHandler<MouseEvent> hide =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("H");
          Circle outer = ((Circle) event.getSource());
          System.out.println(outer.getId());
          outer.setVisible(false);
        }
      };

  /**
   * Draws emergency markers on the map. The method retrieves a list of all emergency events from
   * the EmergencyEventManager and creates a corresponding emergency marker for each event on the
   * map.
   */
  public void drawEmergencies() {}

  //  public ObservableList<String> getAllNodeNames(String floor) throws SQLException {
  //    ObservableList<String> nodeNames = FXCollections.observableArrayList();
  //    for MapNode n : DataManager.getAllNodes()) {
  //      if (n.getFloor().equals(floor)) {
  //        nodeNames.addAll(("" + n.getId()));
  //      }
  //    }
  //    return nodeNames;
  //  }

  /**
   * Generates a list of nodes to display for a given floor, excluding any nodes with type "HALL".
   *
   * @param floor the floor for which to generate nodes
   * @return the list of nodes to display
   * @throws SQLException if there is an error accessing the database
   * @throws IOException if there is an error loading image resources
   */
  //  public ArrayList<javafx.scene.Node> makeAllFloorNodes(String floor) throws SQLException,
  // IOException {
  //    ArrayList<javafx.scene.Node> nodes = new ArrayList<javafx.scene.Node>(); // list of shapes
  // to be displayed
  //    List<NodeCircle> circles = new ArrayList<>(); // List of NodeCircle Objects
  //
  //    for MapNode n : DataManager.getAllNodes()) {
  //      if (n.getFloor().equals(floor)) {
  //        ArrayList<String> nameType = new ArrayList<>();
  //        try {
  //          nameType = n.getShortName();
  //        } catch (SQLException ex) {
  //          System.out.println(ex.toString());
  //          System.out.println("Could not find info");
  //        }
  //        // String shortName = "";
  //        String nodeType = "";
  //        if (nameType.size() == 2) {
  //          // shortName = nameType.get(0);
  //          nodeType = nameType.get(1);
  //        } else {
  //        }
  //        if (!nodeType.equals("HALL")) circles.add(new NodeCircle(n));
  //      }
  //    }
  //    for (NodeCircle c : circles) {
  //      nodes.add(c.p);
  //      nodes.add(c.label);
  //    }
  //    return nodes;
  //  }

  /** Draws location names on the map. */
  public void drawLocationNames() throws SQLException {}

  /** Clears the map of all displayed nodes. */
  public void clearMap() {}

  /**
   * Returns the width of the map.
   *
   * @return the width of the map
   */
  public double getMapWitdh(AnchorPane outerMapAnchor) {
    return outerMapAnchor.getWidth();
  }

  /**
   * returns the height of the map.
   *
   * @return the height of the map
   */
  public double getMapHeight(AnchorPane outerMapAnchor) {
    return outerMapAnchor.getHeight();
  }

  /**
   * Centers and zooms the given GesturePane to display the map.
   *
   * @param parent the GesturePane to center and zoom
   */
  public void centerAndZoom(GesturePane parent, AnchorPane outerMapAnchor) {
    double parentW = getMapWitdh(outerMapAnchor); // 1335;
    double parentH = getMapHeight(outerMapAnchor); // 720;

    System.out.println("PW: " + parentW + ", " + parentH);

    //        parentW = 1662;
    //        parentH = 880;

    //    Point2D scaleOneDim = new Point2D(760 * 2, 512 * 2); // hard Coded
    Point2D scaleOneDim = new Point2D(1335, 768);

    double scaleX = parentW / scaleOneDim.getX();
    double scaleY = parentH / scaleOneDim.getY();

    System.out.println(scaleX + ", " + scaleY);

    double scaleFactor = Double.min(scaleX, scaleY);

    centerPoint = new Point2D(2215, 1045); // Hard Coded
    //    // -1627.2856715232545, -690.8681650647059
    //    Point2D CMin = centerPoint.add(new Point2D(-1610, -648));
    //
    //    System.out.println("CorC: " + CMin.getX() + ", " + CMin.getY());
    //
    //    Point2D CDiff =
    //        new Point2D((parentW / 2) * (1 / scaleFactor), (parentH / 2) * (1 / scaleFactor));
    //
    //    System.out.println("AkC: " + CDiff.getX() + ", " + CDiff.getY());
    //
    //    centerTL = centerPoint.subtract(CMin);

    parent.zoomTo(scaleFactor, Point2D.ZERO);
    parent.centreOn(centerPoint); // Actually Moves the Top left corner
  }

  public void centerAndZoomStart(GesturePane parent, AnchorPane outerMapAnchor, Node sNode) {
    Point2D centerOnStart = new Point2D(sNode.getX(), sNode.getY());
    parent.centreOn(centerOnStart);
  }
}
