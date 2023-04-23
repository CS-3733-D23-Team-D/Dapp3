package edu.wpi.teamname.database.interfaces;

import edu.wpi.teamname.navigation.*;
import java.sql.SQLException;
import java.util.ArrayList;

public interface NodeDAO extends DAO<Node> {
  void sync(Node node) throws SQLException;

  ArrayList<Node> getAll() throws SQLException;

  void add(Node node) throws SQLException;

  void delete(Node node) throws SQLException;
}
