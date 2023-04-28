package edu.wpi.teamname.database.interfaces;

import edu.wpi.teamname.navigation.Signage;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public interface SignageDAO {

  ArrayList<String> getSignage(int kiosk, Timestamp date) throws SQLException;

  ArrayList<Integer> getKiosks(Timestamp date) throws SQLException;

  void sync(Signage signage) throws SQLException;

  ArrayList<Signage> getAll() throws SQLException;

  void add(Signage signage) throws SQLException;

  void delete(Signage signage) throws SQLException;
}
