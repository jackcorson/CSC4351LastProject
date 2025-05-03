package Temp;

public class Temp  {
  private static java.util.Vector map = new java.util.Vector();
  public int num;
  public String toString() {return "t" + num;}
  public Temp() {
    num = map.size();
    map.addElement(this);
  }
  public int key() {return num;}
  public int hashCode() {return num;}
  public static Temp temp(int num) { return (Temp)map.elementAt(num); }
}
