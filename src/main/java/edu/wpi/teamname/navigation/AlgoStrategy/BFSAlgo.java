package edu.wpi.teamname.navigation.AlgoStrategy;

import edu.wpi.teamname.navigation.Graph;
import edu.wpi.teamname.navigation.Node;
import java.util.*;

public class BFSAlgo implements IStrategyAlgo {
  @Override
  public ArrayList<Node> getPathBetween(Graph g, int startNodeId, int targetNodeId) {
    ArrayList<Node> nodes = g.getNodes();
    Node start = g.findNodeByID(startNodeId);
    Node target = g.findNodeByID(targetNodeId);

    Set<Node> visited = new HashSet<>();
    Queue<ArrayList<Node>> queue = new LinkedList<>();
    queue.offer(new ArrayList<>(Collections.singletonList(start)));

    while (!queue.isEmpty()) {
      ArrayList<Node> path = queue.poll();
      Node n = path.get(path.size() - 1);
      if (n.equals(target)) return path;
      if (!visited.contains(n)) {
        visited.add(n);
        List<Node> neighbors = n.getNeighbors();
        if (neighbors != null) {
          for (Node a : neighbors) {
            ArrayList<Node> path2 = new ArrayList<>(path);
            path2.add(a);
            queue.offer(path2);
          }
        }
      }
    }
    return null;
  }
}
