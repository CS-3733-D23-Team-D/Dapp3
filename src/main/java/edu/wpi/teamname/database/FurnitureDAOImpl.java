package edu.wpi.teamname.database;

import edu.wpi.teamname.database.interfaces.FurnitureDAO;
import edu.wpi.teamname.servicerequest.requestitem.Furniture;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FurnitureDAOImpl implements FurnitureDAO {
  /**
   * This method updates an existing Furniture object in the "Furniture" table in the database with
   * the new Furniture object.
   *
   * @param furniture the new Flower object to be updated in the "Furniture" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void sync(Furniture furniture) throws SQLException {
    Connection connection = DataManager.DbConnection();
    try (connection) {
      String query =
          "UPDATE \"Furniture\" SET \"furnitureID\" = ?, \"name\" = ?, \"price\" = ?, \"category\" = ?, \"size\" = ?, \"color\" = ?"
              + " WHERE \"furnitureID\" = ?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, furniture.getItemID());
      statement.setString(2, furniture.getName());
      statement.setFloat(3, furniture.getPrice());
      statement.setString(4, furniture.getCategory());
      statement.setString(5, furniture.getSize());
      statement.setString(6, furniture.getColor());
      statement.setInt(7, furniture.getOriginalID());

      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    connection.close();
  }

  /**
   * The method retrieves all the Furniture objects from the "Furniture" table in the database.
   *
   * @return an ArrayList of the Furniture objects in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public ArrayList<Furniture> getAll() throws SQLException {
    Connection connection = DataManager.DbConnection();
    ArrayList<Furniture> list = new ArrayList<Furniture>();
    try (connection) {
      String query = "SELECT * FROM \"Furniture\"";
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);

      while (rs.next()) {
        int id = rs.getInt("furnitureID");
        String name = rs.getString("name");
        float price = rs.getFloat("price");
        String cate = rs.getString("category");
        String size = rs.getString("size");
        String color = rs.getString("color");
        list.add(new Furniture(id, name, price, cate, size, color));
      }
    } catch (SQLException e) {
      System.out.println("Get all Furniture error.");
    }
    return list;
  }

  /**
   * This method adds a new Furniture object to the "Furniture" table in the database.
   *
   * @param furniture the Furniture object to be added to the "Furniture" table
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void add(Furniture furniture) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query =
        "INSERT INTO \"Furniture\" (\"furnitureID\", \"name\", \"price\", \"category\", \"size\", \"color\") "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, furniture.getItemID());
      statement.setString(2, furniture.getName());
      statement.setFloat(3, furniture.getPrice());
      statement.setString(4, furniture.getCategory());
      statement.setString(5, furniture.getSize());
      statement.setString(6, furniture.getColor());
      statement.executeUpdate();
      System.out.println("Furniture information has been successfully added to the database.");
    } catch (SQLException e) {
      System.err.println("Error adding Furniture information to database: " + e.getMessage());
    }
  }

  /**
   * This method deletes the given Furniture object from the database
   *
   * @param furniture the Flower object that will be deleted in the database
   * @throws SQLException if there is a problem accessing the database
   */
  @Override
  public void delete(Furniture furniture) throws SQLException {
    Connection connection = DataManager.DbConnection();
    String query = "Delete from \"Furniture\" where \"furnitureID\" = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, furniture.getItemID());
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Delete in Furniture table error. " + e);
    }

    try (Statement statement = connection.createStatement()) {
      ResultSet rs2 = statement.executeQuery(query);
      int count = 0;
      while (rs2.next()) count++;
      if (count == 0) System.out.println("Furniture information deleted successfully.");
      else System.out.println("Furniture information did not delete.");
    } catch (SQLException e2) {
      System.out.println("Error checking delete. " + e2);
    }
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
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Furniture\" WHERE \"furnitureID\" = ?";
    Furniture furniture = null;
    try (connection) {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int mealid = rs.getInt("furnitureID");
        String name = rs.getString("name");
        float price = rs.getFloat("price");
        String category = rs.getString("category");
        String size = rs.getString("size");
        String color = rs.getString("color");
        furniture = (new Furniture(mealid, name, price, category, size, color));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return furniture;
  }

  /**
   * Exports data from a PostgreSQL database table "Furniture" to a CSV file
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while exporting the data from the database
   */
  public static void exportFurnitureToCSV(String csvFilePath) throws SQLException, IOException {
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Furniture\"";
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
      writer.write("furnitureID,name,price,category,size,color\n");
      while (resultSet.next()) {
        int id = resultSet.getInt("furnitureID");
        String name = resultSet.getString("name");
        int price = resultSet.getInt("price");
        String category = resultSet.getString("category");
        String size = resultSet.getString("size");
        String color = resultSet.getString("color");

        String row =
            id + "," + name + "," + price + "," + category + "," + size + "," + color + "\n";
        writer.write(row);
      }
      System.out.println("CSV data downloaded from PostgreSQL database");
    } catch (IOException e) {
      System.err.println("Error downloading CSV data from PostgreSQL database: " + e.getMessage());
    }
  }

  /**
   * Uploads CSV data to a PostgreSQL database table "Furniture"
   *
   * @param csvFilePath is a String representing a file path
   * @throws SQLException if an error occurs while uploading the data to the database
   */
  public static void uploadFurnitureToPostgreSQL(String csvFilePath)
      throws SQLException, ParseException {
    List<String[]> csvData;
    Connection connection = DataManager.DbConnection();
    DataManager dataImport = new DataManager();
    csvData = dataImport.parseCSVAndUploadToPostgreSQL(csvFilePath);

    try (connection) {
      String query =
          "INSERT INTO \"Furniture\" (\"furnitureID\", \"name\", \"price\", \"category\", \"size\", \"color\") VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE \"Furniture\";");
      statement.executeUpdate();
      statement = connection.prepareStatement(query);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        statement.setInt(1, Integer.parseInt(row[0])); // furnitureID is an int column
        statement.setString(2, row[1]); // name is a string column
        statement.setFloat(3, Float.parseFloat(row[2])); // price is a float column
        statement.setString(4, row[3]); // category is a string column
        statement.setString(5, row[4]); // size is a string column
        statement.setString(6, row[5]); // color is a string column

        statement.executeUpdate();
      }
      System.out.println("CSV data uploaded to PostgreSQL database");
    } catch (SQLException e) {
      System.err.println("Error uploading CSV data to PostgreSQL database: " + e.getMessage());
    }
  }
}
