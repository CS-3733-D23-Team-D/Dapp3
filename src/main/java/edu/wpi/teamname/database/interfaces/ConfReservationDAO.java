package edu.wpi.teamname.database.interfaces;

import edu.wpi.teamname.servicerequest.ConfReservation;
import edu.wpi.teamname.servicerequest.requestitem.ConfRoom;
import java.sql.SQLException;
import java.util.ArrayList;

public interface ConfReservationDAO extends DAO<ConfReservation> {
  int setResID() throws SQLException;

  ArrayList<ConfReservation> getResForRoom(ConfRoom confrom) throws SQLException;

  void sync(ConfReservation res) throws SQLException;

  ArrayList<ConfReservation> getAll() throws SQLException;

  void add(ConfReservation res) throws SQLException;

  void delete(ConfReservation res) throws SQLException;
}
