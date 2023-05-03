package edu.wpi.teamname;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class ScreenSaver extends Application {
  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("DVD Screensaver");
    Group root = new Group();
    Scene scene = new Scene(root, 1000, 600, Color.BLACK);
    File p = new File("res/Purple.png");
    final Image purple = new Image(p.toURI().toString());
    File r = new File("res/Red.png");
    final Image red = new Image(r.toURI().toString());
    File y = new File("res/Yellow.png");
    final Image yellow = new Image(y.toURI().toString());
    ArrayList<ImageView> Logos = new ArrayList<>();
    ArrayList<Integer> counts = new ArrayList<>();
    ArrayList<Double> dxs = new ArrayList<>();
    ArrayList<Double> dys = new ArrayList<>();
    ImageView firstLogo = new ImageView(purple);
    firstLogo.setX(100);
    firstLogo.setY(200);
    firstLogo.setFitWidth(137);
    firstLogo.setFitHeight(66);
    Logos.add(firstLogo);
    counts.add(0);
    dxs.add(2.0);
    dys.add(3.0);
    scene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        ImageView newLogo = new ImageView(purple);
        newLogo.setX(event.getX());
        newLogo.setY(event.getY());
        newLogo.setFitWidth(137);
        newLogo.setFitHeight(66);
        Logos.add(newLogo);
        counts.add(0);
        dxs.add(2.0);
        dys.add(3.0);
        root.getChildren().add(newLogo);
      }
    });
    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {

      @Override
      public void handle(ActionEvent event) {

        for (int i = 0; i < Logos.size(); i ++) {
          double dx = dxs.get(i);
          double dy = dys.get(i);
          double x = Logos.get(i).getX() + dx;
          double y = Logos.get(i).getY() + dy;

          if (x + 137 > 1000 || x < 0) {
            dxs.set(i, -dx);
            counts.set(i, (counts.get(i) + 1) % 3);
          }

          if (y + 66 > 600 || y < 0) {
            dys.set(i, -dy);
            counts.set(i, (counts.get(i) + 1) % 3);
          }

          if (counts.get(i) == 0) {
            Logos.get(i).setImage(purple);
          } else if (counts.get(i) == 1) {
            Logos.get(i).setImage(yellow);
          } else if (counts.get(i) == 2) {
            Logos.get(i).setImage(red);
          }

          Logos.get(i).setX(x);
          Logos.get(i).setY(y);
        }
      }
    }));
    root.getChildren().addAll(Logos);
    primaryStage.setScene(scene);
    primaryStage.show();
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }
}
