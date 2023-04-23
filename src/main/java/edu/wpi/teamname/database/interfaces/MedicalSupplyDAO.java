package edu.wpi.teamname.database.interfaces;

import edu.wpi.teamname.servicerequest.requestitem.MedicalSupply;
import java.sql.SQLException;
import java.util.ArrayList;

public interface MedicalSupplyDAO extends DAO<MedicalSupply> {
  void sync(MedicalSupply MedicalSupply) throws SQLException;

  ArrayList<MedicalSupply> getAll() throws SQLException;

  void add(MedicalSupply MedicalSupply) throws SQLException;

  void delete(MedicalSupply MedicalSupply) throws SQLException;
}
