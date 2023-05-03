package edu.wpi.teamname.navigation;

import edu.wpi.teamname.App;
import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.Navigation;
import edu.wpi.teamname.Screen;
import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.requestitem.ConfRoom;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.controlsfx.control.PopOver;

public class NodeCircle {

  public Pane p;
  private Pane iconP;
  private Shape inner;
  private Shape outer;

  public Label label;

  private Point2D nodeCords;
  private int nodeID;
  private String firstShortName;

  private VBox nodeBox;
  private PopOver nodePop;
  private VBox changeBox;

  private Map map;
  private boolean isMapPage;

  /**
   * A NodeCircle is a circle that represents a Node in the GUI. It is composed of two circles, an
   * inner one that is filled with a color that represents the Node type, and an outer one that
   * serves as a border. It also contains a label that displays the Node's name. When the user
   * hovers over a NodeCircle, the outer circle and the label become visible. When the user clicks
   * on a NodeCircle, a VBox containing information about the Node is displayed.
   *
   * @param n The Node that this NodeCircle represents.
   * @throws IOException If there was an error reading from the FXML file for the Node editing menu.
   */
  public NodeCircle(Node n, boolean isMapPage, String firstShortName, Map map)
      throws IOException, SQLException {
    float shiftX = 0; // circleR;
    float shiftY = 0; // circleR;

    this.isMapPage = isMapPage;
    this.map = map;

    this.firstShortName = firstShortName;
    float circleRCopy = GlobalVariables.getCircleR();

    nodeCords = new Point2D(n.getX(), n.getY());
    nodeID = n.getId();

    p = new Pane();

    // Visible By default

    float boxW = circleRCopy;
    float boxH = circleRCopy;

    p.setTranslateX(n.getX() - shiftX);
    p.setTranslateY(n.getY() - shiftY);
    p.setMaxWidth(boxW);
    p.setMaxHeight(boxH);

    // Scale all to like 0.75
    // Scale halls to make them smaller.

    if (isMapPage) {
      // Map Page

    } else {
      // Map Edit Page
      // Display All nodes and edges (Edges will have to be made somewhere else)

      //      p.setOnMouseEntered(makeVisible);
      //      p.setOnMouseExited(hide);
      p.setOnMouseClicked(editNodeBox);
    }

    final var resource = App.class.getResource("views/TextLabel.fxml");

    ArrayList<LocationName> l = GlobalVariables.getHMap().get(this.nodeID);

    String locName = "";

    String nodeType = "";
    if (l != null) {
      nodeType = l.get(0).getNodeType();
      locName = l.get(0).getLongName();
    }

    if (!locName.equals("")) {
      //      System.out.println("Serv");
      this.iconP = new Pane();

      int pSize = 20;

      iconP.setMinWidth(pSize);
      iconP.setMaxWidth(pSize);
      iconP.setMinHeight(pSize);
      iconP.setMaxHeight(pSize);
      boolean added = false;

      // */
      if (GlobalVariables.getShowServiceIcons().booleanValue()) {
        int offset = 20;

        iconP.setTranslateX(iconP.getTranslateX() - offset);
        iconP.setTranslateY(iconP.getTranslateY() - offset);

        //        iconP.setBackground(Background.fill(Color.GREEN));

        ImageView servIcon;

        Shape s = NodeCircle.makeNodeShape("").get(1);
        Shape sO = NodeCircle.makeNodeShape("").get(0);

        s.setTranslateX(s.getTranslateX() + pSize / 2);
        s.setTranslateY(s.getTranslateY() + pSize / 2);

        sO.setTranslateX(sO.getTranslateX() + pSize / 2);
        sO.setTranslateY(sO.getTranslateY() + pSize / 2);

        int iconS = 14;

        ArrayList<ServiceRequest> srs = GlobalVariables.getServiceRequests();
        for (ServiceRequest request : srs) {
          Timestamp curr = map.getCurrTime();
          String currStr = curr.toString().split("\\s+")[0];
          if (request.getRoomNumber().equals(locName)) {
            //            s.setFill(Color.RED);
            Timestamp t = request.getDeliverBy();

            String tStr = t.toString().split("\\s+")[0];

            if (tStr.equals(currStr)) {
              //            s.setFill(Color.BLUE);

              s.setOnMouseClicked(
                  event -> {
                    GlobalVariables.setRequestFromMap(true);
                    GlobalVariables.setDateFromMap(t);
                    GlobalVariables.setRoomFromMap(request.getRoomNumber());

                    Navigation.navigate(Screen.SERVICE_REQUEST_VIEW);
                  });

              switch (request.getRequestType()) {
                case MEAL:
                  //                  s.setFill(Color.RED);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/meal.png");
                  break;
                case FLOWER:
                  //                  s.setFill(Color.YELLOW);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/flower.png");
                  break;
                case FURNITURE:
                  //                  s.setFill(Color.ORANGE);
                  servIcon =
                      new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/furniture.png");
                  break;
                case OFFICESUPPLY:
                  //                  s.setFill(Color.PURPLE);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/office.png");
                  break;
                case MEDICALSUPPLY:
                  //                  s.setFill(Color.DARKGREEN);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/bandaid.png");
                  break;
                case PHARMACEUTICAL:
                  //                  s.setFill(Color.WHITE);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/SRIcons/pill.png");
                  break;
                default:
                  //                  s.setFill(Color.BLUE);
                  servIcon = new ImageView("edu/wpi/teamname/images/MenuIcons/assignment.png");
                  break;
              }

              System.out.println("STat: " + request.getStatus().toString());
              switch (request.getStatus()) {
                case DONE:
                  s.setFill(Color.GREEN);
                  break;
                case BLANK:
                  s.setFill(Color.GREY);
                  break;
                case PROCESSING:
                  s.setFill(Color.ORANGE);
                  break;
                default:
                  s.setFill(Color.WHITE);
                  break;
              }

              //              servIcon.minHeight(iconS);
              //              servIcon.maxHeight(iconS);
              servIcon.setFitHeight(iconS);
              servIcon.setFitWidth(iconS);

              float circleR = GlobalVariables.getCircleR();

              servIcon.setTranslateX(servIcon.getTranslateX() + (pSize / 2) - (iconS / 2));
              servIcon.setTranslateY(servIcon.getTranslateY() + (pSize / 2) - (iconS / 2));

              iconP.getChildren().add(sO);
              iconP.getChildren().add(s);
              iconP.getChildren().add(servIcon);

              //              p.getChildren().add(this.iconP);
              added = true;
            }

            //          Timestamp.

            //          System.out.println("SD: " + tStr + " DPD:" + currStr + ": " + t.toString());
          }
        }
      }
      // */

      // */
      if (GlobalVariables.getShowConfItems().booleanValue()) {
        int offset = 20;

        //        iconP.setTranslateX(iconP.getTranslateX() + offset);
        //        iconP.setTranslateY(iconP.getTranslateY() - offset);

        //        iconP.setBackground(Background.fill(Color.GREEN));

        ImageView confIcon =
            new ImageView("edu/wpi/teamname/images/MenuIcons/request_room_home.png");

        Shape s2 = NodeCircle.makeNodeShape("").get(1);
        Shape s2O = NodeCircle.makeNodeShape("").get(0);

        s2.setTranslateX(s2.getTranslateX() + pSize / 2 + offset);
        s2.setTranslateY(s2.getTranslateY() + pSize / 2);

        s2O.setTranslateX(s2O.getTranslateX() + pSize / 2 + offset);
        s2O.setTranslateY(s2O.getTranslateY() + pSize / 2);

        int iconS = 14;

        //        confIcon.minHeight(iconS);
        //        confIcon.maxHeight(iconS);
        confIcon.setFitHeight(iconS);
        confIcon.setFitWidth(iconS);

        float circleR = GlobalVariables.getCircleR();

        confIcon.setTranslateX(confIcon.getTranslateX() + (pSize / 2) + (offset) - (iconS / 2));
        confIcon.setTranslateY(confIcon.getTranslateY() + (pSize / 2) - (iconS / 2));

        ArrayList<ConfRoom> confs = GlobalVariables.getConfRooms();
        String sName = l.get(0).getShortName();
        for (ConfRoom room : confs) {
          String roomname = room.getLocationName().split(",")[0];
          if (roomname.equals(locName)) {

            //            ArrayList<ConfReservation> allRes = GlobalVariables.getConfReservations();

            //            ArrayList<ConfReservation> reses = DataManager.getResForRoom(room);
            ArrayList<ConfReservation> reses =
                GlobalVariables.getAllRes().get(GlobalVariables.roomNumToIndex(room.getRoomID()));

            Timestamp curr = map.getCurrTime();
            String currStr = curr.toString().split("\\s+")[0];
            boolean status = true;
            for (ConfReservation res : reses) {
              Timestamp resT = res.getDateBook();
              String resTStr = resT.toString().split("\\s+")[0];

              if (currStr.equals(resTStr)) {

                Timestamp mushedDateTime = curr;

                Timestamp sysTime = new Timestamp(System.currentTimeMillis());

                //                mushedDateTime.setHours(sysTime.getHours());
                mushedDateTime.setHours(12);
                mushedDateTime.setMinutes(sysTime.getMinutes());
                mushedDateTime.setSeconds(sysTime.getSeconds());

                //                mushedDateTime.set
                //                int nodeID;

                status = map.getRm().checkAvailable(nodeID, mushedDateTime);

                //                RoomStatus status = RoomStatus.AVAILABLE;

                break;
              }
            }
            if (status) {
              s2.setFill(Color.GREEN);
            } else {
              s2.setFill(Color.RED);
            }

            //                switch (status) {
            //                  case ERROR:
            //                    s2.setFill(Color.RED);
            //                    break;
            //                  case BOOKED:
            //                    s2.setFill(Color.ORANGE);
            //                    break;
            //                  case SELECTED:
            //                    s2.setFill(Color.YELLOW);
            //                    break;
            //                  case AVAILABLE:
            //                    s2.setFill(Color.GREEN);
            //                  default:
            //                    s2.setFill(Color.WHITE);
            //                    break;
            //                }
            //
            s2.setOnMouseClicked(
                event -> {
                  GlobalVariables.setRequestFromMap(true);
                  GlobalVariables.setRoomIDFromMap(room.getRoomID());
                  GlobalVariables.setDateFromMap(map.getCurrTime());
                  Navigation.navigate(Screen.CONF_VIEW);
                });

            System.out.println("Child");

            iconP.getChildren().add(s2O);
            iconP.getChildren().add(s2);
            iconP.getChildren().add(confIcon);

            added = true;
          }
        }
      }
      // */

      if (added) {
        p.getChildren().add(this.iconP);
      }
    }

    //    map.getShowLegend();

    ArrayList<Shape> nodeShapes;

    if (map.getShowLegend()) {
      nodeShapes = NodeCircle.makeNodeShape(nodeType);
    } else {
      // Nothing means it will be drawn default, and doesnt edit the node type!
      nodeShapes = NodeCircle.makeNodeShape("");
    }

    this.outer = nodeShapes.get(0);
    this.inner = nodeShapes.get(1);

    p.getChildren().addAll(this.outer, this.inner);

    int index = map.getRoomTypes().indexOf(nodeType);

    // Only show this if the variable is set to true.
    if (index == -1 || map.getShowTypeLabels()[index]) {
      final FXMLLoader loader = new FXMLLoader(resource);
      try {
        label = loader.load();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      int labelTextType = map.getLabelTextType();
      if (labelTextType == 0) {
        // Show Long Name

        label.setText(firstShortName);
        if (l != null) {
          label.setText(l.get(0).getLongName());
        }
      } else if (labelTextType == 1) {
        // Show Short Name
        label.setText(firstShortName);
      } else if (labelTextType == 2) {
        // Show ID
        label.setText("" + this.nodeID);
      } else {
        // Dont add the label.
        return;
      }

      //    CornerRadii corn = new CornerRadii(7);
      //    label.setBackground(new Background(new BackgroundFill(GlobalVariables.getLabelColor(),
      // corn, Insets.EMPTY)));
      //    label.setTextFill(GlobalVariables.getLabelTextColor());

      label.setTranslateX(-GlobalVariables.getCircleR());
      label.setTranslateY(-30);

      p.getChildren().add(this.label);
    }
  }

  public static ArrayList<Shape> makeNodeShape(String nodeType) {
    Shape outer;
    Shape inner;
    Shape inner2 = null;

    float shiftX = 0; // circleR;
    float shiftY = 0; // circleR;

    float circleRCopy = GlobalVariables.getCircleR();

    float squareShift = GlobalVariables.getStrokeThickness();
    float scaleDown = 0.75f;

    float outerRectLength = (circleRCopy * scaleDown + GlobalVariables.getStrokeThickness()) * 2;
    float innerRectLength = (circleRCopy * scaleDown) * 2;
    Rectangle outerRect =
        new Rectangle(
            shiftX - (outerRectLength / 2),
            shiftY - (outerRectLength / 2),
            outerRectLength,
            outerRectLength);

    Rectangle innerRect =
        new Rectangle(
            shiftX + squareShift - (outerRectLength / 2),
            shiftY + squareShift - (outerRectLength / 2),
            innerRectLength,
            innerRectLength);

    Circle outerCircle =
        new Circle(
            shiftX, shiftY, (circleRCopy * scaleDown) + GlobalVariables.getStrokeThickness());

    Circle innerCircle = new Circle(shiftX, shiftY, (circleRCopy * scaleDown));

    ArrayList<Shape> ret = new ArrayList<>();

    switch (nodeType) {
      case "BATH":
        outer = outerCircle;
        inner = innerCircle;
        inner.setFill(GlobalVariables.getInsideYellow());

        break;
      case "CONF":
        outer = outerRect;
        inner = innerRect;

        outer.setRotate(45);
        inner.setRotate(45);

        inner.setFill(GlobalVariables.getInsideBlue());

        break;
      case "DEPT":
        outer = outerRect;
        inner = innerRect;

        outer.setRotate(45);
        inner.setRotate(45);

        inner.setFill(GlobalVariables.getInsideGreen());

        break;
      case "ELEV":
        outer = outerRect;
        inner = innerRect;

        inner.setFill(GlobalVariables.getInsideBlue());

        break;
      case "EXIT":
        outer = outerRect;
        inner = innerRect;

        inner.setFill(GlobalVariables.getInsideRed());

        break;
        //      case "HALL":
        //        break;
      case "INFO":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideBlue());

        break;
      case "LABS":
        outer = outerRect;
        inner = innerRect;

        outer.setRotate(45);
        inner.setRotate(45);

        inner.setFill(GlobalVariables.getInsideOrange());

        break;
      case "REST":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideYellow());

        break;
      case "RETL":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideGreen());

