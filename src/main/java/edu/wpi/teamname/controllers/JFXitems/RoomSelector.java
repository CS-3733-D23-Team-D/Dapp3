package edu.wpi.teamname.controllers.JFXitems;

import edu.wpi.teamname.controllers.ConferenceController;
import edu.wpi.teamname.extras.SFX;
import edu.wpi.teamname.extras.Sound;
import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.requestitem.ConfRoom;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

/** Custom selector item for reserving conference rooms */
public class RoomSelector extends BorderPane {
  @Getter private ConfRoom room;
  private Label name;
  @Setter private Timestamp date;
  @Getter HBox hBox;

  ConferenceController controller;

  ObservableList<SelectorButton> buttons;

  @Getter private static int slots = 22;

  @Getter @Setter private int start = 0;
  @Getter @Setter private int end = 0;
  @Getter @Setter int selected = 0;

  public RoomSelector(ConfRoom room, ConferenceController controller, Timestamp date) {
    this.date = date;
    this.room = room;
    this.controller = controller;
    System.out.println("New Room Selector: " + room.getLocationName());
    //    this.date = date;
    buttons = FXCollections.observableArrayList();
    initialize();
  }

  private void initialize() {
    // HBox.setHgrow(this, Priority.ALWAYS);
    setMinHeight(50);
    setMinWidth(400);
    //    this.getStylesheets().add("../stylesheets/Colors/lightTheme.css");
    //    this.getStyleClass().add("surface-container");
    this.name = new Label(this.room.getLocationName().split(",")[0]);
    this.setLeft(name);
    this.name.setPadding(new Insets(8, 12, 8, 12));
    this.name.getStyleClass().add("primary-container");
    // this.name.setStyle("-fx-background-color: #D5E3FF; -fx-text-fill: #001B3B");
    //    this.setStyle("-fx-background-color: #D5E3FF; -fx-border-color: #6F797A");
    this.name.setMinHeight(75);
    this.name.setMinWidth(260);
    this.getStyleClass().add("primary-container");

    for (int i = 0; i < slots; i++) {
      buttons.add(new SelectorButton(i, this));
    }
    this.hBox = new HBox();
    this.setCenter(hBox);
    this.hBox.getChildren().addAll(buttons);
    handleExistingReservations();
  }

  void handleButtonClick(int id) {
    Sound.playSFX(SFX.BUTTONCLICK);
    System.out.println(String.valueOf(room.getRoomID()) + ": " + String.valueOf(selected));
    if (selected == 0) {
      setSelect(true, id);
      start = id;
      end = id + 1;
      setStartEnd();
      selected = 1;
      controller.setActiveSelector(this);
    } else if (selected == 1) {
      if (id == start) {
        selected = 0;
        setSelect(false, id);
      } else {
        if (id > start) {
          //          start = end;
          end = id + 1;
          setSelect(false, start);
        } else {
          //          end = start;
          start = id;
          setSelect(false, end);
        }
        setStartEnd();
        setAllInRange(true);
        selected = 2;
      }

      controller.setActiveSelector(this);
    } else {
      setAllInRange(false);
      selected = 0;
      handleButtonClick(id);
      //      controller.setStartBox(idToTime(start));
      //      controller.setEndBox(idToTime(end));
    }
  }

  private void setStartEnd() {
    controller.setFromSelector(true);
    System.out.println();
    System.out.println(start);
    System.out.println(end);
    System.out.println();
    controller.setStartBox(idToTime(start));
    controller.setEndBox(idToTime(end));
    controller.setFromSelector(false);
  }

  void setSelect(boolean select, int id) {
    if (select) {
      if (buttons.get(id).getStatus() == RoomStatus.AVAILABLE
          || buttons.get(id).getStatus() == RoomStatus.SELECTED) {
        buttons.get(id).setStatus(RoomStatus.SELECTED);
      } else {
        buttons.get(id).setStatus(RoomStatus.ERROR);
      }
    } else {
      if (buttons.get(id).getStatus() == RoomStatus.SELECTED
          || buttons.get(id).getStatus() == RoomStatus.AVAILABLE) {
        buttons.get(id).setStatus(RoomStatus.AVAILABLE);
      } else {
        buttons.get(id).setStatus(RoomStatus.BOOKED);
      }
    }
  }

  public void setAllInRange(boolean select) {
    //    if (start == end) {
    //      end++;
    //    } else if (start > end) {
    //      int tStart = start;
    //      start = end;
    //      end = tStart;
    //    }
    for (int i = start; i < end; i++) {
      setSelect(select, i);
    }
  }

  private void handleExistingReservations() {
    int oStart;
    int oEnd;
    for (ConfReservation reservation : room.getReservations()) {
      //      System.out.println(reservation.getDateBook());
      //      System.out.println(date);
      if (reservation.getDateBook().getDate() == date.getDate()
          && reservation.getDateBook().getMonth() == date.getMonth()) {
        System.out.println("Reservation Found");
        oStart = timeToID(reservation.getStartTime());
        System.out.println(reservation.getStartTime());
        System.out.println(oStart);
        oEnd = timeToID(reservation.getEndTime());
        System.out.println(reservation.getEndTime());
        System.out.println(oEnd);
        for (int i = oStart; i < oEnd + 1; i++) {
          buttons.get(i).setStatus(RoomStatus.BOOKED);
        }
      }
    }
  }

  public static int timeToID(String time) {
    DateFormat format = new SimpleDateFormat("HH:mm");
    Time tim;
    try {
      tim = new Time(format.parse(time).getTime());
    } catch (ParseException e) {
      return -1;
    }
    int outID = 0;
    outID += 2 * (tim.getHours() - 7);
    if (tim.getMinutes() > 20) {
      outID++;
    }
    return outID;
  }

  String idToTime(int id) {
    DecimalFormat format = new DecimalFormat("00");
    String hour = format.format(id / 2 + 7);
    String min = format.format((id % 2) * 30);
    return hour + ":" + min;
  }

  public ArrayList<String> getTimes() {
    ArrayList<String> times = new ArrayList<>();
    times.add(idToTime(start));
    times.add(idToTime(end - 1));
    return times;
  }
}
