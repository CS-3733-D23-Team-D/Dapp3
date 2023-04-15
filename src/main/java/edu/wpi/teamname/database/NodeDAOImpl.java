package edu.wpi.teamname.database;

import com.sun.javafx.geom.Point2D;
import edu.wpi.teamname.database.interfaces.NodeDAO;
import edu.wpi.teamname.navigation.Node;
import edu.wpi.teamname.navigation.Room;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeDAOImpl implements NodeDAO {
  /**
   * This method updates an existing Node object in the "Node" table in the database with the new
   * Node object.
   *
   * @param node the new Node object to be updated in the "Node" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Node node) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Node\" SET \"xcoord\" = ?, \"ycoord\" = ?, \"floor\" = ?, \"building\" = ?, \"nodeID\" = ?"
              + " WHERE \"nodeID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, node.getX());
      statement.setInt(2, node.getY());
      statement.setString(3, node.getFloor());
      statement.setString(4, node.getBuilding());
      statement.setInt(5, node.getId());
      statement.setInt(6, node.getOriginalID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Move objects from the "Move" table in the database.
   *
   * @return an ArrayList of the Move objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Node> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Node> list = new ArrayList<Node>();

    try (connection) {
      String query = "SELECT * FROM \"Node\"";
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        int id = rs.getInt("nodeID");
        int xcoord = rs.getInt("xcoord");
        int ycoord = rs.getInt("ycoord");
        String floor = rs.getString("floor");
        String building = rs.getString("building");
        list.add(new Node(id, xcoord, ycoord, floor, building));
      }
    } catch (SQLException e) {
      System.out.println("Get all nodes error.");
    }
    return list;
  }

  /**
   * This method adds a new Node object to the "Node" table in the database.
   *
   * @param node the Node object to be added to the "Node" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Node node) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"Node\" (\"nodeID\", xcoord, ycoord, floor, building) "
            + "VALUES (?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, node.getId());
      statement.setInt(2, node.getX());
      statement.setInt(3, node.getY());
      statement.setString(4, node.getFloor());
      statement.setString(5, node.getBuilding());
      statement.executeUpdate();
      System.out.println("Node information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding Node information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given Node object from the database
   *
   * @param node the Node object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Node node) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String del = "Delete ";
    String sel = "Select * ";
    String query = "from \"Node\" WHERE \"nodeID\" = ?";

    try (PreparedStatement statement =
        connection.prepareStatement(del + "from \"Node\" WHERE \"nodeID\" = ?")) {
      statement.setInt(1, node.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in Node table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(sel + query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Node information deleted successfully.");
      else System.out.println("Node information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
  }
  /**
   * Uploads CSV data to a PostgreSQL database table "Node"
   *
   * @param csvFilePath is a String representing the filepath of the file we want to upload (use
   *     "\\" instead of "\")
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadNodeToPostgreSQL(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      DatabaseMetaData dbm = connection.getMetaData();
      ResultSet tables = dbm.getTables(null, null, "Node", null);
      if (!tables.next()) {
        Statement createStatement = connection.createStatement();
        createStatement.executeUpdate(
            "CREATE TABLE \"Node\" (\n"
                + "  \"nodeID\" INT NOT NULL,\n"
                + "  xcoord INT NOT NULL,\n"
                + "  ycoord INT NOT NULL,\n"
                + "  floor VARCHAR(10) NOT NULL,\n"
                + "  building VARCHAR(255) NOT NULL,\n"
                + "  PRIMARY KEY (\"nodeID\")\n"
                + ");");
        System.out.println("Table \"Node\" created successfully.");
      }

      String query =
          "INSERT INTO \"Node\" (\"nodeID\", xcoord, ycoord, floor, building) "
              + "VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Node\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // nodeID is a int column
        statement.setInt(2, Integer.parseInt(row[1])); // xcoord is an integer column
        statement.setInt(3, Integer.parseInt(row[2])); // ycoord is an integer column
        statement.setString(4, row[3]); // assuming floor is a string column
        statement.setString(5, row[4]); // assuming building is a string column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Exports data from a PostgreSQL database table "Node" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  public static void exportNodeToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Node\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("nodeID,xcoord,ycoord,floor,building\n");
      while (resultSet.next()) {
        int nodeID = resultSet.getInt("nodeID");
        int xcoord = resultSet.getInt("xcoord");
        int ycoord = resultSet.getInt("ycoord");
        String floor = resultSet.getString("floor");
        String building = resultSet.getString("building");

        String row = nodeID + "," + xcoord + "," + ycoord + "," + floor + "," + building + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
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
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Node\" WHERE \"nodeID\" = ?";
    Node node = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();

      int nodeid = rs.getInt("nodeID");
      int xcoord = rs.getInt("xcoord");
      int ycoord = rs.getInt("ycoord");
      String floor = rs.getString("floor");
      String building = rs.getString("building");
      node = (new Node(nodeid, xcoord, ycoord, floor, building));
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return node;
  }
  /**
   * Display nodes located on every floor the parameter String is on within the "Node" table
   *
   * @param floor a String representing the floor the user wants to display nodes on
   * @throws SQLException if an error occurs while displaying the data
   */
  public static ArrayList<Point2D> displayNodesByFloor(String floor) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Node\" WHERE floor = ?";
    ArrayList<Point2D> ret = new ArrayList<Point2D>();
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, floor);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        ret.add(
            new Point2D(
                Integer.parseInt(rs.getString("xcoord")),
                Integer.parseInt(rs.getString("ycoord"))));
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
    return ret;
  }
  /**
   * Returns a Room object containing all information about the node with the given ID. The
   * information includes the node's long name, short name, coordinates, node type, building, floor,
   * and the most recent date when the node's location was updated. The function queries the
   * database and joins the "LocationName" and "Move" tables to retrieve the necessary information.
   * It also filters the results by selecting only the information for the node with the given ID
   * and the most recent date prior to the current time. If no information is found for the given
   * ID, null is returned.
   *
   * @param id the ID of the node to retrieve information for
   * @return a Room object containing all information about the node, or null if no information is
   *     found
   * @throws SQLException if there is an error accessing the database
   */
  public static Room getAllInfoOfNode(int id, Timestamp timestamp) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "SELECT \"nodeID\", \"longName\", \"shortName\", xcoord, ycoord, \"nodeType\", building, floor, j.date "
            + "FROM (SELECT n.\"longName\", \"shortName\", n.\"nodeID\", \"nodeType\", xcoord, ycoord, building, floor, date "
            + "      FROM \"LocationName\", "
            + "           (select \"Move\".\"nodeID\", xcoord, ycoord, floor, building, \"longName\", date "
            + "            FROM \"Node\", \"Move\" "
            + "            where \"Node\".\"nodeID\" = \"Move\".\"nodeID\") n "
            + "      WHERE \"LocationName\".\"longName\" = n.\"longName\") j, "
            + "     (SELECT max(date) AS date "
            + "      FROM (SELECT date "
            + "            FROM (SELECT n.\"nodeID\", date "
            + "                  FROM \"LocationName\", "
            + "                       (select \"Move\".\"nodeID\", xcoord, ycoord, floor, building, \"longName\", date "
            + "                        FROM \"Node\", \"Move\" "
            + "                        where \"Node\".\"nodeID\" = \"Move\".\"nodeID\") n "
            + "                  WHERE \"LocationName\".\"longName\" = n.\"longName\") j "
            + "            WHERE date < ? "
            + "              AND j.\"nodeID\" = ?) l) q "
            + "WHERE j.date = q.date "
            + "AND j.\"nodeID\" = ?;";
    Room room = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setTimestamp(1, timestamp);
      statement.setInt(2, id);
      statement.setInt(3, id);

      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id2 = rs.getInt("nodeID");
        String longName = rs.getString("longName");
        String shortN = rs.getString("shortName");
        int xcoord = rs.getInt("xcoord");
        int ycoord = rs.getInt("ycoord");
        String nodeType = rs.getString("nodeType");
        String building = rs.getString("building");
        String floor = rs.getString("floor");
        Timestamp date = rs.getTimestamp("date");

        room = new Room(id2, longName, date, xcoord, ycoord, floor, building, shortN, nodeType);
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return room;
  }

  /**
   * * Gets an arraylist of the combination of Nodes and LocationNames based upon the moves. This
   * info is gotten through looking at the most up-to-date information of the node IDs See
   * getAllRoomsCalculatedByLongName(Timestamp) for calculations based upon longNames
   *
   * @param timestamp the timestamp to filter by
   * @return the list of rooms calculated by node ID at the given timestamp
   * @throws SQLException if there is an error executing the SQL query
   */
  //  public static ArrayList<Room> getAllRoomsCalculatedByNodeID(Timestamp timestamp)
  //      throws SQLException {
  //    Connection connection = DataManager.DbConnection();
  //    String query =
  //        "SELECT DISTINCT q.\"nodeID\", \"longName\", \"shortName\", \"nodeType\", xcoord,
  // ycoord, building, floor, date\n"
  //            + "FROM (SELECT n.\"longName\", \"shortName\", n.\"nodeID\", \"nodeType\", xcoord,
  // ycoord, building, floor, date\n"
  //            + "      FROM \"LocationName\",\n"
  //            + "           (select \"Move\".\"nodeID\", xcoord, ycoord, floor, building,
  // \"longName\", date\n"
  //            + "            FROM \"Node\", \"Move\"\n"
  //            + "            where \"Node\".\"nodeID\" = \"Move\".\"nodeID\") n\n"
  //            + "      WHERE \"LocationName\".\"longName\" = n.\"longName\") w,\n"
  //            + "     (SELECT \"nodeID\" FROM (SELECT n.\"nodeID\"\n"
  //            + "                            FROM \"LocationName\",\n"
  //            + "                                 (select \"Move\".\"nodeID\", xcoord, ycoord,
  // floor, building, \"longName\", date\n"
  //            + "                                  FROM \"Node\", \"Move\"\n"
  //            + "                                  where \"Node\".\"nodeID\" =
  // \"Move\".\"nodeID\") n\n"
  //            + "                            WHERE \"LocationName\".\"longName\" =
  // n.\"longName\"\n"
  //            + "                              AND date < ?) j\n"
  //            + "                            GROUP BY \"nodeID\") q\n"
  //            + "      where q.\"nodeID\" = w.\"nodeID\";";
  //    ArrayList<Room> rooms = new ArrayList<>();
  //    try (connection) {
  //      PreparedStatement statement = connection.prepareStatement(query);
  //      statement.setTimestamp(1, timestamp);
  //
  //      ResultSet rs = statement.executeQuery();
  //      while (rs.next()) {
  //        int id2 = rs.getInt("nodeID");
  //        String longName = rs.getString("longName");
  //        String shortN = rs.getString("shortName");
  //        int xcoord = rs.getInt("xcoord");
  //        int ycoord = rs.getInt("ycoord");
  //        String nodeType = rs.getString("nodeType");
  //        String building = rs.getString("building");
  //        String floor = rs.getString("floor");
  //        Timestamp date = rs.getTimestamp("date");
  //
  //        rooms.add(new Room(id2, longName, date, xcoord, ycoord, floor, building, shortN,
  // nodeType));
  //      }
  //    } catch (SQLException e) {
  //      System.err.println(e.getMessage());
  //    }
  //    return rooms;
  //  }
}
