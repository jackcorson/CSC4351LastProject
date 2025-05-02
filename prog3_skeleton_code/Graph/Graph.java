package Graph;
import java.util.*;

public class Graph {
  private List<Node> nodes = new ArrayList<>();
  
  public List<Node> nodes() {
    return nodes;
  }
  
  public void addNode(Node n) {
    nodes.add(n);
  }
} 