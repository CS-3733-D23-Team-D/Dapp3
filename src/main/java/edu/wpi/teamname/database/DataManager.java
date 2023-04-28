package edu.wpi.teamname.database;

import edu.wpi.teamname.alerts.Alert;
import edu.wpi.teamname.database.interfaces.ConfReservationDAO;
import edu.wpi.teamname.database.interfaces.SignageDAO;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import edu.wpi.teamname.navigation.*;
import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.ItemsOrdered;
import edu.wpi.teamname.servicerequest.ServiceRequest;
import edu.wpi.teamname.servicerequest.requestitem.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.*;
import lombok.Getter;

public class DataManager {
  private static Connection connection;

  @Getter
  private static String DB_URL =
      "jdbc:postgresql://database.cs.wpi.edu:5432/teamddb?currentSchema=\"teamD\"";

  @Getter private static String DB_PASSWORD = "teamd40";
  @Getter private static String DB_USER = "teamd";

  /**
   * Main function to connect to the database
   *
   * @return a Connection to a database
   */
  public static Connection DbConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      System.out.print("--- Connecting To Database... ---");
      try {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println(" Successfully connected to database!");
      } catch (SQLException e) {
        System.out.println(" Connection Failed! Check output console");
        e.printStackTrace();
        connection = null;
      } catch (ClassNotFoundException e) {
        System.out.println(e);
        throw new RuntimeException(e);
      }
    }
    return connection;
  }
  // ----------------Signage---------

  /**
   * Gets the kiosks for specific date
   *
   * @param date
   * @return list of Integers which are kiosk id's
   * @throws SQLException
   */
  public static ArrayList<Integer> getKiosks(Timestamp date) throws SQLException {
    SignageDAO sign = new SignageDAOImpl();
    return sign.getKiosks(date);
  }

  /**
   * Gets the specific signage based on kiosk and date
   *
   * @param kiosk
   * @param date
   * @return list of strings which is the signage
   * @throws SQLException
   */
  public static ArrayList<String> getSignage(int kiosk, Timestamp date) throws SQLException {
    SignageDAO sign = new SignageDAOImpl();
    return sign.getSignage(kiosk, date);
  }

  // ----------------Unique FUNCTIONS-------------

  /**
   * Gets the specific reservations based off the conference room
   *
   * @param confrom
   * @return list of reservations
   * @throws SQLException
   */
  public static ArrayList<ConfReservation> getResForRoom(ConfRoom confrom) throws SQLException {
    ConfReservationDAO confRes = new ConfReservationDAOImpl();
    return confRes.getResForRoom(confrom);
  }

  /**
   * adds a reservation to conferenceReservation table
   *
   * @param res
   * @throws SQLException
   */
  public static void makeReservation(ConfReservation res) throws SQLException {
    addConfReservation(res);
  }

  // ----------------Conference Service Req helper functinos-------------

  /**
   * sets the reservation ID correctly so there is no repeated one
   *
   * @return int which is the new resID
   * @throws SQLException
   */
  public static int setResID() throws SQLException {
    ConfReservationDAO confRes = new ConfReservationDAOImpl();
    return confRes.setResID();
  }

  /**
   * Gets the room ID based off the specific room
   *
   * @param room
   * @return int which is the roomID
   * @throws SQLException
   */
  public static int getRoomID(String room) throws SQLException {
    ConfRoomDAOImpl confRoom = new ConfRoomDAOImpl();
    return confRoom.getRoomID(room);
  }

  /**
   * gets the number of seats based off the room
   *
   * @param room
   * @return int which is seats
   * @throws SQLException
   */
  public static int getSeats(String room) throws SQLException {
    ConfRoomDAOImpl confRoom = new ConfRoomDAOImpl();
    return confRoom.getSeats(room);
  }

  /**
   * gets list of all the conference buildings
   *
   * @return list of strings
   * @throws SQLException
   */
  public static ArrayList<String> getConfBuildings() throws SQLException {
    ConfRoomDAOImpl confRoom = new ConfRoomDAOImpl();
    return confRoom.getConfBuildings();
  }

  /**
   * gets the conference rooms based on selected building
   *
   * @param building
   * @return list of strings
   * @throws SQLException
   */
  public static ArrayList<String> getConfRooms(String building) throws SQLException {
    ConfRoomDAOImpl confRoom = new ConfRoomDAOImpl();
    return confRoom.getConfRooms(building);
  }

  /**
   * fills the conference rooms table based on rest of nodes
   *
   * @throws SQLException
   */
  public static void refreshConfRooms() throws SQLException {
    ConfRoomDAOImpl confRoom = new ConfRoomDAOImpl();
    confRoom.refreshConfRooms();
  }

  /**
   * gets the items for a specific request
   *
   * @param reqID
   * @return
   * @throws SQLException
   */
  public static ArrayList<ItemsOrdered> getItemsFromReq(int reqID) throws SQLException {
    ItemsOrderedDAOImpl itemsOrderd = new ItemsOrderedDAOImpl();
    return itemsOrderd.getItemsFromReq(reqID);
  }
  // -------------------------------------------------------------------------

  /** Sets database connection parameters to connect to the AWS RDS */
  public static void connectToAWS() throws SQLException {
    configConnection(
        "jdbc:postgresql://teamddb3.cwgmodw6cdg6.us-east-1.rds.amazonaws.com:5432/postgres",
        "superuser",
        "password");
  }

  /** Sets database connection parameters to connect to the WPI client-side server */
  public static void connectToWPI() throws SQLException {
    DataManager.configConnection(
        "jdbc:postgresql://database.cs.wpi.edu:5432/teamddb?currentSchema=\"teamD\"",
        "teamd",
        "teamd40");
  }
  /**
   * Main function to create all Database tables if they don't already exist
   *
   * @param createTableQuery a String that reps the query to create a table
   * @param tableName a String that reps the name of the table being checked
   */
  public static void createTableIfNotExists(String tableName, String createTableQuery)
      throws SQLException {
    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    DatabaseMetaData dbm = connection.getMetaData();
    ResultSet rs = dbm.getTables(null, null, tableName, null);
    if (!rs.next()) { // table does not exist
      Statement statement = connection.createStatement();
      statement.executeUpdate(createTableQuery);
      statement.close();
    }
    rs.close();
  }

  /**
   * * Updates the connection arguements
   *
   * @param url
   * @param username
   * @param password
   */
  public static void configConnection(String url, String username, String password)
      throws SQLException {
    DB_URL = url;
    DB_USER = username;
    DB_PASSWORD = password;
    DbConnection().close();
    tryToCreateAllTables();
  }

  // ------------------------DAO Methods------------------------

  /**
   * This method updates an existing Move object in the "Move" table in the database with the new
   * Move object.
   *
   * @param move the new Move object to be updated in the "Move" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncMove(Move move) throws SQLException {
    MoveDAOImpl moveDAO = new MoveDAOImpl();
    moveDAO.sync(move);
  }

  public static void syncAlert(Alert alert) throws SQLException {
    AlertDAOImpl alertDAO = new AlertDAOImpl();
    alertDAO.sync(alert);
  }

  /**
   * This method updates an existing Node object in the "Node" table in the database with the new
   * Node object.
   *
   * @param node the new Node object to be updated in the "Node" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncNode(Node node) throws SQLException {
    NodeDAOImpl nodeDAO = new NodeDAOImpl();
    nodeDAO.sync(node);
  }

  /**
   * This method updates an existing Edge object in the "Edge" table in the database with the new
   * Edge object.
   *
   * @param edge the new Edge object to be updated in the "Edge" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncEdge(Edge edge) throws SQLException {
    EdgeDAOImpl edgeDAO = new EdgeDAOImpl();
    edgeDAO.sync(edge);
  }

  /**
   * This method updates an existing ItemsOrdered object in the "ItemsOrdered" table in the database
   * with the new ItemsOrdered object.
   *
   * @param itemsOrdered the new ItemsOrdered object to be updated in the "ItemsOrdered" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncItemsOrdered(ItemsOrdered itemsOrdered) throws SQLException {
    ItemsOrderedDAOImpl itemsOrderedDao = new ItemsOrderedDAOImpl();
    itemsOrderedDao.sync(itemsOrdered);
  }

  /**
   * This method updates an existing ServiceRequest object in the "ServiceRequest" table in the
   * database with the new ServiceRequest object.
   *
   * @param serviceRequest the new ServiceRequest object to be updated in the "ServiceRequest" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncServiceRequest(ServiceRequest serviceRequest) throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    serviceRequestDAO.sync(serviceRequest);
  }

  /**
   * This method updates an existing Employee object in the "Employee" table in the database with
   * the new Employee object.
   *
   * @param employee the new Employee object to be updated in the "Employee" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncEmployee(Employee employee) throws SQLException {
    EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    employeeDAO.sync(employee);
  }

  /**
   * This method updates an existing Flower object in the "Flower" table in the database with the
   * new Flower object.
   *
   * @param flower the new Flower object to be updated in the "Flower" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncFlower(Flower flower) throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    flowerDAO.sync(flower);
  }

  /**
   * This method updates an existing Meal object in the "Meal" table in the database with the new
   * Meal object.
   *
   * @param meal the new Meal object to be updated in the "Meal" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncMeal(Meal meal) throws SQLException {
    MealDAOImpl mealDAO = new MealDAOImpl();
    mealDAO.sync(meal);
  }

  /**
   * This method updates an existing LocationName object in the "LocationName" table in the database
   * with the new LocationName object.
   *
   * @param locationName the new LocationName object to be updated in the "LocationName" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncLocationName(LocationName locationName) throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    locationNameDAO.sync(locationName);
  }

  /**
   * This method updates an existing Furniture object in the "Furniture" table in the database with
   * the new Furniture object.
   *
   * @param furniture the new Flower object to be updated in the "Furniture" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncFurniture(Furniture furniture) throws SQLException {
    FurnitureDAOImpl furnitureDAO = new FurnitureDAOImpl();
    furnitureDAO.sync(furniture);
  }

  /**
   * This method updates an existing OfficeSupply object in the "OfficeSupply" table in the database
   * with the new OfficeSupply object.
   *
   * @param officeSupply the new OfficeSupply object to be updated in the "OfficeSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncOfficeSupply(OfficeSupply officeSupply) throws SQLException {
    OfficeSupplyDAOImpl officeSupplyDAO = new OfficeSupplyDAOImpl();
    officeSupplyDAO.sync(officeSupply);
  }
  /**
   * This method updates an existing MedicalSupply object in the "MedicalSupply" table in the
   * database with the new MedicalSupply object.
   *
   * @param medicalSupply the new MedicalSupply object to be updated in the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncMedicalSupply(MedicalSupply medicalSupply) throws SQLException {
    MedicalSupplyDAOImpl medicalSupplyDAO = new MedicalSupplyDAOImpl();
    medicalSupplyDAO.sync(medicalSupply);
  }
  /**
   * This method updates an existing MedicalSupply object in the "MedicalSupply" table in the
   * database with the new MedicalSupply object.
   *
   * @param confRoom the new MedicalSupply object to be updated in the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncConfRoom(ConfRoom confRoom) throws SQLException {
    ConfRoomDAOImpl confRoomDAO = new ConfRoomDAOImpl();
    confRoomDAO.sync(confRoom);
  }
  /**
   * This method updates an existing ConfReservation object in the "ConfReservation" table in the
   * database with the new ConfReservation object.
   *
   * @param confReservation the new ConfReservation object to be updated in the "ConfReservations"
   *     table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncConfReservation(ConfReservation confReservation) throws SQLException {
    ConfReservationDAOImpl confReservationDAO = new ConfReservationDAOImpl();
    confReservationDAO.sync(confReservation);
  }
  /**
   * This method updates an existing Signage object in the "Signage" table in the database with the
   * new Signage object.
   *
   * @param signage the new Signage object to be updated in the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncSignage(Signage signage) throws SQLException {
    SignageDAOImpl signageDAO = new SignageDAOImpl();
    signageDAO.sync(signage);
  }

  /**
   * This method updates an existing PathMessage object in the "PathMessages" table in the database
   * with the new PathMessage object.
   *
   * @param pm the new MedicalSupply object to be updated in the "PathMessages" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void syncPM(PathMessage pm) throws SQLException {
    PathMessageDAOImpl pmDAO = new PathMessageDAOImpl();
    pmDAO.sync(pm);
  }
  /**
   * This method returns the employee type of a user
   *
   * @param username the Employees username
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<EmployeeType> getEmployeeType(String username) {
    EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    return getEmployeeType(username);
  }

  // --------------------------------ADDS----------------------------------

  /**
   * This method adds a new Move object to the "Move" table in the database.
   *
   * @param move the Move object to be added to the "Move" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addMoves(Move move) throws SQLException {
    MoveDAOImpl moveDAO = new MoveDAOImpl();
    moveDAO.add(move);
  }

  /**
   * This method adds a new Node object to the "Node" table in the database.
   *
   * @param node the Node object to be added to the "Node" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addNode(Node node) throws SQLException {
    NodeDAOImpl nodeDAO = new NodeDAOImpl();
    nodeDAO.add(node);
  }

  /**
   * This method adds a new Edge object to the "Edge" table in the database.
   *
   * @param edge the Edge object to be added to the "Edge" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addEdge(Edge edge) throws SQLException {
    EdgeDAOImpl edgeDAO = new EdgeDAOImpl();
    edgeDAO.add(edge);
  }

  /**
   * This method adds a new ItemsOrdered object to the "ItemsOrdered" table in the database.
   *
   * @param itemsOrdered the ItemsOrdered object to be added to the "ItemsOrdered" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addItemsOrdered(ItemsOrdered itemsOrdered) throws SQLException {
    ItemsOrderedDAOImpl itemsOrderedDAO = new ItemsOrderedDAOImpl();
    itemsOrderedDAO.add(itemsOrdered);
  }

  /**
   * Adds a service request to the database, along with the corresponding order of items.
   *
   * @param serviceRequest The service request to be added to the database
   * @throws SQLException If an error occurs while accessing the database
   */
  public static void addServiceRequest(ServiceRequest serviceRequest) throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    serviceRequestDAO.add(serviceRequest);
  }

  /**
   * This method adds a new Employee object to the "Employee" table in the database.
   *
   * @param employee the Employee object to be added to the "Employee" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addEmployee(Employee employee) throws SQLException {
    EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    employeeDAO.add(employee);
  }

  /**
   * This method adds a new Flower object to the "Flowers" table in the database.
   *
   * @param flower the Flower object to be added to the "Flower" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addFlower(Flower flower) throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    flowerDAO.add(flower);
  }

  /**
   * This method adds a new Meal object to the "Meal" table in the database.
   *
   * @param meal the Meal object to be added to the "Meal" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addMeal(Meal meal) throws SQLException {
    MealDAOImpl mealDAO = new MealDAOImpl();
    mealDAO.add(meal);
  }

  /**
   * This method adds a new LocationName object to the "LocationName" table in the database.
   *
   * @param locationName the LocationName object to be added to the "LocationName" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addLocationName(LocationName locationName) throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    locationNameDAO.add(locationName);
  }

  /**
   * This method adds a new Furniture object to the "Furniture" table in the database.
   *
   * @param furniture the Furniture object to be added to the "Furniture" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addFurniture(Furniture furniture) throws SQLException {
    FurnitureDAOImpl furnitureDAO = new FurnitureDAOImpl();
    furnitureDAO.add(furniture);
  }

  /**
   * This method adds a new OfficeSupply object to the "OfficeSupply" table in the database.
   *
   * @param officeSupply the OfficeSupply object to be added to the "OfficeSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addOfficeSupply(OfficeSupply officeSupply) throws SQLException {
    OfficeSupplyDAOImpl officeSupplyDAO = new OfficeSupplyDAOImpl();
    officeSupplyDAO.add(officeSupply);
  }

  /**
   * This method adds a new Signage object to the "Signage" table in the database.
   *
   * @param signage the Signage object to be added to the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addSignage(Signage signage) throws SQLException {
    SignageDAOImpl signageDAO = new SignageDAOImpl();
    signageDAO.add(signage);
  }

  /**
   * This method adds a new MedicalSupply object to the "MedicalSupply" table in the database.
   *
   * @param medicalSupply the MedicalSupply object to be added to the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addMedicalSupply(MedicalSupply medicalSupply) throws SQLException {
    MedicalSupplyDAOImpl medicalSupplyDAO = new MedicalSupplyDAOImpl();
    medicalSupplyDAO.add(medicalSupply);
  }
  /**
   * This method adds a new MedicalSupply object to the "MedicalSupply" table in the database.
   *
   * @param confRoom the MedicalSupply object to be added to the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addConfRoom(ConfRoom confRoom) throws SQLException {
    ConfRoomDAOImpl confRoomDAO = new ConfRoomDAOImpl();
    confRoomDAO.add(confRoom);
  }
  /**
   * This method adds a new ConfReservation object to the "ConfReservations" table in the database.
   *
   * @param confReservation the ConfReservation object to be added to the "MedicalSupply" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addConfReservation(ConfReservation confReservation) throws SQLException {
    ConfReservationDAOImpl confReservationDAO = new ConfReservationDAOImpl();
    confReservationDAO.add(confReservation);
  }

  /**
   * This method adds a new PathMessage object to the "PathMessages" table in the database.
   *
   * @param pm the PathMessage object to be added to the "PathMessages" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void addMedicalSupply(PathMessage pm) throws SQLException {
    PathMessageDAOImpl pmDAO = new PathMessageDAOImpl();
    pmDAO.add(pm);
  }

  // --------------------------------DELETES----------------------------------

  /**
   * This method deletes the given Move object from the database
   *
   * @param move the Move object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteMove(Move move) throws SQLException {
    MoveDAOImpl moveDAO = new MoveDAOImpl();
    moveDAO.delete(move);
  }

  /**
   * This method deletes the given Node object from the database
   *
   * @param node the Node object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteNode(Node node) throws SQLException {
    NodeDAOImpl nodeDAO = new NodeDAOImpl();
    nodeDAO.delete(node);
  }

  /**
   * This method deletes the given Edge object from the database
   *
   * @param edge the Edge object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteEdge(Edge edge) throws SQLException {
    EdgeDAOImpl edgeDAO = new EdgeDAOImpl();
    edgeDAO.delete(edge);
  }

  public static void deleteAlert(Alert alert) throws SQLException {
    AlertDAOImpl alertDAO = new AlertDAOImpl();
    alertDAO.delete(alert);
  }
  /**
   * This method deletes the given ItemsOrdered object from the database
   *
   * @param itemsOrdered the ItemsOrdered object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteItemsOrdered(ItemsOrdered itemsOrdered) throws SQLException {
    ItemsOrderedDAOImpl itemsOrderedDAO = new ItemsOrderedDAOImpl();
    itemsOrderedDAO.delete(itemsOrdered);
  }

  /**
   * This method deletes the given ServiceRequest object from the database
   *
   * @param serviceRequest the ServiceRequest object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteServiceRequest(ServiceRequest serviceRequest) throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    serviceRequestDAO.delete(serviceRequest);
  }

  /**
   * Deletes the ServiceRequest and all associated items from the database.
   *
   * @param serviceRequest The ServiceRequest to delete.
   * @throws SQLException if there is an error executing the SQL query.
   */
  public static void deleteServiceRequestsWithItems(ServiceRequest serviceRequest)
      throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    serviceRequestDAO.deleteWithItems(serviceRequest);
  }

  /**
   * This method deletes the given Employee object from the database
   *
   * @param employee the Employee object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteEmployee(Employee employee) throws SQLException {
    EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    employeeDAO.delete(employee);
  }

  /**
   * This method deletes the given Flower object from the database
   *
   * @param flower the Flower object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteFlower(Flower flower) throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    flowerDAO.delete(flower);
  }

  /**
   * This method deletes the given Meal object from the database
   *
   * @param meal the Meal object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteMeals(Meal meal) throws SQLException {
    MealDAOImpl mealDAO = new MealDAOImpl();
    mealDAO.delete(meal);
  }

  /**
   * This method deletes the given LocationName object from the database
   *
   * @param locationName the LocationName object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteLocationName(LocationName locationName) throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    locationNameDAO.delete(locationName);
  }

  /**
   * This method deletes the given Furniture object from the database
   *
   * @param furniture the Flower object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteFurniture(Furniture furniture) throws SQLException {
    FurnitureDAOImpl furnitureDAO = new FurnitureDAOImpl();
    furnitureDAO.delete(furniture);
  }

  /**
   * Deletes the given Signage object from the "Signage" table in the database
   *
   * @param signage the Signage object to be deleted from the "Signage" table
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteSignage(Signage signage) throws SQLException {
    SignageDAOImpl signageDAO = new SignageDAOImpl();
    signageDAO.delete(signage);
  }

  /**
   * This method deletes the given OfficeSupply object from the database
   *
   * @param officeSupply the OfficeSupply object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteOfficeSupply(OfficeSupply officeSupply) throws SQLException {
    OfficeSupplyDAOImpl officeSupplyDAO = new OfficeSupplyDAOImpl();
    officeSupplyDAO.delete(officeSupply);
  }

  /**
   * This method deletes the given MedicalSupply object from the database
   *
   * @param medicalSupply the MedicalSupply object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteMedicalSupply(MedicalSupply medicalSupply) throws SQLException {
    MedicalSupplyDAOImpl medicalSupplyDAO = new MedicalSupplyDAOImpl();
    medicalSupplyDAO.delete(medicalSupply);
  }
  /**
   * This method deletes the given MedicalSupply object from the database
   *
   * @param confRoom the MedicalSupply object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteConfRoom(ConfRoom confRoom) throws SQLException {
    ConfRoomDAOImpl confRoomDAO = new ConfRoomDAOImpl();
    confRoomDAO.delete(confRoom);
  }
  /**
   * This method deletes the given confReservation object from the database
   *
   * @param confReservation the ConfReservation object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deleteConfReservation(ConfReservation confReservation) throws SQLException {
    ConfReservationDAOImpl confReservationDAO = new ConfReservationDAOImpl();
    confReservationDAO.delete(confReservation);
  }

  /**
   * This method deletes the given PathMessage object from the database
   *
   * @param pm the PathMessage object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static void deletePathMessage(PathMessage pm) throws SQLException {
    PathMessageDAOImpl pmDAO = new PathMessageDAOImpl();
    pmDAO.delete(pm);
  }

  // --------------------------------GET ALLS----------------------------------

  /**
   * The method retrieves all the Move objects from the "Move" table in the database.
   *
   * @return an ArrayList of the Move objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Move> getAllMoves() throws SQLException {
    MoveDAOImpl moveDAO = new MoveDAOImpl();
    return moveDAO.getAll();
  }

  /**
   * The method retrieves all the Node objects from the "Node" table in the database.
   *
   * @return an ArrayList of the Node objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Node> getAllNodes() throws SQLException {
    NodeDAOImpl nodeDAO = new NodeDAOImpl();
    return nodeDAO.getAll();
  }

  /**
   * The method retrieves all the Edge objects from the "Edge" table in the database.
   *
   * @return an ArrayList of the Edge objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Edge> getAllEdges() throws SQLException {
    EdgeDAOImpl edgeDAO = new EdgeDAOImpl();
    return edgeDAO.getAll();
  }

  /**
   * The method retrieves all the ItemsOrdered objects from the "ItemsOrdered" table in the
   * database.
   *
   * @return an ArrayList of the ItemsOrdered objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<ItemsOrdered> getAllItemsOrdered() throws SQLException {
    return (new ItemsOrderedDAOImpl()).getAll();
  }

  /**
   * The method retrieves all the ServiceRequest objects from the "ServiceRequest" table in the
   * database.
   *
   * @return an ArrayList of the ServiceRequest objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<ServiceRequest> getAllServiceRequests() throws SQLException {
    return (new ServiceRequestDAOImpl()).getAll();
  }

  public static ArrayList<String> getAllRequestIDs() throws SQLException {
    return (new ServiceRequestDAOImpl()).getAllIDs();
  }

  public static ArrayList<Alert> getAllAlerts() throws SQLException {
    return (new AlertDAOImpl()).getAll();
  }

  public static ArrayList<Integer> getAllAlertIDs() throws SQLException {
    return (new AlertDAOImpl()).getAllIDs();
  }

  public static void addAlert(Alert alert) throws SQLException {
    AlertDAOImpl alertDAO = new AlertDAOImpl();
    alertDAO.add(alert);
  }

  /**
   * The method retrieves all the Employee objects from the "Employee" table in the database.
   *
   * @return an ArrayList of the Employee objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Employee> getAllEmployees() throws SQLException {

    return (new EmployeeDAOImpl()).getAll();
  }

  public static ArrayList<String> getAllUsernames() throws SQLException {
    return (new EmployeeDAOImpl()).getAllUsernames();
  }

  /**
   * The method retrieves all the Flower objects from the "Flowers" table in the database.
   *
   * @return an ArrayList of the Flower objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Flower> getAllFlowers() throws SQLException {
    return (new FlowerDAOImpl()).getAll();
  }

  /**
   * The method retrieves all the Meal objects from the "Meal" table in the database.
   *
   * @return an ArrayList of the Meal objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Meal> getAllMeals() throws SQLException {
    return (new MealDAOImpl()).getAll();
  }

  /**
   * The method retrieves all the LocationName objects from the "ItemsOrdered" table in the
   * database.
   *
   * @return an ArrayList of the ItemsOrdered objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<LocationName> getAllLocationNames() throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    return locationNameDAO.getAll();
  }

  /**
   * The method retrieves all the Furniture objects from the "Furniture" table in the database.
   *
   * @return an ArrayList of the Furniture objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Furniture> getAllFurniture() throws SQLException {
    FurnitureDAOImpl furnitureDAO = new FurnitureDAOImpl();
    return furnitureDAO.getAll();
  }

  /**
   * The method retrieves all the OfficeSupply objects from the "OfficeSupply" table in the
   * database.
   *
   * @return an ArrayList of the OfficeSupply objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<OfficeSupply> getAllOfficeSupplies() throws SQLException {
    OfficeSupplyDAOImpl officeSupplyDAO = new OfficeSupplyDAOImpl();
    return officeSupplyDAO.getAll();
  }

  /**
   * The method retrieves all the Signage objects from the "Signage" table in the database.
   *
   * @return an ArrayList of the Signage objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<Signage> getAllSignage() throws SQLException {
    SignageDAOImpl SignageDAO = new SignageDAOImpl();
    return SignageDAO.getAll();
  }

  /**
   * The method retrieves all the MedicalSupply objects from the "MedicalSupply" table in the
   * database.
   *
   * @return an ArrayList of the MedicalSupply objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<MedicalSupply> getAllMedicalSupplies() throws SQLException {
    MedicalSupplyDAOImpl medicalSupplyDAO = new MedicalSupplyDAOImpl();
    return medicalSupplyDAO.getAll();
  }
  /**
   * The method retrieves all the MedicalSupply objects from the "MedicalSupply" table in the
   * database.
   *
   * @return an ArrayList of the MedicalSupply objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<ConfRoom> getAllConfRoom() throws SQLException {
    ConfRoomDAOImpl confRoomDAO = new ConfRoomDAOImpl();
    return confRoomDAO.getAll();
  }
  /**
   * The method retrieves all the ConfReservation objects from the "ConfReservation" table in the
   * database.
   *
   * @return an ArrayList of the ConfReservation objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<ConfReservation> getAllConfReservation() throws SQLException {
    ConfReservationDAOImpl confReservationDAO = new ConfReservationDAOImpl();
    return confReservationDAO.getAll();
  }

  /**
   * The method retrieves all the PathMessages objects from the "PathMessage" table in the database.
   *
   * @return an ArrayList of the PathMessage objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<PathMessage> getAllPathMessages() throws SQLException {
    PathMessageDAOImpl pmDAO = new PathMessageDAOImpl();
    return pmDAO.getAll();
  }

  // --------------------------------GETSINGLE----------------------------------

  /**
   * This method retrieves a Flower object with the specified ID from the "Flowers" table in the
   * database.
   *
   * @param id the ID of the Flower object to retrieve from the "Flowers" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Flower getFlower(int id) throws SQLException {
    FlowerDAOImpl flowerDAO = new FlowerDAOImpl();
    return flowerDAO.getFlower(id);
  }

  /**
   * This method retrieves an ItemsOrdered object with the specified ID from the "ItemsOrdered"
   * table in the database.
   *
   * @param requestID the requestID of the ItemsOrdered object to retrieve from the "ItemsOrdered"
   *     table
   * @param itemID the itemID of the ItemsOrdered object to retrieve from the "ItemsOrdered" table
   * @return the ItemsOrdered object that matches the specified ID's, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ItemsOrdered getItemOrdered(int requestID, int itemID) throws SQLException {
    ItemsOrderedDAOImpl itemsOrderedDAO = new ItemsOrderedDAOImpl();
    return itemsOrderedDAO.getItemOrdered(requestID, itemID);
  }

  /**
   * This method retrieves a LocationName object with the specified name from the "LocationName"
   * table in the database.
   *
   * @param name the long name of the LocationName object to retrieve from the "LocationName" table
   * @return the LocationName object with the specified name, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static LocationName getLocationName(String name) throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    return locationNameDAO.getLocationName(name);
  }

  /**
   * This method retrieves a Employee object with the specified username from the "Employee" table
   * in the database.
   *
   * @param username the username of the Employee object to retrieve from the "Employee" table
   * @return the Employee object with the specified username, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Employee getEmployee(String username) throws SQLException {
    EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl();
    return employeeDAO.getEmployee(username);
  }

  /**
   * This method retrieves a Meal object with the specified ID from the "Meal" table in the
   * database.
   *
   * @param id the ID of the Meal object to retrieve from the "Meal" table
   * @return the Meal object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Meal getMeal(int id) throws SQLException {
    MealDAOImpl mealDAO = new MealDAOImpl();
    return mealDAO.getMeal(id);
  }

  /**
   * This method retrieves a Node object with the specified ID from the "Node" table in the
   * database.
   *
   * @param id the ID of the Meal object to retrieve from the "Node" table
   * @return the Node object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Node getNode(int id) throws SQLException {
    NodeDAOImpl nodeDAO = new NodeDAOImpl();
    return nodeDAO.getNode(id);
  }

  /**
   * This method retrieves an ServiceRequest object with the specified ID from the "ServiceRequest"
   * table in the database.
   *
   * @param id the ID of the ServiceRequest object to retrieve from the "ServiceRequest" table
   * @return the ServiceRequest object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ServiceRequest getServiceRequest(int id) throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    return serviceRequestDAO.getServiceRequest(id);
  }

  /**
   * This method retrieves a Furniture object with the specified ID from the "Furniture" table in
   * the database.
   *
   * @param id the ID of the Furniture object to retrieve from the "Furniture" table
   * @return the Furniture object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Furniture getFurniture(int id) throws SQLException {
    return FurnitureDAOImpl.getFurniture(id);
  }

  /**
   * This method retrieves an OfficeSupply object with the specified ID from the "OfficeSupply"
   * table in the database.
   *
   * @param id the ID of the OfficeSupply object to retrieve from the "OfficeSupply" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static OfficeSupply getOfficeSupply(int id) throws SQLException {
    return OfficeSupplyDAOImpl.getOfficeSupply(id);
  }

  /**
   * This method retrieves an MedicalSupply object with the specified ID from the "MedicalSupply"
   * table in the database.
   *
   * @param id the ID of the MedicalSupply object to retrieve from the "MedicalSupply" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static MedicalSupply getMedicalSupply(int id) throws SQLException {
    return MedicalSupplyDAOImpl.getMedicalSupply(id);
  }
  /**
   * This method retrieves an MedicalSupply object with the specified ID from the "MedicalSupply"
   * table in the database.
   *
   * @param roomID the ID of the MedicalSupply object to retrieve from the "MedicalSupply" table
   * @return the ConfRoom object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ConfRoom getConfRoom(int roomID) throws SQLException {
    return ConfRoomDAOImpl.getConfRoom(roomID);
  }
  /**
   * This method retrieves an ConfReservation object with the specified ID from the
   * "ConfReservation" table in the database.
   *
   * @param resID the ID of the ConfReservation object to retrieve from the "ConfReservation" table
   * @return the ConfReservation object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ConfReservation getConfReservation(int resID) throws SQLException {
    return ConfReservationDAOImpl.getConfReservation(resID);
  }

  /**
   * This method retrieves an PathMessage object with the specified ID from the "PathMessages" table
   * in the database.
   *
   * @param sNode, eNode, alg as the ID of the PathMessage object to retrieve from the
   *     "PathMessages" table
   * @return the PathMessage object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<PathMessage> getPathMessage(int sNode, int eNode, String alg)
      throws SQLException {
    return PathMessageDAOImpl.getPathMessage(sNode, eNode, alg);
  }
  // --------------------------------UPLOADS----------------------------------

  /**
   * * Parses a CSV after being given a String path and then returns a list of Strings after it
   * parses
   *
   * @param csvFilePath a String that represents a file path (must use "\\" instead of "\")
   * @throws SQLException
   */
  public static List<String[]> parseCSVAndUploadToPostgreSQL(String csvFilePath)
      throws SQLException {
    List<String[]> csvData = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
      String line;

      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line, ",");
        List<String> row = new ArrayList<>();

        while (st.hasMoreTokens()) {
          row.add(st.nextToken());
        }

        csvData.add(row.toArray(new String[0]));
      }
    } catch (IOException e) {
      System.err.println("Error reading CSV file: " + e.getMessage());
    }

    return csvData;
  }

  /**
   * Updates the staff name for a service request with the given request ID in the database.
   *
   * @param requestID the ID of the service request to update.
   * @param staffName the new staff name to set.
   * @throws SQLException if a database error occurs.
   */
  public static void uploadStaffNameToServiceRequest(int requestID, String staffName)
      throws SQLException {
    ServiceRequestDAOImpl serviceRequestDAO = new ServiceRequestDAOImpl();
    serviceRequestDAO.uploadStaffName(requestID, staffName);
  }

  /**
   * Updates the status for a service request with the given request ID in the database.
   *
   * @param requestID the ID of the service request to update.
   * @param status the new staff name to set.
   * @throws SQLException if a database error occurs.
   */
  public static void uploadStatusToServiceRequest(int requestID, String status)
      throws SQLException {
    ServiceRequestDAOImpl.uploadStatus(requestID, status);
  }

  /*/**
   * Uploads CSV data to a PostgreSQL database table "LocationName" also creates table if one does
   * not exist
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while uploading the data to the database
   *
  public static void uploadLocationNameToPostgreSQL(String csvFilePath) throws SQLException {
    LocationNameDAOImpl locationNameDAO = new LocationNameDAOImpl();
    locationNameDAO.uploadLocationNameToPostgreSQL(csvFilePath);
  }*/

  /**
   * Uploads CSV data to a PostgreSQL database table "Edge"-also creates one if one does not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadEdge(String path) throws SQLException {
    EdgeDAOImpl.uploadEdgeToPostgreSQL(path);
  }

  public static void uploadAlert(String path) throws SQLException {
    AlertDAOImpl.uploadAlertToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Flower"-also creates one if one does not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadFlower(String path) throws SQLException, ParseException {
    FlowerDAOImpl.uploadFlowerToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Furniture"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadFurniture(String path) throws SQLException, ParseException {
    FurnitureDAOImpl.uploadFurnitureToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "ItemsOrdered"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadItemsOrdered(String path) throws SQLException, ParseException {
    ItemsOrderedDAOImpl.uploadItemsOrderedToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "LocationName" also creates table if one does
   * not exist
   *
   * @param path a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadLocationName(String path) throws SQLException, ParseException {
    LocationNameDAOImpl.uploadLocationNameToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Employee"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadEmployee(String path) throws SQLException, ParseException {
    EmployeeDAOImpl.uploadEmployeeToPostgreSQL(path);
  }
  //
  //  /**
  //   * Uploads CSV data to a PostgreSQL database table "EmployeeType"-also creates one if one does
  // not
  //   * exist
  //   *
  //   * @param path a string that represents a file path (/ is illegal so you must use double//)
  //   * @throws SQLException if an error occurs while uploading the data to the database
  //   */
  /*public static void uploadEmployeeType(String path) throws SQLException, ParseException {
    EmployeeDAOImpl.uploadEmployeeTypeToPostgreSQL(path);
  }*/

  /**
   * Uploads CSV data to a PostgreSQL database table "Meal"-also creates one if one does not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadMeal(String path) throws SQLException, ParseException {
    MealDAOImpl.uploadMealToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Move"-also creates one if one does not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadMove(String path) throws SQLException, ParseException {
    MoveDAOImpl.uploadMoveToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Node"-also creates one if one does not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadNode(String path) throws SQLException, ParseException {
    NodeDAOImpl.uploadNodeToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "OfficeSupply"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadOfficeSupply(String path) throws SQLException, ParseException {
    OfficeSupplyDAOImpl.uploadOfficeSupplyToPostgreSQL(path);
  }

  /**
   * Imports data from a CSV file to the "Signage" table in the database
   *
   * @param path a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while importing the data to the database
   */
  public static void uploadSignage(String path) throws SQLException, ParseException {
    SignageDAOImpl.importSignageFromCSV(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "MedicalSupply"-also creates one if one does
   * not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadMedicalSupply(String path) throws SQLException, ParseException {
    MedicalSupplyDAOImpl.uploadMedicalSupplyToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "ServiceRequest"-also creates one if one does
   * not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadServiceRequest(String path) throws SQLException, ParseException {
    ServiceRequestDAOImpl.uploadServiceRequestToPostgreSQL(path);
  }
  /**
   * Uploads CSV data to a PostgreSQL database table "ConfRooms"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadConfRoom(String path) throws SQLException, ParseException {
    ConfRoomDAOImpl.uploadConfRoomToPostgreSQL(path);
  }
  /**
   * Uploads CSV data to a PostgreSQL database table "ConfReservation"-also creates one if one does
   * not exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadConfReservation(String path) throws SQLException, ParseException {
    ConfReservationDAOImpl.uploadConfReservationToPostgreSQL(path);
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "PathMessages"-also creates one if one does not
   * exist
   *
   * @param path a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadPathMessage(String path) throws SQLException, ParseException {
    PathMessageDAOImpl.uploadPMToPostgreSQL(path);
  }

  // --------------------------------EXPORTS----------------------------------

  /**
   * This method exports all the Edge objects from the "Edge" table in the database to a CSV file at
   * the specified file path.
   *
   * @param path the file path of the CSV file to export the Edge objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportEdgeToCSV(String path) throws SQLException, IOException {
    EdgeDAOImpl.exportEdgeToCSV(path);
  }

  /**
   * This method exports all the Flower objects from the "Flowers" table in the database to a CSV
   * file at the specified file path.
   *
   * @param path the file path of the CSV file to export the Flower objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportFlowersToCSV(String path) throws SQLException, IOException {
    FlowerDAOImpl.exportFlowersToCSV(path);
  }

  /**
   * This method exports all the Furniture objects from the "Furniture" table in the database to a
   * CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the Furniture objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportFurnitureToCSV(String path) throws SQLException, IOException {
    FurnitureDAOImpl.exportFurnitureToCSV(path);
  }

  public static void exportAlertToCSV(String path) throws SQLException, IOException {
    AlertDAOImpl.exportAlertToCSV(path);
  }
  /**
   * This method exports all the ItemsOrdered objects from the "ItemsOrdered" table in the database
   * to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the ItemsOrdered objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportItemsOrderedToCSV(String path) throws SQLException, IOException {
    ItemsOrderedDAOImpl.exportItemsOrderedToCSV(path);
  }

  /**
   * This method exports all the LocationName objects from the "LocationName" table in the database
   * to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the LocationName objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportLocationNameToCSV(String path) throws SQLException, IOException {
    LocationNameDAOImpl.exportLocationNameToCSV(path);
  }

  /**
   * This method exports all the Employee objects from the "Employee" table in the database to a CSV
   * file at the specified file path.
   *
   * @param path the file path of the CSV file to export the Employee objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportEmployeeToCSV(String path) throws SQLException, IOException {
    EmployeeDAOImpl.exportEmployeeToCSV(path);
  }

  /*public void addEmployeeType(String username, EmployeeType employeeType) throws SQLException {
    EmployeeDAOImpl.addEmployeeType(username, employeeType);
  }*/

  /**
   * This method exports all the EmployeeTypes from the "EmployeeType" table in the database to a
   * CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the EmployeeTypes to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  /*public static void exportEmployeeTypeToCSV(String path) throws SQLException, IOException {
    EmployeeDAOImpl.exportEmployeeTypeToCSV(path);
  }*/

  /**
   * This method exports all the Employee objects from the "Employee" table and all the
   * EmployeeTypes from the "EmployeeType" table in the database to a CSV file at the specified file
   * path. Will save the EmployeeTypes at path_employeeType.csv
   *
   * @param path the file path of the CSV file to export the Employees to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  /*public static void exportAllEmployeeDataToCSV(String path) throws SQLException, IOException {
    EmployeeDAOImpl.exportEmployeeToCSV(path);
    String typePath = path.replaceAll(".csv", "_employeeType.csv");
    EmployeeDAOImpl.exportEmployeeTypeToCSV(typePath);
  }*/

  /**
   * This method exports all the Meal objects from the "Meal" table in the database to a CSV file at
   * the specified file path.
   *
   * @param path the file path of the CSV file to export the Meal objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportMealToCSV(String path) throws SQLException, IOException {
    MealDAOImpl.exportMealToCSV(path);
  }

  /**
   * This method exports all the Move objects from the "Move" table in the database to a CSV file at
   * the specified file path.
   *
   * @param path the file path of the CSV file to export the Move objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportMoveToCSV(String path) throws SQLException, IOException {
    MoveDAOImpl.exportMoveToCSV(path);
  }

  /**
   * This method exports all the Node objects from the "Node" table in the database to a CSV file at
   * the specified file path.
   *
   * @param path the file path of the CSV file to export the Node objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportNodeToCSV(String path) throws SQLException, IOException {
    NodeDAOImpl.exportNodeToCSV(path);
  }

  /**
   * This method exports all the OfficeSupply objects from the "OfficeSupply" table in the database
   * to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the OfficeSupply objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportOfficeSupplyToCSV(String path) throws SQLException, IOException {
    OfficeSupplyDAOImpl.exportOfficeSupplyToCSV(path);
  }

  /**
   * This method exports all the MedicalSupply objects from the "MedicalSupply" table in the
   * database to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the MedicalSupply objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportMedicalSupplyToCSV(String path) throws SQLException, IOException {
    MedicalSupplyDAOImpl.exportMedicalSupplyToCSV(path);
  }

  /**
   * This method exports all the ServiceRequest objects from the "ServiceRequest" table in the
   * database to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the ServiceRequest objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportServiceRequestToCSV(String path) throws SQLException, IOException {
    ServiceRequestDAOImpl.exportServiceRequestToCSV(path);
  }
  /**
   * This method exports all the ServiceRequest objects from the "ConfRooms" table in the database
   * to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the ServiceRequest objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportConfRoomToCSV(String path) throws SQLException, IOException {
    ConfRoomDAOImpl.exportConfRoomsToCSV(path);
  }
  /**
   * This method exports all the ServiceRequest objects from the "ConfReservations" table in the
   * database to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the ServiceRequest objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportConfReservationToCSV(String path) throws SQLException, IOException {
    ConfReservationDAOImpl.exportConfReservationsToCSV(path);
  }

  /**
   * Exports data from a PostgreSQL database table "Signage" to a CSV file
   *
   * @param path a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while exporting the data from the database
   * @throws IOException if an error occurs while writing the data to the file
   */
  public static void exportSignageToCSV(String path) throws SQLException, IOException {
    SignageDAOImpl.exportSignageToCSV(path);
  }

  //  public static ArrayList<String> getConfRooms(Timestamp date) throws SQLException {
  //    ConfRoomDAOImpl r = new ConfRoomDAOImpl();
  //    return r.getConfRooms(date);
  //  }
  //  public static int getConfRoomTimes() throws SQLException {
  //    ConfRoomDAOImpl r = new ConfRoomDAOImpl();
  //    return r.getConfRoomTimes();
  //  }

  /**
   * This method exports all the PathMessages objects from the "PathMessages" table in the database
   * to a CSV file at the specified file path.
   *
   * @param path the file path of the CSV file to export the PathMessage objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportPathMessageToCSV(String path) throws SQLException, IOException {
    PathMessageDAOImpl.exportPMToCSV(path);
  }

  // --------------------------OTHER----------------------

  /**
   * This method retrieves a list of all the long names of locations from the "LocationName" table
   * in the database.
   *
   * @return an ArrayList of Strings representing the long names of all locations in the
   *     "LocationName" table, ordered alphabetically
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<String> getNamesAlphabetically() throws SQLException {
    return LocationNameDAOImpl.getAllLongNames();
  }

  /**
   * Returns a HashMap of node IDs mapped to a list of LocationName objects for all rooms that
   * existed at the specified timestamp. If a node is not mapped to a LocationName, it will have an
   * empty ArrayList while nodes mapped to multiple LocationNames will have them sorted with the
   * most up-to-date one being first.
   *
   * @param timestamp a Timestamp object representing the time at which to retrieve the data
   * @return a HashMap containing node IDs mapped to a list of LocationName objects
   * @throws SQLException if there is an error accessing the database
   */
  public static HashMap<Integer, ArrayList<LocationName>> getAllLocationNamesMappedByNode(
      Timestamp timestamp) throws SQLException {
    return NodeDAOImpl.getAllLocationNamesMappedByNode(timestamp);
  }

  /*/**
   * * Gets an arraylist of the combination of Nodes and LocationNames based upon the moves. This
   * info is gotten through looking at the most up-to-date information of the node IDs See
   * getAllRoomsCalculatedByLongName(Timestamp) for calculations based upon longNames
   *
   * @param timestamp the timestamp to filter by
   * @return the list of rooms calculated by node ID at the given timestamp
   * @throws SQLException if there is an error executing the SQL query

  public static ArrayList<Room> getAllRoomsCalculatedByNodeID(Timestamp timestamp)
      throws SQLException {
    return NodeDAOImpl.getAllRoomsCalculatedByNodeID(timestamp);
  }*/

  /**
   * Returns a Node object containing all information about the node with the given longName. The
   * information includes the locationName's nodeID, coordinates, building, floor. The function
   * queries the database and joins the "LocationName" and "Move" tables to retrieve the necessary
   * information. It also filters the results by selecting only the information for the node with
   * the given ID and the most recent date prior to the current time. If no information is found for
   * the given ID, null is returned.
   *
   * @param name the longName of the LocationName to retrieve information for
   * @return a Room object containing all information about the node, or null if no information is
   *     found
   * @throws SQLException if there is an error accessing the database
   */
  public static Node getNodeByLocationName(String name, Timestamp timestamp) throws SQLException {
    return LocationNameDAOImpl.getNodeByLocationName(name, timestamp);
  }

  /**
   * * Gets an arraylist of the combination of Nodes and LocationNames based upon the moves. This
   * info is gotten through looking at the most up-to-date information of the longNames
   *
   * @param timestamp the timestamp to filter by
   * @return the list of rooms calculated by long name at the given timestamp
   * @throws SQLException if there is an error executing the SQL query
   */
  public static ArrayList<Room> getAllRooms(Timestamp timestamp) throws SQLException {
    return LocationNameDAOImpl.getAllRooms(timestamp);
  }

  /**
   * conencts to the employee database and checks if the given username and pass are valid returns
   * the Employee if it exists
   *
   * @param username the username to check
   * @param password the password to check
   * @return the Employee if it exists and is correct, null if no
   * @throws SQLException
   */
  public static Employee checkLogin(String username, String password) throws SQLException {
    return EmployeeDAOImpl.checkLogin(username, password);
  }

  /*public void deleteEmployeeType(String username) throws SQLException {
    EmployeeDAOImpl.deleteEmployeeType(username);
  }*/

  /**
   * * Creates a table for storing Edge data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createEdgeTable() throws SQLException {
    EdgeDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Employee data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createEmployeeTable() throws SQLException {
    EmployeeDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Flower data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createFlowerTable() throws SQLException {
    FlowerDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Furniture data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createFurnitureTable() throws SQLException {
    FurnitureDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing ItemsOrdered data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createItemsOrderedTable() throws SQLException {
    ItemsOrderedDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing LocationName data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createLocationNameTable() throws SQLException {
    LocationNameDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Meal data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createMealTable() throws SQLException {
    MealDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing MedicalSupply data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createMedicalSupplyTable() throws SQLException {
    MedicalSupplyDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Move data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createMoveTable() throws SQLException {
    MoveDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Node data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createNodeTable() throws SQLException {
    NodeDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing OfficeSupply data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createOfficeSupplyTable() throws SQLException {
    OfficeSupplyDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing ServiceRequest data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createServiceRequestTable() throws SQLException {
    ServiceRequestDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Alert data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createAlertTable() throws SQLException {
    AlertDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing Signage data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createSignageTable() throws SQLException {
    SignageDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing ConfReservation data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createConfReservationsTable() throws SQLException {
    ConfReservationDAOImpl.createTable();
  }

  /**
   * * Creates a table for storing ConfRooms data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createConfRoomsTable() throws SQLException {
    ConfRoomDAOImpl.createTable();
  }

  /**
   * * Creates all tables in the database unless they already exist then do nothing
   *
   * @throws SQLException connection to the database fails
   */
  public static void tryToCreateAllTables() throws SQLException {
    createEdgeTable();
    createEmployeeTable();
    createFlowerTable();
    createFurnitureTable();
    createItemsOrderedTable();
    createLocationNameTable();
    createMealTable();
    createMedicalSupplyTable();
    createMoveTable();
    createNodeTable();
    createOfficeSupplyTable();
    createServiceRequestTable();
    createAlertTable();
    createSignageTable();
    createConfReservationsTable();
    createConfRoomsTable();
  }
}
