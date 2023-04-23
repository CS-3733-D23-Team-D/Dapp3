package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.LocationNameDAO;
import edu.wpi.teamname.navigation.LocationName;
import edu.wpi.teamname.navigation.Node;
import edu.wpi.teamname.navigation.Room;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationNameDAOImpl implements LocationNameDAO {
  /**
   * This method updates an existing LocationName object in the "LocationName" table in the database
   * with the new LocationName object.
   *
   * @param locationName the new LocationName object to be updated in the "LocationName" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(LocationName locationName) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"LocationName\" SET \"shortName\" = ?, \"nodeType\" = ?, \"longName\" = ?"
              + " WHERE \"longName\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, locationName.getShortName());
      statement.setString(2, locationName.getNodeType());
      statement.setString(3, locationName.getLongName());
      statement.setString(4, locationName.getOriginalLongName());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the LocationName objects from the "ItemsOrdered" table in the
   * database.
   *
   * @return an ArrayList of the ItemsOrdered objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<LocationName> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<LocationName> list = new ArrayList<LocationName>();
    try {
      String query = "SELECT * FROM \"LocationName\"";
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        String longn = rs.getString("longName");
        String shortn = rs.getString("shortName");
        String ntype = rs.getString("nodeType");
        list.add(new LocationName(longn, shortn, ntype));
      }
    } catch (SQLException e) {
      System.out.println("Get all Location Names error.");
    }
    return list;
  }

  /**
   * This method adds a new LocationName object to the "LocationName" table in the database.
   *
   * @param locationName the LocationName object to be added to the "LocationName" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(LocationName locationName) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"LocationName\" (\"longName\", \"shortName\", \"nodeType\") "
            + "VALUES (?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, locationName.getLongName());
      statement.setString(2, locationName.getShortName());
      statement.setString(3, locationName.getNodeType());
      statement.executeUpdate();
      System.out.println("Location Name information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println(query);
      System.err.println("Error adding Location Name information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given LocationName object from the database
   *
   * @param locationName the LocationName object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(LocationName locationName) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String del = "Delete ";
    String sel = "Select * ";
    String query = "from \"LocationName\" where \"longName\" = ?";

    try (PreparedStatement statement = connection.prepareStatement(del + query)) {
      statement.setString(1, locationName.getLongName());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in Location Name table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(sel + query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("LocationName information deleted successfully.");
      else System.out.println("LocationName information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
  }
  /**
   * Uploads CSV data to a PostgreSQL database table "LocationName" also creates table if one does
   * not exist
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadLocationNameToPostgreSQL(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String createTableQuery =
          "CREATE TABLE IF NOT EXISTS \"LocationName\" ("
              + "\"id\" SERIAL PRIMARY KEY,"
              + "\"longName\" varchar(255),"
              + "\"shortName\" varchar(255),"
              + "\"nodeType\" varchar(255)"
              + ");";
      PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery);
      createTableStatement.execute();

      String query =
          "INSERT INTO \"LocationName\" (\"longName\", \"shortName\", \"nodeType\") "
              + "VALUES (?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"LocationName\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setString(1, row[0]); // longName is a string column
        statement.setString(2, row[1]); // shortName is a string column
        statement.setString(3, row[2]); // nodeType is a string column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
  /**
   * Exports data from a PostgreSQL database table "LocationName" to a CSV file
   *
   * @param csvFilePath a String representing the csv data (must use "//" not "/")
   * @throws SQLException if an error occurs while exporting the data from the database
   * @throws IOException if an error occurs while writing the data to the file
   */
  public static void exportLocationNameToCSV(String csvFilePath) throws SQLException, IOException {
    List<String[]> csvData = new ArrayList<>();
    Connection connection = DataManager.DbConnection();

    try (connection) {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM \"LocationName\"");

      // add column headers
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      String[] headers = new String[columnCount];
      for (int i = 1; i <= columnCount; i++) {
        headers[i - 1] = metaData.getColumnName(i);
      }
      csvData.add(headers);

      // add data rows
      while (resultSet.next()) {
        String[] row = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
          Object value = resultSet.getObject(i);
          row[i - 1] = value == null ? "" : value.toString();
        }
        csvData.add(row);
      }

      // write data to CSV file
      FileWriter fileWriter = new FileWriter(csvFilePath);
      for (String[] row : csvData) {
        for (int i = 0; i < row.length; i++) {
          fileWriter.append("\"");
          fileWriter.append(row[i].replace("\"", "\"\""));
          fileWriter.append("\"");
          if (i < row.length - 1) {
            fileWriter.append(",");
          }
        }
        fileWriter.append("\n");
      }
      fileWriter.flush();
      fileWriter.close();

      System.out.println("Data exported from PostgreSQL database to CSV file");
    } catch (SQLException | IOException e) {
      System.err.println("Error exporting data from PostgreSQL database: " + e.getMessage());
      throw e;
    }
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
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"LocationName\" WHERE \"longName\" = ?";
    LocationName locationName = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, name);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        String longn = rs.getString("longName");
        String shortn = rs.getString("shortName");
        String type = rs.getString("nodeType");
        locationName = (new LocationName(longn, shortn, type));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return locationName;
  }

  /**
   * This method retrieves a list of all the long names of locations from the "LocationName" table
   * in the database.
   *
   * @return an ArrayList of Strings representing the long names of all locations in the
   *     "LocationName" table, ordered alphabetically
   * @throws SQLException if there is a problem accessing the database
   */
  public static ArrayList<String> getAllLongNames() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<String> list = new ArrayList<String>();

    try (connection) {
      String query =
          "SELECT * FROM \"LocationName\" WHERE \"nodeType\" = 'CONF' OR \"nodeType\" = 'DEPT' OR \"nodeType\" = 'INFO' OR \"nodeType\" = 'LABS' ORDER BY \"longName\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        list.add(rs.getString("longName"));
      }
    }
    connection.close();
    return list;
  }

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
    Connection connection = DataManager.DbConnection();
    String query =
        "SELECT \"nodeID\", \"longName\", \"shortName\", xcoord, ycoord, \"nodeType\", building, floor, j.date\n"
            + "FROM (SELECT max(date) AS maxDate\n"
            + "      FROM \"Move\"\n"
            + "      WHERE \"longName\" = ?\n"
            + "        AND date < ?) l,\n"
            + "     (SELECT n.\"longName\", \"shortName\", n.\"nodeID\", \"nodeType\", xcoord, ycoord, building, floor, date\n"
            + "      FROM \"LocationName\",\n"
            + "           (select \"Move\".\"nodeID\", xcoord, ycoord, floor, building, \"longName\", date\n"
            + "            FROM \"Node\", \"Move\"\n"
            + "            where \"Node\".\"nodeID\" = \"Move\".\"nodeID\") n\n"
            + "      WHERE \"LocationName\".\"longName\" = n.\"longName\") j\n"
            + "WHERE j.date = l.maxDate\n"
            + "  AND j.\"longName\" = ?;";
    Node node = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, name);
      statement.setTimestamp(2, timestamp);
      statement.setString(3, name);

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

        node = new Node(id2, xcoord, ycoord, floor, building);
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return node;
  }

  /**
   * * Gets an arraylist of the combination of Nodes and LocationNames based upon the moves. This
   * info is gotten from the perspective of the LocationName
   *
   * @param timestamp the timestamp to filter by
   * @return the list of rooms calculated by long name at the given timestamp
   * @throws SQLException if there is an error executing the SQL query
   */
  public static ArrayList<Room> getAllRooms(Timestamp timestamp) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "SELECT DISTINCT l.\"nodeID\", nd.\"longName\", \"shortName\", \"nodeType\", xcoord, ycoord, building, floor, l.date\n"
            + "FROM (SELECT \"longName\", max(date) date\n"
            + "      FROM \"Move\"\n"
            + "      WHERE date < ?\n"
            + "      GROUP BY \"longName\") nd,\n"
            + "     (SELECT n.\"longName\", \"shortName\", n.\"nodeID\", \"nodeType\", xcoord, ycoord, building, floor, date\n"
            + "      FROM \"LocationName\",\n"
            + "           (select \"Move\".\"nodeID\", xcoord, ycoord, floor, building, \"longName\", date\n"
            + "            FROM \"Node\", \"Move\"\n"
            + "            where \"Node\".\"nodeID\" = \"Move\".\"nodeID\") n\n"
            + "      WHERE n.\"longName\" = \"LocationName\".\"longName\") l\n"
            + "WHERE nd.\"longName\" = l.\"longName\"\n"
            + "  AND nd.date = l.date\n"
            + "ORDER BY date DESC;";
    ArrayList<Room> rooms = new ArrayList<>();
    Room room = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setTimestamp(1, timestamp);

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

        rooms.add(new Room(id2, longName, date, xcoord, ycoord, floor, building, shortN, nodeType));
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    return rooms;
  }
}
