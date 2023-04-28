package edu.wpi.teamname.database.interfaces;

import edu.wpi.teamname.servicerequest.ItemsOrdered;
import java.sql.SQLException;
import java.util.ArrayList;

public interface ItemsOrderedDAO extends DAO<ItemsOrdered> {
  ArrayList<ItemsOrdered> getItemsFromReq(int reqID) throws SQLException;

  void sync(ItemsOrdered itemsOrdered) throws SQLException;

  ArrayList<ItemsOrdered> getAll() throws SQLException;

  void add(ItemsOrdered itemsOrdered) throws SQLException;

  void delete(ItemsOrdered itemsOrdered) throws SQLException;
}