        break;
      case "SERV":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideGreen());

        break;
      case "STAI":
        outer = outerRect;
        inner = innerRect;

        inner.setFill(GlobalVariables.getInsideOrange());

        break;
      case "ABST":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideWhite());

        break;
      case "STAR":
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideOrange());

        break;
      case "ABSE":
        outer = outerCircle;
        inner = innerCircle;

        inner2 = new Circle(shiftX, shiftY, (circleRCopy * scaleDown * 0.6));

        inner.setFill(GlobalVariables.getInsideWhite());
        inner2.setFill(GlobalVariables.getBorderColor());

        break;
      case "ENDF":
        outer = outerCircle;
        inner = innerCircle;

        inner2 = new Circle(shiftX, shiftY, (circleRCopy * scaleDown * 0.6));

        inner.setFill(GlobalVariables.getInsideOrange());
        inner2.setFill(GlobalVariables.getBorderColor());

        break;
      default:
        outer = outerCircle;
        inner = innerCircle;

        inner.setFill(GlobalVariables.getInsideColor());
        break;
    }

    outer.setFill(GlobalVariables.getBorderColor());

    ret.add(outer);
    ret.add(inner);
    if (inner2 != null) {
      ret.add(inner2);
    }

    return ret;
  }

  /**
   * A mouse event handler that hides the VBox containing information about a NodeCircle when the
   * user's mouse exits the NodeCircle.
   */
  EventHandler<MouseEvent> hide =
      new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          for (javafx.scene.Node n : p.getChildren()) {
            if (n.getClass() == VBox.class) {
              p.getChildren().remove(n);
              break;
            }
          }

          for (javafx.scene.Node n : p.getChildren()) {
            if (n.getClass() == VBox.class) {
              n.setOpacity(0);
            }
          }
          //          p.setOpacity(0);
        }
      };

  /**
   * An event handler to make a node visible on mouse click.
   *
   * <p>This handler sets the opacity of the clicked node's parent pane and all its children to 1,
   * except for any VBoxes which are excluded. This is used to show a previously hidden node and its
   * contents.
   *
   * @param event the mouse event triggering the handler
   */
  EventHandler<MouseEvent> makeVisible =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          p.setOpacity(1);
          for (javafx.scene.Node n : p.getChildren()) {
            if (!(n.getClass() == VBox.class)) {
              n.setOpacity(1);
            }
          }
        }
      };

  EventHandler<MouseEvent> boxVisible =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          MFXButton button = ((MFXButton) event.getSource());
          //        p.setOpacity(1);

          final var resource = App.class.getResource("views/ChangeNode.fxml");
          final FXMLLoader loader = new FXMLLoader(resource);
          try {
            changeBox = loader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          // Set Location Name
          TextField Location =
              (TextField) ((Pane) (changeBox.getChildren().get(0))).getChildren().get(2);

          //          HashMap<Integer, ArrayList<LocationName>> map
          //          try {
          //            map =
          //                DataManager.getAllLocationNamesMappedByNode(
          //                    new Timestamp(System.currentTimeMillis()));
          //          } catch (SQLException e) {
          //            throw new RuntimeException(e);
          //          }

          //          for (Integer key : map.keySet()) {
          //            System.out.println("Key: " + key + " Val: " + map.get(key));
          //          }
          //          System.out.println(map);

          String location;

          HashMap<Integer, ArrayList<LocationName>> idToLocation = GlobalVariables.getHMap();

          if (idToLocation.get(nodeID) != null) {
            //          if (map.get(nodeID).size() > 0) {
            location = idToLocation.get(nodeID).get(0).getLongName();
          } else {
            location = "" + nodeID;
          }
          Location.setText(location);

          // Set X
          TextField XText =
              (TextField) ((Pane) (changeBox.getChildren().get(1))).getChildren().get(2);
          XText.setText("" + nodeCords.getX());
          // Set Y
          TextField YText =
              (TextField) ((Pane) (changeBox.getChildren().get(2))).getChildren().get(2);
          YText.setText("" + nodeCords.getY());

          Node currNode = null;
          try {
            currNode = DataManager.getNode(nodeID);
            //            System.out.println("Done: " + currNode.toString());
          } catch (SQLException e) {
            System.out.println("ERROR: " + e.toString());
            throw new RuntimeException(e);
          }

          TextField floorText =
              (TextField) ((Pane) (changeBox.getChildren().get(3))).getChildren().get(2);

          TextField buildingText =
              (TextField) ((Pane) (changeBox.getChildren().get(4))).getChildren().get(2);

          if (currNode != null) {
            floorText.setText(currNode.getFloor());
            buildingText.setText(currNode.getBuilding());
          }

          // Set Remove Node. On Click
          MFXButton removeNodeButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(7))).getChildren().get(0);
          removeNodeButton.setOnMouseClicked(removeNode);
          // Set Submit
          MFXButton submitButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(7))).getChildren().get(2);
          submitButton.setOnMouseClicked(saveNodeChanges);

          changeBox.getChildren().remove(6);
          changeBox.getChildren().remove(5);
          //          changeBox.getChildren().remove(0);

          System.out.println("AddBox");

          nodePop.hide();

          PopOver pop = new PopOver(changeBox);
          pop.show(inner);
          changeBox.setOnMouseExited(event2 -> pop.hide());

          //          p.getChildren().addAll(changeBox);

          //
        }
      };

  EventHandler<MouseEvent> startMoveNode =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          //          System.out.println("SMN");

          if (map.getMovingNodeId() == -1) {
            map.setMovingNodeId(nodeID);
          } else {

          }
        }
      };

  EventHandler<MouseEvent> startCreateEdge =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          System.out.println("SCE");

          if (map.getStartEdgeNodeId() == -1) {
            map.setStartEdgeNodeId(nodeID);
          } else {
            // addEdge
            //            map.getStartEdgeNodeId(); nodeID;

            if (map.getStartEdgeNodeId() != nodeID) {

              Edge e = new Edge(map.getStartEdgeNodeId(), nodeID);
              try {
                DataManager.addEdge(e);
                map.setCurrentDisplayFloor(map.getCurrentDisplayFloor());
                //              changeFloor();
              } catch (SQLException ex) {
                System.out.println(ex);
                //            throw new RuntimeException(ex);
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }

              map.setStartEdgeNodeId(-1);

              try {
                map.refresh();
              } catch (SQLException ex) {
                throw new RuntimeException(ex);
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            } else {
              map.setStartEdgeNodeId(-1);
            }
          }
        }
      };

  private void addSelfToAlign() {
    //      Node n = DataManager.getNode(nodeID);
    //      ArrayList<Node> selection = map.getAlignSelection();
    ArrayList<Integer> selection = map.getAlignSelection();

    int ind = selection.indexOf(nodeID);

    if (ind != -1) {
      selection.remove(ind);
    } else {
      selection.add(nodeID);
    }

    //      System.out.println(nodeID);
    map.setAlignSelection(selection);

    //      map.refresh();

    //      System.out.println(map.getAlignSelection().size());
    //      System.out.println(map.getAlignSelection());
  }

  EventHandler<MouseEvent> startAlign =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          System.out.println("SA");
          addSelfToAlign();
        }
      };

  EventHandler<MouseEvent> align =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          System.out.println("A");
          addSelfToAlign();
          //          System.out.println(map.getAlignSelection().size());
          //          System.out.println(map.getAlignSelection());

          float averageX = 0, averageY = 0;

          //          ArrayList<Node> selection = map.getAlignSelection();
          ArrayList<Integer> sInt = map.getAlignSelection();

          ArrayList<Node> selection = new ArrayList<>();

          for (Integer i : sInt) {
            try {
              selection.add(DataManager.getNode(i));
            } catch (SQLException e) {
              throw new RuntimeException(e);
            }
          }

          for (Node n : selection) {
            averageX += n.getX();
            averageY += n.getY();
          }

          averageX /= (float) selection.size();
          averageY /= (float) selection.size();

          System.out.println("AX: " + averageX + " AY: " + averageY);

          float projChangeX = 0, projChangeY = 0;

          for (Node n : selection) {
            projChangeX += Math.abs(averageX - n.getX());
            projChangeY += Math.abs(averageY - n.getY());
          }

          System.out.println("PCX: " + projChangeX + " PCY: " + projChangeY);

          for (Node n : selection) {
            int currX = n.getX();
            int currY = n.getY();

            if (projChangeX < projChangeY) {
              System.out.println("Xc");
              //              currX = (int) averageX;
              currX = selection.get(selection.size() - 1).getX();
            } else {
              System.out.println("YC");
              //              currY = (int) averageY;
              currY = selection.get(selection.size() - 1).getY();
            }

            Node newN = new Node(n.getId(), currX, currY, n.getFloor(), n.getBuilding());

            try {
              DataManager.syncNode(newN);
            } catch (SQLException e) {
              throw new RuntimeException(e);
            }
          }

          map.setAlignSelection(new ArrayList<>());
          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  /**
   * EventHandler for saving changes made to a node in the system. This EventHandler is triggered
   * when the "Save Changes" button is clicked on the edit node screen. It retrieves the updated
   * information from the relevant TextFields and constructs a new Node object with the updated
   * information. It then calls the DataManager to update the information in the system database,
   * and prints a message to the console to confirm that the synchronization has been completed.
   *
   * @param event The MouseEvent that triggered the EventHandler.
   * @throws RuntimeException if an SQL exception occurs during the data synchronization process.
   */
  EventHandler<MouseEvent> editNodeBox =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Pane p = ((Pane) event.getSource());
          p.setOpacity(1);

          final var resource = App.class.getResource("views/NodePopup.fxml");
          final FXMLLoader loader = new FXMLLoader(resource);
          try {
            nodeBox = loader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          MFXButton moveButton =
              (MFXButton) ((Pane) (nodeBox.getChildren().get(0))).getChildren().get(0);
          moveButton.setOnMouseClicked(startMoveNode);

          MFXButton editNodeButton =
              (MFXButton) ((Pane) (nodeBox.getChildren().get(1))).getChildren().get(0);
          editNodeButton.setOnMouseClicked(boxVisible);

          MFXButton createEdgeButton =
              (MFXButton) ((Pane) (nodeBox.getChildren().get(2))).getChildren().get(0);

          createEdgeButton.setOnMouseClicked(startCreateEdge);
          if (map.getStartEdgeNodeId() != -1) {
            if (map.getStartEdgeNodeId() != nodeID) {
              editNodeButton.getStyleClass().remove("primary");
              editNodeButton.getStyleClass().add("primary-container");

              createEdgeButton.getStyleClass().remove("primary-container");
              createEdgeButton.getStyleClass().add("primary");
              createEdgeButton.setText("Complete Edge");
            } else {
              createEdgeButton.setText("Cancel Edge");
            }
          }

          MFXButton addAlignButton =
              (MFXButton) ((Pane) (nodeBox.getChildren().get(3))).getChildren().get(0);
          addAlignButton.setOnMouseClicked(startAlign);
          //          ArrayList<Node> selection = map.getAlignSelection();
          ArrayList<Integer> selection = map.getAlignSelection();
          if (selection.contains(nodeID)) {
            addAlignButton.setText("Remove from selection");
          }

          if (map.getAlignSelection().size() > 0) {
            MFXButton alignButton =
                (MFXButton) ((Pane) (nodeBox.getChildren().get(4))).getChildren().get(0);
            alignButton.setOnMouseClicked(align);
            editNodeButton.getStyleClass().remove("primary");
            editNodeButton.getStyleClass().add("primary-container");

          } else {
            ((Pane) (nodeBox.getChildren().get(4))).getChildren().remove(0);
          }

          System.out.println("NodePop");

          nodePop = new PopOver(nodeBox);
          nodePop.show(inner);
          nodeBox.setOnMouseExited(event2 -> nodePop.hide());

          event.consume();
        }
      };

  /**
   * An event handler to remove a node on mouse click.
   *
   * <p>This handler removes the node associated with the clicked button from the parent VBox and
   * deletes it from the database using the node ID. If an exception occurs during the deletion
   * process, it is caught and printed to the console.
   *
   * @param event the mouse event triggering the handler
   */
  EventHandler<MouseEvent> removeNode =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          System.out.println("REM");

          // Only the Node ID is important for Deletion
          Node n = new Node(nodeID, 0, 0, "", "");

          ArrayList<Node> AllNodes;
          try {
            AllNodes = DataManager.getAllNodes();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          ArrayList<Edge> AllEdges;
          try {
            AllEdges = DataManager.getAllEdges();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          for (int i = 0; i < AllEdges.size(); i++) {
            Edge e = AllEdges.get(i);
            Edge edge = null;
            Node adj = null;
            if (e.getStartNodeID() == nodeID) {
              adj = AllNodes.get(Node.idToIndex(e.getEndNodeID()));
              edge = new Edge(nodeID, adj.getId());
            } else if (e.getEndNodeID() == nodeID) {
              adj = AllNodes.get(Node.idToIndex(e.getStartNodeID()));
              edge = new Edge(adj.getId(), nodeID);
            }
            if (edge != null) {
              try {
                DataManager.deleteEdge(edge);
              } catch (SQLException ex) {
                throw new RuntimeException(ex);
              }
            }
          }

          try {
            DataManager.deleteNode(n);
            //            map.setCurrentDisplayFloor(map.getCurrentDisplayFloor());
          } catch (SQLException ex) {
            System.out.println(ex);
          }

          try {
            AllNodes = DataManager.getAllNodes();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          for (int i = 0; i < AllNodes.size(); i++) {
            Node node = AllNodes.get(i);
            int actualId = node.getId();
            int expectedId = Node.indexToId(i);
            if (expectedId != actualId) {

              System.out.println("EID: " + expectedId + ", AID: " + actualId);

              node.setId(expectedId);

              try {
                DataManager.syncNode(node);
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }

              // Update all edges
              try {

                AllEdges = DataManager.getAllEdges();
              } catch (SQLException e) {
                throw new RuntimeException(e);
              }

              for (int j = 0; j < AllEdges.size(); j++) {
                Edge e = AllEdges.get(j);
                //                System.out.println("Edge " + j + " " + e.getStartNodeID()  + ", "
                // + e.getEndNodeID() );
                boolean changed = false;
                if (e.getStartNodeID() == actualId) {
                  System.out.println("Changed ES: " + e.getStartNodeID() + " to: " + expectedId);
                  e.setStartNodeID(expectedId);
                  changed = true;
                } else if (e.getEndNodeID() == actualId) {
                  System.out.println("Changed EE: " + e.getEndNodeID() + " to: " + expectedId);
                  e.setEndNodeID(expectedId);
                  changed = true;
                }
                if (changed) {

                  try {
                    DataManager.syncEdge(e);
                  } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                  }
                }
              }
            }
          }

          try {
            map.refresh();
          } catch (SQLException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      };

  /**
   * An event handler for saving changes to a node when the save button is clicked. This handler is
   * triggered by a MouseEvent and updates the node's x and y coordinates, floor, and building
   * information based on the text fields in the parent VBox. The updated node is then saved to the
   * database using DataManager.syncNode().
   *
   * @param event The MouseEvent triggered by clicking the save button.
   * @throws RuntimeException if there is an SQLException while getting or synchronizing node data.
   */
  EventHandler<MouseEvent> saveNodeChanges =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Sound.playSFX(SFX.BUTTONCLICK);
          MFXButton SubmitButton = ((MFXButton) event.getSource());
          VBox v = (VBox) ((HBox) SubmitButton.getParent()).getParent();

          TextField xText = (TextField) ((Pane) (v.getChildren().get(1))).getChildren().get(2);
          TextField yText = (TextField) ((Pane) (v.getChildren().get(2))).getChildren().get(2);
          TextField floorText = (TextField) ((Pane) (v.getChildren().get(3))).getChildren().get(2);
          TextField buildingText =
              (TextField) ((Pane) (v.getChildren().get(4))).getChildren().get(2);

          Node currNode = null;
          try {
            currNode = DataManager.getNode(nodeID);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }

          int xPos = currNode.getX();
          if (!(xText.getText().equals(""))) {
            xPos = (int) Double.parseDouble(xText.getText());
          }
          int yPos = currNode.getY();
          if (!(yText.getText().equals(""))) {
            yPos = (int) Double.parseDouble(yText.getText());
          }
          String floor = currNode.getFloor();
          if (!(floorText.getText().equals(""))) {
            floor = floorText.getText();
          }
          String building = currNode.getBuilding();
          if (!(buildingText.getText().equals(""))) {
            building = buildingText.getText();
          }

          ArrayList<Node> allNodes;
          try {
            allNodes = DataManager.getAllNodes();
          } catch (SQLException ex) {
            throw new RuntimeException(ex);
          }

          Node n = new Node(nodeID, xPos, yPos, floor, building);
          //          n.setId();

          try {
            DataManager.syncNode(n);
            map.refresh();
          } catch (SQLException ex) {
            System.out.println(ex);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          System.out.println("DONE SYNC");

          // Update Based On text
        }
      };
}
