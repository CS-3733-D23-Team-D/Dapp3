package edu.wpi.teamname;

public enum Screen {
  // Enum Constants Calling the Enum Constructor
  ROOT("views/Root.fxml"),
  TEMPLATE("views/Template.fxml"),
  HOME("views/Home.fxml"),
  SERVICE_REQUEST("views/ServiceRequest2.fxml"),
  MAP("views/Map.fxml"),

  TEST("views/Test.fxml"),
  SIGNAGE("views/SignageLevels.fxml"),

  //  REQ_MENU("views/ServiceRequestMenu.fxml"),
  LOGIN("views/Login.fxml"),
  //  SIGNAGE("views/SignageLevels.fxml"),
  SERVICE_REQUEST_VIEW("views/ServiceRequestView2.fxml"),

  MAP_EDIT("views/MapEdit.fxml"),

  MOVE_TABLE("views/MoveTable.fxml"),
  EMPLOYEE_TABLE("views/EmployeeTable.fxml"),
  LOADING("views/LoadingScreen.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
