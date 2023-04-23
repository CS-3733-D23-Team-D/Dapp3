package edu.wpi.teamname;

import static org.junit.Assert.assertEquals;

import edu.wpi.teamname.navigation.AlgoStrategy.AStarAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.BFSAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.DFSAlgo;
import edu.wpi.teamname.navigation.AlgoStrategy.DijkstraAlgo;
import edu.wpi.teamname.navigation.Graph;
import edu.wpi.teamname.navigation.Node;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class AlgoTests {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(AlgoTests.class);

    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }

    System.out.println(result.wasSuccessful());
    System.out.println("DONE!");
  }

  // Test1: Test AStar pathfinding
  // 120 to 180
  @Test
  public void test1() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    AStarAlgo A = new AStarAlgo();
    int startId = g.getNodes().get((120 - 100) / 5).getId();
    int targetId = g.getNodes().get((180 - 100) / 5).getId();

    listForPath = A.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      System.out.print(n.getId() + " ");
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "120 115 200 1795 2850 1785 1775 1765 180 ");
  }
  // Test2: Test AStar pathfinding
  // 1500 to 1510
  @Test
  public void test2() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    AStarAlgo A = new AStarAlgo();
    int startId = g.getNodes().get((1500 - 100) / 5).getId();
    int targetId = g.getNodes().get((1510 - 100) / 5).getId();

    listForPath = A.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "1500 1505 1510 ");
  }

  // Test3: tests BFS pathfinding: this will return a path with the smallest number of nodes visited
  // This will not necessarily be the shortest path
  // 120 to 180
  @Test
  public void test3() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    BFSAlgo B = new BFSAlgo();
    int startId = g.getNodes().get((120 - 100) / 5).getId();
    int targetId = g.getNodes().get((180 - 100) / 5).getId();

    listForPath = B.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "120 115 200 1795 2850 1785 1775 1765 180 ");
  }

  // Test4: tests BFS pathfinding: this will return a path with the smallest number of nodes visited
  // This will not necessarily be the shortest path
  // 1500 to 1510
  @Test
  public void test4() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    BFSAlgo B = new BFSAlgo();
    int startId = g.getNodes().get((1500 - 100) / 5).getId();
    int targetId = g.getNodes().get((1510 - 100) / 5).getId();

    listForPath = B.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "1500 1505 1510 ");
  }

  // Test5: tests DFS pathfinding: this will return a path with the smallest number of nodes visited
  // This will not necessarily be the shortest path
  // 120 to 180
  @Test
  public void test5() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    // BFSAlgo B = new BFSAlgo();
    DFSAlgo D = new DFSAlgo();
    int startId = g.getNodes().get((120 - 100) / 5).getId();
    int targetId = g.getNodes().get((180 - 100) / 5).getId();

    listForPath = D.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(
        printStr,
        "120 1700 1705 125 2875 2880 940 1020 1845 1005 1025 1045 1055 2905 2915 2940 2945 2595 2600 2655 2660 2675 2680 2840 2830 675 670 685 695 705 725 730 740 800 805 2780 2785 1680 1515 1510 1505 1450 1380 1610 1375 1445 1440 1435 1430 1415 1410 1470 1360 1475 1665 1670 1675 1520 1580 1525 1635 1530 1620 870 865 860 850 845 840 835 830 825 2805 2800 1745 195 165 1735 160 155 1765 180 ");
  }

  // Test6: tests DFS pathfinding: this will return a path with the smallest number of nodes visited
  // This will not necessarily be the shortest path
  @Test
  public void test6() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    // BFSAlgo B = new BFSAlgo();
    DFSAlgo D = new DFSAlgo();
    int startId = g.getNodes().get((1500 - 100) / 5).getId();
    int targetId = g.getNodes().get((1510 - 100) / 5).getId();

    listForPath = D.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "1500 1505 1510 ");
  }
  // Test 7: Dijkstra algorithm
  // this should return the same path as AStar algorithm
  // 120 to 180
  @Test
  public void test7() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    // BFSAlgo B = new BFSAlgo();
    // DFSAlgo D = new DFSAlgo();
    DijkstraAlgo DJ = new DijkstraAlgo();
    int startId = g.getNodes().get((120 - 100) / 5).getId();
    int targetId = g.getNodes().get((180 - 100) / 5).getId();

    listForPath = DJ.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "120 115 200 1795 2850 1785 1775 1765 180 ");
  }
  // Test 8: Dijkstra algorithm
  // this should return the same path as AStar algorithm
  // 1500 to 1510
  @Test
  public void test8() throws Exception {

    Graph g = new Graph();
    ArrayList<Node> listForPath = new ArrayList<>();
    // AStarAlgo A = new AStarAlgo();
    // BFSAlgo B = new BFSAlgo();
    // DFSAlgo D = new DFSAlgo();
    DijkstraAlgo DJ = new DijkstraAlgo();
    int startId = g.getNodes().get((1500 - 100) / 5).getId();
    int targetId = g.getNodes().get((1510 - 100) / 5).getId();

    listForPath = DJ.getPathBetween(g, startId, targetId);

    String printStr = "";
    for (Node n : listForPath) {
      printStr += n.getId() + " ";
    }

    assertEquals(printStr, "1500 1505 1510 ");
  }
}
