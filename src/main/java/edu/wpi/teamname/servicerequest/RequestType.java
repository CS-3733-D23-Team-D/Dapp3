package edu.wpi.teamname.servicerequest;

public enum RequestType {
  MEAL("MEAL"),
  FLOWER("FLOWER"),
  OFFICESUPPLY("OFFICESUPPLY"),
  FURNITURE("FURNITURE"),
  MEDICALSUPPLY("MEDICALSUPPLY");

  private final String type;

  RequestType(String type) {
    this.type = type;
  }

  public String getString() {
    return type;
  }

  @Override
  public String toString() {
    if (this.getString().equals("MEAL")) {
      return "Meal Request";
    } else if (this.getString().equals("FLOWER")) {
      return "Flower Request";
    } else if (this.getString().equals("OFFICESUPPLY")) {
      return "Office Supply Request";
    } else if (this.getString().equals("FURNITURE")) {
      return "Furniture Request";
    } else if (this.getString().equals("MEDICALSUPPLY")) {
      return "Medical Supply Request";
    } else {
      return "";
    }
  }
}
