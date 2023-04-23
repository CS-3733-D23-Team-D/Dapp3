package edu.wpi.teamname;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.employees.ClearanceLevel;
import edu.wpi.teamname.employees.Employee;
import edu.wpi.teamname.employees.EmployeeType;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {
  // assertEquals(converter.toKibenian(), "I");

  @BeforeEach
  void setup() {
    DataManager.configConnection(
        "jdbc:postgresql://database.cs.wpi.edu:5432/teamddb?currentSchema=\"teamD\"",
        "teamd",
        "teamd40");
  }

  @Test
  public void testGoodLogin() throws SQLException {
    Employee employee = DataManager.checkLogin("admin", "admin");
    assertNotNull(employee);
  }

  @Test
  public void testFailedPassword() throws SQLException {
    Employee employee = DataManager.checkLogin("admin", "badadmin");
    assertNull(employee);
  }

  @Test
  public void testFailedUsername() throws SQLException {
    Employee employee = DataManager.checkLogin("NOTAUSER___________", "admin");
    assertNull(employee);
  }

  @Test
  public void setLoginUser() throws SQLException {
    Employee employee =
        new Employee(
            "tset", "pass", 1, "first", "kast", ClearanceLevel.STAFF, EmployeeType.DELIVERY, true);
    employee.setLogin("newuser", "newpAss1##_");
    assertEquals("newuser", employee.getUsername());
  }

  @Test
  public void setLoginPass() throws SQLException {
    Employee employee =
        new Employee(
            "tset", "pass", 1, "first", "kast", ClearanceLevel.STAFF, EmployeeType.DELIVERY, true);
    employee.setLogin("newuser", "newpAss1##_");
    assertEquals("qhzsDvv1##_", employee.getPassword());
  }

  /*@Test
  public void testResetPass() throws SQLException {
    LoginController.forgotPasswordPressed("")
    assertEquals(l.resetPass("Wong"), "Wong");
    // in table if password is not "Poulin anymore"
  }*/

  @Test
  public void testEncrypt() {
    Employee l =
        new Employee(
            "user",
            "toencrypt",
            0,
            "tes",
            "test",
            ClearanceLevel.STAFF,
            EmployeeType.DELIVERY,
            true);
    assertEquals("wrhqfubsw", l.getPassword());
  }

  @Test
  public void testLegalLogin() {
    Employee l =
        new Employee(
            "user", "test", 0, "tes", "test", ClearanceLevel.STAFF, EmployeeType.DELIVERY, true);
    assertTrue(!l.checkLegalLogin("small"));
  }

  @Test
  public void testLegalLogin2() {
    Employee l =
        new Employee(
            "user", "test", 0, "tes", "test", ClearanceLevel.STAFF, EmployeeType.DELIVERY, true);
    assertTrue(!l.checkLegalLogin(";;;;badtest"));
  }

  @Test
  public void testLegalLogin3() {
    Employee l =
        new Employee(
            "user", "test", 0, "tes", "test", ClearanceLevel.STAFF, EmployeeType.DELIVERY, true);
    assertTrue(l.checkLegalLogin("GoodPassword2!"));
  }
}
