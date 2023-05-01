package edu.wpi.teamname;

import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import edu.wpi.teamname.extras.Language;
import edu.wpi.teamname.navigation.LocationName;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public class GlobalVariables {
  // language is set to English by default
  @Getter @Setter public static Property<Language> b = new SimpleObjectProperty<>(Language.ENGLISH);
  @Getter @Setter private static String[] args;
  @Getter @Setter private static boolean futureMovesPressed = false;
  @Getter @Setter private static boolean activeRequestsPressed = false;
  @Getter @Setter private static boolean doneRequestsPressed = false;
  @Getter @Setter private static Color borderColor = Color.web("012D5A"); // Color.web("33567A");
  @Getter @Setter private static Color insideColor = Color.web("35A7FF"); // Color.web("2FA7B0");
  @Getter @Setter private static Color insideBlue = Color.web("35A7FF");
  @Getter @Setter private static Color insideGreen = Color.web("6BD425");
  @Getter @Setter private static Color insideOrange = Color.web("EDB230");
  @Getter @Setter private static Color insideYellow = Color.web("FEEA00");
  @Getter @Setter private static Color insideRed = Color.web("ED6A5A");
  @Getter @Setter private static Color insideMagenta = Color.web("8D5A97");
  @Getter @Setter private static Color insideWhite = Color.web("FFFFFF");

  @Getter @Setter private static float circleR = 10.0f;
  @Getter @Setter private static float lineT = 10.0f;
  @Getter @Setter private static int strokeThickness = 2;
  @Getter @Setter private static Color labelColor = new Color(.835, .89, 1, 1);
  @Getter @Setter private static Color labelTextColor = new Color(0, .106, .231, 1);
  @Getter @Setter private static Timestamp today = new Timestamp(System.currentTimeMillis());
  @Getter @Setter private static HashMap<Integer, ArrayList<LocationName>> hMap;
  //  @Getter @Setter private static Color labelColor = new Color(.835, .89, 1, 1);
  //  @Getter @Setter private static Color labelTextColor = new Color(0, .106, .231, 1);

  private static final Employee dummyEmployee =
      new Employee(
          "dummyU",
          "dummyP",
          -1,
          "dummmyF",
          "dummyL",
          ClearanceLevel.GUEST,
          EmployeeType.NONE,
          false);
  @Getter @Setter private static Employee currentUser = dummyEmployee;
  @Getter @Setter private static Screen currentScreen = Screen.HOME;
  @Getter @Setter private static Screen previousScreen = Screen.HOME;

  @Getter @Setter private static BooleanProperty darkMode = new SimpleBooleanProperty(true) {};

  /** Sets the current user to be null indicating no user is logged in */
  public static void logOut() {
    // dummyEmployee.setType(new ArrayList<>());
    currentUser = dummyEmployee;
  }

  /**
   * Checks whether the current logged-in user is the given type
   *
   * @param type the type that is being checked if the user is
   * @return whether the user is of that type
   */
  public static boolean userIsType(EmployeeType type) {
    return currentUser.getType().equals(type);
  }

  /**
   * Checks whether the current logged-in user is the given level
   *
   * @param level the level that is being checked if the user is
   * @return whether the user is of that level
   */
  public static boolean userIsClearanceLevel(ClearanceLevel level) {
    return currentUser.getLevel().equals(level);
  }
}
