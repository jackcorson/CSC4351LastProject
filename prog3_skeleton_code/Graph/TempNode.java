package Graph;
import Temp.Temp;

public class TempNode extends Node {
  public Temp temp;
  
  public TempNode(Temp t) {
    temp = t;
  }
  
  public String toString() {
    return temp.toString();
  }
} 