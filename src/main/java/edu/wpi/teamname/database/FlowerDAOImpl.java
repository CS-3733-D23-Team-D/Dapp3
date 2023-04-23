package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.FlowerDAO;
import edu.wpi.teamname.servicerequest.requestitem.Flower;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FlowerDAOImpl implements FlowerDAO {
  /**
   * This method updates an existing Flower object in the "Flower" table in the database with the
   * new Flower object.
   *
   * @param flower the new Flower object to be updated in the "Flower" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Flower flower) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Flowers\" SET \"flowerID\" = ?, \"Name\" = ?, \"Price\" = ?, \"Category\" = ?, \"Color\" = ? WHERE \"flowerID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, flower.getItemID());
      statement.setString(2, flower.getName());
      statement.setFloat(3, flower.getPrice());
      statement.setString(4, flower.getCategory());
      statement.setString(5, flower.getColor());
      statement.setInt(6, flower.getOriginalID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Flower objects from the "Flowers" table in the database.
   *
   * @return an ArrayList of the Flower objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Flower> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Flower> list = new ArrayList<Flower>();

    try (connection) {
      String query = "SELECT * FROM \"Flowers\"";
      PreparedStatement statement = connection.prepareStatement(query);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int flowerID = rs.getInt("flowerID");
        String name = rs.getString("Name");
        float price = rs.getFloat("price");
        String category = rs.getString("Category");
        String color = rs.getString("Color");
        list.add(new Flower(flowerID, name, price, category, color));
      }
    }
    connection.close();
    return list;
  }

  /**
   * This method adds a new Flower object to the "Flowers" table in the database.
   *
   * @param flower the Flower object to be added to the "Flower" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Flower flower) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "INSERT INTO \"Flowers\" (\"flowerID\", \"Name\", \"Price\", \"Category\", \"Color\") "
              + "VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, flower.getItemID());
      statement.setString(2, flower.getName());
      statement.setFloat(3, flower.getPrice());
      statement.setString(4, flower.getCategory());
      statement.setString(5, flower.getColor());

      statement.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * This method deletes the given Flower object from the database
   *
   * @param flower the Flower object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Flower flower) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "DELETE FROM \"Flowers\" WHERE \"flowerID\" = ?";
    try (connection) {

      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, flower.getItemID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Flower information deleted successfully.");
      else System.out.println("Flower information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
    connection.close();
  }

  /**
   * This method retrieves a Flower object with the specified ID from the "Flowers" table in the
   * database.
   *
   * @param id the ID of the Flower object to retrieve from the "Flowers" table
   * @return the Flower object with the specified ID, or null if not found
   * @throws SQLException if there is a problem accessing the database
   */
  public static Flower getFlower(int id) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Flowers\" WHERE \"flowerID\" = ?";
    Flower flower = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int flowerID = rs.getInt("flowerID");
        String name = rs.getString("Name");
        float price = rs.getFloat("price");
        String category = rs.getString("Category");
        String color = rs.getString("Color");
        flower = (new Flower(flowerID, name, price, category, color));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return flower;
  }

  /**
   * This method exports all the Flower objects from the "Flower" table in the database to a CSV
   * file at the specified file path.
   *
   * @param csvFilePath the file path of the CSV file to export the Flower objects to
   * @throws SQLException if there is a problem accessing the database
   * @throws IOException if there is a problem writing the CSV file
   */
  public static void exportFlowersToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Flowers\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("flowerID,Name,Price,Category,Color\n");
      while (resultSet.next()) {
        int flowerID = resultSet.getInt("flowerID");
        String name = resultSet.getString("Name");
        double price = resultSet.getInt("Price");
        String category = resultSet.getString("Category");
        String color = resultSet.getString("Color");

        String row = flowerID + "," + name + "," + price + "," + category + "," + color + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Flowers"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadFlowerToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"Flowers\" (\"flowerID\", \"Name\", \"Price\", \"Category\", \"Color\") VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Flowers\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // flowerID is an int column
        statement.setString(2, row[1]); // name is a string column
        statement.setFloat(3, Float.parseFloat(row[2])); // price is a float column
        statement.setString(4, row[3]); // category is a string column
        statement.setString(5, row[4]); // color is a string column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
}
