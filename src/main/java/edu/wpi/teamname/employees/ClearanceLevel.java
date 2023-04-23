package edu.wpi.teamname.employees;

public enum ClearanceLevel {
  ADMIN("ADMIN"),
  STAFF("STAFF"),
  GUEST("GUEST"),
  ;

  private final String level;

  ClearanceLevel(String level) {
    this.level = level;
  }

  public String getString() {
    return level;
  }

  public static String[] formattedValues() {
    String[] output = new String[values().length];
    String string = "";
    for (int i = 0; i < values().length; i++) {
      string = values()[i].getString();
      output[i] = string.substring(0, 1) + string.substring(1).toLowerCase();
    }
    return output;
  }
}
