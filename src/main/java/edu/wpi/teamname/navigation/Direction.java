package edu.wpi.teamname.navigation;

public enum Direction {
  STRAIGHT("Straight"),
  LEFT("Left"),
  RIGHT("Right"),
  BACK("Back"),
  UP("Up"),
  DOWN("Down");

  private final String type;

  Direction(String type) {
    this.type = type;
  }

  public String getString() {
    return type;
  }
}
