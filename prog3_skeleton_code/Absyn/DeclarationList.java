package Absyn;
import java.util.ArrayList;
public class DeclarationList extends Exp{
    public ArrayList<Decl> list;
  public DeclarationList(int p, ArrayList<Decl> l) {
    pos = p;
    list = l;
  }

}
