package edu.wpi.teamname.navigation.AlgoStrategy;

import edu.wpi.teamname.navigation.Graph;
import edu.wpi.teamname.navigation.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class DFSAlgo implements IStrategyAlgo {
  @Override
  public ArrayList<Node> getPathBetween(Graph g, int startNodeId, int targetNodeId) {
    ArrayList<Node> nodes = g.getNodes();
    Node start = g.findNodeByID(startNodeId);
    Node target = g.findNodeByID(targetNodeId);

    int nodeNum = g.getNodes().size();
    Node[] prev = new Node[nodeNum];
    boolean[] visited = new boolean[nodeNum];
    Stack<Node> stack = new Stack<Node>();

    stack.push(start);
    visited[nodes.indexOf(start)] = true;
    prev[nodes.indexOf(start)] = null;

    while (!stack.isEmpty()) {
      Node curr = stack.pop();
      if (curr.equals(target)) break;
      for (Node neighbor : curr.getNeighbors()) {
        if (!visited[nodes.indexOf(neighbor)]) {
          stack.push(neighbor);
          prev[nodes.indexOf(neighbor)] = curr;
          visited[nodes.indexOf(neighbor)] = true;
        }
      }
    }

    ArrayList<Node> finalPath = new ArrayList<Node>();
    for (Node n = target; n != null; n = prev[nodes.indexOf(n)]) {
      finalPath.add(n);
    }

    Collections.reverse(finalPath);
    return finalPath;
  }
}
