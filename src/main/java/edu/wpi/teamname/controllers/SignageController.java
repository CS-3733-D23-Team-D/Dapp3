package edu.wpi.teamname.controllers;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.navigation.Signage;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.swing.text.html.ImageView;

public class SignageController {
    @FXML VBox arrowVbox;
    @FXML VBox contentVBox;
    @FXML Label signLabel;
    @FXML ImageView arrowPic;
    @FXML ComboBox<Integer> KskBox;
    @FXML ObservableList<Integer> kioskList;
    @FXML DatePicker dateChos;
    @FXML MFXButton submit;
  private static Timestamp dateChosen;
  private static ArrayList<String> signsForDate = new ArrayList<>();

  @FXML
  public void initialize() throws SQLException {
    //labelLine.setText("");
    kioskList = FXCollections.observableArrayList();
    kioskList.add(null);
    KskBox.setItems(kioskList);
    dateChos
        .valueProperty()
        .addListener(
            (t, o, n) -> {
              if (dateChos.getValue() != null) {
                System.out.println(
                    dateChos
                        .getValue()
                        .atTime(12, 0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")));
                dateChosen =
                    Timestamp.valueOf(
                        dateChos
                            .getValue()
                            .atTime(12, 0)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnn")));
                try {
                  kioskList = FXCollections.observableArrayList(DataManager.getKiosks(dateChosen));
                  KskBox.setItems(kioskList);
                } catch (SQLException e) {
                  System.out.println(e);
                }
              }
            });

    KskBox.setOnAction(
        event -> {
          if (KskBox.getValue() != null) {
            try {
                signsForDate = DataManager.getSignage(KskBox.getValue(), dateChosen);
            } catch (SQLException e) {
              System.out.println(e);
            }
          }
        });

    submit.setOnMouseClicked(
        event -> {
          System.out.println(signsForDate);
          for (int i = 0; i < signsForDate.size(); i++) {
            //arrowVbox.getChildren().add(signForDate(i));

            setArrowType(signsForDate.get(i));
            //contentVBox.getChildren().add(arrowPic);
          }
        });
  }

  private void setArrowType(String sign){
      int i = sign.indexOf("|");
      String dir = sign.substring(0,i);
          switch (dir) {
          case "UP":
            //arrowPic = up;
            break;
          case "LEFT":
              //arrowPic = left;
            break;
          case "DOWN":
              //arrowPic = down;
            break;
          case "RIGHT":
              //arrowPic = right;
            break;
          case "STOP HERE":
              //arrowPic = stop;
            break;
          case "STRAIGHT":
              //arrowPic = straight;
            break;
          default:
            System.out.println("Direction error");
        }
  }
}
