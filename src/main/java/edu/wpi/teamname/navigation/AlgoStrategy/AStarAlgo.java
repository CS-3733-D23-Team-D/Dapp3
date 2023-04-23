package edu.wpi.teamname.navigation.AlgoStrategy;

import edu.wpi.teamname.navigation.Graph;
import edu.wpi.teamname.navigation.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class AStarAlgo implements IStrategyAlgo {
  @Override
  public ArrayList<Node> getPathBetween(Graph g, int startNodeId, int targetNodeId) {
    System.out.println("ASTAR T");

    ArrayList<Node> nodes = g.getNodes();
    Node s = nodes.get(Node.idToIndex(startNodeId));
    Node t = nodes.get(Node.idToIndex(targetNodeId));

    for (Node j : nodes) {
      j.setParent(null);
    }

    g.setAllG(s, t);
    Node start = s;
    Node target = t;

    PriorityQueue<Node> closedList = new PriorityQueue<>();
    PriorityQueue<Node> openList = new PriorityQueue<>();

    start.setF(start.getG() + start.calculateHeuristic(target));
    openList.add(start);

    while (!openList.isEmpty()) {
      Node ex = openList.peek();
      if (ex == target) {
        System.out.println(closedList.size());
        return getPath(ex);
      }

      for (Node nei : ex.getNeighbors()) {
        double totalWeight = ex.getG() + nei.findWeight(ex);

        //        System.out.println(closedList.size());

        if (!openList.contains(nei) && !closedList.contains(nei)) {
          nei.setParent(ex);
          nei.setG(totalWeight);
          nei.setF(nei.getG() + nei.calculateHeuristic(target));
          openList.add(nei);
        } else {
          if (totalWeight < nei.getG()) {
            nei.setParent(ex);
            nei.setG(totalWeight);
            nei.setF(nei.getG() + nei.calculateHeuristic(target));

            if (closedList.contains(nei)) {
              closedList.remove(nei);
              openList.add(nei);
            }
          }
        }
      }
      openList.remove(ex);
      closedList.add(ex);
      // System.out.println(closedList.size());
    }
    return null;
  }

  /**
   * This method returns the path from the target node to the start node.
   *
   * @param target The target node to start the path from
   * @return The path from the target node to the start node
   */
  public ArrayList<Node> getPath(Node target) {
    System.out.println("Path");
    Node n = target;

    ArrayList<Node> ids = new ArrayList<>();
    if (n == null) return ids;

    while (n.getParent() != null) {
      ids.add(n);
      n = n.getParent();
    }
    ids.add(n);
    Collections.reverse(ids);

    System.out.println("Path LEn:" + ids.size());
    return ids;
  }
}
