package edu.wpi.teamname;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import edu.wpi.teamname.navigation.*;
import edu.wpi.teamname.servicerequest.ItemsOrdered;
import edu.wpi.teamname.servicerequest.RequestType;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.Status;
import edu.wpi.teamname.servicerequest.requestitem.*;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;

public class toStringTests {

  @Test
  public void employeeToString() {
    Employee employee =
        new Employee(
            "username",
            "password",
            1,
            "First",
            "Last",
            ClearanceLevel.STAFF,
            EmployeeType.DELIVERY,
            false);
    assertEquals(
        "Employee{username='username', password='password', employeeID=1, firstName='First', lastName='Last', type=[JANITOR, STAFF]}",
        employee.toString());
  }

  @Test
  public void edgeToString() {
    Edge edge = new Edge(1, 2);
    assertEquals("StartNodeID: 1 EndNodeID: 2", edge.toString());
  }

  @Test
  public void locationNameToString() {
    LocationName locationName = new LocationName("long", "short", "TYPE");
    assertEquals("[long, short, TYPE]", locationName.toString());
  }

  @Test
  public void moveToString() {
    Move move = new Move(1, "long", new Timestamp(1));
    assertEquals("[1, long, 1969-12-31 19:00:00.001]", move.toString());
  }

  @Test
  public void nodeToString() {
    Node node = new Node(1, 24, 25, "floor", "building");
    assertEquals(
        "NodeID:1 Xcord:24 Ycord:25 Heu:0.0 Neighbors: Floor:floor Building:building",
        node.toString());
  }

  @Test
  public void roomToString() {
    Room room = new Room(1, "long", new Timestamp(1), 24, 25, "floor", "building", "short", "type");
    assertEquals(
        "Room{nodeID=1, longName='long', date=1969-12-31 19:00:00.001, xcoord=24, ycoord=25, floor='floor', building='building', shortName='short', nodeType='type'}",
        room.toString());
  }

  @Test
  public void flowerToString() {
    Flower flower = new Flower(1000, "name", 2.3234F, "cat", "plurple");
    assertEquals("[1000, name, 2.32, cat, plurple]", flower.toString());
  }

  @Test
  public void furnitureToString() {
    Furniture furniture = new Furniture(1401, "namef", 24.0F, "sofa", "cat", "plurple");
    assertEquals("[1401, namef, 24.0, sofa, cat, plurple]", furniture.toString());
  }

  @Test
  public void mealToString() {
    Meal meal = new Meal(1101, "burgee", 0F, "linner", "american");
    assertEquals("[1101, burgee, 0.0, linner, american]", meal.toString());
  }

  @Test
  public void officeSupplyToString() {
    OfficeSupply officeSupply = new OfficeSupply(1301, "pencil", 1000.0F, "writing", true);
    assertEquals("[1301, pencil, 1000.0, writing, true]", officeSupply.toString());
  }

  @Test
  public void itemsOrderedToString() {
    ItemsOrdered itemsOrdered = new ItemsOrdered(1, 2, 3);
    assertEquals("[1 2 3]", itemsOrdered.toString());
  }

  @Test
  public void serviceRequest1ToString() {
    ServiceRequest serviceRequest =
        new ServiceRequest(
            1,
            "staff",
            "patient",
            "location",
            new Timestamp(1),
            new Timestamp(2),
            Status.DONE,
            "madeBy",
            RequestType.FLOWER);
    assertEquals(
        "[1, staff, patient, location, 1969-12-31 19:00:00.001, 1969-12-31 19:00:00.002, DONE, madeBy, Flower Request]",
        serviceRequest.getDetails());
  }

  @Test
  public void serviceRequest2ToString() {
    ServiceRequest serviceRequest =
        new ServiceRequest(
            2,
            "staff",
            "patient",
            "location",
            new Timestamp(1),
            new Timestamp(3),
            Status.PROCESSING,
            "madeBy",
            RequestType.OFFICESUPPLY);
    assertEquals(
        "[2, staff, patient, location, 1969-12-31 19:00:00.001, 1969-12-31 19:00:00.003, PROCESSING, madeBy, Office Supply Request]",
        serviceRequest.getDetails());
    assertEquals(Status.DONE.getStatusString(), "DONE");
  }

  @Test
  public void serviceRequest3ToString() {
    ServiceRequest serviceRequest =
        new ServiceRequest(
            5,
            "staff1",
            "patient",
            "location",
            new Timestamp(1),
            new Timestamp(2),
            Status.BLANK,
            "madeBy",
            RequestType.MEAL);
    assertEquals(
        "[5, staff1, patient, location, 1969-12-31 19:00:00.001, 1969-12-31 19:00:00.002, BLANK, madeBy, Meal Request]",
        serviceRequest.getDetails());
  }

  @Test
  public void serviceRequest4ToString() {
    ServiceRequest serviceRequest =
        new ServiceRequest(
            50,
            "staff1",
            "patient",
            "location",
            new Timestamp(1),
            new Timestamp(2),
            Status.BLANK,
            "madeBy",
            RequestType.FURNITURE);
    assertEquals(
        "[50, staff1, patient, location, 1969-12-31 19:00:00.001, 1969-12-31 19:00:00.002, BLANK, madeBy, Furniture Request]",
        serviceRequest.getDetails());
  }
}
