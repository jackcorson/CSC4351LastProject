package Frame;
import Temp.*;
import Tree.Exp;
import Tree.Stm;
import Symbol.Symbol;
import Util.BoolList;

public interface Frame {
  public Temp FP();
  public Temp RV();
  public Tree.Exp externalCall(String func, Tree.ExpList args);
  public Tree.Stm procEntryExit1(Tree.Stm body);
  public String tempMap(Temp temp);
  public Frame newFrame(Symbol name, BoolList formals);
  public Access allocLocal(boolean escape);
  public TempList registers();
  public Label badPtr();
  public Label badSub();
  public int wordSize();
  public Access allocGlobal(Label label, int size);
  public Tree.Exp data(Label label, int size);
  public Tree.Exp string(Label label, String str);
  public Label name();
}
