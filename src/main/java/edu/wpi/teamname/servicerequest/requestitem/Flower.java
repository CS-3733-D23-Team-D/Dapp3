package edu.wpi.teamname.servicerequest.requestitem;

import edu.wpi.teamname.database.DataManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Getter;
import lombok.Setter;

public class Flower extends RequestItem {
  @Getter @Setter private String category;
  @Getter @Setter private String color;

  /**
   * Creates a flower object
   *
   * @param flowerID id of the flower type
   * @param name name of the flower
   * @param price price of the object
   * @param category type of object
   * @param color color of flower
   */
  public Flower(int flowerID, String name, float price, String category, String color) {
    super(flowerID, name, price);
    this.category = category;
    this.color = color;
  }

  /**
   * Creates a flower object
   *
   * @param id the id of the object
   * @throws SQLException thrown when there is an error connecting to the database or an error with
   *     the sql query syntax
   */
  public Flower(int id) throws SQLException {
    super(id);
    Connection connection = DataManager.DbConnection();
    String query = "SELECT * FROM \"Flowers\" WHERE \"flowerID\" = ?;";

    String name = null;
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        super.setItemID(id);
        super.setName(rs.getString("Name"));
        setPrice(rs.getFloat("Price"));
        setCategory(rs.getString("Category"));
        setColor(rs.getString("Color"));
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving flower data: " + e.getMessage());
      System.out.println(id);
    }
  }

  /**
   * Makes a toString of the flower
   *
   * @return String in format [ <itemID>, <name>, <price>, <category>, <color>]
   */
  public String toString() {
    return "["
        + this.getItemID()
        + ", "
        + this.getName()
        + ", "
        + this.getPrice()
        + ", "
        + category
        + ", "
        + color
        + "]";
  }
}
