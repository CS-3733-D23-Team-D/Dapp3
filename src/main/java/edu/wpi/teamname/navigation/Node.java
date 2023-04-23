package edu.wpi.teamname.navigation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Node implements Comparable<Node> {
  @Getter @Setter int id;
  @Getter @Setter private int x;
  @Getter @Setter private int y;
  @Getter @Setter private int z;
  @Getter @Setter private String floor;
  @Getter @Setter private String building;

  @Getter @Setter private Node parent = null;
  @Getter @Setter private List<Node> neighbors;
  //  @Getter @Setter private List<Edge> edges;
  @Getter @Setter private List<Edge> edges;
  @Getter private final int originalID;
  // f: sum of g and h;
  @Getter @Setter private double f = Double.MAX_VALUE;
  // g: Distance from start and node n
  @Getter @Setter private double g = Double.MAX_VALUE;
  // heuristic: WILL NEED A FUNCTION TO FIND THIS
  @Getter @Setter private double h;

  /**
   * Constructor for creating a Node object with the given parameters.
   *
   * @param ID the unique identifier of the node
   * @param x the x-coordinate of the node's location
   * @param y the y-coordinate of the node's location
   * @param Floor the floor on which the node is located
   * @param Building the building in which the node is located
   */
  public Node(int ID, int x, int y, String Floor, String Building) {
    this.x = x;
    this.y = y;
    this.floor = Floor;
    this.building = Building;
    this.h = 0;
    this.id = ID;
    this.neighbors = new ArrayList<>();
    //    this.edges = new ArrayList<>();
    this.originalID = ID;
  }

  /**
   * Compares this node to the specified node based on their f value.
   *
   * @param n the node to compare to
   * @return a negative integer, zero, or a positive integer as this node is less than, equal to, or
   *     greater than the specified node based on their f value
   */
  @Override
  public int compareTo(Node n) {
    return Double.compare(this.f, n.f);
  }

  /**
   * * Gets all the nodes in the database and puts them into an array list
   *
   * @return An array list of all the nodes in the database
   * @throws SQLException
   */
  //  public static ArrayList<Node> getAllNodes() throws SQLException {
  //    DatabaseConnection dbc = new DatabaseConnection();
  //    Connection connection = dbc.DbConnection();
  //    ArrayList<Node> list = new ArrayList<Node>();
  //
  //    try (connection) {
  //      String query = "SELECT * FROM \"Node\"";
  //      Statement statement = connection.createStatement();
  //      ResultSet rs = statement.executeQuery(query);
  //
  //      while (rs.next()) {
  //        int id = rs.getInt("nodeID");
  //        int xcoord = rs.getInt("xcoord");
  //        int ycoord = rs.getInt("ycoord");
  //        String floor = rs.getString("floor");
  //        String building = rs.getString("building");
  //        list.add(new Node(id, xcoord, ycoord, floor, building));
  //      }
  //    }
  //    return list;
  //  }

  //  public void addEdge(Edge edge, Node s, Node e) {
  //    if (!this.edges.contains(edge)) {
  //      edges.add(edge);
  //
  //      if (this.id == edge.startNodeID) {
  //        this.neighbors.add(e);
  //      } else {
  //        this.neighbors.add(s);
  //      }
  //    }
  //  }

  /**
   * Calculates the weight of the edge between this node and the given node. The weight is the
   * Euclidean distance between the coordinates of the two nodes.
   *
   * @param target the node to calculate the weight for
   * @return the weight of the edge between this node and the given node
   */
  public double findWeight(Node target) {
    int x1 = this.x;
    int x2 = target.getX();
    int y1 = this.y;
    int y2 = target.getY();
    int z1 = this.z;
    int z2 = target.z;

    double x = Math.pow((x2 - x1), 2);
    double y = Math.pow((y2 - y1), 2);
    double z = Math.pow((z2 - z1), 2);
    return Math.sqrt(x + y + z);
  }

  /**
   * Returns a string representation of this Node object, which includes its id, x and y
   * coordinates, heuristic value, neighbors, floor and building.
   *
   * @return a string representation of this Node object
   */
  public String toString() {
    String nei = "";
    for (Node n : neighbors) {
      nei += " " + Integer.toString(n.getId());
    }
    return "NodeID:"
        + id
        + " Xcord:"
        + x
        + " Ycord:"
        + y
        + " Heu:"
        + h
        + " Neighbors:"
        + nei
        + " Floor:"
        + floor
        + " Building:"
        + building;
  }

  /**
   * Calculates the heuristic distance from this node to the target node using Euclidean distance
   * formula.
   *
   * @param target the target node to calculate the distance to
   * @return the heuristic distance between this node and the target node
   */
  public double calculateHeuristic(Node target) {
    // Heuristic will return distance from target

    int x1 = this.x;
    int x2 = target.getX();
    int y1 = this.y;
    int y2 = target.getY();
    int z1 = this.z;
    int z2 = target.z;

    double x = Math.pow((x2 - x1), 2);
    double y = Math.pow((y2 - y1), 2);
    double z = Math.pow((z2 - z1), 2);
    return Math.sqrt(x + y + z);
  }

  /**
   * This method gets the (most likely correct) index of this node in the Nodes array list or in the
   * database. This works because the first node in the table has index 100 and each next one has an
   * index of 5 more.
   *
   * @return the index of this node in the Nodes array list or in the database.
   */
  public int getIndex() {
    return Node.idToIndex(this.id);
  }

  public static int idToIndex(int id) {
    int index = ((id - 100) / 5);
    return index;
  }

  /**
   * This gets the number that the floor is on for math purposes. Lower level floors are negative
   *
   * @return number the floor is on
   */
  public int getFloorNum() {
    return Integer.valueOf(getFloor().replace('L', '-'));
    //    try{
    //      return Integer.valueOf(getFloor());
    //    } catch(NumberFormatException e){
    //      return -1*Integer.valueOf(getFloor().r)
    //    }
  }
}
