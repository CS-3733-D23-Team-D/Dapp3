package edu.wpi.teamname.servicerequest.requestitem;

import edu.wpi.teamname.database.DataManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Getter;
import lombok.Setter;

public class MedicalSupply extends RequestItem {
  @Getter @Setter String type;
  @Getter @Setter int accessLvl;

  /**
   * Creates medical supply object
   *
   * @param supplyID
   * @param name
   * @param price
   * @param type
   * @param accessLvl
   */
  public MedicalSupply(int supplyID, String name, float price, String type, int accessLvl) {
    super(supplyID, name, price);
    this.type = type;
    this.accessLvl = accessLvl;
  }

  /**
   * Constructor alternative
   *
   * @param id
   * @throws SQLException
   */
  public MedicalSupply(int id, int aLvl)
      throws SQLException { // Could possibly have it pass in an access lvl of the user and only
    // display items with that access
    super(id);
    Connection connection = DataManager.DbConnection();
    String query =
        "SELECT * FROM \"MedicalSupplies\" WHERE \"supplyID\" = ? AND \"AccessLevel\" <= ?;";

    String name = null;
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      statement.setInt(2, aLvl);
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        super.setItemID(id);
        super.setName(rs.getString("Name"));
        setPrice(rs.getFloat("Price"));
        setType(rs.getString("Type"));
        setAccessLvl(rs.getInt("AccessLevel"));
      }
    } catch (SQLException e) {
      System.out.println("Error retrieving medical supply data: " + e.getMessage());
      System.out.println(id);
    }
  }

  /**
   * A string of the item
   *
   * @return String in format [ <itemID>, <name>, <price>, <type>, <accessLvl>]
   */
  public String toString() {
    return "["
        + this.getItemID()
        + ", "
        + this.getName()
        + ", "
        + this.getPrice()
        + ", "
        + type
        + ", "
        + accessLvl
        + "]";
  }
}
