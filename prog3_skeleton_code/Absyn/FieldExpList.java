package Absyn;
import Symbol.Symbol;

public class FieldExpList extends Absyn {
  public Symbol name;
  public Exp exp;
  public FieldExpList tail;
  public int pos;

  public FieldExpList(Symbol n, Exp e, FieldExpList t, int p) {
    name = n;
    exp = e;
    tail = t;
    pos = p;
  }
} 