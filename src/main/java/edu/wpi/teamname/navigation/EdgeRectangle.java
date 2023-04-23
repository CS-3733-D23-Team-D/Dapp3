package edu.wpi.teamname.navigation;

import edu.wpi.teamname.App;
import edu.wpi.teamname.GlobalVariables;
import edu.wpi.teamname.database.DataManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import org.controlsfx.control.PopOver;

public class EdgeRectangle {

  public Pane p;
  public Path path;
  //  public Label label = new Label();
  private VBox changeBox;

  private Node startNode;
  private Node endNode;

  boolean isMapPage;
  Map map;

  public EdgeRectangle(Node startNode, Node endNode, boolean isMapPage, Map map) {
    this.startNode = startNode;
    this.endNode = endNode;

    this.isMapPage = isMapPage;
    this.map = map;

    float minX = Math.min(this.startNode.getX(), this.endNode.getX());
    float minY = Math.min(this.startNode.getY(), this.endNode.getY());

    this.p = new Pane();

    for (int j = 0; j < 2; j++) {
      path = new Path();
      if (j == 0) {
        path.setStroke(GlobalVariables.getBorderColor());
      } else {
        path.setStroke(GlobalVariables.getInsideColor());
      }
      path.setStrokeWidth(
          GlobalVariables.getLineT() - (GlobalVariables.getStrokeThickness() * 2 * j));

      path.getElements()
          .add(new MoveTo(this.startNode.getX() - minX, this.startNode.getY() - minY));
      path.getElements().add(new LineTo(this.endNode.getX() - minX, this.endNode.getY() - minY));

      path.setStrokeLineJoin(StrokeLineJoin.ROUND);
      p.getChildren().add(path);
    }

    p.setOnMouseClicked(boxVisible);

    float shiftX = 0; // circleR;
    float shiftY = 0; // circleR;

    p.setTranslateX(minX - shiftX);
    p.setTranslateY(minY - shiftY);

    //    p.setBackground(Background.fill(Color.RED));

    //    p.getChildren().add(this.label);
  }

  EventHandler<MouseEvent> boxVisible =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          //          Pane p = ((Pane) event.getSource());
          //          p.setOpacity(1);

          final var resource = App.class.getResource("views/EditEdge.fxml");
          // StartNodeID, EndNodeID, RemoveEdge, Submit
          final FXMLLoader loader = new FXMLLoader(resource);
          try {
            changeBox = loader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          TextField startNodeIdText =
              (TextField) ((Pane) (changeBox.getChildren().get(0))).getChildren().get(1);
          TextField endNodeIdText =
              (TextField) ((Pane) (changeBox.getChildren().get(1))).getChildren().get(1);

          startNodeIdText.setText("" + startNode.getId());
          endNodeIdText.setText("" + endNode.getId());

          MFXButton removeEdgeButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(2))).getChildren().get(0);
          MFXButton submitButton =
              (MFXButton) ((Pane) (changeBox.getChildren().get(2))).getChildren().get(1);
          removeEdgeButton.setOnMouseClicked(removeEdge);
          submitButton.setOnMouseClicked(saveEdgeChanges);

          PopOver pop = new PopOver(changeBox);
          //          pop.show(p);
          pop.show(path);
        }
      };
  EventHandler<MouseEvent> removeEdge =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          System.out.println("REM");

          // Only the Node ID is important for Deletion
          Edge e = new Edge(startNode.getId(), endNode.getId());

          try {
            DataManager.deleteEdge(e);
            map.setCurrentDisplayFloor(map.getCurrentDisplayFloor(), isMapPage);
          } catch (SQLException ex) {
            System.out.println(ex);
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        }
      };

  EventHandler<MouseEvent> saveEdgeChanges =
      new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          System.out.println("SEC");

          MFXButton SubmitButton = ((MFXButton) event.getSource());
          VBox v = (VBox) ((SubmitButton.getParent()).getParent());

          int startId = -1;
          int endId = -1;

          TextField startNodeIdText =
              (TextField) ((Pane) (v.getChildren().get(0))).getChildren().get(1);
          TextField endNodeIdText =
              (TextField) ((Pane) (v.getChildren().get(1))).getChildren().get(1);

          if (!startNodeIdText.getText().equals("")) {
            startId = Integer.parseInt(startNodeIdText.getText());
          }
          if (!endNodeIdText.getText().equals("")) {
            endId = Integer.parseInt(endNodeIdText.getText());
          }

          if (!(startId == -1 || endId == -1)) {
            Edge e = new Edge(startNode.getId(), endNode.getId());
            e.setStartNodeID(startId);
            e.setEndNodeID(endId);
            //            Edge e = new Edge(startId, endId);

            try {
              DataManager.syncEdge(e);
              map.setCurrentDisplayFloor(map.getCurrentDisplayFloor(), isMapPage);
            } catch (SQLException ex) {
              System.out.println(ex);
              //            throw new RuntimeException(ex);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          } else {
            System.out.println("Not enough info to make an edge");
          }
        }
      };
}
