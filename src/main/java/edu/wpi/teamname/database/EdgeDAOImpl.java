package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.EdgeDAO;
import edu.wpi.teamname.navigation.Edge;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EdgeDAOImpl implements EdgeDAO {
  /**
   * This method updates an existing Edge object in the "Edge" table in the database with the new
   * Edge object.
   *
   * @param edge the new Edge object to be updated in the "Edge" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Edge edge) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Edge\" SET \"startNode\" = ?, \"endNode\" = ?"
              + " WHERE \"startNode\" = ? AND \"endNode\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, edge.getStartNodeID());
      statement.setInt(2, edge.getEndNodeID());
      statement.setInt(3, edge.getOriginalStartNodeID());
      statement.setInt(4, edge.getOriginalEndNodeID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Edge objects from the "Edge" table in the database.
   *
   * @return an ArrayList of the Edge objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Edge> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Edge> list = new ArrayList<Edge>();

    try (connection) {
      String query = "SELECT * FROM \"Edge\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int startNode = rs.getInt("startNode");
        int endNode = rs.getInt("endNode");
        list.add(new Edge(startNode, endNode));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new Edge object to the "Edge" table in the database.
   *
   * @param edge the Edge object to be added to the "Edge" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Edge edge) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query = "INSERT INTO \"Edge\" (\"startNode\", \"endNode\") VALUES (?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, edge.getStartNodeID()); // startNode is a int column
      statement.setInt(2, edge.getEndNodeID()); // endNode is a int column

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * This method deletes the given Edge object from the database
   *
   * @param edge the Edge object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Edge edge) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String del = "Delete ";
    String sel = "Select * ";
    String query = "FROM \"Edge\" WHERE \"startNode\" = ? AND \"endNode\" = ?";
    try (connection) {

      PreparedStatement statement = connection.prepareStatement(del + query);
      statement.setInt(1, edge.getStartNodeID());
      statement.setInt(2, edge.getEndNodeID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(sel + query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Edge information deleted successfully.");
      else System.out.println("Edge information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Edge"-also creates one if one does not exist
   *
   * @param csvFilePath a string that represents a file path (/ is illegal so you must use double//)
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadEdgeToPostgreSQL(String csvFilePath) throws SQLException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    csvData = DataManager.parseCSVAndUploadToPostgreSQL(csvFilePath);
    try (connection) {
      String createTableQuery =
          "CREATE TABLE IF NOT EXISTS \"Edge\" (\"startNode\" INTEGER, \"endNode\" INTEGER);";
      PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery);
      createTableStatement.executeUpdate();

      String query = "INSERT INTO \"Edge\" (\"startNode\", \"endNode\") " + "VALUES (?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Edge\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // startNode is a int column
        statement.setInt(2, Integer.parseInt(row[1])); // endNode is a int column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * This method exports all the Edge objects from the "Edge" table in the database to a CSV file at
   * the specified file path.
   *
   * @param csvFilePath the file path of the CSV file to export the Edge objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportEdgeToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Edge\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("\"startNode\",\"endNode\"\n");
      while (resultSet.next()) {
        int startNode = resultSet.getInt("startNode");
        int endNode = resultSet.getInt("endNode");
        String row = String.format("%d,%d\n", startNode, endNode);
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * * Creates a table for storing Edge data if it doesn't already exist
   *
   * @throws SQLException if connection to the database fails
   */
  public static void createTable() throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "create table if not exists \"Edge\"\n"
              + "(\n"
              + "    \"startNode\" integer not null,\n"
              + "    \"endNode\"   integer not null,\n"
              + "    constraint \"Edge_pk\"\n"
              + "        primary key (\"startNode\", \"endNode\")\n"
              + ");";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}
