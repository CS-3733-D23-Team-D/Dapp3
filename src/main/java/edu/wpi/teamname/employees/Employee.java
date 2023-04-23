package edu.wpi.teamname.employees;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class Employee {
  @Getter @Setter private String username;
  @Getter private String password;
  @Getter @Setter private int employeeID;
  @Getter @Setter private String firstName;
  @Getter @Setter private String lastName;
  @Getter private final String originalUsername;
  @Getter @Setter private EmployeeType type;
  @Getter @Setter private ClearanceLevel level;

  // Add a static ArrayList to store all employees
  private static ArrayList<Employee> allEmployees = new ArrayList<>();
  private static ArrayList<EmployeeType> allTypes = new ArrayList<>();

  // The rest of the fields and methods remain unchanged

  /**
   * Gets all employees as an ArrayList.
   *
   * @return the ArrayList of employees
   */
  public static ArrayList<Employee> getAllEmployees() {
    return allEmployees;
  }

  /**
   * Constructor for login that sets the username and password for every instance of someone logging
   * in
   *
   * @param username
   * @param password
   */
  // Updated constructor
  public Employee(
      String username,
      String password,
      int employeeID,
      String firstName,
      String lastName,
      ClearanceLevel level,
      EmployeeType type,
      Boolean encrypt) {
    this.username = username;
    this.originalUsername = username;
    this.employeeID = employeeID;
    this.firstName = firstName;
    this.lastName = lastName;
    this.level = level;
    this.type = type;
    // encrypt the password using SHA-256
    if (encrypt) {
      this.password = hash(password);
    } else {
      this.password = password;
    }
  }

  // Updated constructor
  /*public Employee(
      String username,
      String password,
      int employeeID,
      String firstName,
      String lastName,
      ArrayList<EmployeeType> employeeType,
      boolean encrypt) {
    this.username = username;
    this.originalUsername = username;
    this.employeeID = employeeID;
    type = employeeType;
    this.firstName = firstName;
    this.lastName = lastName;
    // encrypt the password using SHA-256
    if (encrypt) {
      this.password = hash(password);
    } else {
      this.password = password;
    }
  }*/

  /**
   * Takes a new username and password and sets them as this Employee's username and password. The
   * assigned password is encrypted before being assigned
   *
   * @param newUser the new username
   * @param newPass the new password
   * @throws SQLException
   */
  public void setLogin(String newUser, String newPass) throws SQLException {

    if (checkLegalLogin(newPass)) {
      System.out.println(
          "Password does not meet the requirements: 8 Characters, 1 uppercase, 1 number, 1 special.");
    } else { // meets password req
      String encryptedPass = hash(newPass);
      this.username = newUser;
      this.password = encryptedPass;
    }
  }

  public void setPassword(String password) {
    this.password = hash(password);
  }

  @Override
  public String toString() {
    return "Employee{"
        + "username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + ", employeeID="
        + employeeID
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", type="
        + type
        + ", level="
        + level
        + '}';
  }

  /**
   * Hashes the given password using SHA-256
   *
   * @param plaintext the string to be hashed
   * @return the hashed string
   */
  public static String hash(String plaintext) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }

  // ---------------Login requirements-----------

  /**
   * * checks if the given password doesn't violate the password requirements: A password must be 8
   * characters, 1 uppercase letter and 1 special character
   *
   * @param u the given string
   * @return whether the password meets the requirements
   */
  public boolean checkLegalLogin(String u) {
    if (u.contains("\'") || u.contains(";")) {
      return false;
    } else if (u.length() >= 8 && capital(u) && number(u) && special(u)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * checks if the given string has at least 1 capital letter
   *
   * @param u the given string
   * @return if the string has a capital letter
   */
  private boolean capital(String u) {
    for (int i = 0; i < u.length(); i++) {
      char c = u.charAt(i);
      if (Character.isUpperCase(c)) {
        return true;
      }
    }
    return false;
  }

  /**
   * checks if the given string has at least 1 number
   *
   * @param u the given string
   * @return if the string contains a number
   */
  private boolean number(String u) {
    for (int i = 0; i < u.length(); i++) {
      char c = u.charAt(i);
      if (Character.isDigit(c)) {
        return true;
      }
    }
    return false;
  }

  /**
   * checks if the given string has at least 1 special character
   *
   * @param u the give string
   * @return if the string has a special character
   */
  private boolean special(String u) {
    for (int i = 0; i < u.length(); i++) {
      char c = u.charAt(i);
      if (!Character.isDigit(c) && !Character.isLetter(c)) {
        return true;
      }
    }
    return false;
  }
}
