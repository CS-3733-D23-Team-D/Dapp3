package edu.wpi.teamname;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.teamname.database.DataManager;
import edu.wpi.teamname.navigation.Move;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveDAOTest {
  @BeforeEach
  void setUp() throws SQLException {
    // TODO: Put in docker info
    try {
      DataManager.configConnection("jdbc:postgresql://localhost:5432/postgres", "user", "pass");
      String query =
          "CREATE TABLE IF NOT EXISTS \"Move\" (\"nodeID\" INT, \"longName\" VARCHAR(30), \"date\" timestamp);";
      Connection connection = DataManager.DbConnection();
      PreparedStatement statement = connection.prepareStatement(query);
      statement.executeUpdate();

      query = "TRUNCATE TABLE \"Move\"";
      statement = connection.prepareStatement(query);
      statement.executeUpdate();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  void testAdd() throws SQLException {
    // create a location name to add

    Move move = new Move(1, "Test Long Name", new Timestamp(2023, 3, 2, 0, 0, 0, 0));
    // attempt to add the location name
    // Move move2 = new Move(1, "Test Long Name", new Timestamp(2023, 3, 2, 0, 0, 0, 0));
    // assertTrue(move.equals(move2));
    DataManager.addMoves(move);

    // verify that the location name was added
    ArrayList<Move> list = DataManager.getAllMoves();

    boolean isIn = false;
    for (Move mv : list) {
      if (mv.equals(move)) {
        isIn = true;
      }
    }
    assertTrue(isIn);
  }

  @Test
  void testSync() throws SQLException {
    // create a location name to sync
    Move move = new Move(1, "Test Long Name", new Timestamp(2023, 3, 2, 0, 0, 0, 0));
    DataManager.addMoves(move);

    move.setLongName("New Test Long Name");
    DataManager.syncMove(move);
    // verify that the location name was synced
    ArrayList<Move> list = new ArrayList<Move>();
    try {
      list = DataManager.getAllMoves();
    } catch (SQLException e) {
      fail("SQL Exception thrown while getting all location names");
    }
    boolean isIn = false;
    for (Move mv : list) {
      if (mv.equals(move)) {
        isIn = true;
      }
    }

    assertTrue(isIn);
  }

  @Test
  void testGetAll() {
    // insert some test data
    List<Move> testData = new ArrayList<>();
    testData.add(new Move(1, "Test Name 1", new Timestamp(2023, 1, 1, 0, 0, 0, 0)));
    testData.add(new Move(1, "Test Name 2", new Timestamp(2023, 3, 1, 0, 0, 0, 0)));
    testData.add(new Move(2, "Test Name 1", new Timestamp(2023, 3, 1, 0, 0, 0, 0)));
    try {
      for (Move ln : testData) {
        DataManager.addMoves(ln);
      }
    } catch (SQLException e) {
      fail("SQL Exception thrown while adding test location names");
    }

    // retrieve all location names
    List<Move> list = new ArrayList<>();
    try {
      list = DataManager.getAllMoves();
    } catch (SQLException e) {
      fail("SQL Exception thrown while getting all location names");
    }

    boolean id1 = list.get(0).getNodeID() == (1);
    boolean long1 = list.get(0).getLongName().equals("Test Name 1");
    boolean date1 = list.get(0).getDate().equals(new Timestamp(2023, 1, 1, 0, 0, 0, 0));
    boolean id2 = list.get(1).getNodeID() == 1;
    boolean long2 = list.get(1).getLongName().equals("Test Name 2");
    boolean date2 = list.get(1).getDate().equals(new Timestamp(2023, 3, 1, 0, 0, 0, 0));
    boolean id3 = list.get(2).getNodeID() == 2;
    boolean long3 = list.get(2).getLongName().equals("Test Name 1");
    boolean date3 = list.get(2).getDate().equals(new Timestamp(2023, 3, 1, 0, 0, 0, 0));
    boolean all = id1 && long1 && date1 && long2 && id2 && date2 && long3 && id3 && date3;
    // verify that all location names were retrieved
    assertTrue(all);
  }

  @Test
  void testDelete() throws SQLException {
    // create a location name to delete
    Move move = new Move(1, "Test Long Name", new Timestamp(2023, 3, 2, 0, 0, 0, 0));
    Move move2 = new Move(12, "Test Long Name", new Timestamp(2023, 3, 2, 0, 0, 0, 0));

    // add the location name to the database
    try {
      DataManager.addMoves(move);
      DataManager.addMoves(move2);
    } catch (SQLException e) {
      fail("SQL Exception thrown while adding location name");
    }

    // delete the location name from the database
    try {
      DataManager.deleteMove(move);
    } catch (SQLException e) {
      fail("SQL Exception thrown while deleting location name");
    }

    // verify that the location name was deleted
    ArrayList<Move> list = null;
    try {
      list = DataManager.getAllMoves();
    } catch (SQLException e) {
      fail("SQL Exception thrown while getting all location names");
    }

    boolean isIn = false;
    boolean badIn = false;
    for (Move locationName1 : list) {
      if (locationName1.equals(move)) {
        isIn = true;
      } else if (locationName1.equals(move2)) {
        badIn = true;
      }
    }
    assertFalse(isIn && !badIn);
  }

  @Test
  void testExportCSV() throws SQLException {
    // insert some test data
    List<Move> testData = new ArrayList<>();
    testData.add(new Move(1, "Test Name 1", new Timestamp(2023, 3, 2, 0, 0, 0, 0)));
    testData.add(new Move(2, "Test Name 2", new Timestamp(2023, 3, 2, 0, 0, 0, 0)));
    try {
      for (Move ln : testData) {
        DataManager.addMoves(ln);
      }
    } catch (SQLException e) {
      fail("SQL Exception thrown while adding test location names");
    }

    // export the location names to a CSV file
    String csvFilePath = "test_location_names.csv";
    try {
      DataManager.exportMoveToCSV(csvFilePath);
    } catch (IOException e) {
      fail("IOException thrown while exporting location names to CSV");
    }

    // read the CSV file and verify its contents
    try {
      List<String> lines = Files.readAllLines(Paths.get(csvFilePath));
      assertEquals(lines.get(0), "\"nodeID\",\"longName\",\"date\"");
      assertEquals(lines.get(1), "1,\"Test Name 1\",2023-03-02 00:00:00.0");
      assertEquals(lines.get(2), "2,\"Test Name 2\",2023-03-02 00:00:00.0");
    } catch (IOException e) {
      fail("IOException thrown while reading CSV file");
    }
  }
}
