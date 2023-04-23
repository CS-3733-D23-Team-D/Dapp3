package edu.wpi.teamname;

import edu.wpi.teamname.navigation.*;
import edu.wpi.teamname.navigation.Node;
import java.util.ArrayList;
import java.util.List;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class AStarTest {

  public static List<Node> nodes = new ArrayList<>();
  public static List<Edge> edges = new ArrayList<>();

  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(AStarTest.class);

    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }

    System.out.println(result.wasSuccessful());
    System.out.println("DONE!");
  }

  // First test: Node 120 to 110
  //  @Test
  //  public void test1() throws Exception {
  //
  //    Graph g = new Graph();
  //    ArrayList<Node> listForPath = new ArrayList<>();
  //
  //    listForPath = g.AStar(g.getNodes().get((120 - 100) / 5), g.getNodes().get((110 - 100) / 5));
  //
  //    String printStr = "";
  //    for (Node n : listForPath) {
  //      printStr += n.getId() + " ";
  //    }
  //
  //    assertEquals(printStr, "120 115 200 1795 100 190 130 110 ");
  //  }
  //
  //  // Second test: Node 155 to 150
  //  @Test
  //  public void test2() throws Exception {
  //    Graph g = new Graph();
  //    ArrayList<Node> listForPath = new ArrayList<>();
  //
  //    listForPath = g.AStar(g.getNodes().get((155 - 100) / 5), g.getNodes().get((150 - 100) / 5));
  //
  //    String printStr = "";
  //    for (Node n : listForPath) {
  //      printStr += n.getId() + " ";
  //    }
  //
  //    assertEquals(printStr, "155 1765 1775 1785 2850 2845 150 ");
  //  }
  //
  //  // Third test: 1500 to 1510
  //  @Test
  //  public void test3() throws Exception {
  //    Graph g = new Graph();
  //    ArrayList<Node> listForPath = new ArrayList<>();
  //
  //    listForPath = g.AStar(g.getNodes().get((1500 - 100) / 5), g.getNodes().get((1510 - 100) /
  // 5));
  //
  //    String printStr = "";
  //    for (Node n : listForPath) {
  //      printStr += n.getId() + " ";
  //    }
  //
  //    assertEquals(printStr, "1500 1505 1510 ");
  //  }
}
