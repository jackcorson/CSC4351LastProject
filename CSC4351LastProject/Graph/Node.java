package Graph;
import java.util.*;

public class Node {
  public List<Node> adj = new ArrayList<>();
  public int color;
  
  public void addEdge(Node n) {
    if (!adj.contains(n)) {
      adj.add(n);
      n.adj.add(this);
    }
  }
} 